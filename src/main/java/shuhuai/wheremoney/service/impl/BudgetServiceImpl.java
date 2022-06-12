package shuhuai.wheremoney.service.impl;

import org.springframework.stereotype.Service;
import shuhuai.wheremoney.entity.Budget;
import shuhuai.wheremoney.mapper.BookMapper;
import shuhuai.wheremoney.mapper.BudgetMapper;
import shuhuai.wheremoney.service.BudgetService;
import shuhuai.wheremoney.service.excep.common.ParamsException;
import shuhuai.wheremoney.service.excep.common.ServerException;

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
        return bookMapper.selectTotalBudgetByBook(bookId);
    }

    @Override
    public void updateTotalBudgetByBook(Integer bookId, BigDecimal totalBudget, BigDecimal usedBudget) {
        bookMapper.updateTotalBudgetByBook(bookId, totalBudget, usedBudget);
    }

    @Override
    public Budget selectBudgetByCategoryId(Integer billCategoryId) {
        return budgetMapper.selectBudgetByCategoryId(billCategoryId);
    }

    @Override
    public void changeTotalUsedBudgetRelative(Integer id, BigDecimal relativeValue) {
        if (id == null || relativeValue == null) {
            throw new ParamsException("参数错误");
        }
        bookMapper.updateUsedBudgetRelativeById(id, relativeValue);
    }
    @Override
    public void changeCategoryUsedBudgetRelative(Integer billCategoryId, BigDecimal relativeValue) {
        if (billCategoryId == null || relativeValue == null) {
            throw new ParamsException("参数错误");
        }
        budgetMapper.updateUsedRelativeByCategoryId(billCategoryId, relativeValue);
    }

    @Override
    public void changeCategoryTimesRelative(Integer billCategoryId, Integer relativeValue) {
        if (billCategoryId == null || relativeValue == null) {
            throw new ParamsException("参数错误");
        }
        budgetMapper.updateTimesRelativeByCategoryId(billCategoryId, relativeValue);
    }
}