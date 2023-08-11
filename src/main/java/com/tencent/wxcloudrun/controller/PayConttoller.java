package com.tencent.wxcloudrun.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;
import com.tencent.wxcloudrun.config.ApiResponse;
import com.tencent.wxcloudrun.dto.*;
import com.tencent.wxcloudrun.model.OderPay;
import com.tencent.wxcloudrun.model.WxUser;
import com.tencent.wxcloudrun.service.OderPayService;
import com.tencent.wxcloudrun.service.PayService;
import com.tencent.wxcloudrun.service.WxUserService;
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
                WxUser wxUser = wxUserService.queryWxUserOne(oderPay.getOpenId());
                wxUser.setAuthentication("1");
                wxUserService.updateWxUser(wxUser);
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
