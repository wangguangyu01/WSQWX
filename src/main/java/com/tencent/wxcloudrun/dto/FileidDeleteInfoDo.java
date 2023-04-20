package com.tencent.wxcloudrun.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class FileidDeleteInfoDo implements Serializable {

    /**
     * 删除图片的fileid
     */
    private String  fileid;


    /**
     * 删除图片的fileid的status状态，0表示成功
     */
    private Integer  status;

    /**
     * 删除图片的fileid的errmsg
     */
    private Integer  errmsg;
}
