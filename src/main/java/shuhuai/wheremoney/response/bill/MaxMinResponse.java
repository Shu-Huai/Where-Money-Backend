package shuhuai.wheremoney.response.bill;

import shuhuai.wheremoney.entity.BaseBill;

public class MaxMinResponse {
    private BaseBill max;
    private BaseBill min;

    public MaxMinResponse(BaseBill max, BaseBill min) {
        this.max = max;
        this.min = min;
    }

    public BaseBill getMax() {
        return max;
    }

    public void setMax(BaseBill max) {
        this.max = max;
    }

    public BaseBill getMin() {
        return min;
    }

    public void setMin(BaseBill min) {
        this.min = min;
    }
}