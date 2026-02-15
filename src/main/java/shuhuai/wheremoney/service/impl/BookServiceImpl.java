package shuhuai.wheremoney.service.impl;

import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shuhuai.wheremoney.entity.BillCategory;
import shuhuai.wheremoney.entity.Book;
import shuhuai.wheremoney.entity.User;
import shuhuai.wheremoney.mapper.*;
import shuhuai.wheremoney.service.BillCategoryService;
import shuhuai.wheremoney.service.BookService;
import shuhuai.wheremoney.service.BudgetService;
import shuhuai.wheremoney.service.excep.book.TitleOccupiedException;
import shuhuai.wheremoney.service.excep.common.ParamsException;
import shuhuai.wheremoney.service.excep.common.ServerException;
import shuhuai.wheremoney.service.excep.user.UserMissingException;
import shuhuai.wheremoney.type.BillType;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Service
public class BookServiceImpl implements BookService {
    @Value("${default.svg}")
    private String defaultSvg;
    @Resource
    private BookMapper bookMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private BillCategoryService billCategoryService;
    @Resource
    private BudgetService budgetService;
    @Resource
    private PayBillMapper payBillMapper;
    @Resource
    private RefundBillMapper refundBillMapper;
    @Resource
    private IncomeBillMapper incomeBillMapper;
    @Resource
    private TransferBillMapper transferBillMapper;
    @Resource
    private BillCategoryMapper billCategoryMapper;
    @Resource
    private BudgetMapper budgetMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addBook(String userName, String title, Integer beginDate) {
        if (userName == null || title == null || beginDate == null || beginDate < 1 || beginDate > 28) {
            throw new ParamsException("参数错误");
        }
        User user = userMapper.selectUserByUserName(userName);
        Book book = bookMapper.selectBookByUserTitle(user, title);
        if (book != null) {
            throw new TitleOccupiedException("标题已被占用");
        }
        book = new Book(user.getId(), title, beginDate);
        Integer result = bookMapper.insertBookSelective(book);
        if (result != 1) {
            throw new ServerException("服务器错误");
        }
        billCategoryService.addDefaultBillCategory(book.getId());
    }

    @Override
    public List<Book> getBook(String userName) {
        if (userName == null) {
            throw new ParamsException("参数错误");
        }
        User user = userMapper.selectUserByUserName(userName);
        if (user == null) {
            throw new UserMissingException("用户不存在");
        }
        List<Book> books = bookMapper.selectBookByUser(user);
        for (Book book : books) {
            if (book != null && book.getId() != null) {
                budgetService.rebuildBudgetByBook(book.getId());
            }
        }
        return bookMapper.selectBookByUser(user);
    }

    @Override
    public Book getBook(Integer id) {
        if (id == null) {
            throw new ParamsException("参数错误");
        }
        Book book = bookMapper.selectBookById(id);
        if (book == null) {
            return null;
        }
        budgetService.rebuildBudgetByBook(id);
        return bookMapper.selectBookById(id);
    }

    @Override
    public BigDecimal getPayMonth(Integer bookId, Timestamp month) {
        if (bookId == null || month == null) {
            throw new ParamsException("参数错误");
        }
        return bookMapper.selectPayMonthByBookId(bookId, month);
    }

    @Override
    public BigDecimal getIncomeMonth(Integer bookId, Timestamp month) {
        if (bookId == null) {
            throw new ParamsException("参数错误");
        }
        return bookMapper.selectIncomeMonthByBookId(bookId, month);
    }

    @Override
    public BigDecimal getBalanceMonth(Integer bookId, Timestamp month) {
        if (bookId == null) {
            throw new ParamsException("参数错误");
        }
        return bookMapper.selectBalanceMonthByBookId(bookId, month);
    }

    @Override
    public BigDecimal getRefundMonth(Integer bookId, Timestamp month) {
        if (bookId == null) {
            throw new ParamsException("参数错误");
        }
        return bookMapper.selectRefundMonthByBookId(bookId, month);
    }

    @Override
    public List<BillCategory> getAllBillCategory(Integer bookId, BillType type) {
        if (bookId == null) {
            throw new ParamsException("参数错误");
        }
        return billCategoryService.getBillCategoriesByBookType(bookId, type);
    }

    @Override
    public void addBillCategory(Integer bookId, String billCategoryName, String svg, BillType type) {
        if (bookId == null || billCategoryName == null || type == null) {
            throw new ParamsException("参数错误");
        }
        if (svg == null) {
            svg = defaultSvg;
        }
        billCategoryService.addBillCategory(bookId, billCategoryName, svg, type);
    }

    @Override
    public void deleteBillCategory(Integer id) {
        if (id == null) {
            throw new ParamsException("参数错误");
        }
        billCategoryService.deleteBillCategory(id);
    }

    @Override
    public void updateBillCategory(Integer id, String billCategoryName, String svg, BillType type, Integer bookId) {
        if (id == null) {
            throw new ParamsException("参数错误");
        }
        billCategoryService.updateBillCategory(id, billCategoryName, svg, type, bookId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteBook(Integer id) {
        if (id == null) {
            throw new ParamsException("参数错误");
        }
        Book book = bookMapper.selectBookById(id);
        if (book == null) {
            throw new ParamsException("账本不存在");
        }

        refundBillMapper.deleteRefundBillByBookId(id);
        transferBillMapper.deleteTransferBillByBookId(id);
        incomeBillMapper.deleteIncomeBillByBookId(id);
        payBillMapper.deletePayBillByBookId(id);

        budgetMapper.deleteBudgetByBookId(id);
        billCategoryMapper.deleteBillCategoryByBookId(id);
        Integer result = bookMapper.deleteBook(id);
        if (result != 1) {
            throw new ServerException("服务器错误");
        }
    }
}
