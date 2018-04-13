package cn.itcast.bookstore.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

public class LoginFilter implements Filter {
	public void destroy() {

	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		/*
		 * 1. 获取session中的当前用户
		 */
		HttpServletRequest httpReq = (HttpServletRequest) request;
		Object user = httpReq.getSession().getAttribute("sessionUser");
		if(user == null) {
			request.setAttribute("msg", "您还没有登录!");
			request.getRequestDispatcher("/jsps/user/login.jsp").forward(request, response);
			return;
		}
		chain.doFilter(request, response);//放行
	}

	public void init(FilterConfig fConfig) throws ServletException {

	}

}
