package org.dlug.disastercenter.model;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

@Repository
public class ModelApps extends ModelImpl{
	public List<Map<String, Object>> getApp(String gcm_id){
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("gcm_id", gcm_id);
		
		try{
			List<Map<String, Object>> result = sqlMapClientTemplate.queryForList("apps.get_app", parameters);
			return result;
		} catch (DataAccessException e){
			e.printStackTrace();
			return null;
		}
	}
	
	public boolean removeApp(String gcm_id){
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("gcm_id", gcm_id);
		
		try{
			sqlMapClientTemplate.delete("apps.delete_app", parameters);
		} catch (DataAccessException e){
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	public boolean putApp(String gcm_id, String crypt_key, String secret_code){
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("gcm_id", gcm_id);
		parameters.put("crypt_key", crypt_key);
		parameters.put("secret_code", secret_code);
		
		try{
			sqlMapClientTemplate.insert("apps.put_app", parameters);
		} catch (DataAccessException e){
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	public boolean putLocation(String gcm_id, double lat, double lng){
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("gcm_id", gcm_id);
		parameters.put("loc_lat", lat);
		parameters.put("loc_lng", lng);
		
		try{
			sqlMapClientTemplate.update("apps.put_location", parameters);
		} catch (DataAccessException e){
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
}
