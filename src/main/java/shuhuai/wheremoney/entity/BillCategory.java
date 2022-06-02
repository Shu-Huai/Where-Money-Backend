package shuhuai.wheremoney.entity;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;
import shuhuai.wheremoney.type.BillType;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@ToString
public class BillCategory implements Serializable {
    private Integer id;
    private Integer bookId;
    private String billCategoryName;
    private String svg;
    private BillType type;

    public BillCategory(Integer bookId, String billCategoryName, String svg, BillType type) {
        this.bookId = bookId;
        this.billCategoryName = billCategoryName;
        this.svg = svg;
        this.type = type;
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

    public String getBillCategoryName() {
        return billCategoryName;
    }

    public void setBillCategoryName(String billCategoryName) {
        this.billCategoryName = billCategoryName;
    }

    public String getSvg() {
        return svg;
    }

    public void setSvg(String svg) {
        this.svg = svg;
    }

    public BillType getType() {
        return type;
    }

    public void setType(BillType type) {
        this.type = type;
    }
}
