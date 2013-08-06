package org.dlug.disastercenter.controller;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dlug.disastercenter.common.CoordinateTools;
import org.dlug.disastercenter.common.CoordinateTools.CoordKma;
import org.dlug.disastercenter.model.ModelApps;
import org.dlug.disastercenter.model.ModelInfo;
import org.dlug.disastercenter.model.ModelKmaTarget;
import org.dlug.disastercenter.model.ModelNews;
import org.dlug.disastercenter.model.ModelReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/api")
public class ControllerAPI {
	@Autowired
	private ModelApps modelApp;
	
	@Autowired
	private ModelInfo modelInfo;
	
	@Autowired
	private ModelNews modelNews;
	
	@Autowired
	private ModelReport modelReport;
	
	@Autowired
	private ModelKmaTarget modelKmaTarget;
	
	private static final int ERRCODE_DB = -299;
	private static final int ERRCODE_AUTH = -199;
	
	private static final Logger logger = LoggerFactory.getLogger(ControllerAPI.class);
	
	@RequestMapping(value = "reg_app", method = {RequestMethod.GET, RequestMethod.POST})
	public @ResponseBody Map<String, Object> regApp(
			@RequestParam(value="gcm_id", required=true) String gcm_id,
			@RequestParam(value="uuid", required=true) String uuid,
			@RequestParam(value="secret_code", required=false, defaultValue="") String secret_code){
		logger.info("API/RegApp");
		Map<String, Object> result = new HashMap<String, Object>();
		
		List<Map<String, Object>> tmpApps = modelApp.getAppList(uuid);
		
		if(tmpApps == null){
			return errMsgDB();
		} else if(tmpApps.size() != 0){
			logger.info("API/RegApp Exist App");
			
			long appIdx = chkPermNgetIdx(uuid, secret_code);
			if(appIdx == ERRCODE_DB)
				return errMsgDB();
			else if (appIdx == ERRCODE_AUTH)
				return errMsgAuth();
			
			if(modelApp.updateGcmId(uuid, gcm_id)){
				result.put("status", 0);
				result.put("msg", "Success");
				result.put("secret_code",secret_code);
			} else {
				return errMsgDB();
			}			
			
			return result;
		}
		
		modelApp.removeAppWithGcmid(gcm_id);
		
		String crypt_key = String.valueOf((int)(Math.random() * 1024 + 1024));
		String tmpSecret_code = hashing(uuid + crypt_key);
		
		if(modelApp.putApp(gcm_id, uuid, crypt_key, tmpSecret_code)){
			result.put("status", 0);
			result.put("msg", "Success");
			result.put("secret_code", tmpSecret_code);
		} else {
			return errMsgDB();
		}
		
		return result;
	}

	@RequestMapping(value = "unreg_app", method = {RequestMethod.GET, RequestMethod.POST})
	public @ResponseBody Map<String, Object> unregApp(
			@RequestParam(value="uuid", required=true) String uuid,
			@RequestParam(value="secret_code", required=true) String secret_code){
		logger.info("API/UnregApp");
		Map<String, Object> result = new HashMap<String, Object>();
		
		long appIdx = chkPermNgetIdx(uuid, secret_code);
		if(appIdx == ERRCODE_DB)
			return errMsgDB();
		else if (appIdx == ERRCODE_AUTH)
			return errMsgAuth();
		
		if(modelApp.removeApp(uuid)){
			result.put("status", 0);
			result.put("msg", "Success");
		} else {
			return errMsgDB();
		}
		
		return result;
	}
	
	@RequestMapping(value = "put_location", method = {RequestMethod.GET, RequestMethod.POST})
	public @ResponseBody Map<String, Object> putLocation(
			@RequestParam(value="uuid", required=true) String uuid,
			@RequestParam(value="secret_code", required=true) String secret_code,
			@RequestParam(value="lat", required=true) double lat,
			@RequestParam(value="lng", required=true) double lng){
		logger.info("API/PutLocation");
		Map<String, Object> result = new HashMap<String, Object>();
		
		long appIdx = chkPermNgetIdx(uuid, secret_code);
		if(appIdx == ERRCODE_DB)
			return errMsgDB();
		else if (appIdx == ERRCODE_AUTH)
			return errMsgAuth();
		
		if(modelApp.putLocation(uuid, lat, lng)){
			result.put("status", 0);
			result.put("msg", "Success");
			
			CoordKma kmaCoord = CoordinateTools.latlng2Kma(lat, lng);
			List<Map<String, Object>> listTarget = modelKmaTarget.getTargetList(kmaCoord.x, kmaCoord.y);
			
			if(listTarget.size() == 0){
				modelKmaTarget.putTarget(kmaCoord.x, kmaCoord.y);
				
			}
		} else {
			return errMsgDB();
		}
		
		return result;
	}
	
