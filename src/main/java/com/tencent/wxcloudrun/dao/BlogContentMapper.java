package com.tencent.wxcloudrun.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tencent.wxcloudrun.model.BlogContent;
import org.apache.ibatis.annotations.Param;


import java.util.List;

public interface BlogContentMapper extends BaseMapper<BlogContent> {


    IPage<BlogContent> queryBlogContentPage(@Param("page") Page page,
                                            @Param("categories") String categories);
}
