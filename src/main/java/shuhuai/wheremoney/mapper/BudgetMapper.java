package shuhuai.wheremoney.mapper;

import org.apache.ibatis.annotations.Mapper;
import shuhuai.wheremoney.entity.Budget;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface BudgetMapper {
    void insertBudget(Budget budget);

    List<Budget> selectBudgetsByBook(Integer bookId);

    Budget selectBudgetById(Integer id);

    BigDecimal selectTotalBudgetByBook(Integer id);

    void updateTotalBudgetByBook(Integer id, BigDecimal totalBudget, BigDecimal usedBudget);

    void updateBudgetById(Budget budget);

    Budget selectBudgetByCategoryId(Integer categoryId);

}
