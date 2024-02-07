package shuhuai.wheremoney.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Setter
@Getter
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

}
