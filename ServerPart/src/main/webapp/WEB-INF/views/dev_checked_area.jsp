<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" contentType="text/html" pageEncoding="UTF-8" %>
<%
response.setCharacterEncoding("UTF-8");
request.setCharacterEncoding("UTF-8");
%>
<html>
<head>
	<meta name="viewport" content="initial-scale=1.0, user-scalable=no">
	<title>체크중인 장소 - 개발자용 - 재난알림시스템</title>
	<style>
html, body {
  height: 100%;
  margin: 0;
  padding: 0;
}

#map-canvas, #map_canvas {
  height: 100%;
}

@media print {
  html, body {
    height: auto;
  }

  #map-canvas, #map_canvas {
    height: 650px;
  }
}

#panel {
  position: absolute;
  top: 5px;
  left: 50%;
  margin-left: -180px;
  z-index: 5;
  background-color: #fff;
  padding: 5px;
  border: 1px solid #999;
}
	</style>
    <script src="https://maps.googleapis.com/maps/api/js?v=3.exp&sensor=false"></script>
    <script>
var locationArray = [
<c:forEach items="${target_list}" var="item">		new google.maps.LatLng(${item.lat}, ${item.lng}), 
</c:forEach>		new google.maps.LatLng(38, 128)];

var locationNameArray = [
<c:forEach items="${target_list}" var="item">		'${item.addr}', 
</c:forEach>		''];

function initialize(){
	var mapOptions = {
		zoom: 7,
		center: new google.maps.LatLng(36.31263534904849, 127.94677734375),
		mapTypeId: google.maps.MapTypeId.ROADMAP
	};
	
	var map = new google.maps.Map(document.getElementById('map-canvas'), mapOptions);
	
	var coord;
	for (coord in locationArray) {
		new google.maps.Marker({
			position: locationArray[coord],
			map: map,
			title: locationNameArray[coord]
		});
	}
	
	google.maps.event.addListener(map, 'click', function(event) {
		alert('Point.X.Y: ' + event.latLng);
	});
}

google.maps.event.addDomListener(window, 'load', initialize);
    </script>
</head>
<body>
<h1>
	체크중인 장소를 표기합니다.
</h1>

<div id="map-canvas" height=400px width=400px></div>

</body>
</html>
