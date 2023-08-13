package com.tencent.wxcloudrun.vo;


import lombok.Data;

import java.io.Serializable;

@Data
public class WxUserPowerVo implements Serializable {

    /**
     * 是否显示微信号
     */
    private boolean showWxNumber;


    /**
     * 是否显示页面充值按钮
     */
    private boolean showbutton;

    /**
     * 是否付费浏览
     */
    private boolean payShow;
}
