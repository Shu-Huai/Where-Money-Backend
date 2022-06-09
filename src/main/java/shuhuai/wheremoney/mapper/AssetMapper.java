package shuhuai.wheremoney.mapper;

import org.apache.ibatis.annotations.Mapper;
import shuhuai.wheremoney.entity.Asset;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface AssetMapper {
    Integer insertAssetSelective(Asset asset);

    Integer updateAssetSelectiveById(Asset asset);

    Integer updateBalanceRelativeById(Integer id, BigDecimal relativeValue);

    List<Asset> selectAssetByUserId(Integer userId);

    Asset selectAssetById(Integer id);

    BigDecimal selectTotalAssetByUserId(Integer userId);
}