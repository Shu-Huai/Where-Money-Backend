package shuhuai.wheremoney.mapper;

import org.apache.ibatis.annotations.Mapper;
import shuhuai.wheremoney.entity.Budget;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface BudgetMapper {
    Integer insertBudget(Budget budget);

    Integer updateUsedRelativeByCategoryId(Integer billCategoryId, BigDecimal relativeValue);

    Integer updateTimesRelativeByCategoryId(Integer billCategoryId, Integer relativeValue);

    List<Budget> selectBudgetsByBook(Integer bookId);

    BigDecimal selectLimitSumByBook(Integer bookId);

    Budget selectBudgetById(Integer id);

    Integer updateBudgetById(Budget budget);

    Budget selectBudgetByCategoryId(Integer categoryId);

    Integer deleteBudgetById(Integer id);

    Integer deleteBudgetByBookId(Integer bookId);
}
