package com.jt.manage.service;

import java.util.List;

import com.jt.common.vo.EasyUIResult;
import com.jt.manage.pojo.Item;
import com.jt.manage.pojo.ItemDesc;

public interface ItemService {
	public EasyUIResult findItemList(int page, int rows);
	
	//查询分类名称
	public String findItemCatName(Long itemCatId);
	
	//通用mapper的测试案例  查询记录总数
	public int findCountTable();
	
	//新增商品和商品描述信息
	public void saveItem(Item item, String desc);
	
	//修改商品和描述信息
	public void updateItem(Item item, String desc);
	
	//商品删除
	public void deleteItems(Long[] ids);
	
	//商品的状态修改
	public void updateStatus(Long[] ids, int status);
	
	//查询商品描述信息
	public ItemDesc findItemDesc(Long itemId);
}
