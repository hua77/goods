package cn.itcast.bookstore.cart.service;

import java.sql.SQLException;
import java.util.List;

import cn.itcast.bookstore.cart.dao.CartItemDao;
import cn.itcast.bookstore.cart.domain.CartItem;
import cn.itcast.commons.CommonUtils;

public class CartItemService {
	private CartItemDao cartItemDao = new CartItemDao();
	
	/**
	 * 加载多个CartItem
	 * @param cartItemIds
	 * @return
	 */
	public List<CartItem> loadCartItemList(String cartItemIds) {
		try {
			return cartItemDao.loadCartItemList(cartItemIds);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 修改数量
	 * @param cartItemId
	 * @param quantity
	 */
	public void updateQuantity(String cartItemId, int quantity) {
		try {
			cartItemDao.updateQuantity(cartItemId, quantity);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 加载
	 * @param cartItemId
	 * @return
	 */
	public CartItem load(String cartItemId) {
		try {
			return cartItemDao.findByCartItemId(cartItemId);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 我的购物车
	 * @param uid
	 * @return
	 */
	public List<CartItem> myCart(String uid) {
		try {
			return cartItemDao.findByUser(uid);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 添加条目
	 * @param cartItem
	 */
	public void add(CartItem cartItem) {
		try {
			CartItem _cartItem = cartItemDao.findByBookAndUser(
					cartItem.getBook().getBid(), cartItem.getOwner().getUid());
			if(_cartItem == null) {//用户原来没有这个条目，我们完成添加条目
				cartItem.setCartItemId(CommonUtils.uuid());
				cartItemDao.add(cartItem);
			} else {//用户原来已经存在这个条目，我们修改条目的数量
				cartItemDao.updateQuantity(_cartItem.getCartItemId(), 
						cartItem.getQuantity() + _cartItem.getQuantity());
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 批量删除
	 * @param cartItemIds
	 */
	public void delete(String cartItemIds) {
		try {
			cartItemDao.delete(cartItemIds);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}
