package shuhuai.wheremoney.service.impl;

import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import shuhuai.wheremoney.entity.*;
import shuhuai.wheremoney.mapper.IncomeBillMapper;
import shuhuai.wheremoney.mapper.PayBillMapper;
import shuhuai.wheremoney.mapper.RefundBillMapper;
import shuhuai.wheremoney.mapper.TransferBillMapper;
import shuhuai.wheremoney.service.AssetService;
import shuhuai.wheremoney.service.BillCategoryService;
import shuhuai.wheremoney.service.BillService;
import shuhuai.wheremoney.service.BudgetService;
import shuhuai.wheremoney.service.excep.common.ParamsException;
import shuhuai.wheremoney.service.excep.common.ServerException;
import shuhuai.wheremoney.type.BillType;
import shuhuai.wheremoney.utils.RedisConnector;
import shuhuai.wheremoney.utils.TimeComputer;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.*;

@Service
public class BillServiceImpl implements BillService {
    @Value("${redis.bill.expire}")
    private Long billExpire;
    @jakarta.annotation.Resource
    private BillCategoryService billCategoryService;
    @Resource
    private AssetService assetService;
    @jakarta.annotation.Resource
    private PayBillMapper payBillMapper;
    @Resource
    private IncomeBillMapper incomeBillMapper;
    @Resource
    private TransferBillMapper transferBillMapper;
    @Resource
    private RefundBillMapper refundBillMapper;
    @jakarta.annotation.Resource
    private BudgetService budgetService;
    @jakarta.annotation.Resource
    private RedisConnector redisConnector;

    private void writeToRedis(String key, BaseBill bill) {
        if (bill != null) {
            bill.setImage(null);
            redisConnector.writeObject(key, bill, TimeComputer.dayToSecond(billExpire));
        }
    }

