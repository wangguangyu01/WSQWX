package com.tencent.wxcloudrun.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tencent.wxcloudrun.model.WxActivity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface WxActivityMapper extends BaseMapper<WxActivity> {

    List<WxActivity> queryWxActivityList(@Param("activityUuid") String activityUuid);
}
