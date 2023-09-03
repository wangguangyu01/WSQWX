package com.tencent.wxcloudrun.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tencent.wxcloudrun.dao.OderPayMapper;
import com.tencent.wxcloudrun.dao.WxActivityMapper;
import com.tencent.wxcloudrun.model.OderPay;
import com.tencent.wxcloudrun.model.WxActivity;
import com.tencent.wxcloudrun.utils.DateUtils;
import com.wechat.pay.java.service.refund.model.RefundNotification;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tencent.wxcloudrun.dao.OderPayReturnMapper;
import com.tencent.wxcloudrun.model.OderPayReturn;
import com.tencent.wxcloudrun.service.OderPayReturnService;
import org.springframework.util.ObjectUtils;

@Service
public class OderPayReturnServiceImpl extends ServiceImpl<OderPayReturnMapper, OderPayReturn> implements OderPayReturnService{

    @Resource
    private WxActivityMapper wxActivityMapper;

    @Resource
    private OderPayMapper oderPayMapper;


    @Override
    public void saveOderPayReturn(RefundNotification transaction) throws Exception {
        if (ObjectUtils.isEmpty(transaction)) {
             return;
        }


        // 删除支付记录
        LambdaQueryWrapper<OderPay> payWrapper = new LambdaQueryWrapper<>();
        payWrapper.eq(OderPay::getTransactionId, transaction.getTransactionId());
        OderPay oderPay = oderPayMapper.selectOne(payWrapper);

        // 删除报名活动
        LambdaQueryWrapper<WxActivity> activityLambdaQueryWrapper = new LambdaQueryWrapper<>();
        activityLambdaQueryWrapper.eq(WxActivity::getTradeNo, transaction.getOutTradeNo());
        if (!ObjectUtils.isEmpty(oderPay)) {
            activityLambdaQueryWrapper.eq(WxActivity::getOpenId, oderPay.getOpenId());
        }
        List<WxActivity> wxActivitys = wxActivityMapper.selectList(activityLambdaQueryWrapper);
        WxActivity wxActivityReturn = null;
        if (CollectionUtils.isNotEmpty(wxActivitys)) {
            wxActivityReturn = wxActivitys.get(0);

        }
        wxActivityMapper.delete(activityLambdaQueryWrapper);
        oderPayMapper.delete(payWrapper);

        Date returnCreateTime = null;
        if (StringUtils.isNotBlank(transaction.getCreateTime())) {
            returnCreateTime = DateUtils.parseDateTime(transaction.getCreateTime(), DateUtils.DATE_TIME_PATTERN);
        }
        Date successTime = null;
        if (StringUtils.isNotBlank(transaction.getSuccessTime())) {
            successTime = DateUtils.parseDateTime(transaction.getSuccessTime(), DateUtils.DATE_TIME_PATTERN);
        }
        //保存退款记录
        OderPayReturn oderPayReturn = OderPayReturn.builder()
                .returnCreateTime(returnCreateTime)
                .channel(transaction.getChannel().name())
                .outRefundNo(transaction.getOutRefundNo())
                .outTradeNo(transaction.getOutTradeNo())
                .transactionId(transaction.getTransactionId())
                .refundId(transaction.getRefundId())
                .userReceivedAccount(transaction.getUserReceivedAccount())
                .successTime(successTime)
                .fundsAccount(transaction.getFundsAccount().name())
                .status(transaction.getRefundStatus().name())
                .build();
        oderPayReturn.parseAmount(transaction.getAmount());
        oderPayReturn.parseReturnUser(wxActivityReturn);
        this.baseMapper.insert(oderPayReturn);
    }
}
