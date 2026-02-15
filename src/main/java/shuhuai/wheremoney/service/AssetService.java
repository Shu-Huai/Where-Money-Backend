package shuhuai.wheremoney.service;

import shuhuai.wheremoney.entity.Asset;
import shuhuai.wheremoney.type.AssetType;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

public interface AssetService {
    void addAsset(Integer userId, String assetName, BigDecimal balance, AssetType type,
                  Integer billDate, Integer repayDate, BigDecimal quota, Boolean inTotal, String svg);

    List<Asset> getAllAsset(Integer userId);

    Asset getAsset(Integer id);

    void updateAsset(Asset asset);

    List<Map<String, Object>> getDayStatistic(Integer userId, Timestamp startTime, Timestamp endTime);

    void changeBalanceRelative(Integer id, BigDecimal relativeValue);
}