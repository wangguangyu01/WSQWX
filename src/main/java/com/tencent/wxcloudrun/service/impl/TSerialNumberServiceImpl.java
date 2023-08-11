package com.tencent.wxcloudrun.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tencent.wxcloudrun.utils.DateUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tencent.wxcloudrun.model.TSerialNumber;
import com.tencent.wxcloudrun.dao.TSerialNumberMapper;
import com.tencent.wxcloudrun.service.TSerialNumberService;
import org.springframework.util.ObjectUtils;

@Service
public class TSerialNumberServiceImpl extends ServiceImpl<TSerialNumberMapper, TSerialNumber> implements TSerialNumberService{

    private static final AtomicLong atomicLong = new AtomicLong(0);

    @Autowired
    private TSerialNumberMapper tSerialNumberMapper;



    @Autowired
    private RedisTemplate redisTemplate;


    @Override
    public String createSerialNumber() {

        String serialNumberStr = "";
        Date date = new Date();
        String dateStr = DateUtils.format(date, DateUtils.DATE_PATTERN);
        Date date1 = DateUtils.parseDate(dateStr, DateUtils.DATE_PATTERN);
        LambdaQueryWrapper<TSerialNumber> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TSerialNumber::getUpdateDate, date1);
        List<TSerialNumber> tSerialNumbers = tSerialNumberMapper.selectList(queryWrapper);
        TSerialNumber tSerialNumber = null;
        if (CollectionUtils.isEmpty(tSerialNumbers)) {
            tSerialNumber = new TSerialNumber();
        } else {
            tSerialNumber = tSerialNumbers.get(0);
        }
        Long incrementAndGet = null;
        try {
            RedisAtomicLong redisAtomicLong = new RedisAtomicLong("tSerialNumber_key_"+dateStr, redisTemplate.getConnectionFactory());

            if (tSerialNumber.getId() == null) {
                incrementAndGet = redisAtomicLong.incrementAndGet();
            } else {
                Long serialNumber = tSerialNumber.getSerialNumber();
                incrementAndGet = redisAtomicLong.updateAndGet(value -> serialNumber + 1);
            }
            redisTemplate.expire("tSerialNumber_key_"+dateStr, 24, TimeUnit.HOURS);
        } catch (Exception e) {
            long serialNumber = tSerialNumber.getSerialNumber();
            if (!ObjectUtils.isEmpty(serialNumber)) {
                incrementAndGet = serialNumber + 1;
            } else {
                incrementAndGet = atomicLong.incrementAndGet();
            }
        } finally {
            tSerialNumber.setSerialNumber(incrementAndGet);
            tSerialNumber.setUpdateDate(new Date());
            if (tSerialNumber.getId() == null) {
                tSerialNumberMapper.insert(tSerialNumber);
            } else {
                tSerialNumberMapper.updateById(tSerialNumber);
            }
            String incrementStr = String.format("%03d", incrementAndGet);
            serialNumberStr = dateStr + incrementStr;
            serialNumberStr = StringUtils.replaceAll(serialNumberStr, "-", "");
        }

        return serialNumberStr;
    }
}
