/*
 * Copyright (c) 2015 Global Healthcare Exchange, LLC. All rights reserved.
 */

package com.ghx.auto.core.support;

import java.util.Map;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testng.Assert;

import com.ghx.auto.core.ui.support.EnvConfig;

/**
 * @author utadiparthi
 *
 */
public class DBClient {
	
	private EnvConfig envConfig;
	private String section; 
	private JdbcTemplate jdbcTemplate;
	
    public DBClient(EnvConfig envConfig, String section) {
    	this.envConfig = envConfig;
    	this.section = section;
    	createJdbcTemplate();
    }
    
    public DBClient validateDB() {
    	jdbcTemplate.execute("select username from user where id = 10");
    	return this;
    }
	
	private void createJdbcTemplate() {
		this.jdbcTemplate = new JdbcTemplate(getDataSource());
	}
	
	private BasicDataSource getDataSource() {
		BasicDataSource ds = new BasicDataSource();
		ds.setDriverClassName(getConfigParamValue("driverClassName"));
		ds.setUrl(getConfigParamValue("url"));
		ds.setUsername(getConfigParamValue("userName"));
		ds.setPassword(getConfigParamValue("password"));
		return ds;
	}
	
	private String getConfigParamValue(String param) {
    	return getConfigParamValue(this.section,param);
    }
    
    private String getConfigParamValue(String section, String param) {
    	Map<String, String> configSection = this.envConfig.get(section.toUpperCase());
    	Assert.assertNotNull(configSection, "No configuration section provided in configuration file with the name: " + section.toUpperCase());
    	String paramValue =  configSection.get(param);
    	Assert.assertNotNull(StringUtils.defaultIfBlank(paramValue, null),
                "No configuration provided for the param: " + param
                        + ",under section: " + section.toUpperCase());
    	return paramValue;
    }
}
