<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<c:set var="page_title" value="[신고] ${title}"/>
<%@ include file="./layout_head.jsp" %>
<script src="https://maps.googleapis.com/maps/api/js?v=3.exp&sensor=false"></script>
<script type="text/javascript" src="${contextPath}/resources/js/disaster_report_article.js"></script>
<script type="text/javascript">
var reportLat = ${lat};
var reportLng = ${lng};
var reportContent = "${loc_name}";
reportContent.replace(/<br>/g, "");
</script>
<style>
.report_map{
	width: 400px;
	height: 400px;
}
</style>

[신고] ${title}

<div class="report_map" id="map-canvas">
</div>

작성일: ${datetime}<br>
위도: ${lat}<br>
경도: ${lng}<br>
지역명: ${loc_name}<br>
정확도: ${accuracy}<br>
신고유형: ${type_report}<br>
재난유형: ${type_disaster}<br>
${content}
<%@ include file="./layout_tail.jsp" %>