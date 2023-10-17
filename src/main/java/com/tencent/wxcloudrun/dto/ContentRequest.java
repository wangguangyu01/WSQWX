package com.tencent.wxcloudrun.dto;

import lombok.Data;

@Data
public class ContentRequest {

    /**
     * 根据uuid查看内容详情
     */
    private  String uuid;

    /**
     * 登录用户openid
     */
    private String openId;
}
