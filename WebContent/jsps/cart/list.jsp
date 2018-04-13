<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

 
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>cartlist.jsp</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
	<script src="<c:url value='/jquery/jquery-1.5.1.js'/>"></script>
	<script src="<c:url value='/js/round.js'/>"></script>
	
	<link rel="stylesheet" type="text/css" href="<c:url value='/jsps/css/cart/list.css'/>">
<script type="text/javascript">
$(function() {
	showtotal();
	// 给全选框添加点击事件，设置所有条目的复选框与全选框同步！
	$("#selectAll").click(function() {
		var flag = $("#selectAll").attr("checked");
		setItemCheckbox(flag);//让所有条目同步
		setJieSuanBtn(flag);//让结算按钮同步
		showtotal();//重新的计算合计并显示
	});
	
	// 给所有的条目复选框添加事件，让它与selectAll和jiesuan同步
	$(":checkbox[name=checkboxBtn]").click(function() {
		/*
		1. 获取所有条目复选框的个数
		2. 获取所有被选中的条目复选框个数
		*/
		var allLen = $(":checkbox[name=checkboxBtn]").length;
		var selectedLen = $(":checkbox[name=checkboxBtn][checked=true]").length;

		if(allLen == selectedLen) {//如果全选了
			$("#selectAll").attr("checked", true);//把全选框勾选
			setJieSuanBtn(true);//让结算按钮有效
		} else if(selectedLen == 0) {//如果全没选
			$("#selectAll").attr("checked", false);//把全选框不勾选
			setJieSuanBtn(false);//让结算按钮无效			
		} else {
			$("#selectAll").attr("checked", false);//把全选框不勾选
			setJieSuanBtn(true);//让结算按钮有效				
		}
		
		showtotal();//重新的计算合计并显示
	});
	
	// jian添加事件
	$(".jian").click(function() {
		// 1. 获取当前按钮上的id
		var cartItemId = $(this).attr("id").substring(0,32);
		// 2. 通过这个id查询找对应文本框内容
		var quantity = $("#" + cartItemId + "Quantity").val();
		// 3. 判断quantity是否为1，如果为1，弹出警告框，问是否要删除，如果是，那么删除
		if(quantity == 1) {
			if(confirm("您是否要删除该条目？")) {
				location = "<c:url value='/CartItemServlet?method=delete&cartItemIds='/>" + cartItemId;
			}
		} else {
			sendUpdateQuantity(cartItemId, quantity-1);
		}
	});
	
	// 给jia按钮添加事件
	$(".jia").click(function() {
		var cartItemId = $(this).attr("id").substring(0,32);
		var quantity = Number($("#" + cartItemId + "Quantity").val());
		sendUpdateQuantity(cartItemId, quantity+1);
	});
});

//　向服务器发送修改数量的请求
function sendUpdateQuantity(cartItemId, quantity) {
	$.ajax({
		url:"<c:url value='/CartItemServlet'/>",
		data:{method:"updateQuantity", cartItemId:cartItemId, quantity:quantity},
		type:"POST",
		dataType:"json",
		async:false,
		cache:false,
		success:function(result) {//结果包含了数量和小计
			// 通过结果来更新页面
			$("#" + cartItemId + "Quantity").val(result.quantity);
			$("#" + cartItemId + "Subtotal").text(result.subtotal);
			showtotal();
		},
		error:function(a,b,c) {
			alert(c);
		}
	});
}

//设置所有条目的复选框的状态
function setItemCheckbox(flag) {
	$(":checkbox[name=checkboxBtn]").attr("checked", flag);
}

//设置结算按钮的样式
function setJieSuanBtn(flag) {
	if(flag) {
		$("#jiesuan").removeClass("kill").addClass("jiesuan");
		$("#jiesuan").unbind("click");//移除该元素上的click事件
	} else {
		$("#jiesuan").removeClass("jiesuan").addClass("kill");
		// 给该元素添加click事件，让其无效
		$("#jiesuan").click(function() {
			return false;
		});
	}
}

// 计算所有被选中的条目的小计之和，并且显示出来
function showtotal() {
	/*
	1. 获取所有被选中的复选框，循环遍历之
	*/
	var total = 0;
	$(":checkbox[name=checkboxBtn][checked=true]").each(function() {
		/*
		2. 获取复选框的值，即cartItemId，通过它找到对应的subtotal，然后累加
		*/
		var text = $("#" + $(this).val() + "Subtotal").text();
		total += Number(text);
	});
	$("#total").text(round(total, 2));
}

