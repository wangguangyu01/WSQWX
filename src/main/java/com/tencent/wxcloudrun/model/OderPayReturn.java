package com.tencent.wxcloudrun.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.wechat.pay.java.service.refund.model.Amount;
import com.wechat.pay.java.service.refund.model.Promotion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.ObjectUtils;

/**
    * 退款表
    */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "oder_pay_return")
public class OderPayReturn {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 微信支付退款单号
     */
    @TableField(value = "refund_id")
    private String refundId;

    /**
     * 商户退款单号
     */
    @TableField(value = "out_refund_no")
    private String outRefundNo;

    /**
     * 微信支付交易订单号
     */
    @TableField(value = "transaction_id")
    private String transactionId;

    /**
     * 商户订单号
     */
    @TableField(value = "out_trade_no")
    private String outTradeNo;

    /**
     * 退款渠道
     */
    @TableField(value = "channel")
    private String channel;

    /**
     * 退款入账账户
     */
    @TableField(value = "user_received_account")
    private String userReceivedAccount;

    /**
     * 退款成功时间
     */
    @TableField(value = "success_time")
    private Date successTime;

    /**
     * 退款申请时间
     */
    @TableField(value = "return_create_time")
    private Date returnCreateTime;

    /**
     * 退款状态
     */
    @TableField(value = "status")
    private String status;

    /**
     * 资金账户
     */
    @TableField(value = "funds_account")
    private String fundsAccount;

    /**
     * 订单总金额
     */
    @TableField(value = "amount_total")
    private Long amountTotal;

    /**
     * 退款金额
     */
    @TableField(value = "amount_refund")
    private Long amountRefund;

    /**
     * 用户支付金额
     */
    @TableField(value = "amount_payer_total")
    private Long amountPayerTotal;

    /**
     * 用户退款金额
     */
    @TableField(value = "amount_payer_refund")
    private Long amountPayerRefund;

    /**
     * 应结退款金额
     */
    @TableField(value = "amount_settlement_refund")
    private Long amountSettlementRefund;

    /**
     * 应结订单金额
     */
    @TableField(value = "amount_settlement_total")
    private Long amountSettlementTotal;

    /**
     * 优惠退款金额
     */
    @TableField(value = "amount_discount_refund")
    private Long amountDiscountRefund;

    /**
     * 币种
     */
    @TableField(value = "amount_currency")
    private String amountCurrency;

    /**
     * 手续费退款金额
     */
    @TableField(value = "amount_refund_fee")
    private Long amountRefundFee;

    /**
     * 退款人
     */
    @TableField(value = "pay_return_user")
    private String payReturnUser;

    /**
     * 退款人微信
     */
    @TableField(value = "pay_return_number")
    private String payReturnNumber;

    /**
     * 退款人联系电话
     */
    @TableField(value = "pay_return_phone")
    private String payReturnPhone;

    @TableField(value = "activity_uuid")
    private String activityUuid;


    /** 优惠退款信息 说明：优惠退款信息 */
    @TableField(exist = false)
    private List<Promotion> promotionDetail = new ArrayList<Promotion>();


    /** 金额信息 说明：金额详细信息 */
    @TableField(exist = false)
    private Amount amount;

    public void parseAmount(Amount amount) {
        if (ObjectUtils.isEmpty(amount)) {
             return;
        }
        this.amountCurrency = amount.getCurrency();
        this.amountDiscountRefund = amount.getDiscountRefund();
        this.amountPayerRefund = amount.getPayerRefund();
        this.amountPayerTotal = amount.getPayerTotal();
        this.amountRefundFee = amount.getRefundFee();
        this.amountSettlementRefund = amount.getSettlementRefund();
        this.amountSettlementTotal = amount.getSettlementTotal();
        this.amountTotal = amount.getTotal();
        this.amountRefund = amount.getRefund();

    }

    public void parseReturnUser(WxActivity wxActivityReturn) {
        if (ObjectUtils.isEmpty(wxActivityReturn)) {
            return;
        }
        this.payReturnNumber = wxActivityReturn.getWxNumber();
        this.payReturnUser = wxActivityReturn.getNickname();
        this.payReturnPhone = wxActivityReturn.getPhone();
        this.activityUuid = wxActivityReturn.getActivityUuid();

    }
}
