<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" contentType="text/html" pageEncoding="UTF-8" %>
<%
response.setCharacterEncoding("UTF-8");
request.setCharacterEncoding("UTF-8");
%>
<html>
<head>
	<title>메세지 발송 결과 - 개발자용 테스트 - 재난알림시스템</title>
</head>
<body>
<h1>
	메세지 발송 결과입니다.
</h1>
${result}
</body>
</html>
