package org.dlug.disastercenter.service;

import java.util.Timer;

public abstract class ServicePeriodImpl extends ServiceImpl{
	private Timer timer;
	protected int period = 0;
	
	public static final long MSEC_A_DAY = 86400000;
	
	public ServicePeriodImpl(String serviceName, long period){
		super(serviceName);
		
		timer = new Timer();
		timer.scheduleAtFixedRate(this, 0, period);
	}
	
	@Override
	public void finalize() throws Throwable{
		timer.cancel();
		
		super.finalize();
	}
}
