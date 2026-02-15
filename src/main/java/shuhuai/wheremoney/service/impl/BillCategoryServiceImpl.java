package shuhuai.wheremoney.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shuhuai.wheremoney.entity.BillCategory;
import shuhuai.wheremoney.mapper.BillCategoryMapper;
import shuhuai.wheremoney.mapper.IncomeBillMapper;
import shuhuai.wheremoney.mapper.PayBillMapper;
import shuhuai.wheremoney.service.BillCategoryService;
import shuhuai.wheremoney.service.excep.common.ParamsException;
import shuhuai.wheremoney.service.excep.common.ServerException;
import shuhuai.wheremoney.type.BillType;
import shuhuai.wheremoney.utils.JsonOperator;
import shuhuai.wheremoney.utils.RedisConnector;
import shuhuai.wheremoney.utils.TimeComputer;

import java.util.List;

@Service
public class BillCategoryServiceImpl implements BillCategoryService {
    @Value("${default.svg}")
    private String defaultSvg;
    @Value("${redis.billCategory.expire}")
    private Long billCategoryExpire;
    @Resource
    private BillCategoryMapper billCategoryMapper;
    @Resource
    private PayBillMapper payBillMapper;
    @Resource
    private IncomeBillMapper incomeBillMapper;

    @Resource
    private RedisConnector redisConnector;

    private void writeCategoryToRedis(BillCategory billCategory) {
        if (redisConnector.existObject("bill_category:" + billCategory.getId())) {
            redisConnector.setExpire("bill_category:" + billCategory.getId(), TimeComputer.dayToSecond(billCategoryExpire));
        } else {
            redisConnector.writeObject("bill_category:" + billCategory.getId(), billCategory, TimeComputer.dayToSecond(billCategoryExpire));
        }
    }

    private Integer ensureDeletedCategory(Integer bookId, BillType type) {
        BillCategory defaultCategory = billCategoryMapper.selectBillCategoryByBookIdNameType(bookId, "已删除的", type);
        if (defaultCategory != null) {
            return defaultCategory.getId();
        }
        BillCategory temp = new BillCategory(bookId, "已删除的", defaultSvg, type);
        Integer result = billCategoryMapper.insertBillCategorySelective(temp);
        if (result == null || result != 1) {
            throw new ServerException("服务器错误");
        }
        redisConnector.writeObject("bill_category:" + temp.getId(), temp, TimeComputer.dayToSecond(billCategoryExpire));
        return temp.getId();
    }

