package com.tencent.wxcloudrun.controller;


import com.tencent.wxcloudrun.config.ApiResponse;
import com.tencent.wxcloudrun.dto.WxUserCodeDto;
import com.tencent.wxcloudrun.service.WxUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 微信用户接口
 */
@RestController
public class WxUserController {


    @Autowired
    private WxUserService wxUserService;

    @PostMapping(value = "/api/checkWxUser")
    public ApiResponse queryWxUserInfo(@RequestBody WxUserCodeDto code) {
        Map<String, Object> map = wxUserService.queryWxUserInfo(code.getCode());
        return ApiResponse.ok(map);
    }


}
