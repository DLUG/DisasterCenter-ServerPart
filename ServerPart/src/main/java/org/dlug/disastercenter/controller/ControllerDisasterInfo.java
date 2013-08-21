package org.dlug.disastercenter.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.dlug.disastercenter.common.ConstantDisasterType;
import org.dlug.disastercenter.model.ModelImpl;
import org.dlug.disastercenter.model.ModelInfo;
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
@RequestMapping("/disaster_info")
public class ControllerDisasterInfo extends ControllerPages{
	
	@Autowired
	private ModelInfo modelInfo;
	
	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String disasterInfo(Locale locale, Model model) {
		List<Map<String, Object>> articleList = modelInfo.getInfoList(1);
		
		if(articleList == null){
			return "error";
		}
		
		List<Map<String, Object>> tmpList = new ArrayList<Map<String, Object>>();
		
		for(Map<String, Object> article: articleList){
			Map<String, Object> tmpItem = new HashMap<String, Object>();
			
			tmpItem.put("idx", article.get("idx"));
			
			tmpItem.put("title", article.get("title"));
			tmpItem.put("type_disaster", ConstantDisasterType.code2string((Integer) article.get("type_disaster")));
			String datetime = ModelImpl.sdf.format(((Date) article.get("datetime")));
			tmpItem.put("datetime", datetime);
			
			tmpList.add(tmpItem);
		}
		
		model.addAttribute("list", tmpList);
		
		return "disaster_info_home";
	}
	
	@RequestMapping(value = "/{idx}", method = RequestMethod.GET)
	public String disasterInfoWithIdx(Model model, 
			@PathVariable("idx") long idx) {
		
		Map<String, Object> article = modelInfo.getInfo(idx);
		
		if(article == null){
			return "error";
		}
		
		model.addAttribute("idx", article.get("idx"));
		model.addAttribute("title", article.get("title"));
		model.addAttribute("type_disaster", ConstantDisasterType.code2string((Integer) article.get("type_disaster")));
		String datetime = ModelImpl.sdf.format(((Date) article.get("datetime")));
		model.addAttribute("datetime", datetime);
		model.addAttribute("content", article.get("content"));
		
		return "disaster_info_article";
	}
}
