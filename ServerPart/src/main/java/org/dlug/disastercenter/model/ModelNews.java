package org.dlug.disastercenter.model;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

@Repository
public class ModelNews extends ModelImpl{
	public long getAmount(){
		try{
			long result = (Long) sqlMapClientTemplate.queryForObject("news.get_amount");
			
			return result;
		} catch (DataAccessException e){
			e.printStackTrace();
			return -1;
		}
	}
	
	public List<Map<String, Object>> getNews(int page){
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("limit_start", ((page - 1) * PAGE_AMOUNT));
		parameters.put("limit_duration", PAGE_AMOUNT);
		
		try{
			List<Map<String, Object>> result = sqlMapClientTemplate.queryForList("news.get_news", parameters);
			
			return result;
		} catch (DataAccessException e){
			e.printStackTrace();
			return null;
		}
	}
}