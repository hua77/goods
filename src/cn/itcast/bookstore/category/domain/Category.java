package cn.itcast.bookstore.category.domain;

import java.util.List;

public class Category {
	private String cid;//主键
	private String cname;//分类名称
	private String desc;//分类描述
	
	// 只有2级分类会有这个属性值
	private Category parent;//对应pid，pid是外键，它是谁的外键，那么我们就给出谁对应的类型
	// 只有1级分类会有这个属性值
	private List<Category> children;//当前分类的所有子分类
	public String getCid() {
		return cid;
	}
	public void setCid(String cid) {
		this.cid = cid;
	}
	public String getCname() {
		return cname;
	}
	public void setCname(String cname) {
		this.cname = cname;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public Category getParent() {
		return parent;
	}
	public void setParent(Category parent) {
		this.parent = parent;
	}
	public List<Category> getChildren() {
		return children;
	}
	public void setChildren(List<Category> children) {
		this.children = children;
	}
}
