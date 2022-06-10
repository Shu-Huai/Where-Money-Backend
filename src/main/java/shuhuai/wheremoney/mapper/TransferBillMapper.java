package shuhuai.wheremoney.mapper;

import org.apache.ibatis.annotations.Mapper;
import shuhuai.wheremoney.entity.TransferBill;

import java.sql.Timestamp;
import java.util.List;

@Mapper
public interface TransferBillMapper {
    void insertTransferBillSelective(TransferBill transferBill);
    Integer deleteTransferBillById(Integer id);
    Integer updateTransferBillByIdSelective(TransferBill transferBill);

    List<TransferBill> selectTransferBillByBookId(Integer bookId);

    List<TransferBill> selectTransferBillByBookIdTime(Integer bookId, Timestamp startTime, Timestamp endTime);

    TransferBill selectTransferBillById(Integer id);
}