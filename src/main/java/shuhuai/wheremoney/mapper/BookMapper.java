package shuhuai.wheremoney.mapper;

import org.apache.ibatis.annotations.Mapper;
import shuhuai.wheremoney.entity.Book;
import shuhuai.wheremoney.entity.User;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Mapper
public interface BookMapper {
    Integer insertBookSelective(Book book);

    Book selectBookById(Integer id);

    List<Book> selectBookByUser(User user);

    Book selectBookByUserTitle(User user, String title);

    BigDecimal selectPayMonthByBookId(Integer bookId, Timestamp month);

    BigDecimal selectIncomeMonthByBookId(Integer bookId, Timestamp month);

    BigDecimal selectBalanceMonthByBookId(Integer bookId, Timestamp month);

    BigDecimal selectRefundMonthByBookId(Integer bookId, Timestamp month);
}