	@RequestMapping(value = "report_disaster", method = {RequestMethod.GET, RequestMethod.POST})
	public @ResponseBody Map<String, Object> reportDisaster(
			@RequestParam(value="uuid", required=true) String uuid,
			@RequestParam(value="secret_code", required=true) String secret_code,
			@RequestParam(value="lat", required=true) double lat,
			@RequestParam(value="lng", required=true) double lng,
			@RequestParam(value="accuracy", required=true) double accuracy,
			@RequestParam(value="type_disaster", required=true) int type_disaster,
			@RequestParam(value="content", required=true) String content){
		logger.info("API/ReportDisaster");
		Map<String, Object> result = new HashMap<String, Object>();
		
		long appIdx = chkPermNgetIdx(uuid, secret_code);
		if(appIdx == ERRCODE_DB)
			return errMsgDB();
		else if (appIdx == ERRCODE_AUTH)
			return errMsgAuth();
		
		if(modelReport.putReport(appIdx, lat, lng, accuracy, 1, type_disaster, content, new Date())){
			result.put("status", 0);
			result.put("msg", "Success");
		} else {
			return errMsgDB();
		}
		
		return result;
	}
	
	@RequestMapping(value = "disaster_report", method = {RequestMethod.GET, RequestMethod.POST})
	public @ResponseBody Map<String, Object> disasterReport(
			@RequestParam(value="lat", required=false, defaultValue="0") double lat,
			@RequestParam(value="lng", required=false, defaultValue="0") double lng,
			@RequestParam(value="range", required=false, defaultValue="0") double range,
			@RequestParam(value="page", required=false, defaultValue="1") int page,
			@RequestParam(value="offset", required=false, defaultValue="0") long offset){
		logger.info("API/DisasterReport");
		Map<String, Object> result = new HashMap<String, Object>();
		
		if(lat != 0){
			if(lng == 0 || range == 0){
				result.put("status", 399);
				result.put("msg", "Lack InputData");
				return result;
			}
		}
		
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -1);
		Date startDatetime = cal.getTime();
		
		List<Map<String, Object>> resultList = null;
		
		if(offset == 0){
			resultList = modelReport.getReportList(page, lat, lng, range, startDatetime);
		} else {
			resultList = modelReport.getReportList(offset, lat, lng, range, startDatetime);
		}
		
		if(resultList == null){
			return errMsgDB();
		} else if(resultList.size() == 0){
			return errMsgEmpty();
		} else {
			result.put("status", 0);
			result.put("msg", "Success");
			result.put("count", resultList.size());

			long resultAmount = modelReport.getAmount(lat, lng, range, startDatetime);
			if(resultAmount != -1){
				result.put("total_amount", resultAmount);
				result.put("page_amount", (int)((resultAmount - 1) / modelReport.PAGE_AMOUNT) + 1);
			} else {
				result.clear();
				result.put("status", 999);
				result.put("msg", "Get Amount Report Error");
				return result;
			}
			
			List<Map<String, Object>> data = new ArrayList<Map<String,Object>>();
			for(Map<String, Object> item: resultList){
				Map<String, Object> tmpItem = new HashMap<String, Object>();
				tmpItem.put("idx", item.get("idx"));
				tmpItem.put("lat", item.get("loc_lat"));
				tmpItem.put("lng", item.get("loc_lng"));
				tmpItem.put("loc_name", item.get("loc_name"));
				
				Date datetime = (Date) item.get("datetime");
				
				tmpItem.put("timestamp", datetime.getTime());
				tmpItem.put("type_report", item.get("type_report"));
				tmpItem.put("type_disaster", item.get("type_disaster"));
				tmpItem.put("content", item.get("content"));
				data.add(tmpItem);
			}
			result.put("data", data);
		}
		
