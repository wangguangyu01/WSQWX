package com.tencent.wxcloudrun.service;

import com.alibaba.fastjson.JSON;
import com.tencent.wxcloudrun.model.WxMsgTemplate;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tencent.wxcloudrun.model.WxTemplateMsgLog;

public interface WxMsgTemplateService extends IService<WxMsgTemplate> {



    /**
     * 发送订阅消息
     * @param openId
     * @param wxMsgTemplate
     * @return
     */
    public WxTemplateMsgLog sendWxMsg(String openId,  WxMsgTemplate wxMsgTemplate) throws Exception;


    /**
     * 根据模板类型查询
     * @param templateType
     * @return
     */
    WxMsgTemplate queryOne(Integer templateType);

}

