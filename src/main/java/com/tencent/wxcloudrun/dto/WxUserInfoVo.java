package com.tencent.wxcloudrun.dto;


import lombok.Data;

import java.io.Serializable;

@Data
public class WxUserInfoVo implements Serializable {



    private String nickname;


    private String imageUrl;
}
