package cn.itcast.bookstore.admin.admin.web.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.itcast.bookstore.admin.admin.domain.Admin;
import cn.itcast.bookstore.admin.admin.service.AdminService;
import cn.itcast.commons.CommonUtils;
import cn.itcast.servlet.BaseServlet; 

public class AdminServlet extends BaseServlet {
	private AdminService adminService = new AdminService();
	
	/**
	 * 退出功能
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String quit(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		req.getSession().invalidate();
		return "r:/adminjsps/login.jsp";
	}
	
	/**
	 * 登录功能
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String login(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		/*
		 * 1.封装表单数据到Admin中
		 */
		Admin form = CommonUtils.toBean(req.getParameterMap(), Admin.class);
		/*
		 * 2.使用service来查询
		 */
		Admin admin = adminService.login(form.getAdminname(), form.getAdminpwd());
		/*
		 * 3.判断查询结果是否存在
		 */
		if(admin == null) {
			req.setAttribute("msg", "用户名或密码错误！");
			return "f:/adminjsps/msg.jsp";
		} else {
			req.getSession().setAttribute("admin", admin);
			return "r:/adminjsps/admin/index.jsp";
		}
	}
}
