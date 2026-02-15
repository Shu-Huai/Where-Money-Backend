package shuhuai.wheremoney.service;

import shuhuai.wheremoney.entity.BillCategory;
import shuhuai.wheremoney.entity.Book;
import shuhuai.wheremoney.type.BillType;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

public interface BookService {
    void addBook(Integer userId, String title, Integer beginDate);

    List<Book> getBookList(Integer userId);

    Book getBook(Integer id);

    BigDecimal getPayMonth(Integer bookId, Timestamp month);

    BigDecimal getIncomeMonth(Integer bookId, Timestamp month);

    BigDecimal getBalanceMonth(Integer bookId, Timestamp month);

    BigDecimal getRefundMonth(Integer bookId, Timestamp month);

    List<BillCategory> getAllBillCategory(Integer bookId, BillType type);

    void addBillCategory(Integer bookId, String billCategoryName, String svg, BillType type);

    void deleteBillCategory(Integer billCategoryId);

    void updateBillCategory(Integer id, String billCategoryName, String svg, BillType type, Integer bookId);

    void deleteBook(Integer id, Integer userId);
}
