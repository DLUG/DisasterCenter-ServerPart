<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" contentType="text/html" pageEncoding="UTF-8" %>
<%
response.setCharacterEncoding("UTF-8");
request.setCharacterEncoding("UTF-8");
%>
<html>
<head>
	<title>재난 정보 목록 - 재난알림시스템</title>
</head>
<body>
<h1>
	재난 정보 목록입니다.
</h1>
<table border=1>
<tr>
	<td>글 번호</td>
	<td>재난 유형</td>
	<td>제목</td>
	<td>작성시간</td>
</tr>
<c:forEach items="${list}" var="item">
<tr onclick="location.href='./${item.idx}'">
	<td>${item.idx}</td>
	<td>
	<c:choose>
		<c:when test="${item.type_disaster == 100}">
			수해 (기타)
		</c:when>
		<c:when test="${item.type_disaster == 101}">
			폭우
		</c:when>
		<c:when test="${item.type_disaster == 102}">
			홍수
		</c:when>
		<c:when test="${item.type_disaster == 103}">
			해일
		</c:when>
		<c:when test="${item.type_disaster == 104}">
			산사태
		</c:when>
		<c:when test="${item.type_disaster == 200}">
			눈 (기타)
		</c:when>
		<c:when test="${item.type_disaster == 201}">
			폭설
		</c:when>
		<c:when test="${item.type_disaster == 202}">
			눈사태
		</c:when>
		<c:when test="${item.type_disaster == 203}">
			혹한
		</c:when>
		<c:when test="${item.type_disaster == 300}">
			더위 (기타)
		</c:when>
		<c:when test="${item.type_disaster == 301}">
			폭염
		</c:when>
		<c:when test="${item.type_disaster == 400}">
			인재 (기타)
		</c:when>
		<c:when test="${item.type_disaster == 401}">
			건물붕괴
		</c:when>
		<c:when test="${item.type_disaster == 402}">
			교량붕괴
		</c:when>
		<c:when test="${item.type_disaster == 500}">
			기타 (기타)
		</c:when>
		<c:when test="${item.type_disaster == 501}">
			산불
		</c:when>
	</c:choose> 
	</td>
	<td>${item.title}</td>
	<td>${item.datetime}</td>
</tr>
</c:forEach>
</table>  
</body>
</html>
