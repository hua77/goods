package cn.itcast.bookstore.book.web.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.itcast.bookstore.book.domain.Book;
import cn.itcast.bookstore.book.service.BookService;
import cn.itcast.commons.CommonUtils;
import cn.itcast.pager.PageBean;
import cn.itcast.servlet.BaseServlet;

public class BookServlet extends BaseServlet {
	private BookService bookService = new BookService();
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
		System.out.println(cid);
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
		return "f:/jsps/book/list.jsp";
	}
	
	/**
	 * 按图名模糊查询
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String findByBname(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		/*
		 * 1. 获取pc
		 */
		int pc = getPageCode(req);
		/*
		 * 2. 获取条件，bname
		 */
		String bname = req.getParameter("bname");
		System.out.println(bname);
		/*
		 * 3. 调用service方法，得到PageBean
		 */
		PageBean<Book> pb = bookService.findByBname(bname, pc);
		/*
		 * 4. 给PageBean设置url
		 */
		pb.setUrl(getUrl(req));
		/*
		 * 5. 保存PageBean到request中，转发到/jsps/book/list.jsp
		 */
		req.setAttribute("pb", pb);
		return "f:/jsps/book/list.jsp";
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
		return "f:/jsps/book/list.jsp";
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
		return "f:/jsps/book/list.jsp";
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
		return "f:/jsps/book/list.jsp";
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
		Book book = bookService.findByBid(req.getParameter("bid"));
		req.setAttribute("book", book);
		return "f:/jsps/book/desc.jsp";
	}
}
