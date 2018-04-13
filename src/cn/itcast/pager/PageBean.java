package cn.itcast.pager;

import java.util.List;

/**
 * 封装了分页显示相关的数据
 * 
 * @author qdmmy6
 * 
 */
public class PageBean<T> {
	private int pc;// 当前页码
	private int tr;// 总记录数
	private int ps;// 每页记录数
	private String url;// 查询条件
	private List<T> beanList;// 当前页记录

	public int getPc() {
		return pc;
	}

	public void setPc(int pc) {
		this.pc = pc;
	}

	/**
	 * 返回总页数
	 * @return
	 */
	public int getTp() {
		int tp = tr / ps;
		return tr % ps == 0 ? tp : tp+1;
	}

	public int getTr() {
		return tr;
	}

	public void setTr(int tr) {
		this.tr = tr;
	}

	public int getPs() {
		return ps;
	}

	public void setPs(int ps) {
		this.ps = ps;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public List<T> getBeanList() {
		return beanList;
	}

	public void setBeanList(List<T> beanList) {
		this.beanList = beanList;
	}
}
