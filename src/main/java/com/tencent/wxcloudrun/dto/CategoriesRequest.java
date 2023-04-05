package com.tencent.wxcloudrun.dto;


import lombok.Data;

@Data
public class CategoriesRequest {

    /**
     * 内容分类:
     *  1:精彩时刻
     *  2：最新活动
     */
    private String categories;


    /**
     * 每页条数
     */
    private int limit;

    /**
     * 当前页数
     */
    private int currentPage;
}
