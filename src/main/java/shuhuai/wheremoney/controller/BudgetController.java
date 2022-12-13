package shuhuai.wheremoney.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import shuhuai.wheremoney.entity.Budget;
import shuhuai.wheremoney.response.Response;
import shuhuai.wheremoney.response.book.GetBookResponse;
import shuhuai.wheremoney.response.budget.GetAllBudgetResponse;
import shuhuai.wheremoney.response.budget.GetBudgetResponse;
import shuhuai.wheremoney.service.BookService;
import shuhuai.wheremoney.service.BudgetService;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/budget")
@Tag(name = "预算管理")
@Slf4j
public class BudgetController extends BaseController {
    @Resource
    private BudgetService budgetService;
    @Resource
    private BookService bookService;

    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "token过期"),
            @ApiResponse(responseCode = "422", description = "参数错误"),
    })
    @RequestMapping(value = "/add-budget", method = RequestMethod.POST)
    @Operation(summary = "新建预算", description = "新建预算")
    public Response<Object> addBudget(@RequestParam Integer bookId,
                                      @RequestParam Integer billCategoryId, @RequestParam BigDecimal limit) {
        BigDecimal totalLimit = (budgetService.getTotalBudgetByBook(bookId) == null ? BigDecimal.ZERO : budgetService.getTotalBudgetByBook(bookId));
        if (totalLimit.compareTo(limit) < 0) {
            return new Response<>(422, "预算超出账本预算额度", null);
        }
        Budget exist = budgetService.selectBudgetByCategoryId(billCategoryId);
        if (exist != null) {
            return new Response<>(422, "该类别已经存在预算", null);
        }
        budgetService.addBudget(bookId, billCategoryId, limit);
        return new Response<>(200, "新建预算成功", null);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "token过期"),
            @ApiResponse(responseCode = "422", description = "参数错误"),
    })
    @RequestMapping(value = "/update-budget", method = RequestMethod.POST)
    @Operation(summary = "更新预算", description = "更新预算")
    public Response<Object> updateBudget(@RequestParam Integer budgetId,
                                         Integer billCategoryId, BigDecimal limit, BigDecimal amount, Integer times) {
        Budget oldBudget = budgetService.getBudget(budgetId);
        if (billCategoryId != null) {
            oldBudget.setBillCategoryId(billCategoryId);
        }
        if (limit != null) {
            oldBudget.setLimit(limit);
        }
        if (amount != null) {
            oldBudget.setUsed(amount);
        }
        if (times != null) {
            oldBudget.setTimes(times);
        }
        budgetService.updateBudget(oldBudget);
        return new Response<>(200, "更新预算成功", new GetBudgetResponse(budgetService.getBudget(budgetId)));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "token过期"),
            @ApiResponse(responseCode = "422", description = "参数错误"),
    })
    @RequestMapping(value = "/get-budgets-by-book", method = RequestMethod.GET)
    @Operation(summary = "查看账本全部预算", description = "查看账本全部预算")
    public Response<Object> getBudgets(@RequestParam Integer bookId) {
        return new Response<>(200, "获取预算成功", new GetAllBudgetResponse(budgetService.getBudgetsByBook(bookId)));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "token过期"),
            @ApiResponse(responseCode = "422", description = "参数错误"),
    })
    @RequestMapping(value = "/set-book-budget", method = RequestMethod.POST)
    @Operation(summary = "修改账本总预算", description = "修改账本总预算")
    public Response<Object> setTotalBudgets(@RequestParam Integer bookId, BigDecimal totalBudget, BigDecimal usedBudget) {
        if (totalBudget == null && usedBudget == null) {
            return new Response<>(200, "无改动", null);
        }
        BigDecimal sum = BigDecimal.ZERO;
        List<Budget> budgets = budgetService.getBudgetsByBook(bookId);
        if (budgets != null) {
            for (Budget budget : budgets) {
                sum = sum.add(budget.getLimit());
            }
        }
        if (totalBudget != null && totalBudget.compareTo(sum) < 0) {
            return new Response<>(422, "总预算超出分类预算额度", null);
        }
        budgetService.updateTotalBudgetByBook(bookId, totalBudget, usedBudget);
        return new Response<>(200, "修改总预算成功", new GetBookResponse(bookService.getBook(bookId)));
    }
}