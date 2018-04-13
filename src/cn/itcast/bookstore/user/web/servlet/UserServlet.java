package cn.itcast.bookstore.user.web.servlet;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.itcast.bookstore.user.domain.User;
import cn.itcast.bookstore.user.service.UserService;
import cn.itcast.bookstore.user.service.exception.UserException;
import cn.itcast.commons.CommonUtils;
import cn.itcast.servlet.BaseServlet;

public class UserServlet extends BaseServlet {
	private UserService userService = new UserService();
	
	/**
	 * 校验用户名是否已经被注册（支持ajax）
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String validateLoginname(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		/*
		 * 1. 获取用户名
		 */
		String loginname = req.getParameter("loginname");
		/*
		 * 2. 使用用户名调用service的验证方法，得到boolean结果
		 */
		boolean bool = userService.validateLoginname(loginname);
		/*
		 * 3. 把结果发送给客户端
		 */
		resp.getWriter().print(bool);
		return loginname;
	}
	
	/**
	 * 校验老密码
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String validateLoginpass(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		/*
		 * 1. 获取老密码
		 */
		String loginpass = req.getParameter("loginpass");
		/*
		 * 2. 获取当前用户的uid
		 */
		String uid = ((User)req.getSession().getAttribute("sessionUser")).getUid();
		/*
		 * 3. 调用service#login()方法来校验
		 */
		boolean bool = userService.validateLoginpass(uid, loginpass);
		/*
		 * 4. 把结果发送给客户端
		 */
		resp.getWriter().print(bool);
		return uid;
	}
	
	/**
	 * 修改密码
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String updatePassword(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		/*
		 * 1. 封装表单数据
		 */
		User form = CommonUtils.toBean(req.getParameterMap(), User.class);
		/*
		 * 2. 服务器端表单校验(不做)
		 */
		Map<String,String> errors = validatePwd(form, req);
		if(errors != null && errors.size() > 0) {
			req.setAttribute("errors", errors);
			req.setAttribute("form", form);
			return "/jsps/user/pwd.jsp";
		}
		/*
		 * 3. 获取session中user的uid，以及表单中的新密码，调用service#updatePassword()方法
		 */
		String uid = ((User)req.getSession().getAttribute("sessionUser")).getUid();
		userService.updatePassword(uid, form.getNewloginpass());
		/*
		 * 4. 保存成功信息到request中，转发到msg.jsp显示
		 */
		req.setAttribute("msg", "修改密码成功！");
		return "f:/jsps/msg.jsp";
	}
	
	/**
	 * 校验email是否已经被注册（支持ajax）
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String validateEmail(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		/*
		 * 1. 获取Email
		 */
		String email = req.getParameter("email");
		/*
		 * 2. 使用email调用service的验证方法，得到boolean结果
		 */
		boolean bool = userService.validateEmail(email);
		/*
		 * 3. 把结果发送给客户端
		 */
		resp.getWriter().print(bool);
		return email;
	}
	
	/**
	 * 校验验证码是否正确（支持ajax）
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String validateVerifyCode(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		/*
		 * 1. 获取用户输入的验证码
		 */
		String userVerifyCode = req.getParameter("verifyCode");
		/*
		 * 2. 获取图片上的验证码
		 */
		String verifyCode = (String)req.getSession().getAttribute("vCode");
		/*
		 * 3. 比较图片的验证码与用户输入的验证码是否相同，把比较结果发送给客户端
		 */
		resp.getWriter().print(verifyCode.equalsIgnoreCase(userVerifyCode));
		return verifyCode;
	}
	
	/**
	 * 当客户在regist.jsp页面提交表单时调用本方法
	 * 注册功能
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String regist(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		/*
		 * 1. 封装表单数据到User form对象中
		 */
		User form = CommonUtils.toBean(req.getParameterMap(), User.class);
		/*
		 * 2. 对表单进行服务器端校验
		 */
		Map<String,String> errors = validateRegist(form, req);
		if(errors != null && errors.size() > 0) {
			req.setAttribute("errors", errors);
			return "f:/jsps/user/regist.jsp";
		}
		/*
		 * 3. 调用userService完成注册
		 */
		userService.regist(form);
		/*
		 * 4. 保存成功信息，转发到msg.jsp显示
		 */
		req.setAttribute("code", "success");//这个值是通知msg.jsp显示什么图片，如果是sucess，它是对号，如果是error，它是X。
		req.setAttribute("msg", "注册成功，请到你的邮件激活！");
		return "f:/jsps/msg.jsp";
	}
	
	/*
	 * 对注册进行服务器端校验
	 *   如果返回的map为null，或者长度为0，表示校验通过！否则失败！
	 *   在Map中key是表单字段的名称，而value是对应的错误信息。
	 *     例如：map.put("loginname", "用户名不能为空！");
	 */
	private Map<String,String> validateRegist(User form, HttpServletRequest req) {
		Map<String,String> errors = new HashMap<String,String>();
		
		/*
		 * 1. 校验loginname
		 */
		String loginname = form.getLoginname();
		if(loginname == null || loginname.trim().isEmpty()) {//非空校验
			errors.put("loginname", "用户名不能为空！");
		} else if(loginname.length() < 3 || loginname.length() > 20) {//长度校验
			errors.put("loginname", "用户名长度必须在3~20之间！");
		} else {//是否已被注册校验
			boolean bool = userService.validateLoginname(loginname);
			if(!bool) {
				errors.put("loginname", "用户名已被注册！");
			}
		}
		
		/*
		 * 2. 校验loginpass
		 */
		String loginpass = form.getLoginpass();
		if(loginpass == null || loginpass.trim().isEmpty()) {//非空校验
			errors.put("loginpass", "密码不能为空！");
		} else if(loginpass.length() < 3 || loginpass.length() > 20) {//长度校验
			errors.put("loginpass", "密码长度必须在3~20之间！");
		}
		
		/*
		 * 3. 校验reloginname
		 */
		String reloginpass = form.getReloginpass();
		if(reloginpass == null || reloginpass.trim().isEmpty()) {//非空校验
			errors.put("reloginpass", "确认密码不能为空！");
		} else if(!reloginpass.equals(loginpass)) {//两次输入校验
			errors.put("reloginpass", "两次密码输入不一致！");
		}
		
		/*
		 * 4. 校验email
		 */
		String email = form.getEmail();
		if(email == null || email.trim().isEmpty()) {//非空校验
			errors.put("email", "email不能为空！");
		} else if(!email.matches("^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+((\\.[a-zA-Z0-9_-]{2,3}){1,2})$")) {//长度校验
			errors.put("email", "错误的Email地址！");
		} else {//是否已被注册校验
			boolean bool = userService.validateLoginname(loginname);
			if(!bool) {
				errors.put("email", "email已被注册！");
			}
		}
		
		/*
		 * 5. 校验verifyCode
		 */
		String verifyCode = form.getVerifyCode();
		if(verifyCode == null || verifyCode.trim().isEmpty()) {//非空校验
			errors.put("verifyCode", "验证码不能为空！");
		} else if(verifyCode.length() != 4) {//长度校验
			errors.put("verifyCode", "验证码长度错误！");
		} else {//是否已被注册校验
			String sessionVerifyCode = (String)req.getSession().getAttribute("vCode");
			if(!verifyCode.equalsIgnoreCase(sessionVerifyCode)) {
				errors.put("verifyCode", "验证码错误！");
			}
		}
		return errors;
	}
	
	/**
	 * 激活方法
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String activation(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		/*
		 * 1. 获取激活码
		 */
		String activationCode = req.getParameter("activationCode");
		try {
			userService.activation(activationCode);
			req.setAttribute("code", "success");
			req.setAttribute("msg", "激活成功，请马上登录！");
		} catch (UserException e) {//如果出现异常，说明激活失败
			req.setAttribute("code", "error");
			req.setAttribute("msg", e.getMessage());
		}
		
		return "f:/jsps/msg.jsp";
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
		 * 1. 封装表单数据
		 */
		User form = CommonUtils.toBean(req.getParameterMap(), User.class);
		
		/*
		 * 2. 服务器端的表单校验
		 */
		Map<String,String> errors = validateLogin(form, req);
		if(errors != null && errors.size() > 0) {
			req.setAttribute("errors", errors);
			req.setAttribute("form", form);
			return "/jsps/user/login.jsp";
		}
		
		/*
		 * 3. 调用service完成登录
		 */
		try {
			User user = userService.login(form.getLoginname(), form.getLoginpass());
			/*
			 * 4. 把当前用户保存到session中
			 */
			req.getSession().setAttribute("sessionUser", user);
			/*
			 * 5. 把当前用户名保存到cookie中
			 */
			Cookie cookie = new Cookie("loginname", 
					URLEncoder.encode(user.getLoginname(), "UTF-8"));
			cookie.setMaxAge(1000 * 60 * 60 * 24);
			resp.addCookie(cookie);
			return "r:/index.jsp";
		} catch (UserException e) {
			req.setAttribute("msg", e.getMessage());//把异常信息保存到request中
			req.setAttribute("form", form);
			return "/jsps/user/login.jsp";
		}
	}
	
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
		return "r:/jsps/user/login.jsp";
	}
	
	/**
	 * 登录的校验方法
	 * @param form
	 * @param req
	 * @return
	 */
	private Map<String,String> validateLogin(User form, HttpServletRequest req) {
		Map<String,String> errors = new HashMap<String,String>();
		
		/*
		 * 1. 校验loginname
		 */
		String loginname = form.getLoginname();
		if(loginname == null || loginname.trim().isEmpty()) {//非空校验
			errors.put("loginname", "用户名不能为空！");
		} else if(loginname.length() < 3 || loginname.length() > 20) {//长度校验
			errors.put("loginname", "用户名长度必须在3~20之间！");
		}
		
		/*
		 * 2. 校验loginpass
		 */
		String loginpass = form.getLoginpass();
		if(loginpass == null || loginpass.trim().isEmpty()) {//非空校验
			errors.put("loginpass", "密码不能为空！");
		} else if(loginpass.length() < 3 || loginpass.length() > 20) {//长度校验
			errors.put("loginpass", "密码长度必须在3~20之间！");
		}
		
		/*
		 * 3. 校验verifyCode
		 */
		String verifyCode = form.getVerifyCode();
		if(verifyCode == null || verifyCode.trim().isEmpty()) {//非空校验
			errors.put("verifyCode", "验证码不能为空！");
		} else if(verifyCode.length() != 4) {//长度校验
			errors.put("verifyCode", "验证码长度错误！");
		} else {//是否已被注册校验
			String sessionVerifyCode = (String)req.getSession().getAttribute("vCode");
			if(!verifyCode.equalsIgnoreCase(sessionVerifyCode)) {
				errors.put("verifyCode", "验证码错误！");
			}
		}
		return errors;
	}
	
	/**
	 * 校验修改密码表单
	 * @param form
	 * @param req
	 * @return
	 */
	private Map<String,String> validatePwd(User form, HttpServletRequest req) {
		Map<String,String> errors = new HashMap<String,String>();
		/*
		 * 这里等待你来完成！
		 */
		return errors;
	}
}
