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
     * 1: 80后女
     * 2：80后男
     * 3：90后女
     * 4: 90后男
     *
     *
     *  搜搜在这个日期前
     */
    private String birthdaySexSearch;


    /**
     * 根据地域查询
     * 1:京户女
     * 2:京户男
     *
     */
    private String regionSexSearch;



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
