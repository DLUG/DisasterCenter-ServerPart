var PAGE_LIST_AMOUNT = 9;	// MUST ODD Number;

var reportData;
var debugJSON;

var minIdx;
var maxIdx;

var pageAmount;
var currentPage = 0;

$(document).ready(function(){
	if(window.location.hash) {
		var hash = window.location.hash.substring(1);
		getPage(hash);
	} else {
		getPage(0);	
	}
});

function getPage(page){
	if(page == 0){
		page = 1;
	} else {
		location.href=CONTEXT_PATH + "/disaster_report/#" + page;
	}
	
	query = "page=" + page;

	$.ajax({
		type: 'post', 
		async: true,
		url: CONTEXT_PATH + '/api/disaster_report',
		data: query,
		success: function(data){
			debugJSON = data;

			if(data.msg != "Success"){		// TODO: Modify with API
				alert("데이터를 가져올 수 없습니다. 다시 확인해주세요.");
				return;
			}
				
			var i;
			
			minIdx = data.data[0].idx;
			maxIdx = data.data[0].idx;
			
			for(i = 0; i < data.data.length; i++){
				var item = data.data[i];

				pageAmount = data.page_amount;
				
				if(minIdx > item.idx)
					minIdx = item.idx;
				if(maxIdx > item.idx)
					maxIdx = item.idx;
			}
			
			reportData = data;
			
			currentPage = page;
			showReportList();
		},
		error: function(data, status, err){
			alert(err);
		}
	});
}

function showReportList(){
	var list = $('#report_list');
	var backupedHeader = $('#report_list_header').clone();
	
	list.html("");
	list.append(backupedHeader);
	
	var data = reportData.data;
	
	var i;
	
	for(i = 0; i < data.length; i++){
		var reportDatetime = new Date(data[i].timestamp);
		
		var item = $("<tr onclick=\"location.href='" + CONTEXT_PATH + "/disaster_report/" + data[i].idx + "'\"></tr>");
		item.append($("<td>" + data[i].idx + "</td>"));
		item.append($("<td>" + data[i].loc_name + "</td>"));
		item.append($("<td>" + CONSTANT_DISASTER_TYPE[data[i].type_disaster] + "</td>"));
		item.append($("<td>" + CONSTANT_REPORT_TYPE[data[i].type_report] + "</td>"));
		item.append($("<td>" + (reportDatetime.getMonth() + 1) + "월 " + reportDatetime.getDate() + "일 "
				+ reportDatetime.getHours() + ":" + reportDatetime.getMinutes() + "</td>"));
		
		list.append(item);
	}
	
	// ========= Page List Proc =======
	
	var pageList = $("#page_list");
	pageList.html("");
	
	if(pageAmount > PAGE_LIST_AMOUNT){
		if(currentPage > Math.floor(PAGE_LIST_AMOUNT / 2 + 1)){
			var prevPrevPage = currentPage - PAGE_LIST_AMOUNT;
			if(prevPrevPage < 1)
				prevPrevPage == 1;
			
			pageList.append("<div class='prev_prev_page page_btn' onclick='getPage(" + prevPrevPage + ")'>&lt;&lt;</div>");
		} else {
			pageList.append("<div class='page_btn'> </div>");
		}
		if(currentPage > 1)
			pageList.append("<div class='prev_page page_btn' onclick='getPage(" + (currentPage - 1) + ")'>&lt;</div>");
		else
			pageList.append("<div class='page_btn'> </div>");
		
		var viewFirstPage = currentPage - Math.floor(PAGE_LIST_AMOUNT / 2);
		if(viewFirstPage < 1)
			viewFirstPage = 1;
		
		if(viewFirstPage > (pageAmount - PAGE_LIST_AMOUNT))
			viewFirstPage = pageAmount - PAGE_LIST_AMOUNT + 1;
		
		var i;
		for(i = 0; i < PAGE_LIST_AMOUNT; i++){
			var viewPage = viewFirstPage + i;
			var tmpHtml = "<div class='page_num page_btn ";
			
			if(viewPage == currentPage)
				tmpHtml += "page_current";
			
			tmpHtml += "' onclick='getPage(" + viewPage + ")'>" + viewPage + "</div>";
			pageList.append(tmpHtml);
		}
		
		if(currentPage < pageAmount)
			pageList.append("<div class='next_page page_btn' onclick='getPage(" + (currentPage + 1) + ")'>&gt;</div>");
		else
			pageList.append("<div class='page_btn'> </div>");
		
		if(currentPage < (pageAmount - (PAGE_LIST_AMOUNT / 2))){
			var nextNextPage = currentPage + PAGE_LIST_AMOUNT;
			if(nextNextPage > pageAmount)
				nextNextPage == pageAmount;
			
			pageList.append("<div class='prev_prev_page page_btn' onclick='getPage(" + nextNextPage + ")'>&gt;&gt;</div>");
		} else {
			pageList.append("<div class='page_btn'> </div>");
		}
	} else {
		var amountHalfBlock = (13 - pageAmount);
		
		for(var i = 0; i < amountHalfBlock; i++){
			pageList.append("<div class='page_btn_half'> </div>");
		}
		
		for(var i = 0; i < pageAmount; i++){
			var viewPage = 1 + i;
			var tmpHtml = "<div class='page_num page_btn ";
			
			if(viewPage == currentPage)
				tmpHtml += "page_current";
			
			tmpHtml += "' onclick='getPage(" + viewPage + ")'>" + viewPage + "</div>";
			pageList.append(tmpHtml);
		}
		
		for(var i = 0; i < amountHalfBlock; i++){
			pageList.append("<div class='page_btn_half'> </div>");
		}
	}
}