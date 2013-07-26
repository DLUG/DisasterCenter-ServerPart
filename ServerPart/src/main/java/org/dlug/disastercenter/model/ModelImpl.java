package org.dlug.disastercenter.model;

import java.text.SimpleDateFormat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ibatis.SqlMapClientTemplate;

public abstract class ModelImpl {
	@Autowired
	protected SqlMapClientTemplate sqlMapClientTemplate;
	
	public final static int PAGE_AMOUNT = 10;
	public final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public void setSqlMapClientTemplate(SqlMapClientTemplate sqlMapClientTemplate){
		this.sqlMapClientTemplate = sqlMapClientTemplate;
	}
}
