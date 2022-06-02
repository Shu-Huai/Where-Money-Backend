package shuhuai.wheremoney.entity;

import lombok.AllArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

@AllArgsConstructor
public class Book implements Serializable {
    private Integer id;
    private Integer userId;
    private String title;
    private Timestamp createTime;
    private Integer beginDate;
    private BigDecimal totalBudget;
    private BigDecimal usedBudget;

    public Book(Integer userId, String title, Integer beginDate) {
        this.userId = userId;
        this.title = title;
        this.beginDate = beginDate;
    }

    public BigDecimal getUsedBudget() {
        return usedBudget;
    }

    public void setUsedBudget(BigDecimal usedBudget) {
        this.usedBudget = usedBudget;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public Integer getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Integer beginDate) {
        this.beginDate = beginDate;
    }

    public BigDecimal getTotalBudget() {
        return totalBudget;
    }

    public void setTotalBudget(BigDecimal totalBudget) {
        this.totalBudget = totalBudget;
    }
}
