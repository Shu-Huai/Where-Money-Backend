package shuhuai.wheremoney.response.book;

import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
public class StatisticAmountResponse {
    private BigDecimal amount;

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
