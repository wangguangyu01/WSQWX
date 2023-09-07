package com.tencent.wxcloudrun.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tencent.wxcloudrun.dto.WxPersonalBrowseDTO;
import com.tencent.wxcloudrun.dto.WxPersonalBrowsePageDTO;
import com.tencent.wxcloudrun.dto.WxUserPageParamDto;
import com.tencent.wxcloudrun.model.WxBrowsingUsers;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tencent.wxcloudrun.model.WxUser;
import com.tencent.wxcloudrun.vo.WxUserBrowsingUsersVo;

public interface WxBrowsingUsersService extends IService<WxBrowsingUsers>{


    /**
     * 分页查询已经审核通过并且标记为相亲
     *
     * @param wxPersonalBrowseDTO
     * @return
     */
    IPage<WxUserBrowsingUsersVo> queryWxUserPage(WxPersonalBrowsePageDTO wxPersonalBrowseDTO) throws Exception;



    int queryCount(WxPersonalBrowsePageDTO wxPersonalBrowseDTO);

}
