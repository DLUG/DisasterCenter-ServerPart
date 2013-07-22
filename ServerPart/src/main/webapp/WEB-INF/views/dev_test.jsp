<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" contentType="text/html" pageEncoding="UTF-8" %>
<%
response.setCharacterEncoding("UTF-8");
request.setCharacterEncoding("UTF-8");
%>
<html>
<head>
	<title>개발자용 테스트 - 재난알림시스템</title>
</head>
<body>
<h1>
	개발자용 테스트 페이지입니다.
</h1>

<form method="GET" action="./send_test_msg">
	ReportId: <select name="report_idx">
<c:forEach items="${report}" var="item">
		<option value="${item.idx}">${item.label}</option>
</c:forEach>
	</select><br>
	GcmId: <select name="app_idx">
<c:forEach items="${app}" var="item">
		<option value="${item.idx}">${item.label}</option>
</c:forEach>
	</select><br>
	<input type="submit"></input>
</form>

</body>
</html>
