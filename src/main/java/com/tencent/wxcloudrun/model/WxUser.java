package com.tencent.wxcloudrun.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "wx_user")
public class WxUser {
    /**
     * 微信openid
     */
    @TableId(value = "openid", type = IdType.INPUT)
    private String openid;

    /**
     * appid
     */
    @TableField(value = "appid")
    private String appid;

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
    private Byte sex;

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
    private String personprofile;

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
}