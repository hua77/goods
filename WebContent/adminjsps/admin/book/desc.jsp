<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>book_desc.jsp</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<meta http-equiv="content-type" content="text/html;charset=utf-8">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
<link rel="stylesheet" type="text/css" href="<c:url value='/adminjsps/admin/css/book/desc.css'/>">
<link rel="stylesheet" type="text/css" href="<c:url value='/jquery/jquery.datepick.css'/>">
<script type="text/javascript" src="<c:url value='/jquery/jquery-1.5.1.js'/>"></script>
<script type="text/javascript" src="<c:url value='/jquery/jquery.datepick.js'/>"></script>
<script type="text/javascript" src="<c:url value='/jquery/jquery.datepick-zh-CN.js'/>"></script>

<script type="text/javascript" src="<c:url value='/adminjsps/admin/js/book/desc.js'/>"></script>

<script type="text/javascript">

$(function() {
	$("#box").attr("checked", false);
	$("#formDiv").css("display", "none");
	$("#show").css("display", "");	
	
	// 操作和显示切换
	$("#box").click(function() {
		if($(this).attr("checked")) {
			$("#show").css("display", "none");
			$("#formDiv").css("display", "");
		} else {
			$("#formDiv").css("display", "none");
			$("#show").css("display", "");		
		}
	});
	
	// 给pid下拉列表添加change事件，让其加载当前1级分类的所有2级分类，显示到cid这个下拉列表中
	$("#pid").change(function() {
		// 1. 获取一级分类下拉列表的值！
		var pid = $("#pid").val();
		// 2. 如果值不为空，发送异步请求，得到所有2级分类
		if(pid) {
			$.ajax({
				url:"/goods/admin/AdminBookServlet",
				data:{method:"loadChildren",pid:pid},
				dataType:"json",
				type:"POST",
				async:true,
				cache:false,
				success: function(array) {//参数是2级分类的数组
					// 3. 把2级分类显示到cid元素中(先删除原来的2级分类，再添加新的2级分类)
					$("#cid").empty();//清空所有选项
					$("#cid").append($("<option>").text("==请选择2级分类=="));//添加头
					for(var i = 0; i < array.length; i++) {
						$("#cid").append($("<option>").val(array[i].cid).text(array[i].cname));
					}
				},
				error:function(a,b,c) {
					alert(c);
				}
			});
		}
	});
	
	$("#editBtn").click(function() {
		var bname = $("#bname").val();
		var currPrice = $("#currPrice").val();
		var price = $("#price").val();
		var discount = $("#discount").val();
		var author = $("#author").val();
		var press = $("#press").val();
		var pid = $("#pid").val();
		var cid = $("#cid").val();
		
		if(!bname || !currPrice || !price || !discount || !author || !press || !pid || !cid) {
			alert("图名、当前价、定价、折扣、作者、出版社、1级分类、2级分类！");
			return false;
		}
		
		if(isNaN(currPrice) || isNaN(price) || isNaN(discount)) {
			alert("当前价、定价、折扣必须是合法小数！");
			return false;
		}
		
		//给表单的hidden[name=method]的字段赋值
		$(":hidden[name=method]").val("edit");
		$("#form").submit();	
	});
	
	$("#delBtn").click(function() {
		$(":hidden[name=method]").val("delete");
		$("#form").submit();
	});
});

