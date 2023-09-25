package com.tencent.wxcloudrun.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class WxMenuVo implements Serializable {
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

}
