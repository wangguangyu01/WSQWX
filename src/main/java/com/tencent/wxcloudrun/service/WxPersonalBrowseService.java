package com.tencent.wxcloudrun.service;

import com.tencent.wxcloudrun.dto.WxPersonalBrowseDTO;
import com.tencent.wxcloudrun.model.WxPersonalBrowse;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

public interface WxPersonalBrowseService extends IService<WxPersonalBrowse> {


    public Map<String, Object> paypPersonalBrowse(WxPersonalBrowseDTO wxPersonalBrowseDTO) throws Exception;

}

