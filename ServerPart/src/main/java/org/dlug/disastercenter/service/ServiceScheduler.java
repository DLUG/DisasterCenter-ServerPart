package org.dlug.disastercenter.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.springframework.beans.factory.DisposableBean;

public class ServiceScheduler extends TimerTask{
	private static final ServiceScheduler instance = new ServiceScheduler();
	private List<Map<String, Object>> listService;
	
	private Timer timer;

	private ServiceScheduler(){
		listService = new ArrayList<Map<String, Object>>();
		timer = new Timer();
		timer.scheduleAtFixedRate(this, 0, 60000);
	}
	
	public static ServiceScheduler getInstance(){
		return instance;
	}
	
	public void setScheduler(ServiceImpl service, int hour, int minute){
		Map<String, Object> tmpService = new HashMap<String, Object>();
		tmpService.put("hour", hour);
		tmpService.put("minute", minute);
		tmpService.put("service", service);
		
		listService.add(tmpService);
	}

	public void unsetScheduler(ServiceImpl service){
		listService.remove(service);
	}
	
	@Override
	public void run() {
		Calendar calendar = Calendar.getInstance();
		int hoursNow = calendar.get(Calendar.HOUR_OF_DAY);
		int minutesNow = calendar.get(Calendar.MINUTE);
		
		for(Map<String, Object> item: listService){
			if(hoursNow == ((Integer)item.get("hour")).intValue()
					&& minutesNow == ((Integer)item.get("minute")).intValue()){
				ServiceImpl service = (ServiceImpl) item.get("service");
				System.out.println("INFO_SCHEDULER : Start " + service.getServiceName() 
						+ " at " + hoursNow + ":" + minutesNow);
				
				new Thread(service).start();
			}
		}
	}
	
	public void destroy(){
		if(timer != null){
			try{
				System.out.println("INFO_SCHEDULER : Finalizing Scheduler");
				timer.cancel();
				timer.purge();
				timer = null;
			}catch (Exception e){
				e.printStackTrace();
			}
		}
	}
}