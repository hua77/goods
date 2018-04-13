package cn.itcast.bookstore.order.web.servlet;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.itcast.bookstore.book.domain.Book;
import cn.itcast.bookstore.cart.domain.CartItem;
import cn.itcast.bookstore.cart.service.CartItemService;
import cn.itcast.bookstore.order.domain.Order;
import cn.itcast.bookstore.order.domain.OrderItem;
import cn.itcast.bookstore.order.service.OrderService;
import cn.itcast.bookstore.user.domain.User;
import cn.itcast.bookstore.utils.PaymentUtils;
import cn.itcast.commons.CommonUtils;
import cn.itcast.pager.PageBean;
import cn.itcast.servlet.BaseServlet;

public class OrderServlet extends BaseServlet {
	private OrderService orderService = new OrderService();
	private CartItemService cartItemService = new CartItemService();
	
	/**
	 * 支付回调
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String back(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		/*
		 * 1. 获取11参数和hmac
		 */
		String p1_MerId = req.getParameter("p1_MerId");
		String r0_Cmd = req.getParameter("r0_Cmd");
		String r1_Code = req.getParameter("r1_Code");//固定值 “1”, 代表支付成功.
		String r2_TrxId = req.getParameter("r2_TrxId");
		String r3_Amt = req.getParameter("r3_Amt");
		String r4_Cur = req.getParameter("r4_Cur");
		String r5_Pid = req.getParameter("r5_Pid");
		String r6_Order = req.getParameter("r6_Order");//订单号就是我们请求时给易宝传递的
		String r7_Uid = req.getParameter("r7_Uid");
		String r8_MP = req.getParameter("r8_MP");
		String r9_BType = req.getParameter("r9_BType");//回调方法：1表示重定向，2表示点对点
		String hmac = req.getParameter("hmac");
		
		/*
		 * 2. 校验身份
		 * * 获取密钥，使用校验方法，传递hmac和11个参数给校验方法，得到一个boolean值
		 */
		// 获取密钥
		Properties props = new Properties();
		props.load(this.getClass().getClassLoader().getResourceAsStream("payment.properties"));
		String keyValue = props.getProperty("keyValue");
		
		// 校验
		boolean bool = PaymentUtils.verifyCallback(hmac, p1_MerId, r0_Cmd, r1_Code, r2_TrxId,
				r3_Amt, r4_Cur, r5_Pid, r6_Order, r7_Uid, r8_MP, r9_BType,
				keyValue);
		if(!bool) {
			req.setAttribute("code", "error");
			req.setAttribute("msg'", "签名无效，支付失败！");
			return "f:/jsps/msg.jsp";
		}
		
