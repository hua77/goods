package cn.itcast.bookstore.admin.order.web.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.itcast.bookstore.order.domain.Order;
import cn.itcast.bookstore.order.service.OrderService;
import cn.itcast.bookstore.user.domain.User;
import cn.itcast.pager.PageBean;
import cn.itcast.servlet.BaseServlet;

public class AdminOrderServlet extends BaseServlet {
	private OrderService orderService = new OrderService();
	
	/**
	 * 查询所有订单
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String findAll(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		/*
		 * 1. 获取pc
		 */
		int pc = getPageCode(req);
		/*
		 * 3. 调用service方法，得到PageBean
		 */
		PageBean<Order> pb = orderService.findAll(pc);
		/*
		 * 4. 给PageBean设置url
		 */
		pb.setUrl(getUrl(req));
		/*
		 * 5. 保存PageBean到request中，转发到/jsps/book/list.jsp
		 */
		req.setAttribute("pb", pb);
		return "f:/adminjsps/admin/order/list.jsp";
	}
	
	/**
	 * 按状态查询
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String findByStatus(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		/*
		 * 1. 获取pc
		 */
		int pc = getPageCode(req);
		/*
		 * 2. 获取状态
		 */
		int status = Integer.parseInt(req.getParameter("status"));
		/*
		 * 3. 调用service方法，得到PageBean
		 */
		PageBean<Order> pb = orderService.findByStatus(status, pc);
		/*
		 * 4. 给PageBean设置url
		 */
		pb.setUrl(getUrl(req));
		/*
		 * 5. 保存PageBean到request中，转发到/jsps/book/list.jsp
		 */
		req.setAttribute("pb", pb);
		return "f:/adminjsps/admin/order/list.jsp";
	}
	
	/**
	 * 加载订单
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String load(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String oid = req.getParameter("oid");
		String oper = req.getParameter("oper");
		Order order = orderService.load(oid);
		req.setAttribute("order", order);
		req.setAttribute("oper", oper);
		
		return "f:/adminjsps/admin/order/desc.jsp";
	}
	
	/**
	 * 发货
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String deliver(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String oid = req.getParameter("oid");
		int status = orderService.findStatus(oid);
		if(status != 2) {
			req.setAttribute("msg", "订单状态是不能发货的！");
		} else {
			orderService.updateStatus(oid, 3);
			req.setAttribute("msg", "发货成功！");
		}
		return "f:/adminjsps/msg.jsp";
	}
	
	/**
	 * 取消
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String cancel(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String oid = req.getParameter("oid");
		int status = orderService.findStatus(oid);
		if(status != 1) {
			req.setAttribute("msg", "订单状态是不能取消的！");
		} else {
			orderService.updateStatus(oid, 5);
			req.setAttribute("msg", "取消成功！");
		}
		return "f:/adminjsps/msg.jsp";
	}
	
	/*
	 * 获取当前页码
	 */
	private int getPageCode(HttpServletRequest req) {
		int pc = 1;//默认为1
		String str = req.getParameter("pc");
		if(str != null) {
			pc = Integer.parseInt(str);
		}
		return pc;
	}
	
	/*
	 * 获取url
	 */
	private String getUrl(HttpServletRequest req) {
		String url = req.getRequestURI() + "?" + req.getQueryString();
		int index = url.lastIndexOf("&pc=");
		if(index != -1) {
			url = url.substring(0, index);
		}
		return url;
	}
}
