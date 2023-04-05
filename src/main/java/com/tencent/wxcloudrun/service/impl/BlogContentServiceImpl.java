package com.tencent.wxcloudrun.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tencent.wxcloudrun.dao.BlogContentMapper;
import com.tencent.wxcloudrun.dto.CategoriesRequest;
import com.tencent.wxcloudrun.dto.ContentRequest;
import com.tencent.wxcloudrun.model.BlogContent;
import com.tencent.wxcloudrun.service.BlogContentService;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

@Service
public class BlogContentServiceImpl implements BlogContentService {

    final BlogContentMapper blogContentMapper;

    public BlogContentServiceImpl(BlogContentMapper blogContentMapper) {
        this.blogContentMapper = blogContentMapper;
    }

    @Override
    public IPage<BlogContent> BlogContentByCategoriesPage(CategoriesRequest categoriesRequest) {
        Page page = new Page(categoriesRequest.getCurrentPage(), categoriesRequest.getLimit());
        IPage<BlogContent> blogContentIPage = blogContentMapper.queryBlogContentPage(page, categoriesRequest.getCategories());
        return blogContentIPage;
    }


    @Override
    public BlogContent queryBlogContentInfo(ContentRequest contentRequest) {
        LambdaQueryWrapper<BlogContent> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BlogContent::getUuid, contentRequest.getUuid());
        return blogContentMapper.selectOne(queryWrapper);
    }
}
