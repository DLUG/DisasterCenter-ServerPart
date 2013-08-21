<%@ page session="false" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="./layout_head.jsp" %>
<script src="https://maps.googleapis.com/maps/api/js?v=3.exp&sensor=false"></script>
<script type="text/javascript" src="${contextPath}/constant.js"></script>
<script type="text/javascript" src="${contextPath}/resources/js/home.js"></script>

<div class="home_box report_map_box">
	<div class="box_title">
		▶ 알림 지도
	</div>
	<div class="report_map" id="map-canvas"></div>
</div>
<div class="home_box report_list_box">
	<div class="box_title">
		▶ <a href="${contextPath}/disaster_report/">최근 보고 내역</a>
	</div>
	<table border="1" class="report_list list" id="report_list">
		<tr class="list_header" id="report_list_header">
			<td class="report_list_location">위치</td>
			<td class="report_list_type_disaster">재난유형</td>
			<td class="report_list_type_report">보고유형</td>
			<td class="report_list_report_datetime">보고시간</td>
		</tr>
		<tr id="report_list_item" >
			<td class="list_loading" colspan="4">로딩중입니다.</td>
		</tr>
	</table>
</div>
<div class="home_box news_list_box">
</div>
<div class="home_box info_list_box">
</div>

<%@ include file="./layout_tail.jsp" %>