    private void evictBillCaches(String cachePrefix, List<Integer> billIds) {
        if (billIds == null || billIds.isEmpty()) {
            return;
        }
        String[] keys = billIds.stream().map(id -> cachePrefix + id).toArray(String[]::new);
        redisConnector.deleteObject(keys);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addDefaultBillCategory(Integer bookId) {
        JSONArray jsonArray = JsonOperator.getMapFromJson("DefaultBillCategory");
        if (jsonArray == null) {
            return;
        }
        for (Object item : jsonArray) {
            JSONObject obj = JSON.parseObject(item + "");
            BillCategory temp = new BillCategory(bookId, obj.get("billCategoryName").toString(), obj.get("svg").toString(), BillType.valueOf(obj.get("type").toString()));
            Integer result = billCategoryMapper.insertBillCategorySelective(temp);
            if (result != 1) {
                throw new ServerException("服务器错误");
            }
            redisConnector.writeObject("bill_category:" + temp.getId(), temp, TimeComputer.dayToSecond(billCategoryExpire));
        }
    }

    @Override
    public BillCategory getBillCategory(Integer id) {
        if (id == null) {
            throw new ParamsException("参数错误");
        }
        if (redisConnector.existObject("bill_category:" + id)) {
            redisConnector.setExpire("bill_category:" + id, TimeComputer.dayToSecond(billCategoryExpire));
            return (BillCategory) redisConnector.readObject("bill_category:" + id);
        }
        BillCategory result = billCategoryMapper.selectBillCategoryById(id);
        if (result != null) {
            redisConnector.writeObject("bill_category:" + id, result, TimeComputer.dayToSecond(billCategoryExpire));
        }
        return result;
    }

    @Override
    public List<BillCategory> getBillCategoriesByBook(Integer bookId) {
        List<BillCategory> result = billCategoryMapper.selectBillCategoryByBook(bookId);
        for (BillCategory item : result) {
            writeCategoryToRedis(item);
        }
        return result;
    }

    @Override
    public List<BillCategory> getBillCategoriesByBookType(Integer bookId, BillType type) {
        List<BillCategory> result = billCategoryMapper.selectBillCategoryByBookType(bookId, type);
        for (BillCategory item : result) {
            writeCategoryToRedis(item);
        }
        return billCategoryMapper.selectBillCategoryByBookType(bookId, type);
    }

    @Override
    public void addBillCategory(Integer bookId, String name, String svg, BillType type) {
        BillCategory temp = new BillCategory(bookId, name, svg, type);
        Integer result = billCategoryMapper.insertBillCategorySelective(temp);
        if (result != 1) {
            throw new ServerException("服务器错误");
        }
        redisConnector.writeObject("bill_category:" + temp.getId(), temp, TimeComputer.dayToSecond(billCategoryExpire));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteBillCategory(Integer id) {
        if (id == null) {
            throw new ParamsException("参数错误");
        }
        BillCategory billCategory = billCategoryMapper.selectBillCategoryById(id);
        if (billCategory == null) {
            throw new ParamsException("参数错误");
        }
        if (billCategory.getBillCategoryName().equals("已删除的")) {
            throw new ParamsException("参数错误");
        }
        // 如果是支出分类，则需要处理关联的账单
        if (billCategory.getType() == BillType.支出) {
            List<Integer> billIds = payBillMapper.selectPayBillIdsByBookIdCategory(billCategory.getBookId(), billCategory.getId());
            // 如果有关联的账单，则将其分类设为默认分类
            if (!billIds.isEmpty()) {
                // 确保存在默认分类
                Integer defaultCategoryId = ensureDeletedCategory(billCategory.getBookId(), billCategory.getType());
                Integer updateResult = payBillMapper.updatePayBillCategoryByBookIdCategory(billCategory.getBookId(), billCategory.getId(), defaultCategoryId);
                if (updateResult == null || updateResult != billIds.size()) {
                    throw new ServerException("服务器错误");
                }
                evictBillCaches("pay_bill:", billIds);
            }
            // 如果是收入分类，则需要处理关联的账单
        } else if (billCategory.getType() == BillType.收入) {
            List<Integer> billIds = incomeBillMapper.selectIncomeBillIdsByBookIdCategory(billCategory.getBookId(), billCategory.getId());
            if (!billIds.isEmpty()) {
                Integer defaultCategoryId = ensureDeletedCategory(billCategory.getBookId(), billCategory.getType());
                Integer updateResult = incomeBillMapper.updateIncomeBillCategoryByBookIdCategory(billCategory.getBookId(), billCategory.getId(), defaultCategoryId);
                if (updateResult == null || updateResult != billIds.size()) {
                    throw new ServerException("服务器错误");
                }
                evictBillCaches("income_bill:", billIds);
            }
        } else {
            throw new ParamsException("参数错误");
        }
        Integer result = billCategoryMapper.deleteBillCategory(id);
        if (result != 1) {
            throw new ServerException("服务器错误");
        }
        redisConnector.deleteObject("bill_category:" + id);
    }

    @Override
    public void updateBillCategory(Integer id, String name, String svg, BillType type, Integer bookId) {
        if (id == null) {
            throw new ParamsException("参数错误");
        }
        BillCategory billCategory = new BillCategory(id, bookId, name, svg, type);
        Integer result = billCategoryMapper.updateBillCategorySelective(billCategory);
        if (result != 1) {
            throw new ServerException("服务器错误");
        }
        writeCategoryToRedis(billCategory);
    }
}
