package shuhuai.wheremoney.mapper;

import org.apache.ibatis.annotations.Mapper;
import shuhuai.wheremoney.entity.IncomeBill;

import java.sql.Timestamp;
import java.util.List;

@Mapper
public interface IncomeBillMapper {
    void insertIncomeBillSelective(IncomeBill incomeBill);

    Integer deleteIncomeBillById(Integer id);

    Integer updateIncomeBillByIdSelective(IncomeBill incomeBill);

    List<IncomeBill> selectIncomeBillByBookId(Integer bookId);

    List<IncomeBill> selectIncomeBillByBookIdTime(Integer bookId, Timestamp startTime, Timestamp endTime);

    IncomeBill selectIncomeBillById(Integer id);
}