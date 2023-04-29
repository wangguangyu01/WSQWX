package com.tencent.wxcloudrun.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tencent.wxcloudrun.dto.WxUserPageParamDto;
import com.tencent.wxcloudrun.model.BlogContent;
import com.tencent.wxcloudrun.model.WxUser;
import org.apache.ibatis.annotations.Param;

public interface WxUserMapper extends BaseMapper<WxUser> {


    IPage<WxUser> queryWxUserPage(@Param("page") Page page,
                                  @Param("wxUserPageParamDto") WxUserPageParamDto wxUserPageParamDto);


    int updateByPhone(@Param("openId") String openId, @Param("phone") String phone);
}
