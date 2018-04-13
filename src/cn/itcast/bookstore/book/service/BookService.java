package cn.itcast.bookstore.book.service;

import java.sql.SQLException;

import javax.servlet.jsp.jstl.sql.SQLExecutionTag;

import cn.itcast.bookstore.book.dao.BookDao;
import cn.itcast.bookstore.book.domain.Book;
import cn.itcast.pager.PageBean;

public class BookService {
	private BookDao bookDao= new BookDao();
	
	/**
	 * 按分类查询（分页查询）
	 * @param cid
	 * @param pc
	 * @return
	 */
	public PageBean<Book> findByCatgory(String cid, int pc) {
		try {
			return bookDao.findByCategory(cid, pc);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 按作者查询（分页查询）
	 */
	public PageBean<Book> findByAuthor(String author, int pc) {
		try {
			return bookDao.findByAuthor(author, pc);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 按出版社查询（分页查询）
	 * @param press
	 * @param pc
	 * @return
	 */
	public PageBean<Book> findByPress(String press, int pc) {
		try {
			return bookDao.findByPress(press, pc);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 按图名查询（分页查询）
	 * @param bname
	 * @param pc
	 * @return
	 */
	public PageBean<Book> findByBname(String bname, int pc) {
		try {
			return bookDao.findByBname(bname, pc);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 多条件组合查询（分页查询）
	 * @param book
	 * @param pc
	 * @return
	 */
	public PageBean<Book> findByCombination(Book book, int pc) {
		try {
			return bookDao.findByCombination(book, pc);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 按bid查询
	 * @param bid
	 * @return
	 */
	public Book findByBid(String bid) {
		try {
			return bookDao.findByBid(bid);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 添加图书
	 * @param book
	 */
	public void add(Book book) {
		try {
			bookDao.add(book);
		} catch(SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 编辑图书
	 * @param book
	 */
	public void edit(Book book) {
		try {
			bookDao.edit(book);
		} catch(SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 删除图书
	 * @param bid
	 */
	public void delete(String bid) {
		try {
			bookDao.delete(bid);
		} catch(SQLException e) {
			throw new RuntimeException(e);
		}
	}
}
