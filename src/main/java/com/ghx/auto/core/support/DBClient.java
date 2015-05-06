/*
 * Copyright (c) 2015 Global Healthcare Exchange, LLC. All rights reserved.
 */

package com.ghx.auto.core.support;

import java.util.List;
import java.util.Map;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataAccessException;
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
    
    public DBClient verify_row_count(String sql, int rowCountExpected) {
    	List<Map<String, Object>> results = null;
    	try {
    		results = jdbcTemplate.queryForList(sql);
    	}
    	catch (DataAccessException dae) {
    		Assert.fail("Unable to execute query, sql is: " + sql);
    	}
    	
    	Assert.assertTrue(results.size() == rowCountExpected, "Row count differs from expected, " +  
    						"Expected: " + rowCountExpected + " Actual: " + results.size() + ", sql is: " + sql);
    	return this;
    }
    
    public DBClient verify_row_count_greater_than_zero(String sql) {
    	List<Map<String, Object>> results = null;
    	try {
    		results = jdbcTemplate.queryForList(sql);
    	}
    	catch (DataAccessException dae) {
    		Assert.fail("Unable to execute query, sql is: " + sql);
    	}
    	
    	Assert.assertTrue(results.size() > 0, "Row count is not greater than 0, sql is: " + sql);
    	return this;
    }
    
    public DBClient insert_rows(String sql) {
    	int result = 0;
    	try {
    		result = jdbcTemplate.update(sql);
    	}
    	catch (DataAccessException dae) {
    		Assert.fail("Unable to execute query, sql is: " + sql);
    	}
    	Assert.assertTrue(result > 0, "Unable to insert row(s), sql is: " + sql);

    	return this;
    }
    
    public DBClient update_rows(String sql) {
    	try {
    		jdbcTemplate.update(sql);
    	}
    	catch (DataAccessException dae) {
    		Assert.fail("Unable to update row(s), sql is: " + sql);
    	}
    	
    	return this;
    }
    
    public DBClient delete_rows(String sql) {
    	try {
    		jdbcTemplate.update(sql);
    	}
    	catch (DataAccessException dae) {
    		Assert.fail("Unable to delete row(s), sql is: " + sql);
    	}
    	
    	return this;
    }
    
    public DBClient execute_query(String sql) {
    	try {
    		jdbcTemplate.execute(sql);
    	}
    	catch (DataAccessException dae) {
    		Assert.fail("Unable to execute query, error message is: " + dae.getMessage() + " sql is: " + sql);
    	}
    	
    	return this;
    }
    
    public Map<String, Object> get_single_row(String sql) {
    	try {
    		return jdbcTemplate.queryForMap(sql);
    	}
    	catch (DataAccessException dae) {
    		Assert.fail("Unable to execute query, sql is: " + sql);
    	}
    	
    	return null; //should not happen
    }
    
    public List<Map<String, Object>> get_multiple_rows(String sql) {
    	try {
    		return jdbcTemplate.queryForList(sql);
    	}
    	catch (DataAccessException dae) {
    		Assert.fail("Unable to execute query, sql is: " + sql);
    	}
    	
    	return null; //should not happen
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
