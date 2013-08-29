// ==== Setting ====
var PIN_ANIMATION_TERM = 10;
// =============================

var reportMap;
var reportData = new Array();
var reportDataCnt = 0;

var pinAnimationPlayIdx = new Array();
var pinAnimationPlayIdxCnt = 0;

var pins = new Array();

function JobTreeNode(initValue){
//	console.log("New: " + initValue.timeline);
	this.jobTicket = initValue;
	this.left = null;
	this.right = null;
	this.put = function(jobTicket){
		var thisTimeline = this.jobTicket.timeline;
		var targetTimeline = jobTicket.timeline;
		
//		console.log("put: " + thisTimeline + ", target: " + targetTimeline);
		
		if(thisTimeline <= targetTimeline){
			if(this.right == null){
//				console.log("putted");
				this.right = new JobTreeNode(jobTicket);
			} else {
				this.right.put(jobTicket);
			}
		} else {
			if(this.left == null){
//				console.log("putted");
				this.left = new JobTreeNode(jobTicket);
			} else {
				this.left.put(jobTicket);
			}
		}
	};
	
	this.get = function(timeline){
		var thisTimeline = this.jobTicket.timeline; 
//		console.log("get: " + thisTimeline + ", target: " + timeline + ", isused: " + this.jobTicket.isused);
		
		if(thisTimeline == timeline && !this.jobTicket.isused){
//			console.log("getted1");
			this.jobTicket.isused = true;
			return this.jobTicket;
		}else {
			var getTarget = null;
			if(thisTimeline <= timeline)
				getTarget = this.right;
			else
				getTarget = this.left;
			
			if(getTarget == null){
				if(this.jobTicket.isused)
					return null;
//				console.log("getted2");
				this.jobTicket.isused = true;
				return this.jobTicket;
			}else {
				var tmpResult = getTarget.get(timeline);
				if(tmpResult){
					return tmpResult
				} else {
					if(this.jobTicket.isused)
						return null;
//					console.log("getted3");
					this.jobTicket.isused = true;
					return this.jobTicket;
				}
			}
				
		}
	};
}

function JobTicket(timeline){
	this.timeline = timeline;
	this.jobIdx = null;
	this.jobAction = null;
	this.isused = false;
	this.timeout = 0;
}

var PinAnimator2 = new Object();
PinAnimator2.ANICODE_HIDE_ANIMATION = 0;
PinAnimator2.ANICODE_HIDE = 1;
PinAnimator2.ANICODE_SHOW_ANIMATION = 2;
var initJobTicket = new JobTicket(0);
initJobTicket.isused = true;
PinAnimator2.data = new JobTreeNode(initJobTicket);
//PinAnimator2.regTimeline = 0;
PinAnimator2.playedTimeline = 0;
PinAnimator2.addAni = function(idx, actionCode, timeout){
	var thisTimeline = (this.playedTimeline + timeout);
	
	var tmpJobTicket = new JobTicket(thisTimeline);
	tmpJobTicket.jobIdx = idx;
	tmpJobTicket.jobAction = actionCode;
	tmpJobTicket.timeout = timeout;
	
	this.data.put(tmpJobTicket);
	
	setTimeout(function(){
		 
		var thisTimeline = PinAnimator2.playedTimeline;
		var thisJobTicket = PinAnimator2.data.get(thisTimeline);
		
		console.log("playing: " + thisJobTicket.timeline);
		if((thisJobTicket.timeline) > PinAnimator2.playedTimeline)
			PinAnimator2.playedTimeline = (thisJobTicket.timeline);
		
		var jobAction = thisJobTicket.jobAction;
		var jobIdx = thisJobTicket.jobIdx;
		
		if(jobAction == PinAnimator2.ANICODE_HIDE_ANIMATION){
			reportData[jobIdx].pin.setAnimation(google.maps.Animation.BOUNCE);
		} else if(jobAction == PinAnimator2.ANICODE_HIDE){
			reportData[jobIdx].pin.setVisible(false);
		} else if(jobAction == PinAnimator2.ANICODE_SHOW_ANIMATION){
			reportData[jobIdx].pin.setAnimation(google.maps.Animation.DROP);
			reportData[jobIdx].pin.setVisible(true);
		}
	}, timeout);
};

var PinAnimator = new Object();
PinAnimator.ANICODE_HIDE_ANIMATION = 0;
PinAnimator.ANICODE_HIDE = 1;
PinAnimator.ANICODE_SHOW_ANIMATION = 2;
PinAnimator.idxArr = new Array();
PinAnimator.idxObj = new Object();
PinAnimator.actionArr = new Array();
PinAnimator.timeoutArr = new Array();
PinAnimator.timelineArr = new Array();
PinAnimator.regIdx = 0;
PinAnimator.playIdx = 0;
PinAnimator.playTimeline = 0;
PinAnimator.regTimeline = 0;
PinAnimator.getMaxTimeout = function(){
	var regIdx = this.regIdx;
	var playIdx = this.playIdx;
	var tmpMaxTimeout = 0;
	
	tmpMaxTimeout = this.timeoutArr[playIdx];
	for(var i = playIdx + 1; i < regIdx; i++){
		if(tmpMaxTimeout < this.timeoutArr[i]){
			tmpMaxTimeout = this.timeoutArr[i];
		}
	}
	
	if(!tmpMaxTimeout)
		tmpMaxTimeout = 0;

	return tmpMaxTimeout;
};

