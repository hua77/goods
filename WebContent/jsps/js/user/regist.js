$(function() {
	/*
	 * 1. 错误信息在不存在时，隐藏起来！因为谷歌和火狐在没有信息时也会显示边框和背景图片
	 */
	// 循环遍历每个使用了labelError这个class的元素
	$(".labelError").each(function() {
		var text = $(this).text();//获取当前元素的内容
		if(text) {//如果内容不空
			$(this).css("display", "");//显示元素
		} else {//如果内容为空
			$(this).css("display", "none");//隐藏元素
		}
	});
	
	/*
	 * 2. 让提交按钮上的图片在鼠标进入和离开时进行切换
	 */
	$("#submit").hover(
		// 鼠标进行元素时被调用
		function() {
			$("#submit").attr("src", "/goods/images/regist2.jpg");//修改按钮的src属性
		},
		// 鼠标离开元素时被调用
		function() {
			$("#submit").attr("src", "/goods/images/regist1.jpg");
		}
	);
	
	/*
	 * 3. 在表单提交时进行校验
	 */
	// 当表单提交时执行
	$("#registForm").submit(function() {
		/*
		 * 对所有的表单项进行校验
		 */
		var bool = true;
		$(".input").each(function() {
			var inputId = $(this).attr("id");//获取input的id
			var methodName = "validate" + inputId.substring(0,1).toUpperCase() + inputId.substring(1) + "()";
			if(!eval(methodName)) {
				bool = false;
			}
		});
		return bool;
	});
	
	/*
	 * 4. 给所有的input添加focus和blur事件
	 */
	// 隐藏错误信息
	$(".input").focus(function() {
		var inputId = $(this).attr("id");//获取当前input的id
		var inputErrorId = inputId + "Error";//获取对应的错误label的id
		$("#" + inputErrorId).css("display", "none");
	});
	// 进行校验
	$(".input").blur(function() {
		var inputId = $(this).attr("id");//获取当前input的id
		var methodName = "validate" + inputId.substring(0, 1).toUpperCase() + inputId.substring(1) + "()";
		eval(methodName);
	});
});

/*
 * 校验登录名
 */
function validateLoginname() {
	$("#loginnameError").css("display", "none");
	/*
	 * 一共3项校验
	 *   * 非空校验
	 *   * 长度校验
	 *   * 是否已注册校验
	 */
	var bool = true;
	
	var val = $("#loginname").val();//获取文本框的值
	if(!val) {//非空校验
		//在对应的错误label上显示信息
		$("#loginnameError").text("用户名不能为空！");//设置错误信息
		$("#loginnameError").css("display", "");//让label可见
		bool = false;
	} else if(val.length < 3 || val.length > 20) {//长度校验
		$("#loginnameError").text("用户名长度必须在3~20之间！");//设置错误信息
		$("#loginnameError").css("display", "");//让label可见
		bool = false;
	} else {//是否注册校验
		// 发送异步请求
		$.ajax({
			url:"/goods/UserServlet",//请求服务器端的URL
			data:{method:"validateLoginname", loginname:val},//发送给服务器的参数
			type:"POST",//请求方式
			dataType:"json",//服务器返回的数据的类型
			async:false,//是否顺序执行
			cache:false,//不让浏览器进行缓存
			success:function(result) {//异步请求成功时执行的函数
				if(!result) {
					$("#loginnameError").text("用户名已被注册！");//设置错误信息
					$("#loginnameError").css("display", "");//让label可见
					bool = false;
				}
			}
		});
	}
	
	return bool;
}


/*
 * 校验登录密码
 */
function validateLoginpass() {
	$("#loginpassError").css("display", "none");
	/*
	 * 一共3项校验
	 *   * 非空校验
	 *   * 长度校验
	 */
	var bool = true;
	
	var val = $("#loginpass").val();//获取文本框的值
	if(!val) {//非空校验
		//在对应的错误label上显示信息
		$("#loginpassError").text("密码不能为空！");//设置错误信息
		$("#loginpassError").css("display", "");//让label可见
		bool = false;
	} else if(val.length < 3 || val.length > 20) {//长度校验
		$("#loginpassError").text("密码长度必须在3~20之间！");//设置错误信息
		$("#loginpassError").css("display", "");//让label可见
		bool = false;
	}
	return bool;	
}

