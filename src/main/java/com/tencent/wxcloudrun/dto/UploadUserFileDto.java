package com.tencent.wxcloudrun.dto;

import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Data
@ToString
public class UploadUserFileDto {


    private String openId;

    /**
     * 腾讯服务器中的图片id
     */
    private String fileid;


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
     */
    private Integer type;
}
