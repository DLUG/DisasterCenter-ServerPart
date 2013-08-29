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
	<title>
<c:if test="${not empty page_title}">
		${page_title} - 
</c:if>
		재난알림센터
	</title>
	<link rel="stylesheet" type="text/css" href="${contextPath}/resources/css/default.css" />
	<link rel="stylesheet" href="http://code.jquery.com/ui/1.10.3/themes/smoothness/jquery-ui.css" />
	<script src="http://code.jquery.com/jquery-1.10.2.js"></script>
  	<script src="http://code.jquery.com/ui/1.10.3/jquery-ui.js"></script>
	<script type="text/javascript" src="${contextPath}/resources/js/default.js"></script>
	<script type="text/javascript">
var CONTEXT_PATH = "${contextPath}";
	</script>
</head>
<body>
<div class="head_box">
	<div class="app_ad">알파버전 안드로이드 앱을 받으시려면 <a href="./resources/disastercenter_0.5.apk">여기</a>를 누르세요.</div>
	<div class="title" onclick="location.href='${contextPath}/';">
		재난알림센터
	</div>
	<div class="top_menu">
		<a href="${contextPath}/">Home</a> | <a href="#">Help</a>
	</div>
</div>
<div class="content">