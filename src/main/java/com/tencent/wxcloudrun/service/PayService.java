package com.tencent.wxcloudrun.service;

import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 支付的service
 */
public interface PayService {

    /**
     * 下单的方法
     * @return
     */
    public Map<String, Object>  unifiedOrder(String openId, int payType, String requestIp) throws Exception;
}
