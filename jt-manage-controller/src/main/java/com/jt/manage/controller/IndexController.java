package com.jt.manage.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexController {
	
	/*@RequestMapping("/index")
	public String index(){
		
		//返回的页面名称
		return "index";
	}
	
	//跳转到商品新增页面
	@RequestMapping("/page/item-add")
	public String addItem(){
		
		return "item-add";
	}
	
	
	@RequestMapping("/page/item-list")
	public String itemList(){
		return "item-list";
	}*/
	
	//页面的通用跳转
	@RequestMapping("/page/{moduleName}")
	public String toModule(@PathVariable String moduleName){
		return moduleName;
	}
	
}
