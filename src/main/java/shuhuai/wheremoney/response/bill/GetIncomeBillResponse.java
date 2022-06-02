package shuhuai.wheremoney.response.bill;

import shuhuai.wheremoney.entity.BaseBill;
import shuhuai.wheremoney.type.BillType;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class GetIncomeBillResponse extends BaseGetBillResponse {
    private String incomeAsset;
    private String billCategory;


    public GetIncomeBillResponse(Integer id, BigDecimal amount, String remark, Timestamp billTime, String incomeAsset, String billCategory) {
        super(id, amount, BillType.收入, billTime, remark);
        this.incomeAsset = incomeAsset;
        this.billCategory = billCategory;
    }

    public GetIncomeBillResponse(BaseBill bill, String incomeAsset, String billCategory) {
        super(bill, BillType.收入);
        this.incomeAsset = incomeAsset;
        this.billCategory = billCategory;
    }

    public String getIncomeAsset() {
        return incomeAsset;
    }

    public void setIncomeAsset(String incomeAsset) {
        this.incomeAsset = incomeAsset;
    }

    public String getBillCategory() {
        return billCategory;
    }

    public void setBillCategory(String billCategory) {
        this.billCategory = billCategory;
    }
}