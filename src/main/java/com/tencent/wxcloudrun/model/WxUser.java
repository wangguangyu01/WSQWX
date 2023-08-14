package com.tencent.wxcloudrun.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "wx_user")
public class WxUser implements Serializable {
    /**
     * 微信openid
     */
    @TableId(value = "openid", type = IdType.INPUT)
    private String openId;

    /**
     * appid
     */
    @TableField(value = "appid")
    private String appid;

    @TableField(value = "serial_number")
    private String serialNumber;

    /**
     * 微信号
     */
    @TableField(value = "wx_number")
    private String wxNumber;

    /**
     * 手机号
     */
    @TableField(value = "phone")
    private String phone;

    /**
     * 昵称
     */
    @TableField(value = "nickname")
    private String nickname;


    /**
     * 性别(0-未知、1-男、2-女)
     */
    @TableField(value = "sex")
    private String sex;


    @TableField(value = "height")
    private String height;



    @TableField(value = "weight")
    private String weight;

    /**
     * 出生年月
     */
    @TableField(value = "birthday")
    private String birthday;

    /**
     * 城市
     */
    @TableField(value = "city")
    private String city;

    /**
     * 省份
     */
    @TableField(value = "province")
    private String province;

    /**
     * 地区
     */
    @TableField(value = "region")
    private String region;

    /**
     * 头像
     */
    @TableField(value = "headimgurl")
    private String headimgurl;

    /**
     * unionid
     */
    @TableField(value = "unionid")
    private String unionid;

    /**
     * 个人介绍
     */
    @TableField(value = "personProfile")
    private String personProfile;

    /**
     * 择偶要求
     */
    @TableField(value = "mating_requirement")
    private String matingRequirement;

    /**
     * 扫码场景值
     */
    @TableField(value = "qr_scene_str")
    private String qrSceneStr;

    @TableField(value = "create_time")
    private Date createTime;

    @TableField(value = "update_time")
    private Date updateTime;

    /**
     * 学历
     */
    @TableField(value = "education")
    private String education;


    /**
     * 职业
     */
    @TableField(value = "occupation")
    private String occupation;



    /**
     * 月收入
     */
    @TableField(value = "remuneration")
    private String remuneration;


    /**
     * 审批是否通过
     * 0:通过
     * 1:不通过
     * 2:待审核
     */
    @TableField(value = "approve")
    private String approve;

    @TableField(value = "marriage_seeking_flag")
    private Integer marriageSeekingFlag;


    /**
     * 获取上传到服务器上的图片地址
     */
    @TableField(exist = false)
    private List<SysFile> imagePaths;


    /**
     * 身份证
     */
    @TableField(exist = false)
    private List<SysFile> identityCard;


    /**
     * 收入证明
     */
    @TableField(exist = false)
    private List<SysFile> salary;


    /**
     * 学历证明
     */
    @TableField(exist = false)
    private List<SysFile> academicCertificate;



    /**
     * 行车证
     */
    @TableField(exist = false)
    private List<SysFile> vehicleLicense;


    /**
     * 征信
     */
    @TableField(exist = false)
    private List<SysFile> credit;


    /**
     * 房本
     */
    @TableField(exist = false)
    private List<SysFile> premisesPermit;


    /**
     * 头像
     */
    @TableField(exist = false)
    private SysFile headimgurlFile;


    /**
     * 是否认证
     */
    private  String authentication;


    @TableField(exist = false)
    private boolean showWxNumber;


    /**
     * 是否显示页面充值按钮
     */
    @TableField(exist = false)
    private boolean showbutton;

}
