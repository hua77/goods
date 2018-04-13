package cn.itcast.bookstore.book.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import cn.itcast.bookstore.book.domain.Book;
import cn.itcast.bookstore.category.domain.Category;
import cn.itcast.commons.CommonUtils;
import cn.itcast.jdbc.TxQueryRunner;
import cn.itcast.pager.Expression;
import cn.itcast.pager.PageBean;
import cn.itcast.pager.PageConstance;

public class BookDao {
	private QueryRunner qr = new TxQueryRunner();
	
	/**
	 * 查询指定分类下图书个数
	 * @param cid
	 * @return
	 * @throws SQLException
	 */
	public int findCountByCategory(String cid) throws SQLException {
		String sql = "select count(1) from t_book where cid=?";
		Number cnt = (Number)qr.query(sql, new ScalarHandler(), cid);
		return cnt == null ? 0 : cnt.intValue();
	}
	
	/**
	 * 按分类查询
	 * @param cid
	 * @param pc
	 * @return
	 * @throws SQLException 
	 */
	public PageBean<Book> findByCategory(String cid, int pc) throws SQLException {
		List<Expression> exprList = new ArrayList<Expression>();
		Expression expr = new Expression("cid", "=", cid);		
		exprList.add(expr);
		
		return findByCretiera(exprList, pc);
	}
	
	/**
	 * 按作者查询
	 * @param author
	 * @param pc
	 * @return
	 * @throws SQLException
	 */
	public PageBean<Book> findByAuthor(String author, int pc) throws SQLException {
		List<Expression> exprList = new ArrayList<Expression>();
		Expression expr = new Expression("author", "=", author);		
		exprList.add(expr);
		
		return findByCretiera(exprList, pc);
	}
	
	/**
	 * 按图名查询
	 * @param bname
	 * @param pc
	 * @return
	 * @throws SQLException
	 */
	public PageBean<Book> findByBname(String bname, int pc) throws SQLException {
		List<Expression> exprList = new ArrayList<Expression>();
		Expression expr = new Expression("bname", "like", bname);	
		exprList.add(expr);
		
		return findByCretiera(exprList, pc);
	}
	
	/**
	 * 按出版社查询
	 * @param press
	 * @param pc
	 * @return
	 * @throws SQLException
	 */
	public PageBean<Book> findByPress(String press, int pc) throws SQLException {
		List<Expression> exprList = new ArrayList<Expression>();
		Expression expr = new Expression("press", "like", press);		
		exprList.add(expr);
		
		return findByCretiera(exprList, pc);
	}
	
	/**
	 * 多条件组合查询
	 * @param press
	 * @param pc
	 * @return
	 * @throws SQLException
	 */
	public PageBean<Book> findByCombination(Book book, int pc) throws SQLException {
		List<Expression> exprList = new ArrayList<Expression>();
		Expression expr = new Expression("bname", "like", book.getBname());		
		exprList.add(expr);
		
		expr = new Expression("author", "like", book.getAuthor());		
		exprList.add(expr);
		
		expr = new Expression("press", "like", book.getPress());		
		exprList.add(expr);
		
		return findByCretiera(exprList, pc);
	}
	
