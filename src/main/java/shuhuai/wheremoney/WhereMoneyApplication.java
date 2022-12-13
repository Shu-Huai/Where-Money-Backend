package shuhuai.wheremoney;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import shuhuai.wheremoney.utils.JasyptComputer;

@SpringBootApplication
@EnableEncryptableProperties
public class WhereMoneyApplication {
    public static void main(String[] args) {
        SpringApplication.run(WhereMoneyApplication.class, args);
    }

    @Bean("jasyptComputer")
    public StringEncryptor jasyptComputer() {
        return new JasyptComputer();
    }
}