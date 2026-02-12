package shuhuai.wheremoney.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Setter
@Getter
@ToString
@NoArgsConstructor
public class RefundBill extends BaseBill {
    private Integer payBillId;
    private Integer refundAssetId;

    public RefundBill(Integer id, Integer bookId, Integer payBillId, Integer refundAssetId, BigDecimal amount, Timestamp billTime, String remark, byte[] image) {
        super(id, bookId, amount, billTime, remark, image);
        this.payBillId = payBillId;
        this.refundAssetId = refundAssetId;
    }

    public RefundBill(Integer bookId, Integer payBillId, Integer refundAssetId, BigDecimal amount, Timestamp billTime, String remark, byte[] image) {
        super(bookId, amount, billTime, remark, image);
        this.payBillId = payBillId;
        this.refundAssetId = refundAssetId;
    }

}
