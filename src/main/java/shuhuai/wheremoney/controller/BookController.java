package shuhuai.wheremoney.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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

import javax.annotation.Resource;
import java.sql.Timestamp;

@RestController
@RequestMapping("/api/book")
@Api(tags = "账本管理")
@Slf4j
public class BookController extends BaseController {
    @Resource
    private BookService bookService;

    @ApiResponses(value = {
            @ApiResponse(code = 401, message = "token过期"),
            @ApiResponse(code = 422, message = "参数错误"),
            @ApiResponse(code = 400, message = "标题已被占用"),
            @ApiResponse(code = 500, message = "服务器错误")
    })
    @RequestMapping(value = "/add-book", method = RequestMethod.POST)
    @ApiOperation(value = "新建账本")
    public Response<Object> addBook(@RequestParam String title, @RequestParam Integer beginDate) {
        String userName = TokenValidator.getUser().get("userName");
        bookService.addBook(userName, title, beginDate);
        return new Response<>(200, "新建账本成功", null);
    }

    @ApiResponses(value = {
            @ApiResponse(code = 401, message = "token过期"),
            @ApiResponse(code = 422, message = "参数错误"),
            @ApiResponse(code = 404, message = "用户不存在")
    })
    @RequestMapping(value = "/get-book", method = RequestMethod.GET)
    @ApiOperation(value = "获得账本")
    public Response<GetAllBookResponse> getBook() {
        String userName = TokenValidator.getUser().get("userName");
        return new Response<>(200, "获得账本成功", new GetAllBookResponse(bookService.getBook(userName)));
    }

    @ApiResponses(value = {
            @ApiResponse(code = 401, message = "token过期"),
            @ApiResponse(code = 422, message = "参数错误"),
            @ApiResponse(code = 404, message = "用户不存在")
    })
    @RequestMapping(value = "/book", method = RequestMethod.GET)
    @ApiOperation(value = "获得账本")
    public Response<GetBookResponse> getBook(@RequestParam Integer id) {
        return new Response<>(200, "获得账本成功", new GetBookResponse(bookService.getBook(id)));
    }

    @ApiResponses(value = {
            @ApiResponse(code = 401, message = "token过期"),
            @ApiResponse(code = 422, message = "参数错误"),
    })
    @RequestMapping(value = "/pay-month", method = RequestMethod.GET)
    @ApiOperation(value = "获得账本月支出")
    public Response<Object> getPayMonth(@RequestParam Integer bookId, @RequestParam Timestamp month) {
        return new Response<>(200, "获得账本月支出成功", new StatisticAmountResponse(bookService.getPayMonth(bookId, month)));
    }

    @ApiResponses(value = {
            @ApiResponse(code = 401, message = "token过期"),
            @ApiResponse(code = 422, message = "参数错误"),
    })
    @RequestMapping(value = "/income-month", method = RequestMethod.GET)
    @ApiOperation(value = "获得账本月收入")
    public Response<Object> getIncomeMonth(@RequestParam Integer bookId, @RequestParam Timestamp month) {
        return new Response<>(200, "获得账本月收入成功", new StatisticAmountResponse(bookService.getIncomeMonth(bookId, month)));
    }

    @ApiResponses(value = {
            @ApiResponse(code = 401, message = "token过期"),
            @ApiResponse(code = 422, message = "参数错误"),
    })
    @RequestMapping(value = "/balance-month", method = RequestMethod.GET)
    @ApiOperation(value = "获得账本月结余")
    public Response<Object> getBalanceMonth(@RequestParam Integer bookId, @RequestParam Timestamp month) {
        return new Response<>(200, "获得账本月结余成功", new StatisticAmountResponse(bookService.getBalanceMonth(bookId, month)));
    }

    @ApiResponses(value = {
            @ApiResponse(code = 401, message = "token过期"),
            @ApiResponse(code = 422, message = "参数错误"),
    })
    @RequestMapping(value = "/refund-month", method = RequestMethod.GET)
    @ApiOperation(value = "获得账本月退款")
    public Response<Object> getRefundMonth(@RequestParam Integer bookId, @RequestParam Timestamp month) {
        return new Response<>(200, "获得账本月退款成功", new StatisticAmountResponse(bookService.getRefundMonth(bookId, month)));
    }

    @ApiResponses(value = {
            @ApiResponse(code = 401, message = "token过期"),
            @ApiResponse(code = 422, message = "参数错误"),
    })
    @RequestMapping(value = "/all-bill-category", method = RequestMethod.GET)
    @ApiOperation(value = "获得所有账单分类")
    public Response<Object> getAllBillCategory(@RequestParam Integer bookId, @RequestParam BillType type) {
        return new Response<>(200, "获得所有账单分类", new GetAllBillCategoryResponse(bookService.getAllBillCategory(bookId, type)));
    }
}