package shuhuai.wheremoney.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import shuhuai.wheremoney.entity.*;
import shuhuai.wheremoney.mapper.IncomeBillMapper;
import shuhuai.wheremoney.mapper.PayBillMapper;
import shuhuai.wheremoney.mapper.RefundBillMapper;
import shuhuai.wheremoney.mapper.TransferBillMapper;
import shuhuai.wheremoney.service.BillCategoryService;
import shuhuai.wheremoney.service.BillService;
import shuhuai.wheremoney.service.excep.common.ParamsException;
import shuhuai.wheremoney.service.excep.common.ServerException;
import shuhuai.wheremoney.type.BillType;
import shuhuai.wheremoney.utils.TimeComputer;

import javax.annotation.Resource;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.*;

@Service
public class BillServiceImpl implements BillService {
    @Resource
    private BillCategoryService billCategoryService;
    @Resource
    private PayBillMapper payBillMapper;
    @Resource
    private IncomeBillMapper incomeBillMapper;
    @Resource
    private TransferBillMapper transferBillMapper;
    @Resource
    private RefundBillMapper refundBillMapper;

    @Override
    public void addIncomeBill(Integer bookId, Integer incomeAssetId, Integer billCategoryId, BigDecimal amount, Timestamp time, String remark, MultipartFile file) {
        if (bookId == null || amount == null || incomeAssetId == null || billCategoryId == null) {
            throw new ParamsException("参数错误");
        }
        byte[] fileBytes = null;
        if (file != null) {
            try {
                fileBytes = file.getBytes();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        IncomeBill incomeBill = new IncomeBill(bookId, incomeAssetId, billCategoryId, amount, time, remark, fileBytes);
        incomeBillMapper.insertIncomeBillSelective(incomeBill);
    }

    @Override
    public void addPayBill(Integer bookId, Integer payAssetId, Integer billCategoryId, BigDecimal amount, Timestamp time, String remark, Boolean refunded, MultipartFile file) {
        if (bookId == null || amount == null || payAssetId == null || billCategoryId == null) {
            throw new ParamsException("参数错误");
        }
        byte[] fileBytes = null;
        if (file != null) {
            try {
                fileBytes = file.getBytes();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        PayBill payBill = new PayBill(bookId, payAssetId, billCategoryId, amount, time, remark, false, fileBytes);
        payBillMapper.insertPayBillSelective(payBill);
    }

    @Override
    public void addRefundBill(Integer bookId, Integer payBillId, Integer refundAssetId, BigDecimal amount, Timestamp time, String remark, MultipartFile file) {
        if (bookId == null || amount == null || payBillId == null || refundAssetId == null) {
            throw new ParamsException("参数错误");
        }
        byte[] fileBytes = null;
        if (file != null) {
            try {
                fileBytes = file.getBytes();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        RefundBill refundBill = new RefundBill(bookId, payBillId, refundAssetId, amount, time, remark, fileBytes);
        refundBillMapper.insertRefundBillSelective(refundBill);
    }

    @Override
    public void addTransferBill(Integer bookId, Integer inAssetId, Integer outAssetId, BigDecimal amount, BigDecimal transferFee, Timestamp time, String remark,
                                MultipartFile file) {
        if (bookId == null || amount == null || inAssetId == null || outAssetId == null) {
            throw new ParamsException("参数错误");
        }
        byte[] fileBytes = null;
        if (file != null) {
            try {
                fileBytes = file.getBytes();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        TransferBill transferBill = new TransferBill(bookId, inAssetId, outAssetId, amount, transferFee, time, remark, fileBytes);
        transferBillMapper.insertTransferBillSelective(transferBill);
    }

    @Override
    public void updatePayBill(Integer id, Integer bookId, Integer payAssetId, Integer billCategoryId, BigDecimal amount, Timestamp time, String remark, Boolean refuned,
                              MultipartFile file) {
        if (id == null) {
            throw new ParamsException("参数错误");
        }
        byte[] fileBytes = null;
        if (file != null) {
            try {
                fileBytes = file.getBytes();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Integer result = payBillMapper.updatePayBillByIdSelective(new PayBill(id, bookId, payAssetId, billCategoryId, amount, time, remark, refuned, fileBytes));
        if (result == 0) {
            throw new ServerException("服务器错误");
        }
    }

    @Override
    public List<BaseBill> getBillByBook(Integer bookId) {
        if (bookId == null) {
            throw new ParamsException("参数错误");
        }
        List<BaseBill> bills = new ArrayList<>();
        bills.addAll(payBillMapper.selectPayBillByBookId(bookId));
        bills.addAll(incomeBillMapper.selectIncomeBillByBookId(bookId));
        bills.addAll(transferBillMapper.selectTransferBillByBookId(bookId));
        bills.addAll(refundBillMapper.selectRefundBillByBookId(bookId));
        bills.sort(Comparator.comparing(BaseBill::getBillTime).reversed());
        return bills;
    }

    @Override
    public List<BaseBill> getBillByBookTime(Integer bookId, Timestamp startTime, Timestamp endTime) {
        if (bookId == null || startTime == null || endTime == null) {
            throw new ParamsException("参数错误");
        }
        List<BaseBill> bills = new ArrayList<>();
        bills.addAll(payBillMapper.selectPayBillByBookIdTime(bookId, startTime, endTime));
        bills.addAll(incomeBillMapper.selectIncomeBillByBookIdTime(bookId, startTime, endTime));
        bills.addAll(transferBillMapper.selectTransferBillByBookIdTime(bookId, startTime, endTime));
        bills.addAll(refundBillMapper.selectRefundBillByBookIdTime(bookId, startTime, endTime));
        bills.sort(Comparator.comparing(BaseBill::getBillTime).reversed());
        return bills;
    }

    @Override
    public BaseBill getBill(Integer id, BillType type) {
        if (id == null || type == null) {
            throw new ParamsException("参数错误");
        }
        return switch (type) {
            case 支出 -> payBillMapper.selectPayBillById(id);
            case 收入 -> incomeBillMapper.selectIncomeBillById(id);
            case 转账 -> transferBillMapper.selectTransferBillById(id);
            case 退款 -> refundBillMapper.selectRefundBillById(id);
        };
    }

    private Map<Integer, BigDecimal> statisticRefund(List<RefundBill> refundBills) {
        Map<Integer, BigDecimal> result = new HashMap<>();
        for (RefundBill refundBill : refundBills) {
            if (result.containsKey(refundBill.getPayBillId())) {
                result.replace(refundBill.getPayBillId(), result.get(refundBill.getPayBillId()).add(refundBill.getAmount()));
            } else {
                result.put(refundBill.getPayBillId(), refundBill.getAmount());
            }
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> categoryPayStatisticTime(Integer bookId, Timestamp startTime, Timestamp endTime) {
        if (bookId == null || startTime == null || endTime == null) {
            throw new ParamsException("参数错误");
        }
        List<PayBill> payBills = payBillMapper.selectPayBillByBookIdTime(bookId, startTime, endTime);
        List<RefundBill> refundBills = refundBillMapper.selectRefundBillByBookIdTime(bookId, startTime, endTime);
        Map<Integer, BigDecimal> refundMap = statisticRefund(refundBills);
        Map<Integer, BigDecimal> temp = new java.util.HashMap<>();
        BigDecimal total = BigDecimal.ZERO;
        for (PayBill payBill : payBills) {
            Integer categoryId = payBill.getBillCategoryId();
            BigDecimal amount = payBill.getAmount();
            if (refundMap.containsKey(payBill.getId())) {
                amount = amount.subtract(refundMap.get(payBill.getId()));
            }
            if (temp.containsKey(categoryId)) {
                temp.replace(categoryId, temp.get(categoryId).add(amount));
            } else {
                temp.put(categoryId, amount);
            }
            total = total.add(amount);
        }
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<Integer, BigDecimal> entry : temp.entrySet()) {
            if (entry.getValue().compareTo(BigDecimal.ZERO) > 0) {
                result.add(Map.of("category", billCategoryService.getBillCategory(entry.getKey()).getBillCategoryName(), "amount", entry.getValue(),
                        "percent", entry.getValue().divide(total, 4, RoundingMode.HALF_UP).movePointRight(2) + "%"));
            }
        }
        result.sort((first, second) -> ((BigDecimal) second.get("amount")).compareTo((BigDecimal) first.get("amount")));
        return result;
    }

    @Override
    public List<Map<String, Object>> categoryIncomeStatisticTime(Integer bookId, Timestamp startTime, Timestamp endTime) {
        if (bookId == null || startTime == null || endTime == null) {
            throw new ParamsException("参数错误");
        }
        List<IncomeBill> incomeBills = incomeBillMapper.selectIncomeBillByBookIdTime(bookId, startTime, endTime);
        Map<Integer, BigDecimal> temp = new java.util.HashMap<>();
        BigDecimal total = BigDecimal.ZERO;
        for (IncomeBill incomeBill : incomeBills) {
            Integer categoryId = incomeBill.getBillCategoryId();
            BigDecimal amount = incomeBill.getAmount();
            if (temp.containsKey(categoryId)) {
                temp.replace(categoryId, temp.get(categoryId).add(amount));
            } else {
                temp.put(categoryId, amount);
            }
            total = total.add(amount);
        }
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<Integer, BigDecimal> entry : temp.entrySet()) {
            if (entry.getValue().compareTo(BigDecimal.ZERO) > 0) {
                result.add(Map.of("category", billCategoryService.getBillCategory(entry.getKey()).getBillCategoryName(), "amount", entry.getValue(),
                        "percent", entry.getValue().divide(total, 4, RoundingMode.HALF_UP).movePointRight(2) + "%"));
            }
        }
        result.sort((first, second) -> ((BigDecimal) second.get("amount")).compareTo((BigDecimal) first.get("amount")));
        return result;
    }

    @Override
    public List<Map<String, Object>> getDayPayStatisticTime(Integer bookId, Timestamp startTime, Timestamp endTime) {
        if (bookId == null || startTime == null || endTime == null) {
            throw new ParamsException("参数错误");
        }
        List<Map<String, Object>> result = new ArrayList<>();
        List<PayBill> payBills = payBillMapper.selectPayBillByBookIdTime(bookId, startTime, endTime);
        List<RefundBill> refundBills = refundBillMapper.selectRefundBillByBookIdTime(bookId, startTime, endTime);
        Map<Integer, BigDecimal> refundMap = statisticRefund(refundBills);
        for (PayBill payBill : payBills) {
            if (refundMap.containsKey(payBill.getId())) {
                payBill.setAmount(payBill.getAmount().subtract(refundMap.get(payBill.getId())));
            }
        }
        for (Timestamp time = TimeComputer.getDay(startTime); time.before(endTime); time = TimeComputer.nextDay(time)) {
            Timestamp temp = time;
            result.add(Map.of("day", temp, "amount", payBills.stream().filter(bill ->
                            bill.getBillTime().after(temp) && bill.getBillTime().before(TimeComputer.nextDay(temp)))
                    .map(PayBill::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add)));
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> getDayIncomeStatisticTime(Integer bookId, Timestamp startTime, Timestamp endTime) {
        if (bookId == null || startTime == null || endTime == null) {
            throw new ParamsException("参数错误");
        }
        List<Map<String, Object>> result = new ArrayList<>();
        List<IncomeBill> incomeBills = incomeBillMapper.selectIncomeBillByBookIdTime(bookId, startTime, endTime);
        for (Timestamp time = TimeComputer.getDay(startTime); time.before(endTime); time = TimeComputer.nextDay(time)) {
            Timestamp temp = time;
            result.add(Map.of("day", temp, "amount", incomeBills.stream().filter(bill ->
                            bill.getBillTime().after(temp) && bill.getBillTime().before(TimeComputer.nextDay(temp)))
                    .map(IncomeBill::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add)));
        }
        return result;
    }

    @Override
    public Map<String, PayBill> getMaxMinPayBill(Integer bookId, Timestamp startTime, Timestamp endTime) {
        List<PayBill> payBills = payBillMapper.selectPayBillByBookIdTime(bookId, startTime, endTime);
        PayBill max = payBills.get(0);
        PayBill min = payBills.get(0);
        boolean first = true;
        for (PayBill payBill : payBills) {
            if (payBill.getRefunded()) {
                List<RefundBill> refundBills = refundBillMapper.selectRefundBillByPayBillId(payBill.getId());
                BigDecimal refundAmount = refundBills.stream().map(RefundBill::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
                payBill.setAmount(payBill.getAmount().subtract(refundAmount));
            }
            if (first || payBill.getAmount().compareTo(BigDecimal.ZERO) > 0 && payBill.getAmount().compareTo(max.getAmount()) > 0) {
                max = payBill;
            }
            if (first || payBill.getAmount().compareTo(BigDecimal.ZERO) > 0 && payBill.getAmount().compareTo(min.getAmount()) < 0) {
                min = payBill;
            }
            first = false;
        }
        return new HashMap<>(Map.of("max", max, "min", min));
    }

    @Override
    public Map<String, IncomeBill> getMaxMinIncomeBill(Integer bookId, Timestamp startTime, Timestamp endTime) {
        if (bookId == null || startTime == null || endTime == null) {
            throw new ParamsException("参数错误");
        }
        List<IncomeBill> incomeBills = incomeBillMapper.selectIncomeBillByBookIdTime(bookId, startTime, endTime);
        IncomeBill max = incomeBills.get(0);
        IncomeBill min = incomeBills.get(0);
        for (IncomeBill incomeBill : incomeBills) {
            if (incomeBill.getAmount().compareTo(max.getAmount()) > 0) {
                max = incomeBill;
            }
            if (incomeBill.getAmount().compareTo(min.getAmount()) < 0) {
                min = incomeBill;
            }
        }
        return new HashMap<>(Map.of("max", max, "min", min));
    }
}