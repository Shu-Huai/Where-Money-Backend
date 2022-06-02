package shuhuai.wheremoney.mapper;

import org.apache.ibatis.annotations.Mapper;
import shuhuai.wheremoney.entity.RefundBill;

import java.sql.Timestamp;
import java.util.List;

@Mapper
public interface RefundBillMapper {
    void insertRefundBillSelective(RefundBill refundBill);

    List<RefundBill> selectRefundBillByBookId(Integer bookId);

    List<RefundBill> selectRefundBillByBookIdTime(Integer bookId, Timestamp startTime, Timestamp endTime);

    RefundBill selectRefundBillById(Integer id);
}