package com.tencent.wxcloudrun.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileRequestDto implements Serializable {

    private String fileid;

    /**
     * 默认两个小时过期时间
     */
    private int max_age = 7200;




    public FileRequestDto(String fileid) {
        this.fileid = fileid;
    }

}
