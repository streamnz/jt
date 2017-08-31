package com.jt.manage.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jt.common.vo.EasyUIResult;
import com.jt.common.vo.SysResult;
import com.jt.manage.pojo.Item;
import com.jt.manage.pojo.ItemDesc;
import com.jt.manage.service.ItemService;

@Controller
@RequestMapping("/item")
public class ItemController {
	//引入日志对象
	private static final Logger log = Logger.getLogger(ItemController.class);
	
	@Autowired
	private ItemService itemService;
	
	
	
	/***
	 * http://localhost:8091/item/query?page=1&rows=20
	 * 
	 * {"total":2000,"rows":[{},{},{}]} easyUI分页格式要求
	 * 所以采用面向对象的方式进行展现 配置@ResponseBody
	 * @return
	 */
	@RequestMapping("/query")
	@ResponseBody //将集合或对象转化为JSON串  不经过视图解析器
	public EasyUIResult findItemList(int page,int rows){
		
	/*	List<Item> itemList = itemService.findItemList();
		ObjectMapper objectMapper = new ObjectMapper();
		String json = objectMapper.writeValueAsString(itemList);*/
		
		return  itemService.findItemList(page,rows);
	}
	
	
	//根据商品分类id查询分类名称
	/*@RequestMapping("/cat/queryItemName")
	public void findItemCatName(Long itemCatId,HttpServletResponse response) throws IOException{
		String itemCatName = itemService.findItemCatName(itemCatId);
		
		response.setContentType("text/html;charset=utf-8");
		response.getWriter().write(itemCatName);
	}*/
	
	@RequestMapping(value="/cat/queryItemName",
			produces="text/html;charset=utf-8")
	@ResponseBody
	public String findItemCatName(Long itemCatId) throws IOException{
		
		return itemService.findItemCatName(itemCatId);
		
	}
	
	
	//商品新增
	@RequestMapping("/save")
	@ResponseBody
	public SysResult saveItem(Item item,String desc){
		
		try {
			itemService.saveItem(item,desc);
			log.info("{新增商品成功}");
			return SysResult.build(200, "新增成功");
		} catch (Exception e) {
			log.error("{"+e.getMessage()+"}");
			return SysResult.build(201, "新增失败");
		}

	}
	
	
	//商品的修改
	@RequestMapping("/update")
	@ResponseBody
	public SysResult updateItem(Item item,String desc){
		try {
			
			itemService.updateItem(item,desc);
			log.info("~~~~~~~~~~~商品修改成功");
			return SysResult.build(200, "商品修改成功");
			
		} catch (Exception e) {
			log.error(e.getMessage());
			return SysResult.build(201, "商品修改失败");
		}
	}
	
	@RequestMapping("/delete")
	@ResponseBody
	public SysResult deleteItems(Long[] ids){
		try {
			
			itemService.deleteItems(ids);
			log.info("~~~~~~~~~~~商品删除成功");
			return SysResult.build(200, "商品删除成功");
			
		} catch (Exception e) {
			log.error(e.getMessage());
			return SysResult.build(201, "商品删除失败");
		}
	}
	
	
	//商品的上架
	@RequestMapping("/reshelf")
	@ResponseBody
	public SysResult reshelfItems(Long[] ids){
		try {
			int status = 1;  //上架
			itemService.updateStatus(ids,status);
			log.info("~~~~~~~~~~~商品上架成功");
			return SysResult.build(200, "商品上架成功");
			
		} catch (Exception e) {
			log.error(e.getMessage());
			return SysResult.build(201, "商品上架失败");
		}
	}
	
	//商品的下架   /item/instock
	@RequestMapping("/instock")
	@ResponseBody
	public SysResult instockItems(Long[] ids){
		try {
			int status = 2;  //下架
			itemService.updateStatus(ids,status);
			log.info("~~~~~~~~~~~商品下架成功");
			return SysResult.build(200, "商品下架成功");
			
		} catch (Exception e) {
			log.error(e.getMessage());
			return SysResult.build(201, "商品下架失败");
		}
	}
	
	//查询商品描述信息
	@RequestMapping("/query/item/desc/{itemId}")
	@ResponseBody  
	/* 返回后的json串
	 * {status:200,msg:ok,data:{itemId:123,itemDesc:"html语句"}}  
	 */
	public SysResult findItemDesc(@PathVariable Long itemId){
		
		try {
			ItemDesc itemDesc = itemService.findItemDesc(itemId);
			log.info("~~~~~~~~~~~商品描述信息查询成功");
			return SysResult.oK(itemDesc);   
      //功能和 SysResult build(Integer status, String msg, Object data)相同
			
		} catch (Exception e) {
			log.error(e.getMessage());
			return SysResult.build(201, "查询失败");
		}
	}
	
	
	
	
	
	
}
