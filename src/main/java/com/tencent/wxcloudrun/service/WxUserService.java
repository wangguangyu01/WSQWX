package com.tencent.wxcloudrun.service;

import com.tencent.wxcloudrun.model.WxUser;

public interface WxUserService {

    /**
     * 微信登录wx.login获取的code
     * @param code
     * @return
     */
    public boolean queryWxUserInfo(String code);
}
