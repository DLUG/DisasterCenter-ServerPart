package org.dlug.disastercenter.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.dlug.disastercenter.common.CoordinateTools;
import org.dlug.disastercenter.common.DisasterType;
import org.dlug.disastercenter.model.ModelApps;
import org.dlug.disastercenter.model.ModelReport;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.ibatis.SqlMapClientTemplate;

import com.google.android.gcm.server.Constants;
import com.google.android.gcm.server.Message.Builder;
import com.google.android.gcm.server.MulticastResult;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;

public final class ServicePushMessage extends ServiceImpl{
	private static final ServicePushMessage instance = new ServicePushMessage();
	
	private static final int MULTICAST_SIZE = 1000;
	private String gcm_simple_key = "";
	private static Sender sender;

	private static final Executor threadPool = Executors.newFixedThreadPool(5);
	
	private static ModelReport modelReport = new ModelReport();
	private static ModelApps modelApps = new ModelApps();
	
	
	public static ServicePushMessage getInstance(){
		return instance;
	}
	
	private ServicePushMessage(){
		super("PushMessageService");

		Properties gcmProperty = new Properties();
		try {
			gcmProperty.load(this.getClass().getClassLoader().getResourceAsStream("gcm.property"));
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		gcm_simple_key = gcmProperty.getProperty("gcm.simple_api_key");
		sender = new Sender(gcm_simple_key);
	}

	public void pushReport(double lat, double lng, int type_disaster, Date reportDate){
		SqlMapClientTemplate sqlMapClientTemplate = getSqlMapClientTemplate();
		modelReport.setSqlMapClientTemplate(sqlMapClientTemplate);
		modelApps.setSqlMapClientTemplate(sqlMapClientTemplate);
		List<Map<String, Object>> reportList = modelReport.getReportList(lat, lng, 1, type_disaster, reportDate);
		
		if(reportList.size() != 0){
			
			List<Map<String, Object>> targetAppList = modelApps.getAppList(lat, lng, 50);

			List<String> appGcmIdList = new ArrayList<String>();
			List<Long> appIdxList = new ArrayList<Long>();
			
			for(Map<String, Object> targetApp : targetAppList){
				double tmpDistance = CoordinateTools.calcDistance(lat, lng, (Double)targetApp.get("loc_lat"), (Double)targetApp.get("loc_lng"));
				
				if(tmpDistance < (double) ((Integer) targetApp.get("setting_range"))){
					appGcmIdList.add((String) targetApp.get("gcm_id"));
					appIdxList.add((Long) targetApp.get("idx"));
				}
			}
			
			if(appGcmIdList.size() > 0){
				Map<String, Object> report = reportList.get(0);
				
				Map<String, String> gcmMessage = new HashMap<String, String>();
				gcmMessage.put("report_idx", String.valueOf((Long)report.get("idx")));
				gcmMessage.put("type_disaster", String.valueOf((Integer)report.get("type_disaster")));
				
				//Deprecated
				gcmMessage.put("type_disaster_string", DisasterType.code2string((Integer)report.get("type_disaster")));
				
				gcmMessage.put("content", (String) report.get("content"));
				
				Date datetime = (Date) report.get("datetime");
				
				gcmMessage.put("timestamp", String.valueOf(datetime.getTime()));
				
				//String result = ServiceGcm.getInstance().sendReport(appIdxList, appGcmIdList, gcmMessage);
				sendMessage(appIdxList, appGcmIdList, gcmMessage);
			}
		}
	}
	
	public String sendMessage(List<Long> listAppIdx, List<String> listGcmId, Map<String, String> messages){
		Builder messageBuilder = new Message.Builder()
//		.collapseKey("")
		.timeToLive(1800)
 		.delayWhileIdle(false)
 		.dryRun(true)
 		.restrictedPackageName("org.dlug.disastercenter");

		
		for(String key: messages.keySet()){
			messageBuilder.addData(key, messages.get(key));
		}
		Message message = messageBuilder.build();
		
		// NOTE: check below is for demonstration purposes; a real application
		// could always send a multicast, even for just one recipient
		if (listGcmId.size() == 1) {
			// send a single message using plain post
			String registrationId = listGcmId.get(0);
			Long appIdx = listAppIdx.get(0);
//			Message message = new Message.Builder().build();

			Result result = null;
			try {
				result = sender.send(message, registrationId, 5);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			if(result.getMessageId() != null){
				putSuccessMessage(appIdx, Long.valueOf(messages.get("report_idx")));
			}
			
			Logger.info("Sent message to one device: " + result);
			return "Sent message to one device: " + result;
		} else {
			// send a multicast message using JSON
			// must split in chunks of 1000 devices (GCM limit)
			int total = listGcmId.size();
			List<String> partialDevices = new ArrayList<String>(total);
			int counter = 0;
//			int tasks = 0;
			for (String device : listGcmId) {
				counter++;
				partialDevices.add(device);
				int partialSize = partialDevices.size();
				if (partialSize == MULTICAST_SIZE || counter == total) {
					asyncSend(listAppIdx, partialDevices, message);
					partialDevices.clear();
//					tasks++;
				}
			}
		}
		
		return "";
	}

	private void asyncSend(List<Long> listApp, List<String> listDevice, final Message message) {
		// make a copy
		final List<String> devices = new ArrayList<String>(listDevice);
		final List<Long> apps = new ArrayList<Long>(listApp);
		
		threadPool.execute(new Runnable() {

			public void run() {
				MulticastResult multicastResult;
				try {
					multicastResult = sender.send(message, devices, 5);
				} catch (IOException e) {
					Logger.error("Error posting messages: " + e);
					return;
				}
				List<Result> results = multicastResult.getResults();
				// analyze the results
				for (int i = 0; i < devices.size(); i++) {
					String regId = devices.get(i);
					Result result = results.get(i);
					String messageId = result.getMessageId();
					if (messageId != null) {
						
						
						
						Logger.info("Succesfully sent message to device: " + regId +
								"; messageId = " + messageId);
						
						putSuccessMessage(apps.get(i), Long.valueOf(message.getData().get("report_idx")));
						
						String canonicalRegId = result.getCanonicalRegistrationId();
						if (canonicalRegId != null) {
							// same device has more than on registration id: update it
							Logger.info("canonicalRegId " + canonicalRegId);
//							Datastore.updateRegistration(regId, canonicalRegId);
							
							updateGcmId(regId, canonicalRegId);
						}
					} else {
						String error = result.getErrorCodeName();
						if (error.equals(Constants.ERROR_NOT_REGISTERED)) {
							// application has been removed from device - unregister it
							Logger.info("Unregistered device: " + regId);
//							Datastore.unregister(regId);
							removeGcmId(regId);
						} else {
							Logger.error("Error sending message to " + regId + ": " + error);
						}
					}
				}
			}});
	}

	private void putSuccessMessage(long appIdx, long reportIdx){
		SqlMapClientTemplate sqlMapClientTemplate = getSqlMapClientTemplate();
		
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("app_idx", appIdx);
		parameters.put("report_idx", reportIdx);
		
		sqlMapClientTemplate.insert("messages.put_message", parameters);
	}
	
	private boolean updateGcmId(String oldGcmId, String newGcmId){
		SqlMapClientTemplate sqlMapClientTemplate = getSqlMapClientTemplate();
		
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("old_gcmid", oldGcmId);
		parameters.put("new_gcmid", newGcmId);
		
		try{
			sqlMapClientTemplate.update("apps.update_gcmid_with_gcmid", parameters);
		} catch (DataAccessException e){
			e.printStackTrace();
			return false;
		}
		
		return true;
	}

	private boolean removeGcmId(String gcmId){
		SqlMapClientTemplate sqlMapClientTemplate = getSqlMapClientTemplate();
		
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("gcm_id", gcmId);
		
		try{
			sqlMapClientTemplate.update("apps.delete_app_with_gcmid", parameters);
		} catch (DataAccessException e){
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	@Override
	protected void process(SqlMapClientTemplate sqlMapClientTemplate) {}
}
