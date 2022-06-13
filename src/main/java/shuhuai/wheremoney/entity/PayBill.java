package shuhuai.wheremoney.entity;

import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

@ToString
@NoArgsConstructor
public class PayBill extends BaseBill implements Serializable {
    private Integer payAssetId;
    private Integer billCategoryId;
    private Boolean refunded;

    public PayBill(Integer id, Integer bookId, Integer payAssetId, Integer billCategoryId, BigDecimal amount, Timestamp billTime, String remark, Boolean refunded, byte[] image) {
        super(id, bookId, amount, billTime, remark, image);
        this.payAssetId = payAssetId;
        this.billCategoryId = billCategoryId;
        this.refunded = refunded;
    }

    public PayBill(Integer bookId, Integer payAssetId, Integer billCategoryId, BigDecimal amount, Timestamp billTime, String remark, Boolean refunded, byte[] image) {
        super(bookId, amount, billTime, remark, image);
        this.payAssetId = payAssetId;
        this.billCategoryId = billCategoryId;
        this.refunded = refunded;
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

    public Boolean getRefunded() {
        return refunded;
    }

    public void setRefunded(Boolean refunded) {
        this.refunded = refunded;
    }
}