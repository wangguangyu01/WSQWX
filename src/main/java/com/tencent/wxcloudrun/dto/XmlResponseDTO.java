package com.tencent.wxcloudrun.dto;


import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

@Slf4j
@Data
public class XmlResponseDTO {

    private String return_code;

    private String return_msg;


    public XmlResponseDTO() {

    }

    public XmlResponseDTO(String return_code, String return_msg) {
        this.return_code = return_code;
        this.return_msg = return_msg;
    }


    public static String buildXml(XmlResponseDTO xmlResponseDTO) throws Exception {

        TreeMap<String, Object> treeMap = new TreeMap<String, Object>();
        treeMap.put("return_code", xmlResponseDTO.getReturn_code());
        treeMap.put("return_msg", xmlResponseDTO.getReturn_msg());
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("<xml>");
        Set<Map.Entry<String,Object>> entries = treeMap.entrySet();
        for (Map.Entry<String,Object> entry: entries) {
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
