package cn.itcast.bookstore.user.service;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Properties;

import javax.mail.Session;

import cn.itcast.bookstore.user.dao.UserDao;
import cn.itcast.bookstore.user.domain.User;
import cn.itcast.bookstore.user.service.exception.UserException;
import cn.itcast.commons.CommonUtils;
import cn.itcast.mail.Mail;
import cn.itcast.mail.MailUtils;

public class UserService {
	private UserDao userDao = new UserDao();

	/**
	 * 校验用户是否存在
	 *   校验成功(不存在为成功)返回true，失败返回false
	 * @param loginname
	 * @return
	 */
	public boolean validateLoginname(String loginname) {
		try {
			int cnt = userDao.findCountByLoginname(loginname);
			return cnt > 0 ? false : true;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 校验email是否存在
	 * @param email
	 * @return
	 */
	public boolean validateEmail(String email) {
		try {
			int cnt = userDao.findCountByEmail(email);
			System.out.println(cnt);
			return cnt > 0 ? false : true;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 注册功能
	 * @param form
	 */
	public void regist(User form) {
		try {
			/*
			 * 1. 数据的补全
			 */
			form.setUid(CommonUtils.uuid());//设置uid
			form.setStatus(false);//设置状态为未激活
			form.setActivationCode(CommonUtils.uuid() + CommonUtils.uuid());
			/*
			 * 2. 发送激活邮件
			 */
			
			// 获取配置文件信息
			Properties props = new Properties();
			props.load(this.getClass().getClassLoader()
					.getResourceAsStream("email_template.properties"));
			
			// 得到session
			Session session = MailUtils.createSession(props.getProperty("host"), 
					props.getProperty("username"), props.getProperty("password"));
			
			// 把模板中的{0}替换成当前用户的激活码
			String content = props.getProperty("content");
			content = MessageFormat.format(content, form.getActivationCode());
			// 创建Mail对象
			Mail mail = new Mail(props.getProperty("from"), form.getEmail(),
					props.getProperty("subject"), content);
			// 发送邮件
			MailUtils.send(session, mail);
			
			/*
			 * 调用userDao的方法，向数据库插入记录
			 */
			userDao.add(form);
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 激活功能
	 * @param actitvationCode
	 * @throws UserException 
	 */
	public void activation(String activationCode) throws UserException {
		/*
		 * 1. 通过激活码查询用户
		 *   > 如果存在说明有效用户
		 *   > 如果不存在，说明无效激活码，抛出异常。
		 */
		try {
			User user = userDao.findByActivationCode(activationCode);
			if(user == null) throw new UserException("无效的激活码！");
			
			/*
			 * 2. 查看用户的状态是否为已激活状态，如果是说明用户是再次激活！
			 */
			if(user.isStatus()) throw new UserException("您已经激活过了，不要再次激活！");
			/*
			 * 3. 修改用户的状态为true，表示已激活状态
			 */
			userDao.updateStatus(user.getUid(), true);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 登录功能
	 * @param loginname
	 * @param loginpass
	 * @return
	 * @throws UserException 
	 */
	public User login(String loginname, String loginpass) throws UserException {
		try {
			/*
			 * 1. 使用登录名和登录密码查询用户
			 */
			User user = userDao.findByLoginnameAndLoginpass(loginname, loginpass);
			/*
			 * 2. 校验user是否为null，如果为null，抛出异常
			 */
			if(user == null) throw new UserException("用户名或密码错误！");
			/*
			 * 3. 校验用户状态，如果是未激活，抛出异常
			 */
			if(!user.isStatus()) throw new UserException("您还没有激活！");
			// 返回user
			return user;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 *  校验密码
	 * @param uid
	 * @param loginpass
	 * @return
	 */
	public boolean validateLoginpass(String  uid, String loginpass) {
		try {
			return userDao.validateLoginpass(uid, loginpass);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 修改密码
	 * @param uid
	 * @param newloginpass
	 */
	public void updatePassword(String uid, String newloginpass) {
		try {
			userDao.updatePassword(uid, newloginpass);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		
	}
}
