package org.dlug.disastercenter.service;

import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Enumeration;

import org.springframework.beans.factory.DisposableBean;

import com.mysql.jdbc.AbandonedConnectionCleanupThread;

public class ServiceDbDestroyer  implements DisposableBean{
	public ServiceDbDestroyer(){
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		System.out.println("INFO_SERVICE : ServiceDbDestroyer - Standby");
	}
	
	@Override
	public void destroy() throws Exception {
		System.out.println("INFO_SERVICE : ServiceDbDestroyer - Start");
		
	    try {
	        Enumeration<Driver> drivers = DriverManager.getDrivers();
	        while(drivers.hasMoreElements()) {
	            DriverManager.deregisterDriver(drivers.nextElement());
	        }
	        
            AbandonedConnectionCleanupThread.shutdown();
	    } catch(Exception e) {
	    	e.printStackTrace();
	    }
	    System.out.println("INFO_SERVICE : ServiceDbDestroyer - Finish");
	}
}
