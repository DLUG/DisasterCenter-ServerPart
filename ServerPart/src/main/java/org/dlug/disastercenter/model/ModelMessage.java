package org.dlug.disastercenter.model;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

@Repository
public class ModelMessage extends ModelImpl{
	public List<Map<String, Object>> getMessageList(){
		try{
			@SuppressWarnings("unchecked")
			List<Map<String, Object>> result = sqlMapClientTemplate.queryForList("message.get_message_list");
			
			return result;
		} catch (DataAccessException e){
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Map<String, Object>> getTargetList(int kma_x, int kma_y){
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("kma_x", kma_x);
		parameters.put("kma_y", kma_y);
		
		try{
			@SuppressWarnings("unchecked")
			List<Map<String, Object>> result = sqlMapClientTemplate.queryForList("service_kma_target.get_target_list_with_kmacoord", parameters);
			
			return result;
		} catch (DataAccessException e){
			e.printStackTrace();
			return null;
		}
	}
	
	public boolean putMessage(long appIdx, long reportIdx){
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("app_idx", appIdx);
		parameters.put("report_idx", reportIdx);
		
		try{
			sqlMapClientTemplate.update("message.put_message", parameters);
		} catch (DataAccessException e){
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
}
