package com.tencent.wxcloudrun;


import cn.hutool.core.util.CharsetUtil;
import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.symmetric.SM4;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import com.alibaba.fastjson.JSONObject;
import com.tencent.wxcloudrun.dao.CountersMapper;
import com.tencent.wxcloudrun.dto.UserOpenInfoDto;
import com.tencent.wxcloudrun.model.Counter;
import com.tencent.wxcloudrun.utils.DateUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
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



    @Value("${slatKey:wx7290Wsqklollnk}")
    private String slatKey;


    @Test
    public void testCount() throws Exception {
       // Counter counter =  countersMapper.getCounter(1);
       // System.out.println(restTemplate);
        String date = "2023-04-04 03:26:36";
        String date2 = "2023-04-03 03:26:36";
        Date date1 = DateUtils.parseDate(date, DateUtils.DATE_TIME_PATTERN);
        Date date3 = DateUtils.parseDate(date2, DateUtils.DATE_TIME_PATTERN);
        System.out.println(DateUtils.dateCompareTo(date3, date1));


        UserOpenInfoDto userOpenInfoDto = JSONObject.parseObject("{\"session_key\":\"C\\/Av3LqEi7sYeGW4zoR7WA==\",\"openid\":\"o0orR5Ky23-zyG74OInlL3QreR0s\"}",
                UserOpenInfoDto.class);
        System.out.println(userOpenInfoDto);
    }


    @Test
    public void testpwd() throws Exception {
        SymmetricCrypto sm4 =  SmUtil.sm4(slatKey.getBytes());
        String decryptStr = sm4.encryptHex("123432csafsdds",  CharsetUtil.CHARSET_UTF_8);
        System.out.println(decryptStr);
        String encrypt = sm4.decryptStr(decryptStr, CharsetUtil.CHARSET_UTF_8);
        System.out.println(encrypt);

    }
}
