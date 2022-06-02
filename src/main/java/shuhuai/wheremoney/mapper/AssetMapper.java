package shuhuai.wheremoney.mapper;

import org.apache.ibatis.annotations.Mapper;
import shuhuai.wheremoney.entity.Asset;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface AssetMapper {
    Integer insertAssetSelective(Asset asset);

    List<Asset> selectAssetByUserId(Integer userId);

    Asset selectAssetById(Integer id);

    Integer updateAssetSelectiveById(Asset asset);

    BigDecimal selectTotalAssetByUserId(Integer userId);
}