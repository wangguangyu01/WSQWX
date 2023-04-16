package com.tencent.wxcloudrun.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import com.tencent.wxcloudrun.dto.FileRequestDto;
import com.tencent.wxcloudrun.dto.FileResponseDto;
import com.tencent.wxcloudrun.service.AttachmentService;
import com.tencent.wxcloudrun.utils.FileUtil;
import com.tencent.wxcloudrun.utils.UUIDGenerator;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.tomcat.util.http.fileupload.FileItem;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;


@Service
@Slf4j
public class AttachmentServiceImpl implements AttachmentService {


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




    @Override
    public String weixinToken() {
        String token = "";
        String tokenUrl = weixinUrl + "cgi-bin/token?grant_type=client_credential&appid=" + weixinAppId + "&secret=" + weixinSecret;
        ResponseEntity<JSONObject> responseEntity = restTemplate.getForEntity(tokenUrl, JSONObject.class);
        if (responseEntity.getStatusCodeValue() == 200) {
            JSONObject body = responseEntity.getBody();
            if (!ObjectUtils.isEmpty(body)) {
                token = body.getString("access_token");
            }
        }
        return token;
    }


    @Override
    public String weixinUpload(String path) throws IOException {
        if (StringUtils.isBlank(path)) {
            return "";
        }
        String fileName = UUIDGenerator.getUUID();
        File file = new File(path);

        String fileId = "";
        // 获取微信的token
        String token = this.weixinToken();
        // 存储对象创建文件美女椅子
        String fileUploadUrl = weixinUrl + "tcb/uploadfile?=access_token=" + token;
        Map<String, Object> body = new HashMap<>();
        body.put("env", weixinEnv);
        body.put("path", fileName);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(JSONObject.toJSONString(body), httpHeaders);
        ResponseEntity<JSONObject> responseEntity = restTemplate.postForEntity(fileUploadUrl, request, JSONObject.class);
        JSONObject jsonUploadUrl = null;
        if (responseEntity.getStatusCodeValue() == 200) {
            jsonUploadUrl = responseEntity.getBody();
            // 文件名字创建成功
            if ("0".equals(jsonUploadUrl.getString("errcode"))) {
                // 将文件上传到对象存储中刚才创建的文件中
                FileItem fileItem = new DiskFileItemFactory().createItem(fileName,
                        MediaType.MULTIPART_FORM_DATA_VALUE, true, fileName);
                OutputStream outputStream = fileItem.getOutputStream();
                InputStream inputStream = new FileInputStream(file);
                IOUtils.copy(inputStream, outputStream);
                httpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
                MultiValueMap<String, Object> multiValueMap = new LinkedMultiValueMap<>();
                multiValueMap.add("key", fileName);
                multiValueMap.add("Signature", jsonUploadUrl.getString("authorization"));
                multiValueMap.add("x-cos-security-token", jsonUploadUrl.getString("token"));
                multiValueMap.add("x-cos-meta-fileid", jsonUploadUrl.getString("cos_file_id"));
                multiValueMap.add("file", fileItem.get());
                HttpEntity<MultiValueMap> requestForm = new HttpEntity<>(multiValueMap, httpHeaders);
                ResponseEntity<JSONObject> objectResponseEntity = restTemplate.postForEntity(jsonUploadUrl.getString("url"), requestForm, JSONObject.class);
                if (objectResponseEntity.getStatusCodeValue() == 204) {
                    fileId = jsonUploadUrl.getString("file_id");
                }
            }
        }
        return fileId;
    }


