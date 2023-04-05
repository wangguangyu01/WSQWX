package com.tencent.wxcloudrun.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tencent.wxcloudrun.model.Counter;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;


public interface CountersMapper extends BaseMapper<Counter> {

  Counter getCounter(@Param("id") Integer id);

  void upsertCount(Counter counter);

  void clearCount(@Param("id") Integer id);
}
