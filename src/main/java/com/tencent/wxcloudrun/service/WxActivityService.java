package com.tencent.wxcloudrun.service;

import com.tencent.wxcloudrun.dto.WxActivityDTO;
import com.tencent.wxcloudrun.model.WxActivity;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

public interface WxActivityService extends IService<WxActivity>{


    /**
     * 保存参加活动信息，并且向微信下订单
     * @param wxActivityDTO
     * @return
     */
    public Map<String, Object> activityOrder(WxActivityDTO wxActivityDTO);

}
