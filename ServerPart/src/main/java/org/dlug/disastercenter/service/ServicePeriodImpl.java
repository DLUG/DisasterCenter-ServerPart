package org.dlug.disastercenter.service;

import java.util.Timer;

import org.springframework.beans.factory.DisposableBean;

public abstract class ServicePeriodImpl extends ServiceImpl implements DisposableBean{
	private Timer timer;
	protected int period = 0;
	
	public static final long MSEC_A_DAY = 86400000;
	
	public ServicePeriodImpl(String serviceName, long period){
		super(serviceName);
		
		timer = new Timer();
		timer.scheduleAtFixedRate(this, 0, period);
	}
	
	@Override
	public void destroy(){
		Logger.info("Finalizing Service");

		timer.cancel();
		timer.purge();
		timer = null;
	}
}
