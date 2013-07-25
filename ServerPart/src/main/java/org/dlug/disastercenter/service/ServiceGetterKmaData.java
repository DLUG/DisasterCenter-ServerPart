package org.dlug.disastercenter.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dlug.disastercenter.service.CoordinateConverter.CoordLatLng;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.orm.ibatis.SqlMapClientTemplate;

public class ServiceGetterKmaData extends ServicePeriodImpl{
	public final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	private ApiKMA apiKMA = new ApiKMA();
	
	public ServiceGetterKmaData(){
		super("ServiceGetterKmaData", 300000);
	}
	
	@Override
	protected void process(SqlMapClientTemplate sqlMapClientTemplate) {
		List<Map<String, Object>> targetList = sqlMapClientTemplate.queryForList("service_kma_target.get_target_list");
		
		for(Map<String, Object> target: targetList){
			Map<String, Object> currentWeather = apiKMA.getCurrentWeather((Integer) target.get("kma_x"), (Integer) target.get("kma_y"));
			
			if((Integer) currentWeather.get("temp") > WeatherAlertLimit.TEMP_HIGH_ALERT){
				CoordLatLng latlng = CoordinateConverter.Kma2latlng((Integer) target.get("kma_x"), (Integer) target.get("kma_y"));
				
				String content = "[기상청] 폭염경보 기준 온도 초과!<br>\n<br>\n" 
						+ "현재 폭염경보 기준 온도인 35도가 넘었습니다. 일사병에 주의해주시기바랍니다.";
				
				putReport(latlng.lat, latlng.lng, 906, content, sqlMapClientTemplate);
			} else if((Integer) currentWeather.get("temp") > WeatherAlertLimit.TEMP_HIGH_WATCH){
				CoordLatLng latlng = CoordinateConverter.Kma2latlng((Integer) target.get("kma_x"), (Integer) target.get("kma_y"));
				
				String content = "[기상청] 폭염주의보 기준 온도 초과!<br>\n<br>\n" 
						+ "현재 폭염주의보 기준 온도인 33도가 넘었습니다. 일사병에 주의해주시기바랍니다.";
				
				putReport(latlng.lat, latlng.lng, 905, content, sqlMapClientTemplate);
			}
			
			if((Integer) currentWeather.get("wind_speed") > WeatherAlertLimit.WIND_FAST_ALERT){
				CoordLatLng latlng = CoordinateConverter.Kma2latlng((Integer) target.get("kma_x"), (Integer) target.get("kma_y"));
				
				String content = "[기상청] 강풍경보 기준 풍속 초과!<br>\n<br>\n" 
						+ "현재 강풍경보 기준 풍속인 " + WeatherAlertLimit.WIND_FAST_ALERT + " m/s가 넘었습니다.";
				
				putReport(latlng.lat, latlng.lng, 904, content, sqlMapClientTemplate);
			} else if((Integer) currentWeather.get("wind_speed") > WeatherAlertLimit.WIND_FAST_WATCH){
				CoordLatLng latlng = CoordinateConverter.Kma2latlng((Integer) target.get("kma_x"), (Integer) target.get("kma_y"));
				
				String content = "[기상청] 강풍주의보 기준 풍속 초과!<br>\n<br>\n" 
						+ "현재 강풍주의보 기준 풍속인 " + WeatherAlertLimit.WIND_FAST_ALERT + " m/s가 넘었습니다.";
				
				putReport(latlng.lat, latlng.lng, 903, content, sqlMapClientTemplate);
			}
		}
	}
	
	private void putReport(double lat, double lng, int type_disaster, String content, SqlMapClientTemplate sqlMapClientTemplate){
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
	}
}
