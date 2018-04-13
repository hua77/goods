package cn.itcast.bookstore.order.service;

import java.sql.SQLException;

import cn.itcast.bookstore.order.dao.OrderDao;
import cn.itcast.bookstore.order.domain.Order;
import cn.itcast.jdbc.JdbcUtils;
import cn.itcast.pager.PageBean;

public class OrderService {
	private OrderDao orderDao = new OrderDao();
	
	/**
	 * 创建订单
	 * @param order
	 */
	public void create(Order order) {
		try {
			JdbcUtils.beginTransaction();//开启事务
			orderDao.add(order);
			JdbcUtils.commitTransaction();//提交事务
		} catch(SQLException e) {
			try {
				JdbcUtils.rollbackTransaction();//回滚事务
			} catch (SQLException e1) {
			}
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 加载订单
	 * @param oid
	 * @return
	 */
	public Order load(String oid) {
		try {
			return orderDao.load(oid);
		} catch(SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 确认收货
	 * @param oid
	 * @return
	 */
	public boolean confirm(String oid) {
		try {
			int status = orderDao.findOrderStatus(oid);//获取当前订单的状态
			if(status != 3) return false;//判断状态如果不是3，即不是已发货状态，那么就返回false
			orderDao.updateStatus(oid, 4);//如果是3,那么调用dao方法完成对订单的状态的修改，修改成4，表示交易成功
			return true;//返回true，表示成功了
		} catch(SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 取消订单
	 * @param oid
	 * @return
	 */
	public boolean cancel(String oid) {
		try {
			int status = orderDao.findOrderStatus(oid);//获取当前订单的状态
			if(status != 1) return false;//判断状态如果不是1，即不是未付款状态，那么就返回false
			orderDao.updateStatus(oid, 5);//如果是1,那么调用dao方法完成对订单的状态的修改，修改成5，表示取消
			return true;//返回true，表示成功了
		} catch(SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 我的订单
	 * @param uid
	 * @return
	 */
	public PageBean<Order> myOrders(String uid, int pc) {
		try {
			JdbcUtils.beginTransaction();//开启事务
			PageBean<Order> pb = orderDao.findByUser(uid, pc);
			JdbcUtils.commitTransaction();//提交事务
			return pb;
		} catch(SQLException e) {
			try {
				JdbcUtils.rollbackTransaction();//回滚事务
			} catch (SQLException e1) {
			}
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 查询所有订单
	 * @param pc
	 * @return
	 */
	public PageBean<Order> findAll(int pc) {
		try {
			JdbcUtils.beginTransaction();//开启事务
			PageBean<Order> pb = orderDao.findAll(pc);
			JdbcUtils.commitTransaction();//提交事务
			return pb;
		} catch(SQLException e) {
			try {
				JdbcUtils.rollbackTransaction();//回滚事务
			} catch (SQLException e1) {
			}
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 按状态查询
	 * @param status
	 * @param pc
	 * @return
	 */
	public PageBean<Order> findByStatus(int status, int pc) {
		try {
			JdbcUtils.beginTransaction();//开启事务
			PageBean<Order> pb = orderDao.findByStatus(status, pc);
			JdbcUtils.commitTransaction();//提交事务
			return pb;
		} catch(SQLException e) {
			try {
				JdbcUtils.rollbackTransaction();//回滚事务
			} catch (SQLException e1) {
			}
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 修改订单状态
	 * @param r6_Order
	 * @param i
	 */
	public void updateStatus(String oid, int status) {
		try {
			orderDao.updateStatus(oid, status);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 返回订单状态
	 * @param oid
	 * @param status
	 */
	public int findStatus(String oid) {
		try {
			return orderDao.findOrderStatus(oid);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}
