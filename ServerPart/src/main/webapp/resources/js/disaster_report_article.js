var reportMap;

function map_init(){
	var mapOptions = {
		zoom: 12,
		center: new google.maps.LatLng(reportLat, reportLng),
		mapTypeId: google.maps.MapTypeId.ROADMAP
	};
	
	reportMap = new google.maps.Map(document.getElementById('map-canvas'), mapOptions);
	
	new google.maps.Marker({
		animation: google.maps.Animation.DROP,
		icon: "http://maps.google.com/mapfiles/ms/icons/red-dot.png",
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