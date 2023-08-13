package com.tencent.wxcloudrun.controller;


import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tencent.wxcloudrun.config.ApiResponse;
import com.tencent.wxcloudrun.dto.WxActivityDTO;
import com.tencent.wxcloudrun.dto.WxPersonalBrowseDTO;
import com.tencent.wxcloudrun.model.OderPay;
import com.tencent.wxcloudrun.model.WxActivity;
import com.tencent.wxcloudrun.model.WxPersonalBrowse;
import com.tencent.wxcloudrun.service.OderPayService;
import com.tencent.wxcloudrun.service.PayService;
import com.tencent.wxcloudrun.service.WxPersonalBrowseService;
import com.tencent.wxcloudrun.utils.IPUtil;
import com.tencent.wxcloudrun.utils.MD5Utils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

@RestController
@Slf4j
public class WxPersonalBrowseController {

    @Autowired
    private WxPersonalBrowseService wxPersonalBrowseService;

    @Autowired
    private PayService payService;

    @Autowired
    private OderPayService oderPayService;


    @PostMapping(value = "/api/paypPersonalBrowse")
    public ApiResponse paypPersonalBrowse(@RequestBody WxPersonalBrowseDTO wxPersonalBrowseDTO) {
        try {
            if (ObjectUtils.isEmpty(wxPersonalBrowseDTO)
                    || StringUtils.isBlank(wxPersonalBrowseDTO.getLoginOpenId())
                    || StringUtils.isBlank(wxPersonalBrowseDTO.getBrowsingOpenid()))  {
                return ApiResponse.error("缺少报名的条件");
            }
            log.info("paypPersonalBrowse wxPersonalBrowseDTO--->{}", JSON.toJSONString(wxPersonalBrowseDTO));

            // 查询单独支付浏览费用的订单，如果不为空，同时支付失败的情况下删除记录
            LambdaQueryWrapper<WxPersonalBrowse> browseLambdaQueryWrapper = new LambdaQueryWrapper<>();
            browseLambdaQueryWrapper.eq(WxPersonalBrowse::getLoginOpenId, wxPersonalBrowseDTO.getLoginOpenId());
            browseLambdaQueryWrapper.eq(WxPersonalBrowse::getBrowsingOpenid, wxPersonalBrowseDTO.getBrowsingOpenid());
            WxPersonalBrowse wxPersonalBrowse = wxPersonalBrowseService.getOne(browseLambdaQueryWrapper);
            if (!ObjectUtils.isEmpty(wxPersonalBrowse)) {
                if (StringUtils.isNotBlank(wxPersonalBrowse.getTradeNo())) {
                    OderPay oderPay  = payService.queryOderPay(wxPersonalBrowse.getTradeNo());
                    if (ObjectUtils.isEmpty(oderPay)) {
                        wxPersonalBrowseService.removeById(wxPersonalBrowse.getId());
                    } else {
                        if (oderPay.getPaySuccess() != 2) {
                            wxPersonalBrowseService.removeById(wxPersonalBrowse.getId());
                            oderPayService.removeById(oderPay.getId());
                        }
                    }
                }
            }
            // 设置i感情ip
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            IPUtil ipUtil = new IPUtil();
            String ip  = ipUtil.getIP(request);
            wxPersonalBrowseDTO.setRequestIp(ip);
            Map<String,Object> map = wxPersonalBrowseService.paypPersonalBrowse(wxPersonalBrowseDTO);
            map.put("browsingOpenid", wxPersonalBrowseDTO.getBrowsingOpenid());
            return ApiResponse.ok(map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ApiResponse.error("添加失败");
    }
}
