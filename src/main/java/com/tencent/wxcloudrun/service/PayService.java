package com.tencent.wxcloudrun.service;

import com.tencent.wxcloudrun.model.OderPay;
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
    public Map<String, Object>  unifiedOrder(String openId, int payType, String requestIp, String activityUuid)
            throws Exception;


    /**
     * 根据订单号查询交易
     * @param tradeNo
     * @return
     */
    OderPay queryOderPay(String tradeNo);
}
