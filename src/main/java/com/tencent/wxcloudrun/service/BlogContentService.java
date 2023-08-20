package com.tencent.wxcloudrun.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tencent.wxcloudrun.dto.CategoriesRequest;
import com.tencent.wxcloudrun.dto.ContentRequest;
import com.tencent.wxcloudrun.model.BlogContent;

import java.util.List;

public interface BlogContentService {


    /**
     * 查询精彩时刻以及发布内容的分页
     *
     * @param categoriesRequest
     * @return
     */
    IPage<BlogContent> BlogContentByCategoriesPage(CategoriesRequest categoriesRequest) throws Exception;

    /**
     * 根据uuid查询内容详情
     * @param contentRequest
     * @return
     */
    BlogContent queryBlogContentInfo(ContentRequest contentRequest);


    /**
     * 根据uuid查询内容详情
     * @param uuid
     * @return
     */
    BlogContent queryBlogContentInfo(String  uuid);
}
