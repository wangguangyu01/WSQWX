package com.tencent.wxcloudrun.service;

import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tencent.wxcloudrun.dto.CategoriesRequest;
import com.tencent.wxcloudrun.dto.WxUserDto;
import com.tencent.wxcloudrun.dto.WxUserInfoVo;
import com.tencent.wxcloudrun.dto.WxUserPageParamDto;
import com.tencent.wxcloudrun.model.BlogContent;
import com.tencent.wxcloudrun.model.WxUser;

import java.util.List;
import java.util.Map;

public interface WxUserService {



    /**
     * 微信登录wx.login获取的code
     *
     * @param code
     * @return
     */
   Map<String,Object> queryWxUserInfo(String code);


   int addWxUser(WxUser wxUser);


    /**
     * 分页查询已经审核通过并且标记为相亲
     *
     * @param wxUserPageParamDto
     * @return
     */
    IPage<WxUser> queryWxUserPage(WxUserPageParamDto wxUserPageParamDto) throws Exception;


    /**
     * 根据openid查询要展示相亲的人员信息
     * @param openid
     * @return
     */
    WxUser queryWxUserOne(String openid) throws Exception;


    int updateWxUser(WxUser wxUser);


    WxUser queryWxUserOneByPhone(String phone) throws Exception;



    int  queryCount(String openid);


    int queryPhoneCount(String phone);


    int updateByPhone(String openid, String phone);


    /**
     * 添加或是保存微信注册用户
     * @param wxUser
     */
    public WxUser  addOrUpdateWxUser(WxUserDto wxUserDto);


    /**
     * 查询6条男生，按照出生日期倒序
     * @return
     */
    List<WxUser> queryBoarduserWithMan();

    /**
     * 查询6条女生，按照出生日期倒序
     * @return
     */
    List<WxUser> queryBoarduserWithWoman();



}

