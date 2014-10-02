/*
 * Copyright (c) 2013 Global Healthcare Exchange, LLC. All rights reserved.
 */

package com.ghx.auto.core.support;

import static org.hamcrest.core.StringContains.containsString;
import static org.hamcrest.xml.HasXPath.hasXPath;

import org.apache.commons.lang3.StringUtils;
import org.testng.Assert;

import com.ghx.auto.core.ui.support.EnvConfig;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;


public class RestAccess {
	
    private EnvConfig envConfig;
    private String section;
    
    public RestAccess(EnvConfig envConfig, String section) {
    	this.envConfig = envConfig;
    	this.section = section;
    }
    
    public RestAccess get_verify_status(String relativePath, int statusCode) {
    	RestAssured
    		//.given().auth().certificate(getConfigParamValue("keystorePath"), getConfigParamValue("keystorePassword"))
    		.given()
    			.auth().certificate(getConfigParamValue("cacertsPath"), getConfigParamValue("cacertsPassword"))
    		.expect()
    			.statusCode(statusCode)
    		.when()
    			.get(getConfigParamValue("baseUrl")+ relativePath);
    			
    	return this;
    }
    
    public RestAccess get_verify_status_content_xml(String relativePath, int code, String xpath, String value ) {
    	RestAssured
    		//.given().auth().certificate(getConfigParamValue("keystorePath"), getConfigParamValue("keystorePassword"))
    		.given()
    			.auth().certificate(getConfigParamValue("cacertsPath"), getConfigParamValue("cacertsPassword"))
    		.expect()
    			.statusCode(code)
    			.body(hasXPath(xpath), containsString(value))
    		.when()
    			.get(getConfigParamValue("baseUrl")+ relativePath);
    			
    	return this;
    }
    
    public RestAccess get_verify_status_content_json(String relativePath, int code, String jsonPath, String value ) {
    	RestAssured
    		//.given().auth().certificate(getConfigParamValue("keystorePath"), getConfigParamValue("keystorePassword"))
    		.given()
    			.auth().certificate(getConfigParamValue("cacertsPath"), getConfigParamValue("cacertsPassword"))
    		.expect()
    			.statusCode(code)
    			.body(jsonPath, containsString(value))
    		.when()
    			.get(getConfigParamValue("baseUrl")+ relativePath);
    			
    	return this;
    }
    
    public RestAccess get_verify_content_xml(String relativePath,String xpath, String value ) {
    	RestAssured
    		//.given().auth().certificate(getConfigParamValue("keystorePath"), getConfigParamValue("keystorePassword"))
    		.given()
    			.auth().certificate(getConfigParamValue("cacertsPath"), getConfigParamValue("cacertsPassword"))
    		.when()
    			.get(getConfigParamValue("baseUrl")+ relativePath)
    		.then()
    			.assertThat()
    			.body(hasXPath(xpath), containsString(value));
    			
    	return this;
    }
    
    public RestAccess get_verify_content_json(String relativePath,String jsonPath, String value ) {
    	RestAssured
    		//.given().auth().certificate(getConfigParamValue("keystorePath"), getConfigParamValue("keystorePassword"))
    		.given()
    			.auth().certificate(getConfigParamValue("cacertsPath"), getConfigParamValue("cacertsPassword"))
    		.when()
    			.get(getConfigParamValue("baseUrl")+ relativePath)
    		.then()
    			.assertThat()
    			.body(jsonPath, containsString(value));
    			
    	return this;
    }
    
    public RestAccess get_verify_status_content(String relativePath, RequestSpecification requestSpec,ResponseSpecification responseSpec) {
    	RestAssured
    		//.given().auth().certificate(getConfigParamValue("keystorePath"), getConfigParamValue("keystorePassword"))
    		.given()
    			.auth().certificate(getConfigParamValue("cacertsPath"), getConfigParamValue("cacertsPassword"))
    			.spec(requestSpec)
    		.when()
    			.get(getConfigParamValue("baseUrl")+ relativePath)
    		.then()
    			.spec(responseSpec);
    			
    	return this;
    }
    
    public RestAccess post_xml_verify_status(String relativePath, int statusCode) {
    	RestAssured
    		//.given().auth().certificate(getConfigParamValue("keystorePath"), getConfigParamValue("keystorePassword"))
    		.given()
    			.auth().certificate(getConfigParamValue("cacertsPath"), getConfigParamValue("cacertsPassword"))
    		.expect()
    			.statusCode(statusCode)
    		.when()
    			.get(getConfigParamValue("baseUrl")+ relativePath);
    			
    	return this;
    }
    
