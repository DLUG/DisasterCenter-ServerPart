package org.dlug.disastercenter.controller;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.dlug.disastercenter.model.ModelApps;
import org.dlug.disastercenter.model.ModelInfo;
import org.dlug.disastercenter.model.ModelReport;
import org.dlug.disastercenter.service.ServiceGcm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Handles requests for the application home page.
 */
@Controller
@RequestMapping("/dev_test")
public class ControllerDevTest extends ControllerPages{
	
	@Autowired
	private ModelReport modelReport;

	@Autowired
	private ModelApps modelApps;

	
	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String devTestMain(Locale locale, Model model) {
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
		
		return "dev_test";
	}
	
	@RequestMapping(value = "/send_test_msg", method = RequestMethod.GET)
	public String sendTestMsg(Locale locale, Model model, @RequestParam(value="report_idx", required=true) long reportIdx,
			@RequestParam(value="app_idx", required=true) long appIdx) {
		Map<String, Object> report = modelReport.getReport(reportIdx);
		Map<String, Object> app = modelApps.getAppWithIdx(appIdx);
		
		Map<String, String> gcmMessage = new HashMap<String, String>();
		gcmMessage.put("report_idx", String.valueOf((Long)report.get("idx")));
		gcmMessage.put("type_disaster", String.valueOf((Integer)report.get("type_disaster")));
		gcmMessage.put("type_disaster_string", "테스트");
		Date datetime = (Date) report.get("datetime");
		
		gcmMessage.put("timestamp", String.valueOf(datetime.getTime()));
		
		List<String> appList = new ArrayList<String>();
		appList.add((String) app.get("gcm_id"));
		
		String result = ServiceGcm.getInstance().sendReport(appList, gcmMessage);  
		
		model.addAttribute("result", result);
		
		return "dev_test_gcm_sent";
	}
}
