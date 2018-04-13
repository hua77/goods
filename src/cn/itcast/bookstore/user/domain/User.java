package cn.itcast.bookstore.user.domain;

/**
 * 实体类
 * @author qdmmy6
 *
 */
/*
 * 1. 对应数据库的t_user
 * 2. 还要对应user模块所有功能中的表单！
 */
public class User implements java.io.Serializable {
	private String uid;//主键
	private String loginname;//登录名
	private String loginpass;//登录密码
	private String email;//邮件地址
	private boolean status;//用户状态，两种：true表示已激活，否则未激活
	private String activationCode;//激活码，它是唯一的，每个用户的激活码都不同。
	
	private String verifyCode;//验证码
	private String reloginpass;//确认密码
	private String newloginpass;//新密码
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getLoginname() {
		return loginname;
	}
	public void setLoginname(String loginname) {
		this.loginname = loginname;
	}
	public String getLoginpass() {
		return loginpass;
	}
	public void setLoginpass(String loginpass) {
		this.loginpass = loginpass;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public boolean isStatus() {
		return status;
	}
	public void setStatus(boolean status) {
		this.status = status;
	}
	public String getActivationCode() {
		return activationCode;
	}
	public void setActivationCode(String activationCode) {
		this.activationCode = activationCode;
	}
	public String getVerifyCode() {
		return verifyCode;
	}
	public void setVerifyCode(String verifyCode) {
		this.verifyCode = verifyCode;
	}
	public String getReloginpass() {
		return reloginpass;
	}
	public void setReloginpass(String reloginpass) {
		this.reloginpass = reloginpass;
	}
	public String getNewloginpass() {
		return newloginpass;
	}
	public void setNewloginpass(String newloginpass) {
		this.newloginpass = newloginpass;
	}
}
