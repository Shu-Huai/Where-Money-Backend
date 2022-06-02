package shuhuai.wheremoney.response.bill;

import java.util.List;
import java.util.Map;

public class StatisticResponse {
    private List<Map<String, Object>> payStatistic;
    private List<Map<String, Object>> incomeStatistic;

    public StatisticResponse(List<Map<String, Object>> payStatistic, List<Map<String, Object>> incomeStatistic) {
        this.payStatistic = payStatistic;
        this.incomeStatistic = incomeStatistic;
    }

    public List<Map<String, Object>> getPayStatistic() {
        return payStatistic;
    }

    public void setPayStatistic(List<Map<String, Object>> payStatistic) {
        this.payStatistic = payStatistic;
    }

    public List<Map<String, Object>> getIncomeStatistic() {
        return incomeStatistic;
    }

    public void setIncomeStatistic(List<Map<String, Object>> incomeStatistic) {
        this.incomeStatistic = incomeStatistic;
    }
}