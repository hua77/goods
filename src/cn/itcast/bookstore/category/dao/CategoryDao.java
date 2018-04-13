package cn.itcast.bookstore.category.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import cn.itcast.bookstore.category.domain.Category;
import cn.itcast.commons.CommonUtils;
import cn.itcast.jdbc.TxQueryRunner;

public class CategoryDao {
	private QueryRunner qr = new TxQueryRunner();
	
	// 把一个Map映射成一个Category对象
	private Category toCategory(Map<String,Object> map) {
		// map中的cid、cname、desc与Category的属性名相同！可以映射，但map中的pid没有对象
		Category category = CommonUtils.toBean(map, Category.class);
		// 我们需要手动把map中的pid赋给Category parent的cid，再把它赋值给Category的parent属性
		Category parent = new Category();// 创建父分类
		parent.setCid((String)map.get("pid"));//把pid赋值给父分类的cid
		category.setParent(parent);//给category指定父分类
		return category;
	}
	
	// 把多个Map映射成多个Category对象
	private List<Category> toCategoryList(List<Map<String,Object>> mapList) {
		List<Category> categoryList = new ArrayList<Category>();
		for(Map<String,Object> map : mapList) {
			categoryList.add(toCategory(map));
		}
		return categoryList;
	}
	
	/**
	 * 为指定的父分类加载其所有子分类
	 * @param parent
	 * @return
	 * @throws SQLException 
	 */
	public List<Category> loadChildren(String pid) throws SQLException {
		String sql = "select * from t_category where pid=? order by orderBy";
		List<Map<String,Object>> mapList = qr.query(sql, new MapListHandler(), pid);
		return toCategoryList(mapList);
	}
	
	/**
	 * 获取所有1级分类
	 * @return
	 * @throws SQLException 
	 */
	public List<Category> loadParents() throws SQLException {
		String sql = "select * from t_category where pid is null order by orderBy";
		List<Map<String,Object>> mapList = qr.query(sql, new MapListHandler());
		return toCategoryList(mapList);
	}

	/**
	 * 查询所有分类
	 * @return
	 */
	public List<Category> findAll() throws SQLException {
		/*
		 * 1. 查询所有1级分类
		 */
		String sql = "select * from t_category where pid is null order by orderBy";
		/*
		 * 把结果集映射成List<Map>，其中一个Map对应结果集的一行记录！多行记录就对象List<Map>
		 */
		List<Map<String,Object>> mapList = qr.query(sql, new MapListHandler());
		List<Category> parents = toCategoryList(mapList);
		/*
		 * 2. 循环遍历所有1级分类，为其加载2级分类
		 */
		for(Category parent : parents) {
			List<Category> children = loadChildren(parent.getCid());//为每个父分类加载其所有子分类
			parent.setChildren(children);
		}
		return parents;
	}
	
	/**
	 * 添加分类
	 * @param category
	 * @throws SQLException 
	 */
	public void add(Category category) throws SQLException {
		String sql = "insert into t_category(cid,cname,pid,`desc`) values(?,?,?,?)";
		/*
		 * 如果添加的是一级分类，那么它没有父分类
		 * 也就是说，category.getParent()是null
		 * 为了不出现空指针异常，所以...
		 */
		String pid = null;
		if(category.getParent() != null) {
			pid = category.getParent().getCid();
		}
		Object[] params = { category.getCid(), category.getCname(),
				pid, category.getDesc() };
		qr.update(sql, params);
	}
	
	/**
	 * 加载分类
	 * @param cid
	 * @return
	 * @throws SQLException
	 */
	public Category load(String cid) throws SQLException {
		String sql = "select * from t_category where cid=?";
		Map<String,Object> map = qr.query(sql, new MapHandler(), cid);
		return toCategory(map);
	}
	
	/**
	 * 编辑分类
	 * @param category
	 * @throws SQLException 
	 */
	public void edit(Category category) throws SQLException {
		String sql = "update t_category set cname=?, pid=?, `desc`=? where cid=?";
		// 如果添加的是一级分类，那么它没有父分类，这时需要小心空指针异常
		String pid = null;
		if(category.getParent() != null) {
			pid = category.getParent().getCid();
		}
		Object[] params = {category.getCname(), pid, category.getDesc(), category.getCid()};
		qr.update(sql, params);
	}
	
	/**
	 * 查询指定一级分类下二级分类的个数
	 * @param pid
	 * @return
	 * @throws SQLException 
	 */
	public int findCountByParent(String pid) throws SQLException {
		String sql = "select count(1) from t_category where pid=?";
		Number cnt = (Number)qr.query(sql, new ScalarHandler(), pid);
		return cnt == null ? 0 : cnt.intValue();
	}
	
	/**
	 * 删除分类
	 * @param cid
	 * @throws SQLException 
	 */
	public void delete(String cid) throws SQLException {
		String sql = "delete from t_category where cid=?";
		qr.update(sql, cid);
	}
}
