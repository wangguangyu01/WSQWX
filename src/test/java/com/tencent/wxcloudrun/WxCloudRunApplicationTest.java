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
import com.tencent.wxcloudrun.dto.FileRequestDto;
import com.tencent.wxcloudrun.dto.FileResponseDto;
import com.tencent.wxcloudrun.dto.UserOpenInfoDto;
import com.tencent.wxcloudrun.dto.WxUserPageParamDto;
import com.tencent.wxcloudrun.model.Counter;
import com.tencent.wxcloudrun.model.TSerialNumber;
import com.tencent.wxcloudrun.model.WxUser;
import com.tencent.wxcloudrun.service.AttachmentService;
import com.tencent.wxcloudrun.service.TSerialNumberService;
import com.tencent.wxcloudrun.service.WxUserService;
import com.tencent.wxcloudrun.utils.DateUtils;
import com.tencent.wxcloudrun.utils.UUIDGenerator;
import org.apache.commons.collections4.CollectionUtils;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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

}
