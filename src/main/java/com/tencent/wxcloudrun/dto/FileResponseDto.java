package com.tencent.wxcloudrun.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileResponseDto extends FileRequestDto implements Serializable {

    private String download_url;

    public FileResponseDto(String fileid, String download_url) {
         super(fileid);
         this.download_url = download_url;
    }
}
