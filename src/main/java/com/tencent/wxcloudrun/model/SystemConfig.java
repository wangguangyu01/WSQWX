package com.tencent.wxcloudrun.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "system_config")
public class SystemConfig {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField(value = "sys_config_key")
    private String sysConfigKey;

    @TableField(value = "sys_config_value")
    private String sysConfigValue;
}