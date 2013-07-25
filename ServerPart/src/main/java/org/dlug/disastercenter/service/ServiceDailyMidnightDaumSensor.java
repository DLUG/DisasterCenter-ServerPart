package org.dlug.disastercenter.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.springframework.orm.ibatis.SqlMapClientTemplate;

public class ServiceDailyMidnightDaumSensor extends ServiceImpl {

	public ServiceDailyMidnightDaumSensor() {
		super("Daily Midnight Service - DaumSensor");
		
		ServiceScheduler.getInstance().setScheduler(this, 00, 00);
	}

	@Override
	protected void process(SqlMapClientTemplate sqlMapClientTemplate) {
		List<Map<String, Object>> sensorList = sqlMapClientTemplate.queryForList("daumsensor.get_sensor_list");
		
		for(Map<String, Object> item: sensorList){
			int sensorIdx = (Integer) item.get("idx");
			
			JSONObject lowestHumidityObj = (JSONObject) ApiDaumSensor.getJSONLowestHumidityYesterday(sensorIdx).get(0);
			String lowestHumidity = (String) lowestHumidityObj.get("humidity");
			
			JSONObject coldestTempObj = (JSONObject) ApiDaumSensor.getJSONColdestTempYesterday(sensorIdx).get(0);
			String coldestTemp = (String) coldestTempObj.get("temperature");
			
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("lowest_humidity", lowestHumidity);
			parameters.put("lowest_temp", coldestTemp);
			
			sqlMapClientTemplate.update("daumsensor.update_daily_weather", parameters);
			
		}
	}
}
