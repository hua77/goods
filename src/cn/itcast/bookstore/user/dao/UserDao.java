package cn.itcast.bookstore.user.dao;

import java.sql.SQLException;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import cn.itcast.bookstore.user.domain.User;
import cn.itcast.jdbc.TxQueryRunner;

public class UserDao {
	private QueryRunner qr = new TxQueryRunner();

	/**
	 * 查询指定名称的用户个数
	 * @param loginname
	 * @return
	 * @throws SQLException 
	 */
	public int findCountByLoginname(String loginname) throws SQLException {
		String sql = "select count(1) from t_user where loginname=?";
		/*
		 * 如果你对dbutils不是很了解，那么请去看“相关视频”
		 * QueryRunner的query()方法用来执行select语句
		 *   我们知道执行select得到的是ResultSet
		 *   ScalarHandler是ResultSetHandler接口的实现类，ResultSetHandler用来把结果集映射成对象！
		 *   而ScalarHandler的作用是把单行单列的结果集映射成一个对象。因为结果集是数字类型，所以它就是Number类型。
		 */
		Number cnt = (Number)qr.query(sql, new ScalarHandler(), loginname);
		return cnt == null ? 0 : cnt.intValue();
	}

	/**
	 * 查询指定email的用户个数
	 * @param email
	 * @return
	 * @throws SQLException
	 */
	public int findCountByEmail(String email) throws SQLException {
		String sql = "select count(1) from t_user where email=?";
		Number cnt = (Number)qr.query(sql, new ScalarHandler(), email);
		return cnt == null ? 0 : cnt.intValue();
	}

	/**
	 * 添加用户
	 * @param form
	 * @throws SQLException 
	 */
	public void add(User form) throws SQLException {
		String sql = "insert into t_user values(?,?,?,?,?,?)";
		Object[] params = {form.getUid(), form.getLoginname(), 
				form.getLoginpass(), form.getEmail(), 
				form.isStatus(), form.getActivationCode()};
		qr.update(sql, params);
		System.out.println(params);
	}
	
	/**
	 * 按激活码查询用户
	 * @param activationCode
	 * @return
	 * @throws SQLException 
	 */
	public User findByActivationCode(String activationCode) throws SQLException {
		String sql = "select * from t_user where activationCode=?";
		return qr.query(sql, new BeanHandler<User>(User.class), activationCode);
	}
	
	/**
	 * 修改指定用户的状态
	 * @param uid
	 * @param status
	 * @throws SQLException 
	 */
	public void updateStatus(String uid, boolean status) throws SQLException {
		String sql = "update t_user set status=? where uid=?";
		qr.update(sql, status, uid);
	}
	
	/**
	 * 按登录名和登录密码查询
	 * @param loginname
	 * @param loginpass
	 * @return
	 * @throws SQLException
	 */
	public User findByLoginnameAndLoginpass(String loginname, String loginpass) throws SQLException {
		String sql = "select * from t_user where loginname=? and loginpass=?";
		return qr.query(sql, new BeanHandler<User>(User.class), loginname, loginpass);
	}
	
	/**
	 * 校验密码
	 * @param uid
	 * @param loginpass
	 * @return
	 * @throws SQLException 
	 */
	public boolean validateLoginpass(String uid, String loginpass) throws SQLException {
		String sql = "select count(1) from t_user where uid=? and loginpass=?";
		Number cnt = (Number)qr.query(sql, new ScalarHandler(), uid, loginpass);
		return cnt == null ? false : cnt.intValue() > 0;
	}

	/**
	 * 修改密码
	 * @param uid
	 * @param newloginpass
	 * @throws SQLException 
	 */
	public void updatePassword(String uid, String newloginpass) throws SQLException {
		String sql = "update t_user set loginpass=? where uid=?";
		qr.update(sql, newloginpass, uid);
	}
}
