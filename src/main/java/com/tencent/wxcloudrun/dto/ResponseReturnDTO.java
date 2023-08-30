package com.tencent.wxcloudrun.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class ResponseReturnDTO  implements Serializable {


    private String returnCode;


    private String appid;


    private String mchId;


    private String subAppid;


    private String subMchId;


    private String nonceStr;


    private String transactionId;


    private String outTradeNo;


    private String refundId;


    private String outRefundNo;


    private int totalFee;


    private int settlementTotalFee;


    private int refundFee;


    private int settlementRefundFee;


    private String refundStatus;


    private String successTime;


    private String refundRecvAccout;


    private String refundAccount;


    private String refundRequestSource;
}