	/**
	 * 通过指定的条件完成分页显示
	 * 把List<Expresion>转发成where子句，然后和select * 还有select count(1)合并成一个完整的sql语句。
	 * @param exprList
	 * @param pc
	 * @return
	 * @throws SQLException
	 */
	private PageBean<Book> findByCretiera(List<Expression> exprList, int pc) throws SQLException {
		/*
		 * 创建一个PageBean对象
		 * * pc
		 * * tr
		 * * ps
		 * * beanList
		 */
		/*
		 * 1. 生成where子句
		 * 把exprList转换成sql字句！
		 * where ....
		 */
		StringBuilder whereSql = new StringBuilder(" where 1=1");//sql语句的载体
		List<Object> params = new ArrayList<Object>();//对应sql中的问号！
		for(Expression expr : exprList) {
			whereSql.append(" and ").append(expr.getName()).append(" ");
			String operator = expr.getOperator();
			if(operator.equalsIgnoreCase("like")) {
				whereSql.append(operator);
				whereSql.append(" ?");
				params.add("%" + expr.getValue() + "%");
			} else if(operator.equalsIgnoreCase("is null")) {
				whereSql.append(operator);
			} else {
				whereSql.append(operator);
				whereSql.append(" ?");
				params.add(expr.getValue());
			}
		}
		/*
		 * 2. 得到tr(总记录数)
		 *   给出select count(1) ... where ...
		 *   
		 *   select count(1) + where子句
		 */
		String sql = "select count(1) from t_book" + whereSql;
		Number cnt = (Number)qr.query(sql, new ScalarHandler(), params.toArray());
		int tr = cnt == null ? 0 : cnt.intValue();		
		/*
		 * 3. 得到当前页记录
		 * 给出select * from t_book where ... limit ...
		 */
		String orderByAndLimitSql = " order by orderBy limit ?,?";
		// ?1 = (pc-1) * ps,   ?2 = ps
		int ps = PageConstance.BOOK_PAGE_SIZE;
		params.add((pc-1) * ps);
		params.add(ps);
	
		sql = "select * from t_book" + whereSql + orderByAndLimitSql;
		List<Map<String,Object>> mapList = qr.query(sql, new MapListHandler(), params.toArray());
		
		// 映射
		List<Book> bookList = new ArrayList<Book>();
		for(Map<String,Object> map : mapList) {
			Book book = CommonUtils.toBean(map, Book.class);
			Category category = CommonUtils.toBean(map, Category.class);
			book.setCategory(category);
			bookList.add(book);
		}
		
		/*
		 * 4. 创建PageBean
		 */
		PageBean<Book> pb = new PageBean<Book>();
		pb.setBeanList(bookList);
		pb.setPc(pc);
		pb.setPs(ps);
		pb.setTr(tr);
		
		return pb;
	}
	
	/**
	 * 按bid查询
	 * @param bid
	 * @return
	 * @throws SQLException
	 */
	public Book findByBid(String bid) throws SQLException {
		String sql = "select * from t_book b, t_category c where b.cid=c.cid and b.bid=?";
		// map中的键值对照：bid, bname, price, .... , cid, cname, desc, pid
		Map<String,Object> map = qr.query(sql, new MapHandler(), bid);
		Book book = CommonUtils.toBean(map, Book.class);
		Category child = CommonUtils.toBean(map, Category.class);//cid, cname, desc
		Category parent = new Category();
		parent.setCid((String)map.get("pid"));//把pid赋值给parent对象
		
		// 建立关系
		child.setParent(parent);
		book.setCategory(child);
		
		return book;
	}

	/**
	 * 添加图书
	 * @param book
	 * @throws SQLException 
	 */
	public void add(Book book) throws SQLException {
		String sql = "insert into t_book(bid,bname,author,price,currPrice,discount," +
				"press,publishtime,edition,pageNum,wordNum,printtime,booksize," +
				"paper,cid,image_w,image_b) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		Object[] params = {book.getBid(),book.getBname(),book.getAuthor(),book.getPrice(),
				book.getCurrPrice(), book.getDiscount(), book.getPress(),book.getPublishtime(),
				book.getEdition(),book.getPageNum(),book.getWordNum(),book.getPrinttime(),
				book.getBooksize(),book.getPaper(), book.getCategory().getCid(),
				book.getImage_w(),book.getImage_b()};
		qr.update(sql, params);
	}

	/**
	 * 编辑图书
	 * @param book
	 * @throws SQLException 
	 */
	public void edit(Book book) throws SQLException {
		String sql = "update t_book set bname=?,author=?,price=?,currPrice=?,discount=?," +
				"press=?,publishtime=?,edition=?,pageNum=?,wordNum=?,printtime=?," +
				"booksize=?,paper=?,cid=? where bid=?";
		Object[] params = {book.getBname(),book.getAuthor(),book.getPrice(),
				book.getCurrPrice(), book.getDiscount(), book.getPress(),book.getPublishtime(),
				book.getEdition(),book.getPageNum(),book.getWordNum(),book.getPrinttime(),
				book.getBooksize(),book.getPaper(), book.getCategory().getCid(),
				book.getBid()};
		qr.update(sql, params);
	}
	
	/**
	 * 删除图书
	 * @param bid
	 * @throws SQLException 
	 */
	public void delete(String bid) throws SQLException {
		String sql = "delete from t_book where bid=?";
		qr.update(sql, bid);
	}
}
