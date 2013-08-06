<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<c:set var="page_title" value="재난 정보 목록"/>
<%@ include file="./layout_head.jsp" %>
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
		<td>${item.type_disaster}</td>
		<td>${item.title}</td>
		<td>${item.datetime}</td>
	</tr>
</c:forEach>
</table>  
<%@ include file="./layout_tail.jsp" %>