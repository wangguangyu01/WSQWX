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

/**
    * 微信浏览信息
    */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "wx_browsing_users")
public class WxBrowsingUsers {
    /**
     * 主键
     */
    @TableId
    private Long id;

    /**
     * 登录用户的openid
     */
    @TableField(value = "login_open_id")
    private String loginOpenId;

    /**
     * 被浏览人openid
     */
    @TableField(value = "browsing_users_openid")
    private String browsingUsersOpenid;

    /**
     * 浏览类型
     *   0: 普通白漂用户
         1：认证用户
         2：单独付费浏览

     */
    @TableField(value = "browsing_type")
    private String browsingType;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private Date createTime;
}
