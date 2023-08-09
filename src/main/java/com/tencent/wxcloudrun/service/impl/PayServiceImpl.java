package com.tencent.wxcloudrun.service.impl;

import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tencent.wxcloudrun.dao.SysConfigMapper;
import com.tencent.wxcloudrun.dto.UnifiedorderDto;
import com.tencent.wxcloudrun.model.OderPay;
import com.tencent.wxcloudrun.model.SysConfig;
import com.tencent.wxcloudrun.service.OderPayService;
import com.tencent.wxcloudrun.service.PayService;
import com.tencent.wxcloudrun.service.TSerialNumberService;
import com.tencent.wxcloudrun.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

@Slf4j
@Service
public class PayServiceImpl implements PayService {


    @Value("${weixin.mchid}")
    private String mchid;


    @Value("${weixin.cert.key}")
    private String certKey;


    @Value("${weixin.appid}")
    private String weixinAppId;

    @Autowired
    private SysConfigMapper sysConfigMapper;

    @Autowired
    private TSerialNumberService tSerialNumberService;

    @Autowired
    private OderPayService oderPayService;


    private String notify_url = "https://springboot-u4yq-39835-6-1317513730.sh.run.tcloudbase.com/notifyOrder";


    private String unifiedOrderUrl = "https://api.mch.weixin.qq.com/pay/unifiedorder";

    @Autowired
    public RestTemplate restTemplate;

    /**
     * 生成订单保存到数据库中
     *
     * @param openId
     * @param payType 1:会员充值；2：参加活动
     * @return
     */
    @Override
    public Map<String, Object> unifiedOrder(String openId, int payType, String requestIp) throws Exception {
        String body = "大卫维尼-缴费";
        String priceStr = "";
        String nonceStr = NonceStrUtil.generateNonceStr();
        String out_trade_no = createTradeNo(nonceStr);
        String attach = "大卫维尼-缴费";
        if (payType == 1) {
            // 查询配置中的会员费用
            LambdaQueryWrapper<SysConfig> queryWrapper = new LambdaQueryWrapper();
            queryWrapper.eq(SysConfig::getParamKey, "membership_fee");
            SysConfig sysConfig = sysConfigMapper.selectOne(queryWrapper);
            priceStr = sysConfig.getParamValue();
            body = "大卫维尼-会员充值";
            attach = "会员充值";
        }
        UnifiedorderDto unifiedorderDto = UnifiedorderDto
                .builder()
                .appid(weixinAppId)
                .attach(attach)
                .body(body)
                .mch_id(mchid)
                .nonce_str(nonceStr)
                .sign_type("MD5")
                .notify_url(notify_url)
                .openid(openId)
                .out_trade_no(out_trade_no)
                .spbill_create_ip(requestIp)
                .total_fee(priceStr)
                .fee_type("CNY")
                .trade_type("JSAPI")
                .key(certKey)
                .build();
        String xml = UnifiedorderDto.buildXml(unifiedorderDto);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_XML);
        restTemplate.getMessageConverters().clear();
        restTemplate.getMessageConverters().add(new FastJsonHttpMessageConverter());
        restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
        String xmlResponse = restTemplate.postForObject(unifiedOrderUrl, new HttpEntity<>(xml, httpHeaders), String.class);
        log.info("unifiedOrder xmlResponse --->{}", xmlResponse);
        Date  currenttime = new  Date();
        Long timeStamp = System.currentTimeMillis() / 1000;
        Map<String, String> map = XmlToStringUtil.xmlToMap(xmlResponse);
        if (StringUtils.equals((String) map.get("return_code"), "SUCCESS")
                && StringUtils.equals((String) map.get("result_code"), "SUCCESS")) {
            String prepay_id = (String) map.get("prepay_id");
            TreeMap<String, Object> treeMap = new TreeMap<>();
            treeMap.put("appId", weixinAppId);
            treeMap.put("timeStamp", timeStamp);
            treeMap.put("nonceStr", nonceStr);
            treeMap.put("package",  prepay_id);
            treeMap.put("signType", "MD5");
            String signA = StringUtils.join(treeMap.entrySet(), "&");
            signA += "&key=" + certKey;
            String sgin = MD5Utils.encryptNoWithSalt(signA);
            treeMap.put("paySign", sgin);
            treeMap.remove("appId");
            Date date = DateUtils.timestampTransitionDate(timeStamp);
            OderPay oderPay = OderPay.builder().payAttach(attach)
                    .payBody(body)
                    .payNoncestr(nonceStr)
                    .payTimestamp(timeStamp)
                    .paySuccess(1)
                    .payType(payType)
                    .openId(openId)
                    .prepayId("prepay_id=" + prepay_id)
                    .tradeNo(out_trade_no)
                    .tradeCreateTime(new Date())
                    .build();
            oderPayService.save(oderPay);
            log.info("unifiedOrder--->{}", JacksonUtils.toJson(treeMap));
            return treeMap;
        }
        return null;
    }


    /**
     * redis生成的序列号加随机字符串
     * 生成订单号
     */
    private String createTradeNo(String nonceStr) {
        // 随机生成一个数字，在0到10之间，用于截取32位随机字符串
        Random random = new Random();
        int randomInt = random.nextInt(5);
        if (randomInt == 0) {
            randomInt = randomInt + 1;
        }
        String out_trade_no = tSerialNumberService.createSerialNumber();
        String orderNo = StringUtils.substring(nonceStr, 0,randomInt);
        out_trade_no = out_trade_no + orderNo;
        return out_trade_no;
    }
}
