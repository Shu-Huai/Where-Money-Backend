package shuhuai.wheremoney.response.book;

import shuhuai.wheremoney.entity.BillCategory;

import java.util.List;

public class GetAllBillCategoryResponse {
    private List<BillCategory> billCategoryList;

    public GetAllBillCategoryResponse(List<BillCategory> billCategoryList) {
        this.billCategoryList = billCategoryList;
    }

    public List<BillCategory> getBillCategoryList() {
        return billCategoryList;
    }

    public void setBillCategoryList(List<BillCategory> billCategoryList) {
        this.billCategoryList = billCategoryList;
    }
}