package com.tencent.wxcloudrun.controller;

import com.alibaba.fastjson.JSON;
import com.tencent.wxcloudrun.config.ApiResponse;
import com.tencent.wxcloudrun.dao.WxActivityMapper;
import com.tencent.wxcloudrun.dto.WxActivityDTO;
import com.tencent.wxcloudrun.dto.WxUserDto;
import com.tencent.wxcloudrun.model.OderPay;
import com.tencent.wxcloudrun.model.WxActivity;
import com.tencent.wxcloudrun.model.WxUser;
import com.tencent.wxcloudrun.service.PayService;
import com.tencent.wxcloudrun.service.WxActivityService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
            log.info("addWxUser wxUserDto--->{}", JSON.toJSONString(wxActivityDTO));
            WxActivity wxActivity = wxActivityService.queryWxUserOne(wxActivityDTO.getOpenId(),
                    wxActivityDTO.getActivityUuid());
            OderPay oderPay = payService.queryOderPay(wxActivity.getTradeNo());
            if (ObjectUtils.isEmpty(wxActivity) ||  oderPay.getPaySuccess() != 2 ) {
                wxActivityService.activityOrder(wxActivityDTO);
            } else if (!ObjectUtils.isEmpty(wxActivity) &&  oderPay.getPaySuccess() == 2 ) {
                ApiResponse.ok("您已经成功报名");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ApiResponse.error("添加失败");
    }

}
