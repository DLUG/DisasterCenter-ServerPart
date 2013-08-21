package org.dlug.disastercenter.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.dlug.disastercenter.common.ApiDaumLocal;
import org.dlug.disastercenter.common.CoordinateTools;
import org.dlug.disastercenter.common.CoordinateTools.CoordLatLng;
import org.dlug.disastercenter.common.ConstantDisasterType;
import org.dlug.disastercenter.model.ModelApps;
import org.dlug.disastercenter.model.ModelKmaTarget;
import org.dlug.disastercenter.model.ModelReport;
import org.dlug.disastercenter.service.ServicePushMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Handles requests for the application home page.
 */
@Controller
@RequestMapping("/dev")
public class ControllerDev extends ControllerPages{
	
	@Autowired
	private ModelReport modelReport;

	@Autowired
	private ModelApps modelApps;

	@Autowired
	private ModelKmaTarget modelKmaTarget;
	
	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String devMain(Locale locale, Model model) {
		
		return "dev";
	}
	
	@RequestMapping(value = "checked_area", method = RequestMethod.GET)
	public String devCheckedArea(Locale locale, Model model) {
		
		List<Map<String, Object>> targetKmaList = modelKmaTarget.getTargetList();
		
		List<Map<String, Object>> targetList = new ArrayList<Map<String,Object>>();
		
		for(Map<String, Object> item: targetKmaList){
			Map<String, Object> resultItem = new HashMap<String, Object>();
			
			int kmaX = (Integer) item.get("kma_x");
			int kmaY = (Integer) item.get("kma_y");
			
			CoordLatLng latlng = CoordinateTools.Kma2latlng(kmaX, kmaY);
			
			resultItem.put("lat", latlng.lat);
			resultItem.put("lng", latlng.lng);
			resultItem.put("addr", item.get("idx") + " " + ApiDaumLocal.getAddress(latlng.lat, latlng.lng));
			
			targetList.add(resultItem);
		}
		
		model.addAttribute("target_list", targetList);
		
		return "dev_checked_area";
	}
	
	
	@RequestMapping(value = "gcm", method = RequestMethod.GET)
	public String devTestGcm(Locale locale, Model model) {
		List<Map<String, Object>> reportList = modelReport.getReportList(1);
		List<Map<String, Object>> appList = modelApps.getAppList();
		
		if(reportList == null){
			return "error";
		}
		
		List<Map<String, Object>> tmpReportList = new ArrayList<Map<String, Object>>();
		
		for(Map<String, Object> report: reportList){
			Map<String, Object> tmpItem = new HashMap<String, Object>();
			
			tmpItem.put("idx", report.get("idx"));
			
			tmpItem.put("label", report.get("content"));
			
			tmpReportList.add(tmpItem);
		}
		
		List<Map<String, Object>> tmpAppList = new ArrayList<Map<String, Object>>();
		
		for(Map<String, Object> app: appList){
			Map<String, Object> tmpItem = new HashMap<String, Object>();
			
			tmpItem.put("idx", app.get("idx"));
			
			tmpItem.put("label", app.get("uuid") + " " + app.get("gcm_id"));
			
			tmpAppList.add(tmpItem);
		}
		
		
		model.addAttribute("report", tmpReportList);
		model.addAttribute("app", tmpAppList);
		
		return "dev_test_gcm";
	}
	
	@RequestMapping(value = "send_test_msg", method = RequestMethod.GET)
	public String sendTestMsg(Locale locale, Model model, 
			@RequestParam(value="report_idx", required=true) long reportIdx,
			@RequestParam(value="app_idx", required=true) long appIdx) {
		Map<String, Object> report = modelReport.getReport(reportIdx);
		Map<String, Object> app = modelApps.getApp(appIdx);
		
		Map<String, String> gcmMessage = new HashMap<String, String>();
		gcmMessage.put("report_idx", String.valueOf((Long)report.get("idx")));
		gcmMessage.put("type_disaster", String.valueOf((Integer)report.get("type_disaster")));
		gcmMessage.put("type_disaster_string", "테스트" + ConstantDisasterType.code2string((Integer)report.get("type_disaster")));
		Date datetime = (Date) report.get("datetime");
		
		gcmMessage.put("timestamp", String.valueOf(datetime.getTime()));
		
		List<String> appList = new ArrayList<String>();
		appList.add((String) app.get("gcm_id"));
		
		List<Long> appIdxs = new ArrayList<Long>();
		appIdxs.add((Long)app.get("idx"));
		
		String result = ServicePushMessage.getInstance().sendMessage(appIdxs, appList, gcmMessage);  
		
		model.addAttribute("result", result);
		
		return "dev_test_gcm_sent";
	}
	
	@RequestMapping(value = "coord2address", method = RequestMethod.GET)
	public String devCoordToAddress(Locale locale, Model model) {
		return "dev_coord2address";
	}
	
	@RequestMapping(value = "coord2address_result", method = {RequestMethod.GET, RequestMethod.POST}, produces = "text/plain; charset=utf-8")
	public @ResponseBody String devCoordToAddressResult(
			@RequestParam(value="lat", required=true) double lat,
			@RequestParam(value="lng", required=true) double lng){
		String result = "";
		
		result = ApiDaumLocal.getAddress(lat, lng);
		
		return result;
	}
	
	@RequestMapping(value = "fill_address", method = {RequestMethod.GET, RequestMethod.POST}, produces = "text/plain; charset=utf-8")
	public @ResponseBody String devFillAddress(){
		String procResult = "";
		
		List<Map<String, Object>> data = modelReport.getNullAddressedReport();
		
		if(data == null){
			return "Get Null AddressedReport is Fail";
		}
		
		List<Map<String, Object>> results = new ArrayList<Map<String,Object>>();
		
		for(Map<String, Object> item: data){
			Map<String, Object> result = new HashMap<String, Object>();
			String address = ApiDaumLocal.getAddress((Double)item.get("loc_lat"), (Double)item.get("loc_lng"));

			result.put("idx", item.get("idx"));
			result.put("loc_name", address);
			
			results.add(result);
		}
		
		modelReport.updateNullAddressedReport(results);
		
		procResult = "Success";
		
		return procResult;
	}
}

