var reportMap;

function map_init(){
	var mapOptions = {
		zoom: 12,
		center: new google.maps.LatLng(reportLat, reportLng),
		mapTypeId: google.maps.MapTypeId.ROADMAP
	};
	
	reportMap = new google.maps.Map(document.getElementById('map-canvas'), mapOptions);
	
	var icon = "";
	if(reportTypeDisaster == 905)
		icon = CONTEXT_PATH + "/resources/img/weather_temp_high_watch.png";
	else if(reportTypeDisaster == 905)
		icon = CONTEXT_PATH + "/resources/img/weather_temp_high_alert.png";
	else if(reportTypeDisaster == 901)
		icon = CONTEXT_PATH + "/resources/img/weather_rain_hard_watch.png";
	else if(reportTypeDisaster == 902)
		icon = CONTEXT_PATH + "/resources/img/weather_rain_hard_watch.png";
	else if(reportTypeDisaster == 903)
		icon = CONTEXT_PATH + "/resources/img/weather_wind_fast_watch.png";
	else if(reportTypeDisaster == 904)
		icon = CONTEXT_PATH + "/resources/img/weather_wind_fast_watch.png";
	
	new google.maps.Marker({
		animation: google.maps.Animation.DROP,
		icon: icon,
		position: new google.maps.LatLng(reportLat, reportLng),
		map: reportMap,
		title: reportContent
	});
/*	
	google.maps.event.addListener(map, 'click', function(event) {
		alert('Point.X.Y: ' + event.latLng);
	});
*/
}

google.maps.event.addDomListener(window, 'load', map_init);