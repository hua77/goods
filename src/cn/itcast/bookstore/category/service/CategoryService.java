package cn.itcast.bookstore.category.service;

import java.sql.SQLException;
import java.util.List;

import cn.itcast.bookstore.book.dao.BookDao;
import cn.itcast.bookstore.category.dao.CategoryDao;
import cn.itcast.bookstore.category.domain.Category;

public class CategoryService {
	private CategoryDao categoryDao = new CategoryDao();
	private BookDao bookDao = new BookDao();
	
	public List<Category> findAll() {
		try {
			return categoryDao.findAll();
		} catch(SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 添加分类
	 * @param category
	 */
	public void add(Category category) {
		try {
			categoryDao.add(category);
		} catch(SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 加载所有1级分类
	 * @return
	 */
	public List<Category> loadParents() {
		try {
			return categoryDao.loadParents();
		} catch(SQLException e) {
			throw new RuntimeException(e);
		}		
	}
	
	/**
	 * 加载分类
	 * @param cid
	 * @return
	 */
	public Category load(String cid) {
		try {
			return categoryDao.load(cid);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 编辑分类
	 * @param category
	 */
	public void edit(Category category) {
		try {
			categoryDao.edit(category);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 删除一级分类
	 * @param pid
	 * @return
	 */
	public boolean deleteOneLevel(String pid) {
		try {
			int cnt = categoryDao.findCountByParent(pid);//获取该一级分类下的二级分类个数
			if(cnt > 0) return false;//如果该一级分类下存在二级分类，那么不能删除
			categoryDao.delete(pid);
			return true;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}		
	}
	
	/**
	 * 删除二级分类
	 * @param pid
	 * @return
	 */
	public boolean deleteTwoLevel(String cid) {
		try {
			int cnt = bookDao.findCountByCategory(cid);//获取图书个数
			if(cnt > 0) return false;//如果该分类下存在图书，不能删除
			categoryDao.delete(cid);//删除图书
			return true;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}		
	}
	
	/**
	 * 加载指定1级分类的所有2级分类
	 * @param cid
	 * @return
	 */
	public List<Category> loadChildren(String pid) {
		try {
			return categoryDao.loadChildren(pid);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}		
	}
}
