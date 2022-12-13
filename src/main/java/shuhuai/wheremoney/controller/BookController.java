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
import shuhuai.wheremoney.response.Response;
import shuhuai.wheremoney.response.book.GetAllBillCategoryResponse;
import shuhuai.wheremoney.response.book.GetAllBookResponse;
import shuhuai.wheremoney.response.book.GetBookResponse;
import shuhuai.wheremoney.response.book.StatisticAmountResponse;
import shuhuai.wheremoney.service.BookService;
import shuhuai.wheremoney.type.BillType;
import shuhuai.wheremoney.utils.TokenValidator;

import java.sql.Timestamp;

@RestController
@RequestMapping("/api/book")
@Tag(name = "账本管理")
@Slf4j
public class BookController extends BaseController {
    @Resource
    private BookService bookService;

    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "token过期"),
            @ApiResponse(responseCode = "422", description = "参数错误"),
            @ApiResponse(responseCode = "400", description = "标题已被占用"),
            @ApiResponse(responseCode = "500", description = "服务器错误")
    })
    @RequestMapping(value = "/add-book", method = RequestMethod.POST)
    @Operation(summary = "新建账本")
    public Response<Object> addBook(@RequestParam String title, @RequestParam Integer beginDate) {
        String userName = TokenValidator.getUser().get("userName");
        bookService.addBook(userName, title, beginDate);
        return new Response<>(200, "新建账本成功", null);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "token过期"),
            @ApiResponse(responseCode = "422", description = "参数错误"),
            @ApiResponse(responseCode = "404", description = "用户不存在")
    })
    @RequestMapping(value = "/get-book", method = RequestMethod.GET)
    @Operation(summary = "获得账本")
    public Response<GetAllBookResponse> getBook() {
        String userName = TokenValidator.getUser().get("userName");
        return new Response<>(200, "获得账本成功", new GetAllBookResponse(bookService.getBook(userName)));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "token过期"),
            @ApiResponse(responseCode = "422", description = "参数错误"),
            @ApiResponse(responseCode = "404", description = "用户不存在")
    })
    @RequestMapping(value = "/book", method = RequestMethod.GET)
    @Operation(summary = "获得账本")
    public Response<GetBookResponse> getBook(@RequestParam Integer id) {
        return new Response<>(200, "获得账本成功", new GetBookResponse(bookService.getBook(id)));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "token过期"),
            @ApiResponse(responseCode = "422", description = "参数错误"),
    })
    @RequestMapping(value = "/pay-month", method = RequestMethod.GET)
    @Operation(summary = "获得账本月支出")
    public Response<Object> getPayMonth(@RequestParam Integer bookId, @RequestParam Timestamp month) {
        return new Response<>(200, "获得账本月支出成功", new StatisticAmountResponse(bookService.getPayMonth(bookId, month)));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "token过期"),
            @ApiResponse(responseCode = "422", description = "参数错误"),
    })
    @RequestMapping(value = "/income-month", method = RequestMethod.GET)
    @Operation(summary = "获得账本月收入")
    public Response<Object> getIncomeMonth(@RequestParam Integer bookId, @RequestParam Timestamp month) {
        return new Response<>(200, "获得账本月收入成功", new StatisticAmountResponse(bookService.getIncomeMonth(bookId, month)));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "token过期"),
            @ApiResponse(responseCode = "422", description = "参数错误"),
    })
    @RequestMapping(value = "/balance-month", method = RequestMethod.GET)
    @Operation(summary = "获得账本月结余")
    public Response<Object> getBalanceMonth(@RequestParam Integer bookId, @RequestParam Timestamp month) {
        return new Response<>(200, "获得账本月结余成功", new StatisticAmountResponse(bookService.getBalanceMonth(bookId, month)));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "token过期"),
            @ApiResponse(responseCode = "422", description = "参数错误"),
    })
    @RequestMapping(value = "/refund-month", method = RequestMethod.GET)
    @Operation(summary = "获得账本月退款")
    public Response<Object> getRefundMonth(@RequestParam Integer bookId, @RequestParam Timestamp month) {
        return new Response<>(200, "获得账本月退款成功", new StatisticAmountResponse(bookService.getRefundMonth(bookId, month)));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "token过期"),
            @ApiResponse(responseCode = "422", description = "参数错误"),
    })
    @RequestMapping(value = "/all-bill-category", method = RequestMethod.GET)
    @Operation(summary = "获得所有账单分类")
    public Response<Object> getAllBillCategory(@RequestParam Integer bookId, @RequestParam BillType type) {
        return new Response<>(200, "获得所有账单分类", new GetAllBillCategoryResponse(bookService.getAllBillCategory(bookId, type)));
    }
}