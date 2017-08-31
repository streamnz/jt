package com.jt.manage.service;

import java.util.List;

import com.jt.common.vo.ItemCatResult;
import com.jt.manage.pojo.ItemCat;

public interface ItemCatService {
	//根据父级ID,查询所有子级分类列表信息
	List<ItemCat> findItemCatList(Long parentId);
	
	public ItemCatResult getItemCatResult();
}
