var reportMap;
var reportData = new Array();
var reportDataCnt = 0;

var pinAnimationPlayIdx = new Array();
var pinAnimationPlayIdxCnt = 0;

var pins = new Array();

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
	
	$("#slider-range").slider({
        range: true,
        min: 0,
        max: reportData.length - 1,
        values: [0, reportData.length - 1],
        step:1,
        slide: changePin
    });
	changePin();
}

var changePinEvent = setTimeout(function(){}, 0);
var changePinHideAni1Idx = 0;
var changePinHideAni2Idx = 0;
var changePinShowAniIdx = 0;

function changePin(event, ui){
	var fromValue = reportData.length - 1 - $("#slider-range").slider("values", 0);
	var toValue = reportData.length - 1 - $("#slider-range").slider("values", 1);

	clearTimeout(changePinEvent);
	changePinEvent = setTimeout(function(){
		var aniCnt = 0;
		
//		console.log(tmpMaxTimeout + "??");
		var tmpMaxTimeout = 0;
		
		for(var i = 0; i < reportData.length; i++){
			if(i >= toValue && i <= fromValue){
				if(reportData[i].pin.getVisible()){
					
				} else {
					PinAnimator.addAni(i, PinAnimator.ANICODE_SHOW_ANIMATION, tmpMaxTimeout + (aniCnt * PIN_ANIMATION_TERM));
					aniCnt++;
				}
			} else {
				if(reportData[i].pin.getVisible()){
					PinAnimator.addAni(i, PinAnimator.ANICODE_HIDE_ANIMATION, tmpMaxTimeout + (aniCnt * PIN_ANIMATION_TERM));
					PinAnimator.addAni(i, PinAnimator.ANICODE_HIDE, tmpMaxTimeout + (aniCnt * PIN_ANIMATION_TERM) + 500);
					aniCnt++;
				} else {
					
				}
			}
		}
	}, 100);
	
	var fromDate = new Date(reportData[fromValue].timestamp);
	var toDate = new Date(reportData[toValue].timestamp);
	
	var fromText = fromDate.getDate() + "일 " + fromDate.getHours() + ":" + fromDate.getMinutes();
	var toText = toDate.getDate() + "일 " + toDate.getHours() + ":" + toDate.getMinutes();
	$("#slider-range-text").html(fromText + " ~ " + toText);
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
			
			if(data.msg == "Data Empty"){
				$("#loading_msg").html("가져올 데이터가 없습니다.");
				
				return;
			}
			
			var minIdx = data.data[0].idx;
			
			for(i = 0; i < data.data.length; i++){
				var item = data.data[i];
				reportData[reportDataCnt++] = item;
				
				if(minIdx > item.idx)
					minIdx = item.idx;
			}
			
			if(offset == 0){
				remainPage = data.page_amount;
			}
			
			getAllReport(minIdx - 1, remainPage - 1);
		},
		error: function(data, status, err){
			alert(err);
		}
	});
}
function showPins(){
	var i;
	var pinAnimationTimeline = 0;
		
	for(i = reportData.length - 1; i >= 0 ; i--){
//	for(i = 0; i < reportData.length ; i++){
		var item = reportData[i];
		
		var icon = "http://maps.google.com/mapfiles/ms/icons/info.png";
		
		if(item.type_report > 1100 && item.type_report < 1199){
			if(item.type_disaster == 905)
				icon = CONTEXT_PATH + "/resources/img/weather_temp_high_watch.png";
			else if(item.type_disaster == 905)
				icon = CONTEXT_PATH + "/resources/img/weather_temp_high_alert.png";
			else if(item.type_disaster == 901)
				icon = CONTEXT_PATH + "/resources/img/weather_rain_hard_watch.png";
			else if(item.type_disaster == 902)
				icon = CONTEXT_PATH + "/resources/img/weather_rain_hard_watch.png";
			else if(item.type_disaster == 903)
				icon = CONTEXT_PATH + "/resources/img/weather_wind_fast_watch.png";
			else if(item.type_disaster == 904)
				icon = CONTEXT_PATH + "/resources/img/weather_wind_fast_watch.png";
		} else {
			if(item.type_disaster == 905)
				icon = CONTEXT_PATH + "/resources/img/weather_temp_high_watch.png";
			else if(item.type_disaster == 905)
				icon = CONTEXT_PATH + "/resources/img/weather_temp_high_alert.png";
			else if(item.type_disaster == 901)
				icon = CONTEXT_PATH + "/resources/img/weather_rain_hard_watch.png";
			else if(item.type_disaster == 902)
				icon = CONTEXT_PATH + "/resources/img/weather_rain_hard_watch.png";
			else if(item.type_disaster == 903)
				icon = CONTEXT_PATH + "/resources/img/weather_wind_fast_watch.png";
			else if(item.type_disaster == 904)
				icon = CONTEXT_PATH + "/resources/img/weather_wind_fast_watch.png";
		}
		
		var content = item.content.replace(/\n/g, "");
		content = content.replace(/<br>/g, "\n");
		content += "\n" + "지역명: " + item.loc_name;
		
		var reportDate = new Date (item.timestamp);

		
		
		content += "\n" + "등록날짜/시각: " +  (reportDate.getMonth() + 1) + "월 " + reportDate.getDate() + "일 " 
				+ reportDate.getHours() + ":" + reportDate.getMinutes(); 
		
		
		
		item.icon = icon;
		item.mapContent = content;
		item.position = new google.maps.LatLng(item.lat, item.lng);
		
		item.pin = new google.maps.Marker({
//			animation: google.maps.Animation.DROP,
			icon: item.icon,
			position: item.position,
			map: reportMap,
			title: item.mapContent,
			zIndex: reportData.length - 1 - i,
			visible: false
		});
		item.pin.setVisible(false);
		
		PinAnimator.pinArr[i] = item.pin;
	}
}

