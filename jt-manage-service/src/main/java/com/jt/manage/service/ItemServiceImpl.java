package com.jt.manage.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jt.common.vo.EasyUIResult;
import com.jt.manage.mapper.ItemDescMapper;
import com.jt.manage.mapper.ItemMapper;
import com.jt.manage.pojo.Item;
import com.jt.manage.pojo.ItemDesc;
@Service
public class ItemServiceImpl implements ItemService{
	
	@Autowired
	private ItemMapper itemMapper;
	
	@Autowired
	private ItemDescMapper itemDescMapper;
	/**
	 * 参数:
	 * 	page:查询的页数
	 *  rows:查询记录数
	 */
	@Override
	public EasyUIResult findItemList(int page, int rows) {
		// 1页   20条数据     SELECT * FROM tb_item LIMIT 0,20
		// 2页   20条数据     SELECT * FROM tb_item LIMIT 20,20
		// 3页   20条数据     SELECT * FROM tb_item LIMIT 40,20
		// n页   20条数据     SELECT * FROM tb_item LIMIT (n-1)*20,20
		
		//查询商品列表
		/*int start = (page-1)*rows;
		List<Item> itemList = itemMapper.findItemList(start,rows);
		
		//查询商品总数
		int total = itemMapper.findItemCount();*/
		
		
		//使用分页插件设定参数
		PageHelper.startPage(page, rows);
		//分页插件要求紧跟着写查询操作    并且查询的是全部的数据信息
		//使用分页插件时,已经实现了自动的分页查询,itemList的数据已经完成了分页操作
		List<Item> itemList = itemMapper.findPageItemList();
		
		//显示分页后的计算页数
		PageInfo<Item> info = new PageInfo<Item>(itemList);
		
		
		EasyUIResult result = 
				new EasyUIResult(info.getTotal(), itemList);
		return result;
	}


	@Override
	public String findItemCatName(Long itemCatId) {
		
		return itemMapper.findItemCatName(itemCatId);
	}


	@Override
	public int findCountTable() {
		
		return itemMapper.findCountTable();
	}


	@Override
	public void saveItem(Item item,String desc) {
		
		//itemMapper.insert("对象")  将对象的全部属性都会进行插入操作
		//itemMapper.insertSelective("对象")  将对象中不为null的值 执行插入操作
		
		item.setStatus(1); //1表示正常
		item.setCreated(new Date());
		item.setUpdated(item.getCreated());
		//通过通用mapper实现新增操作
		itemMapper.insertSelective(item);
		
		/**
		 * 说明:商品新增时,需要同时插入2张表 tb_item tb_item_desc
		 * 新增Item时  tb_item表中的主键id是自增的
		 * 但是插入tb_item_desc数据时 必须拿到 itemId才可以,,这样才能保证数据的一致性
		 * 问题:id只有做完新增操作时 才能获取
		 * 说明:通用mapper在执行完新增操作时,或执行查询操作,自动封装数据到Item中
		 * 
		 */
		ItemDesc itemDesc = new ItemDesc();
		itemDesc.setItemId(item.getId());
		itemDesc.setItemDesc(desc);
		itemDesc.setCreated(item.getCreated());
		itemDesc.setUpdated(item.getUpdated());
		itemDescMapper.insertSelective(itemDesc);
	}

	@Override
	public void updateItem(Item item, String desc) {
		item.setUpdated(new Date());
		itemMapper.updateByPrimaryKeySelective(item);
		
		ItemDesc itemDesc = new ItemDesc();
		itemDesc.setItemId(item.getId());
		itemDesc.setItemDesc(desc);
		itemDesc.setUpdated(item.getUpdated());
		itemDescMapper.updateByPrimaryKeySelective(itemDesc);
	}
	
	


	@Override
	public void deleteItems(Long[] ids) {
		
		itemDescMapper.deleteByIDS(ids);
		itemMapper.deleteByIDS(ids);
		
	}


	@Override
	public void updateStatus(Long[] ids, int status) {
		
		itemMapper.updateStatus(ids,status);
		
	}


	@Override
	public ItemDesc findItemDesc(Long itemId) {
		
		return itemDescMapper.selectByPrimaryKey(itemId);
	}




}