		return result;
	}
	
	@RequestMapping(value = "disaster_news", method = {RequestMethod.GET, RequestMethod.POST})
	public @ResponseBody Map<String, Object> disasterNews(
			@RequestParam(value="page", required=false, defaultValue="1") int page,
			@RequestParam(value="offset", required=false, defaultValue="0") long offset){
		logger.info("API/DisasterNews");
		Map<String, Object> result = new HashMap<String, Object>();
		
		List<Map<String, Object>> resultList = null;
		
		if(offset == 0){
			resultList = modelNews.getNewsList(page);
		} else {
			resultList = modelNews.getNewsList(offset);
		}
		
		if(resultList == null){
			return errMsgDB();
		} else if(resultList.size() == 0){
			return errMsgEmpty();
		} else {
			result.put("status", 0);
			result.put("msg", "Success");
			result.put("count", resultList.size());
			
			long resultAmount = modelNews.getAmount();
			if(resultAmount != -1){
				result.put("total_amount", resultAmount);
				result.put("page_amount", (int)((resultAmount - 1) / modelNews.PAGE_AMOUNT) + 1);
			} else {
				result.clear();
				result.put("status", 999);
				result.put("msg", "Get Amount Report Error");
				return result;
			}
			
			List<Map<String, Object>> data = new ArrayList<Map<String,Object>>();
			for(Map<String, Object> item: resultList){
				Map<String, Object> tmpItem = new HashMap<String, Object>();
				tmpItem.put("idx", item.get("idx"));
				if(item.get("loc_lat") != null){
					tmpItem.put("lat", item.get("loc_lat"));
					tmpItem.put("lat", item.get("loc_lng"));
					tmpItem.put("loc_name", item.get("loc_name"));
				}
				
				Date datetime = (Date) item.get("datetime");
				
				tmpItem.put("timestamp", datetime.getTime());
				tmpItem.put("type_disaster", item.get("type_disaster"));
				
				String title = (String) item.get("title");
				try {
					title = URLDecoder.decode(title, "UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				
				tmpItem.put("title", title);
				
				String content = (String) item.get("content");
				try {
					content = URLDecoder.decode(content, "UTF-8");
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				tmpItem.put("content", content);
				data.add(tmpItem);
			}
			result.put("data", data);
		}
		
		return result;
	}
	
	@RequestMapping(value = "disaster_info", method = {RequestMethod.GET, RequestMethod.POST})
	public @ResponseBody Map<String, Object> disasterInfo(
			@RequestParam(value="page", required=false, defaultValue="1") int page,
			@RequestParam(value="offset", required=false, defaultValue="0") long offset){
		logger.info("API/DisasterInfo");
		Map<String, Object> result = new HashMap<String, Object>();
		
		List<Map<String, Object>> resultList = null;
		
		if(offset == 0){
			resultList = modelInfo.getInfoList(page);
		} else {
			resultList = modelInfo.getInfoList(offset);
		}
		
		if(resultList == null){
			return errMsgDB();
		} else if(resultList.size() == 0){
			return errMsgEmpty();
		} else {
			result.put("status", 0);
			result.put("msg", "Success");
			result.put("count", resultList.size());
			
			long resultAmount = modelNews.getAmount();
			if(resultAmount != -1){
				result.put("total_amount", resultAmount);
				result.put("page_amount", (int)((resultAmount - 1) / modelNews.PAGE_AMOUNT) + 1);
			} else {
				result.clear();
				result.put("status", 999);
				result.put("msg", "Get Amount Report Error");
				return result;
			}
			
			List<Map<String, Object>> data = new ArrayList<Map<String,Object>>();
			for(Map<String, Object> item: resultList){
				Map<String, Object> tmpItem = new HashMap<String, Object>();
				tmpItem.put("idx", item.get("idx"));
				tmpItem.put("type_disaster", item.get("type_disaster"));
				
				Date datetime = (Date) item.get("datetime");
				
				tmpItem.put("timestamp", datetime.getTime());
				tmpItem.put("title", item.get("title"));
				tmpItem.put("content", item.get("content"));
				data.add(tmpItem);
			}
			result.put("data", data);
		}
		
		return result;
	}
	
	@RequestMapping(value = "setting_alarmrange", method = {RequestMethod.GET, RequestMethod.POST})
	public @ResponseBody Map<String, Object> settingAlarmRange(
			@RequestParam(value="uuid", required=true) String uuid,
			@RequestParam(value="secret_code", required=true) String secret_code,
			@RequestParam(value="range", required=true) double range){
		logger.info("API/SettingAlarmRange");
		Map<String, Object> result = new HashMap<String, Object>();
		
		long appIdx = chkPermNgetIdx(uuid, secret_code);
		if(appIdx == ERRCODE_DB)
			return errMsgDB();
		else if (appIdx == ERRCODE_AUTH)
			return errMsgAuth();
		
		if(modelApp.settingAlarmRange(uuid, range)){
			result.put("status", 0);
			result.put("msg", "Success");
		} else {
			return errMsgDB();
		}
		
		return result;
	}
	
// ========= Utility =========
	
	private long chkPermNgetIdx(String uuid, String secret_code){
		List<Map<String, Object>> tmpApps = modelApp.getAppList(uuid);
		
		if(tmpApps == null){
			return ERRCODE_DB;
		} else if(tmpApps.size() == 0){
			return ERRCODE_AUTH;
		}
		
		Map<String, Object> tmpApp = tmpApps.get(0);
		
		int crypt_key = (Integer) tmpApp.get("crypt_key");
		String tmpSecretCode = hashing(uuid + crypt_key);
		
		if(!tmpSecretCode.equals(secret_code)){
			return ERRCODE_AUTH;
		}
		
		return (Long) tmpApp.get("idx");
	}
	
	private Map<String, Object> errMsgEmpty(){
		Map<String, Object> result = new HashMap<String, Object>();
		logger.info("API Data Empty");
		result.put("status", 0);
		result.put("msg", "Data Empty");
		
		return result;
	}
	
	private Map<String, Object> errMsgDB(){
		Map<String, Object> result = new HashMap<String, Object>();
		logger.info("API DB Error");
		result.put("status", 999);
		result.put("msg", "DB Error");
		
		return result;
	}
	
	private Map<String, Object> errMsgAuth(){
		Map<String, Object> result = new HashMap<String, Object>();
		logger.info("API/Auth DB Error");
		result.put("status", 199);
		result.put("msg", "Auth Error");
		
		return result;
	}
	
	private static String hashing(String src) {
	    String output;

	    try {
	        MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(src.getBytes());
	        byte[] hash = digest.digest();
	        BigInteger bigInt = new BigInteger(1, hash);
	        output = bigInt.toString(16);
	    } 
	    catch (Exception e) {
	        e.printStackTrace(System.err);
	        return null;
	    }

	    return output;
	}
}