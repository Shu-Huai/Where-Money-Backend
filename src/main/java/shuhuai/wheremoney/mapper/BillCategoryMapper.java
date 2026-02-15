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

    Integer deleteBillCategory(Integer id);

    Integer updateBillCategorySelective(BillCategory billCategory);

    BillCategory selectBillCategoryByBookIdNameType(Integer bookId, String billCategoryName, BillType type);

    Integer deleteBillCategoryByBookId(Integer bookId);
}