package com.tencent.wxcloudrun.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tencent.wxcloudrun.dao.BlogContentMapper;
import com.tencent.wxcloudrun.dao.SysFileMapper;
import com.tencent.wxcloudrun.dao.WxUserMapper;
import com.tencent.wxcloudrun.dto.FileRequestDto;
import com.tencent.wxcloudrun.dto.FileResponseDto;
import com.tencent.wxcloudrun.dto.UploadUserFileDto;
import com.tencent.wxcloudrun.model.SysFile;
import com.tencent.wxcloudrun.model.WxUser;
import com.tencent.wxcloudrun.service.AttachmentService;
import com.tencent.wxcloudrun.service.SysFileService;
import com.tencent.wxcloudrun.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


@Service
@Slf4j
public class SysFileServiceImpl implements SysFileService {


    final SysFileMapper sysFileMapper;

    @Autowired
    private  AttachmentService attachmentService;

    @Autowired
    private WxUserMapper  wxUserMapper;

    public SysFileServiceImpl(SysFileMapper sysFileMapper) {
        this.sysFileMapper = sysFileMapper;
    }


    @Override
    public int saveFile(SysFile sysFile) {
        return sysFileMapper.insert(sysFile);
    }


    @Override
    public List<SysFile> queryFile(String contentId, Integer type) {
        LambdaQueryWrapper<SysFile> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysFile::getContentId, contentId);
        queryWrapper.eq(SysFile::getType, type);
        queryWrapper.orderByDesc(SysFile::getCreateDate);
        return sysFileMapper.selectList(queryWrapper);
    }


    @Override
    public int updateFile(SysFile sysFile) {
        return sysFileMapper.updateById(sysFile);
    }


    @Override
    public SysFile queryFileOneByFileId(String fileId) {
        LambdaQueryWrapper<SysFile> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysFile::getFileId, fileId);
        return sysFileMapper.selectOne(queryWrapper);
    }


    @Override
    public int remove(UploadUserFileDto uploadUserFileDto) {
        List<String> fileid_list = new ArrayList<>();
        fileid_list.add(uploadUserFileDto.getFileid());
        List<String> list = attachmentService.batchDeleteFile(fileid_list);
        LambdaQueryWrapper<SysFile> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.in(SysFile::getFileId, list);
        List<SysFile> sysFiles = sysFileMapper.selectList(queryWrapper);
        List<Long> idList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(sysFiles)) {
              for (SysFile file: sysFiles) {
                  idList.add(file.getId());
              }
        }
        if (uploadUserFileDto.getType() == 11) {
            WxUser wxUser = wxUserMapper.selectById(uploadUserFileDto.getOpenId());
            if (!ObjectUtils.isEmpty(wxUser)) {
                wxUser.setHeadimgurl("");
                wxUserMapper.updateById(wxUser);
            }
        }

        return sysFileMapper.deleteBatchIds(idList);
    }


    @Override
    public SysFile updateFileUrl(SysFile file) throws Exception {
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
                this.updateFile(file);
            }
        }
        return file;
    }
}
