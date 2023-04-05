package com.tencent.wxcloudrun;


import com.tencent.wxcloudrun.dao.CountersMapper;
import com.tencent.wxcloudrun.model.Counter;
import com.tencent.wxcloudrun.utils.DateUtils;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
public class WxCloudRunApplicationTest {

    @Autowired
    private CountersMapper countersMapper;

    @Value("${weixin.secret}")
    private String secret;

    @Autowired
    private RestTemplate restTemplate;


    @Test
    public void testCount() throws Exception {
       // Counter counter =  countersMapper.getCounter(1);
       // System.out.println(restTemplate);
        String date = "2023-04-04 03:26:36";
        String date2 = "2023-04-03 03:26:36";
        Date date1 = DateUtils.parseDate(date, DateUtils.DATE_TIME_PATTERN);
        Date date3 = DateUtils.parseDate(date2, DateUtils.DATE_TIME_PATTERN);
        System.out.println(DateUtils.dateCompareTo(date3, date1));
    }
}
