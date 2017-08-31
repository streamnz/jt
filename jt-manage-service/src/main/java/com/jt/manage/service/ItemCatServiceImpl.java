package com.jt.manage.service;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jt.common.vo.ItemCatData;
import com.jt.common.vo.ItemCatResult;
import com.jt.manage.mapper.ItemCatMapper;
import com.jt.manage.pojo.ItemCat;

import redis.clients.jedis.JedisCluster;

@Service
public class ItemCatServiceImpl implements ItemCatService {
	
	@Autowired
	private ItemCatMapper itemCatMapper;
	
/*	@Autowired
	private ShardRedisService shardRedisService;*/
	
	@Autowired
	private JedisCluster jedisCluster;
	
	private static final Logger log = Logger.getLogger(ItemCatServiceImpl.class);
	/**
	 * 如何通过缓存查询数据信息
	 * 1.应该先从redis中查询数据      
	 * 	 有数据:获取的是字符串   
	 * 2.如果有数据应该将JSON串转化List<ItemCat>   
	 * 3.如果没有数据,则从数据库中查询,之后存入缓存中
	 */
	@Override
	public List<ItemCat> findItemCatList(Long parentId) {
		List<ItemCat> itemCatList = new ArrayList<ItemCat>();
		
		ObjectMapper objectMapper = new ObjectMapper();
		
		String ItemCatkey = "ITEM_CAT_"+parentId;
		
		//从缓存中获取数据
	/*	String value = shardRedisService.getKey(ItemCatkey);*/
		String value =jedisCluster.get(ItemCatkey);
		//判断数据是否为空   [{id:i,name:18},{},{}]
		if(StringUtils.isNotEmpty(value)){
			try {
				//JSon串如何转为List集合
				ItemCat[] itemCats = objectMapper.readValue(value, ItemCat[].class);
				for (ItemCat itemCat : itemCats) {
					itemCatList.add(itemCat);
				}
				
			} catch (IOException e) {
				log.error(e.getMessage());
				e.printStackTrace();
			}
		}else {
			//表示数据为空,缓存中没有数据

			/**
			 * 通过Mapper解析时 自动拼接不为null的数据为  
			 * sql: select * from tb_item_cat where parent_id = XXXX
			 */
			ItemCat itemCat = new ItemCat();
			itemCat.setParentId(parentId);
			itemCatList = itemCatMapper.select(itemCat);
			String itemCatJson;
			try {
				itemCatJson = objectMapper.writeValueAsString(itemCatList);
			/*	shardRedisService.setKey(ItemCatkey, itemCatJson);*/
				jedisCluster.set(ItemCatkey, itemCatJson);
			} catch (JsonProcessingException e) {
				log.error(e.getMessage());
				e.printStackTrace();
			}
			
		}
		
		return itemCatList;
				
	}
	
	
	//获取前台商品分类菜单
	@Override
	public ItemCatResult getItemCatResult(){
		ItemCatResult result = new ItemCatResult();
		//1.获取某个级菜单，它的所有的子菜单
		Map<Long,List<ItemCat>> map = new HashMap<Long,List<ItemCat>>();
		//循环所有的记录
		List<ItemCat> cats = itemCatMapper.select(null);
		for(ItemCat itemCat: cats){
			//key不存在
			if(!map.containsKey(itemCat.getParentId())){
				//构建结构
				map.put(itemCat.getParentId(), new ArrayList<ItemCat>());
			}
			map.get(itemCat.getParentId()).add(itemCat);
		}
		
		//2.三层循环，一级菜单，二级菜单，三级菜单
		List<ItemCatData> list1 = new ArrayList<ItemCatData>();
		//遍历一级菜单
		for(ItemCat itemCat1 : map.get(0L)){
			//一级菜单内容：url/name/items
			ItemCatData d1 = new ItemCatData();
			d1.setUrl("/products/"+itemCat1.getId()+".html");
			d1.setName("<a href=\""+d1.getUrl()+"\">"+itemCat1.getName()+"</a>");
			
			//遍历二级菜单
			List<ItemCatData> list2 = new ArrayList<ItemCatData>();
			for(ItemCat itemCat2: map.get(itemCat1.getId())){
				ItemCatData d2 = new ItemCatData();
				d2.setUrl("/products/"+itemCat2.getId()+".html");
				d2.setName(itemCat2.getName());
				
				//遍历三级菜单
				List<String> list3 = new ArrayList<String>();
				for(ItemCat itemCat3: map.get(itemCat2.getId())){
					list3.add("/products/"+itemCat3.getId()+".html|"+itemCat3.getName());
				}
				d2.setItems(list3);
				list2.add(d2);
			}
			
			d1.setItems(list2);
			list1.add(d1);
			
			result.setItemCats(list1);
			if(list1.size()>=14){
				break;	//保证主菜单位置不会超长
			}
		}
		
		return result;
	}
	
}
