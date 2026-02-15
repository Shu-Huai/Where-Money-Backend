package shuhuai.wheremoney.service;

import shuhuai.wheremoney.entity.Budget;

import java.math.BigDecimal;
import java.util.List;

public interface BudgetService {
    void addBudget(Integer bookId, Integer billCategoryId, BigDecimal limit);

    void updateBudget(Budget budget);

    void addBudgetValidated(Integer bookId, Integer billCategoryId, BigDecimal limit);

    void updateBudgetValidated(Integer budgetId, Integer billCategoryId, BigDecimal limit, BigDecimal amount, Integer times);

    Budget getBudget(Integer id);

    List<Budget> getBudgetsByBook(Integer bookId);

    BigDecimal getTotalBudgetByBook(Integer bookId);

    BigDecimal getAllocatedLimitByBook(Integer bookId);

    void updateTotalBudgetByBook(Integer bookId, BigDecimal totalBudget, BigDecimal usedBudget);

    boolean updateTotalBudgetValidated(Integer bookId, BigDecimal totalBudget, BigDecimal usedBudget);

    Budget selectBudgetByCategoryId(Integer billCategoryId);

    void rebuildBudgetByBook(Integer bookId);

    void changeTotalUsedBudgetRelative(Integer id, BigDecimal relativeValue);

    void changeCategoryUsedBudgetRelative(Integer billCategoryId, BigDecimal relativeValue);

    void changeCategoryTimesRelative(Integer billCategoryId, Integer relativeValue);

    void deleteBudget(Integer id);
}
