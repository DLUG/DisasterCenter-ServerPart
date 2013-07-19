package org.dlug.disastercenter.model;


import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

@Repository
public class ModelReport extends ModelImpl{
	private final static double DEGREE_PER_KM_LAT = 0.008983;
	private final static double DEGREE_PER_KM_LNG = 0.015060;
	
	public long getAmount(double lat, double lng, double rangeKM, Date startDatetime){
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		if(rangeKM != 0){
			parameters.put("lat_start", lat - (rangeKM * DEGREE_PER_KM_LAT));
			parameters.put("lat_end", lat + (rangeKM * DEGREE_PER_KM_LAT));
			parameters.put("lng_start", lng - (rangeKM * DEGREE_PER_KM_LNG));
			parameters.put("lng_end", lng + (rangeKM * DEGREE_PER_KM_LNG));
		}

		if(startDatetime != null){
			parameters.put("datetime_start", sdf.format(startDatetime));
		}
		
		try{
			long result = (Long) sqlMapClientTemplate.queryForObject("reports.get_amount", parameters);
			
			return result;
		} catch (DataAccessException e){
			e.printStackTrace();
			return -1;
		}
	}

	
	public boolean putReport(long app_idx, double lat, double lng, double accuracy, int type_report, int type_disaster, String content, Date datetime){
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("app_idx", app_idx);
		parameters.put("loc_lat", lat);
		parameters.put("loc_lng", lng);
		parameters.put("loc_accuracy", accuracy);
		parameters.put("type_report", type_report);
		parameters.put("type_disaster", type_disaster);
		parameters.put("content", content);
		parameters.put("datetime", sdf.format(datetime));
		
		try{
			sqlMapClientTemplate.insert("reports.put_report", parameters);
		} catch (DataAccessException e){
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	public List<Map<String, Object>> getReportList(int page){
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		
		parameters.put("limit_start", ((page - 1) * PAGE_AMOUNT));
		return getReportProc(parameters, 0, 0, 0, null);
	}
	
	public List<Map<String, Object>> getReportList(int page, double lat, double lng, double rangeKM, Date startDatetime){
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		
		parameters.put("limit_start", ((page - 1) * PAGE_AMOUNT));
		return getReportProc(parameters, lat, lng, rangeKM, startDatetime);
	}
	
	public List<Map<String, Object>> getReportList(long offset, double lat, double lng, double rangeKM, Date startDatetime){
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		
		parameters.put("limit_start", 0);
		parameters.put("offset", offset);
		
		return getReportProc(parameters, lat, lng, rangeKM, startDatetime);
	}	
	
	private List<Map<String, Object>> getReportProc(HashMap<String, Object> parameters, double lat, double lng, double rangeKM, Date startDatetime){
		parameters.put("limit_duration", PAGE_AMOUNT);
		
		if(rangeKM != 0){
			parameters.put("lat_start", lat - (rangeKM * DEGREE_PER_KM_LAT));
			parameters.put("lat_end", lat + (rangeKM * DEGREE_PER_KM_LAT));
			parameters.put("lng_start", lng - (rangeKM * DEGREE_PER_KM_LNG));
			parameters.put("lng_end", lng + (rangeKM * DEGREE_PER_KM_LNG));
		}

		if(startDatetime != null){
			parameters.put("datetime_start", sdf.format(startDatetime));
		}
		
		try{
			List<Map<String, Object>> result = sqlMapClientTemplate.queryForList("reports.get_report_list", parameters);
			
			return result;
		} catch (DataAccessException e){
			e.printStackTrace();
			return null;
		}
	}
	
	public Map<String, Object> getReport(long idx){
		try{
			Map<String, Object> result = (Map<String, Object>) sqlMapClientTemplate.queryForObject("reports.get_report", idx);
			
			return result;
		}  catch (DataAccessException e){
			e.printStackTrace();
			return null;
		}
	}
}
