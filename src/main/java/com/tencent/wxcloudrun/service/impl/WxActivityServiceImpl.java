package com.tencent.wxcloudrun.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tencent.wxcloudrun.dto.WxActivityDTO;
import com.tencent.wxcloudrun.service.PayService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tencent.wxcloudrun.dao.WxActivityMapper;
import com.tencent.wxcloudrun.model.WxActivity;
import com.tencent.wxcloudrun.service.WxActivityService;
import org.springframework.util.ObjectUtils;


@Service
public class WxActivityServiceImpl extends ServiceImpl<WxActivityMapper, WxActivity> implements WxActivityService{

    @Autowired
    private PayService payService;



    @Override
    public Map<String, Object> activityOrder(WxActivityDTO wxActivityDTO) throws Exception {

        if (ObjectUtils.isEmpty(wxActivityDTO.getOpenId())
                || ObjectUtils.isEmpty(wxActivityDTO.getActivityUuid())) {
            return null;
        }
        wxActivityDTO.setCreateTime(new Date());
        WxActivity wxActivity = new WxActivity();
        BeanUtils.copyProperties(wxActivityDTO, wxActivity);
        this.save(wxActivity);
        Map<String, Object> map = payService.unifiedOrder(wxActivity.getOpenId(), 2,
                wxActivityDTO.getRequestIp(),
                wxActivity.getActivityUuid());
        return map;
    }



    @Override
    public WxActivity queryWxUserOne(String openId, String activityUuid) {
        LambdaQueryWrapper<WxActivity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WxActivity::getOpenId, openId);
        queryWrapper.eq(WxActivity::getActivityUuid, activityUuid);
        WxActivity wxActivity = baseMapper.selectOne(queryWrapper);
        return wxActivity;
    }

    @Override
    public int queryPhoneCount(String phone, String activityUuid) {
        LambdaQueryWrapper<WxActivity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WxActivity::getPhone, phone);
        queryWrapper.eq(WxActivity::getActivityUuid, activityUuid);
        int count = baseMapper.selectCount(queryWrapper);
        return count;
    }
}
