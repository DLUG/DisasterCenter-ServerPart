// ==== Setting ====
var PIN_ANIMATION_TERM = 10;
// =============================

var reportMap;
var reportData = new Array();
var reportDataCnt = 0;

var pinAnimators = new Array();
var pinAnimatorCnt = 0;
var pinAnimatorPlayCnt = 0;

var debugJSON;

$(document).ready(function(){
	map_init();
	
	getAllReport(0, 0);
});

function map_init(){
	var mapOptions = {
		zoom: 7,
		center: new google.maps.LatLng(36.31263534904849, 127.94677734375),
		mapTypeId: google.maps.MapTypeId.ROADMAP
	};
	
	reportMap = new google.maps.Map(document.getElementById('map-canvas'), mapOptions);
/*	
	google.maps.event.addListener(map, 'click', function(event) {
		alert('Point.X.Y: ' + event.latLng);
	});
*/
}

function load_pins(){
	var coord;
	
	for (coord in locationArray) {
		new google.maps.Marker({
			animation: google.maps.Animation.DROP,
			icon: 'http://maps.google.com/mapfiles/ms/icons/red-dot.png',
			position: locationArray[coord],
			map: map,
			title: locationNameArray[coord]
		});
	}
}

function finishGetAllReport(){
	showPins();
	showReportList();
}

function getAllReport(offset, remainPage){
	if(offset != 0 && remainPage == 0){
		finishGetAllReport();
		return;
	}
		
	var query = "";
	if(offset)
		query = "offset=" + offset;
	
	$.ajax({
		type: 'post', 
		async: true,
		url: CONTEXT_PATH + '/api/disaster_report',
		data: query,
		success: function(data){
			var i;
			debugJSON = data;
			
			var minIdx = data.data[0].idx;
			
			for(i = 0; i < data.data.length; i++){
				var item = data.data[i];
				
				if(minIdx > item.idx)
					minIdx = item.idx;
			}
			
			if(offset == 0){
				remainPage = data.page_amount;
			}
			
			reportData[reportDataCnt++] = data;
			
			getAllReport(minIdx - 1, remainPage - 1);
		},
		error: function(data, status, err){
			alert(err);
		}
	});
}
function showPins(){
	var i, j;
	var pinAnimationTimeline = 0;
		
	for(i = reportData.length - 1; i >= 0 ; i--){
//	for(i = 0; i < reportData.length ; i++){
		var data = reportData[i];
		
		
		for(j = 0; j < data.data.length; j++){
			var item = data.data[j];
			
			var icon = "http://maps.google.com/mapfiles/ms/icons/info.png";
			
			if(item.type_report > 1100 && item.type_report < 1199){
				if(item.type_disaster == 905)
					icon = "http://maps.google.com/mapfiles/ms/icons/pink.png";
				else
					icon = "http://maps.google.com/mapfiles/ms/icons/red.png";
			} else {
				if(item.type_disaster == 905)
					icon = "http://maps.google.com/mapfiles/ms/icons/pink-dot.png";
				else
					icon = "http://maps.google.com/mapfiles/ms/icons/red-dot.png";
			}
				
			
			var content = item.content.replace(/<br>/g, "");
			content += "\n" + "지역명: " + item.loc_name;
			
			var reportDate = new Date (item.timestamp);
			content += "\n" + "등록날짜/시각: " +  (reportDate.getMonth() + 1) + "월 " + reportDate.getDate() + "일 " 
					+ reportDate.getHours() + ":" + reportDate.getMinutes(); 
			
			var tmpObject = new Object();
			pinAnimators[pinAnimatorCnt++] = tmpObject;
			
			tmpObject.icon = icon;
			tmpObject.content = content;
			tmpObject.position = new google.maps.LatLng(item.lat, item.lng);
			
			setTimeout(function(){
					var pinAnimation = pinAnimators[pinAnimatorPlayCnt++];
					new google.maps.Marker({
						animation: google.maps.Animation.DROP,
						icon: pinAnimation.icon,
						position: pinAnimation.position,
						map: reportMap,
						title: pinAnimation.content
					});
				}, (pinAnimationTimeline += PIN_ANIMATION_TERM));
		}
	}
}

function showReportList(){
	var list = $('#report_list');
	var backupedHeader = $('#report_list_header').clone();
	
	list.html("");
	list.append(backupedHeader);
	
	var recentList = reportData[0].data;
	
	var i;
	
	for(i = 0; i < recentList.length; i++){
		var reportDatetime = new Date(recentList[i].timestamp);
		
		var item = $("<tr onclick=\"location.href='" + CONTEXT_PATH + "/disaster_report/" + recentList[i].idx + "'\"></tr>");
		item.append($("<td>" + recentList[i].loc_name + "</td>"));
		item.append($("<td>" + CONSTANT_DISASTER_TYPE[recentList[i].type_disaster] + "</td>"));
		item.append($("<td>" + CONSTANT_REPORT_TYPE[recentList[i].type_report] + "</td>"));
		item.append($("<td>" + reportDatetime.getDate() + "일 " + reportDatetime.getHours() + ":" + reportDatetime.getMinutes() + "</td>"));
		
		list.append(item);
	}
}

//google.maps.event.addDomListener(window, 'load', map_init);