package com.tencent.wxcloudrun.service.impl;

import com.tencent.wxcloudrun.dto.WxActivityDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tencent.wxcloudrun.dao.WxActivityMapper;
import com.tencent.wxcloudrun.model.WxActivity;
import com.tencent.wxcloudrun.service.WxActivityService;


@Service
public class WxActivityServiceImpl extends ServiceImpl<WxActivityMapper, WxActivity> implements WxActivityService{

    @Override
    public Map<String, Object> activityOrder(WxActivityDTO wxActivityDTO) {
        wxActivityDTO.setCreateTime(new Date());
        WxActivity wxActivity = new WxActivity();
        BeanUtils.copyProperties(wxActivityDTO, wxActivity);
        this.save(wxActivity);

        return null;
    }
}
