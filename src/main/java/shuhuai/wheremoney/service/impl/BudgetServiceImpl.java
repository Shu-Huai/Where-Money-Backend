package shuhuai.wheremoney.service.impl;

import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shuhuai.wheremoney.entity.Budget;
import shuhuai.wheremoney.entity.PayBill;
import shuhuai.wheremoney.entity.RefundBill;
import shuhuai.wheremoney.mapper.BookMapper;
import shuhuai.wheremoney.mapper.BudgetMapper;
import shuhuai.wheremoney.mapper.PayBillMapper;
import shuhuai.wheremoney.mapper.RefundBillMapper;
import shuhuai.wheremoney.service.BudgetService;
import shuhuai.wheremoney.service.excep.common.ParamsException;
import shuhuai.wheremoney.service.excep.common.ServerException;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class BudgetServiceImpl implements BudgetService {
    @jakarta.annotation.Resource
    private BudgetMapper budgetMapper;
    @Resource
    private BookMapper bookMapper;
    @Resource
    private PayBillMapper payBillMapper;
    @Resource
    private RefundBillMapper refundBillMapper;

    @Override
    public void addBudget(Integer bookId, Integer billCategoryId, BigDecimal limit) {
        if (bookId == null || billCategoryId == null || limit == null) {
            throw new ParamsException("参数错误");
        }
        Budget budget = new Budget(bookId, billCategoryId, limit);
        Integer result = budgetMapper.insertBudget(budget);
        if (result == null || result != 1) {
            throw new ServerException("服务器错误");
        }
    }

    @Override
    public void updateBudget(Budget budget) {
        if (budget == null || budget.getId() == null) {
            throw new ParamsException("参数错误");
        }
        Integer result = budgetMapper.updateBudgetById(budget);
        if (result == null || result != 1) {
            throw new ServerException("服务器错误");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addBudgetValidated(Integer bookId, Integer billCategoryId, BigDecimal limit) {
        if (bookId == null || billCategoryId == null || limit == null || limit.compareTo(BigDecimal.ZERO) < 0) {
            throw new ParamsException("参数错误");
        }
        BigDecimal totalLimit = getTotalBudgetByBook(bookId);
        totalLimit = totalLimit == null ? BigDecimal.ZERO : totalLimit;
        BigDecimal allocatedLimit = getAllocatedLimitByBook(bookId);
        allocatedLimit = allocatedLimit == null ? BigDecimal.ZERO : allocatedLimit;
        if (allocatedLimit.add(limit).compareTo(totalLimit) > 0) {
            throw new ParamsException("预算超出账本预算额度");
        }
        Budget exist = selectBudgetByCategoryId(billCategoryId);
        if (exist != null) {
            throw new ParamsException("该类别已经存在预算");
        }
        addBudget(bookId, billCategoryId, limit);
        rebuildBudgetByBook(bookId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateBudgetValidated(Integer budgetId, Integer billCategoryId, BigDecimal limit, BigDecimal amount, Integer times) {
        if (amount != null || times != null) {
            throw new ParamsException("预算已使用金额和次数由系统维护，不可手动修改");
        }
        Budget oldBudget = getBudget(budgetId);
        if (oldBudget == null) {
            throw new ParamsException("参数错误");
        }
        Integer targetCategoryId = billCategoryId == null ? oldBudget.getBillCategoryId() : billCategoryId;
        if (!Objects.equals(targetCategoryId, oldBudget.getBillCategoryId())) {
            Budget exist = selectBudgetByCategoryId(targetCategoryId);
            if (exist != null && !Objects.equals(exist.getId(), oldBudget.getId())) {
                throw new ParamsException("该类别已经存在预算");
            }
        }
        if (limit != null && limit.compareTo(BigDecimal.ZERO) < 0) {
            throw new ParamsException("参数错误");
        }
        BigDecimal targetLimit = limit == null ? oldBudget.getLimit() : limit;
        targetLimit = targetLimit == null ? BigDecimal.ZERO : targetLimit;
        BigDecimal totalLimit = getTotalBudgetByBook(oldBudget.getBookId());
        totalLimit = totalLimit == null ? BigDecimal.ZERO : totalLimit;
        BigDecimal allocatedLimit = getAllocatedLimitByBook(oldBudget.getBookId());
        allocatedLimit = allocatedLimit == null ? BigDecimal.ZERO : allocatedLimit;
        BigDecimal allocatedWithoutCurrent = allocatedLimit.subtract(oldBudget.getLimit() == null ? BigDecimal.ZERO : oldBudget.getLimit());
        if (allocatedWithoutCurrent.add(targetLimit).compareTo(totalLimit) > 0) {
            throw new ParamsException("预算超出账本预算额度");
        }
        if (billCategoryId != null) {
            oldBudget.setBillCategoryId(billCategoryId);
        }
        if (limit != null) {
            oldBudget.setLimit(limit);
        }
        updateBudget(oldBudget);
        rebuildBudgetByBook(oldBudget.getBookId());
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
    public BigDecimal getAllocatedLimitByBook(Integer bookId) {
        return budgetMapper.selectLimitSumByBook(bookId);
    }

    @Override
    public void updateTotalBudgetByBook(Integer bookId, BigDecimal totalBudget, BigDecimal usedBudget) {
        bookMapper.updateTotalBudgetByBook(bookId, totalBudget, usedBudget);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateTotalBudgetValidated(Integer bookId, BigDecimal totalBudget, BigDecimal usedBudget) {
        if (bookId == null) {
            throw new ParamsException("参数错误");
        }
        if (totalBudget == null && usedBudget == null) {
            return false;
        }
        if (usedBudget != null) {
            throw new ParamsException("已使用预算由系统维护，不可手动修改");
        }
        if (totalBudget.compareTo(BigDecimal.ZERO) < 0) {
            throw new ParamsException("参数错误");
        }
        BigDecimal allocatedLimit = getAllocatedLimitByBook(bookId);
        allocatedLimit = allocatedLimit == null ? BigDecimal.ZERO : allocatedLimit;
        if (totalBudget.compareTo(allocatedLimit) < 0) {
            throw new ParamsException("总预算超出分类预算额度");
        }
        updateTotalBudgetByBook(bookId, totalBudget, null);
        rebuildBudgetByBook(bookId);
        return true;
    }

    @Override
    public Budget selectBudgetByCategoryId(Integer billCategoryId) {
        return budgetMapper.selectBudgetByCategoryId(billCategoryId);
    }

    private Map<Integer, BigDecimal> statisticRefund(List<RefundBill> refundBills) {
        Map<Integer, BigDecimal> result = new HashMap<>();
        if (refundBills == null) {
            return result;
        }
        for (RefundBill refundBill : refundBills) {
            Integer payBillId = refundBill.getPayBillId();
            BigDecimal current = result.getOrDefault(payBillId, BigDecimal.ZERO);
            result.put(payBillId, current.add(refundBill.getAmount()));
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rebuildBudgetByBook(Integer bookId) {
        if (bookId == null) {
            throw new ParamsException("参数错误");
        }
        List<Budget> budgets = budgetMapper.selectBudgetsByBook(bookId);
        Map<Integer, Budget> budgetMap = new HashMap<>();
        if (budgets != null) {
            for (Budget budget : budgets) {
                budget.setUsed(BigDecimal.ZERO);
                budget.setTimes(0);
                budgetMap.put(budget.getBillCategoryId(), budget);
            }
        }
        List<PayBill> payBills = payBillMapper.selectPayBillByBookId(bookId);
        Map<Integer, BigDecimal> refundMap = statisticRefund(refundBillMapper.selectRefundBillByBookId(bookId));
        BigDecimal totalUsed = BigDecimal.ZERO;
        if (payBills != null) {
            for (PayBill payBill : payBills) {
                BigDecimal netPay = payBill.getAmount().subtract(refundMap.getOrDefault(payBill.getId(), BigDecimal.ZERO));
                if (netPay.compareTo(BigDecimal.ZERO) < 0) {
                    netPay = BigDecimal.ZERO;
                }
                totalUsed = totalUsed.add(netPay);
                Budget budget = budgetMap.get(payBill.getBillCategoryId());
                if (budget != null) {
                    budget.setUsed(budget.getUsed().add(netPay));
                    if (netPay.compareTo(BigDecimal.ZERO) > 0) {
                        budget.setTimes(budget.getTimes() + 1);
                    }
                }
            }
        }
        if (budgets != null) {
            for (Budget budget : budgets) {
                Budget updated = new Budget(null, null, null);
                updated.setId(budget.getId());
                updated.setUsed(budget.getUsed());
                updated.setTimes(budget.getTimes());
                Integer result = budgetMapper.updateBudgetById(updated);
                if (result == null || result != 1) {
                    throw new ServerException("服务器错误");
                }
            }
        }
        bookMapper.updateTotalBudgetByBook(bookId, null, totalUsed);
    }

    @Override
    public void changeTotalUsedBudgetRelative(Integer id, BigDecimal relativeValue) {
        if (id == null || relativeValue == null) {
            throw new ParamsException("参数错误");
        }
        Integer result = bookMapper.updateUsedBudgetRelativeById(id, relativeValue);
        if (result == null || result != 1) {
            throw new ServerException("服务器错误");
        }
    }

    @Override
    public void changeCategoryUsedBudgetRelative(Integer billCategoryId, BigDecimal relativeValue) {
        if (billCategoryId == null || relativeValue == null) {
            throw new ParamsException("参数错误");
        }
        Integer result = budgetMapper.updateUsedRelativeByCategoryId(billCategoryId, relativeValue);
        if (result == null || result != 1) {
            throw new ServerException("服务器错误");
        }
    }

    @Override
    public void changeCategoryTimesRelative(Integer billCategoryId, Integer relativeValue) {
        if (billCategoryId == null || relativeValue == null) {
            throw new ParamsException("参数错误");
        }
        Integer result = budgetMapper.updateTimesRelativeByCategoryId(billCategoryId, relativeValue);
        if (result == null || result != 1) {
            throw new ServerException("服务器错误");
        }
    }
}
