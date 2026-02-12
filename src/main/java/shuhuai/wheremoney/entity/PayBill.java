package shuhuai.wheremoney.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Setter
@Getter
@ToString
@NoArgsConstructor
public class PayBill extends BaseBill implements Serializable {
    private Integer payAssetId;
    private Integer billCategoryId;
    private Boolean refunded;

    public PayBill(Integer id, Boolean refunded) {
        super(id);
        this.refunded = refunded;
    }

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

}