package shuhuai.wheremoney.response.bill;

import shuhuai.wheremoney.entity.BaseBill;
import shuhuai.wheremoney.type.BillType;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class GetPayBillResponse extends BaseGetBillResponse {
    private String payAsset;
    private String billCategory;

    private Boolean refunded;

    public GetPayBillResponse(Integer id, BigDecimal amount, String remark, Timestamp billTime, String payAsset, String billCategory, Boolean refunded) {
        super(id, amount, BillType.支出, billTime, remark);
        this.payAsset = payAsset;
        this.billCategory = billCategory;
        this.refunded = refunded;
    }

    public GetPayBillResponse(BaseBill bill, String payAsset, String billCategory, Boolean refunded) {
        super(bill, BillType.支出);
        this.payAsset = payAsset;
        this.billCategory = billCategory;
        this.refunded = refunded;
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

    public Boolean getRefunded() {
        return refunded;
    }

    public void setRefunded(Boolean refunded) {
        this.refunded = refunded;
    }
}