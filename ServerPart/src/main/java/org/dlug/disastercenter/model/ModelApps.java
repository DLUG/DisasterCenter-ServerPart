package org.dlug.disastercenter.model;


import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

@Repository
public class ModelApps extends ModelImpl{
	public List<Map<String, Object>> getApp(String uuid){
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("uuid", uuid);
		
		try{
			List<Map<String, Object>> result = sqlMapClientTemplate.queryForList("apps.get_app", parameters);
			return result;
		} catch (DataAccessException e){
			e.printStackTrace();
			return null;
		}
	}
	
	public boolean removeApp(String uuid){
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("uuid", uuid);
		
		try{
			sqlMapClientTemplate.delete("apps.delete_app", parameters);
		} catch (DataAccessException e){
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	public boolean removeAppWithGcmid(String gcm_id){
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("gcm_id", gcm_id);
		
		try{
			sqlMapClientTemplate.delete("apps.delete_app_with_gcmid", parameters);
		} catch (DataAccessException e){
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	public boolean putApp(String gcm_id, String uuid, String crypt_key, String secret_code){
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("gcm_id", gcm_id);
		parameters.put("uuid", uuid);
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
	
	public boolean putLocation(String uuid, double lat, double lng){
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("uuid", uuid);
		parameters.put("loc_lat", lat);
		parameters.put("loc_lng", lng);
		parameters.put("update_datetime", sdf.format(new Date()));
		
		try{
			sqlMapClientTemplate.update("apps.put_location", parameters);
		} catch (DataAccessException e){
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	public boolean settingAlarmRange(String uuid, double range){
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("uuid", uuid);
		parameters.put("range", range);
		
		try{
			sqlMapClientTemplate.update("apps.setting_alarmrange", parameters);
		} catch (DataAccessException e){
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	
	public boolean updateGcmId(String uuid, String gcm_id){
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("uuid", uuid);
		parameters.put("gcm_id", gcm_id);
		
		try{
			sqlMapClientTemplate.update("apps.update_gcmid", parameters);
		} catch (DataAccessException e){
			e.printStackTrace();
			return false;
		}
		
		return true;
	} 
}