    @Override
    public void addBill(Integer bookId, Integer inAssetId, Integer outAssetId, Integer payBillId, Integer billCategoryId,
                        BillType type, BigDecimal amount, BigDecimal transferFee, Timestamp time, String remark, Boolean refunded, MultipartFile file) {
        byte[] fileBytes = null;
        if (file != null) {
            try {
                fileBytes = file.getBytes();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        switch (type) {
            case 支出 -> {
                if (bookId == null || amount == null || outAssetId == null || billCategoryId == null) {
                    throw new ParamsException("参数错误");
                }
                budgetService.changeTotalUsedBudgetRelative(bookId, amount);
                budgetService.changeCategoryUsedBudgetRelative(billCategoryId, amount);
                budgetService.changeCategoryTimesRelative(billCategoryId, 1);
                PayBill payBill = new PayBill(bookId, outAssetId, billCategoryId, amount, time, remark, false, fileBytes);
                assetService.changeBalanceRelative(outAssetId, amount.negate());
                payBillMapper.insertPayBillSelective(payBill);
                writeToRedis("pay_bill:" + payBill.getId(), payBill);
            }
            case 收入 -> {
                if (bookId == null || amount == null || inAssetId == null || billCategoryId == null) {
                    throw new ParamsException("参数错误");
                }
                assetService.changeBalanceRelative(inAssetId, amount);
                IncomeBill incomeBill = new IncomeBill(bookId, inAssetId, billCategoryId, amount, time, remark, fileBytes);
                incomeBillMapper.insertIncomeBillSelective(incomeBill);
                writeToRedis("income_bill:" + incomeBill.getId(), incomeBill);
            }
            case 转账 -> {
                if (bookId == null || amount == null || inAssetId == null || outAssetId == null) {
                    throw new ParamsException("参数错误");
                }
                assetService.changeBalanceRelative(inAssetId, amount.subtract(transferFee != null ? transferFee : BigDecimal.ZERO));
                assetService.changeBalanceRelative(outAssetId, amount.negate());
                TransferBill transferBill = new TransferBill(bookId, inAssetId, outAssetId, amount, transferFee, time, remark, fileBytes);
                transferBillMapper.insertTransferBillSelective(transferBill);
                writeToRedis("transfer_bill:" + transferBill.getId(), transferBill);
            }
            case 退款 -> {
                if (bookId == null || amount == null || payBillId == null || inAssetId == null) {
                    throw new ParamsException("参数错误");
                }
                changeBill(payBillId, null, null, null, null, null, null, null,
                        Boolean.TRUE, BillType.支出, null, null, null);
                budgetService.changeTotalUsedBudgetRelative(bookId, amount.negate());
                budgetService.changeCategoryUsedBudgetRelative(billCategoryId, amount.negate());
                budgetService.changeCategoryTimesRelative(billCategoryId, -1);
                RefundBill refundBill = new RefundBill(bookId, payBillId, inAssetId, amount, time, remark, fileBytes);
                assetService.changeBalanceRelative(inAssetId, amount);
                refundBillMapper.insertRefundBillSelective(refundBill);
                writeToRedis("refund_bill:" + refundBill.getId(), refundBill);
            }
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
        switch (type) {
            case 支出 -> {
                if (redisConnector.existObject("pay_bill:" + id)) {
                    return (PayBill) redisConnector.readObject("pay_bill:" + id);
                }
                PayBill payBill = payBillMapper.selectPayBillById(id);
                writeToRedis("pay_bill:" + id, payBill);
                return payBill;
            }
            case 收入 -> {
                if (redisConnector.existObject("income_bill:" + id)) {
                    return (IncomeBill) redisConnector.readObject("income_bill:" + id);
                }
                IncomeBill incomeBill = incomeBillMapper.selectIncomeBillById(id);
                writeToRedis("income_bill:" + id, incomeBill);
                return incomeBill;
            }
            case 转账 -> {
                if (redisConnector.existObject("transfer_bill:" + id)) {
                    return (TransferBill) redisConnector.readObject("transfer_bill:" + id);
                }
                TransferBill transferBill = transferBillMapper.selectTransferBillById(id);
                writeToRedis("transfer_bill:" + id, transferBill);
                return transferBill;
            }
            case 退款 -> {
                if (redisConnector.existObject("refund_bill:" + id)) {
                    return (RefundBill) redisConnector.readObject("refund_bill:" + id);
                }
                RefundBill refundBill = refundBillMapper.selectRefundBillById(id);
                writeToRedis("refund_bill:" + id, refundBill);
                return refundBill;
            }
            default -> throw new ParamsException("参数错误");
        }
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
        if (bookId == null || startTime == null || endTime == null) {
            throw new ParamsException("参数错误");
        }
        List<PayBill> payBills = payBillMapper.selectPayBillByBookIdTime(bookId, startTime, endTime);
        if (payBills.isEmpty()) {
            return new HashMap<>(Map.of(
                    "max", new PayBill(null, null, null, null, BigDecimal.ZERO, null, null, null, null),
                    "min", new PayBill(null, null, null, null, BigDecimal.ZERO, null, null, null, null)));
        }
        PayBill max = null;
        PayBill min = null;
        for (PayBill payBill : payBills) {
            if (payBill.getRefunded()) {
                List<RefundBill> refundBills = refundBillMapper.selectRefundBillByPayBillId(payBill.getId());
                BigDecimal refundAmount = refundBills.stream().map(RefundBill::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
                payBill.setAmount(payBill.getAmount().subtract(refundAmount));
            }
            if (payBill.getAmount().compareTo(BigDecimal.ZERO) > 0 && (max == null || payBill.getAmount().compareTo(max.getAmount()) > 0)) {
                max = payBill;
            }
            if (payBill.getAmount().compareTo(BigDecimal.ZERO) > 0 && (min == null || payBill.getAmount().compareTo(min.getAmount()) < 0)) {
                min = payBill;
            }
        }
        return new HashMap<>(Map.of("max", max == null ? payBills.get(0) : max, "min", min == null ? payBills.get(0) : min));
    }

    @Override
    public Map<String, IncomeBill> getMaxMinIncomeBill(Integer bookId, Timestamp startTime, Timestamp endTime) {
        if (bookId == null || startTime == null || endTime == null) {
            throw new ParamsException("参数错误");
        }
        List<IncomeBill> incomeBills = incomeBillMapper.selectIncomeBillByBookIdTime(bookId, startTime, endTime);
        if (incomeBills.isEmpty()) {
            return new HashMap<>(Map.of(
                    "max", new IncomeBill(null, null, null, null, BigDecimal.ZERO, null, null, null),
                    "min", new IncomeBill(null, null, null, null, BigDecimal.ZERO, null, null, null)));
        }
        IncomeBill max = null;
        IncomeBill min = null;
        for (IncomeBill incomeBill : incomeBills) {
            if (max == null || incomeBill.getAmount().compareTo(max.getAmount()) > 0) {
                max = incomeBill;
            }
            if (min == null || incomeBill.getAmount().compareTo(min.getAmount()) < 0) {
                min = incomeBill;
            }
        }
        return new HashMap<>(Map.of("max", max == null ? incomeBills.get(0) : max, "min", min == null ? incomeBills.get(0) : min));
    }

    @Override
    public byte[] getBillImage(Integer id, BillType type) {
        if (id == null || type == null) {
            throw new ParamsException("参数错误");
        }
        switch (type) {
            case 支出 -> {
                PayBill payBill = payBillMapper.selectPayBillById(id);
                if (payBill == null) {
                    throw new ParamsException("参数错误");
                }
                return payBill.getImage();
            }
            case 收入 -> {
                IncomeBill incomeBill = incomeBillMapper.selectIncomeBillById(id);
                if (incomeBill == null) {
                    throw new ParamsException("参数错误");
                }
                return incomeBill.getImage();
            }
            case 退款 -> {
                RefundBill refundBill = refundBillMapper.selectRefundBillById(id);
                if (refundBill == null) {
                    throw new ParamsException("参数错误");
                }
                return refundBill.getImage();
            }
            case 转账 -> {
                TransferBill transferBill = transferBillMapper.selectTransferBillById(id);
                if (transferBill == null) {
                    throw new ParamsException("参数错误");
                }
                return transferBill.getImage();
            }
            default -> throw new ParamsException("参数错误");
        }
    }


    @Override
    public void changeBill(Integer id, Integer bookId, BigDecimal amount, Timestamp billTime, String remark, Integer inAssetId, Integer outAssetId,
                           Integer billCategoryId, Boolean refunded, BillType type, MultipartFile file, Integer payBillId, BigDecimal transferFee) {
        if (id == null || type == null) {
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
        switch (type) {
            case 支出 -> {
                PayBill originBill = (PayBill) getBill(id, BillType.支出);
                if (originBill == null) {
                    throw new ParamsException("参数错误");
                }
                PayBill newBill = new PayBill(id, bookId, outAssetId, billCategoryId, amount, billTime, remark, refunded, fileBytes);
                if (newBill.getRefunded() != null && originBill.getRefunded() && !newBill.getRefunded()) {
                    List<RefundBill> refundBills = refundBillMapper.selectRefundBillByPayBillId(originBill.getId());
                    for (RefundBill refundBill : refundBills) {
                        deleteBill(refundBill);
                    }
                }
                if (newBill.getPayAssetId() != null && !Objects.equals(newBill.getPayAssetId(), originBill.getPayAssetId())) {
                    assetService.changeBalanceRelative(originBill.getPayAssetId(), originBill.getAmount());
                    if (newBill.getAmount() != null && originBill.getAmount().compareTo(newBill.getAmount()) != 0) {
                        assetService.changeBalanceRelative(newBill.getPayAssetId(), newBill.getAmount().negate());
                    } else {
                        assetService.changeBalanceRelative(newBill.getPayAssetId(), originBill.getAmount().negate());
                    }
                } else {
                    if (newBill.getAmount() != null && originBill.getAmount().compareTo(newBill.getAmount()) != 0) {
                        assetService.changeBalanceRelative(originBill.getPayAssetId(), originBill.getAmount().subtract(newBill.getAmount()));
                    }
                }
                Integer result = payBillMapper.updatePayBillByIdSelective(newBill);
                if (result != 1) {
                    throw new ServerException("服务器错误");
                }
            }
            case 收入 -> {
                IncomeBill originBill = (IncomeBill) getBill(id, BillType.收入);
                if (originBill == null) {
                    throw new ParamsException("参数错误");
                }
                IncomeBill newBill = new IncomeBill(id, bookId, inAssetId, billCategoryId, amount, billTime, remark, fileBytes);
                if (newBill.getIncomeAssetId() != null && !Objects.equals(newBill.getIncomeAssetId(), originBill.getIncomeAssetId())) {
                    assetService.changeBalanceRelative(originBill.getIncomeAssetId(), originBill.getAmount().negate());
                    if (newBill.getAmount() != null && originBill.getAmount().compareTo(newBill.getAmount()) != 0) {
                        assetService.changeBalanceRelative(newBill.getIncomeAssetId(), newBill.getAmount());
                    } else {
                        assetService.changeBalanceRelative(newBill.getIncomeAssetId(), originBill.getAmount());
                    }
                } else {
                    if (newBill.getAmount() != null && originBill.getAmount().compareTo(newBill.getAmount()) != 0) {
                        assetService.changeBalanceRelative(originBill.getIncomeAssetId(), originBill.getAmount().subtract(newBill.getAmount()).negate());
                    }
                }
                Integer result = incomeBillMapper.updateIncomeBillByIdSelective(newBill);
                if (result != 1) {
                    throw new ServerException("服务器错误");
                }
            }
            case 退款 -> {
                RefundBill originBill = (RefundBill) getBill(id, BillType.退款);
                if (originBill == null) {
                    throw new ParamsException("参数错误");
                }
                RefundBill newBill = new RefundBill(id, bookId, payBillId, inAssetId, amount, billTime, remark, fileBytes);
                if (newBill.getPayBillId() != null && !Objects.equals(newBill.getPayBillId(), originBill.getPayBillId())) {
                    throw new ParamsException("参数错误");
                }
                if (newBill.getRefundAssetId() != null && !Objects.equals(newBill.getRefundAssetId(), originBill.getRefundAssetId())) {
                    assetService.changeBalanceRelative(originBill.getRefundAssetId(), originBill.getAmount().negate());
                    if (newBill.getAmount() != null && originBill.getAmount().compareTo(newBill.getAmount()) != 0) {
                        assetService.changeBalanceRelative(newBill.getRefundAssetId(), newBill.getAmount());
                    } else {
                        assetService.changeBalanceRelative(newBill.getRefundAssetId(), originBill.getAmount());
                    }
                } else {
                    if (newBill.getAmount() != null && originBill.getAmount().compareTo(newBill.getAmount()) != 0) {
                        assetService.changeBalanceRelative(originBill.getRefundAssetId(), originBill.getAmount().subtract(newBill.getAmount()).negate());
                    }
                }
                Integer result = refundBillMapper.updateRefundBillByIdSelective(newBill);
                if (result != 1) {
                    throw new ServerException("服务器错误");
                }
            }
            case 转账 -> {
                TransferBill originBill = (TransferBill) getBill(id, BillType.转账);
                if (originBill == null) {
                    throw new ParamsException("参数错误");
                }
                TransferBill newBill = new TransferBill(id, bookId, inAssetId, outAssetId, amount, transferFee, billTime, remark, fileBytes);
                BigDecimal originTransferFee = originBill.getTransferFee() != null ? originBill.getTransferFee() : BigDecimal.ZERO;
                BigDecimal newTransferFee = newBill.getTransferFee() != null ? newBill.getTransferFee() : originTransferFee;
                if (newBill.getInAssetId() != null && !Objects.equals(newBill.getInAssetId(), originBill.getInAssetId()) &&
                        newBill.getOutAssetId() != null && !Objects.equals(newBill.getOutAssetId(), originBill.getOutAssetId())) {
                    assetService.changeBalanceRelative(originBill.getOutAssetId(), originBill.getAmount());
                    assetService.changeBalanceRelative(originBill.getInAssetId(), (originBill.getAmount().subtract(originTransferFee)).negate());
                    if (newBill.getAmount() != null && originBill.getAmount().compareTo(newBill.getAmount()) != 0) {
                        assetService.changeBalanceRelative(newBill.getOutAssetId(), newBill.getAmount().negate());
                        assetService.changeBalanceRelative(newBill.getInAssetId(), newBill.getAmount().subtract(newTransferFee));
                    } else {
                        assetService.changeBalanceRelative(newBill.getOutAssetId(), originBill.getAmount().negate());
                        assetService.changeBalanceRelative(newBill.getInAssetId(), originBill.getAmount().subtract(newTransferFee));
                    }
                } else if (newBill.getOutAssetId() != null && Objects.equals(newBill.getOutAssetId(), originBill.getOutAssetId())) {
                    assetService.changeBalanceRelative(originBill.getOutAssetId(), originBill.getAmount());
                    if (newBill.getAmount() != null && originBill.getAmount().compareTo(newBill.getAmount()) != 0) {
                        assetService.changeBalanceRelative(newBill.getOutAssetId(), newBill.getAmount().negate());
                        assetService.changeBalanceRelative(newBill.getInAssetId(),
                                newBill.getAmount().subtract(newTransferFee).subtract(originBill.getAmount().subtract(originTransferFee)));
                    } else {
                        assetService.changeBalanceRelative(newBill.getOutAssetId(), originBill.getAmount().negate());
                    }
                } else if (newBill.getInAssetId() != null && Objects.equals(newBill.getInAssetId(), originBill.getInAssetId())) {
                    assetService.changeBalanceRelative(originBill.getInAssetId(), (originBill.getAmount().subtract(originTransferFee)).negate());
                    if (newBill.getAmount() != null && originBill.getAmount().compareTo(newBill.getAmount()) != 0) {
                        assetService.changeBalanceRelative(newBill.getOutAssetId(), (newBill.getAmount().subtract(originBill.getAmount())).negate());
                        assetService.changeBalanceRelative(newBill.getInAssetId(), newBill.getAmount().subtract(newTransferFee));
                    } else {
                        assetService.changeBalanceRelative(newBill.getInAssetId(), originBill.getAmount().subtract(newTransferFee));
                    }
                } else {
                    if (newBill.getAmount() != null && originBill.getAmount().compareTo(newBill.getAmount()) != 0) {
                        assetService.changeBalanceRelative(originBill.getOutAssetId(), originBill.getAmount().subtract(newBill.getAmount()));
                        assetService.changeBalanceRelative(originBill.getInAssetId(),
                                newBill.getAmount().subtract(newTransferFee).subtract(originBill.getAmount().subtract(originTransferFee)));
                    } else {
                        assetService.changeBalanceRelative(originBill.getInAssetId(), originTransferFee.subtract(newTransferFee));
                    }
                }
                Integer result = transferBillMapper.updateTransferBillByIdSelective(newBill);
                if (result != 1) {
                    throw new ServerException("服务器错误");
                }
            }
        }
    }

    private void deleteBill(BaseBill bill) {
        Integer result = 0;
        if (bill instanceof PayBill payBill) {
            assetService.changeBalanceRelative(payBill.getPayAssetId(), payBill.getAmount());
            if (payBill.getRefunded()) {
                List<RefundBill> refundBills = refundBillMapper.selectRefundBillByPayBillId(payBill.getId());
                for (RefundBill refundBill : refundBills) {
                    deleteBill(refundBill);
                }
            }
            result = payBillMapper.deletePayBillById(payBill.getId());
            if (redisConnector.existObject("pay_bill:" + payBill.getId())) {
                redisConnector.deleteObject("pay_bill:" + payBill.getId());
            }
        } else if (bill instanceof IncomeBill incomeBill) {
            assetService.changeBalanceRelative(incomeBill.getIncomeAssetId(), incomeBill.getAmount().negate());
            result = incomeBillMapper.deleteIncomeBillById(incomeBill.getId());
            if (redisConnector.existObject("income_bill:" + incomeBill.getId())) {
                redisConnector.deleteObject("income_bill:" + incomeBill.getId());
            }
        } else if (bill instanceof TransferBill transferBill) {
            assetService.changeBalanceRelative(transferBill.getOutAssetId(), transferBill.getAmount());
            assetService.changeBalanceRelative(transferBill.getInAssetId(), transferBill.getAmount().subtract(transferBill.getTransferFee()).negate());
            result = transferBillMapper.deleteTransferBillById(transferBill.getId());
            if (redisConnector.existObject("transfer_bill:" + transferBill.getId())) {
                redisConnector.deleteObject("transfer_bill:" + transferBill.getId());
            }
        } else if (bill instanceof RefundBill refundBill) {
            assetService.changeBalanceRelative(refundBill.getRefundAssetId(), refundBill.getAmount().negate());
            result = refundBillMapper.deleteRefundBillById(refundBill.getId());
            if (redisConnector.existObject("refund_bill:" + refundBill.getId())) {
                redisConnector.deleteObject("refund_bill:" + refundBill.getId());
            }
        }
        if (result != 1) {
            throw new ServerException("服务器错误");
        }
    }


    @Override
    public void deleteBill(Integer id, BillType type) {
        if (id == null || type == null) {
            throw new ParamsException("参数错误");
        }
        switch (type) {
            case 支出 -> {
                PayBill payBill = (PayBill) getBill(id, type);
                if (payBill == null) {
                    throw new ParamsException("参数错误");
                }
                deleteBill(payBill);
            }
            case 收入 -> {
                IncomeBill incomeBill = (IncomeBill) getBill(id, type);
                if (incomeBill == null) {
                    throw new ParamsException("参数错误");
                }
                deleteBill(incomeBill);
            }
            case 退款 -> {
                RefundBill refundBill = (RefundBill) getBill(id, type);
                if (refundBill == null) {
                    throw new ParamsException("参数错误");
                }
                deleteBill(refundBill);
            }
            case 转账 -> {
                TransferBill transferBill = (TransferBill) getBill(id, type);
                if (transferBill == null) {
                    throw new ParamsException("参数错误");
                }
                deleteBill(transferBill);
            }
        }
    }

    @Override
    public void deleteBillImage(Integer id, BillType type) {
        if (id == null || type == null) {
            throw new ParamsException("参数错误");
        }
        Integer result = 0;
        switch (type) {
            case 支出 -> {
                PayBill payBill = (PayBill) getBill(id, type);
                if (payBill == null) {
                    throw new ParamsException("参数错误");
                }
                payBill.setImage(null);
                result = payBillMapper.updatePayBillById(payBill);
            }
            case 收入 -> {
                IncomeBill incomeBill = (IncomeBill) getBill(id, type);
                if (incomeBill == null) {
                    throw new ParamsException("参数错误");
                }
                incomeBill.setImage(null);
                result = incomeBillMapper.updateIncomeBillById(incomeBill);
            }
            case 退款 -> {
                RefundBill refundBill = (RefundBill) getBill(id, type);
                if (refundBill == null) {
                    throw new ParamsException("参数错误");
                }
                refundBill.setImage(null);
                result = refundBillMapper.updateRefundBillById(refundBill);
            }
            case 转账 -> {
                TransferBill transferBill = (TransferBill) getBill(id, type);
                if (transferBill == null) {
                    throw new ParamsException("参数错误");
                }
                transferBill.setImage(null);
                result = transferBillMapper.updateTransferBillById(transferBill);
            }
        }
        if (result != 1) {
            throw new ServerException("服务器错误");
        }
    }
}