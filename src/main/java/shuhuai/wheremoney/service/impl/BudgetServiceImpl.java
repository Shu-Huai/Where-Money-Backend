package shuhuai.wheremoney.service.impl;

import org.springframework.stereotype.Service;
import shuhuai.wheremoney.entity.Budget;
import shuhuai.wheremoney.mapper.BookMapper;
import shuhuai.wheremoney.mapper.BudgetMapper;
import shuhuai.wheremoney.service.BudgetService;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

@Service
public class BudgetServiceImpl implements BudgetService {
    @Resource
    private BudgetMapper budgetMapper;
    @Resource
    private BookMapper bookMapper;

    @Override
    public void addBudget(Integer bookId, Integer billCategoryId, BigDecimal limit) {
        Budget budget = new Budget(bookId, billCategoryId, limit);
        budgetMapper.insertBudget(budget);
    }

    @Override
    public void updateBudget(Budget budget) {
        budgetMapper.updateBudgetById(budget);
    }

    @Override
    public Budget getBudget(Integer id) {
        return budgetMapper.selectBudgetById(id);
    }

    @Override
    public List<Budget> getBudgetsByBook(Integer bookId) {
        return budgetMapper.selectBudgetsByBook(bookId);
    }

    @Override
    public BigDecimal getTotalBudgetByBook(Integer bookId) {
        return budgetMapper.selectTotalBudgetByBook(bookId);
    }

    @Override
    public void updateTotalBudgetByBook(Integer bookId, BigDecimal totalBudget, BigDecimal usedBudget) {
        budgetMapper.updateTotalBudgetByBook(bookId, totalBudget, usedBudget);
    }

    @Override
    public Budget selectBudgetByCategoryId(Integer billCategoryId) {
        return budgetMapper.selectBudgetByCategoryId(billCategoryId);
    }
}
