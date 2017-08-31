package com.jt.manage.controller.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jt.manage.service.ItemCatService;

@Controller	//标识这个类是为前台服务的，包路径加web，所有类前面加web。
public class WebItemCatController {
	@Autowired
	private ItemCatService itemCatService;
	
	//获取商品分类菜单，按前台要求拼成一个json格式字符串
	@RequestMapping("/web/item/cat/all")
//	@ResponseBody
//	public ItemCatResult getItemCatResult(){
//		return itemCatService.getItemCatResult();
//	}
	@ResponseBody
	public Object getItemCatResult(String callback){
		//把java对象转成jsonp，fun(json)
		MappingJacksonValue mjv = new MappingJacksonValue(itemCatService.getItemCatResult());
		mjv.setJsonpFunction(callback); //callback如果是null
		return mjv;
	}
}
