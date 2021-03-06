package shuhuai.wheremoney.service;

import org.springframework.web.multipart.MultipartFile;
import shuhuai.wheremoney.entity.BaseBill;
import shuhuai.wheremoney.entity.IncomeBill;
import shuhuai.wheremoney.entity.PayBill;
import shuhuai.wheremoney.type.BillType;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

public interface BillService {
    void addBill(Integer bookId, Integer inAssetId, Integer outAssetId, Integer payBillId, Integer billCategoryId,
                 BillType type, BigDecimal amount, BigDecimal transferFee, Timestamp time, String remark, Boolean refunded, MultipartFile file);

    List<BaseBill> getBillByBook(Integer bookId);

    List<BaseBill> getBillByBookTime(Integer bookId, Timestamp startTime, Timestamp endTime);

    BaseBill getBill(Integer id, BillType type);

    List<Map<String, Object>> categoryPayStatisticTime(Integer bookId, Timestamp startTime, Timestamp endTime);

    List<Map<String, Object>> categoryIncomeStatisticTime(Integer bookId, Timestamp startTime, Timestamp endTime);

    List<Map<String, Object>> getDayPayStatisticTime(Integer bookId, Timestamp startTime, Timestamp endTime);

    List<Map<String, Object>> getDayIncomeStatisticTime(Integer bookId, Timestamp startTime, Timestamp endTime);

    Map<String, PayBill> getMaxMinPayBill(Integer bookId, Timestamp startTime, Timestamp endTime);

    Map<String, IncomeBill> getMaxMinIncomeBill(Integer bookId, Timestamp startTime, Timestamp endTime);

    byte[] getBillImage(Integer id, BillType type);

    void changeBill(Integer id, Integer bookId, BigDecimal amount, Timestamp billTime, String remark, Integer inAssetId, Integer outAssetId,
                    Integer billCategoryId, Boolean refunded, BillType type, MultipartFile file, Integer payBillId, BigDecimal transferFee);

    void deleteBill(Integer id, BillType type);

    void deleteBillImage(Integer id, BillType type);
}