package com.tencent.wxcloudrun.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tencent.wxcloudrun.dao.WxTemplateMsgLogMapper;
import com.tencent.wxcloudrun.model.WxTemplateMsgLog;
import com.tencent.wxcloudrun.service.AttachmentService;
import com.tencent.wxcloudrun.utils.JacksonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tencent.wxcloudrun.model.WxMsgTemplate;
import com.tencent.wxcloudrun.dao.WxMsgTemplateMapper;
import com.tencent.wxcloudrun.service.WxMsgTemplateService;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class WxMsgTemplateServiceImpl extends ServiceImpl<WxMsgTemplateMapper, WxMsgTemplate> implements WxMsgTemplateService {


    @Value("${weixin.env}")
    private String weixinEnv;

    @Value("${weixin.url}")
    private String weixinUrl;

    @Autowired
    private WxTemplateMsgLogMapper wxTemplateMsgLogMapper;

    @Autowired
    private AttachmentService attachmentService;

    @Autowired
    public RestTemplate restTemplate;





    @Override
    public WxTemplateMsgLog sendWxMsg(String openId,  WxMsgTemplate wxMsgTemplate) throws Exception {
        WxTemplateMsgLog wxTemplateMsgLog = null;
        // 获取token
        String token = attachmentService.weixinToken();
        // 存储对象创建文件美女椅子
        String fileUploadUrl = weixinUrl + "cgi-bin/message/subscribe/send?access_token=" + token;
        Map<String, Object> body = new HashMap<>();
        body.put("touser", openId);
        body.put("template_id", wxMsgTemplate.getTemplateId());
        body.put("page", wxMsgTemplate.getUrl());
        JSONObject jsonObject = JSONObject.parseObject(wxMsgTemplate.getData());
        body.put("data", jsonObject);
        body.put("miniprogram_state", wxMsgTemplate.getMiniprogram());
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(JSONObject.toJSONString(body), httpHeaders);
        ResponseEntity<JSONObject> responseEntity = restTemplate.postForEntity(fileUploadUrl, request, JSONObject.class);
        log.info("sendWxMsg---->{}", responseEntity);
        if (responseEntity.getStatusCodeValue() == 200) {
            JSONObject jsonObjectData = responseEntity.getBody();
            wxTemplateMsgLog = WxTemplateMsgLog.builder().templateId(wxMsgTemplate.getTemplateId())
                    .templateType(wxMsgTemplate.getTemplateType())
                    .appid(wxMsgTemplate.getAppid())
                    .miniprogram(wxMsgTemplate.getMiniprogram())
                    .url(wxMsgTemplate.getUrl())
                    .data(wxMsgTemplate.getData())
                    .sendResult(NumberUtils.toInt(String.valueOf(jsonObjectData.get("errcode"))))
                    .sendTime(new Date())
                    .touser(openId)
                    .build();
            wxTemplateMsgLogMapper.insert(wxTemplateMsgLog);
        }
        return wxTemplateMsgLog;
    }


    @Override
    public WxMsgTemplate queryOne(Integer templateType) {
        LambdaQueryWrapper<WxMsgTemplate> msgTemplateLambdaQueryWrapper = new LambdaQueryWrapper<>();
        msgTemplateLambdaQueryWrapper.eq(WxMsgTemplate::getTemplateType, templateType);
        msgTemplateLambdaQueryWrapper.eq(WxMsgTemplate::getStatus, 0);
        WxMsgTemplate wxMsgTemplate = baseMapper.selectOne(msgTemplateLambdaQueryWrapper);
        return wxMsgTemplate;
    }
}

