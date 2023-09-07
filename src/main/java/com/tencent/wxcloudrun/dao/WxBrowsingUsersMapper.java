package com.tencent.wxcloudrun.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tencent.wxcloudrun.dto.WxPersonalBrowseDTO;
import com.tencent.wxcloudrun.dto.WxPersonalBrowsePageDTO;
import com.tencent.wxcloudrun.model.WxBrowsingUsers;
import com.tencent.wxcloudrun.model.WxUser;
import com.tencent.wxcloudrun.vo.WxUserBrowsingUsersVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface WxBrowsingUsersMapper extends BaseMapper<WxBrowsingUsers> {


    /**
     * 查询
     *
     * @param page
     * @param wxPersonalBrowseDTO
     * @return
     */
    IPage<WxUserBrowsingUsersVo> queryBrowsingUsersPage(@Param("page") Page page,
                                                        @Param("wxPersonalBrowseDTO") WxPersonalBrowsePageDTO wxPersonalBrowseDTO);

    int queryCount(@Param("wxPersonalBrowseDTO") WxPersonalBrowsePageDTO wxPersonalBrowseDTO);
}
