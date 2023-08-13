package com.tencent.wxcloudrun.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserOpenInfoDto implements Serializable {

    /**
     * 会话密钥
     */
    private String session_key;


    /**
     * 用户在开放平台的唯一标识符
     */
    private String unionid;

    /**
     * 资料用户的openid
     */
    private String openid;


    private String phone;

    private String errmsg;

    private String errcode;

    /**
     * 登录人员的openid
     */
    private String loginOpenId;
}
