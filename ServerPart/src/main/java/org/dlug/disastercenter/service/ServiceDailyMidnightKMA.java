package org.dlug.disastercenter.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dlug.disastercenter.common.CoordinateConverter;
import org.dlug.disastercenter.common.CoordinateConverter.CoordLatLng;
import org.dlug.disastercenter.model.ModelReport;
import org.json.simple.JSONObject;
import org.springframework.orm.ibatis.SqlMapClientTemplate;

public class ServiceDailyMidnightKMA extends ServiceImpl {
	private ApiKMA apiKMA = new ApiKMA();
	
	public final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public ServiceDailyMidnightKMA() {
		super("Daily Midnight Service - KMA");
		
		ServiceScheduler.getInstance().setScheduler(this, 00, 00);
	}

	@Override
	protected void process(SqlMapClientTemplate sqlMapClientTemplate) {
		List<Map<String, Object>> targetList = sqlMapClientTemplate.queryForList("service_kma_target.get_target_list");
		
		for(Map<String, Object> target: targetList){
			Map<String, Object> todayWeather = apiKMA.getTodayWeather((Integer) target.get("kma_x"), (Integer) target.get("kma_y"));
			
			if(todayWeather.containsKey("alert_high_temp_start")){
				CoordLatLng latlng = CoordinateConverter.Kma2latlng((Integer) target.get("kma_x"), (Integer) target.get("kma_y"));
				
				String content = "[기상청] 폭염경보 예정!<br>\n<br>\n" 
					+ todayWeather.get("alert_high_temp_start") + " 시 부터 "
					+ todayWeather.get("alert_high_temp_end") + " 시 까지 "
					+ "최고 " + todayWeather.get("temp_max") + " 도의 온도가 예상되어 폭염경보가 발령될 가능성이 높습니다.";
				
				putReport(latlng.lat, latlng.lng, 906, content, sqlMapClientTemplate);
			}
			
			if(todayWeather.containsKey("watch_high_temp_start")){
				CoordLatLng latlng = CoordinateConverter.Kma2latlng((Integer) target.get("kma_x"), (Integer) target.get("kma_y"));
				
				String content = "[기상청] 폭염주의보 예정!<br>\n<br>\n" 
					+ todayWeather.get("watch_high_temp_start") + " 시 부터 "
					+ todayWeather.get("watch_high_temp_end") + " 시 까지 "
					+ "최고 " + todayWeather.get("temp_max") + " 도의 온도가 예상되어 폭염주의보가 발령될 가능성이 높습니다.";
				
				putReport(latlng.lat, latlng.lng, 905, content, sqlMapClientTemplate);
			}
			
			if(todayWeather.containsKey("alert_low_temp_start")){
				CoordLatLng latlng = CoordinateConverter.Kma2latlng((Integer) target.get("kma_x"), (Integer) target.get("kma_y"));
				
				String content = "[기상청] 한파경보 예정!<br>\n<br>\n" 
					+ todayWeather.get("alert_low_temp_start") + " 시 부터 "
					+ todayWeather.get("alert_low_temp_end") + " 시 까지 "
					+ "최저 " + todayWeather.get("temp_min") + " 도의 온도가 예상되어 한파경보가 발령될 가능성이 높습니다.";
				
				putReport(latlng.lat, latlng.lng, 908, content, sqlMapClientTemplate);
			}
			
			if(todayWeather.containsKey("watch_low_temp_start")){
				CoordLatLng latlng = CoordinateConverter.Kma2latlng((Integer) target.get("kma_x"), (Integer) target.get("kma_y"));
				
				String content = "[기상청] 한파주의보 예정!<br>\n<br>\n" 
					+ todayWeather.get("watch_low_temp_start") + " 시 부터 "
					+ todayWeather.get("watch_low_temp_end") + " 시 까지 "
					+ "최저 " + todayWeather.get("temp_min") + " 도의 온도가 예상되어 한파주의보가 발령될 가능성이 높습니다.";
				
				putReport(latlng.lat, latlng.lng, 907, content, sqlMapClientTemplate);
			}
			
			if(todayWeather.containsKey("alert_hard_rain_start")){
				CoordLatLng latlng = CoordinateConverter.Kma2latlng((Integer) target.get("kma_x"), (Integer) target.get("kma_y"));
				
				String content = "[기상청] 호우경보 예정!<br>\n<br>\n" 
					+ todayWeather.get("alert_hard_rain_start") + " 시 부터 "
					+ "최대 " + todayWeather.get("rain6_max") + " mm의 6시간강우량이 예상되어 호우경보가 발령될 가능성이 높습니다.";
				
				putReport(latlng.lat, latlng.lng, 902, content, sqlMapClientTemplate);
			}
			
			if(todayWeather.containsKey("watch_hard_rain_start")){
				CoordLatLng latlng = CoordinateConverter.Kma2latlng((Integer) target.get("kma_x"), (Integer) target.get("kma_y"));
				
				String content = "[기상청] 호우주의보 예정!<br>\n<br>\n" 
					+ todayWeather.get("watch_hard_rain_start") + " 시 부터 "
					+ "최대 " + todayWeather.get("rain6_max") + " mm의 6시간강우량이 예상되어 호우주의보가 발령될 가능성이 높습니다.";
				
				putReport(latlng.lat, latlng.lng, 901, content, sqlMapClientTemplate);
			}
			
			if(todayWeather.containsKey("alert_fast_wind_start")){
				CoordLatLng latlng = CoordinateConverter.Kma2latlng((Integer) target.get("kma_x"), (Integer) target.get("kma_y"));
				
				String content = "[기상청] 강풍경보 예정!<br>\n<br>\n" 
					+ todayWeather.get("alert_fast_wind_start") + " 시 부터 "
					+ "최대 " + todayWeather.get("wind_max") + " m/s의 강풍이 예상되어 강풍경보가 발령될 가능성이 높습니다.";
				
				putReport(latlng.lat, latlng.lng, 904, content, sqlMapClientTemplate);
			}
			
			if(todayWeather.containsKey("watch_fast_wind_start")){
				CoordLatLng latlng = CoordinateConverter.Kma2latlng((Integer) target.get("kma_x"), (Integer) target.get("kma_y"));
				
				String content = "[기상청] 강풍주의보 예정!<br>\n<br>\n" 
					+ todayWeather.get("watch_fast_wind_start") + " 시 부터 "
					+ "최대 " + todayWeather.get("wind_max") + " m/s의 강풍이 예상되어 강풍주의보가 발령될 가능성이 높습니다.";
				
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
