package shuhuai.wheremoney.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import shuhuai.wheremoney.entity.BillCategory;
import shuhuai.wheremoney.mapper.BillCategoryMapper;
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
    @Value("${redis.billCategory.expire}")
    private Long billCategoryExpire;
    @Resource
    private BillCategoryMapper billCategoryMapper;

    @jakarta.annotation.Resource
    private RedisConnector redisConnector;

    private void writeCategoryToRedis(BillCategory billCategory) {
        if (redisConnector.existObject("bill_category:" + billCategory.getId())) {
            redisConnector.setExpire("bill_category:" + billCategory.getId(), TimeComputer.dayToSecond(billCategoryExpire));
        } else {
            redisConnector.writeObject("bill_category:" + billCategory.getId(), billCategory, TimeComputer.dayToSecond(billCategoryExpire));
        }
    }

    @Override
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
}
