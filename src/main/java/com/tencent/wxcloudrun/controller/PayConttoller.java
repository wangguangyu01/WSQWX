package com.tencent.wxcloudrun.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;
import com.tencent.wxcloudrun.config.ApiResponse;
import com.tencent.wxcloudrun.config.WxApiResponse;
import com.tencent.wxcloudrun.dto.*;
import com.tencent.wxcloudrun.model.*;
import com.tencent.wxcloudrun.service.*;
import com.tencent.wxcloudrun.utils.IPUtil;
import com.wechat.pay.contrib.apache.httpclient.WechatPayHttpClientBuilder;
import com.wechat.pay.contrib.apache.httpclient.auth.CertificatesVerifier;
import com.wechat.pay.contrib.apache.httpclient.util.CertSerializeUtil;
import com.wechat.pay.contrib.apache.httpclient.util.PemUtil;
import com.wechat.pay.java.core.RSAAutoCertificateConfig;
import com.wechat.pay.java.core.exception.ValidationException;
import com.wechat.pay.java.core.notification.NotificationConfig;
import com.wechat.pay.java.core.notification.NotificationParser;
import com.wechat.pay.java.service.partnerpayments.jsapi.model.Transaction;
import com.wechat.pay.java.service.refund.model.RefundNotification;
import com.wechat.pay.java.service.refund.model.Status;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.transform.sax.SAXResult;
import java.io.*;
import java.math.BigInteger;
import java.nio.file.Paths;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.*;

import static org.apache.http.HttpHeaders.ACCEPT;
import static org.apache.http.entity.ContentType.APPLICATION_JSON;
import static org.springframework.http.MediaType.TEXT_XML_VALUE;

@RestController
@Slf4j
public class PayConttoller {

    @Autowired
    private PayService payService;


    @Autowired
    private OderPayService oderPayService;

    @Autowired
    private WxUserService wxUserService;

    @Autowired
    private WxBrowsingUsersService wxBrowsingUsersService;

    @Autowired
    private WxPersonalBrowseService wxPersonalBrowseService;

    @Autowired
    private WxMsgTemplateService wxMsgTemplateService;

    @Autowired
    private WxActivityService wxActivityService;

    @Autowired
    private BlogContentService blogContentService;



    @Resource
    private SystemConfigService systemConfigService;


    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private ServletContext context;

    @Resource
    private OderPayReturnService oderPayReturnService;


