package shuhuai.wheremoney.entity;

import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class BaseBill implements Serializable {
    private Integer id;
    private Integer bookId;
    private BigDecimal amount;
    private Timestamp billTime;
    private String remark;
    private byte[] image;

    public BaseBill(Integer id) {
        this.id = id;
    }

    public BaseBill(Integer bookId, BigDecimal amount, Timestamp billTime, String remark, byte[] image) {
        this.bookId = bookId;
        this.amount = amount;
        this.billTime = billTime;
        this.remark = remark;
        this.image = image;
    }

}