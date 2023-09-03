package com.tencent.wxcloudrun.config;

import java.util.HashMap;

public class WxApiResponse  {

    private String  code;
    private String errorMsg;
    private Object data;

    private WxApiResponse(String code, String errorMsg, Object data) {
        this.code = code;
        this.errorMsg = errorMsg;
        this.data = data;
    }

    public static WxApiResponse ok() {
        return new WxApiResponse("SUCCESS", "", new HashMap<>());
    }

    public static WxApiResponse ok(Object data) {
        return new WxApiResponse("SUCCESS", "", data);
    }

    public static WxApiResponse error(String errorMsg) {
        return new WxApiResponse("FAIL", errorMsg, new HashMap<>());
    }

}
