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
@TableName(value = "wx_msg_template")
public class WxMsgTemplate {
    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * appid
     */
    @TableField(value = "appid")
    private String appid;

    /**
     * 公众号模板ID
     */
    @TableField(value = "template_id")
    private String templateId;

    /**
     * 订阅消息模板类型：
     * 1: 会员审核提醒
     * 2: 会员审核结果提醒
     * 3: 参加活动提醒
     */
    @TableField(value = "template_type")
    private Integer templateType;

    /**
     * 模版名称
     */
    @TableField(value = "name")
    private String name;

    /**
     * 标题
     */
    @TableField(value = "title")
    private String title;

    /**
     * 模板内容
     */
    @TableField(value = "content")
    private String content;

    /**
     * 消息内容
     */
    @TableField(value = "data")
    private String data;

    /**
     * 链接
     */
    @TableField(value = "url")
    private String url;

    /**
     * 小程序信息
     */
    @TableField(value = "miniprogram")
    private String miniprogram;

    /**
     * 是否有效
     */
    @TableField(value = "status")
    private Integer status;

    /**
     * 修改时间
     */
    @TableField(value = "update_time")
    private Date updateTime;
}
