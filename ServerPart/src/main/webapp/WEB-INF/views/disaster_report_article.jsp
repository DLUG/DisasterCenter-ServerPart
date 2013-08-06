<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<c:set var="page_title" value="[신고] ${title}"/>
<%@ include file="./layout_head.jsp" %>
<h1>
	[신고] ${title}
</h1>
작성일: ${datetime}<br>
위도: ${lat}<br>
경도: ${lng}<br>
정확도: ${accuracy}<br>
신고유형: ${type_report}<br>
재난유형: ${type_disaster}<br>
${content}
<%@ include file="./layout_tail.jsp" %>