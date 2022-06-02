package shuhuai.wheremoney.utils;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class JasyptComputerTests {
    @Resource
    JasyptComputer jasyptComputer;

    @Test
    public void compileTest() {
        log.info("编译测试");
    }

    @Test
    public void jasyptEncryptTest() {
        log.info("加密测试：" + jasyptComputer.jasyptEncrypt("prwq0421"));
    }
}
