package shuhuai.wheremoney.entity;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

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

    public BaseBill(Integer bookId, BigDecimal amount, Timestamp billTime, String remark, byte[] image) {
        this.bookId = bookId;
        this.amount = amount;
        this.billTime = billTime;
        this.remark = remark;
        this.image = image;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getBookId() {
        return bookId;
    }

    public void setBookId(Integer bookId) {
        this.bookId = bookId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Timestamp getBillTime() {
        return billTime;
    }

    public void setBillTime(Timestamp billTime) {
        this.billTime = billTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
}