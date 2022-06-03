package shuhuai.wheremoney.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import shuhuai.wheremoney.entity.BaseBill;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

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

    @Test
    public void categoryStatisticTimeTest() throws ParseException {
        log.info("类别统计测试");
        Timestamp beginTime = new Timestamp(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2022-05-01 00:00:00").getTime());
        Timestamp endTime = new Timestamp(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2022-05-31 23:59:59").getTime());
        List<Map<String, Object>> result = billService.categoryPayStatisticTime(23, beginTime, endTime);
        for (Map<String, Object> map : result) {
            log.info(map.toString());
        }
    }
}