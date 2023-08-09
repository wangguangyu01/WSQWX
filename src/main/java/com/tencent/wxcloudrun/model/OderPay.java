package com.tencent.wxcloudrun.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 支付表
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "oder_pay")
public class OderPay {
    @TableId(value = "id", type = IdType.INPUT)
    private Long id;

    /**
     * 订单号
     */
    @TableField(value = "trade_no")
    private String tradeNo;

    /**
     * 本微信小程序生成的用户唯一标识
     */
    @TableField(value = "open_id")
    private String openId;

    /**
     * 支付类型
     * 1：会员支付
     * 2：活动支付
     */
    @TableField(value = "pay_type")
    private Integer payType;

    /**
     * 支付内容
     */
    @TableField(value = "pay_body")
    private String payBody;

    /**
     * 支付附加数据
     */
    @TableField(value = "pay_attach")
    private String payAttach;

    /**
     * 支付的随机字符串
     */
    @TableField(value = "pay_nonceStr")
    private String payNoncestr;

    /**
     * prepay_id
     */
    @TableField(value = "prepay_id")
    private String prepayId;

    /**
     * 支付状态
     * 1：下单；
     * 2：支付成功
     * 3：下单失败
     * 4：支付失败
     */
    @TableField(value = "pay_success")
    private int paySuccess;

    /**
     * 发起支付的时间戳
     */
    @TableField(value = "pay_timeStamp")
    private long payTimestamp;

    /**
     * 创建时间
     */
    @TableField(value = "trade_create_time")
    private Date tradeCreateTime;

    /**
     * 支付时间
     */
    @TableField(value = "trade_pay_time")
    private Date tradePayTime;

    @TableField(value = "transaction_id")
    private String transactionId;
}
