<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" contentType="text/html" pageEncoding="UTF-8" %>
<%
response.setCharacterEncoding("UTF-8");
request.setCharacterEncoding("UTF-8");
%>
<html>
<head>
	<title>개발자용 - 재난알림시스템</title>
</head>
<body>
<h1>
	개발자용 페이지입니다.
</h1>

<a href="./gcm">GCM Test</a><br>
<a href="./checked_area">Show Checked Area</a><br>

</body>
</html>
