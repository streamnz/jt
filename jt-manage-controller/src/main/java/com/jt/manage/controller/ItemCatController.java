package com.jt.manage.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jt.manage.pojo.ItemCat;
import com.jt.manage.service.ItemCatService;

@Controller
@RequestMapping("/item")
public class ItemCatController {
	
	@Autowired
	private ItemCatService itemCatService;
	/**
	 * {"id":2,"text":"商品名",state:"closed"}
		注:state的属性如果是closed，表示这个是父节点，它还有子节点。open代表子节点
	 *  EasyUI要求数据返回的JSON格式
	 *  
	 *  id属性是EasyUI 传递的节点信息编号  该id充当商品分类查询的父级Id
	 *  @RequestParam(value="id",defaultValue="0")
	 *  实现id的转化赋值给parentId,并且如果是一级标题 的父类为0进行查询
	 * @return
	 */
	@RequestMapping("/cat/list")
	@ResponseBody
	public List<ItemCat> findItemCatList
	(@RequestParam(value="id",defaultValue="0") Long parentId){
		
		return itemCatService.findItemCatList(parentId);
	}
	
	
	
	
	
	
	
	
}
