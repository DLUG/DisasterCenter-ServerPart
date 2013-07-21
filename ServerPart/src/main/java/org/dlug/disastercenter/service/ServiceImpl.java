package org.dlug.disastercenter.service;

import java.io.IOException;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.ibatis.SqlMapClientTemplate;

import com.ibatis.common.resources.Resources;
import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapClientBuilder;

public abstract class ServiceImpl extends TimerTask implements Runnable{
	private String serviceName = "";
	
	protected ServiceLogger Logger;
	
	public ServiceImpl(String serviceName){
		this.serviceName = serviceName;
		
		Logger = new ServiceLogger();
	}
	
	@Override
	public void run() {
		Logger.info("Start");
		
		Properties jdbcProperty = new Properties();
		try {
			jdbcProperty.load(this.getClass().getClassLoader().getResourceAsStream("jdbc.property"));
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		
		dataSource.setDriverClassName("com.mysql.jdbc.Driver");
		dataSource.setUrl(jdbcProperty.getProperty("jdbc.url"));
		dataSource.setUsername(jdbcProperty.getProperty("jdbc.username"));
		dataSource.setPassword(jdbcProperty.getProperty("jdbc.password"));

		SqlMapClient sqlMapClient = null;
		try {
			sqlMapClient = SqlMapClientBuilder.buildSqlMapClient (Resources.getResourceAsReader(this.getClass().getClassLoader(), "ibatis/SqlMapConfig.xml"));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		SqlMapClientTemplate sqlMapClientTemplate = new SqlMapClientTemplate();
		
		sqlMapClientTemplate.setDataSource(dataSource);
		sqlMapClientTemplate.setSqlMapClient(sqlMapClient);
		
		process(sqlMapClientTemplate);
		Logger.info("Finish");	
	}
	
	protected abstract void process(SqlMapClientTemplate sqlMapClientTemplate);
	
	public String getServiceName(){
		return serviceName;
	}
	
	class ServiceLogger{
		public void info(String message){
			System.out.println("INFO_SERVICE : " + serviceName + " - " + message);
		}
		
		public void debug(String message){
			System.out.println("DEBUG_SERVICE : " + serviceName + " - " + message);
		}
	}
}
