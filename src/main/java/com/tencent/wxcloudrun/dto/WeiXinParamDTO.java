package com.tencent.wxcloudrun.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeiXinParamDTO {

    /**
     * 小程序的appid
     */
    private String weixinAppid;

    /**
     * 小程序的密钥
     */
    private String weixinSecret;

    /**
     * 商户号
     */
    private String mchid;

    /**
     * 商户支付的安全api的密钥
     */
    private String certKey;
}
