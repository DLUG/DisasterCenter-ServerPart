package org.dlug.disastercenter.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.orm.ibatis.SqlMapClientTemplate;

public class ServiceGetterDaumSensorData extends ServicePeriodImpl{
	private static final int TYPE_WATCH = 10000;
	private static final int TYPE_ALERT = 10001;
	
	public final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public ServiceGetterDaumSensorData(){
		super("ServiceGetterDaumSensorData", 300000);
	}
	
	@Override
	protected void process(SqlMapClientTemplate sqlMapClientTemplate) {
		JSONArray listWatchTemp = ApiDaumSensor.getJSONSensorHotterThanWatchLimit(period);
		
		String content = "";
		
		if(listWatchTemp.size() > 1){
			for(Object item: listWatchTemp){
				JSONObject itemHandler = (JSONObject) item;
				
				System.out.println(item);
				
				long sensor_idx = (Long) itemHandler.get("resourceId");
				long temperature = (Long) itemHandler.get("temperature");
				
				if(temperature > 350)
					content += "호우 경보!<br>\n";
				else 
					content += "호우 주의보!<br>\n";
				
				content += "현재 기온: " + (temperature / 10) + "<br>\n";
				
				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("idx", sensor_idx);
				Map<String, Object> sensorInfo = (Map<String, Object>) sqlMapClientTemplate.queryForList("daumsensor.get_sensor").get(0);
				
				Map<String, Object> report = new HashMap<String, Object>();
				
				report.put("app_idx", 0);
				report.put("loc_lat", (Double) sensorInfo.get("lat"));
				report.put("loc_lng", (Double) sensorInfo.get("lng"));
				report.put("loc_accuracy", "1");
				report.put("loc_name", "");
				report.put("type_report", 0);
				if(temperature > 350)
					report.put("type_disaster", 902);
				else 
					report.put("type_disaster", 901);
				
				report.put("content", content);
				report.put("datetime", sdf.format(new Date()));
				
				sqlMapClientTemplate.insert("reports.put_report", report);
			}
		}
		
//		JSONArray listWatchWind = ApiDaumSensor.getJSONSensorFasterWindThanWatchLimit(period);
	}
}
