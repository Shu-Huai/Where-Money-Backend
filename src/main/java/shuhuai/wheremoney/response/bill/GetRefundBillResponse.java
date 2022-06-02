package shuhuai.wheremoney.response.bill;

import shuhuai.wheremoney.entity.BaseBill;
import shuhuai.wheremoney.entity.RefundBill;
import shuhuai.wheremoney.type.BillType;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class GetRefundBillResponse extends BaseGetBillResponse {
    private Integer payBillId;
    private String refundAsset;

    public GetRefundBillResponse(Integer id, BigDecimal amount, String remark, Timestamp billTime, Integer payBillId, String refundAsset) {
        super(id, amount, BillType.退款, billTime, remark);
        this.payBillId = payBillId;
        this.refundAsset = refundAsset;
    }

    public GetRefundBillResponse(BaseBill bill, String refundAsset) {
        super(bill, BillType.退款);
        this.payBillId = ((RefundBill) bill).getPayBillId();
        this.refundAsset = refundAsset;
    }

    public Integer getPayBillId() {
        return payBillId;
    }

    public void setPayBillId(Integer payBillId) {
        this.payBillId = payBillId;
    }

    public String getRefundAsset() {
        return refundAsset;
    }

    public void setRefundAsset(String refundAsset) {
        this.refundAsset = refundAsset;
    }
}