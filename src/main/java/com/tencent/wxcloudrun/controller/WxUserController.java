package com.tencent.wxcloudrun.controller;


import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tencent.wxcloudrun.config.ApiResponse;
import com.tencent.wxcloudrun.dto.*;
import com.tencent.wxcloudrun.model.*;
import com.tencent.wxcloudrun.service.*;
import com.tencent.wxcloudrun.utils.DateUtils;
import com.tencent.wxcloudrun.vo.WxUserPowerVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

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
    private OderPayService oderPayService;

    @Autowired
    private SysConfigService sysConfigService;
    @Autowired
    private SysFileService fileService;


    @Autowired
    private WxMsgTemplateService wxMsgTemplateService;


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
            WxUser wxUser = wxUserService.addOrUpdateWxUser(wxUserDto);
            if (StringUtils.isNotBlank(wxUser.getOpenId())) {
                WxMsgTemplate wxMsgTemplate = wxMsgTemplateService.queryOne(1);
                String data = wxMsgTemplate.getData();
                data = StringUtils.replace(data, "审核事项", "用户"+wxUser.getPhone() + "等待审核");
                data = StringUtils.replace(data, "申请时间(2019-10-20 21:00:00)", DateUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
                data = StringUtils.replace(data, "申请人", wxUser.getNickname());
                data = StringUtils.replace(data, "备注",  "审核时间大约在1到7天内完成");
                wxMsgTemplate.setData(data);
                wxMsgTemplateService.sendWxMsg(wxUser.getOpenId(), wxMsgTemplate);
                data = StringUtils.replace(data, "审核时间大约在1到7天内完成", "请尽快审核");
                wxMsgTemplate.setData(data);
                wxMsgTemplateService.sendWxMsg("o0orR5CXlh4fpa9WDvio5KU94dRo", wxMsgTemplate);
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
                    || StringUtils.isBlank(userOpenInfoDto.getOpenid())) {
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

            Date birthdayDate = DateUtils.parseDate(wxUserOne.getBirthday(), DateUtils.DATE_PATTERN);
            if (!ObjectUtils.isEmpty(birthdayDate)) {
                // 当前日期
                Calendar nowCalendar = Calendar.getInstance();
                // 当前年
                int yearNow = nowCalendar.get(Calendar.YEAR);

                Calendar birthCalendar = Calendar.getInstance();
                birthCalendar.setTime(birthdayDate);
                if (nowCalendar.after(birthCalendar)) {
                    int yearBirth = birthCalendar.get(Calendar.YEAR);
                    int age = yearNow - yearBirth;
                    wxUserOne.setAge(age);
                }
            }
            // 查询登录用户
            WxUser wxUser = wxUserService.queryWxUserOne(userOpenInfoDto.getLoginOpenId());
            if (StringUtils.equals(userOpenInfoDto.getLoginOpenId(), userOpenInfoDto.getOpenid())) {
                wxUserOne.setShowWxNumber(true);
                wxUserOne.setShowbutton(false);
            } else {
                if (!ObjectUtils.isEmpty(wxUser) && StringUtils.equals(wxUser.getApprove(), "0") && wxUser.getMarriageSeekingFlag() == 0) {
                    // 如果审核通过了，查询浏览的用户是否包含该浏览的用户
                    LambdaQueryWrapper<WxBrowsingUsers> usersLambdaQueryWrapper = new LambdaQueryWrapper<>();
                    usersLambdaQueryWrapper.eq(WxBrowsingUsers::getLoginOpenId, userOpenInfoDto.getLoginOpenId());
                    usersLambdaQueryWrapper.eq(WxBrowsingUsers::getBrowsingUsersOpenid, userOpenInfoDto.getOpenid());
                    // 根据登录用户查询浏览的人数
                    List<WxBrowsingUsers> wxBrowsingUsers = wxBrowsingUsersService.list(usersLambdaQueryWrapper);
                    if (CollectionUtils.isNotEmpty(wxBrowsingUsers)) {
                        for (int i = 0; i < wxBrowsingUsers.size(); i++) {
                            if (StringUtils.equals(wxBrowsingUsers.get(i).getBrowsingType(), "2")
                                    || StringUtils.equals(wxBrowsingUsers.get(i).getBrowsingType(), "1")) {
                                wxUserOne.setShowWxNumber(true);
                                wxUserOne.setShowbutton(false);
                            } else {
                                wxUserOne.setShowWxNumber(false);
                                wxUserOne.setShowbutton(true);
                            }
                        }
                    } else {
                        WxBrowsingUsers wxBrowsingUsersSave = WxBrowsingUsers.builder()
                                .browsingUsersOpenid(userOpenInfoDto.getOpenid())
                                .browsingType("0")
                                .loginOpenId(userOpenInfoDto.getLoginOpenId())
                                .createTime(new Date())
                                .build();
                        wxBrowsingUsersService.save(wxBrowsingUsersSave);
                    }

                    wxUserOne.setLoginApprove(true);
                    if (StringUtils.equals(wxUser.getAuthentication(), "1")) {
                        wxUserOne.setLoginAuthentication(true);
                    } else {
                        wxUserOne.setLoginAuthentication(false);
                    }
                } else {
                    wxUserOne.setShowWxNumber(false);
                    wxUserOne.setShowbutton(false);
                    wxUserOne.setLoginApprove(false);
                    wxUserOne.setLoginAuthentication(false);
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
        log.info("queryWxUserPage userOpenInfoDto--->{}", JSON.toJSONString(userOpenInfoDto));
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
            if (!ObjectUtils.isEmpty(wxUserOne)) {
                if (StringUtils.isNotBlank(wxUserOne.getPhone()) && StringUtils.isBlank(wxUserOne.getHeight())) {
                    wxUserOne.setHeight("-");
                }
                if (StringUtils.isNotBlank(wxUserOne.getPhone()) && StringUtils.isBlank(wxUserOne.getWeight())) {
                    wxUserOne.setWeight("-");
                }
            }
            Date birthdayDate = DateUtils.parseDate(wxUserOne.getBirthday(), DateUtils.DATE_PATTERN);
            if (!ObjectUtils.isEmpty(birthdayDate)) {
                // 当前日期
                Calendar nowCalendar = Calendar.getInstance();
                // 当前年
                int yearNow = nowCalendar.get(Calendar.YEAR);
                Calendar birthCalendar = Calendar.getInstance();
                birthCalendar.setTime(birthdayDate);
                if (nowCalendar.after(birthCalendar)) {

                    int yearBirth = birthCalendar.get(Calendar.YEAR);
                    int age = yearNow - yearBirth;
                    wxUserOne.setAge(age);
                }
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
            return ApiResponse.ok(userOpenInfoDto.getOpenid());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ApiResponse.error("数据获取失败");
    }


    /**
     * 查询六条男用户
     * @return
     */
    @GetMapping(value = "/api/queryBoarduserWithMan")
    public ApiResponse queryBoarduserWithMan() {
       List<WxUser> wxUsers  = wxUserService.queryBoarduserWithMan();
       return ApiResponse.ok(wxUsers);
    }


    /**
     * 查询六条男用户
     * @return
     */
    @GetMapping(value = "/api/queryBoarduserWithWoman")
    public ApiResponse queryBoarduserWithWoman() {
        List<WxUser> wxUsers  = wxUserService.queryBoarduserWithWoman();
        return ApiResponse.ok(wxUsers);
    }


    @PostMapping(value = "/api/checkOpneId")
    public ApiResponse checkOpenId(@RequestBody OpenIdDTO openIdDTO) {
        log.info("openIdDTO --->{}", openIdDTO);

        WxUser wxUser  = null;
        Map<String, Object> map = new HashMap<>();
        try {
            wxUser = wxUserService.queryWxUserOne(openIdDTO.getOpenId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!ObjectUtils.isEmpty(wxUser)) {
            map.put("openid", openIdDTO.getOpenId());
            map.put("flag", true);
        } else {
            map.put("openid", openIdDTO.getOpenId());
            map.put("flag", false);
        }
        return ApiResponse.ok(map);
    }

}
