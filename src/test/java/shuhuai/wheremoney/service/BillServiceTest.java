package shuhuai.wheremoney.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import shuhuai.wheremoney.entity.BaseBill;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class BillServiceTest {
    @Resource
    private BillService billService;

    @Test
    public void compileTest() {
        log.info("编译测试");
    }

    @Test
    public void getBillTest() {
        log.info("获取账单测试");
        List<BaseBill> bills = billService.getBillByBook(23);
        for (BaseBill bill : bills) {
            log.info(bill.toString());
        }
    }
}