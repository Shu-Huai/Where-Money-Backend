package shuhuai.wheremoney.utils;

import org.jasypt.encryption.StringEncryptor;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class JasyptComputer {
    @Resource
    private StringEncryptor stringEncryptor;

    public String jasyptEncrypt(String text) {
        return stringEncryptor.encrypt(text);
    }
}