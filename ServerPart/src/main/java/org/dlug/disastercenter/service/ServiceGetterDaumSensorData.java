package org.dlug.disastercenter.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.dlug.disastercenter.common.ConstantAlertLimit;
import org.dlug.disastercenter.common.DisasterType;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.orm.ibatis.SqlMapClientTemplate;

public class ServiceGetterDaumSensorData extends ServicePeriodImpl{
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
				
				if(temperature > 35)
					content += "[Daum기상센서] 폭염경보 기준 온도 초과!<br>\n<br>\n"
								+ "현재 폭염경보 기준 온도인 " + ConstantAlertLimit.TEMP_HIGH_ALERT + "도가 넘었습니다. 일사병에 주의해주시기바랍니다.<br>\n<br>\n";
				else if(temperature > 33)
					content += "[Daum기상센서] 폭염주의보 기준 온도 초과!<br>\n<br>\n"
							+ "현재 폭염주의보 기준 온도인 " + ConstantAlertLimit.TEMP_HIGH_WATCH + "도가 넘었습니다. 일사병에 주의해주시기바랍니다.<br>\n<br>\n";
				
				content += "현재 기온: " + (temperature) + "<br>\n";
				
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
				if(temperature > 35)
					report.put("type_disaster", DisasterType.TEMP_HIGH_ALERT);
				else 
					report.put("type_disaster", DisasterType.TEMP_HIGH_WATCH);
				
				report.put("content", content);
				report.put("datetime", sdf.format(new Date()));
				
				sqlMapClientTemplate.insert("reports.put_report", report);
			}
		}
	}
}
