package com.tencent.wxcloudrun.service;

import java.math.BigInteger;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;

public interface DownLoadCertService {

    /**
     * 支付api证书序列号
     */
    public static final String  serialNo = "6E8DA7447FACAF1C13A10B7801D951A613308E4B";


    /**
     * 获取证书下载的地址
     */
    public static final String certificates = "https://api.mch.weixin.qq.com/v3/certificates";

    /**
     * 通过api获取证书
     * @return
     * @throws Exception
     */
    public Map<BigInteger, X509Certificate> getWechatPayCertificates() throws Exception;
}
