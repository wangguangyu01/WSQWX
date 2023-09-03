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
    * 通知通告发送记录
    */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "oa_notify_record")
public class OaNotifyRecord {
    /**
     * 编号
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 通知通告ID
     */
    @TableField(value = "notify_id")
    private Long notifyId;

    /**
     * 接受人
     */
    @TableField(value = "user_id")
    private Long userId;

    /**
     * 阅读标记
     */
    @TableField(value = "is_read")
    private Boolean isRead;

    /**
     * 阅读时间
     */
    @TableField(value = "read_date")
    private Date readDate;
}