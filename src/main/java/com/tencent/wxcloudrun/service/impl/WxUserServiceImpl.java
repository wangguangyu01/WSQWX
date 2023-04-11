package com.tencent.wxcloudrun.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.tencent.wxcloudrun.dao.WxUserMapper;
import com.tencent.wxcloudrun.model.WxUser;
import com.tencent.wxcloudrun.service.WxUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class WxUserServiceImpl implements WxUserService {

    @Value("${weixin.secret}")
    private String weixinSecret;

    @Value("${weixin.appid}")
    private String weixinAppId;


    @Value("${weixin.env}")
    private String weixinEnv;

    @Value("${weixin.url}")
    private String weixinUrl;



    @Autowired
    public RestTemplate restTemplate;

    @Autowired
    private WxUserMapper wxUserMapper;


    /**
     * 微信登录wx.login获取的code
     * @param code
     * @return
     */
    @Override
    public boolean queryWxUserInfo(String code) {
        String openid= "";
        String url = weixinUrl + "sns/jscode2session?appid="+weixinAppId+"&secret="+weixinSecret+"&js_code="+code+"&grant_type=authorization_code";
        ResponseEntity<JSONObject> responseEntity = restTemplate.getForEntity(url, JSONObject.class);
        if (responseEntity.getStatusCodeValue() == 200) {
            JSONObject body = responseEntity.getBody();
            if (!ObjectUtils.isEmpty(body)) {
                openid = body.getString("openid");
                if (StringUtils.isNotBlank(openid)) {
                    WxUser wxUser = wxUserMapper.selectById(openid);
                    if (!ObjectUtils.isEmpty(wxUser)) {
                         return true;
                    }
                }

            }
        }
        return false;
    }
}
