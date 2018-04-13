package cn.itcast.bookstore.admin.category.web.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.itcast.bookstore.category.domain.Category;
import cn.itcast.bookstore.category.service.CategoryService;
import cn.itcast.commons.CommonUtils;
import cn.itcast.servlet.BaseServlet;

public class AdminCategoryServlet extends BaseServlet {
	private CategoryService categoryService = new CategoryService();
	
	/**
	 * 获取所有分类，在图书管理的主页左侧显示
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String findAllForBook(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		/*
		 * 1. 使用service查询，得到所有分类
		 * 2. 保存到request中
		 * 3. 转发到list.jsp显示
		 */
		List<Category> categoryList = categoryService.findAll();
		req.setAttribute("parents", categoryList);
		return "f:/adminjsps/admin/book/left.jsp";
	}
	
	/**
	 *  查询所有分类
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String findAll(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		/*
		 * 1. 使用service查询，得到所有分类
		 * 2. 保存到request中
		 * 3. 转发到list.jsp显示
		 */
		List<Category> categoryList = categoryService.findAll();
		req.setAttribute("categoryList", categoryList);
		return "f:/adminjsps/admin/category/list.jsp";
	}
	
	/**
	 * 添加一级分类
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String addOneLevel(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		/*
		 * 1. 封装表单数据到Category
		 */
		Category category = CommonUtils.toBean(req.getParameterMap(), Category.class);
		/*
		 * 2. 调用service方法完成添加
		 */
		category.setCid(CommonUtils.uuid());
		categoryService.add(category);
		/*
		 * 3. 调用本类findAll()，回到list.jsp显示所有分类
		 */
		return findAll(req, resp);
	}
	
	/**
	 * 添加二级分类：第一步
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String addTwoLevelPre(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		/*
		 * 1. 获取当前一级分类的cid
		 */
		String pid = req.getParameter("pid");
		/*
		 * 2. 获取所有1级分类
		 */
		List<Category> parents = categoryService.loadParents();
		/*
		 * 3. 都保存到request中
		 */
		req.setAttribute("pid", pid);
		req.setAttribute("parents", parents);
		/*
		 * 4. 转发到/adminjsps/admin/category/add2.jsp
		 */
		return "f:/adminjsps/admin/category/add2.jsp";
	}
	
	/**
	 * 添加二级分类：第二步
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String addTwoLevel(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		/*
		 * 1. 封装表单数据到Category child
		 */
		Category child = CommonUtils.toBean(req.getParameterMap(), Category.class);
		child.setCid(CommonUtils.uuid());
		/*
		 * 2. 创建Category parent，并把表单中的pid赋给parent的cid
		 */
		Category parent = new Category();
		parent.setCid(req.getParameter("pid"));
		/*
		 * 3. 两层把parent赋给child
		 */
		child.setParent(parent);
		/*
		 * 4. 调用service的方法完成添加
		 */
		categoryService.add(child);
		/*
		 * 5. 转发到list.jsp显示所有分类，调用findAll()
		 */
		return findAll(req, resp);
	}
	
	/**
	 * 编辑一级分类：第一步
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String editOneLevelPre(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		/*
		 * 1. 获取要编辑的分类id
		 */
		String cid = req.getParameter("cid");
		/*
		 * 2. 通过cid来获取Category
		 */
		Category category =  categoryService.load(cid);
		/*
		 * 3. 保存到request中
		 */
		req.setAttribute("category", category);
		/*
		 * 4. 转发到/adminjsps/admin/category/edit.jsp
		 */
		return "f:/adminjsps/admin/category/edit.jsp";
	}
	
	/**
	 * 编辑一级分类：第二步
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String editOneLevel(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		/*
		 * 1. 把表单数据封装到Category中
		 */
		Category category = CommonUtils.toBean(req.getParameterMap(), Category.class);
		/*
		 * 2. 调用service方法完成编辑
		 */
		categoryService.edit(category);
		/*
		 * 3. 回到list.jsp
		 */
		return findAll(req, resp);
	}
	
	/**
	 * 编辑二级分类：第一步
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String editTwoLevelPre(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		/*
		 * 1. 获取cid，添加分类
		 */
		String cid = req.getParameter("cid");
		Category child = categoryService.load(cid);
		/*
		 * 2. 加载所有1级分类
		 */
		List<Category> parents = categoryService.loadParents();
		/*
		 * 3. 保存到request中
		 */
		req.setAttribute("child", child);
		req.setAttribute("parents", parents);
		/*
		 * 4. 转发到edit2.jsp
		 */
		return "f:/adminjsps/admin/category/edit2.jsp";
	}
	
	/**
	 * 编辑二级分类：第二步
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String editTwoLevel(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		/*
		 * 1. 封装表单参数到child
		 */
		Category child = CommonUtils.toBean(req.getParameterMap(), Category.class);
		/*
		 * 2. 创建父分类，把表单的pid赋给父分类的cid
		 */
		Category parent = new Category();
		parent.setCid(req.getParameter("pid"));
		child.setParent(parent);
		/*
		 * 3. 调用service完成编辑
		 */
		categoryService.edit(child);
		/*
		 * 4.回到list.jsp
		 */
		return findAll(req, resp);
	}
	
	/**
	 * 删除一级分类
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String deleteOneLevel(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		/*
		 * 1. 获取cid
		 */
		String cid = req.getParameter("cid");
		/*
		 * 2. 调用service方法得到boolean
		 */
		if(categoryService.deleteOneLevel(cid)) {//3. 如果返回的是true，说明成功了，返回到list.jsp
			return findAll(req, resp);
		} else {//4. 如果失败了，保存失败信息，转发到msg.jsp
			req.setAttribute("msg", "该一级分类下存在二级分类，不能删除！");
			return "f:/adminjsps/msg.jsp";
		}
	}
	
	public String deleteTwoLevel(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		/*
		 * 1. 获取cid
		 */
		String cid = req.getParameter("cid");
		/*
		 * 2. 调用service方法得到boolean
		 */
		if(categoryService.deleteTwoLevel(cid)) {//3. 如果返回的是true，说明成功了，返回到list.jsp
			return findAll(req, resp);
		} else {//4. 如果失败了，保存失败信息，转发到msg.jsp
			req.setAttribute("msg", "该二级分类下存在图书，不能删除！");
			return "f:/adminjsps/msg.jsp";
		}
	}
}
