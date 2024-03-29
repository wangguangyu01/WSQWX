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
    * 文件上传
    */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "sys_file")
public class SysFile {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     *  文件类型,
     *     1:内容附件;
     *     2:收款二维码
     *     3：微信二维码
     *     4: 个人图片,可能是二维码，也有可能是个人照片
     *     5: 身份证(正反面)
     *     6、收入证明
     *     7、学历证明
     *     8、行车证
     *     9、征信
     *     10、房本
     *     11、微信头像
     */
    @TableField(value = "type")
    private Integer type;

    /**
     * 腾讯返回的URL地址
     */
    @TableField(value = "url")
    private String url;

    /**
     * 腾讯上传文件返回的文件id
     */
    @TableField(value = "file_id")
    private String fileId;

    /**
     * 文章id，blog_content主键
     */
    @TableField(value = "content_id")
    private String contentId;

    /**
     * 腾讯显示文件链接的开始请求的时间
     */
    @TableField(value = "request_time")
    private Date requestTime;

    /**
     * 腾讯显示文件链接过期时间
     */
    @TableField(value = "expire_time")
    private Date expireTime;

    /**
     * 创建时间
     */
    @TableField(value = "create_date")
    private Date createDate;
}
