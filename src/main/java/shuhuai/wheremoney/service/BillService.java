package shuhuai.wheremoney.service;

import org.springframework.web.multipart.MultipartFile;
import shuhuai.wheremoney.entity.BaseBill;
import shuhuai.wheremoney.type.BillType;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

public interface BillService {
    void addIncomeBill(Integer bookId, Integer incomeAssetId, Integer billCategoryId, BigDecimal amount, Timestamp time, String remark,
                       MultipartFile file);

    void addPayBill(Integer bookId, Integer payAssetId, Integer billCategoryId, BigDecimal amount, Timestamp time, String remark, Boolean refunded,
                    MultipartFile file);

    void addRefundBill(Integer bookId, Integer payBillId, Integer refundAssetId, BigDecimal amount, Timestamp time, String remark,
                       MultipartFile file);

    void addTransferBill(Integer bookId, Integer inAssetId, Integer outAssetId, BigDecimal amount, BigDecimal transferFee, Timestamp time, String remark,
                         MultipartFile file);

    void updatePayBill(Integer id, Integer bookId, Integer payAssetId, Integer billCategoryId, BigDecimal amount, Timestamp time, String remark, Boolean refunded,
                       MultipartFile file);

    List<BaseBill> getBillByBook(Integer bookId);

    List<BaseBill> getBillByBookTime(Integer bookId, Timestamp startTime, Timestamp endTime);

    BaseBill getBill(Integer id, BillType type);

    List<Map<String, Object>> categoryPayStatisticTime(Integer bookId, Timestamp startTime, Timestamp endTime);

    List<Map<String, Object>> categoryIncomeStatisticTime(Integer bookId, Timestamp startTime, Timestamp endTime);

    List<Map<String, Object>> getDayPayStatisticTime(Integer bookId, Timestamp startTime, Timestamp endTime);

    List<Map<String, Object>> getDayIncomeStatisticTime(Integer bookId, Timestamp startTime, Timestamp endTime);
}