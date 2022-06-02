package shuhuai.wheremoney.response.bill;

import shuhuai.wheremoney.entity.BaseBill;
import shuhuai.wheremoney.entity.TransferBill;
import shuhuai.wheremoney.type.BillType;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class GetTransferBillResponse extends BaseGetBillResponse {
    private String inAsset;
    private String outAsset;
    private BigDecimal transferFee;

    public GetTransferBillResponse(Integer id, BigDecimal amount, String remark, Timestamp billTime, String inAsset, String outAsset, BigDecimal transferFee) {
        super(id, amount, BillType.转账, billTime, remark);
        this.inAsset = inAsset;
        this.outAsset = outAsset;
        this.transferFee = transferFee;
    }

    public GetTransferBillResponse(BaseBill bill, String inAsset, String outAsset) {
        super(bill, BillType.转账);
        this.inAsset = inAsset;
        this.outAsset = outAsset;
        this.transferFee = ((TransferBill) bill).getTransferFee();
    }

    public String getInAsset() {
        return inAsset;
    }

    public void setInAsset(String inAsset) {
        this.inAsset = inAsset;
    }

    public String getOutAsset() {
        return outAsset;
    }

    public void setOutAsset(String outAsset) {
        this.outAsset = outAsset;
    }

    public BigDecimal getTransferFee() {
        return transferFee;
    }

    public void setTransferFee(BigDecimal transferFee) {
        this.transferFee = transferFee;
    }
}