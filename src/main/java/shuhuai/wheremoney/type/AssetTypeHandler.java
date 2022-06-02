package shuhuai.wheremoney.type;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@MappedJdbcTypes(JdbcType.VARCHAR)
@MappedTypes(AssetType.class)
public class AssetTypeHandler extends BaseTypeHandler<AssetType> {
    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int index, AssetType assetType, JdbcType jdbcType) throws SQLException {
        preparedStatement.setString(index, assetType.getType());
    }

    @Override
    public AssetType getNullableResult(ResultSet resultSet, String field) throws SQLException {
        return AssetType.getAssetTypeEnum(resultSet.getString(field));
    }

    @Override
    public AssetType getNullableResult(ResultSet resultSet, int index) throws SQLException {
        return AssetType.getAssetTypeEnum(resultSet.getString(index));
    }

    @Override
    public AssetType getNullableResult(CallableStatement callableStatement, int index) throws SQLException {
        return AssetType.getAssetTypeEnum(callableStatement.getString(index));
    }
}