    @RequestMapping(value = "/notifyOrder", consumes = TEXT_XML_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
    public String notifyOrder(@RequestBody XmlRequestDTO requestDTO) throws Exception {
        XmlResponseDTO response = new XmlResponseDTO("SUCCESS", "OK");
        try {
            log.info("notifyOrder request---> {}", requestDTO);
            LambdaQueryWrapper<OderPay> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(OderPay::getTradeNo, requestDTO.getOut_trade_no());
            OderPay oderPay = oderPayService.getOne(queryWrapper);
            if (!ObjectUtils.isEmpty(oderPay)) {
                oderPay.setPaySuccess(2);
                oderPay.setTransactionId(requestDTO.getTransaction_id());
                oderPayService.updateById(oderPay);
                if (oderPay.getPayType() == 1 && oderPay.getPaySuccess() == 2) {
                    WxUser wxUser = wxUserService.queryWxUserOne(oderPay.getOpenId());
                    wxUser.setAuthentication("1");
                    wxUserService.updateWxUser(wxUser);
                } else if (oderPay.getPayType() == 3 && oderPay.getPaySuccess() == 2) {
                    // 普通会员浏览资料费用
                    LambdaQueryWrapper<WxPersonalBrowse> browseLambdaQueryWrapper = new LambdaQueryWrapper<>();
                    browseLambdaQueryWrapper.eq(WxPersonalBrowse::getTradeNo, requestDTO.getOut_trade_no());
                    WxPersonalBrowse wxPersonalBrowse = wxPersonalBrowseService.getOne(browseLambdaQueryWrapper);
                    if (!ObjectUtils.isEmpty(wxPersonalBrowse)) {
                        LambdaQueryWrapper<WxBrowsingUsers> browsingUsersLambdaQueryWrapper = new LambdaQueryWrapper<>();
                        browsingUsersLambdaQueryWrapper.eq(WxBrowsingUsers::getBrowsingUsersOpenid, wxPersonalBrowse.getBrowsingOpenid());
                        browsingUsersLambdaQueryWrapper.eq(WxBrowsingUsers::getLoginOpenId, wxPersonalBrowse.getLoginOpenId());
                        browsingUsersLambdaQueryWrapper.eq(WxBrowsingUsers::getBrowsingType, "0");
                        WxBrowsingUsers wxBrowsingUsers = wxBrowsingUsersService.getOne(browsingUsersLambdaQueryWrapper);
                        if (!ObjectUtils.isEmpty(wxBrowsingUsers)) {
                            wxBrowsingUsers.setBrowsingType("2");
                            wxBrowsingUsersService.updateById(wxBrowsingUsers);
                        }

                    }
                } else if (oderPay.getPayType() == 2 && oderPay.getPaySuccess() == 2) {
                    // 查询活动报名成功的模板
                    WxMsgTemplate wxMsgTemplate = wxMsgTemplateService.queryOne(3);
                    if (!ObjectUtils.isEmpty(wxMsgTemplate)) {
                        LambdaQueryWrapper<WxActivity> activityLambdaQueryWrapper = new LambdaQueryWrapper<>();
                        activityLambdaQueryWrapper.eq(WxActivity::getTradeNo, oderPay.getTradeNo());
                        List<WxActivity> wxActivities = wxActivityService.list(activityLambdaQueryWrapper);
                        if (CollectionUtils.isNotEmpty(wxActivities)) {
                            String url = wxMsgTemplate.getUrl();
                            url = StringUtils.replace(url, "{url}", wxActivities.get(0).getActivityUuid());
                            BlogContent blogContent = blogContentService.queryBlogContentInfo(wxActivities.get(0).getActivityUuid());
                            wxMsgTemplate.setUrl(url);
                            String data = wxMsgTemplate.getData();
                            data = StringUtils.replace(data, "活动名称", blogContent.getContent());
                            data = StringUtils.replace(data, "与会嘉宾", wxActivities.get(0).getNickname());
                            wxMsgTemplate.setData(data);
                            wxMsgTemplateService.sendWxMsg(oderPay.getOpenId(), wxMsgTemplate);
                            wxMsgTemplateService.sendWxMsg("o0orR5CXlh4fpa9WDvio5KU94dRo", wxMsgTemplate);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return XmlResponseDTO.buildXml(response);
    }


    @PostMapping("/api/placeOrder")
    public ApiResponse placeOrder(@RequestBody PayOrderDo payOrderDo) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        Map<String, Object> map = null;
        try {
            WxUser wxUser = wxUserService.queryWxUserOne(payOrderDo.getOpenId());
            if (ObjectUtils.isEmpty(wxUser)) {
                return ApiResponse.ok(map);
            }
            if (!StringUtils.equals(wxUser.getApprove(), "0")) {
                map.put("price", "0");
                return ApiResponse.ok(map);
            }
            log.info("placeOrder payOrderDo ---> {}", payOrderDo);
            IPUtil ipUtil = new IPUtil();
            String ip = ipUtil.getIP(request);
            map = payService.unifiedOrder(payOrderDo.getOpenId(), payOrderDo.getPayType(), ip, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ApiResponse.ok(map);
    }


    @PostMapping("/api/returnPlay")
    public WxApiResponse returnPlay(HttpServletRequest request, HttpServletResponse response) throws IOException {
        BufferedReader reader = null;
        try {
            StringBuffer stringBuffer = new StringBuffer();
            reader = request.getReader();
            String line = "";
            while (null != (line = reader.readLine())) {
                stringBuffer.append(line);
            }
            String data = stringBuffer.toString();
            com.wechat.pay.java.core.notification.RequestParam requestParam = new com.wechat.pay.java.core.notification.RequestParam
                    .Builder()
                    .serialNumber(request.getHeader("Wechatpay-Serial"))
                    .nonce(request.getHeader("Wechatpay-Nonce"))
                    .signature(request.getHeader("Wechatpay-Signature"))
                    .timestamp(request.getHeader("Wechatpay-Timestamp"))
                    .body(data)
                    .build();

            LambdaQueryWrapper<SystemConfig> merchantIdWrapper = new LambdaQueryWrapper<>();
            merchantIdWrapper.eq(SystemConfig::getSysConfigKey, "weixinMchid");
            SystemConfig merchanCofig = systemConfigService.getOne(merchantIdWrapper);


            LambdaQueryWrapper<SystemConfig> api3KeyWrapper = new LambdaQueryWrapper<>();
            api3KeyWrapper.eq(SystemConfig::getSysConfigKey, "weixinCertKeyApi3");
            SystemConfig api3Key = systemConfigService.getOne(api3KeyWrapper);
            log.info("获取的路径地址::" + context.getRealPath("/mchidCert/apiclient_key.pem"));
            org.springframework.core.io.Resource  resource = resourceLoader.getResource("classpath:mchidCert/apiclient_key.pem");
            PrivateKey privateKey = PemUtil.loadPrivateKey(resource.getInputStream());
            NotificationConfig config = new RSAAutoCertificateConfig.Builder()
                    .merchantId(merchanCofig.getSysConfigValue())
                    .privateKey(privateKey)
                    .merchantSerialNumber(DownLoadCertService.serialNo)
                    .apiV3Key(api3Key.getSysConfigValue())
                    .build();
            // 初始化 NotificationParser
            NotificationParser parser = new NotificationParser(config);

            try {
                // 以支付通知回调为例，验签、解密并转换成 Transaction
                RefundNotification transaction = parser.parse(requestParam, RefundNotification.class);
                log.info("RefundNotification--- >{}", transaction);
                Status status = transaction.getRefundStatus();
                log.info("status.equals(Status.SUCCESS) --->{}", status.equals(Status.SUCCESS));
                if (status.equals(Status.SUCCESS)) {
                    oderPayReturnService.saveOderPayReturn(transaction);
                }
            } catch (ValidationException e) {
                // 签名验证失败，返回 401 UNAUTHORIZED 状态码
                log.error("sign verification failed", e);
                return WxApiResponse.error(HttpStatus.UNAUTHORIZED.getReasonPhrase());
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
        return WxApiResponse.ok();
    }




}
