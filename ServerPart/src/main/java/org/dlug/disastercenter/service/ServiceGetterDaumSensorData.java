package org.dlug.disastercenter.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.springframework.orm.ibatis.SqlMapClientTemplate;

public class ServiceGetterDaumSensorData extends ServicePeriodImpl{
	public ServiceGetterDaumSensorData(){
		super("EveryHourService", 60000);
	}
	
	@Override
	protected void process(SqlMapClientTemplate sqlMapClientTemplate) {
		JSONArray listWatchTemp = ApiDaumSensor.getJSONSensorHotterThanWatchLimit(period);
		
		
		
		JSONArray listWatchWind = ApiDaumSensor.getJSONSensorFasterWindThanWatchLimit(period);
	}
}
