package com.tencent.wxcloudrun.service;



import com.tencent.wxcloudrun.dto.FileRequestDto;
import com.tencent.wxcloudrun.dto.FileResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 附件表
 *
 * @author liangsl
 * @email 1992lcg@163.com
 * @date 2020-12-31 15:45:15
 */
public interface AttachmentService {


	/**
	 * 获取微信token
	 * @return 返回token
	 */
	public String weixinToken();


	/**
	 * 上传到微信的存储对象服务器上，
	 * @param path 上传的文件路径
	 * @return 返回是文件的id
	 */
	public String weixinUpload(String path) throws IOException;


	/**
	 * 上传到微信的存储对象服务器上，
	 * @param file 上传的文件
	 * @return 返回是文件的id
	 */
	public String weixinUpload(MultipartFile file) throws IOException;


	/**
	 * 下载图片
	 * @param fileRequestDtos
	 * @return 返回图片显示链接
	 */
	public List<FileResponseDto> batchDownloadFile(List<FileRequestDto> fileRequestDtos);



	/**
	 * 批量删除图片
	 * @param fileid_list 腾讯服务器图片fileId
	 * @return 返回图片显示链接
	 */
	public List<String> batchDeleteFile(List<String> fileid_list);
}
