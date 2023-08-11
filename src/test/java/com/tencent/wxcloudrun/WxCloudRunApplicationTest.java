package com.tencent.wxcloudrun;


import cn.hutool.core.util.CharsetUtil;
import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.symmetric.SM4;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tencent.wxcloudrun.dao.CountersMapper;
import com.tencent.wxcloudrun.dao.TSerialNumberMapper;
import com.tencent.wxcloudrun.dto.*;
import com.tencent.wxcloudrun.model.Counter;
import com.tencent.wxcloudrun.model.TSerialNumber;
import com.tencent.wxcloudrun.model.WxUser;
import com.tencent.wxcloudrun.service.AttachmentService;
import com.tencent.wxcloudrun.service.TSerialNumberService;
import com.tencent.wxcloudrun.service.WxUserService;
import com.tencent.wxcloudrun.utils.DateUtils;
import com.tencent.wxcloudrun.utils.MD5Utils;
import com.tencent.wxcloudrun.utils.UUIDGenerator;
import com.tencent.wxcloudrun.utils.XmlToStringUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;

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

    @Autowired
    private AttachmentService attachmentService;
    @Autowired
    private WxUserService wxUserService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private TSerialNumberService tSerialNumberService;

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


    @Test
    public void tesFile() throws Exception {
       String fileId= "cloud://prod-0gws2yp30d12fdb1.7072-prod-0gws2yp30d12fdb1-1317513730/6bee57cbc0b94400ae1c900f83cb4792.jpg";
        FileRequestDto fileRequestDto = new FileRequestDto();
        fileRequestDto.setFileid(fileId);
        List<FileRequestDto> fileRequestDtoList = new ArrayList<>();
        fileRequestDtoList.add(fileRequestDto);
        List<FileResponseDto> dtoList = attachmentService.batchDownloadFile(fileRequestDtoList);

    }


    @Test
    public void testQueryUser() throws Exception {
        List<String> fileid_list = new ArrayList<>();
        fileid_list.add("cloud://prod-0gws2yp30d12fdb1.7072-prod-0gws2yp30d12fdb1-1317513730/3eee2b8c23334d69a421d84037c68c07.jpg");
        attachmentService.batchDeleteFile(fileid_list);
    }


    @Test
    public void testRedis() throws Exception {
        String serialNumber = tSerialNumberService.createSerialNumber();
        System.out.println(serialNumber);
    }



    @Test
    public void testUpdateUser() throws Exception {
        String uuid = UUIDGenerator.getUUID();
        System.out.println(uuid);
        int count = wxUserService.updateByPhone(uuid, "13535356755");
        Assert.assertTrue(count >= 0);
    }



    @Test
    public void testSm4() throws Exception {
        SymmetricCrypto sm4 =  SmUtil.sm4(slatKey.getBytes());
        String dd  = sm4.decryptStr("f9e6a7da85ff52b1dd041c170f8fc27d690ccd6cc59b3bffe22d2d684a39df19");
        System.out.println(dd);
    }


    @Test
    public void testMap() throws Exception {
        UnifiedorderDto unifiedorderDto = UnifiedorderDto
                .builder()
                .appid("wx638e212bbba9e7f7")
                .attach("支付测试")
                .body("JSAPI支付测试")
                .mch_id("1649752154")
                .nonce_str("1add1a30ac87aa2db72f57a2375d8fec")
                .sign_type("MD5")
                .notify_url("https://springboot-u4yq-39835-6-1317513730.sh.run.tcloudbase.com/placeOrder")
                .openid("o0orR5Ky23-zyG74OInlL3QreR0s")
                .out_trade_no("141565987755678")
                .spbill_create_ip("14.23.150.211")
                .total_fee("1")
                .fee_type("CNY")
                .trade_type("JSAPI")
                .key("gundan7890321gundan7890321123456")
                .build();
        String xml = UnifiedorderDto.buildXml(unifiedorderDto);
        System.out.println("xml--->" + xml);
    }

    @Test
    public void xmlToMap() throws Exception {
        String xml = "<xml><return_code><![CDATA[SUCCESS]]></return_code>\n" +
                "<return_msg><![CDATA[OK]]></return_msg>\n" +
                "<result_code><![CDATA[SUCCESS]]></result_code>\n" +
                "<mch_id><![CDATA[1649752154]]></mch_id>\n" +
                "<appid><![CDATA[wx638e212bbba9e7f7]]></appid>\n" +
                "<nonce_str><![CDATA[dP1CO3IigrlC5oSf]]></nonce_str>\n" +
                "<sign><![CDATA[893D669003736C1EE4D803EF568FBA50]]></sign>\n" +
                "<prepay_id><![CDATA[wx0800392700032189ceb0fbf88a5a930000]]></prepay_id>\n" +
                "<trade_type><![CDATA[JSAPI]]></trade_type>\n" +
                "</xml>";

       Map<String,String> map = XmlToStringUtil.xmlToMap(xml);
       System.out.println(map);
    }


    @Test
    public void testMapSginPay() throws Exception {

        TreeMap<String, Object> treeMap = new TreeMap<>();
        treeMap.put("appId", "wx638e212bbba9e7f7");
        treeMap.put("timeStamp", System.currentTimeMillis() / 1000);
        treeMap.put("nonceStr", "bdfbdfbdfbdfbd");
        treeMap.put("package", "prepay_id=wx0800392700032189ceb0fbf88a5a930000");
        treeMap.put("signType", "MD5");
        String signA = StringUtils.join(treeMap.entrySet(), "&");
        signA+="&key=gundan7890321gundan7890321123456";
        System.out.println(signA);
        String sign = MD5Utils.encryptNoWithSalt(signA);
        System.out.println(sign);

    }

    @Test
    public void testTime() {
//        Long timeStamp = System.currentTimeMillis() / 1000;
//        Date date = new Date(timeStamp * 1000);
//        System.out.println(DateUtils.format(date, DateUtils.DATE_TIME_PATTERN) );

//        Date date = DateUtils.timestampTransitionDate(1691594943L);
//        String dateStr = DateUtils.format(date, DateUtils.DATE_TIME_PATTERN);
//        System.out.println(dateStr);
        Date date = new Date();
        String dd = DateUtils.format(date, "yyyyMMddHHmmssSSS");
        System.out.println(dd);
    }

}
