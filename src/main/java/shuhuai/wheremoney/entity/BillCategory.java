package shuhuai.wheremoney.entity;

import lombok.*;
import shuhuai.wheremoney.type.BillType;

import java.io.Serializable;

@Setter
@Getter
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
}
