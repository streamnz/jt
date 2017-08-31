package com.jt.manage.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jt.manage.service.ItemService;

@Controller
public class FindCountController {
	
	@Autowired
	private ItemService itemService;
	
	@RequestMapping("/findCount")
	@ResponseBody
	public int getCountNum(){
		return itemService.findCountTable();
	}
}
