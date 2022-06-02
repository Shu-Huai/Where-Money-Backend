package shuhuai.wheremoney.service;

import shuhuai.wheremoney.entity.Asset;
import shuhuai.wheremoney.type.AssetType;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

public interface AssetService {
    void addAsset(String userName, String assetName, BigDecimal balance, AssetType type,
                  Integer billDate, Integer repayDate, BigDecimal quota, Boolean inTotal, String svg);

    List<Asset> getAllAsset(String userName);

    Asset getAsset(Integer id);

    void updateAsset(Asset asset);

    List<Map<String, Object>> getDayStatisticTime(String userName, Timestamp startTime, Timestamp endTime);
}
