package cn.itcast.bookstore.order.dao;

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
import cn.itcast.bookstore.order.domain.Order;
import cn.itcast.bookstore.order.domain.OrderItem;
import cn.itcast.bookstore.user.domain.User;
import cn.itcast.commons.CommonUtils;
import cn.itcast.jdbc.TxQueryRunner;
import cn.itcast.pager.Expression;
import cn.itcast.pager.PageBean;
import cn.itcast.pager.PageConstance;

public class OrderDao {
	private QueryRunner qr = new TxQueryRunner();
	
	/**
	 * 添加订单
	 * @param order
	 * @throws SQLException 
	 */
	public void add(Order order) throws SQLException {
		/*
		 * 1. 插入订单
		 */
		String orderSql = "insert into t_order values(?,?,?,?,?,?)";
		Object[] params = {order.getOid(),order.getOrdertime(),
				order.getTotal(), order.getStatus(), order.getAddress(),
				order.getUser().getUid()};
		qr.update(orderSql, params);
		
		/*
		 * 2. 循环遍历所有订单条目，插入之
		 */
		String orderItemSql = "insert into t_orderitem values(?,?,?,?,?,?,?,?)";
		Object[][] itemParams = new Object[order.getOrderItemList().size()][];
		int index = 0;
		for(OrderItem item : order.getOrderItemList()) {
			itemParams[index++] = new Object[] {item.getOrderItemId(), item.getQuantity(),
					item.getSubtotal(), item.getBook().getBid(), item.getBook().getBname(),
					item.getBook().getCurrPrice(), item.getBook().getImage_b(),
					order.getOid()};
		}
		qr.batch(orderItemSql, itemParams);
	}
	
	/**
	 * 按uid查询
	 * @param uid
	 * @return
	 * @throws SQLException 
	 */
	public PageBean<Order> findByUser(String uid, int pc) throws SQLException {
		List<Expression> exprList = new ArrayList<Expression>();
		Expression expr = new Expression("uid", "=", uid);		
		exprList.add(expr);
		
		return findByCretiera(exprList, pc);
	}
	
	/**
	 * 查询所有订单
	 * @param pc
	 * @return
	 * @throws SQLException
	 */
	public PageBean<Order> findAll(int pc) throws SQLException {
		List<Expression> exprList = new ArrayList<Expression>();
		return findByCretiera(exprList, pc);
	}
	
	/**
	 * 按状态查询
	 * @param status
	 * @param pc
	 * @return
	 * @throws SQLException
	 */
	public PageBean<Order> findByStatus(int status, int pc) throws SQLException {
		List<Expression> exprList = new ArrayList<Expression>();
		Expression expr = new Expression("status", "=", status);		
		exprList.add(expr);
		
		return findByCretiera(exprList, pc);
	}
	
	/**
	 * 通过oid查询
	 * @param oid
	 * @return
	 * @throws SQLException 
	 */
	public Order load(String oid) throws SQLException {
		String sql = "select * from t_order where oid=?";
		Order order = toOrder(qr.query(sql, new MapHandler(), oid));
		List<OrderItem> orderItemList = findOrderItemListByOrder(order);
		order.setOrderItemList(orderItemList);
		return order;
	}
	
	/*
	 *  为指定订单，加载其订单条目
	 */
	private List<OrderItem> findOrderItemListByOrder(Order order) throws SQLException {
		String sql = "select * from t_orderitem where oid=?";
		List<Map<String,Object>> mapList = qr.query(sql, new MapListHandler(), order.getOid());
		return toOrderItemList(mapList);
	}

	/*
	 * 把List<Map>映射成List<OrderItem>
	 */
	private List<OrderItem> toOrderItemList(List<Map<String, Object>> mapList) {
		List<OrderItem> orderItemList = new ArrayList<OrderItem>();
		for(Map<String,Object> map : mapList) {
			orderItemList.add(toOrderItem(map));
		}
		return orderItemList;
	}

	/*
	 * 把Map映射成OrderItem
	 */
	private OrderItem toOrderItem(Map<String, Object> map) {
		OrderItem orderItem = CommonUtils.toBean(map, OrderItem.class);
		Book book = CommonUtils.toBean(map, Book.class);
		Order order = CommonUtils.toBean(map, Order.class);
		orderItem.setBook(book);
		orderItem.setOrder(order);
		return orderItem;
	}

	/*
	 * 把List<Map>映射成List<Order>
	 */
	private List<Order> toOrderList(List<Map<String, Object>> mapList) {
		List<Order> orderList = new ArrayList<Order>();
		for(Map<String,Object> map : mapList) {
			orderList.add(toOrder(map));
		}
		return orderList;
	}

	/*
	 * 把一个Map映射成一个Oorder对象
	 */
	private Order toOrder(Map<String, Object> map) {
		Order order = CommonUtils.toBean(map, Order.class);
		User user = CommonUtils.toBean(map, User.class);
		order.setUser(user);
		return order;
	}
	
	/**
	 * 修改订单状态
	 * @param oid
	 * @param status
	 * @throws SQLException 
	 */
	public void updateStatus(String oid, int status) throws SQLException {
		String sql = "update t_order set status=? where oid=?";
		qr.update(sql, status, oid);
	}
	
	/**
	 * 查看订单状态
	 * @param oid
	 * @return
	 * @throws SQLException
	 */
	public int findOrderStatus(String oid) throws SQLException {
		String sql = "select status from t_order where oid=?";
		Number status = (Number)qr.query(sql, new ScalarHandler(), oid);
		return status == null ? 0 : status.intValue();
	}
	
	private PageBean<Order> findByCretiera(List<Expression> exprList, int pc) throws SQLException {
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
		String sql = "select count(1) from t_order" + whereSql;
		Number cnt = (Number)qr.query(sql, new ScalarHandler(), params.toArray());
		int tr = cnt == null ? 0 : cnt.intValue();
		/*
		 * 3. 得到当前页记录
		 * 给出select * from t_book where ... limit ...
		 */
		String orderByAndLimitSql = " order by ordertime desc limit ?,?";
		// ?1 = (pc-1) * ps,   ?2 = ps
		int ps = PageConstance.BOOK_PAGE_SIZE;
		params.add((pc-1) * ps);
		params.add(ps);
	
		sql = "select * from t_order" + whereSql + orderByAndLimitSql;
		List<Map<String,Object>> mapList = qr.query(sql, new MapListHandler(), params.toArray());
		
		// 映射
		List<Order> orderList = toOrderList(mapList);
		for(Order order : orderList) {
			order.setOrderItemList(findOrderItemListByOrder(order));
		}
		
		/*
		 * 4. 创建PageBean
		 */
		PageBean<Order> pb = new PageBean<Order>();
		pb.setBeanList(orderList);
		pb.setPc(pc);
		pb.setPs(ps);
		pb.setTr(tr);
		
		return pb;
	}
}
