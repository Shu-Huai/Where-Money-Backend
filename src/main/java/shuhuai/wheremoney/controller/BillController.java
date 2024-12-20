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
import org.springframework.web.multipart.MultipartFile;
import shuhuai.wheremoney.entity.*;
import shuhuai.wheremoney.response.Response;
import shuhuai.wheremoney.response.bill.*;
import shuhuai.wheremoney.service.AssetService;
import shuhuai.wheremoney.service.BillCategoryService;
import shuhuai.wheremoney.service.BillService;
import shuhuai.wheremoney.type.BillType;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bill")
@Tag(name = "账单管理")
@Slf4j
public class BillController extends BaseController {
    @Resource
    private BillService billService;
    @Resource
    private AssetService assetService;
    @jakarta.annotation.Resource
    private BillCategoryService billCategoryService;

    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "token过期"),
            @ApiResponse(responseCode = "422", description = "参数错误"),
            @ApiResponse(responseCode = "500", description = "服务器错误")
    })
    @RequestMapping(value = "/bill", method = RequestMethod.POST)
    @Operation(summary = "新建账单")
    public Response<Object> addBill(@RequestParam Integer bookId, Integer inAssetId, Integer outAssetId, Integer payBillId,
                                    Integer billCategoryId, @RequestParam BillType type, @RequestParam BigDecimal amount, BigDecimal transferFee,
                                    @RequestParam Timestamp time, String remark, Boolean refunded, MultipartFile file) {
        billService.addBill(bookId, inAssetId, outAssetId, payBillId, billCategoryId, type, amount, transferFee, time, remark, refunded, file);
        return new Response<>(200, "新建账单成功", null);
    }

    private String[] idToString(BaseBill bill) {
        if (bill instanceof PayBill) {
            String payAsset = null;
            String billCategory = null;
            if (((PayBill) bill).getPayAssetId() != null) {
                payAsset = assetService.getAsset(((PayBill) bill).getPayAssetId()).getAssetName();
            }
            if (((PayBill) bill).getBillCategoryId() != null) {
                billCategory = billCategoryService.getBillCategory(((PayBill) bill).getBillCategoryId()).getBillCategoryName();
            }
            return new String[]{payAsset, billCategory};
        }
        if (bill instanceof IncomeBill) {
            String incomeAsset = null;
            String billCategory = null;
            if (((IncomeBill) bill).getIncomeAssetId() != null) {
                incomeAsset = assetService.getAsset(((IncomeBill) bill).getIncomeAssetId()).getAssetName();
            }
            if (((IncomeBill) bill).getBillCategoryId() != null) {
                billCategory = billCategoryService.getBillCategory(((IncomeBill) bill).getBillCategoryId()).getBillCategoryName();
            }
            return new String[]{incomeAsset, billCategory};
        }
        if (bill instanceof TransferBill) {
            String inAsset = null;
            String outAsset = null;
            if (((TransferBill) bill).getInAssetId() != null) {
                inAsset = assetService.getAsset(((TransferBill) bill).getInAssetId()).getAssetName();
            }
            if (((TransferBill) bill).getOutAssetId() != null) {
                outAsset = assetService.getAsset(((TransferBill) bill).getOutAssetId()).getAssetName();
            }
            return new String[]{inAsset, outAsset};
        }
        if (bill instanceof RefundBill) {
            String refundAsset = null;
            if (((RefundBill) bill).getRefundAssetId() != null) {
                refundAsset = assetService.getAsset(((RefundBill) bill).getRefundAssetId()).getAssetName();
            }
            return new String[]{refundAsset};
        }
        return null;
    }

    private BaseGetBillResponse entityToResponse(BaseBill bill) {
        String[] strings = idToString(bill);
        if (bill instanceof PayBill) {
            return new GetPayBillResponse(bill, strings[0], strings[1], ((PayBill) bill).getRefunded());
        }
        if (bill instanceof IncomeBill) {
            return new GetIncomeBillResponse(bill, strings[0], strings[1]);
        }
        if (bill instanceof TransferBill) {
            return new GetTransferBillResponse(bill, strings[0], strings[1]);
        }
        if (bill instanceof RefundBill) {
            return new GetRefundBillResponse(bill, strings[0]);
        }
        return null;
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "token过期"),
            @ApiResponse(responseCode = "422", description = "参数错误"),
    })
    @RequestMapping(value = "/bill", method = RequestMethod.GET)
    @Operation(summary = "获得账单")
    public Response<BaseGetBillResponse> getBill(@RequestParam Integer id, @RequestParam BillType type) {
        BaseBill bill = billService.getBill(id, type);
        return new Response<>(200, "获得账单成功", entityToResponse(bill));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "token过期"),
            @ApiResponse(responseCode = "422", description = "参数错误"),
    })
    @RequestMapping(value = "/all", method = RequestMethod.GET)
    @Operation(summary = "获得指定账本的所有账单")
    public Response<GetAllBillResponse> getBillByBook(@RequestParam Integer bookId) {
        List<BaseBill> billList = billService.getBillByBook(bookId);
        List<BaseGetBillResponse> billResponseList = new ArrayList<>();
        for (BaseBill bill : billList) {
            billResponseList.add(entityToResponse(bill));
        }
        return new Response<>(200, "获得指定账本的所有账单成功", new GetAllBillResponse(billResponseList));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "token过期"),
            @ApiResponse(responseCode = "422", description = "参数错误"),
    })
    @RequestMapping(value = "/all/time", method = RequestMethod.GET)
    @Operation(summary = "获得指定账本的所有账单时间")
    public Response<GetAllBillResponse> getBillByBookTIme(@RequestParam Integer bookId, @RequestParam Timestamp startTime, @RequestParam Timestamp endTime) {
        List<BaseBill> billList = billService.getBillByBookTime(bookId, startTime, endTime);
        List<BaseGetBillResponse> billResponseList = new ArrayList<>();
        for (BaseBill bill : billList) {
            billResponseList.add(entityToResponse(bill));
        }
        return new Response<>(200, "获得指定账本的所有账单时间成功", new GetAllBillResponse(billResponseList));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "token过期"),
            @ApiResponse(responseCode = "422", description = "参数错误"),
    })
    @RequestMapping(value = "/statistic/category", method = RequestMethod.GET)
    @Operation(summary = "分类统计指定账本的指定时间段的账单")
    public Response<StatisticResponse> getCategoryStatisticTime(@RequestParam Integer bookId, @RequestParam Timestamp startTime, @RequestParam Timestamp endTime) {
        List<Map<String, Object>> payStatistic = billService.categoryPayStatisticTime(bookId, startTime, endTime);
        List<Map<String, Object>> incomeStatistic = billService.categoryIncomeStatisticTime(bookId, startTime, endTime);
        return new Response<>(200, "分类统计指定账本的指定时间段的账单成功",
                new StatisticResponse(payStatistic, incomeStatistic));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "token过期"),
            @ApiResponse(responseCode = "422", description = "参数错误"),
    })
    @RequestMapping(value = "/statistic/day", method = RequestMethod.GET)
    @Operation(summary = "分日统计指定账本的指定时间段的账单")
    public Response<StatisticResponse> getDayStatisticTime(@RequestParam Integer bookId, @RequestParam Timestamp startTime, @RequestParam Timestamp endTime) {
        List<Map<String, Object>> payStatistic = billService.getDayPayStatisticTime(bookId, startTime, endTime);
        List<Map<String, Object>> incomeStatistic = billService.getDayIncomeStatisticTime(bookId, startTime, endTime);
        return new Response<>(200, "分日统计指定账本的指定时间段的账单成功",
                new StatisticResponse(payStatistic, incomeStatistic));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "token过期"),
            @ApiResponse(responseCode = "422", description = "参数错误"),
    })
    @RequestMapping(value = "/category", method = RequestMethod.POST)
    @Operation(summary = "添加用户自定义账单分类")
    public Response<StatisticResponse> addBillCategory(@RequestParam Integer bookId, @RequestParam String billCategoryName,
                                                       @RequestParam String svg, @RequestParam BillType type) {
        billCategoryService.addBillCategory(bookId, billCategoryName, svg, type);
        return new Response<>(200, "添加成功", null);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "token过期"),
            @ApiResponse(responseCode = "422", description = "参数错误"),
    })
    @RequestMapping(value = "/category", method = RequestMethod.GET)
    @Operation(summary = "查看账本下的所有账单分类")
    public Response<List<BillCategory>> getBillCategory(@RequestParam Integer bookId) {
        List<BillCategory> list = billCategoryService.getBillCategoriesByBook(bookId);
        return new Response<>(200, "获取成功", list);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "token过期"),
            @ApiResponse(responseCode = "422", description = "参数错误"),
    })
    @RequestMapping(value = "/statistic/pay", method = RequestMethod.GET)
    @Operation(summary = "获得最大最小支出时间")
    public Response<MaxMinResponse> getMaxMinPayTime(@RequestParam Integer bookId, @RequestParam Timestamp startTime, @RequestParam Timestamp endTime) {
        Map<String, PayBill> result = billService.getMaxMinPayBill(bookId, startTime, endTime);
        GetPayBillResponse max = (GetPayBillResponse) entityToResponse(result.get("max"));
        GetPayBillResponse min = (GetPayBillResponse) entityToResponse(result.get("min"));
        return new Response<>(200, "获得最大最小支出时间成功", new MaxMinResponse(max, min));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "token过期"),
            @ApiResponse(responseCode = "422", description = "参数错误"),
    })
    @RequestMapping(value = "/statistic/income", method = RequestMethod.GET)
    @Operation(summary = "获得最大最小收入时间")
    public Response<MaxMinResponse> getMaxMinIncomeTime(@RequestParam Integer bookId, @RequestParam Timestamp startTime, @RequestParam Timestamp endTime) {
        Map<String, IncomeBill> result = billService.getMaxMinIncomeBill(bookId, startTime, endTime);
        GetIncomeBillResponse max = (GetIncomeBillResponse) entityToResponse(result.get("max"));
        GetIncomeBillResponse min = (GetIncomeBillResponse) entityToResponse(result.get("min"));
        return new Response<>(200, "获得最大最小收入时间成功", new MaxMinResponse(max, min));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "token过期"),
            @ApiResponse(responseCode = "422", description = "参数错误"),
    })
    @RequestMapping(value = "/image", method = RequestMethod.GET)
    @Operation(summary = "获得账单图片")
    public Response<byte[]> getBillImage(@RequestParam Integer billId, @RequestParam BillType type) {
        byte[] image = billService.getBillImage(billId, type);
        return new Response<>(200, "获得账单图片成功", image);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "token过期"),
            @ApiResponse(responseCode = "422", description = "参数错误"),
    })
    @RequestMapping(value = "/bill", method = RequestMethod.PATCH)
    @Operation(summary = "修改账单")
    public Response<Object> changeBill(@RequestParam Integer id, Integer bookId, BigDecimal amount, Timestamp billTime, String remark, Integer inAssetId,
                                       Integer outAssetId, Integer billCategoryId, Boolean refunded, @RequestParam BillType type, MultipartFile file,
                                       Integer payBillId, BigDecimal transferFee) {
        billService.changeBill(id, bookId, amount, billTime, remark, inAssetId, outAssetId, billCategoryId, refunded, type, file, payBillId, transferFee);
        return new Response<>(200, "修改账单成功", null);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "token过期"),
            @ApiResponse(responseCode = "422", description = "参数错误"),
    })
    @RequestMapping(value = "/bill", method = RequestMethod.DELETE)
    @Operation(summary = "删除账单")
    public Response<Object> deleteBill(@RequestParam Integer id, @RequestParam BillType type) {
        billService.deleteBill(id, type);
        return new Response<>(200, "修改账单成功", null);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "token过期"),
            @ApiResponse(responseCode = "422", description = "参数错误"),
    })
    @RequestMapping(value = "/image", method = RequestMethod.DELETE)
    @Operation(summary = "删除账单图片")
    public Response<Object> deleteBillImage(@RequestParam Integer id, @RequestParam BillType type) {
        billService.deleteBillImage(id, type);
        return new Response<>(200, "删除账单图片成功", null);
    }
}