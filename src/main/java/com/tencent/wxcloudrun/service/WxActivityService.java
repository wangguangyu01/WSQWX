package com.tencent.wxcloudrun.service;

import com.tencent.wxcloudrun.dto.WxActivityDTO;
import com.tencent.wxcloudrun.model.WxActivity;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tencent.wxcloudrun.model.WxUser;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface WxActivityService extends IService<WxActivity>{


    /**
     * 保存参加活动信息，并且向微信下订单
     * @param wxActivityDTO
     * @return
     */
    public Map<String, Object> activityOrder(WxActivityDTO wxActivityDTO) throws Exception;


    /**
     * 根据openId查询用户
     * @param openId
     * @param activityUuid
     * @return
     */
    WxActivity queryWxUserOne(String openId, String activityUuid);


    /**
     * 根据参加活动人员手机以及活动uuid查询是否有人参加活动
     * @param phone
     * @param activityUuid
     * @return
     */
    int queryPhoneCount(String phone, String activityUuid);


    /**
     * 根据活动uuid查询报名人
     * @param activityUuid
     * @return
     */
    List<WxActivity> queryWxActivityList(String activityUuid);
}
