<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
response.setCharacterEncoding("UTF-8");
request.setCharacterEncoding("UTF-8");
%>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<link rel="stylesheet" type="text/css" href="${contextPath}/resources/css/default.css" />
	<title>
<c:if test="${not empty page_title}">
		${page_title} - 
</c:if>
		재난알림센터
	</title>
</head>
<body>
<div class="head_box">
	<div class="title">
		재난알림센터
	</div>
	<div class="top_menu">
		<a href="${contextPath}/">Home</a> | <a href="#">Help</a>
	</div>
</div>
<div class="content">