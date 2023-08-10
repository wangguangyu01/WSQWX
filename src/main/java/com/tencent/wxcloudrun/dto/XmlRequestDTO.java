package com.tencent.wxcloudrun.dto;


import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

import javax.xml.bind.annotation.XmlRootElement;

@Data
@JacksonXmlRootElement(localName = "xml")
public class XmlRequestDTO {

    /**
     * 小程序的appid
     */
    @JacksonXmlProperty(localName = "appid")
    private String appid;


    /**
     * 商家数据包,包装支付用途以及活动信息
     */
    @JacksonXmlProperty(localName = "attach")
    private String attach;


    /**
     * 付款银行
     */
    @JacksonXmlProperty(localName = "bank_type")
    private String bank_type;


    /**
     * 付款币种
     */
    @JacksonXmlProperty(localName = "fee_type")
    private String fee_type;


    /**
     * 是否关注
     */
    @JacksonXmlProperty(localName = "is_subscribe")
    private String is_subscribe;


    /**
     * 商户号
     */
    @JacksonXmlProperty(localName = "mch_id")
    private String mch_id;


    /**
     * 随机字符串
     */
    @JacksonXmlProperty(localName = "nonce_str")
    private String nonce_str;


    /**
     * 支付的小程序用户
     */
    @JacksonXmlProperty(localName = "openid")
    private String openid;


    /**
     * 订单号
     */
    @JacksonXmlProperty(localName = "out_trade_no")
    private String out_trade_no;


    /**
     * 业务结果
     */
    @JacksonXmlProperty(localName = "result_code")
    private String result_code;


    /**
     * 返回结果
     */
    @JacksonXmlProperty(localName = "return_code")
    private String return_code;


    /**
     * 签名
     */
    @JacksonXmlProperty(localName = "sign")
    private String sign;


    /**
     * 交易结束时间
     */
    @JacksonXmlProperty(localName = "time_end")
    private String time_end;


    /**
     * 总金额
     */
    @JacksonXmlProperty(localName = "total_fee")
    private String total_fee;


    /**
     * 交易类型
     */
    @JacksonXmlProperty(localName = "trade_type")
    private String trade_type;


    /**
     * 微信支付订单号，用于查询微信支付订单的
     */
    @JacksonXmlProperty(localName = "transaction_id")
    private String transaction_id;
}
