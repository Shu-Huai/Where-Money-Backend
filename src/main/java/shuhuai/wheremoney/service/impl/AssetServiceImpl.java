package shuhuai.wheremoney.service.impl;

import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import shuhuai.wheremoney.entity.*;
import shuhuai.wheremoney.mapper.AssetMapper;
import shuhuai.wheremoney.mapper.BookMapper;
import shuhuai.wheremoney.mapper.UserMapper;
import shuhuai.wheremoney.service.AssetService;
import shuhuai.wheremoney.service.BillService;
import shuhuai.wheremoney.service.excep.common.ParamsException;
import shuhuai.wheremoney.service.excep.common.ServerException;
import shuhuai.wheremoney.type.AssetType;
import shuhuai.wheremoney.utils.BeanGetter;
import shuhuai.wheremoney.utils.RedisConnector;
import shuhuai.wheremoney.utils.TimeComputer;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AssetServiceImpl implements AssetService {
    @Value("${redis.asset.expire}")
    private Long assetExpire;
    @Resource
    private AssetMapper assetMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private BookMapper bookMapper;
    @jakarta.annotation.Resource
    private RedisConnector redisConnector;

    @Override
    public void addAsset(Integer userId, String assetName, BigDecimal balance, AssetType type, Integer billDate,
                         Integer repayDate, BigDecimal quota, Boolean inTotal, String svg) {
        if (userId == null || assetName == null || balance == null || type == null || inTotal == null) {
            throw new ParamsException("参数错误");
        }
        Asset asset = new Asset(userId, type, balance, assetName, billDate, repayDate, quota, inTotal, svg);
        Integer result = assetMapper.insertAssetSelective(asset);
        if (result != 1) {
            throw new ServerException("服务器错误");
        }
        redisConnector.writeObject("asset:" + asset.getId(), asset, TimeComputer.dayToSecond(assetExpire));
    }

    @Override
    public List<Asset> getAllAsset(Integer userId) {
        if (userId == null) {
            throw new ParamsException("参数错误");
        }
        return assetMapper.selectAssetByUserId(userId);
    }

    @Override
    public Asset getAsset(Integer id) {
        if (id == null) {
            throw new ParamsException("参数错误");
        }
        if (redisConnector.existObject("asset:" + id)) {
            redisConnector.setExpire("asset:" + id, TimeComputer.dayToSecond(assetExpire));
            return (Asset) redisConnector.readObject("asset:" + id);
        }
        Asset result = assetMapper.selectAssetById(id);
        if (result != null) {
            redisConnector.writeObject("asset:" + id, result, TimeComputer.dayToSecond(assetExpire));
        }
        return result;
    }

    @Override
    public void updateAsset(Asset asset) {
        Integer result = assetMapper.updateAssetSelectiveById(asset);
        if (result != 1) {
            throw new ServerException("服务器错误");
        }
        redisConnector.writeObject("asset:" + asset.getId(), asset, TimeComputer.dayToSecond(assetExpire));
    }

    @Override
    public List<Map<String, Object>> getDayStatistic(Integer userId, Timestamp startTime, Timestamp endTime) {
        if (userId == null || startTime == null || endTime == null) {
            throw new ParamsException("参数错误");
        }
        User user = userMapper.selectUserByUserId(userId);
        if (user == null) {
            throw new ParamsException("参数错误");
        }
        BigDecimal curTotal = assetMapper.selectTotalAssetByUserId(user.getId());
        if (curTotal == null) {
            curTotal = BigDecimal.ZERO;
        }
        List<Book> bookList = bookMapper.selectBookByUser(user);
        if (bookList == null) {
            bookList = new ArrayList<>();
        }
        List<Map<String, Object>> result = new ArrayList<>();
        List<BaseBill> billTimeList = new ArrayList<>();
        BillService billService = BeanGetter.getBean(BillService.class);
        // Backtrack from current total: bills between endTime and now are also required.
        Timestamp backtrackEndTime = TimeComputer.nextDay(TimeComputer.getDay(TimeComputer.getNow()));
        for (Book book : bookList) {
            billTimeList.addAll(billService.getBillByBookTime(book.getId(), startTime, backtrackEndTime));
        }
        Map<Timestamp, List<BaseBill>> billTimeMap = new HashMap<>();
        for (BaseBill bill : billTimeList) {
            Timestamp day = TimeComputer.getDay(bill.getBillTime());
            if (billTimeMap.containsKey(day)) {
                billTimeMap.get(day).add(bill);
            } else {
                List<BaseBill> temp = new ArrayList<>();
                temp.add(bill);
                billTimeMap.put(day, temp);
            }
        }
        for (Timestamp curTime = TimeComputer.getDay(TimeComputer.getNow()); !curTime.before(TimeComputer.getDay(startTime)); curTime = TimeComputer.prevDay(curTime)) {
            BigDecimal dayTotal = BigDecimal.ZERO;
            List<BaseBill> dayBillList = billTimeMap.get(curTime);
            if (dayBillList != null) {
                for (BaseBill bill : dayBillList) {
                    if (bill instanceof IncomeBill || bill instanceof RefundBill) {
                        dayTotal = dayTotal.add(bill.getAmount());
                    } else if (bill instanceof PayBill) {
                        dayTotal = dayTotal.subtract(bill.getAmount());
                    }
                }
            }
            Timestamp finalTime = curTime;
            BigDecimal finalDayTotal = curTotal;
            if (!curTime.after(TimeComputer.getDay(endTime))) {
                result.add(new HashMap<>(2) {{
                    put("time", TimeComputer.getDayEnd(finalTime));
                    put("total", finalDayTotal);
                }});
                curTotal = curTotal.subtract(dayTotal);
            }
        }
        return result;
    }

    @Override
    public void changeBalanceRelative(Integer id, BigDecimal relativeValue) {
        if (id == null || relativeValue == null) {
            throw new ParamsException("参数错误");
        }
        Integer result = assetMapper.updateBalanceRelativeById(id, relativeValue);
        if (result != 1) {
            throw new ServerException("服务器错误");
        }
        if (redisConnector.existObject("asset:" + id)) {
            Asset asset = (Asset) redisConnector.readObject("asset:" + id);
            asset.setBalance(asset.getBalance().add(relativeValue));
            redisConnector.writeObject("asset:" + id, asset, TimeComputer.dayToSecond(assetExpire));
        }
    }
}
