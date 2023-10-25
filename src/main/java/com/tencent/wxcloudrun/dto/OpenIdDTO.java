package com.tencent.wxcloudrun.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;


@Data
@Slf4j
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OpenIdDTO implements Serializable {

    /**
     * 登录用户唯一标识码
     */
    private String openId;
}
