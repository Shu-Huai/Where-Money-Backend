package shuhuai.wheremoney.mapper;

import org.apache.ibatis.annotations.Mapper;
import shuhuai.wheremoney.entity.PayBill;

import java.sql.Timestamp;
import java.util.List;

@Mapper
public interface PayBillMapper {
    void insertPayBillSelective(PayBill payBill);

    List<PayBill> selectPayBillByBookId(Integer bookId);

    List<PayBill> selectPayBillByBookIdTime(Integer bookId, Timestamp startTime, Timestamp endTime);

    PayBill selectPayBillById(Integer id);
}