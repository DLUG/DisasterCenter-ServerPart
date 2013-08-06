package org.dlug.disastercenter.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.orm.ibatis.SqlMapClientTemplate;
import org.dlug.disastercenter.common.ApiDaumSensor;

public class ServiceDailyDaumSensor extends ServicePeriodImpl{
	public final static SimpleDateFormat dateFormatMySql = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public ServiceDailyDaumSensor(){
		super("EveryDayService - DaumSensor", ServicePeriodImpl.MSEC_A_DAY);
	}
	
	@Override
	protected void process(SqlMapClientTemplate sqlMapClientTemplate) {
		sqlMapClientTemplate.delete("daumsensor.flush_sensor");
		
		long sensorAmount = ApiDaumSensor.getSensorAmount();
		
		int queryAmount = (int) (((sensorAmount - 1) / 50) + 1);
		
		for(int i = 0; i < queryAmount; i++){
			JSONArray sensorList = ApiDaumSensor.getJSONSensors(i * 50);
			
			for(Object item: sensorList){
				JSONObject itemHandler = (JSONObject) item;
				
				Map<String, Object> tmpItem = new HashMap<String, Object>();
				tmpItem.put("idx", itemHandler.get("resourceId"));
				tmpItem.put("lat", itemHandler.get("latitude"));
				tmpItem.put("lng", itemHandler.get("longitude"));
				tmpItem.put("alt", itemHandler.get("altitude"));
				
				JSONObject obsTimeFirstObject = (JSONObject) itemHandler.get("firstObservationTime");
				Date obsTimeFirst = ApiDaumSensor.convertDateType(obsTimeFirstObject);
				
				tmpItem.put("obs_time_first", dateFormatMySql.format(obsTimeFirst));
				
				JSONObject obsTimeLastObject = (JSONObject) itemHandler.get("lastObservationTime");
				Date obsTimeLast = ApiDaumSensor.convertDateType(obsTimeLastObject);
				
				tmpItem.put("obs_time_last", dateFormatMySql.format(obsTimeLast));
				
				sqlMapClientTemplate.insert("daumsensor.put_sensor", tmpItem);
			}
		}
	}
}

