package shuhuai.wheremoney.response.bill;

import shuhuai.wheremoney.entity.BaseBill;
import shuhuai.wheremoney.type.BillType;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class GetPayBillResponse extends BaseGetBillResponse {
    private String payAsset;
    private String billCategory;

    public GetPayBillResponse(Integer id, BigDecimal amount, String remark, Timestamp billTime, String payAsset, String billCategory) {
        super(id, amount, BillType.支出, billTime, remark);
        this.payAsset = payAsset;
        this.billCategory = billCategory;
    }

    public GetPayBillResponse(BaseBill bill, String payAsset, String billCategory) {
        super(bill, BillType.支出);
        this.payAsset = payAsset;
        this.billCategory = billCategory;
    }

    public String getPayAsset() {
        return payAsset;
    }

    public void setPayAsset(String payAsset) {
        this.payAsset = payAsset;
    }

    public String getBillCategory() {
        return billCategory;
    }

    public void setBillCategory(String billCategory) {
        this.billCategory = billCategory;
    }
}
