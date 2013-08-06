package org.dlug.disastercenter.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.dlug.disastercenter.common.DisasterType;
import org.dlug.disastercenter.model.ModelNews;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Handles requests for the application home page.
 */
@Controller
@RequestMapping("/disaster_news")
public class ControllerDisasterNews extends ControllerPages{
	
	@Autowired
	private ModelNews modelNews;
	
	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String disasterNews(Locale locale, Model model) {
		List<Map<String, Object>> articleList = modelNews.getNewsList(1);
		
		if(articleList == null){
			return "error";
		}
		
		List<Map<String, Object>> tmpList = new ArrayList<Map<String, Object>>();
		
		for(Map<String, Object> article: articleList){
			Map<String, Object> tmpItem = new HashMap<String, Object>();
			
			tmpItem.put("idx", article.get("idx"));
			if(article.containsKey("loc_lat")){
				tmpItem.put("lat", article.get("loc_lat"));
				tmpItem.put("lng", article.get("loc_lng"));
				tmpItem.put("loc_name", article.get("loc_name"));
			}
			tmpItem.put("type_disaster", DisasterType.code2string((Integer) article.get("type_disaster")));
			tmpItem.put("title", article.get("title"));
			String datetime = modelNews.sdf.format(((Date) article.get("datetime")));
			tmpItem.put("datetime", datetime);
			
			tmpList.add(tmpItem);
		}
		
		model.addAttribute("list", tmpList);
		
		return "disaster_news_home";
	}
	
	@RequestMapping(value = "/{idx}", method = RequestMethod.GET)
	public String disasterNewsWithIdx(Model model, 
			@PathVariable("idx") long idx) {
		
		Map<String, Object> article = modelNews.getNews(idx);
		
		if(article == null){
			return "error";
		}
		
		model.addAttribute("idx", article.get("idx"));
		if(article.containsKey("loc_lat")){
			model.addAttribute("lat", article.get("loc_lat"));
			model.addAttribute("lng", article.get("loc_lng"));
			model.addAttribute("loc_name", article.get("loc_name"));
		}
		model.addAttribute("type_disaster", DisasterType.code2string((Integer) article.get("type_disaster")));
		model.addAttribute("title", article.get("title"));
		String datetime = modelNews.sdf.format(((Date) article.get("datetime")));
		model.addAttribute("datetime", datetime);
		model.addAttribute("content", article.get("content"));
		
		return "disaster_news_article";
	}
	
}
