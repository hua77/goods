<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>My JSP 'regist.jsp' starting page</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
	<link rel="stylesheet" type="text/css" href="<c:url value='/jsps/css/user/regist.css'/>">
	<link rel="stylesheet" type="text/css" href="<c:url value='/css/css.css'/>">
	<script type="text/javascript" src="<c:url value='/jquery/jquery-1.5.1.js'/>"></script>
	<script type="text/javascript" src="<c:url value='/jsps/js/user/regist.js'/>"></script>
	<script type="text/javascript" src="<c:url value='/jsps/js/user/common.js'/>"></script>
  </head>
  <%-- <c:url value='/UserServlet'/> --%>
  <body>
<form action=<c:url value='/UserServlet'/> method="post" id="registForm">
  <input name="method" value="regist" type="hidden"/>
<div class="divBody">
  <div class="divTitle">
    <span class="spanTitle">新用户注册</span>
  </div>
  <div class="divContent">
    <table class="table1">
      <tr class="tr1">
        <td class="tdLabel">用户名：</td>
        <td class="tdInput">
          <input class="input" type="text" name="loginname" value="" id="loginname"/>
        </td>
        <td class="tdError">
          <label class="labelError" id="loginnameError">${errros.loginname }</label>
        </td>
      </tr>
      <tr class="tr1">
        <td class="tdLabel">登录密码：</td>
        <td class="tdInput">
          <input class="input" type="password" name="loginpass" value="" id="loginpass"/>
        </td>
        <td class="tdError">
          <label class="labelError" id="loginpassError">${errros.loginpass }</label>
        </td>
      </tr>
      <tr class="tr1">
        <td class="tdLabel">确认密码：</td>
        <td class="tdInput">
          <input class="input" type="password" name="reloginpass" value="" id="reloginpass"/>
        </td>
        <td class="tdError">
          <label class="labelError" id="reloginpassError">${errros.reloginname }</label>
        </td>
      </tr>
      <tr class="tr1">
        <td class="tdLabel">Email：</td>
        <td class="tdInput">
          <input class="input" type="text" name="email" value="" id="email"/>
        </td>
        <td class="tdError">
          <label class="labelError" id="emailError">${errros.email }</label>
        </td>
      </tr>
      <tr class="tr1">
        <td class="tdLabel">图形验证码：</td>
        <td class="tdInput">
          <input class="input" type="text" name="verifyCode" value="" id="verifyCode"/>
        </td>
        <td class="tdError">
          <label class="labelError" id="verifyCodeError">${errros.verifyCode }</label>
        </td>
      </tr>
      <tr class="tr1">
        <td class="tdLabel">&nbsp;</td>
        <td class="tdVerifyCode">
          <%-- 显示验证码：需要把VerfiyCodeServlet在web.xml配置，然后在这里指定这个Servlet即可 --%>
          <span class="spanVerifyCode">
            <img id="imgVerifyCode" width="100" class="imgVerifyCode" src="<c:url value='/VerifyCodeServlet'/>" />
          </span>
        </td>
        <td>
          <a href="javascript:_go();">换张图</a>
        </td>
        </tr>
       <%--  <tr>
        <td>激活码:&nbsp;</td>
        <tr>
        <td class="jhButton">
          <input class="jihuoButton" type="text" src="<c:url value='/UserServlet/activationCode'/>" id="submit" />
        </td>
        <td>&nbsp;</td>
      </tr> --%>
      <tr class="tr1">
        <td>&nbsp;</td>
        <td class="tdButton">
          <input class="registBtn" type="image" src="<c:url value='/images/regist1.jpg'/>" id="submit" />
        </td>
        <td>&nbsp;</td>
      </tr>
       
    </table>
  </div>
</div>
</form>
  </body>
</html>
