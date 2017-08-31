package com.jt.manage.controller;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.jt.common.vo.PicUploadResult;

@Controller
public class FileUploadController {
	
	private static final Logger log = Logger.getLogger(FileUploadController.class);
	/**
	 * JSON串的回显要求
	 * 	 {"error":0,"url":"图片的保存路径","width":图片的宽度,"height":图片的高度}
	 * 文件上传的思路:
	 * 	1.判断是否为一个图片
	 *  2.判断后缀是否为.jpg|png|gif
	 *  3.通过特定的方法转化为图片的流
	 *  4.获取高度和宽度
	 *  5.将文件进行写盘操作      磁盘真实路径D:\\jt-upload/2017/08/22/15/文件名称
	 *  
	 *  lg:https://img14.360buyimg.com//fuwu/jfs/t2308/22/1900521573/1313/a055eace/567cb057N77e247ca.png
	 *  6.准备页面回显的url  页面请求路径  http://image.jt.com/2017/08/22/15/文件名称
	 *  7.准备2个路径 
	 *  	1.磁盘路径:D:\\jt-upload/2017/08/22/15/文件名称
	 *      2.页面url:http://image.jt.com/2017/08/22/15/文件名称
	 *  8.写盘操作.
	 *  9.返回对象
	 */
	@RequestMapping("/pic/upload")
	@ResponseBody
	public PicUploadResult fileUpload(MultipartFile uploadFile){
		PicUploadResult result = new PicUploadResult();
		
		//1.获取图片路径       123.jpg
		String fileName = uploadFile.getOriginalFilename();
		
		//2.获取文件的类型        获取   .jpg
		String typeName = fileName.substring(fileName.lastIndexOf("."));
		
		//3.判断是否为图片类型
		if(!typeName.matches("^\\.(jpg|png|gif)$")){
			//证明不是图片类型
			result.setError(1);
			return result;
		}
		
		//4.将文件转化为图片的流
		try {
			BufferedImage bufferedImage = 
					ImageIO.read(uploadFile.getInputStream());
			String width = bufferedImage.getWidth()+"";
			String height = bufferedImage.getHeight()+"";
			result.setHeight(height);
			result.setWidth(width);
			
			//定义真实路径   D:\\jt-upload/2017/08/22/15
			String localPath = "D:/jt-upload/";
			
			//页面请求的url
			String url = "http://image.jt.com/";
			
			String datePath = 
			new SimpleDateFormat("yyyy/MM/dd/HH").format(new Date());
			
			//拼接路径  磁盘路径    D:\\jt-upload/2017/08/22/15
			localPath = localPath + datePath;
			
			//页面路径: http://image.jt.com/2017/08/22/15/文件名称
			url  = url + datePath + "/" + fileName;
			
			//页面中要求含有Url ,通过浏览器请求
			result.setUrl(url);
			
			//将文件写入磁盘
			File file = new File(localPath);
			
			//判断是否有该文件夹
			if(!file.exists()){
				file.mkdirs();  //创建文件夹
			}
			
			uploadFile.transferTo(new File(localPath+"/"+fileName));
			
			log.info("{文件写入成功"+localPath+"/"+fileName+"}");
			
			/**
			 * 一个小时内可会出现名称一致的现象
			 * 文件名称 拼接当前时间毫秒数+"UUID"/"随机数"/哈希值
			 */
			
		} catch (IOException e) {
			//是一个恶意程序
			log.error("{"+e.getMessage()+"不是图片}");
			e.printStackTrace();
			result.setError(1);
			return result;
		}
		
		return result;
	}
	
	
	
	
	
	
	
	
	
	
	
	

}
