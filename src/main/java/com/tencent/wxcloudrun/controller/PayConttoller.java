package com.tencent.wxcloudrun.controller;


import com.tencent.wxcloudrun.config.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@Slf4j
public class PayConttoller  {


    @GetMapping("/placeOrder")
    public ApiResponse placeOrder(HttpServletRequest request, HttpServletResponse response) {
        log.info("placeOrder response---> {}", response);
        return ApiResponse.ok();
    }

}
