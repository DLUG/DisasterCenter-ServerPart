package org.dlug.disastercenter.model;


import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dlug.disastercenter.common.ConstantCoordinate;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

@Repository
public class ModelReport extends ModelImpl{
	public long getAmount(double lat, double lng, double rangeKM, Date startDatetime){
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		if(rangeKM != 0){
			parameters.put("lat_start", lat - (rangeKM * ConstantCoordinate.DEGREE_PER_KM_LAT));
			parameters.put("lat_end", lat + (rangeKM * ConstantCoordinate.DEGREE_PER_KM_LAT));
			parameters.put("lng_start", lng - (rangeKM * ConstantCoordinate.DEGREE_PER_KM_LNG));
			parameters.put("lng_end", lng + (rangeKM * ConstantCoordinate.DEGREE_PER_KM_LNG));
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

	
	public boolean putReport(long app_idx, double lat, double lng, String loc_name, double accuracy, int type_report, int type_disaster, String content, Date datetime){
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("app_idx", app_idx);
		parameters.put("loc_lat", lat);
		parameters.put("loc_lng", lng);
		parameters.put("loc_name", loc_name);
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
		parameters.put("limit_duration", PAGE_AMOUNT);
		
		return getReportList(parameters, 0, 0, 0, null);
	}
	
	public List<Map<String, Object>> getReportList(double lat, double lng, double rangeKM, int type_disaster, Date startDatetime){
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		
		parameters.put("type_disaster", type_disaster);
		
		return getReportList(parameters, lat, lng, rangeKM, startDatetime);
	}
	
	public List<Map<String, Object>> getReportList(int page, double lat, double lng, double rangeKM, Date startDatetime){
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		
		parameters.put("limit_start", ((page - 1) * PAGE_AMOUNT));
		parameters.put("limit_duration", PAGE_AMOUNT);
		
		return getReportList(parameters, lat, lng, rangeKM, startDatetime);
	}
	
	public List<Map<String, Object>> getReportList(long offset, double lat, double lng, double rangeKM, Date startDatetime){
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		
		parameters.put("limit_start", 0);
		parameters.put("offset", offset);
		parameters.put("limit_duration", PAGE_AMOUNT);
		
		return getReportList(parameters, lat, lng, rangeKM, startDatetime);
	}	
	
	public List<Map<String, Object>> getReportList(Map<String, Object> parameters, double lat, double lng, double rangeKM, Date startDatetime){
		if(rangeKM != 0){
			parameters.put("lat_start", lat - (rangeKM * ConstantCoordinate.DEGREE_PER_KM_LAT));
			parameters.put("lat_end", lat + (rangeKM * ConstantCoordinate.DEGREE_PER_KM_LAT));
			parameters.put("lng_start", lng - (rangeKM * ConstantCoordinate.DEGREE_PER_KM_LNG));
			parameters.put("lng_end", lng + (rangeKM * ConstantCoordinate.DEGREE_PER_KM_LNG));
		}

		if(startDatetime != null){
			parameters.put("datetime_start", sdf.format(startDatetime));
		}
		
		try{
			@SuppressWarnings("unchecked")
			List<Map<String, Object>> result = sqlMapClientTemplate.queryForList("reports.get_report_list", parameters);
			
			return result;
		} catch (DataAccessException e){
			e.printStackTrace();
			return null;
		}
	}
	
	public Map<String, Object> getReport(long idx){
		try{
			@SuppressWarnings("unchecked")
			Map<String, Object> result = (Map<String, Object>) sqlMapClientTemplate.queryForObject("reports.get_report", idx);
			
			return result;
		}  catch (DataAccessException e){
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Map<String, Object>> getNullAddressedReport(){
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> result = (List<Map<String, Object>>) sqlMapClientTemplate.queryForList("reports.get_null_address");
		
		return result;
	}
	
	public void updateNullAddressedReport(List<Map<String, Object>> list){
		for(Map<String, Object> item: list){
			sqlMapClientTemplate.update("reports.update_null_address", item);
		}
	}
}