    @Override
    public String weixinUpload(MultipartFile file) throws IOException {
        String fileId = "";
        // 获取微信的token
        String token = this.weixinToken();
        // 存储对象创建文件美女椅子
        String fileUploadUrl = weixinUrl + "tcb/uploadfile?=access_token=" + token;
        Map<String, Object> body = new HashMap<>();
        String fileName = file.getOriginalFilename();
        fileName = FileUtil.renameToUUID(fileName);
        body.put("env", weixinEnv);
        body.put("path", fileName);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(JSONObject.toJSONString(body), httpHeaders);
        ResponseEntity<JSONObject> responseEntity = restTemplate.postForEntity(fileUploadUrl, request, JSONObject.class);
        JSONObject jsonUploadUrl = null;
        if (responseEntity.getStatusCodeValue() == 200) {
            jsonUploadUrl = responseEntity.getBody();
            // 文件名字创建成功
            if ("0".equals(jsonUploadUrl.getString("errcode"))) {
                httpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
                MultiValueMap<String, Object> multiValueMap = new LinkedMultiValueMap<>();
                multiValueMap.add("key", fileName);
                multiValueMap.add("Signature", jsonUploadUrl.getString("authorization"));
                multiValueMap.add("x-cos-security-token", jsonUploadUrl.getString("token"));
                multiValueMap.add("x-cos-meta-fileid", jsonUploadUrl.getString("cos_file_id"));
                multiValueMap.add("file", file.getBytes());
                HttpEntity<MultiValueMap> requestForm = new HttpEntity<>(multiValueMap, httpHeaders);
                ResponseEntity<JSONObject> objectResponseEntity = restTemplate.postForEntity(jsonUploadUrl.getString("url"), requestForm, JSONObject.class);
                if (objectResponseEntity.getStatusCodeValue() == 204) {
                    fileId = jsonUploadUrl.getString("file_id");
                }
            }
        }
        return fileId;
    }


    @Override
    public List<FileResponseDto> batchDownloadFile(List<FileRequestDto> fileRequestDtos) {
        if (CollectionUtils.isEmpty(fileRequestDtos)) {
            return null;
        }
        String token = this.weixinToken();
        String fileUploadUrl = weixinUrl + "tcb/batchdownloadfile?=access_token=" + token;
        Map<String, Object> body = new HashMap<>();
        body.put("env", weixinEnv);
        body.put("file_list", fileRequestDtos);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(JSONObject.toJSONString(body), httpHeaders);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(fileUploadUrl, request, String.class);
        log.info("batchDownloadFile responseEntity--->{}", responseEntity.toString());
        if (responseEntity.getStatusCodeValue() == 200) {
            JSONObject jsonObject = JSONObject.parseObject(responseEntity.getBody());
            String errcode = jsonObject.getString("errcode");
            if ("0".equals(errcode)) {
                JSONArray jsonArray =jsonObject.getJSONArray("file_list");
                List<FileResponseDto> fileResponseDtos = jsonArray.toJavaList(FileResponseDto.class);
                return fileResponseDtos;
            }
        }
        return null;
    }


    @Override
    public List<String> batchDeleteFile(List<String> fileid_list) {
        if (CollectionUtils.isEmpty(fileid_list)) {
            return null;
        }
        String token = this.weixinToken();
        String batchdeletefileUrl = weixinUrl + "tcb/batchdeletefile?=access_token=" + token;
        Map<String, Object> body = new HashMap<>();
        body.put("env", weixinEnv);
        body.put("fileid_list", fileid_list);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(JSONObject.toJSONString(body), httpHeaders);
        ResponseEntity<JSONObject> responseEntity = restTemplate.postForEntity(batchdeletefileUrl, request, JSONObject.class);
        List<String> list = new ArrayList<>();
        if (responseEntity.getStatusCodeValue() == 200) {
            JSONObject jsonObject = responseEntity.getBody();
            String errcode = jsonObject.getString("errcode");
            if ("0".equals(errcode)) {
                JSONArray jsonArray =jsonObject.getJSONArray("delete_list");
                for (int i = 0; i < jsonArray.size(); i++) {
                     JSONObject jsonObjectData = jsonArray.getJSONObject(i);
                     if ("0".equals(jsonObjectData.getString("status"))) {
                         list.add(jsonObjectData.getString("fileid"));
                     }
                }
                return list;
            }
        }

        return null;
    }
}
