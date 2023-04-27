package com.tencent.wxcloudrun.controller;


import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tencent.wxcloudrun.config.ApiResponse;
import com.tencent.wxcloudrun.dto.UserOpenInfoDto;
import com.tencent.wxcloudrun.dto.WxUserCodeDto;
import com.tencent.wxcloudrun.dto.WxUserDto;
import com.tencent.wxcloudrun.dto.WxUserPageParamDto;
import com.tencent.wxcloudrun.model.WxUser;
import com.tencent.wxcloudrun.service.TSerialNumberService;
import com.tencent.wxcloudrun.service.WxUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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

    @Autowired
    private TSerialNumberService tSerialNumberService;



    @PostMapping(value = "/api/checkWxUser")
    public ApiResponse checkWxUser(@RequestBody WxUserCodeDto code) {
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
            WxUser wxUserObj = wxUserService.queryWxUserOne(wxUser.getOpenId());
            if (ObjectUtils.isEmpty(wxUserObj)) {
                String serialNumber = tSerialNumberService.createSerialNumber();
                wxUser.setSerialNumber(serialNumber);
                wxUserService.addWxUser(wxUser);
            } else {
                wxUserService.updateWxUser(wxUser);
            }
            return ApiResponse.ok(wxUser);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ApiResponse.error("添加失败");
    }


    /**
     * 注册人员信息
     * @param wxUserPageParamDto
     * @return
     */
    @PostMapping(value = "/api/queryWxUserPage")
    public ApiResponse queryWxUserPage(@RequestBody WxUserPageParamDto wxUserPageParamDto) {
        try {

            log.info("queryWxUserPage wxUserDto--->{}", JSON.toJSONString(wxUserPageParamDto));
            IPage<WxUser> wxUserIPage = wxUserService.queryWxUserPage(wxUserPageParamDto);
            return ApiResponse.ok(wxUserIPage);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ApiResponse.error("数据获取失败");
    }



    /**
     * 注册人员信息
     * @param userOpenInfoDto
     * @return
     */
    @PostMapping(value = "/api/queryWxUserInfo")
    public ApiResponse queryWxUserInfo(@RequestBody UserOpenInfoDto userOpenInfoDto) {
        try {

            log.info("queryWxUserPage wxUserDto--->{}", JSON.toJSONString(userOpenInfoDto));
            if (ObjectUtils.isEmpty(userOpenInfoDto)) {
                return ApiResponse.error("缺少参数");
            }
            WxUser wxUserOne = wxUserService.queryWxUserOne(userOpenInfoDto.getOpenid());
            if (StringUtils.isBlank(wxUserOne.getHeight())) {
                wxUserOne.setHeight("/");
            }
            if (StringUtils.isBlank(wxUserOne.getWeight())) {
                wxUserOne.setWeight("/");
            }
            return ApiResponse.ok(wxUserOne);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ApiResponse.error("数据获取失败");
    }

}
