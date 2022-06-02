package shuhuai.wheremoney.response.bill;

import shuhuai.wheremoney.entity.BaseBill;
import shuhuai.wheremoney.type.BillType;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class BaseGetBillResponse {
    private Integer id;
    private BigDecimal amount;
    private BillType type;
    private Timestamp billTime;
    private String remark;

    public BaseGetBillResponse(Integer id, BigDecimal amount, BillType type, Timestamp billTime, String remark) {
        this.id = id;
        this.amount = amount;
        this.type = type;
        this.billTime = billTime;
        this.remark = remark;
    }

    public BaseGetBillResponse(BaseBill bill, BillType type) {
        this.id = bill.getId();
        this.amount = bill.getAmount();
        this.type = type;
        this.billTime = bill.getBillTime();
        this.remark = bill.getRemark();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BillType getType() {
        return type;
    }

    public void setType(BillType type) {
        this.type = type;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Timestamp getBillTime() {
        return billTime;
    }

    public void setBillTime(Timestamp billTime) {
        this.billTime = billTime;
    }
}