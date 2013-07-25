package org.dlug.disastercenter.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dlug.disastercenter.service.CoordinateConverter.CoordKma;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ApiKMA {
	private final static String dfsUrl = "http://www.kma.go.kr/wid/queryDFS.jsp";
	private final static String odamUrl = "http://www.kma.go.kr/wid/queryODAM.jsp";
//	private final static String apiKey = "DAUM_SENSORQL_DEMO_APIKEY";
	
	private static CoordinateConverter converter;
	
	public ApiKMA(){
		if(converter == null){
			converter = new CoordinateConverter();
		}
	}
	
	public Map<String, Object>getCurrentWeather(double lat, double lng){
		CoordKma kmaCoord = converter.latlng2Kma(lat, lng); 
		
		String query = odamUrl;
		
		try {
			query = URLEncoder.encode(query, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		String requestUrl = query + "?gridx=" + kmaCoord.x + "&gridy=" + kmaCoord.y;
		
		try {
			URL url;
			url = new URL(requestUrl);
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
			
			String xmlSource = "";
			String inputLine;
			
			while ((inputLine = reader.readLine()) != null){
				xmlSource += inputLine;
			}

			XMLDocument xmlParser = new XMLDocument(xmlSource);
			XMLElement xml = xmlParser.parse();
			
			XMLElement xmlBody = xml.getChild("body").get(0);
			XMLElement xmlData = xmlBody.getChild("data").get(0);
			
			Map<String, Object> result = new HashMap<String, Object>();
			
			result.put("temp", Double.parseDouble(xmlData.getChild("temp").get(0).getValue()));
			result.put("humidity", Integer.parseInt(xmlData.getChild("reh").get(0).getValue()));
			result.put("wind_speed", Double.parseDouble(xmlData.getChild("ws").get(0).getValue()));
			result.put("rain1_meter", Double.parseDouble(xmlData.getChild("rn1").get(0).getValue()));
			
			return result;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public Map<String, Object>getTodayWeather(int kma_x, int kma_y){
		//Coord kmaCoord = converter.latlng2Kma(lat, lng); 
		
		String query = dfsUrl;
		
		try {
			query = URLEncoder.encode(query, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		String requestUrl = query + "?gridx=" + kma_x + "&gridy=" + kma_y;
		
		try {
			URL url;
			url = new URL(requestUrl);
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
			
			String xmlSource = "";
			String inputLine;
			
			while ((inputLine = reader.readLine()) != null){
				xmlSource += inputLine;
			}

			XMLDocument xmlParser = new XMLDocument(xmlSource);
			XMLElement xml = xmlParser.parse();
			
			XMLElement xmlBody = xml.getChild("body").get(0);
			List<XMLElement> xmlDatas = xmlBody.getChild("data");
			
			Double todayMaxTemp = Double.parseDouble(xmlDatas.get(0).getChild("temp").get(0).getValue());
			Double todayMinTemp = todayMaxTemp;
			
			int todayWatchHighTempStart = -1;
			int todayWatchHighTempEnd = -1;
			int todayWatchLowTempStart = -1;
			int todayWatchLowTempEnd = -1;
			
			int todayAlertHighTempStart = -1;
			int todayAlertHighTempEnd = -1;
			int todayAlertLowTempStart = -1;
			int todayAlertLowTempEnd = -1;
			
			Double todayMaxRain6meter = 0.0;
			int todayWatchHardRainStart = -1;
			int todayAlertHardRainStart = -1;
			
			int todayMaxWindSpeed = 0;
			int todayWatchFastWindStart = -1;
			int todayAlertFastWindStart = -1;
			
			int todayMaxHumidity = Integer.parseInt(xmlDatas.get(0).getChild("reh").get(0).getValue());
			
			for(int i = 1; i < 8; i++){
				int tmpHour = Integer.parseInt(xmlDatas.get(i).getChild("hour").get(0).getValue());
				
				Double tmpTemp = Double.parseDouble(xmlDatas.get(i).getChild("temp").get(0).getValue());
				int tmpHumidity = Integer.parseInt(xmlDatas.get(i).getChild("reh").get(0).getValue());
				Double tmpRain6Meter = Double.parseDouble(xmlDatas.get(i).getChild("r06").get(0).getValue());
				int tmpWindSpeed = Integer.parseInt(xmlDatas.get(i).getChild("ws").get(0).getValue());
				
				if(tmpTemp > todayMaxTemp)
					todayMaxTemp = tmpTemp;

				if(tmpTemp < todayMinTemp)
					todayMinTemp = tmpTemp;

				if(tmpRain6Meter > todayMaxRain6meter)
					todayMaxRain6meter = tmpRain6Meter;
				
				if(tmpWindSpeed > todayMaxWindSpeed)
					todayMaxWindSpeed = tmpWindSpeed;

				if(tmpHumidity > todayMaxHumidity)
					todayMaxHumidity = tmpHumidity;
				
				if(tmpTemp > WeatherAlertLimit.TEMP_HIGH_ALERT && todayAlertHighTempStart == -1){
					todayAlertHighTempStart = tmpHour;
				}
				
				if(todayAlertHighTempStart != -1 && todayAlertHighTempEnd == -1){
					if(tmpTemp < WeatherAlertLimit.TEMP_HIGH_ALERT)
						todayAlertHighTempEnd = tmpHour;
				}
				
				if(tmpTemp > WeatherAlertLimit.TEMP_HIGH_WATCH && todayWatchHighTempStart  == -1){
					todayWatchHighTempStart = tmpHour;
				}
				
				if(todayWatchHighTempStart != -1 && todayWatchHighTempEnd == -1){
					if(tmpTemp < WeatherAlertLimit.TEMP_HIGH_WATCH)
						todayWatchHighTempEnd = tmpHour;
				}
				
				if(tmpTemp < WeatherAlertLimit.TEMP_LOW_ALERT && todayAlertLowTempStart == -1){
					todayAlertLowTempStart = tmpHour;
				}
				
				if(todayAlertLowTempStart != -1 && todayAlertLowTempEnd == -1){
					if(tmpTemp < WeatherAlertLimit.TEMP_LOW_ALERT)
						todayAlertLowTempEnd = tmpHour;
				}
				
				if(tmpTemp < WeatherAlertLimit.TEMP_LOW_WATCH && todayWatchLowTempStart  == -1){
					todayWatchLowTempStart = tmpHour;
				}
				
				if(todayWatchLowTempStart != -1 && todayWatchLowTempEnd == -1){
					if(tmpTemp < WeatherAlertLimit.TEMP_LOW_WATCH)
						todayWatchLowTempEnd = tmpHour;
				}
				
				if(tmpRain6Meter > WeatherAlertLimit.RAIN6_HARD_ALERT && todayAlertHardRainStart  == -1){
					todayAlertHardRainStart = tmpHour;
				}
				
				if(tmpRain6Meter > WeatherAlertLimit.RAIN6_HARD_WATCH && todayWatchHardRainStart  == -1){
					todayWatchHardRainStart = tmpHour;
				}
				
				if(tmpWindSpeed > WeatherAlertLimit.WIND_FAST_ALERT && todayAlertFastWindStart  == -1){
					todayAlertFastWindStart = tmpHour;
				}
				
				if(tmpWindSpeed > WeatherAlertLimit.WIND_FAST_WATCH && todayWatchFastWindStart  == -1){
					todayWatchFastWindStart = tmpHour;
				}
			}
			
			Map<String, Object> result = new HashMap<String, Object>();
			
			result.put("temp_max", todayMaxTemp);
			if(todayAlertHighTempStart != -1){
				result.put("alert_high_temp_start", todayAlertHighTempStart);
				result.put("alert_high_temp_end", todayAlertHighTempEnd);
			}
			if(todayWatchHighTempStart != -1){
				result.put("watch_high_temp_start", todayWatchHighTempStart);
				result.put("watch_high_temp_end", todayWatchHighTempEnd);
			}
			
			result.put("temp_min", todayMinTemp);
			if(todayAlertLowTempStart != -1){
				result.put("alert_low_temp_start", todayAlertLowTempStart);
				result.put("alert_low_temp_end", todayAlertLowTempEnd);
			}
			if(todayWatchLowTempStart != -1){
				result.put("watch_low_temp_start", todayWatchLowTempStart);
				result.put("watch_low_temp_end", todayWatchLowTempEnd);
			}
			
			result.put("rain6_max", todayMaxRain6meter);
			if(todayAlertHardRainStart != -1)
				result.put("alert_hard_rain_start", todayAlertHardRainStart);
			if(todayWatchHardRainStart != -1)
				result.put("watch_hard_rain_start", todayWatchHardRainStart);
			
			result.put("wind_max", todayMaxWindSpeed);
			if(todayAlertFastWindStart != -1)
				result.put("alert_fast_wind_start", todayAlertFastWindStart);
			if(todayWatchFastWindStart != -1)
				result.put("watch_fast_wind_start", todayWatchFastWindStart);
			
			result.put("humidity_max", todayMaxHumidity);
			
			return result;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
}