/*
 * 校验确认密码
 */
function validateReloginpass() {
	$("#reloginpassError").css("display", "none");
	/*
	 * 一共3项校验
	 *   * 非空校验
	 *   * 长度校验
	 */
	var bool = true;
	
	var val = $("#reloginpass").val();//获取文本框的值
	if(!val) {//非空校验
		//在对应的错误label上显示信息
		$("#reloginpassError").text("确认密码不能为空！");//设置错误信息
		$("#reloginpassError").css("display", "");//让label可见
		bool = false;
	} else if(val != $("#loginpass").val()) {//两次输入是否一致
		$("#reloginpassError").text("两次密码输入不一致！");//设置错误信息
		$("#reloginpassError").css("display", "");//让label可见
		bool = false;
	}
	return bool;	
}

/*
 * 校验email
 */
function validateEmail() {
	$("#emailError").css("display", "none");
	/*
	 * 一共3项校验
	 *   * 非空校验
	 *   * 格式校验
	 *   * 是否已注册校验
	 */
	var bool = true;
	
	var val = $("#email").val();//获取文本框的值
	if(!val) {//非空校验
		//在对应的错误label上显示信息
		$("#emailError").text("Email不能为空！");//设置错误信息
		$("#emailError").css("display", "");//让label可见
		bool = false;
	} else if(!/^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+((\.[a-zA-Z0-9_-]{2,3}){1,2})$/.test(val)) {//格式校验
		$("#emailError").text("Email格式错误！");//设置错误信息
		$("#emailError").css("display", "");//让label可见
		bool = false;
	} else {//是否注册校验
		// 发送异步请求
		$.ajax({
			url:"/goods/UserServlet",//请求服务器端的URL
			data:{method:"validateEmail", email:val},//发送给服务器的参数
			type:"POST",//请求方式
			dataType:"json",//服务器返回的数据的类型
			async:false,//是否顺序执行
			cache:false,//不让浏览器进行缓存
			success:function(result) {//异步请求成功时执行的函数
				if(!result) {
					$("#emailError").text("Email已被注册！");//设置错误信息
					$("#emailError").css("display", "");//让label可见
					bool = false;
				}
			}
		});
	}
	return bool;	
}

/*
 * 校验验证码
 */
function validateVerifyCode() {
	$("#verifyCodeError").css("display", "none");
	/*
	 * 一共3项校验
	 *   * 非空校验
	 *   * 长度校验
	 *   * 是否已注册校验
	 */
	var bool = true;
	
	var val = $("#verifyCode").val();//获取文本框的值
	if(!val) {//非空校验
		//在对应的错误label上显示信息
		$("#verifyCodeError").text("验证码不能为空！");//设置错误信息
		$("#verifyCodeError").css("display", "");//让label可见
		bool = false;
	} else if(val.length != 4) {//格式校验
		$("#verifyCodeError").text("验证码长度错误！");//设置错误信息
		$("#verifyCodeError").css("display", "");//让label可见
		bool = false;
	} else {//是否正确
		// 发送异步请求
		$.ajax({
			url:"/goods/UserServlet",//请求服务器端的URL
			data:{method:"validateVerifyCode", verifyCode:val},//发送给服务器的参数
			type:"POST",//请求方式
			dataType:"json",//服务器返回的数据的类型
			async:false,//是否顺序执行
			cache:false,//不让浏览器进行缓存
			success:function(result) {//异步请求成功时执行的函数
				if(!result) {
					$("#verifyCodeError").text("验证码错误！");//设置错误信息
					$("#verifyCodeError").css("display", "");//让label可见
					bool = false;
				}
			}
		});
	}
	return bool;		
}

