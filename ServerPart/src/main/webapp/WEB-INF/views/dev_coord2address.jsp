<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" contentType="text/html" pageEncoding="UTF-8" %>
<%
response.setCharacterEncoding("UTF-8");
request.setCharacterEncoding("UTF-8");
%>
<html>
<head>
	<meta name="viewport" content="initial-scale=1.0, user-scalable=no">
	<title>좌표2주소 - 개발자용 - 재난알림시스템</title>
</head>
<body>
<form action="./coord2address_result" method="get">
	Latitude: <input name="lat" value="37.507502379027"/><br>
	Longitude: <input name="lng" value="127.05590291409"/><br>
	<input type="submit" value="Submit" />
</form>

</body>
</html>
