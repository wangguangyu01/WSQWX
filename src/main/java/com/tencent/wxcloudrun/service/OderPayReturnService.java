package com.tencent.wxcloudrun.service;

import com.tencent.wxcloudrun.model.OderPayReturn;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wechat.pay.java.service.refund.model.RefundNotification;

public interface OderPayReturnService extends IService<OderPayReturn>{


    /**
     * 处理退款回调
     * @param transaction
     */
    public void saveOderPayReturn(RefundNotification transaction) throws Exception;

}
