package shuhuai.wheremoney.utils;

import java.sql.Timestamp;

public class TimeComputer {
    private static final Timestamp now = new Timestamp(System.currentTimeMillis());

    public static Timestamp getNow() {
        return now;
    }

    public static Timestamp nextDay(Timestamp timestamp) {
        return new Timestamp(timestamp.getTime() + 24 * 60 * 60 * 1000);
    }

    public static Timestamp prevDay(Timestamp timestamp) {
        return new Timestamp(timestamp.getTime() - 24 * 60 * 60 * 1000);
    }

    public static Timestamp getDay(Timestamp timestamp) {
        return new Timestamp(timestamp.getTime() - (timestamp.getTime() + 60 * 60 * 8 * 1000) % (24 * 60 * 60 * 1000));
    }

    public static Timestamp getDayEnd(Timestamp timestamp) {
        return new Timestamp(getDay(timestamp).getTime() + 24 * 60 * 60 * 1000 - 1);
    }

    public static Long dayToSecond(Long day) {
        return day * 24 * 60 * 60;
    }
}