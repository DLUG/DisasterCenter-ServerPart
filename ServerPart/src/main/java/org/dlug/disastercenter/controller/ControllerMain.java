package org.dlug.disastercenter.controller;

import java.io.UnsupportedEncodingException;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.dlug.disastercenter.common.ConstantDisasterType;
import org.dlug.disastercenter.common.ConstantReportType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Handles requests for the application home page.
 */
@Controller
public class ControllerMain extends ControllerPages {
	private static String constantJsCache = null;
	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Locale locale, Model model) {
		return "home";
	}

	@RequestMapping(value = "constant.js", method = {RequestMethod.GET, RequestMethod.POST}, produces = "text/javascript; charset=utf-8")
	public @ResponseBody String constant_js(){
		if(constantJsCache == null){
			constantJsCache = "";
			constantJsCache += "var CONSTANT_DISASTER_TYPE = new Array();\n";
			
			Map<Integer, String> typeMap = ConstantDisasterType.getTypeMap();
			
			for(Integer typeCode: typeMap.keySet()){
				constantJsCache += "CONSTANT_DISASTER_TYPE[" + typeCode + "] = \"" + typeMap.get(typeCode) + "\";\n"; 
			}
			
			constantJsCache += "\n";
			
			constantJsCache += "var CONSTANT_REPORT_TYPE = new Array();\n";
			
			typeMap = ConstantReportType.getTypeMap();
			
			for(Integer typeCode: typeMap.keySet()){
				constantJsCache += "CONSTANT_REPORT_TYPE[" + typeCode + "] = \"" + typeMap.get(typeCode) + "\";\n"; 
			}
		}
		
		return constantJsCache;
	}	
}
