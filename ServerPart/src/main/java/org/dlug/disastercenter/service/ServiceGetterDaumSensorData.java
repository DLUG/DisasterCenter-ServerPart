package org.dlug.disastercenter.service;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dlug.disastercenter.common.ApiDaumLocal;
import org.dlug.disastercenter.common.ApiDaumSensor;
import org.dlug.disastercenter.common.ConstantAlertLimit;
import org.dlug.disastercenter.common.DisasterType;
import org.dlug.disastercenter.model.ModelReport;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.orm.ibatis.SqlMapClientTemplate;

public class ServiceGetterDaumSensorData extends ServicePeriodImpl{
	public final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private static ModelReport modelReport = new ModelReport();
	
	public ServiceGetterDaumSensorData(){
		super("ServiceGetterDaumSensorData", 300000);
	}
	
	@Override
	protected void process(SqlMapClientTemplate sqlMapClientTemplate) {
		modelReport.setSqlMapClientTemplate(sqlMapClientTemplate);
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
				@SuppressWarnings("unchecked")
				Map<String, Object> sensorInfo = (Map<String, Object>) sqlMapClientTemplate.queryForList("daumsensor.get_sensor").get(0);
				
				
				double lat = (Double) sensorInfo.get("lat");
				double lng = (Double) sensorInfo.get("lng");
				
				Date reportDate = new Date();
				
				int typeDisaster = 0;
				
				if(temperature > 35)
					typeDisaster = DisasterType.TEMP_HIGH_ALERT;
				else 
					typeDisaster = DisasterType.TEMP_HIGH_WATCH;
				
				
				Calendar cal = Calendar.getInstance();
				cal.add(Calendar.HOUR_OF_DAY, -1);
				
				List<Map<String, Object>> reportList = modelReport.getReportList(lat, lng, 10, typeDisaster, cal.getTime());
				
					if(reportList.size() == 0){
					
					String locName = ApiDaumLocal.getAddress(lat, lng);
					
	/* Remove This When Process Perfectly				
					Map<String, Object> report = new HashMap<String, Object>();
					
					report.put("app_idx", 0);
					report.put("loc_lat", lat);
					report.put("loc_lng", lng);
					report.put("loc_accuracy", "1");
					report.put("loc_name", "");
					report.put("type_report", 0);
					
					
					report.put("type_disaster", typeDisaster);
					
					report.put("content", content);
					report.put("datetime", sdf.format(reportDate));
					
					sqlMapClientTemplate.insert("reports.put_report", report);
	*/				
					
					
					modelReport.putReport(0, lat, lng, locName, 1, 0, typeDisaster, content, reportDate);
					
					ServicePushMessage.getInstance().pushReport(lat, lng, typeDisaster, reportDate);
				}
			}
		}
	}
}
