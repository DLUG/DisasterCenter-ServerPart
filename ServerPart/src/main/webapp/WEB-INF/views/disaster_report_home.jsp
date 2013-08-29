<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<c:set var="page_title" value="재난 신고 목록"/>
<%@ include file="./layout_head.jsp" %>
<style>
.page_list{
	width: 390px;
	text-align: center;
	margin: 20px auto;
}

.page_btn{
	text-align: center;
	float: left;
	margin: 5px;
	width: 20px;
}

.page_btn_half{
	text-align: center;
	float: left;
	margin: 5px 2px 5px 3px;
	width: 10px;
}

.page_current{
	font-weight: bold;
}
</style>

<script type="text/javascript" src="${contextPath}/constant.js"></script>
<script type="text/javascript" src="${contextPath}/resources/js/disaster_report_home.js"></script>

▶ 재난 신고 목록

<table border="1" class="report_list list" id="report_list">
	<tr class="list_header" id="report_list_header">
		<td class="report_list_idx">글 번호</td>
		<td class="report_list_location">위치</td>
		<td class="report_list_type_disaster">재난 유형</td>
		<td class="report_list_type_report">신고 유형</td>
		<td class="report_list_report_datetime">작성시간</td>
	</tr>
	<tr id="report_list_item" >
		<td class="list_loading" colspan="5">로딩중입니다.</td>
	</tr>
</table>
<div class="page_list" id="page_list">1</div>
<%@ include file="./layout_tail.jsp" %>