<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" contentType="text/html" pageEncoding="UTF-8" %>
<%
response.setCharacterEncoding("UTF-8");
request.setCharacterEncoding("UTF-8");
%>
<html>
<head>
	<title>재난 신고 목록 - 재난알림시스템</title>
</head>
<body>
<h1>
	재난 신고 목록입니다.
</h1>
<table border=1>
<tr>
	<td>글 번호</td>
	<td>위치</td>
	<td>재난 유형</td>
	<td>신고 유형</td>
	<td>작성시간</td>
</tr>
<c:forEach items="${list}" var="item">
<tr onclick="location.href='./${item.idx}'">
	<td>${item.idx}</td>
	<td>${item.loc_name}</td>
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
	<td>
	<c:choose>
		<c:when test="${item.type_report == 0}">
			공공DB
		</c:when>
		<c:when test="${item.type_report == 1}">
			사용자신고
		</c:when>
	</c:choose>
	</td>
	<td>${item.datetime}</td>
</tr>
</a>
</c:forEach>
</table> 
</body>
</html>
