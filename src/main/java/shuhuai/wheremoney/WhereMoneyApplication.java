package shuhuai.wheremoney;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WhereMoneyApplication {
    public static void main(String[] args) {
        System.setProperty("jasypt.encryptor.password", "prwq0421");
        SpringApplication.run(WhereMoneyApplication.class, args);
    }
}