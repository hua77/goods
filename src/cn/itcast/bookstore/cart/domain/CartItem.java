package cn.itcast.bookstore.cart.domain;

import java.math.BigDecimal;

import cn.itcast.bookstore.book.domain.Book;
import cn.itcast.bookstore.user.domain.User;

/**
 * 购物车条目类
 * 
 * @author qdmmy6
 * 
 */
public class CartItem {
	private String cartItemId;// 主键
	private int quantity;// 数量
	private Book book;// 当前条目购买的商品
	private User owner;// 当前条目的所有者
	
	// 返回小计
	/*
	 * java的数学运算只能在非money方法使用，如果与钱相关，那么就不行了。
	 * 问：2.0 - 11 = 0.8999999999999999
	 * 
	 * 使用BigDecimal，而且必须用它的String参数的构造器
	 */
	public double getSubtotal() {
		BigDecimal v1 = new BigDecimal(book.getCurrPrice() + "");
		BigDecimal v2 = new BigDecimal(quantity + "");
		BigDecimal v3 = v1.multiply(v2);
		return v3.doubleValue();
	}

	public String getCartItemId() {
		return cartItemId;
	}

	public void setCartItemId(String cartItemId) {
		this.cartItemId = cartItemId;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public Book getBook() {
		return book;
	}

	public void setBook(Book book) {
		this.book = book;
	}

	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

}
