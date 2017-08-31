package com.jt.manage.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.jt.common.mapper.SysMapper;
import com.jt.manage.pojo.Item;

public interface ItemMapper extends SysMapper<Item>{
	
	public List<Item> findItemList(@Param("start") int start,@Param("rows") int rows);
	
	//查询商品总数
	public int findItemCount();
	
	//查询商品分类名称
	public String findItemCatName(Long itemCatId);
	
	//查询全部的商品
	public List<Item> findPageItemList();
	
	//批量修改状态
	public void updateStatus(@Param("ids") Long[] ids,@Param("status") int status);
}	