function showReportList(){
	var list = $('#report_list');
	var backupedHeader = $('#report_list_header').clone();
	
	list.html("");
	list.append(backupedHeader);
	
	var i;
	var listMaxAmount = 10;
	if(listMaxAmount > reportData.length)
		listMaxAmount = reportData.length;
	
	for(i = 0; i < listMaxAmount; i++){
		var reportDatetime = new Date(reportData[i].timestamp);
		
		var item = $("<tr onclick=\"location.href='" + CONTEXT_PATH + "/disaster_report/" + reportData[i].idx + "'\" onmouseover=\"reportMouseOver(" + i + ")\"></tr>");
		item.append($("<td>" + reportData[i].loc_name + "</td>"));
		item.append($("<td>" + CONSTANT_DISASTER_TYPE[reportData[i].type_disaster] + "</td>"));
		item.append($("<td>" + CONSTANT_REPORT_TYPE[reportData[i].type_report] + "</td>"));
		item.append($("<td>" + reportDatetime.getDate() + "일 " + reportDatetime.getHours() + ":" + reportDatetime.getMinutes() + "</td>"));
		
		list.append(item);
	}
}

function reportMouseOver(idx){
	var listMaxAmount = 10;
	if(listMaxAmount > reportData.length)
		listMaxAmount = reportData.length;
	
	for(var i = 0; i < listMaxAmount; i++){
		reportData[i].pin.setAnimation();
	}
	
	if(reportData[idx].pin.getVisible()){
		reportData[idx].pin.setAnimation(google.maps.Animation.BOUNCE);
	}
}

function test(){
	return;
	AnimationQueue.add(function(){
		pinAnimationPlayIdx[1] = 0;
		pinAnimationPlayIdx[2] = 0;
		for(var i = 0; i< reportData.length; i++){
			AnimationQueue.setTimeout(function(){
				reportData[reportData.length - 1 - pinAnimationPlayIdx[1]].pin.setAnimation(google.maps.Animation.BOUNCE);
				pinAnimationPlayIdx[1]++;
			}, PIN_ANIMATION_TERM * i);
	
			AnimationQueue.setTimeout(function(){
				reportData[reportData.length - 1 - pinAnimationPlayIdx[2]].pin.setVisible(false);
				pinAnimationPlayIdx[2]++;
				
			}, 500 + (PIN_ANIMATION_TERM * (i)));
		}
	});
}
//google.maps.event.addDomListener(window, 'load', map_init);