PinAnimator.addAni = function(idx, actionCode, timeout){
	var regIdx = this.regIdx++;
	
	var regTimeline = this.regTimeline;
	
	this.idxArr[regIdx] = idx;
	this.actionArr[regIdx] = actionCode;
	
	this.timeoutArr[regIdx] = timeout;
	
	console.log("Timeout: " + timeout);
	
	setTimeout(function(){
		var playIdx = PinAnimator.playIdx;
		
		var reqIdx = PinAnimator.idxArr[playIdx];
		var actionCode = PinAnimator.actionArr[playIdx];
		
		if(actionCode == PinAnimator.ANICODE_HIDE_ANIMATION){
			reportData[reqIdx].pin.setAnimation(google.maps.Animation.BOUNCE);
		} else if(actionCode == PinAnimator.ANICODE_HIDE){
			reportData[reqIdx].pin.setVisible(false);
		} else if(actionCode == PinAnimator.ANICODE_SHOW_ANIMATION){
			reportData[reqIdx].pin.setAnimation(google.maps.Animation.DROP);
			reportData[reqIdx].pin.setVisible(true);
		}
	}, timeout);
};

var AnimationQueue = new Object();
AnimationQueue.queue = new Array();
AnimationQueue.queueCnt = 0;

AnimationQueue.add = function(animationSet){
	this.queue[this.queueCnt] = animationSet;
	this.queueCnt++;
	
	if(!this.runStatus){
		this.run();
	}
};

AnimationQueue.setTimeout = function(func, timeout){
	this.regCnt++;
	
	setTimeout(func, timeout);
	setTimeout(function(){
		AnimationQueue.regCnt--;
		
		if(AnimationQueue.regCnt == 0)
			AnimationQueue.run();
	}, timeout + 1000);
};


AnimationQueue.runCnt = 0;
AnimationQueue.runStatus = false;
AnimationQueue.run = function(){
	if(this.runCnt != this.queueCnt){
		runStatus = true;
		this.queue[this.runCnt]();
		this.runCnt++;
	}else {
		runStatus = false;
	}
};
AnimationQueue.regCnt = 0;

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
		
//		var tmpMaxTimeout = PinAnimator.getMaxTimeout();
//		console.log(tmpMaxTimeout + "??");
		var tmpMaxTimeout = 0;
		
		for(var i = 0; i < reportData.length; i++){
			if(i >= toValue && i <= fromValue){
				if(reportData[i].pin.getVisible()){
					
				} else {
					PinAnimator2.addAni(i, PinAnimator2.ANICODE_SHOW_ANIMATION, tmpMaxTimeout + (aniCnt * PIN_ANIMATION_TERM));
					aniCnt++;
				}
			} else {
				if(reportData[i].pin.getVisible()){
					PinAnimator2.addAni(i, PinAnimator2.ANICODE_HIDE_ANIMATION, tmpMaxTimeout + (aniCnt * PIN_ANIMATION_TERM));
					PinAnimator2.addAni(i, PinAnimator2.ANICODE_HIDE, tmpMaxTimeout + (aniCnt * PIN_ANIMATION_TERM) + 500);
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
				icon = "http://maps.google.com/mapfiles/ms/icons/pink.png";
			else
				icon = "http://maps.google.com/mapfiles/ms/icons/red.png";
		} else {
			if(item.type_disaster == 905)
				icon = "http://maps.google.com/mapfiles/ms/icons/pink-dot.png";
			else
				icon = "http://maps.google.com/mapfiles/ms/icons/red-dot.png";
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
	}
	
	AnimationQueue.add(function(){
		pinAnimationPlayIdx[0] = 0;
		for(i = reportData.length - 1; i >= 0 ; i--){
			AnimationQueue.setTimeout(function(){
				var reportItem = reportData[reportData.length - 1 - pinAnimationPlayIdx[0]];
				
				reportItem.pin = new google.maps.Marker({
					animation: google.maps.Animation.DROP,
					icon: reportItem.icon,
					position: reportItem.position,
					map: reportMap,
					title: reportItem.mapContent,
					zIndex: pinAnimationPlayIdx[0]
				});
				
				pinAnimationPlayIdx[0]++;
			}, (pinAnimationTimeline += PIN_ANIMATION_TERM));
		}
	});
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