package com.tencent.wxcloudrun.dto;


import lombok.Data;

import javax.xml.bind.annotation.XmlRootElement;

@Data
@XmlRootElement(name = "xml")
public class XmlRequestDTO {

    /**
     * 小程序的appid
     */
    private String appid;


    /**
     * 商家数据包,包装支付用途以及活动信息
     */
    private String attach;


    /**
     * 付款银行
     */
    private String bank_type;


    /**
     * 付款币种
     */
    private String fee_type;


    /**
     * 是否关注
     */
    private String is_subscribe;


    /**
     * 商户号
     */
    private String mch_id;


    /**
     * 随机字符串
     */
    private String nonce_str;


    /**
     * 支付的小程序用户
     */
    private String openid;


    /**
     * 订单号
     */
    private String out_trade_no;


    /**
     * 业务结果
     */
    private String result_code;


    /**
     * 返回结果
     */
    private String return_code;


    /**
     * 签名
     */
    private String sign;


    /**
     * 交易结束时间
     */
    private String time_end;


    /**
     * 总金额
     */
    private String total_fee;


    /**
     * 交易类型
     */
    private String trade_type;


    /**
     * 微信支付订单号，用于查询微信支付订单的
     */
    private String transaction_id;
}
