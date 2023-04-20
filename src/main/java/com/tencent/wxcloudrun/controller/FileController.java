package com.tencent.wxcloudrun.controller;

import com.alibaba.fastjson.JSONObject;
import com.tencent.wxcloudrun.config.ApiResponse;
import com.tencent.wxcloudrun.dto.FileRequestDto;
import com.tencent.wxcloudrun.dto.FileResponseDto;
import com.tencent.wxcloudrun.dto.UploadUserFileDto;
import com.tencent.wxcloudrun.model.SysFile;
import com.tencent.wxcloudrun.service.AttachmentService;
import com.tencent.wxcloudrun.service.SysFileService;
import com.tencent.wxcloudrun.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;



/**
 * 文件控制器
 */
@RestController
@Slf4j
public class FileController {

    @Autowired
    private AttachmentService attachmentService;

    @Autowired
    private SysFileService fileService;

    @PostMapping("/api/uploadFile" )
    public ApiResponse uploadFile(@RequestBody UploadUserFileDto uploadUserFileDto) throws IOException {
        try {
            log.info("uploadFile uploadUserFileDto-->{}", uploadUserFileDto);
            String fileid = uploadUserFileDto.getFileid();
            if (StringUtils.isNotBlank(fileid)) {
                List<FileRequestDto> list = new ArrayList<>();
                FileRequestDto fileRequestDto = new FileRequestDto(fileid);
                list.add(fileRequestDto);
                List<FileResponseDto> responseDtos = attachmentService.batchDownloadFile(list);
                Date date = new Date();
                for (FileResponseDto fileResponseDto : responseDtos) {
                    Date expireTime  = DateUtils.calculateDate(date, Calendar.SECOND, fileResponseDto.getMax_age());
                    SysFile sysFile = SysFile.builder()
                            .fileId(fileResponseDto.getFileid())
                            .createDate(new Date())
                            .requestTime(new Date())
                            .expireTime(expireTime)
                            .url(fileResponseDto.getDownload_url())
                            .contentId(uploadUserFileDto.getOpenId())
                            .type(4)
                            .build();
                    fileService.saveFile(sysFile);
                }
            }
            return ApiResponse.ok();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ApiResponse.error("操作异常");
    }


    @ResponseBody
    @PostMapping("/api/fileDelete")
    ApiResponse fileDelete(@RequestBody UploadUserFileDto uploadUserFileDto) {
        try {
            log.info("/api/fileDelete uploadUserFileDto--->{}", JSONObject.toJSONString(uploadUserFileDto));
            fileService.remove(uploadUserFileDto.getFileid());
            List<SysFile> fileList = fileService.queryFile(uploadUserFileDto.getOpenId(), 4);
            // 直接返回腾讯在服务器上的id
            List<String> fileIds = new ArrayList<>(10);
            if (CollectionUtils.isEmpty(fileList)) {
                return ApiResponse.ok(fileIds);
            } else {
                for (SysFile file: fileList) {
                    fileIds.add(file.getFileId());
                }
            }
            return ApiResponse.ok(fileIds);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ApiResponse.error("操作异常");
    }
}
