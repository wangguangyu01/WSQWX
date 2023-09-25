package com.tencent.wxcloudrun.controller;


import com.tencent.wxcloudrun.config.ApiResponse;
import com.tencent.wxcloudrun.model.WxMenu;
import com.tencent.wxcloudrun.service.WxMenuService;
import com.tencent.wxcloudrun.vo.WxMenuVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
public class WxMenuController {

    @Resource
    private WxMenuService wxMenuService;


    @GetMapping("/api/getWxMenuList")
    public ApiResponse getWxMenuList() {
        try {

            List<WxMenu> wxMenuList = wxMenuService.list();
            List<WxMenuVo> wxMenuVoList = new ArrayList<>();

            if (CollectionUtils.isNotEmpty(wxMenuList)) {
                wxMenuList = wxMenuList.stream()
                        .sorted(Comparator.comparing(WxMenu::getWxMenuOrder))
                        .collect(Collectors.toList());
                for (WxMenu wxMenu: wxMenuList) {
                    WxMenuVo wxMenuVo = new WxMenuVo();
                    BeanUtils.copyProperties(wxMenu, wxMenuVo);
                    wxMenuVoList.add(wxMenuVo);
                }
            }
            return ApiResponse.ok(wxMenuVoList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ApiResponse.error("获取微信首页菜单失败");
    }
}
