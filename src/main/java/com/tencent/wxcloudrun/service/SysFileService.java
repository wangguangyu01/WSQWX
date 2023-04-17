package com.tencent.wxcloudrun.service;

import com.tencent.wxcloudrun.model.SysFile;

import java.util.List;

public interface SysFileService {


    public int saveFile(SysFile sysFile);

    /**
     * 根据文章id查询文件集合
     * @param contentId
     *            文章uuid
     * @param type
     *           附件类型：1:附件；2：收款二维码
     * @return
     */
    public List<SysFile> queryFile(String contentId, Integer type);


    /**
     * 修改文件的url地址以及url的过期时间
     * @param sysFile
     * @return
     */
    public int updateFile(SysFile sysFile);


    /**
     * 根据fileId查询记录
     * @param fileId
     * @return
     */
    public SysFile queryFileOneByFileId(String fileId);



    public int remove(String  fileId);

    public SysFile updateFileUrl(SysFile file) throws Exception;
}
