package com.tencent.wxcloudrun.dto;

import com.tencent.wxcloudrun.model.WxActivity;
import lombok.Data;

import java.io.Serializable;

@Data
public class WxActivityDTO extends WxActivity implements Serializable {


    private String requestIp;
}
