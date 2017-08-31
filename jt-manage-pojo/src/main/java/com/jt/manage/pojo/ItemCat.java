package com.jt.manage.pojo;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jt.common.po.BasePojo;

@Table(name="tb_item_cat")
@JsonIgnoreProperties(ignoreUnknown=true)  //如果发现未知属性,将不做任何处理 忽略
public class ItemCat extends BasePojo{
	/**
	 * 1.商品分类一般分为3级\
	 * 2.页面的格式要求:
	 * 	 {"id":2,"text":"商品名",state:"closed"}    节点状态,“open”或“closed”,默认是“open”。
	 */
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;  //商品分类ID
	private Long parentId; //上级分类Id
	private String name;   //商品分类名称
	private Integer status; //商品状态  可选值：1正常，2删除
	private Integer sortOrder; //商品分类排序号
	private Boolean isParent;  //是否为父级   如果是父级 则为1,如果不是父级则为0
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getParentId() {
		return parentId;
	}
	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public Integer getSortOrder() {
		return sortOrder;
	}
	public void setSortOrder(Integer sortOrder) {
		this.sortOrder = sortOrder;
	}
	public Boolean getIsParent() {
		return isParent;
	}
	public void setIsParent(Boolean isParent) {
		this.isParent = isParent;
	}
	
	/**
	 * 为了满足EasyUI的格式要求 添加getXXX()
	 */
	//1.获取商品分类名称
	public String getText(){
		return this.name;
	}
	
	//2.表示当前节点是否为父级   节点状态,“open”或“closed”,默认是“open”。
	//如果为父级 节点状态应该是closed,如果不是父级应该open
	public String getState(){	
		return isParent ? "closed" : "open";
	}
	
	
}
