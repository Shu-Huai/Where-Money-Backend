package shuhuai.wheremoney.utils;

import org.springframework.util.DigestUtils;

public class HashComputer {
    public static String getHashedString(String str) {
        return DigestUtils.md5DigestAsHex(str.getBytes()).toUpperCase();
    }
}
