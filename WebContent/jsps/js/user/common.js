function _go() {
	$("#imgVerifyCode").attr("src", "/goods/VerifyCodeServlet?" + new Date().getTime());
}