package com.tencent.wxcloudrun.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;
import com.tencent.wxcloudrun.config.ApiResponse;
import com.tencent.wxcloudrun.dto.*;
import com.tencent.wxcloudrun.model.OderPay;
import com.tencent.wxcloudrun.model.WxBrowsingUsers;
import com.tencent.wxcloudrun.model.WxPersonalBrowse;
import com.tencent.wxcloudrun.model.WxUser;
import com.tencent.wxcloudrun.service.*;
import com.tencent.wxcloudrun.utils.IPUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.transform.sax.SAXResult;
import java.io.ByteArrayOutputStream;
import java.util.Map;

import static org.springframework.http.MediaType.TEXT_XML_VALUE;

@RestController
@Slf4j
public class PayConttoller  {

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



    @RequestMapping(value = "/notifyOrder", consumes = TEXT_XML_VALUE,produces = MediaType.APPLICATION_XML_VALUE)
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
                if (oderPay.getPayType() == 1) {
                    WxUser wxUser = wxUserService.queryWxUserOne(oderPay.getOpenId());
                    wxUser.setAuthentication("1");
                    wxUserService.updateWxUser(wxUser);
                } else if (oderPay.getPayType() == 3) {
                    // 普通会员浏览资料费用
                    LambdaQueryWrapper<WxPersonalBrowse> browseLambdaQueryWrapper = new LambdaQueryWrapper<>();
                    browseLambdaQueryWrapper.eq(WxPersonalBrowse::getTradeNo, requestDTO.getOut_trade_no());
                    WxPersonalBrowse wxPersonalBrowse = wxPersonalBrowseService.getOne(browseLambdaQueryWrapper);
                    if (!ObjectUtils.isEmpty(wxPersonalBrowse)) {
                        LambdaQueryWrapper<WxBrowsingUsers> browsingUsersLambdaQueryWrapper = new LambdaQueryWrapper<>();
                        browsingUsersLambdaQueryWrapper.eq(WxBrowsingUsers::getBrowsingUsersOpenid, wxPersonalBrowse.getBrowsingOpenid());
                        browsingUsersLambdaQueryWrapper.eq(WxBrowsingUsers::getLoginOpenId, wxPersonalBrowse.getLoginOpenId());
                        browsingUsersLambdaQueryWrapper.eq(WxBrowsingUsers::getBrowsingType, "0");
                        WxBrowsingUsers wxBrowsingUsers  = wxBrowsingUsersService.getOne(browsingUsersLambdaQueryWrapper);
                        if (!ObjectUtils.isEmpty(wxBrowsingUsers)) {
                            wxBrowsingUsers.setBrowsingType("2");
                            wxBrowsingUsersService.updateById(wxBrowsingUsers);
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
    public ApiResponse placeOrder(@RequestBody PayOrderDo payOrderDo)  {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        Map<String, Object> map = null;
        try {
            log.info("placeOrder payOrderDo ---> {}", payOrderDo);
            IPUtil ipUtil = new IPUtil();
            String ip  = ipUtil.getIP(request);
            map = payService.unifiedOrder(payOrderDo.getOpenId(), payOrderDo.getPayType(), ip, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ApiResponse.ok(map);
    }





}
