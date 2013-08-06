package org.dlug.disastercenter.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dlug.disastercenter.common.ConstantAlertLimit;
import org.dlug.disastercenter.common.CoordinateTools;
import org.dlug.disastercenter.common.DisasterType;
import org.dlug.disastercenter.common.CoordinateTools.CoordLatLng;
import org.dlug.disastercenter.model.ModelApps;
import org.dlug.disastercenter.model.ModelReport;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.orm.ibatis.SqlMapClientTemplate;

public class ServiceGetterKmaData extends ServicePeriodImpl{
	public final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	private ModelReport modelReport = new ModelReport();
	private ModelApps modelApps = new ModelApps();
	
	private ApiKMA apiKMA = new ApiKMA();
	
	public ServiceGetterKmaData(){
		super("ServiceGetterKmaData", 300000);
	}
	
	@Override
	protected void process(SqlMapClientTemplate sqlMapClientTemplate) {
		modelReport.setSqlMapClientTemplate(sqlMapClientTemplate);
		modelApps.setSqlMapClientTemplate(sqlMapClientTemplate);
		
		List<Map<String, Object>> targetList = sqlMapClientTemplate.queryForList("service_kma_target.get_target_list");
		
		for(Map<String, Object> target: targetList){
			Map<String, Object> currentWeather = apiKMA.getCurrentWeather((Integer) target.get("kma_x"), (Integer) target.get("kma_y"));
			
			if((Double) currentWeather.get("temp") > ConstantAlertLimit.TEMP_HIGH_ALERT){
				CoordLatLng latlng = CoordinateTools.Kma2latlng((Integer) target.get("kma_x"), (Integer) target.get("kma_y"));
				
				String content = "[기상청] 폭염경보 기준 온도 초과!<br>\n<br>\n" 
						+ "현재 폭염경보 기준 온도인 " 
						+ ConstantAlertLimit.TEMP_HIGH_ALERT + "도가 넘었습니다. 일사병에 주의해주시기바랍니다.<br>\n<br>\n"
						+ "현재 기온: " + currentWeather.get("temp");
				
				putReport(latlng.lat, latlng.lng, DisasterType.TEMP_HIGH_ALERT, content, sqlMapClientTemplate);
			} else if((Double) currentWeather.get("temp") > ConstantAlertLimit.TEMP_HIGH_WATCH){
				CoordLatLng latlng = CoordinateTools.Kma2latlng((Integer) target.get("kma_x"), (Integer) target.get("kma_y"));
				
				String content = "[기상청] 폭염주의보 기준 온도 초과!<br>\n<br>\n" 
						+ "현재 폭염주의보 기준 온도인 " 
						+ ConstantAlertLimit.TEMP_HIGH_WATCH + " 도가 넘었습니다. 일사병에 주의해주시기바랍니다.<br>\n<br>\n"
						+ "현재 기온: " + currentWeather.get("temp");
				
				putReport(latlng.lat, latlng.lng, DisasterType.TEMP_HIGH_WATCH, content, sqlMapClientTemplate);
			}
			
			if((Double) currentWeather.get("temp") < ConstantAlertLimit.TEMP_LOW_ALERT
					&& (Double) currentWeather.get("temp") > -999){
				CoordLatLng latlng = CoordinateTools.Kma2latlng((Integer) target.get("kma_x"), (Integer) target.get("kma_y"));
				
				String content = "[기상청] 한파경보 기준 온도 미달!<br>\n<br>\n" 
						+ "현재 한파경보 기준 온도인 " 
						+ ConstantAlertLimit.TEMP_LOW_ALERT + "도를 넘지 못했습니다. 동상에 주의해주시기바랍니다.<br>\n<br>\n"
						+ "현재 기온: " + currentWeather.get("temp");
				
				Logger.info("Cold - KMA_X: " + target.get("kma_x") + ", KMA_Y: " + target.get("kma_y"));
				Logger.info("Temp: " + currentWeather.get("temp"));
				
				putReport(latlng.lat, latlng.lng, DisasterType.TEMP_LOW_ALERT, content, sqlMapClientTemplate);
			} else if((Double) currentWeather.get("temp") < ConstantAlertLimit.TEMP_LOW_WATCH
					&& (Double) currentWeather.get("temp") > -999){
				CoordLatLng latlng = CoordinateTools.Kma2latlng((Integer) target.get("kma_x"), (Integer) target.get("kma_y"));
				
				String content = "[기상청] 한파주의보 기준 온도 미달!<br>\n<br>\n" 
						+ "현재 한파주의보 기준 온도인 " 
						+ ConstantAlertLimit.TEMP_LOW_WATCH + " 도를 넘지 못했습니다. 동상에 주의해주시기바랍니다.<br>\n<br>\n"
						+ "현재 기온: " + currentWeather.get("temp");
				
				putReport(latlng.lat, latlng.lng, DisasterType.TEMP_LOW_WATCH, content, sqlMapClientTemplate);
			}
			
			if((Double) currentWeather.get("wind_speed") > ConstantAlertLimit.WIND_FAST_ALERT){
				CoordLatLng latlng = CoordinateTools.Kma2latlng((Integer) target.get("kma_x"), (Integer) target.get("kma_y"));
				
				String content = "[기상청] 강풍경보 기준 풍속 초과!<br>\n<br>\n" 
						+ "현재 강풍경보 기준 풍속인 " + ConstantAlertLimit.WIND_FAST_ALERT + " m/s가 넘었습니다.<br>\n<br>\n"
						+ "현재 풍속: " + currentWeather.get("wind_speed");
				
				putReport(latlng.lat, latlng.lng, DisasterType.WIND_FAST_ALERT, content, sqlMapClientTemplate);
			} else if((Double) currentWeather.get("wind_speed") > ConstantAlertLimit.WIND_FAST_WATCH){
				CoordLatLng latlng = CoordinateTools.Kma2latlng((Integer) target.get("kma_x"), (Integer) target.get("kma_y"));
				
				String content = "[기상청] 강풍주의보 기준 풍속 초과!<br>\n<br>\n" 
						+ "현재 강풍주의보 기준 풍속인 " + ConstantAlertLimit.WIND_FAST_WATCH + " m/s가 넘었습니다.<br>\n<br>\n"
						+ "현재 풍속: " + currentWeather.get("wind_speed");
				
				putReport(latlng.lat, latlng.lng, DisasterType.WIND_FAST_WATCH, content, sqlMapClientTemplate);
			}
		}
	}
	
