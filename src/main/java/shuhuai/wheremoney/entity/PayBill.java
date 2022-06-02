package shuhuai.wheremoney.entity;

import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

@ToString
public class PayBill extends BaseBill implements Serializable {
    private Integer payAssetId;
    private Integer billCategoryId;

    public PayBill(Integer id, Integer bookId, Integer payAssetId, Integer billCategoryId, BigDecimal amount, Timestamp billTime, String remark, byte[] image) {
        super(id, bookId, amount, billTime, remark, image);
        this.payAssetId = payAssetId;
        this.billCategoryId = billCategoryId;
    }

    public PayBill(Integer bookId, Integer payAssetId, Integer billCategoryId, BigDecimal amount, Timestamp billTime, String remark, byte[] image) {
        super(bookId, amount, billTime, remark, image);
        this.payAssetId = payAssetId;
        this.billCategoryId = billCategoryId;
    }

    public Integer getPayAssetId() {
        return payAssetId;
    }

    public void setPayAssetId(Integer payAssetId) {
        this.payAssetId = payAssetId;
    }

    public Integer getBillCategoryId() {
        return billCategoryId;
    }

    public void setBillCategoryId(Integer billCategoryId) {
        this.billCategoryId = billCategoryId;
    }
}