		/*
		 * 3. 修改订单的状态
		 */
		orderService.updateStatus(r6_Order, 2);
		if("1".equals(r9_BType)) {//重定向
			req.setAttribute("code", "success");
			req.setAttribute("msg", "恭喜，支付成功！");
			return "f:/jsps/msg.jsp";
		} else if("2".equals(r9_BType)) { // 点对点
			System.out.println("点对点");
			resp.getWriter().print("success");
			return  null;
		}
		return null;
	}
	
	/**
	 * 支付请求
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String payment(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		/*
		 * 1. 准备13参数，其中商号、密钥，以及回调url都在配置文件中。
		 */
		// 加载配置文件
		Properties props = new Properties();
		props.load(this.getClass().getClassLoader().getResourceAsStream("payment.properties"));
		
		String p0_Cmd = "Buy";//业务类型，固定值Buy，表示买。
		String p1_MerId = props.getProperty("p1_MerId");//从配置文件上获取，它是电商在易宝注册的商号
		String p2_Order = req.getParameter("oid");//订单编号，易宝在回调时会把它再传递回来。
		String p3_Amt = "0.01";//支付的金额！因为我们支付的钱时拿不回来的，所以我们只支付1分
		String p4_Cur = "CNY";//交易币种，固定值为CNY，表示人民币
		String p5_Pid = "";//商品名称
		String p6_Pcat = "";//商品各类
		String p7_Pdesc = "";//商品描述
		String p8_Url = props.getProperty("p8_Url");//回调url，当支付成功后，易宝会使用两种方式来访问这个地址。
		String p9_SAF = "";//送货地址
		String pa_MP = "";//扩展信息
		String pd_FrpId = req.getParameter("yh");//消费者选择的银行编号
		String pr_NeedResponse = "1";//应答方式
		/*
		 * 2. 计算hmac
		 * 计算hmac需要：13参数值，加密算法，密钥（千万不能泄露）！
		 */
		String keyValue = props.getProperty("keyValue");
		String hmac = PaymentUtils.buildHmac(p0_Cmd, p1_MerId, p2_Order,
				p3_Amt, p4_Cur, p5_Pid, p6_Pcat, p7_Pdesc, p8_Url, p9_SAF,
				pa_MP, pd_FrpId, pr_NeedResponse, keyValue);
		
		/*
		 * 3.准备易宝网页的url,以及追加14参数
		 */
		StringBuilder url = new StringBuilder("https://www.yeepay.com/app-merchant-proxy/node");
		url.append("?").append("p0_Cmd=").append(p0_Cmd);
		url.append("&").append("p1_MerId=").append(p1_MerId);
		url.append("&").append("p2_Order=").append(p2_Order);
		url.append("&").append("p3_Amt=").append(p3_Amt);
		url.append("&").append("p4_Cur=").append(p4_Cur);
		url.append("&").append("p5_Pid=").append(p5_Pid);
		url.append("&").append("p6_Pcat=").append(p6_Pcat);
		url.append("&").append("p7_Pdesc=").append(p7_Pdesc);
		url.append("&").append("p8_Url=").append(p8_Url);
		url.append("&").append("p9_SAF=").append(p9_SAF);
		url.append("&").append("pa_MP=").append(pa_MP);
		url.append("&").append("pd_FrpId=").append(pd_FrpId);
		url.append("&").append("pr_NeedResponse=").append(pr_NeedResponse);
		url.append("&").append("hmac=").append(hmac);
		
		/*
		 * 4. 重定向到易宝网关
		 */
		resp.sendRedirect(url.toString());
		return null;
	}
	
	/**
	 * 支付之前
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String paymentPre(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		/*
		 * 1.获取oid，加载Order，保存
		 * 2.转发到pay.jsp
		 */
		String oid = req.getParameter("oid");
		Order order = orderService.load(oid);
		req.setAttribute("order", order);
		return "/jsps/order/pay.jsp";
	}
	
	public String confirm(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		/*
		 * 1.获取订单id
		 */
		String oid = req.getParameter("oid");
		boolean bool = orderService.confirm(oid);
		if(bool) {//确认成功了！
			req.setAttribute("msg", "交易成功！");
			req.setAttribute("code", "success");
		} else {
			req.setAttribute("msg", "订单不是已发货状态，不能确认收货！");
			req.setAttribute("code", "error");			
		}
		return "/jsps/msg.jsp";
	}
	
	/**
	 * 取消订单
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String cancel(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		/*
		 * 1.获取订单id
		 */
		String oid = req.getParameter("oid");
		boolean bool = orderService.cancel(oid);
		if(bool) {//取消成功了
			req.setAttribute("msg", "订单已取消！");
			req.setAttribute("code", "success");
		} else {
			req.setAttribute("msg", "订单不是未付款状态，不能取消！");
			req.setAttribute("code", "error");			
		}
		return "/jsps/msg.jsp";
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
		
		return "f:/jsps/order/desc.jsp";
	}
	
	/**
	 * 创建订单
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String createOrder(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		/*
		 * 1. 获取参数
		 */
		String cartItemIds = req.getParameter("cartItemIds");//获取所有购物车条目的id
		String address = req.getParameter("address");//获取收货地址
		double total = Double.parseDouble(req.getParameter("total"));//获取合计
		
		/*
		 * 2. 创建订单
		 */
		Order order = new Order();
		order.setOid(CommonUtils.uuid());//指定订单编号
		order.setOrdertime(String.format("%tF %<tT", new java.util.Date()));//指定下单时间
		order.setTotal(total);//指定合计
		order.setStatus(1);//指定订单状态为1，表示未付款
		User user = (User)req.getSession().getAttribute("sessionUser");
		order.setUser(user);//指定订单所有者
		
		/*
		 * 3. 查询出所有购物车条目
		 * 循环遍历每个购物车条目，用来生成订单条目
		 */
		List<CartItem> cartItemList = cartItemService.loadCartItemList(cartItemIds);
		List<OrderItem> orderItemList = new ArrayList<OrderItem>();
		
		for(CartItem cartItem : cartItemList) {
			OrderItem orderItem = new OrderItem();
			orderItem.setOrderItemId(CommonUtils.uuid());//指定订单条目指定id
			orderItem.setQuantity(cartItem.getQuantity());//指定订单条目的数量
			orderItem.setSubtotal(cartItem.getSubtotal());//指定订单条目的小计
			orderItem.setBook(cartItem.getBook());//指定订单条目关联的图书
			orderItem.setOrder(order);//指定订单条目所属订单
			
			orderItemList.add(orderItem);
		}
		// 把订单条目的list给订单
		order.setOrderItemList(orderItemList);
		
		/*
		 * 4.通过orderService完成添加
		 */
		orderService.create(order);
		
		/*
		 * 5. 删除对应的购物车条目
		 */
		cartItemService.delete(cartItemIds);
		
		/*
		 * 6.把当前订单保存到request中，转发到/order/ordersucc.jsp
		 */
		req.setAttribute("order", order);
		return "f:/jsps/order/ordersucc.jsp";
	}
	
	/**
	 * 我的订单
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String myOrders(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		/*
		 * 1. 获取pc
		 */
		int pc = getPageCode(req);
		/*
		 * 2. 获取uid
		 */
		User user = (User)req.getSession().getAttribute("sessionUser");
		String uid = user.getUid();
		/*
		 * 3. 调用service方法，得到PageBean
		 */
		PageBean<Order> pb = orderService.myOrders(uid, pc);
		/*
		 * 4. 给PageBean设置url
		 */
		pb.setUrl(getUrl(req));
		/*
		 * 5. 保存PageBean到request中，转发到/jsps/book/list.jsp
		 */
		req.setAttribute("pb", pb);
		return "f:/jsps/order/list.jsp";
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
