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
@MappedTypes(BillType.class)
public class BillTypeHandler extends BaseTypeHandler<BillType> {
    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int index, BillType billType, JdbcType jdbcType) throws SQLException {
        preparedStatement.setString(index, billType.getType());
    }

    @Override
    public BillType getNullableResult(ResultSet resultSet, String field) throws SQLException {
        return BillType.getBillTypeEnum(resultSet.getString(field));
    }

    @Override
    public BillType getNullableResult(ResultSet resultSet, int index) throws SQLException {
        return BillType.getBillTypeEnum(resultSet.getString(index));
    }

    @Override
    public BillType getNullableResult(CallableStatement callableStatement, int index) throws SQLException {
        return BillType.getBillTypeEnum(callableStatement.getString(index));
    }
}