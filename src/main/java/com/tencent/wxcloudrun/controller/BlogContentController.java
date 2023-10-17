package com.tencent.wxcloudrun.controller;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tencent.wxcloudrun.config.ApiResponse;
import com.tencent.wxcloudrun.dto.*;
import com.tencent.wxcloudrun.model.BlogContent;
import com.tencent.wxcloudrun.model.Counter;
import com.tencent.wxcloudrun.model.SysFile;
import com.tencent.wxcloudrun.model.WxUser;
import com.tencent.wxcloudrun.service.*;
import com.tencent.wxcloudrun.utils.DateUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * 内容展示
 */
@RestController
public class BlogContentController {
    final Logger logger;

    final BlogContentService blogContentService;

    final SysFileService sysFileService;

    final AttachmentService attachmentService;

    final WxUserService wxUserService;



    public BlogContentController(@Autowired BlogContentService blogContentService,
                                 @Autowired SysFileService sysFileService,
                                 @Autowired AttachmentService attachmentService,
                                 @Autowired WxUserService wxUserService) {
        this.blogContentService = blogContentService;
        this.sysFileService = sysFileService;
        this.attachmentService = attachmentService;
        this.wxUserService = wxUserService;
        this.logger = LoggerFactory.getLogger(CounterController.class);
    }



    /**
     * 根据类型获取内容信息
     * @return API response json
     */
    @PostMapping(value = "/api/blogContent")
    ApiResponse get(@RequestBody CategoriesRequest categoriesRequest) {
        IPage<BlogContent> blogContentIPage = null;
        try {
            logger.info("/api/blogContent get request{}", JSONObject.toJSONString(categoriesRequest));
            blogContentIPage = blogContentService.BlogContentByCategoriesPage(categoriesRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ApiResponse.ok(blogContentIPage);
    }



    /**
     * 根据类型获取内容信息
     * @return API response json
     */
    @PostMapping(value = "/api/blogContentInfo")
    ApiResponse get(@RequestBody ContentRequest contentRequest) throws Exception {
        logger.info("/api/blogContentInfo get request{}", contentRequest);
        BlogContent blogContent = blogContentService.queryBlogContentInfo(contentRequest);
        List<SysFile> fileList = sysFileService.queryFile(blogContent.getUuid(), 1);
        List<String> fileUrlList = new ArrayList<>();
        for (SysFile file: fileList) {
            SysFile sysFile = updateFileUrl(file);
            fileUrlList.add(sysFile.getUrl());
        }
        blogContent.setFileList(fileUrlList);
        List<SysFile> moneyQRCodeFileList = sysFileService.queryFile(blogContent.getUuid(), 2);
        if (CollectionUtils.isNotEmpty(moneyQRCodeFileList)) {
            SysFile moneyQRCodeFile = moneyQRCodeFileList.get(0);
            SysFile moneyQRCodeFileNew = updateFileUrl(moneyQRCodeFile);
            blogContent.setMoneyQRCode(moneyQRCodeFileNew.getUrl());
        } else {
            blogContent.setMoneyQRCode("");
        }
        if (Objects.nonNull(blogContent) && Objects.nonNull(blogContent.getPrice())) {
            boolean flag = false;
            if (StringUtils.isNotBlank(contentRequest.getOpenId())) {
                WxUser wxUser = wxUserService.queryWxUserOne(contentRequest.getOpenId());
                if (Objects.nonNull(wxUser) && StringUtils.equals(wxUser.getAuthentication(), "1")
                        && Objects.nonNull(blogContent.getProPay())) {
                    // 用于会员 金额的显示
                    BigDecimal proPaydecimal = NumberUtils.createBigDecimal(blogContent.getProPay() + "");
                    BigDecimal  proPayDecimal = proPaydecimal.divide(new BigDecimal(100), 2, RoundingMode.HALF_UP );
                    blogContent.setPriceDouble(NumberUtils.toDouble(String.valueOf(proPayDecimal), 0d));
                    flag = true;
                }
            }
            if (!flag) {
                // 用于金额的显示
                BigDecimal decimal = NumberUtils.createBigDecimal(blogContent.getPrice() + "");
                BigDecimal  bigDecimal = decimal.divide(new BigDecimal(100), 2, RoundingMode.HALF_UP );
                blogContent.setPriceDouble(NumberUtils.toDouble(String.valueOf(bigDecimal), 0d));
            }

        }

        return ApiResponse.ok(blogContent);
    }


    private SysFile updateFileUrl(SysFile file) throws Exception {
        Date requestTime = file.getRequestTime();
        Date expireTime  = DateUtils.calculateDate(requestTime, Calendar.SECOND, 7200);
        if (DateUtils.dateCompareTo(new Date(), expireTime) == 0 || DateUtils.dateCompareTo(new Date(), expireTime) > 0) {
            List<FileRequestDto> list = new ArrayList<>();
            FileRequestDto fileRequestDto = new FileRequestDto(file.getFileId());
            list.add(fileRequestDto);
            List<FileResponseDto> responseDtos = attachmentService.batchDownloadFile(list);
            Date date = new Date();
            for (FileResponseDto fileResponseDto : responseDtos) {
                expireTime  = DateUtils.calculateDate(date, Calendar.SECOND, fileResponseDto.getMax_age());
                file.setUrl(fileResponseDto.getDownload_url());
                file.setRequestTime(date);
                file.setExpireTime(expireTime);
                sysFileService.updateFile(file);
            }
        }
        return file;
    }


}
