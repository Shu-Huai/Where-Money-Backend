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
public class TransferBill extends BaseBill {
    private Integer inAssetId;
    private Integer outAssetId;
    private BigDecimal transferFee;

    public TransferBill(Integer id, Integer bookId, Integer inAssetId, Integer outAssetId, BigDecimal amount, BigDecimal tranferFee, Timestamp billTime, String remark,
                        byte[] image) {
        super(id, bookId, amount, billTime, remark, image);
        this.inAssetId = inAssetId;
        this.outAssetId = outAssetId;
        this.transferFee = tranferFee;
    }

    public TransferBill(Integer bookId, Integer inAssetId, Integer outAssetId, BigDecimal amount, BigDecimal tranferFee, Timestamp billTime, String remark, byte[] image) {
        super(bookId, amount, billTime, remark, image);
        this.inAssetId = inAssetId;
        this.outAssetId = outAssetId;
        this.transferFee = tranferFee;
    }

}
