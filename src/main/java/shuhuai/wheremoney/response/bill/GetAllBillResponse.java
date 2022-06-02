package shuhuai.wheremoney.response.bill;

import java.util.List;

public class GetAllBillResponse {
    private List<BaseGetBillResponse> billList;

    public GetAllBillResponse(List<BaseGetBillResponse> billList) {
        this.billList = billList;
    }

    public List<BaseGetBillResponse> getBillList() {
        return billList;
    }

    public void setBillList(List<BaseGetBillResponse> billList) {
        this.billList = billList;
    }
}
