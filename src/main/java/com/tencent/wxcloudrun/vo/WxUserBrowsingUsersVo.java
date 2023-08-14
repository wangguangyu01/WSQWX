package com.tencent.wxcloudrun.vo;

import com.tencent.wxcloudrun.model.WxUser;
import lombok.Data;

@Data
public class WxUserBrowsingUsersVo extends WxUser {


    private String browsingType;
}
