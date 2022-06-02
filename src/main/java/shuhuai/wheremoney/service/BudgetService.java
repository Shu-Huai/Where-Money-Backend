package shuhuai.wheremoney.service;

import shuhuai.wheremoney.entity.Budget;

import java.math.BigDecimal;
import java.util.List;

public interface BudgetService {
    void addBudget(Integer bookId, Integer billCategoryId, BigDecimal limit);

    void updateBudget(Budget budget);

    Budget getBudget(Integer id);

    List<Budget> getBudgetsByBook(Integer bookId);

    BigDecimal getTotalBudgetByBook(Integer bookId);

    void updateTotalBudgetByBook(Integer bookId, BigDecimal totalBudget, BigDecimal usedBudget);

    Budget selectBudgetByCategoryId(Integer billCategoryId);
}
