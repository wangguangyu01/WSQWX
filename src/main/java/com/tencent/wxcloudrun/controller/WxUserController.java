package com.tencent.wxcloudrun.controller;


import com.alibaba.fastjson.JSON;
import com.tencent.wxcloudrun.config.ApiResponse;
import com.tencent.wxcloudrun.dto.WxUserCodeDto;
import com.tencent.wxcloudrun.dto.WxUserDto;
import com.tencent.wxcloudrun.model.WxUser;
import com.tencent.wxcloudrun.service.WxUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 微信用户接口
 */
@RestController
@Slf4j
public class WxUserController {


    @Autowired
    private WxUserService wxUserService;




    @PostMapping(value = "/api/checkWxUser")
    public ApiResponse queryWxUserInfo(@RequestBody WxUserCodeDto code) {
        Map<String, Object> map = wxUserService.queryWxUserInfo(code.getCode());
        return ApiResponse.ok(map);
    }



    @PostMapping(value = "/api/addWxUser")
    public ApiResponse addWxUser(@RequestBody WxUserDto wxUserDto) {
        try {
            if (ObjectUtils.isEmpty(wxUserDto))  {
                return ApiResponse.error("缺少注册用户信息");
            }
            log.info("addWxUser wxUserDto--->{}", JSON.toJSONString(wxUserDto));
            WxUser wxUser = new WxUser();
            BeanUtils.copyProperties(wxUserDto, wxUser);
            int count = wxUserService.addWxUser(wxUser);
            if (count > 0) {
                return ApiResponse.ok(wxUser);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ApiResponse.error("添加失败");
    }


}
