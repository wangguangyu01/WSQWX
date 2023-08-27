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
@TableName(value = "wx_template_msg_log")
public class WxTemplateMsgLog {
    /**
     * ID
     */
    @TableId(value = "log_id", type = IdType.AUTO)
    private Long logId;

    /**
     * appid
     */
    @TableField(value = "appid")
    private String appid;

    /**
     * 用户openid
     */
    @TableField(value = "touser")
    private String touser;

    /**
     * templateid
     */
    @TableField(value = "template_id")
    private String templateId;

    /**
     * 订阅消息模板类型：
1: 会员审核提醒
2: 会员审核结果提醒
3: 参加活动提醒
     */
    @TableField(value = "template_type")
    private Integer templateType;

    /**
     * 消息数据
     */
    @TableField(value = "data")
    private String data;

    /**
     * 消息链接
     */
    @TableField(value = "url")
    private String url;

    /**
     * 小程序信息
     */
    @TableField(value = "miniprogram")
    private String miniprogram;

    /**
     * 发送时间
     */
    @TableField(value = "send_time")
    private Date sendTime;

    /**
     * 发送结果
0:成功
1:失败
     */
    @TableField(value = "send_result")
    private String sendResult;
}
