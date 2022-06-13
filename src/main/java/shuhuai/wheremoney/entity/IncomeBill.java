package shuhuai.wheremoney.entity;

import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.sql.Timestamp;

@ToString
@NoArgsConstructor
public class IncomeBill extends BaseBill {
    private Integer incomeAssetId;
    private Integer billCategoryId;

    public IncomeBill(Integer id, Integer bookId, Integer incomeAssetId, Integer billCategoryId, BigDecimal amount, Timestamp billTime, String remark, byte[] image) {
        super(id, bookId, amount, billTime, remark, image);
        this.incomeAssetId = incomeAssetId;
        this.billCategoryId = billCategoryId;
    }

    public IncomeBill(Integer bookId, Integer incomeAssetId, Integer billCategoryId, BigDecimal amount, Timestamp billTime, String remark, byte[] image) {
        super(bookId, amount, billTime, remark, image);
        this.incomeAssetId = incomeAssetId;
        this.billCategoryId = billCategoryId;
    }

    public Integer getIncomeAssetId() {
        return incomeAssetId;
    }

    public void setIncomeAssetId(Integer incomeAssetId) {
        this.incomeAssetId = incomeAssetId;
    }

    public Integer getBillCategoryId() {
        return billCategoryId;
    }

    public void setBillCategoryId(Integer billCategoryId) {
        this.billCategoryId = billCategoryId;
    }
}