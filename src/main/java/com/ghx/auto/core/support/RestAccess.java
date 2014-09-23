/*
 * Copyright (c) 2013 Global Healthcare Exchange, LLC. All rights reserved.
 */

package com.ghx.auto.core.support;

import org.apache.commons.lang3.StringUtils;
import org.testng.Assert;

import com.ghx.auto.core.ui.support.EnvConfig;
import com.jayway.restassured.RestAssured;

public class RestAccess {
	
    private EnvConfig envConfig;
    private String section;
    
    public RestAccess(EnvConfig envConfig, String section) {
    	this.envConfig = envConfig;
    	this.section = section;
    }
    
    public RestAccess test_status_code_for_get(String relativePath, int code) {
    	RestAssured
    		.expect()
    			.statusCode(code)
    		.when()
    			.get(getConfigParamValue("baseUrl")+ relativePath);
    	
    	return this;
    }
    
    public RestAccess test_status_code_for_post(String relativePath, int code) {
    	RestAssured
    		.expect()
    			.statusCode(code)
    		.when()
    			.post(getConfigParamValue("baseUrl")+ relativePath);
    	
    	return this;
    }
    
    public RestAccess test_status_code_for_put(String relativePath, int code) {
    	RestAssured
    		.expect()
    			.statusCode(code)
    		.when()
    			.put(getConfigParamValue("baseUrl")+ relativePath);
    	
    	return this;
    }
    
    public RestAccess test_status_code_for_delete(String relativePath, int code) {
    	RestAssured
    		.expect()
    			.statusCode(code)
    		.when()
    			.delete(getConfigParamValue("baseUrl")+ relativePath);
    	
    	return this;
    }
    
    private String getConfigParamValue(String param) {
    	String paramValue =  this.envConfig.get(section.toUpperCase()).get(param);
    	Assert.assertNotNull(StringUtils.defaultIfBlank(paramValue, null),
                "No configuration provided for the param: " + param
                        + ",under section: " + section.toUpperCase());
    	return paramValue;
    }

}
