package com.tencent.wxcloudrun.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PayOrderDo {

    /**
     * 用户唯一标识码
     */
    private String openId;


    /**
     * 1:会员充值；2：参加活动
     */
    private Integer payType;
}