// 批量删除
function batchDelete() {
	var cartItemIdArray = new Array();//创建数组
	var index = 0;
	//获取所有勾选的条目，把它的id放到一个数组中，最后再把数组转换成字符串
	$(":checkbox[name=checkboxBtn][checked=true]").each(function() {
		cartItemIdArray[index++] = $(this).val();
	});
	location="/goods/CartItemServlet?method=delete&cartItemIds=" + cartItemIdArray;
}

// 给结算添加事件
function jieSuan() {
	//1. 获取被选中的所有条目的id，放到数组中，转发成字符串
	var cartItemIdArray = new Array();//创建数组
	var index = 0;
	$(":checkbox[name=checkboxBtn][checked=true]").each(function() {
		cartItemIdArray[index++] = $(this).val();
	});
	
	//2. 把字符串赋值给表单的hidden字段
	$("#cartItemIds").val(cartItemIdArray.toString());
	//3. 提交表单
	$("#form1").submit();
}
</script>
  </head>
  <body>
<c:choose>
  <c:when test="${empty cartItemList or fn:length(cartItemList) == 0 }">
	<table width="95%" align="center" cellpadding="0" cellspacing="0">
		<tr>
			<td align="right">
				<img align="top" src="<c:url value='/images/icon_empty.png'/>"/>
			</td>
			<td>
				<span class="spanEmpty">您的购物车中暂时没有商品</span>
			</td>
		</tr>
	</table>  
  </c:when>
  <c:otherwise>
<br/>
<br/>


<table width="95%" align="center" cellpadding="0" cellspacing="0">
	<tr align="center" bgcolor="#efeae5">
		<td align="left" width="50px">
			<input type="checkbox" id="selectAll" checked="checked"/><label for="selectAll">全选</label>
		</td>
		<td colspan="2">商品名称</td>
		<td>单价</td>
		<td>数量</td>
		<td>小计</td>
		<td>操作</td>
	</tr>



<c:forEach items="${cartItemList }" var="item">
	<tr align="center">
		<td align="left">
			<%--每个条目都一个复选框，让这个复选框的值为当前条目的cartItemId --%>
			<input value="${item.cartItemId }" type="checkbox" name="checkboxBtn" checked="checked"/>
		</td>
		<td align="left" width="70px">
			<a class="linkImage" href="<c:url value='/jsps/book/desc.jsp'/>"><img border="0" width="54" align="top" src="<c:url value='/${item.book.image_b }'/>"/></a>
		</td>
		<td align="left" width="400px">
		    <a href="<c:url value='/jsps/book/desc.jsp'/>"><span>${item.book.bname }</span></a>
		</td>
		<td><span>&yen;<span class="currPrice" id="${item.cartItemId }CurrPrice">${item.book.currPrice }</span></span></td>
		<td>
			<a class="jian" id="${item.cartItemId }Jian"></a><input class="quantity" readonly="readonly" id="${item.cartItemId }Quantity" type="text" value="${item.quantity }"/><a class="jia" id="${item.cartItemId }Jia"></a>
		</td>
		<td width="100px">
			<span class="price_n">&yen;<span class="subTotal" id="${item.cartItemId }Subtotal">${item.subtotal }</span></span>
		</td>
		<td>
			<a href="<c:url value='/CartItemServlet?method=delete&cartItemIds=${item.cartItemId }'/>">删除</a>
		</td>
	</tr>
</c:forEach>


















	
	<tr>
		<td colspan="4" class="tdBatchDelete">
			<a href="javascript:batchDelete();">批量删除</a>
		</td>
		<td colspan="3" align="right" class="tdTotal">
			<span>总计：</span><span class="price_t">&yen;<span id="total"></span></span>
		</td>
	</tr>
	<tr>
		<td colspan="7" align="right">
			<a href="javascript:jieSuan();" id="jiesuan" class="jiesuan"></a>
		</td>
	</tr>
</table>
	<form id="form1" action="<c:url value='/CartItemServlet'/>" method="post">
		<input type="hidden" name="cartItemIds" id="cartItemIds"/>
		<input type="hidden" name="method" value="loadCartItemList"/>
	</form>
  </c:otherwise>
</c:choose>

  </body>
</html>
