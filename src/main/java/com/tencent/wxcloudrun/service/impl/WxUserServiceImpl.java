package com.tencent.wxcloudrun.service.impl;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.digest.SM3;
import cn.hutool.crypto.symmetric.SM4;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tencent.wxcloudrun.dao.WxUserMapper;
import com.tencent.wxcloudrun.dto.CategoriesRequest;
import com.tencent.wxcloudrun.dto.UserOpenInfoDto;
import com.tencent.wxcloudrun.dto.WxUserInfoVo;
import com.tencent.wxcloudrun.dto.WxUserPageParamDto;
import com.tencent.wxcloudrun.model.BlogContent;
import com.tencent.wxcloudrun.model.SysFile;
import com.tencent.wxcloudrun.model.WxUser;
import com.tencent.wxcloudrun.service.AttachmentService;
import com.tencent.wxcloudrun.service.SysFileService;
import com.tencent.wxcloudrun.service.WxUserService;
import com.tencent.wxcloudrun.utils.JacksonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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


    @Value("${slatKey:wx7290Wsqklollnk}")
    private String slatKey;

    @Autowired
    private SysFileService sysFileService;


    @Autowired
    private AttachmentService attachmentService;


    /**
     * 微信登录wx.login获取的code
     *
     * @param code
     * @return
     */
    @Override
    public Map<String,Object> queryWxUserInfo(String code) {

        Map<String,Object> map = new HashMap<>();
        map.put("openid", "");
        map.put("flag", false);
        String url = weixinUrl + "sns/jscode2session?appid=" + weixinAppId + "&secret=" + weixinSecret + "&js_code=" + code + "&grant_type=authorization_code";
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
        if (responseEntity.getStatusCodeValue() == 200) {
            String body = responseEntity.getBody();
            if (!ObjectUtils.isEmpty(body)) {
                UserOpenInfoDto userOpenInfoDto = JSONObject.parseObject(body, UserOpenInfoDto.class);
                String openid = userOpenInfoDto.getOpenid();
                WxUser wxUser = wxUserMapper.selectById(openid);
                if (!ObjectUtils.isEmpty(wxUser)) {
                    map.put("openid", openid);
                    map.put("flag", true);
                } else {
                    map.put("openid", openid);
                    map.put("flag", false);
                }
            }
        }
        return map;
    }


    @Override
    public int addWxUser(WxUser wxUser) {
        return wxUserMapper.insert(wxUser);
    }


    @Override
    public IPage<WxUser> queryWxUserPage(WxUserPageParamDto wxUserPageParamDto) throws Exception {
        // 查询审核通过的
        wxUserPageParamDto.setApprove("0");
        Page page = new Page(wxUserPageParamDto.getCurrentPage(), wxUserPageParamDto.getLimit());
        IPage<WxUser> wxUserIPage = wxUserMapper.queryWxUserPage(page, wxUserPageParamDto);
        if (!ObjectUtils.isEmpty(wxUserIPage)) {
            List<WxUser> wxUsers = wxUserIPage.getRecords();
            for (WxUser wxUser: wxUsers) {
                List<SysFile> fileList  = sysFileService.queryFile(wxUser.getOpenId(), 4);
                List<SysFile> imagePaths = new ArrayList<>();
                for (SysFile sysFile: fileList) {
                    sysFileService.updateFileUrl(sysFile);
                    imagePaths.add(sysFile);
                }
                wxUser.setImagePaths(imagePaths);
            }
        }
        return wxUserIPage;
    }


    @Override
    public WxUser queryWxUserOne(String openid) {
        WxUser wxUser = new WxUser();
        try {
            wxUser = wxUserMapper.selectById(openid);
            getWxUserFile(openid, wxUser);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return wxUser;
    }

    private void getWxUserFile(String openid, WxUser wxUser) throws Exception {

        // 个人照片秀
        List<SysFile> headimgurlFiles = sysFileService.queryFile(openid, 11);
        if (CollectionUtils.isNotEmpty(headimgurlFiles)) {
            SysFile sysFile =  headimgurlFiles.get(0);
            sysFileService.updateFileUrl(sysFile);
            wxUser.setHeadimgurlFile(sysFile);
            wxUser.setHeadimgurl(sysFile.getUrl());
        }


        // 个人照片秀
        List<SysFile> list = sysFileService.queryFile(openid, 4);
        List<SysFile> imagePaths = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(list)) {
            for (SysFile file: list) {
                sysFileService.updateFileUrl(file);
                imagePaths.add(file);
            }
            wxUser.setImagePaths(imagePaths);
        }

        // 身份证
        List<SysFile> identityCard = sysFileService.queryFile(openid, 5);
        List<SysFile> identityCardList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(identityCard)) {
            for (SysFile identityCardFile: identityCard) {
                sysFileService.updateFileUrl(identityCardFile);
                identityCardList.add(identityCardFile);
            }
            wxUser.setIdentityCard(identityCardList);
        }

        // 收入证明
        List<SysFile> salary = sysFileService.queryFile(openid, 6);
        List<SysFile> salaryList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(salary)) {
            for (SysFile salaryFile: salary) {
                sysFileService.updateFileUrl(salaryFile);
                salaryList.add(salaryFile);
            }
            wxUser.setSalary(salaryList);
        }


        // 学历证明
        List<SysFile> academicCertificate = sysFileService.queryFile(openid, 7);
        List<SysFile> academicCertificateList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(academicCertificate)) {
            for (SysFile academicCertificateFile: academicCertificate) {
                sysFileService.updateFileUrl(academicCertificateFile);
                academicCertificateList.add(academicCertificateFile);
            }
            wxUser.setAcademicCertificate(academicCertificateList);
        }



        // 行车证
        List<SysFile> vehicleLicense = sysFileService.queryFile(openid, 8);
        List<SysFile> vehicleLicenseList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(vehicleLicense)) {
            for (SysFile vehicleLicenseFile: vehicleLicense) {
                sysFileService.updateFileUrl(vehicleLicenseFile);
                vehicleLicenseList.add(vehicleLicenseFile);
            }
            wxUser.setVehicleLicense(vehicleLicenseList);
        }

        // 征信
        List<SysFile> credit = sysFileService.queryFile(openid, 9);
        List<SysFile> creditList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(credit)) {
            for (SysFile creditFile: credit) {
                sysFileService.updateFileUrl(creditFile);
                creditList.add(creditFile);
            }
            wxUser.setCredit(creditList);
        }

        // 房本
        List<SysFile> premisesPermit = sysFileService.queryFile(openid, 10);
        List<SysFile> premisesPermitList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(premisesPermit)) {
            for (SysFile premisesPermitFile: premisesPermit) {
                sysFileService.updateFileUrl(premisesPermitFile);
                premisesPermitList.add(premisesPermitFile);
            }
            wxUser.setPremisesPermit(premisesPermitList);
        }
    }


    @Override
    public int updateWxUser(WxUser wxUser) {
        return wxUserMapper.updateById(wxUser);
    }





    @Override
    public WxUser queryWxUserOneByPhone(String phone) throws Exception {

        WxUser wxUser = new WxUser();
        try {
            LambdaQueryWrapper<WxUser> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(WxUser::getPhone, phone);
            wxUser = wxUserMapper.selectOne(queryWrapper);
            getWxUserFile(wxUser.getOpenId(), wxUser);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return wxUser;
    }


    @Override
    public int queryCount(String openid) {
        LambdaQueryWrapper<WxUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WxUser::getOpenId, openid);
        return wxUserMapper.selectCount(queryWrapper);
    }

    @Override
    public int queryPhoneCount(String phone) {
        LambdaQueryWrapper<WxUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WxUser::getPhone, phone);
        return wxUserMapper.selectCount(queryWrapper);
    }


    @Override
    public int updateByPhone(String openid,String phone) {
        return  wxUserMapper.updateByPhone(openid, phone);
    }
}

