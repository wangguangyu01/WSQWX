package com.tencent.wxcloudrun.dto;

import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Data
@ToString
public class UploadUserFileDto {


    private String openId;




    /**
     * 腾讯服务器中的图片id
     */
    private String fileid;
}
