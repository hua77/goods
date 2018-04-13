package cn.itcast.bookstore.cart.web.servlet;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.itcast.bookstore.book.service.BookService;
import cn.itcast.bookstore.cart.domain.CartItem;
import cn.itcast.bookstore.cart.service.CartItemService;
import cn.itcast.bookstore.user.domain.User;
import cn.itcast.servlet.BaseServlet;

public class addCarServlet extends BaseServlet{
	private CartItemService cartItemService = new CartItemService();
	private BookService bookService = new BookService();
	/**
	 * 加载多个CartItem
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String loadCartItemList(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String cartItemIds = req.getParameter("cartItemIds");
		List<CartItem> cartItemList = cartItemService.loadCartItemList(cartItemIds);
		req.setAttribute("cartItemList", cartItemList);
		
		// 计算合计
		BigDecimal total = new BigDecimal("0.0");
		for(CartItem cartItem : cartItemList) {
			BigDecimal subtotal = new BigDecimal(Double.toString(cartItem.getSubtotal()));
			total = total.add(subtotal);
		}
		req.setAttribute("total", total.doubleValue());
		
		// 保存所有条目的id
		req.setAttribute("cartItemIds", cartItemIds);
		
		return "f:/jsps/cart/showitem.jsp";
	}
	/**
	 * 我的购物车
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String myCart(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		/*
		 * 1. 获取session中的user
		 */
		User user = (User)req.getSession().getAttribute("sessionUser");
		/*
		 * 2. 调用service方法得到List<CartItem>
		 */
		List<CartItem> cartItemList = cartItemService.myCart(user.getUid());
		/*
		 * 3. 保存List<CartItem>到request中, 转发到/jsps/cart/list.jsp
		 */
		req.setAttribute("cartItemList", cartItemList);
		return "f:/jsps/cart/list.jsp";
	}
}
