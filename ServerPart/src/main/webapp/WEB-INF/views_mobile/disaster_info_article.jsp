<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" contentType="text/html" pageEncoding="UTF-8" %>
<%
response.setCharacterEncoding("UTF-8");
request.setCharacterEncoding("UTF-8");
%>
<html>
<head>
	<title>[정보] ${title} - 재난알림시스템 모바일</title>
</head>
<body>
<h1>
	[정보] ${title}
</h1>
	작성일: ${datetime}<br>
	재난유형:
	<c:choose>
		<c:when test="${type_disaster == 100}">
			수해 (기타)
		</c:when>
		<c:when test="${type_disaster == 101}">
			폭우
		</c:when>
		<c:when test="${type_disaster == 102}">
			홍수
		</c:when>
		<c:when test="${type_disaster == 103}">
			해일
		</c:when>
		<c:when test="${type_disaster == 104}">
			산사태
		</c:when>
		<c:when test="${type_disaster == 200}">
			눈 (기타)
		</c:when>
		<c:when test="${type_disaster == 201}">
			폭설
		</c:when>
		<c:when test="${type_disaster == 202}">
			눈사태
		</c:when>
		<c:when test="${type_disaster == 203}">
			혹한
		</c:when>
		<c:when test="${type_disaster == 300}">
			더위 (기타)
		</c:when>
		<c:when test="${type_disaster == 301}">
			폭염
		</c:when>
		<c:when test="${type_disaster == 400}">
			인재 (기타)
		</c:when>
		<c:when test="${type_disaster == 401}">
			건물붕괴
		</c:when>
		<c:when test="${type_disaster == 402}">
			교량붕괴
		</c:when>
		<c:when test="${type_disaster == 500}">
			기타 (기타)
		</c:when>
		<c:when test="${type_disaster == 501}">
			산불
		</c:when>
	</c:choose> 
	${content}
</body>
</html>
