package org.dlug.disastercenter.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Level;

import org.springframework.dao.DataAccessException;
import org.springframework.orm.ibatis.SqlMapClientTemplate;

import com.google.android.gcm.server.Constants;
import com.google.android.gcm.server.Message.Builder;
import com.google.android.gcm.server.MulticastResult;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;

public final class ServiceGcm extends ServiceImpl{
	private static final int MULTICAST_SIZE = 1000;
	private String gcm_simple_key = "";
	private Sender sender;

	private static final Executor threadPool = Executors.newFixedThreadPool(5);
	
	private ServiceGcm(){
		super("GcmService");

		Properties gcmProperty = new Properties();
		try {
			gcmProperty.load(this.getClass().getClassLoader().getResourceAsStream("jdbc.property"));
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		gcm_simple_key = gcmProperty.getProperty("gcm.simple_api_key");

		sender = new Sender(gcm_simple_key);
	}

	public void sendReport(List<String> listGcmId, Map<String, String> messages){
		Builder messageBuilder = new Message.Builder()
		.collapseKey("")
		.timeToLive(1800)
 		.delayWhileIdle(true)
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
//			Message message = new Message.Builder().build();

			Result result = null;
			try {
				result = sender.send(message, registrationId, 5);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Logger.info("Sent message to one device: " + result);
		} else {
			// send a multicast message using JSON
			// must split in chunks of 1000 devices (GCM limit)
			int total = listGcmId.size();
			List<String> partialDevices = new ArrayList<String>(total);
			int counter = 0;
			int tasks = 0;
			for (String device : listGcmId) {
				counter++;
				partialDevices.add(device);
				int partialSize = partialDevices.size();
				if (partialSize == MULTICAST_SIZE || counter == total) {
					asyncSend(partialDevices, message);
					partialDevices.clear();
					tasks++;
				}
			}
		}
	}

	private void asyncSend(List<String> partialDevices, final Message message) {
		// make a copy
		final List<String> devices = new ArrayList<String>(partialDevices);
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

	public boolean updateGcmId(String oldGcmId, String newGcmId){
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

	public boolean removeGcmId(String gcmId){
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
