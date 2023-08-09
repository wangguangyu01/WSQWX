package com.tencent.wxcloudrun.dto;


import com.tencent.wxcloudrun.utils.MD5Utils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ObjectUtils;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;


/**
 * 下单的参数
 */
@Data
@Slf4j
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UnifiedorderDto {

    /**
     * 公众号小程序的appid
     */
    private String appid;


    /**
     * 微信支付分配的商户号
     */
    private String mch_id;

    /**
     * 附加数据
     */
    private String attach;


    /**
     * 代码随机数
     */
    private String nonce_str;


    /**
     * 签名
     */
    private String sign;


    /**
     * 签名类型 默认MD5
     */
    private String sign_type;

    /**
     * 商品描述
     */
    private String body;


    /**
     * 订单号
     */
    private String out_trade_no;


    /**
     * 标价币种
     */
    private String fee_type;


    /**
     * 支付金额
     */
    private String  total_fee;


    /**
     * 终端IP
     */
    private String spbill_create_ip;


    /**
     * 交易类型
     */
    private String trade_type;


    /**
     * 小程序以及公众号的支付人员
     */
    private String openid;


    /**
     * trade_type=NATIVE时，此参数必传。此参数为二维码中包含的商品ID，商户自行定义。
     */
    private String product_id;


    /**
     *
     * 该字段常用于线下活动时的场景信息上报，支持上报实际门店信息，商户也可以按需求自己上报相关信息。
     * 例子：{"store_info" : {
     * "id": "SZTX001",
     * "name": "腾大餐厅",
     * "area_code": "440305",
     * "address": "科技园中一路腾讯大厦" }}
     *
     *
     */
    private String scene_info;


    /**
     * 安全api密钥
     */
    private String key;


    /**
     * 回调函数
     */
    private String notify_url;


    /**
     * 生成下订单的签名
     * @param unifiedorderDto
     * @return
     */
    public static String creatSign(UnifiedorderDto unifiedorderDto) throws IllegalAccessException, UnsupportedEncodingException, NoSuchAlgorithmException {
        TreeMap<String, Object> params = new TreeMap<String, Object>();
        // 如果装载后的params是空，返回true，直接返回空
        if (getTreeMap(unifiedorderDto, params)) {
            return "";
        }
        String signA = StringUtils.join(params.entrySet(), "&");
        signA +="&key="+ unifiedorderDto.getKey();
        System.out.println(signA);
        String sgin = MD5Utils.encryptNoWithSalt(signA);
        unifiedorderDto.setSign(sgin);
        return sgin;
    }

    private static boolean getTreeMap(UnifiedorderDto unifiedorderDto, TreeMap<String, Object> params) throws IllegalAccessException {
        Class<UnifiedorderDto> unifiedorderDtoClass = UnifiedorderDto.class;
        // 返回所有字段
        Field[] fields = unifiedorderDtoClass.getDeclaredFields();
        for (Field field: fields) {
            field.setAccessible(true);
            Object o = field.get(unifiedorderDto);
            if (o instanceof ch.qos.logback.classic.Logger ) {
                  continue;
            }
            String fieldValue = (String)o;
            if (StringUtils.isBlank(fieldValue)) {
                 continue;
            }
            String name = field.getName();
            if (StringUtils.equals(name, "key")) {
                continue;
            }
            params.put(name, fieldValue);
        }
        if (MapUtils.isEmpty(params)) {
            return true;
        }
        return false;
    }


    public static String buildXml(UnifiedorderDto unifiedorderDto)
            throws IllegalAccessException, UnsupportedEncodingException, NoSuchAlgorithmException {
        TreeMap<String, Object> treeMap = new TreeMap<String, Object>();
        StringBuffer stringBuffer = new StringBuffer();
        String sgin  = creatSign(unifiedorderDto);
        // 如果装载后的params是空，返回true，直接返回空
        if (getTreeMap(unifiedorderDto, treeMap)) {
            return "";
        }

        Set<Map.Entry<String,Object>> entries = treeMap.entrySet();
        stringBuffer.append("<xml>");
        for (Map.Entry<String,Object> entry: entries) {
            if (StringUtils.equals(entry.getKey(), "key")) {
                 continue;
            }
            stringBuffer.append("<").append(entry.getKey()).append(">");
            stringBuffer.append("<![CDATA[").append(entry.getValue()).append("]]>");
            stringBuffer.append("</").append(entry.getKey()).append(">");
        }
        stringBuffer.append("</xml>");
        String xml = stringBuffer.toString();
        log.info("UnifiedorderDto XML ---》{}",  xml);
        return xml;
    }





}
