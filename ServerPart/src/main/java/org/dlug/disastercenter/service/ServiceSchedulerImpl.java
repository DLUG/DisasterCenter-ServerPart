package org.dlug.disastercenter.service;

import org.springframework.beans.factory.DisposableBean;

public abstract class ServiceSchedulerImpl extends ServiceImpl implements DisposableBean{
	public ServiceSchedulerImpl(String serviceName) {
		super(serviceName);
	}

	protected void setScheduler(ServiceImpl service, int hour, int minute){
		ServiceScheduler.getInstance().setScheduler(service, hour, minute);
	}
	
	@Override
	public void destroy(){
		ServiceScheduler.getInstance().destroy();
	}
}