    public RestAccess post_xml_verify_content(String relativePath,String xpath, String value ) {
    	RestAssured
    		//.given().auth().certificate(getConfigParamValue("keystorePath"), getConfigParamValue("keystorePassword"))
    		.given()
    			.auth().certificate(getConfigParamValue("cacertsPath"), getConfigParamValue("cacertsPassword"))
    		.when()
    			.get(getConfigParamValue("baseUrl")+ relativePath)
    		.then()
    			.assertThat()
    			.body(hasXPath(xpath), containsString(value));
    			
    	return this;
    }
        
    public RestAccess post_xml_verify_status_content(String relativePath, int statusCode, String xpath, String value ) {
    	RestAssured
    		//.given().auth().certificate(getConfigParamValue("keystorePath"), getConfigParamValue("keystorePassword"))
    		.given()
    			.auth().certificate(getConfigParamValue("cacertsPath"), getConfigParamValue("cacertsPassword"))
    		.expect()
    			.statusCode(statusCode)
    			.body(hasXPath(xpath), containsString(value))
    		.when()
    			.get(getConfigParamValue("baseUrl")+ relativePath);
    			
    	return this;
    }
    
    public RestAccess post_xml_verify_status_content(String relativePath, RequestSpecification requestSpec,ResponseSpecification responseSpec) {
    	RestAssured
    		//.given().auth().certificate(getConfigParamValue("keystorePath"), getConfigParamValue("keystorePassword"))
    		.given()
    			.auth().certificate(getConfigParamValue("cacertsPath"), getConfigParamValue("cacertsPassword"))
    			.spec(requestSpec)
    		.when()
    			.get(getConfigParamValue("baseUrl")+ relativePath)
    		.then()
    			.spec(responseSpec);
    			
    	return this;
    }
    
    public RestAccess put_xml_verify_status_content(String relativePath, RequestSpecification requestSpec,ResponseSpecification responseSpec) {
    	RestAssured
    		//.given().auth().certificate(getConfigParamValue("keystorePath"), getConfigParamValue("keystorePassword"))
    		.given()
    			.auth().certificate(getConfigParamValue("cacertsPath"), getConfigParamValue("cacertsPassword"))
    			.spec(requestSpec)
    		.when()
    			.get(getConfigParamValue("baseUrl")+ relativePath)
    		.then()
    			.spec(responseSpec);
    			
    	return this;
    }
    
    public RestAccess delete_verify_status(String relativePath, int statusCode) {
    	RestAssured
    		//.given().auth().certificate(getConfigParamValue("keystorePath"), getConfigParamValue("keystorePassword"))
    		.given()
    			.auth().certificate(getConfigParamValue("cacertsPath"), getConfigParamValue("cacertsPassword"))
    		.expect()
    			.statusCode(statusCode)
    		.when()
    			.get(getConfigParamValue("baseUrl")+ relativePath);
    			
    	return this;
    }
    
    public RestAccess delete_verify_content(String relativePath,String xpath, String value ) {
    	RestAssured
    		//.given().auth().certificate(getConfigParamValue("keystorePath"), getConfigParamValue("keystorePassword"))
    		.given()
    			.auth().certificate(getConfigParamValue("cacertsPath"), getConfigParamValue("cacertsPassword"))
    		.when()
    			.get(getConfigParamValue("baseUrl")+ relativePath)
    		.then()
    			.assertThat()
    			.body(hasXPath(xpath), containsString(value));
    			
    	return this;
    }
        
    public RestAccess delete_verify_status_content(String relativePath, int statusCode, String xpath, String value ) {
    	RestAssured
    		//.given().auth().certificate(getConfigParamValue("keystorePath"), getConfigParamValue("keystorePassword"))
    		.given()
    			.auth().certificate(getConfigParamValue("cacertsPath"), getConfigParamValue("cacertsPassword"))
    		.expect()
    			.statusCode(statusCode)
    			.body(hasXPath(xpath), containsString(value))
    		.when()
    			.get(getConfigParamValue("baseUrl")+ relativePath);
    			
    	return this;
    }
    
    public RestAccess delete_verify_status_content(String relativePath, RequestSpecification requestSpec,ResponseSpecification responseSpec) {
    	RestAssured
    		//.given().auth().certificate(getConfigParamValue("keystorePath"), getConfigParamValue("keystorePassword"))
    		.given()
    			.auth().certificate(getConfigParamValue("cacertsPath"), getConfigParamValue("cacertsPassword"))
    			.spec(requestSpec)
    		.when()
    			.get(getConfigParamValue("baseUrl")+ relativePath)
    		.then()
    			.spec(responseSpec);
    			
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
