package shuhuai.wheremoney.response.bill;

import shuhuai.wheremoney.entity.BaseBill;

public class MaxMinResponse {
    private BaseGetBillResponse max;
    private BaseGetBillResponse min;

    public MaxMinResponse(BaseGetBillResponse max, BaseGetBillResponse min) {
        this.max = max;
        this.min = min;
    }

    public BaseGetBillResponse getMax() {
        return max;
    }

    public void setMax(BaseGetBillResponse max) {
        this.max = max;
    }

    public BaseGetBillResponse getMin() {
        return min;
    }

    public void setMin(BaseGetBillResponse min) {
        this.min = min;
    }
}