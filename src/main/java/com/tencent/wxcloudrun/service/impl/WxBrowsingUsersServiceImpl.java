package com.tencent.wxcloudrun.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tencent.wxcloudrun.dto.WxPersonalBrowsePageDTO;
import com.tencent.wxcloudrun.model.WxUser;
import com.tencent.wxcloudrun.vo.WxUserBrowsingUsersVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tencent.wxcloudrun.dao.WxBrowsingUsersMapper;
import com.tencent.wxcloudrun.model.WxBrowsingUsers;
import com.tencent.wxcloudrun.service.WxBrowsingUsersService;
@Service
public class WxBrowsingUsersServiceImpl extends ServiceImpl<WxBrowsingUsersMapper, WxBrowsingUsers> implements WxBrowsingUsersService{

    @Autowired
    private WxBrowsingUsersMapper wxBrowsingUsersMapper;


    @Override
    public IPage<WxUserBrowsingUsersVo> queryWxUserPage(WxPersonalBrowsePageDTO wxPersonalBrowseDTO) throws Exception {
        Page page = new Page(wxPersonalBrowseDTO.getCurrentPage(), wxPersonalBrowseDTO.getLimit());
        IPage<WxUserBrowsingUsersVo> wxUserIPage = wxBrowsingUsersMapper.queryBrowsingUsersPage(page, wxPersonalBrowseDTO);
        return wxUserIPage;
    }

    @Override
    public int queryCount(WxPersonalBrowsePageDTO wxPersonalBrowseDTO) {
        return wxBrowsingUsersMapper.queryCount(wxPersonalBrowseDTO);
    }

    @Override
    public IPage<WxUserBrowsingUsersVo> queryBebrowsedPage(WxPersonalBrowsePageDTO wxPersonalBrowseDTO) {
        Page page = new Page(wxPersonalBrowseDTO.getCurrentPage(), wxPersonalBrowseDTO.getLimit());
        IPage<WxUserBrowsingUsersVo> wxUserIPage = wxBrowsingUsersMapper.queryBebrowsedPage(page, wxPersonalBrowseDTO);
        return wxUserIPage;
    }
}
