package shuhuai.wheremoney.service;

import shuhuai.wheremoney.entity.BillCategory;
import shuhuai.wheremoney.type.BillType;

import java.util.List;

public interface BillCategoryService {
    void addDefaultBillCategory(Integer bookId);

    BillCategory getBillCategory(Integer id);

    List<BillCategory> getBillCategoriesByBook(Integer bookId);

    List<BillCategory> getBillCategoriesByBookType(Integer bookId, BillType type);

    void addBillCategory(Integer bookId, String name, String svg, BillType type);
}