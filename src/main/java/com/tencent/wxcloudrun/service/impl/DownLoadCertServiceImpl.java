package com.tencent.wxcloudrun.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tencent.wxcloudrun.dao.SystemConfigMapper;
import com.tencent.wxcloudrun.model.SystemConfig;
import com.tencent.wxcloudrun.service.DownLoadCertService;
import com.tencent.wxcloudrun.service.OderPayService;
import com.wechat.pay.contrib.apache.httpclient.WechatPayHttpClientBuilder;
import com.wechat.pay.contrib.apache.httpclient.util.CertSerializeUtil;
import com.wechat.pay.contrib.apache.httpclient.util.PemUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.apache.http.HttpHeaders.ACCEPT;
import static org.apache.http.entity.ContentType.APPLICATION_JSON;

@Slf4j
@Service
public class DownLoadCertServiceImpl implements DownLoadCertService {



    @Autowired
    private SystemConfigMapper systemConfigMapper;

    @Autowired
    private ResourceLoader resourceLoader;

    @Override
    public Map<BigInteger, X509Certificate> getWechatPayCertificates() throws Exception {
        List<X509Certificate> x509Certificates = new ArrayList<>();
        File file = resourceLoader.getResource("classpath:mchidCert/apiclient_key.pem").getFile();
        LambdaQueryWrapper<SystemConfig> systemConfigLambdaQueryWrapper = new LambdaQueryWrapper<>();
        systemConfigLambdaQueryWrapper.eq(SystemConfig::getSysConfigKey, "weixinMchid");
        SystemConfig systemConfigMchid = systemConfigMapper.selectOne(systemConfigLambdaQueryWrapper);
        LambdaQueryWrapper<SystemConfig> api3KeyQueryWrapper = new LambdaQueryWrapper<>();
        api3KeyQueryWrapper.eq(SystemConfig::getSysConfigKey, "weixinCertKeyApi3");
        SystemConfig api3Key = systemConfigMapper.selectOne(api3KeyQueryWrapper);
        String api3KeyStr = api3Key.getSysConfigValue();
        PrivateKey privateKey = PemUtil.loadPrivateKey(new FileInputStream(file));
        WechatPayHttpClientBuilder builder = WechatPayHttpClientBuilder.create()
                .withMerchant(systemConfigMchid.getSysConfigValue(), DownLoadCertService.serialNo, privateKey)
                .withValidator((response) -> true);
        CloseableHttpClient httpClient = builder.build();
        HttpGet httpGet = new HttpGet(DownLoadCertService.certificates);
        httpGet.addHeader(ACCEPT, APPLICATION_JSON.toString());
        CloseableHttpResponse response = httpClient.execute(httpGet);
        if (response.getStatusLine().getStatusCode() == 200) {
            String body = EntityUtils.toString(response.getEntity());
            log.info("getWechatPayCertificates response --->{}", body);
            if (StringUtils.isNotBlank(body)) {
                Map<BigInteger, X509Certificate> newMap =  CertSerializeUtil.deserializeToCerts(api3KeyStr.getBytes("UTF-8"), body);
                return newMap;
            }
        }

        return null;
    }
}
