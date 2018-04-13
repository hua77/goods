package cn.itcast.bookstore.admin.book.web.servlete;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import cn.itcast.bookstore.book.domain.Book;
import cn.itcast.bookstore.book.service.BookService;
import cn.itcast.bookstore.category.domain.Category;
import cn.itcast.bookstore.category.service.CategoryService;
import cn.itcast.commons.CommonUtils;

public class AdminAddBookServlet extends HttpServlet {
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=utf-8");
		
		/*
		 * 1. 上传解析三步
		 */
		DiskFileItemFactory factory = new DiskFileItemFactory(20*1024, new File("F:/F/temp"));
		ServletFileUpload sfu = new ServletFileUpload(factory);
		sfu.setFileSizeMax(80 * 1024);//限制单个上传文件的大小为80KB
		List<FileItem> fileItemList = null;
		try {
			fileItemList = sfu.parseRequest(request);
		} catch (FileUploadException e) {
			/*
			 * 2. 处理异常
			 * 如果抛出异常一定是单个上传的文件超出了80KB
			 * 保存所有1级分类
			 * 我们要保存错误信息，转发到add.jsp页面显示
			 */
			error("您上传的文件超出了80KB!", request, response);
			return;
		}
		/*
		 * 3. 创建Book，把表单数据封装到Book中
		 */
		// 把List<FileItem>转换成Map，然后把Map转换成Book
		Map<String,Object> map = new HashMap<String,Object>();
		for(FileItem fileItem : fileItemList) {
			if(fileItem.isFormField()) {
				map.put(fileItem.getFieldName(), fileItem.getString("UTF-8"));
			}
		}
		Book book = CommonUtils.toBean(map, Book.class);
		Category category = CommonUtils.toBean(map, Category.class);
		book.setCategory(category);
		book.setBid(CommonUtils.uuid());
		
		/*
		 * 4. 获取图片路径，截取图片名称，校验扩展名，保存图片，把图片的保存路径设置book
		 */
		if(!saveImage(book, fileItemList.get(1), true, request, response)) {
			return;
		}
		if(!saveImage(book, fileItemList.get(2), false, request, response)) {
			return;
		}
		/*
		 * 5. 使用bookService的add(Book)方法保存book
		 */
		BookService bookService = new BookService();
		bookService.add(book);
		/*
		 * 6. 转发到msg.jsp显示成功信息
		 */
		request.setAttribute("msg", "添加图书成功！");
		request.getRequestDispatcher("/adminjsps/msg.jsp").forward(request, response);
	}
	
	/*
	 * 获取图片路径，截取图片名称，校验扩展名，保存图片，把图片的保存路径设置book
	 */
	private boolean saveImage(Book book, FileItem fileItem, boolean big,
			HttpServletRequest request, HttpServletResponse response) 
					throws ServletException, IOException {
		/*
		 * 1. 获取文件路径，截取文件名称
		 */
		String name = fileItem.getName();//获取上传文件的路径
		int index = name.lastIndexOf("\\");
		if(index != -1) {
			name = name.substring(index+1);
		}
		/*
		 * 2. 校验文件扩展名
		 * jpg、bmp、png
		 */
		if(!name.toLowerCase().endsWith(".jpg") && 
				!name.toLowerCase().endsWith(".png") &&
				!name.toLowerCase().endsWith(".bmp")) {
			error("您上传的图片扩展名错误！", request, response);
			return false;
		}
		/*
		 * 3. 保存图片
		 */
		// 为了不让同名图片相互覆盖，所以我们需要给文件名称添加uuid前缀
		name = CommonUtils.uuid() + "_" + name;
		// 获取保存路径
		String savepath = this.getServletContext().getRealPath("/book_img");
		// 创建目标文件
		File destFile = new File(savepath, name);
		// 保存图片
		try {
			fileItem.write(destFile);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		/*
		 * 4. 把图片路径设置给Book的属性
		 */
		String imgPath = "book_img/" + name;
		if(big) {//设置大图
			book.setImage_w(imgPath);
		} else {//设置小图
			book.setImage_b(imgPath);
		}
		return true;
	}
	
	/*
	 * 校验失败时，转发到add.jsp页面
	 */
	private void error(String msg, HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		request.setAttribute("msg", msg);
		request.setAttribute("parents", new CategoryService().loadParents());
		request.getRequestDispatcher("/adminjsps/admin/book/add.jsp").forward(request, response);
	}
}
