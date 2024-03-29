package com.tencent.wxcloudrun.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tencent.wxcloudrun.config.ApiResponse;
import com.tencent.wxcloudrun.dao.WxActivityMapper;
import com.tencent.wxcloudrun.dto.WeiXinParamDTO;
import com.tencent.wxcloudrun.dto.WxActivityDTO;
import com.tencent.wxcloudrun.dto.WxUserDto;
import com.tencent.wxcloudrun.model.OderPay;
import com.tencent.wxcloudrun.model.WxActivity;
import com.tencent.wxcloudrun.model.WxUser;
import com.tencent.wxcloudrun.service.PayService;
import com.tencent.wxcloudrun.service.WxActivityService;
import com.tencent.wxcloudrun.utils.IPUtil;
import com.tencent.wxcloudrun.utils.MD5Utils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@RestController
@Slf4j
public class WxActivityController {

    @Autowired
    private WxActivityService wxActivityService;

    @Autowired
    private PayService payService;




    @PostMapping(value = "/api/addWxActivity")
    public ApiResponse addWxUser(@RequestBody WxActivityDTO wxActivityDTO) {
        try {
            if (ObjectUtils.isEmpty(wxActivityDTO)
                    || StringUtils.isBlank(wxActivityDTO.getOpenId())
                    || StringUtils.isBlank(wxActivityDTO.getActivityUuid())
                    || StringUtils.isBlank(wxActivityDTO.getPhone()))  {
                return ApiResponse.error("缺少报名的条件");
            }

            log.info("addWxActivity wxActivityDTO--->{}", JSON.toJSONString(wxActivityDTO));

            // 设置i感情ip
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            IPUtil ipUtil = new IPUtil();
            String ip  = ipUtil.getIP(request);
            wxActivityDTO.setRequestIp(ip);

            WxActivity wxActivity = wxActivityService.queryWxUserOne(wxActivityDTO.getOpenId(),
                    wxActivityDTO.getActivityUuid());

            Map<String, Object> map = new HashMap<>();
            if (ObjectUtils.isEmpty(wxActivity)) {
                map = wxActivityService.activityOrder(wxActivityDTO);
                map.put("activityUuid", wxActivityDTO.getActivityUuid());
                return ApiResponse.ok(map);
            } else  {
                OderPay oderPay = payService.queryOderPay(wxActivity.getTradeNo());
                if (oderPay.getPaySuccess() == 2) {
                    ApiResponse.error("您已经成功报名");
                } else if (oderPay.getPaySuccess() == 1) {
                    wxActivityService.removeById(wxActivity.getId());
                    payService.deleteOderPay(oderPay.getTradeNo());
                    map = wxActivityService.activityOrder(wxActivityDTO);
                    map.put("activityUuid", wxActivityDTO.getActivityUuid());
                    return ApiResponse.ok(map);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ApiResponse.error("添加失败");
    }


    @PostMapping(value = "/api/queryWxActivity")
    public ApiResponse queryWxActivity(@RequestBody WxActivityDTO wxActivityDTO) {
        if (StringUtils.isBlank(wxActivityDTO.getActivityUuid())) {
            return ApiResponse.ok(new ArrayList<>());
        }
        List<WxActivity> list = wxActivityService.queryWxActivityList(wxActivityDTO.getActivityUuid());
        return ApiResponse.ok(list);
    }

}
