package com.tencent.wxcloudrun.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tencent.wxcloudrun.dao.BlogContentMapper;
import com.tencent.wxcloudrun.dto.CategoriesRequest;
import com.tencent.wxcloudrun.dto.ContentRequest;
import com.tencent.wxcloudrun.dto.FileRequestDto;
import com.tencent.wxcloudrun.dto.FileResponseDto;
import com.tencent.wxcloudrun.model.BlogContent;
import com.tencent.wxcloudrun.model.SysFile;
import com.tencent.wxcloudrun.service.AttachmentService;
import com.tencent.wxcloudrun.service.BlogContentService;
import com.tencent.wxcloudrun.service.SysFileService;
import com.tencent.wxcloudrun.utils.DateUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class BlogContentServiceImpl implements BlogContentService {

    final BlogContentMapper blogContentMapper;

    @Autowired
    AttachmentService attachmentService;
    @Autowired
    private SysFileService fileService;

    public BlogContentServiceImpl(BlogContentMapper blogContentMapper) {
        this.blogContentMapper = blogContentMapper;
    }

    @Override
    public IPage<BlogContent> BlogContentByCategoriesPage(CategoriesRequest categoriesRequest) throws Exception {
        Page page = new Page(categoriesRequest.getCurrentPage(), categoriesRequest.getLimit());
        IPage<BlogContent> blogContentIPage = blogContentMapper.queryBlogContentPage(page, categoriesRequest.getCategories());
        List<BlogContent> blogContentIPageRecords = blogContentIPage.getRecords();
        for (BlogContent blogContent: blogContentIPageRecords) {
            List<SysFile> fileList = fileService.queryFile(blogContent.getUuid(),1);
            List<String> fileUrlList = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(fileList)) {
                for (SysFile file: fileList) {
                     updateFileUrl(file);
                     fileUrlList.add(file.getUrl());
                }
                blogContent.setFileList(fileUrlList);
            }

            List<SysFile> moneyQRCodeList = fileService.queryFile(blogContent.getUuid(),2);
            if (CollectionUtils.isNotEmpty(moneyQRCodeList)) {
                SysFile moneyQRCode = moneyQRCodeList.get(0);
                moneyQRCode = updateFileUrl(moneyQRCode);
                blogContent.setMoneyQRCode(moneyQRCode.getUrl());
            }
        }
        return blogContentIPage;
    }


    @Override
    public BlogContent queryBlogContentInfo(ContentRequest contentRequest) {
        LambdaQueryWrapper<BlogContent> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BlogContent::getUuid, contentRequest.getUuid());
        return blogContentMapper.selectOne(queryWrapper);
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
                fileService.updateFile(file);
            }
        }
        return file;
    }


    @Override
    public BlogContent queryBlogContentInfo(String uuid) {
        LambdaQueryWrapper<BlogContent> lambdaQueryWrapper= new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(BlogContent::getUuid, uuid);
        return blogContentMapper.selectOne(lambdaQueryWrapper);
    }
}
