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
import shuhuai.wheremoney.entity.Asset;
import shuhuai.wheremoney.response.Response;
import shuhuai.wheremoney.response.asset.DayStatisticTimeResponse;
import shuhuai.wheremoney.response.asset.GetAllAssetResponse;
import shuhuai.wheremoney.response.asset.GetAssetResponse;
import shuhuai.wheremoney.service.AssetService;
import shuhuai.wheremoney.type.AssetType;
import shuhuai.wheremoney.utils.TokenValidator;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/asset")
@Tag(name = "资产管理")
@Slf4j
public class AssetController extends BaseController {
    @Resource
    private AssetService assetService;

    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "token过期"),
            @ApiResponse(responseCode = "422", description = "参数错误"),
            @ApiResponse(responseCode = "500", description = "服务器错误")
    })
    @RequestMapping(value = "/asset", method = RequestMethod.POST)
    @Operation(summary = "新建资产")
    public Response<Object> addAsset(@RequestParam String assetName, @RequestParam BigDecimal balance, @RequestParam AssetType type,
                                     Integer billDate, Integer repayDate, BigDecimal quota,
                                     @RequestParam Boolean inTotal, @RequestParam String svg) {
        String userName = TokenValidator.getUser().get("userName");
        assetService.addAsset(userName, assetName, balance, type, billDate, repayDate, quota, inTotal, svg);
        return new Response<>(200, "新建资产成功", null);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "token过期"),
            @ApiResponse(responseCode = "422", description = "参数错误"),
            @ApiResponse(responseCode = "500", description = "服务器错误")
    })
    @RequestMapping(value = "/asset", method = RequestMethod.PATCH)
    @Operation(summary = "修改资产")
    public Response<Object> updateAsset(@RequestParam Integer assetId, BigDecimal balance, String assetName,
                                        Integer billDate, Integer repayDate, BigDecimal quota, Boolean inTotal, String svg) {
        Asset oldAsset = assetService.getAsset(assetId);
        if (balance != null) oldAsset.setBalance(balance);
        if (assetName != null) oldAsset.setAssetName(assetName);
        if (billDate != null) oldAsset.setBillDate(billDate);
        if (repayDate != null) oldAsset.setRepayDate(repayDate);
        if (quota != null) oldAsset.setQuota(quota);
        if (inTotal != null) oldAsset.setInTotal(inTotal);
        if (svg != null) oldAsset.setSvg(svg);
        assetService.updateAsset(oldAsset);
        return new Response<>(200, "修改资产成功", null);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "token过期"),
            @ApiResponse(responseCode = "422", description = "参数错误"),
    })
    @RequestMapping(value = "/all", method = RequestMethod.GET)
    @Operation(summary = "获得所有资产")
    public Response<GetAllAssetResponse> getAllAsset() {
        String userName = TokenValidator.getUser().get("userName");
        List<Asset> assetList = assetService.getAllAsset(userName);
        return new Response<>(200, "获得资产成功", new GetAllAssetResponse(assetList));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "token过期"),
            @ApiResponse(responseCode = "422", description = "参数错误"),
    })
    @RequestMapping(value = "/asset", method = RequestMethod.GET)
    @Operation(summary = "获得资产")
    public Response<GetAssetResponse> getAsset(@RequestParam Integer id) {
        Asset asset = assetService.getAsset(id);
        return new Response<>(200, "获得资产成功", new GetAssetResponse(asset));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "token过期"),
            @ApiResponse(responseCode = "422", description = "参数错误"),
    })
    @RequestMapping(value = "/day-statistic", method = RequestMethod.GET)
    @Operation(summary = "获得日统计时间")
    public Response<DayStatisticTimeResponse> getDayStatistic(@RequestParam Timestamp startTime, @RequestParam Timestamp endTime) {
        String userName = TokenValidator.getUser().get("userName");
        List<Map<String, Object>> result = assetService.getDayStatistic(userName, startTime, endTime);
        return new Response<>(200, "获得日统计时间", new DayStatisticTimeResponse(result));
    }
}