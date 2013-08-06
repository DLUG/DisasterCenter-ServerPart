package org.dlug.disastercenter.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.dlug.disastercenter.common.XMLDocument;
import org.dlug.disastercenter.common.XMLElement;
import org.springframework.orm.ibatis.SqlMapClientTemplate;

public class ServiceNewsCrawler extends ServicePeriodImpl{
	public final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public final static SimpleDateFormat dateFormatterRssPubDate = 
			new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
	
	public ServiceNewsCrawler(){
		super("ServiceNewsCrawler", 300000);
	}
	
	@Override
	protected void process(SqlMapClientTemplate sqlMapClientTemplate) {
		
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> rssList = sqlMapClientTemplate.queryForList("service_news.get_rss_list");
		
		for(Map<String, Object> item: rssList){
			String requestUrl = (String) item.get("rss_url");
			Logger.info("Checking RSS: " + requestUrl);
			try {
				URL url;
				url = new URL(requestUrl);
				
				BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
				
				String result = "";
				String inputLine;
				
				while ((inputLine = reader.readLine()) != null){
					result += inputLine;
				}

				XMLDocument xmlParser = new XMLDocument(result);
				XMLElement xml = xmlParser.parse();
				
				XMLElement xmlChannel = xml.getChild(0);
				
				List<XMLElement> articleList = xmlChannel.getChild("item");
				
				for(XMLElement article: articleList){
					String pubDateString = article.getChild("pubDate").get(0).getValue();
					Date pubDate = null;
					Date lastCrawledTime = null;
					
					try {
						pubDate = dateFormatterRssPubDate.parse(pubDateString);
//						String lastUpdateTimeString = (String)item.get("update_datetime");
						if(item.get("update_datetime") != null)
							lastCrawledTime = new Date(((Timestamp)item.get("update_datetime")).getTime());
					} catch (java.text.ParseException e) {
						e.printStackTrace();
					}

					if(lastCrawledTime != null)
						if(pubDate.compareTo(lastCrawledTime) < 0){
							break;
						}
					
					String title = article.getChild("title").get(0).getValue();
					int type_disaster = 0;
					
					if(title.contains("장마")
							|| title.contains("호우주의보")
							|| title.contains("호우특보")
							|| title.contains("호우경보")
							|| title.contains("폭우")
							|| title.contains("집중호우")
							|| title.contains("장맛비")){
						type_disaster = 101;
					} else if(title.contains("산사태")){
						type_disaster = 403;
					} else if(title.contains("침수")
							|| title.contains("물바다")
							|| title.contains("홍수")){
						type_disaster = 102;
					} else if(title.contains("폭염")
							|| title.contains("불볕더위")
							|| title.contains("폭서")
							|| title.contains("무더위")){
						type_disaster = 301;
					} else if(title.contains("열대야")){
						type_disaster = 302;
					} else if(title.contains("산불")){
						type_disaster = 502;
					} else if(title.contains("화재")){
						type_disaster = 501;
					} else if(title.contains("태풍")
							|| title.contains("허리케인")){
						type_disaster = 104;
					} else if(title.contains("지진")){
						type_disaster = 404;
					} else if(title.contains("전쟁")){
						type_disaster = 801;
					} 
					
					if(type_disaster != 0){
						Logger.info("Crawled Article: " + title);
						String content = article.getChild("description").get(0).getValue()
								+ "<br>\n<br>\n<a href=\"" + article.getChild("link").get(0).getValue() + "\">원문보기</a>";
						
						Map<String, Object> tmpArticle = new HashMap<String, Object>();
						
						tmpArticle.put("type_disaster", type_disaster);
						title = URLEncoder.encode(title, "UTF-8");
						content = URLEncoder.encode(content, "UTF-8");
						tmpArticle.put("title", title);
						tmpArticle.put("content", content);
						tmpArticle.put("datetime", sdf.format(pubDate));
						
						sqlMapClientTemplate.insert("news.put_news_without_loc", tmpArticle);
					}
					
					Map<String, Object> parameters = new HashMap<String, Object>();
					String rssPubDateString = xmlChannel.getChild("pubDate").get(0).getValue();
					
					Date rssPubDate = null;
					
					try {
						rssPubDate = dateFormatterRssPubDate.parse(rssPubDateString);
					} catch (java.text.ParseException e) {
						e.printStackTrace();
					}
					
					parameters.put("update_datetime", sdf.format(rssPubDate));
					parameters.put("idx", item.get("idx"));
					
					sqlMapClientTemplate.update("service_news.update_datetime", parameters);
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}			
		}
	}
}
