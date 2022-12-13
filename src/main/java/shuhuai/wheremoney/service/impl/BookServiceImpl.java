package shuhuai.wheremoney.service.impl;

import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import shuhuai.wheremoney.entity.BillCategory;
import shuhuai.wheremoney.entity.Book;
import shuhuai.wheremoney.entity.User;
import shuhuai.wheremoney.mapper.BookMapper;
import shuhuai.wheremoney.mapper.UserMapper;
import shuhuai.wheremoney.service.BillCategoryService;
import shuhuai.wheremoney.service.BookService;
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
    @Resource
    private BookMapper bookMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private BillCategoryService billCategoryService;

    @Override
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
        return bookMapper.selectBookByUser(user);
    }

    @Override
    public Book getBook(Integer id) {
        if (id == null) {
            throw new ParamsException("参数错误");
        }
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
}