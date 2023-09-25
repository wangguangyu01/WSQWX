package com.tencent.wxcloudrun.controller;

import com.tencent.wxcloudrun.config.ApiResponse;
import com.tencent.wxcloudrun.model.SysFile;
import com.tencent.wxcloudrun.model.WxBanner;
import com.tencent.wxcloudrun.service.SysFileService;
import com.tencent.wxcloudrun.service.WxBannerService;
import com.tencent.wxcloudrun.vo.WxBannerVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
public class WxBannerController {


    @Resource
    private WxBannerService wxBannerService;

    @Resource
    private SysFileService fileService;


    @GetMapping("/api/getWxBaanerList")
    public ApiResponse getWxBaanerList() {
        try {
            List<WxBanner> wxBannerList = wxBannerService.list();
            List<WxBannerVo> wxBannerVos = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(wxBannerList)) {
                for (WxBanner wxBanner: wxBannerList) {
                    if (StringUtils.isBlank(wxBanner.getBannerContentId())) {
                        continue;
                    }
                    List<SysFile> sysFiles = fileService.queryFile(wxBanner.getBannerContentId(), 1);
                    if (CollectionUtils.isEmpty(sysFiles)) {
                        continue;
                    }
                    WxBannerVo wxBannerVo = WxBannerVo.builder()
                            .bannerContentId(wxBanner.getBannerContentId())
                            .bannerImagPath(sysFiles.get(0).getUrl())
                            .bannerNanme(wxBanner.getBannerNanme())
                            .bannerOder(wxBanner.getBannerOder())
                            .bannerType(wxBanner.getBannerType())
                            .build();
                    wxBannerVos.add(wxBannerVo);
                }
            }
            return ApiResponse.ok(wxBannerVos);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ApiResponse.error("获取数据失败");
    }


}
