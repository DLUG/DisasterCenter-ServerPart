<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<c:set var="page_title" value="[정보] ${title}"/>
<%@ include file="./layout_head.jsp" %>
<h1>
	[정보] ${title}
</h1>
작성일: ${datetime}<br>
재난유형: ${type_disaster}<br> 
${content}
<%@ include file="./layout_tail.jsp" %>