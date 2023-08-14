package com.tencent.wxcloudrun.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BrowsingUsersCountVo implements Serializable {

    /**
     * 浏览总数
     */
    private  int total;


    /**
     * 付费浏览数
     */
    private int payCount;
}
