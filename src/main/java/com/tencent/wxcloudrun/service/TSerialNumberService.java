package com.tencent.wxcloudrun.service;

import com.tencent.wxcloudrun.model.TSerialNumber;
import com.baomidou.mybatisplus.extension.service.IService;
public interface TSerialNumberService extends IService<TSerialNumber>{


    /**
     * 创建编号
     * @return
     */
    public String createSerialNumber();
}
