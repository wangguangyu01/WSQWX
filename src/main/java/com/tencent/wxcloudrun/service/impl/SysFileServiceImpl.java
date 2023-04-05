package com.tencent.wxcloudrun.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tencent.wxcloudrun.dao.BlogContentMapper;
import com.tencent.wxcloudrun.dao.SysFileMapper;
import com.tencent.wxcloudrun.model.SysFile;
import com.tencent.wxcloudrun.service.SysFileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@Slf4j
public class SysFileServiceImpl implements SysFileService {


    final SysFileMapper sysFileMapper;

    public SysFileServiceImpl(SysFileMapper sysFileMapper) {
        this.sysFileMapper = sysFileMapper;
    }


    @Override
    public int saveFile(SysFile sysFile) {
        return sysFileMapper.insert(sysFile);
    }


    @Override
    public List<SysFile> queryFile(String contentId, Integer type) {
        LambdaQueryWrapper<SysFile> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysFile::getContentId, contentId);
        queryWrapper.eq(SysFile::getType, type);
        return sysFileMapper.selectList(queryWrapper);
    }


    @Override
    public int updateFile(SysFile sysFile) {
        return sysFileMapper.updateById(sysFile);
    }


    @Override
    public SysFile queryFileOneByFileId(String fileId) {
        LambdaQueryWrapper<SysFile> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysFile::getFileId, fileId);
        return sysFileMapper.selectOne(queryWrapper);
    }
}
