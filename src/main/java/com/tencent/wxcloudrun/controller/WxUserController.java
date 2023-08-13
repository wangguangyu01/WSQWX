package com.tencent.wxcloudrun.controller;


import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tencent.wxcloudrun.config.ApiResponse;
import com.tencent.wxcloudrun.dto.UserOpenInfoDto;
import com.tencent.wxcloudrun.dto.WxUserCodeDto;
import com.tencent.wxcloudrun.dto.WxUserDto;
import com.tencent.wxcloudrun.dto.WxUserPageParamDto;
import com.tencent.wxcloudrun.model.*;
import com.tencent.wxcloudrun.service.*;
import com.tencent.wxcloudrun.vo.WxUserPowerVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
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

    @Autowired
    private WxBrowsingUsersService wxBrowsingUsersService;


    @Autowired
    private WxPersonalBrowseService wxPersonalBrowseService;

    @Autowired
    private OderPayService oderPayService;

    @Autowired
    private SysConfigService sysConfigService;


    @PostMapping(value = "/api/checkWxUser")
    public ApiResponse checkWxUser(@RequestBody WxUserCodeDto code) {
        Map<String, Object> map = wxUserService.queryWxUserInfo(code.getCode());
        return ApiResponse.ok(map);
    }


    @PostMapping(value = "/api/addWxUser")
    public ApiResponse addWxUser(@RequestBody WxUserDto wxUserDto) {
        try {
            if (ObjectUtils.isEmpty(wxUserDto)) {
                return ApiResponse.error("缺少注册用户信息");
            }
            log.info("addWxUser wxUserDto--->{}", JSON.toJSONString(wxUserDto));
            WxUser wxUser = new WxUser();
            BeanUtils.copyProperties(wxUserDto, wxUser);
            wxUser.setApprove("0");
            WxUser wxUserObj = wxUserService.queryWxUserOne(wxUser.getOpenId());
            int count = wxUserService.queryPhoneCount(wxUserDto.getPhone());
            if (ObjectUtils.isEmpty(wxUserObj) && count == 0) {
                String serialNumber = tSerialNumberService.createSerialNumber();
                wxUser.setSerialNumber(serialNumber);
                wxUserService.addWxUser(wxUser);
            } else if (ObjectUtils.isEmpty(wxUserObj) && count > 0) {
                wxUserService.updateByPhone(wxUserDto.getOpenId(), wxUserDto.getPhone());
                wxUserService.updateWxUser(wxUser);
            } else if (!ObjectUtils.isEmpty(wxUserObj)) {
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
     *
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
     * 会员的信息
     *
     * @param userOpenInfoDto
     * @return
     */
    @PostMapping(value = "/api/queryWxUserInfo")
    public ApiResponse queryWxUserInfo(@RequestBody UserOpenInfoDto userOpenInfoDto) {
        try {

            log.info("queryWxUserPage wxUserDto--->{}", JSON.toJSONString(userOpenInfoDto));
            if (ObjectUtils.isEmpty(userOpenInfoDto)
                    || StringUtils.isBlank(userOpenInfoDto.getOpenid())
                    || StringUtils.isBlank(userOpenInfoDto.getLoginOpenId())) {
                return ApiResponse.error("缺少参数");
            }
            WxUser wxUserOne = null;
            int openIdCount = 0;
            if (StringUtils.isNotBlank(userOpenInfoDto.getOpenid())) {
                openIdCount = wxUserService.queryCount(userOpenInfoDto.getOpenid());
                if (openIdCount != 0) {
                    wxUserOne = wxUserService.queryWxUserOne(userOpenInfoDto.getOpenid());
                }
            }

            if (openIdCount == 0 && StringUtils.isNotBlank(userOpenInfoDto.getPhone())) {
                wxUserOne = wxUserService.queryWxUserOneByPhone(userOpenInfoDto.getPhone());
            }

            if (StringUtils.isNotBlank(wxUserOne.getPhone()) && StringUtils.isBlank(wxUserOne.getHeight())) {
                wxUserOne.setHeight("-");
            }
            if (StringUtils.isNotBlank(wxUserOne.getPhone()) && StringUtils.isBlank(wxUserOne.getWeight())) {
                wxUserOne.setWeight("-");
            }
            // 查询登录用户
            WxUser wxUser = wxUserService.queryWxUserOne(userOpenInfoDto.getLoginOpenId());
            if (StringUtils.equals(userOpenInfoDto.getLoginOpenId(), userOpenInfoDto.getOpenid())) {
                wxUserOne.setShowWxNumber(true);
                wxUserOne.setShowbutton(false);
            } else {
                if (!ObjectUtils.isEmpty(wxUser) && StringUtils.equals(wxUser.getApprove(), "1")) {
                    // 如果审核通过了，查询浏览的用户是否包含该浏览的用户
                    LambdaQueryWrapper<WxBrowsingUsers> usersLambdaQueryWrapper = new LambdaQueryWrapper<>();
                    usersLambdaQueryWrapper.eq(WxBrowsingUsers::getLoginOpenId, userOpenInfoDto.getLoginOpenId());
                    // 根据登录用户查询浏览的人数
                    List<WxBrowsingUsers> wxBrowsingUsers = wxBrowsingUsersService.list(usersLambdaQueryWrapper);
                    int updateCount = 0;
                    if (CollectionUtils.isNotEmpty(wxBrowsingUsers)) {
                        for (int i = 0; i < wxBrowsingUsers.size(); i++) {
                            if (!StringUtils.equals(wxBrowsingUsers.get(i).getBrowsingUsersOpenid(),
                                    userOpenInfoDto.getOpenid())) {
                                updateCount++;
                                continue;
                            } else {
                                usersLambdaQueryWrapper.eq(WxBrowsingUsers::getBrowsingUsersOpenid, userOpenInfoDto.getOpenid());
                                WxBrowsingUsers wxBrowsingUsersOneObj = wxBrowsingUsersService.getOne(usersLambdaQueryWrapper);
                                if (!ObjectUtils.isEmpty(wxBrowsingUsersOneObj)) {
                                    if (StringUtils.equals(wxBrowsingUsersOneObj.getBrowsingType(), "2")
                                            || StringUtils.equals(wxBrowsingUsersOneObj.getBrowsingType(), "1")) {
                                        wxUserOne.setShowWxNumber(true);
                                        wxUserOne.setShowbutton(false);
                                    } else {
                                        wxUserOne.setShowWxNumber(false);
                                        wxUserOne.setShowbutton(true);
                                    }
                                    break;
                                }
                            }
                        }
                    }
                    // 如果没有浏览记录才会添加新的记录
                    if (updateCount == CollectionUtils.size(wxBrowsingUsers)) {
                        WxBrowsingUsers wxBrowsingUsersSave = WxBrowsingUsers.builder()
                                .browsingUsersOpenid(userOpenInfoDto.getOpenid())
                                .browsingType("0")
                                .loginOpenId(userOpenInfoDto.getLoginOpenId())
                                .createTime(new Date())
                                .build();
                        wxBrowsingUsersService.save(wxBrowsingUsersSave);
                    }
                } else {
                    wxUserOne.setShowWxNumber(false);
                    wxUserOne.setShowbutton(false);
                }
            }
            return ApiResponse.ok(wxUserOne);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ApiResponse.error("数据获取失败");
    }



    /**
     * 注册人员信息
     *
     * @param userOpenInfoDto
     * @return
     */
    @PostMapping(value = "/api/queryRegisterUserInfo")
    public ApiResponse queryRegisterUserInfo(@RequestBody UserOpenInfoDto userOpenInfoDto)  {
        log.info("queryWxUserPage wxUserDto--->{}", JSON.toJSONString(userOpenInfoDto));
        WxUser wxUserOne = null;
        try {
            int openIdCount = 0;
            if (StringUtils.isNotBlank(userOpenInfoDto.getOpenid())) {
                openIdCount = wxUserService.queryCount(userOpenInfoDto.getOpenid());
                if (openIdCount != 0) {
                    wxUserOne = wxUserService.queryWxUserOne(userOpenInfoDto.getOpenid());
                }
            }

            if (openIdCount == 0 && StringUtils.isNotBlank(userOpenInfoDto.getPhone())) {
                wxUserOne = wxUserService.queryWxUserOneByPhone(userOpenInfoDto.getPhone());
            }

            if (StringUtils.isNotBlank(wxUserOne.getPhone()) && StringUtils.isBlank(wxUserOne.getHeight())) {
                wxUserOne.setHeight("-");
            }
            if (StringUtils.isNotBlank(wxUserOne.getPhone()) && StringUtils.isBlank(wxUserOne.getWeight())) {
                wxUserOne.setWeight("-");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ApiResponse.ok(wxUserOne);
    }


    /**
     * 注册人员信息
     *
     * @param userOpenInfoDto
     * @return
     */
    @PostMapping(value = "/api/showWxNumberClick")
    public ApiResponse showWxNumberClick(@RequestBody UserOpenInfoDto userOpenInfoDto) {
        try {

            log.info("showWxNumberClick userOpenInfoDto--->{}", JSON.toJSONString(userOpenInfoDto));
            LambdaQueryWrapper<OderPay> oderPayLambdaQueryWrapper = new LambdaQueryWrapper<>();
            oderPayLambdaQueryWrapper.eq(OderPay::getOpenId, userOpenInfoDto.getLoginOpenId());
            oderPayLambdaQueryWrapper.eq(OderPay::getPayType, 1);
            List<OderPay> oderPays = oderPayService.list(oderPayLambdaQueryWrapper);
            // 允许查看用户微信号个数
            int total = 10;
            if (CollectionUtils.isNotEmpty(oderPays)) {
                LambdaQueryWrapper<SysConfig> sysConfigLambdaQueryWrapper = new LambdaQueryWrapper<>();
                sysConfigLambdaQueryWrapper.eq(SysConfig::getParamKey, "vip_show_count");
                SysConfig sysConfig = sysConfigService.getOne(sysConfigLambdaQueryWrapper);
                int showMembersCount = NumberUtils.toInt(sysConfig.getParamValue(), 10);
                total = showMembersCount * oderPays.size();

            }
            LambdaQueryWrapper<WxBrowsingUsers> browsingUsersLambdaQueryWrapper = new LambdaQueryWrapper<>();
            browsingUsersLambdaQueryWrapper.eq(WxBrowsingUsers::getLoginOpenId, userOpenInfoDto.getLoginOpenId());
            browsingUsersLambdaQueryWrapper.eq(WxBrowsingUsers::getBrowsingType, "1");
            int countShow  = wxBrowsingUsersService.count(browsingUsersLambdaQueryWrapper);
            if (countShow == total) {
                return ApiResponse.error("您已经到获取用户微信联系方式的上限");
            }
            browsingUsersLambdaQueryWrapper.clear();
            browsingUsersLambdaQueryWrapper.eq(WxBrowsingUsers::getBrowsingUsersOpenid, userOpenInfoDto.getOpenid());
            browsingUsersLambdaQueryWrapper.eq(WxBrowsingUsers::getLoginOpenId, userOpenInfoDto.getLoginOpenId());
            browsingUsersLambdaQueryWrapper.eq(WxBrowsingUsers::getBrowsingType, "0");
            WxBrowsingUsers wxBrowsingUsers = wxBrowsingUsersService.getOne(browsingUsersLambdaQueryWrapper);
            if (!ObjectUtils.isEmpty(wxBrowsingUsers)) {
                wxBrowsingUsers.setBrowsingType("1");
                wxBrowsingUsersService.updateById(wxBrowsingUsers);
            }
            return ApiResponse.ok();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ApiResponse.error("数据获取失败");
    }


}
