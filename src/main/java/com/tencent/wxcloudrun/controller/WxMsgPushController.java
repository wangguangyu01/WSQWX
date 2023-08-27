package com.tencent.wxcloudrun.controller;


import com.tencent.wxcloudrun.config.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
public class WxMsgPushController {


    @PostMapping("/api/getPushMsgInfo")
    public ApiResponse getPushMsgInfo(@RequestBody Map<String,Object> map){
        log.info("getPushMsgInfo map --->{}", map);

        return ApiResponse.ok();
    }
}