	private void putReport(double lat, double lng, int type_disaster, String content, SqlMapClientTemplate sqlMapClientTemplate){
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.HOUR_OF_DAY, -1);
		
		List<Map<String, Object>> reportList = modelReport.getReportList(lat, lng, 10, type_disaster, cal.getTime());
		
		if(reportList.size() == 0){
			Map<String, Object> report = new HashMap<String, Object>();
			
			report.put("app_idx", 0);
			report.put("loc_lat", lat);
			report.put("loc_lng", lng);
			report.put("loc_accuracy", "1");
			report.put("loc_name", "");
			report.put("type_report", 0);
			report.put("type_disaster", type_disaster);
			
			report.put("content", content);
			report.put("datetime", sdf.format(new Date()));
			
			sqlMapClientTemplate.insert("reports.put_report", report);
			
			reportList = modelReport.getReportList(lat, lng, 1, type_disaster, cal.getTime());
			
			if(reportList.size() != 0){
				
				List<Map<String, Object>> targetAppList = modelApps.getAppList(lat, lng, 50);

				List<String> appGcmIdList = new ArrayList<String>();
				List<Long> appIdxList = new ArrayList<Long>();
				
				for(Map<String, Object> targetApp : targetAppList){
					double tmpDistance = CoordinateTools.calcDistance(lat, lng, (Double)targetApp.get("loc_lat"), (Double)targetApp.get("loc_lng"));
					
					if(tmpDistance < (Double)targetApp.get("setting_range")){
						appGcmIdList.add((String) targetApp.get("gcm_id"));
						appIdxList.add((Long) targetApp.get("idx"));
					}
				}
				
				if(appGcmIdList.size() > 0){
					report = reportList.get(0);
					
					Map<String, String> gcmMessage = new HashMap<String, String>();
					gcmMessage.put("report_idx", String.valueOf((Long)report.get("idx")));
					gcmMessage.put("type_disaster", String.valueOf((Integer)report.get("type_disaster")));
					
					//Deprecated
					gcmMessage.put("type_disaster_string", DisasterType.code2string((Integer)report.get("type_disaster")));
					
					gcmMessage.put("content", (String) report.get("content"));
					
					Date datetime = (Date) report.get("datetime");
					
					gcmMessage.put("timestamp", String.valueOf(datetime.getTime()));
					
		//TODO:			
					
					String result = ServiceGcm.getInstance().sendReport(appIdxList, appGcmIdList, gcmMessage);  
				}
			}
			
		}
	}
}
