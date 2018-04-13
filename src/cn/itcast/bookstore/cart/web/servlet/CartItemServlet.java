package cn.itcast.bookstore.cart.web.servlet;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.itcast.bookstore.book.domain.Book;
import cn.itcast.bookstore.book.service.BookService;
import cn.itcast.bookstore.cart.domain.CartItem;
import cn.itcast.bookstore.cart.service.CartItemService;
import cn.itcast.bookstore.user.domain.User;
import cn.itcast.servlet.BaseServlet;

public class CartItemServlet extends BaseServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
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
	 * 修改条目数量（ajax）
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String updateQuantity(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		/*
		 * 1. 获取cartItemId
		 * 2. 获取quantity
		 */
		String cartItemId = req.getParameter("cartItemId");
		int quantity = Integer.parseInt(req.getParameter("quantity"));
		/*
		 * 3. 使用service方法完成修改
		 */
		cartItemService.updateQuantity(cartItemId, quantity);
		/*
		 * 4. 通过id加载被修改条目
		 * 把条目的小计和数量包装成json，返回给客户端
		 */
		CartItem cartItem = cartItemService.load(cartItemId);
		String json = "{\"quantity\":" + cartItem.getQuantity() + ", \"subtotal\":"
				+ cartItem.getSubtotal() + "}";
		resp.getWriter().print(json);
		return json;
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
	
	/**
	 * 添加条目
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String addCartItem(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		/*
		 * 1. 获取bid和quantity
		 */
		String bid = req.getParameter("bid");
		Book book = bookService.findByBid(bid);
		int quantity = Integer.parseInt(req.getParameter("quantity"));
		/*
		 * 2. 从session中获取user
		 */
		User user = (User)req.getSession().getAttribute("sessionUser");
		/*
		 * 3. 创建CartItem
		 */
		CartItem cartItem = new CartItem();
		cartItem.setBook(book);
		cartItem.setOwner(user);
		cartItem.setQuantity(quantity);
		/*
		 * 3. 调用service方法完成添加, 调用myCart()返回
		 */
		cartItemService.add(cartItem);
		
		return myCart(req, resp);
	}
	
	/**
	 * 批量删除
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String delete(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		/*
		 * 1. 获取cartItemIds参数，即要删除的条目id
		 */
		String cartItemIds = req.getParameter("cartItemIds");
		/*
		 * 2. 调用service完成删除
		 */
		cartItemService.delete(cartItemIds);
		/*
		 * 3. 返回到list.jsp显示购物车
		 */
		return myCart(req, resp);
	}
}
