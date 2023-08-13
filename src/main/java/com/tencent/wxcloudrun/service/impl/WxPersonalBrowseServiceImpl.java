package com.tencent.wxcloudrun.service.impl;

import com.tencent.wxcloudrun.dto.WxActivityDTO;
import com.tencent.wxcloudrun.dto.WxPersonalBrowseDTO;
import com.tencent.wxcloudrun.model.WxActivity;
import com.tencent.wxcloudrun.service.PayService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tencent.wxcloudrun.model.WxPersonalBrowse;
import com.tencent.wxcloudrun.dao.WxPersonalBrowseMapper;
import com.tencent.wxcloudrun.service.WxPersonalBrowseService;
import org.springframework.util.ObjectUtils;

@Service
public class WxPersonalBrowseServiceImpl extends ServiceImpl<WxPersonalBrowseMapper, WxPersonalBrowse> implements WxPersonalBrowseService {


    @Autowired
    private PayService payService;

    @Override
    public Map<String, Object> paypPersonalBrowse(WxPersonalBrowseDTO wxPersonalBrowseDTO) throws Exception {

        if (ObjectUtils.isEmpty(wxPersonalBrowseDTO.getLoginOpenId())
                || ObjectUtils.isEmpty(wxPersonalBrowseDTO.getBrowsingOpenid())) {
            return null;
        }
        wxPersonalBrowseDTO.setCreateTime(new Date());
        WxPersonalBrowse wxPersonalBrowse = new WxPersonalBrowse();
        BeanUtils.copyProperties(wxPersonalBrowseDTO, wxPersonalBrowse);
        this.save(wxPersonalBrowse);
        Map<String, Object> map = payService.unifiedOrder(wxPersonalBrowseDTO.getLoginOpenId(), 3,
                wxPersonalBrowseDTO.getRequestIp(),
                wxPersonalBrowseDTO.getBrowsingOpenid());
        return map;
    }

}

