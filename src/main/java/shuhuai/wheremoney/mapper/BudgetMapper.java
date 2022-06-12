package shuhuai.wheremoney.mapper;

import org.apache.ibatis.annotations.Mapper;
import shuhuai.wheremoney.entity.Budget;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface BudgetMapper {
    void insertBudget(Budget budget);

    Integer updateUsedRelativeByCategoryId(Integer billCategoryId, BigDecimal relativeValue);

    Integer updateTimesRelativeByCategoryId(Integer billCategoryId, Integer relativeValue);

    List<Budget> selectBudgetsByBook(Integer bookId);

    Budget selectBudgetById(Integer id);

    void updateBudgetById(Budget budget);

    Budget selectBudgetByCategoryId(Integer categoryId);
}