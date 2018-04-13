package cn.itcast.bookstore.cart.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;

import cn.itcast.bookstore.book.domain.Book;
import cn.itcast.bookstore.cart.domain.CartItem;
import cn.itcast.bookstore.user.domain.User;
import cn.itcast.commons.CommonUtils;
import cn.itcast.jdbc.TxQueryRunner;

public class CartItemDao {
	private QueryRunner qr = new TxQueryRunner();
	
	public List<CartItem> loadCartItemList(String cartItemIds) throws SQLException {
		/*
		 * 1. 得到sql语句
		 */
		String sql = "select * from t_cartitem c, t_book b where c.bid=b.bid and cartItemId in";
		
		Object[] cartItemIdArray = cartItemIds.split(","); 
		StringBuilder sb = new StringBuilder("(");
		for(int i = 0; i < cartItemIdArray.length; i++) {
			sb.append("?");
			if(i < cartItemIdArray.length-1) {
				sb.append(",");
			}
		}
		sb.append(")");
		sql += sb.toString();
		
		/*
		 * 2. 执行查询
		 */
		List<Map<String,Object>> mapList = qr.query(sql, new MapListHandler(), cartItemIdArray);
		return toCartItemList(mapList);
	}
	
	/**
	 * 加载
	 * @param cartItemId
	 * @return
	 * @throws SQLException 
	 */
	public CartItem findByCartItemId(String cartItemId) throws SQLException {
		String sql = "select * from t_cartitem c, t_book b where c.bid=b.bid and c.cartItemId=?";
		return toCartItem(qr.query(sql, new MapHandler(), cartItemId));
	}
	
	/**
	 * 按用户查询
	 * @param uid
	 * @return
	 * @throws SQLException
	 */
	public List<CartItem> findByUser(String uid) throws SQLException {
		String sql = "select * from t_cartitem c, t_book b where c.bid=b.bid and c.uid=? order by c.orderBy";
		List<Map<String,Object>> mapList = qr.query(sql, new MapListHandler(), uid);
		return toCartItemList(mapList);
	}

	/**
	 * 把List<Map>转发成List<CartItem>
	 * @param mapList
	 * @return
	 */
	private List<CartItem> toCartItemList(List<Map<String, Object>> mapList) {
		List<CartItem> cartItemList = new ArrayList<CartItem>();
		for(Map<String,Object> map : mapList) {
			cartItemList.add(toCartItem(map));
		}
		return cartItemList;
	}

	/**
	 * 把Map映射成CartItem
	 * @param map
	 * @return
	 */
	private CartItem toCartItem(Map<String, Object> map) {
		if(map == null || map.size() == 0) return null;
		CartItem cartItem = CommonUtils.toBean(map, CartItem.class);
		Book book = CommonUtils.toBean(map, Book.class);
		User owner = CommonUtils.toBean(map, User.class);
		cartItem.setBook(book);
		cartItem.setOwner(owner);
		
		return cartItem;
	}
	
	/**
	 * 修改条目的数量
	 * @param cartItemId
	 * @param quantity
	 * @throws SQLException
	 */
	public void updateQuantity(String cartItemId, int quantity) throws SQLException {
		String sql = "update t_cartitem set quantity=? where cartItemId=?";
		qr.update(sql, quantity, cartItemId);
	}
	
	/**
	 * 添加条目
	 * @param cartItem
	 * @throws SQLException 
	 */
	public void add(CartItem cartItem) throws SQLException {
		String sql = "insert into t_cartitem(cartItemId, quantity, bid, uid) values(?,?,?,?)";
		Object[] params = {cartItem.getCartItemId(), cartItem.getQuantity(),
				cartItem.getBook().getBid(), cartItem.getOwner().getUid()};
		qr.update(sql, params);
	}
	
	/**
	 * 用来校验当前用户是否存在指定bid的条目。
	 * @param bid
	 * @param uid
	 * @return
	 * @throws SQLException
	 */
	public CartItem findByBookAndUser(String bid, String uid) throws SQLException {
		String sql = "select * from t_cartitem c, t_book b where c.bid=b.bid and c.bid=? and c.uid=?";
		return toCartItem(qr.query(sql, new MapHandler(), bid, uid));
	}
	
	/**
	 * 批量删除
	 * @param cartItemIds：1~N个cartItemId，中间用逗号分隔。
	 * @throws SQLException 
	 */
	public void delete(String cartItemIds) throws SQLException {
		String sql = "delete from t_cartitem where cartItemId in";
		
		// 使用逗号分割cartItemIds,得到数组
		Object[] cartItemIdArray = cartItemIds.split(","); 
		StringBuilder sb = new StringBuilder("(");
		for(int i = 0; i < cartItemIdArray.length; i++) {
			sb.append("?");
			if(i < cartItemIdArray.length-1) {
				sb.append(",");
			}
		}
		sb.append(")");
		sql += sb.toString();
		
		
		// 删除所有指定id的条目
		qr.update(sql, cartItemIdArray);
	}
}
