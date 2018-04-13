package cn.itcast.bookstore.admin.book.web.servlete;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.itcast.bookstore.book.domain.Book;
import cn.itcast.bookstore.book.service.BookService;
import cn.itcast.bookstore.category.domain.Category;
import cn.itcast.bookstore.category.service.CategoryService;
import cn.itcast.commons.CommonUtils;
import cn.itcast.pager.PageBean;
import cn.itcast.servlet.BaseServlet;

public class AdminBookServlet extends BaseServlet {
	private BookService bookService = new BookService();
	private CategoryService categoryService = new CategoryService();

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
	
	/**
	 * 按分类查询
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String findByCategory(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		/*
		 * 1. 获取pc
		 */
		int pc = getPageCode(req);
		/*
		 * 2. 获取条件，cid
		 */
		String cid = req.getParameter("cid");
		/*
		 * 3. 调用service方法，得到PageBean
		 */
		PageBean<Book> pb = bookService.findByCatgory(cid, pc);
		/*
		 * 4. 给PageBean设置url
		 */
		pb.setUrl(getUrl(req));
		/*
		 * 5. 保存PageBean到request中，转发到/jsps/book/list.jsp
		 */
		req.setAttribute("pb", pb);
		return "f:/adminjsps/admin/book/list.jsp";
	}
	
	/**
	 * 按作者查询
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String findByAuthor(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		/*
		 * 1. 获取pc
		 */
		int pc = getPageCode(req);
		/*
		 * 2. 获取条件，bname
		 */
		String author = req.getParameter("author");
		/*
		 * 3. 调用service方法，得到PageBean
		 */
		PageBean<Book> pb = bookService.findByAuthor(author, pc);
		/*
		 * 4. 给PageBean设置url
		 */
		pb.setUrl(getUrl(req));
		/*
		 * 5. 保存PageBean到request中，转发到/jsps/book/list.jsp
		 */
		req.setAttribute("pb", pb);
		return "f:/adminjsps/admin/book/list.jsp";
	}
	
	/**
	 * 按出版社查询
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String findByPress(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		/*
		 * 1. 获取pc
		 */
		int pc = getPageCode(req);
		/*
		 * 2. 获取条件，bname
		 */
		String press = req.getParameter("press");
		/*
		 * 3. 调用service方法，得到PageBean
		 */
		PageBean<Book> pb = bookService.findByPress(press, pc);
		/*
		 * 4. 给PageBean设置url
		 */
		pb.setUrl(getUrl(req));
		/*
		 * 5. 保存PageBean到request中，转发到/jsps/book/list.jsp
		 */
		req.setAttribute("pb", pb);
		return "f:/adminjsps/admin/book/list.jsp";
	}
	
	/**
	 * 多条件组合查询
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String findByCombination(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		/*
		 * 1. 获取pc
		 */
		int pc = getPageCode(req);
		/*
		 * 2. 获取条件，bname
		 */
		Book book = CommonUtils.toBean(req.getParameterMap(), Book.class);
		/*
		 * 3. 调用service方法，得到PageBean
		 */
		PageBean<Book> pb = bookService.findByCombination(book, pc);
		/*
		 * 4. 给PageBean设置url
		 */
		pb.setUrl(getUrl(req));
		/*
		 * 5. 保存PageBean到request中，转发到/jsps/book/list.jsp
		 */
		req.setAttribute("pb", pb);
		return "f:/adminjsps/admin/book/list.jsp";
	}
	
	/**
	 *  加载图书
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String load(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Book book = bookService.findByBid(req.getParameter("bid"));//获取图书
		req.setAttribute("book", book);//保存图书
		req.setAttribute("parents", categoryService.loadParents());//加载所有一级分类
		req.setAttribute("children", categoryService.loadChildren(
				book.getCategory().getParent().getCid()));//加载当前一级分类下所有2级分类
		return "f:/adminjsps/admin/book/desc.jsp";
	}
	
	/**
	 * 添加图书：第一步
	 * 加载所有1级分类
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String addPre(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		req.setAttribute("parents", categoryService.loadParents());
		return "f:/adminjsps/admin/book/add.jsp";
	}
	
	/**
	 * 加载所有2级分类，响应json
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String loadChildren(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		/*
		 * 1. 获取1级分类的id
		 */
		String pid = req.getParameter("pid");
		/*
		 * 2. 获取指定1级分类的所有2级分类
		 */
		List<Category> children = categoryService.loadChildren(pid);
		/*
		 * 3. 转换成json，返回给客户端
		 */
		resp.getWriter().print(toJson(children));
		return null;
	}
	
	/*
	 * 把一个List<Category>转换成javascript的数组
	 */
	public String toJson(List<Category> categoryList) {
		StringBuilder sb = new StringBuilder("[");
		for(int i = 0; i < categoryList.size(); i++) {
			sb.append(toJson(categoryList.get(i)));
			if(i < categoryList.size() - 1) {
				sb.append(",");
			}
		}
		sb.append("]");
		return sb.toString();
	}
	
	/*
	 * 把一个Category对象，转换成一个javascript对象
	 */
	public String toJson(Category category) {
		StringBuilder sb = new StringBuilder("{");
		sb.append("\"cid\":\"").append(category.getCid()).append("\",");
		sb.append("\"cname\":\"").append(category.getCname()).append("\"}");
		return sb.toString();
	}
	
	/**
	 * 编辑图书
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String edit(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Book book = CommonUtils.toBean(req.getParameterMap(), Book.class);
		Category child = CommonUtils.toBean(req.getParameterMap(), Category.class);
		book.setCategory(child);
		bookService.edit(book);
		req.setAttribute("msg", "编辑图书成功！");
		return "f:/adminjsps/msg.jsp";
	}
	
	/**
	 * 删除图书
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String delete(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		/*
		 * 1. 获取bid，加载Book
		 */
		Book book = bookService.findByBid(req.getParameter("bid"));
		/*
		 * 2. 删除大图和小图
		 */
		String image_w = book.getImage_w();
		String path = this.getServletContext().getRealPath("/" + image_w);
		new File(path).delete();
		
		String image_b = book.getImage_b();
		path = this.getServletContext().getRealPath("/" + image_b);
		new File(path).delete();
		/*
		 * 3. 通过service完成删除
		 */
		bookService.delete(book.getBid());
		/*
		 * 4. 保存功能信息，转发到msg.jsp
		 */
		req.setAttribute("msg", "删除图书成功！");
		return "f:/adminjsps/msg.jsp";
	}
}
