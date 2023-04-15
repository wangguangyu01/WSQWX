package com.tencent.wxcloudrun.service;

import com.tencent.wxcloudrun.model.WxUser;

import java.util.Map;

public interface WxUserService {

    /**
     * 微信登录wx.login获取的code
     *
     * @param code
     * @return
     */
    public Map<String,Object> queryWxUserInfo(String code);
}

