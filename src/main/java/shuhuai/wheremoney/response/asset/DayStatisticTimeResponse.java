package shuhuai.wheremoney.response.asset;

import java.util.List;
import java.util.Map;

public class DayStatisticTimeResponse {
    List<Map<String, Object>> dayStatistic;

    public DayStatisticTimeResponse(List<Map<String, Object>> dayStatistic) {
        this.dayStatistic = dayStatistic;
    }

    public List<Map<String, Object>> getDayStatistic() {
        return dayStatistic;
    }

    public void setDayStatistic(List<Map<String, Object>> dayStatistic) {
        this.dayStatistic = dayStatistic;
    }
}