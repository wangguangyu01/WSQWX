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
 * 微信滚动广告管理
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "wx_banner")
public class WxBanner {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 名称
     */
    @TableField(value = "banner_nanme")
    private String bannerNanme;

    /**
     * 类型：
     * 1：精彩时刻
     * 2：最新活动
     * 3：会员
     * 4：广告
     * 5：相关合作
     */
    @TableField(value = "banner_type")
    private String bannerType;

    /**
     * 关联外键
     */
    @TableField(value = "banner_content_id")
    private String bannerContentId;

    /**
     * 排序
     */
    @TableField(value = "banner_oder")
    private Long bannerOder;

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

    /**
     * 创建人id
     */
    @TableField(value = "create_user_id")
    private Integer createUserId;


    @TableField(exist = false)
    private String bannerImagPath;
}
