package com.tencent.wxcloudrun.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
    * 文章内容
    */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "blog_content")
public class BlogContent {


    /**
     * 业务主键
     */
    @TableId(value = "uuid", type = IdType.INPUT)
    private String uuid;

    /**
     * 标题
     */
    @TableField(value = "title")
    private String title;

    @TableField(value = "slug")
    private String slug;

    /**
     * 创建人id
     */
    @TableField(value = "created")
    private Long created;

    /**
     * 最近修改人id
     */
    @TableField(value = "modified")
    private Long modified;

    /**
     * 内容
     */
    @TableField(value = "content")
    private String content;

    /**
     * 类型
     */
    @TableField(value = "type")
    private String type;

    /**
     * 标签
     */
    @TableField(value = "tags")
    private String tags;

    /**
     * 分类
     */
    @TableField(value = "categories")
    private String categories;

    @TableField(value = "hits")
    private Integer hits;

    /**
     * 评论数量
     */
    @TableField(value = "comments_num")
    private Integer commentsNum;

    /**
     * 开启评论
     */
    @TableField(value = "allow_comment")
    private Integer allowComment;

    /**
     * 允许ping
     */
    @TableField(value = "allow_ping")
    private Integer allowPing;

    /**
     * 允许反馈
     */
    @TableField(value = "allow_feed")
    private Integer allowFeed;

    /**
     * 状态
     */
    @TableField(value = "status")
    private Integer status;

    /**
     * 作者
     */
    @TableField(value = "author")
    private String author;

    /**
     * 创建时间
     */
    @TableField(value = "gtm_create")
    private Date gtmCreate;

    /**
     * 修改时间
     */
    @TableField(value = "gtm_modified")
    private Date gtmModified;


    /**
     * 附件url集合
     */
    @TableField(exist = false)
    private List<String> fileList;


    /**
     * 收款二维码
     */
    @TableField(exist = false)
    private  String moneyQRCode;


    @TableField(value = "price")
    private Integer  price;


    @TableField(exist = false)
    private double  priceDouble;


    /**
     * 是否有效
     * 0：有效
     * 1：过期
     */
    @TableField(value = "staleDated")
    private String staleDated;



}
