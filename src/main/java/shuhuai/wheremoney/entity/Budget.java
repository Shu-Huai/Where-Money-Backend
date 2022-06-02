package shuhuai.wheremoney.entity;

import lombok.AllArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@AllArgsConstructor
public class Budget implements Serializable {
    private Integer id;
    private Integer bookId;
    private Integer billCategoryId;
    private BigDecimal used;
    private BigDecimal limit;
    private Integer times;

    public Budget(Integer bookId, Integer billCategoryId, BigDecimal limit) {
        this.bookId = bookId;
        this.billCategoryId = billCategoryId;
        this.limit = limit;
    }

    public BigDecimal getLimit() {
        return limit;
    }

    public void setLimit(BigDecimal limit) {
        this.limit = limit;
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

    public Integer getBillCategoryId() {
        return billCategoryId;
    }

    public void setBillCategoryId(Integer billCategoryId) {
        this.billCategoryId = billCategoryId;
    }

    public BigDecimal getUsed() {
        return used;
    }

    public void setUsed(BigDecimal used) {
        this.used = used;
    }

    public Integer getTimes() {
        return times;
    }

    public void setTimes(Integer times) {
        this.times = times;
    }
}