</script>
  </head>
  
  <body>
    <input type="checkbox" id="box"><label for="box">编辑或删除</label>
    <br/>
    <br/>
  <div id="show">
    <div class="sm">${book.bname }</div>
    <img align="top" src="<c:url value='/${book.image_w }'/>" class="tp"/>
    <div id="book" style="float:left;">
	    <ul>
	    	<li>商品编号：${book.bid }</li>
	    	<li>当前价：<span class="price_n">&yen;${book.currPrice }</span></li>
	    	<li>定价：<span style="text-decoration:line-through;">&yen;${book.price }</span>　折扣：<span style="color: #c30;">${book.discount }</span>折</li>
	    </ul>
		<hr style="margin-left: 50px; height: 1px; color: #dcdcdc"/>
		<table class="tab">
			<tr>
				<td colspan="3">
					作者：${book.author }著
				</td>
			</tr>
			<tr>
				<td colspan="3">
					出版社：${book.press }</a>
				</td>
			</tr>
			<tr>
				<td colspan="3">出版时间：${book.publishtime }</td>
			</tr>
			<tr>
				<td>版次：${book.edition }</td>
				<td>页数：${book.pageNum }</td>
				<td>字数：${book.wordNum }</td>
			</tr>
			<tr>
				<td width="180">印刷时间：${book.printtime }</td>
				<td>开本：${book.booksize }开</td>
				<td>纸张：${book.paper }</td>
			</tr>
		</table>
	</div>
  </div>
  
  
  <div id='formDiv'>
   <div class="sm">&nbsp;</div>
   <form action="<c:url value='/admin/AdminBookServlet'/>" method="post" id="form">
   	<input type="hidden" name="bid" value="${book.bid }"/>
   	<input type="hidden" name="method"/>
    <img align="top" src="<c:url value='/${book.image_w }'/>" class="tp"/>
    <div style="float:left;">
	    <ul>
	    	<li>商品编号：${book.bid }</li>
	    	<li>书名：　<input id="bname" type="text" name="bname" value="${book.bname }" style="width:500px;"/></li>
	    	<li>当前价：<input id="currPrice" type="text" name="currPrice" value="${book.currPrice }" style="width:50px;"/></li>
	    	<li>定价：　<input id="price" type="text" name="price" value="${book.price }" style="width:50px;"/>
	    	折扣：<input id="discount" type="text" name="discount" value="${book.discount }" style="width:30px;"/>折</li>
	    </ul>
		<hr style="margin-left: 50px; height: 1px; color: #dcdcdc"/>
		<table class="tab">
			<tr>
				<td colspan="3">
					作者：　　<input id="author" type="text" name="author" value="${book.author }" style="width:150px;"/>
				</td>
			</tr>
			<tr>
				<td colspan="3">
					出版社：　<input id="press" type="text" name="press" value="${book.press }" style="width:200px;"/>
				</td>
			</tr>
			<tr>
				<td colspan="3">出版时间：<input id="publishtime" type="text" name="publishtime" value="${book.publishtime }" style="width:100px;"/></td>
			</tr>
			<tr>
				<td>版次：　　<input id="edition" type="text" name="edition" value="${book.edition }" style="width:40px;"/></td>
				<td>页数：　　<input id="pageNum" type="text" name="pageNum" value="${book.pageNum }" style="width:50px;"/></td>
				<td>字数：　　<input id="wordNum" type="text" name="wordNum" value="${book.wordNum }" style="width:80px;"/></td>
			</tr>
			<tr>
				<td width="250px">印刷时间：<input id="printtime" type="text" name="printtime" value="${book.printtime }" style="width:100px;"/></td>
				<td width="250px">开本：　　<input id="booksize" type="text" name="booksize" value="${book.booksize }" style="width:30px;"/></td>
				<td>纸张：　　<input id="paper" type="text" name="paper" value="${book.paper }" style="width:80px;"/></td>
			</tr>
			<tr>
				<td>
					一级分类：<select name="pid" id="pid">
						<option value="">==请选择1级分类==</option>
<c:forEach items="${parents }" var="parent">
	<option value="${parent.cid }" <c:if test="${book.category.parent.cid eq parent.cid }">selected='selected'</c:if>>${parent.cname }</option>
</c:forEach>
					</select>
				</td>
				<td>
					二级分类：<select name="cid" id="cid">
						<option value="">==请选择2级分类==</option>
<c:forEach items="${children }" var="child">
	<option value="${child.cid }" <c:if test="${book.category.cid eq child.cid }">selected='selected'</c:if>>${child.cname }</option>
</c:forEach>
					</select>
				</td>
				<td></td>
			</tr>
			<tr>
				<td colspan="2">
					<input type="button" name="method" id="editBtn" class="btn" value="编　　辑">
					<input type="button" name="method" id="delBtn" class="btn" value="删　　除">
				</td>
				<td></td>
			</tr>
		</table>
	</div>
   </form>
  </div>

  </body>
</html>
