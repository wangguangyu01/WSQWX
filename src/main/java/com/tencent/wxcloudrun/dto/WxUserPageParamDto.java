package com.tencent.wxcloudrun.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 相亲相关列表查询
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WxUserPageParamDto implements Serializable {


    /**
     * 只支持80到90
     * 1: 80后
     * 2：90后
     *
     *  搜搜在这个日期前
     */
    private String birthdaySearch;


    /**
     * 根据地域查询
     */
    private String region;

    /**
     * 获取到的上班的工作人员
     */
    private String sex;


    /**
     * 每页条数
     */
    private int limit;

    /**
     * 当前页数
     */
    private int currentPage;


    private String approve;

}
