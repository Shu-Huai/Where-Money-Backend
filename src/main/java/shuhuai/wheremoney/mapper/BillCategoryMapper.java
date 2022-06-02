package shuhuai.wheremoney.mapper;

import org.apache.ibatis.annotations.Mapper;
import shuhuai.wheremoney.entity.BillCategory;
import shuhuai.wheremoney.type.BillType;

import java.util.List;

@Mapper
public interface BillCategoryMapper {
    Integer insertBillCategorySelective(BillCategory billCategory);

    List<BillCategory> selectBillCategoryByBook(Integer bookId);

    List<BillCategory> selectBillCategoryByBookType(Integer bookId, BillType type);

    BillCategory selectBillCategoryById(Integer id);
}