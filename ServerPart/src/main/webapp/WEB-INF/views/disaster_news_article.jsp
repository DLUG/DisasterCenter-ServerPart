<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<c:set var="page_title" value="[소식] ${title}"/>
<%@ include file="./layout_head.jsp" %>
<h1>
	[소식] ${title}
</h1>
작성일: ${datetime}<br>
<c:if test="${not empty lat}">
	위도: ${lat}<br>
	경도: ${lng}<br>
</c:if>
재난유형: ${type_disaster}<br>
${content}
<%@ include file="./layout_tail.jsp" %>