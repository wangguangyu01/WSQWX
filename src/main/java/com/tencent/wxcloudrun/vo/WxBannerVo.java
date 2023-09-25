package com.tencent.wxcloudrun.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WxBannerVo implements Serializable {

    /**
     * 名称
     */
    private String bannerNanme;

    /**
     * 类型：
     * 1：精彩时刻
     * 2：最新活动
     * 3：会员
     * 4：广告
     * 5：相关合作
     */
    private String bannerType;

    /**
     * 关联外键
     */
    private String bannerContentId;

    /**
     * 排序
     */
    private Long bannerOder;


    /**
     * 导航图片地址
     */
    private String bannerImagPath;
}
