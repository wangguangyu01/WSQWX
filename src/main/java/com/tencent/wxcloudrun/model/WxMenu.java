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
 * 微信小程序首页导航
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "wx_menu")
public class WxMenu {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 主页名称
     */
    @TableField(value = "wx_menu_name")
    private String wxMenuName;

    /**
     * 导航链接
     */
    @TableField(value = "wx_menu_url")
    private String wxMenuUrl;

    /**
     * 排序
     */
    @TableField(value = "wx_menu_order")
    private Long wxMenuOrder;

    /**
     * 菜单类型
     * 1: tabBar
     * 2：普通页面
     */
    @TableField(value = "wx_menu_type")
    private String wxMenuType;

    /**
     * 创建人
     */
    @TableField(value = "create_user_id")
    private Long createUserId;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private Date createTime;

    /**
     * 修改时间
     */
    @TableField(value = "update_time")
    private Date updateTime;

    @TableField(value = "wx_imag_url")
    private String wxImagUrl;


    @TableField(value = "wx_menu_show")
    private String wxMenuShow;
}
