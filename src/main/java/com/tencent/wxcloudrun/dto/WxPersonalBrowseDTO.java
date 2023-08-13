package com.tencent.wxcloudrun.dto;

import com.tencent.wxcloudrun.model.WxPersonalBrowse;
import lombok.Data;

@Data
public class WxPersonalBrowseDTO extends WxPersonalBrowse {

    private String requestIp;
}
