/*
 * Copyright (c) 2013 Global Healthcare Exchange, LLC. All rights reserved.
 */

package com.ghx.auto.core.support;

import static org.hamcrest.core.StringContains.containsString;
import static org.hamcrest.xml.HasXPath.hasXPath;

import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.testng.Assert;

import com.ghx.auto.core.ui.support.EnvConfig;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.builder.ResponseSpecBuilder;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;


public class RestClient {
	
    private EnvConfig envConfig;
    private String section;
    
    public RestClient(EnvConfig envConfig, String section) {
    	this.envConfig = envConfig;
    	this.section = section;
    	loadCertificates();
    }
    
    public RestClient get_verify_status(String relativePath, int statusCode) {
    	RestAssured
    		.expect()
    			.statusCode(statusCode)
    		.when()
    			.get(getConfigParamValue("baseUrl")+ relativePath);
    			
    	return this;
    }
    
    public RestClient get_verify_status_content_xml(String relativePath, int statusCode, String xpath, String value ) {
    	RestAssured
    		.expect()
    			.statusCode(statusCode)
    			.body(hasXPath(xpath), containsString(value))
    		.when()
    			.get(getConfigParamValue("baseUrl")+ relativePath);
    			
    	return this;
    }
    
    public RestClient get_verify_status_content_json(String relativePath, int statusCode, String jsonPath, String value ) {
    	RestAssured
    		.expect()
    			.statusCode(statusCode)
    			.body(jsonPath, containsString(value))
    		.when()
    			.get(getConfigParamValue("baseUrl")+ relativePath);
    			
    	return this;
    }
    
    public RestClient get_verify_content_xml(String relativePath,String xpath, String value ) {
    	RestAssured
    		.when()
    			.get(getConfigParamValue("baseUrl")+ relativePath)
    		.then()
    			.assertThat()
    			.body(hasXPath(xpath), containsString(value));
    			
    	return this;
    }
    
    public RestClient get_verify_content_json(String relativePath,String jsonPath, String value ) {
    	RestAssured
    		.when()
    			.get(getConfigParamValue("baseUrl")+ relativePath)
    		.then()
    			.assertThat()
    			.body(jsonPath, containsString(value));
    			
    	return this;
    }
    
    public RestClient get_verify_status_content(String relativePath, RequestSpecification requestSpec,ResponseSpecification responseSpec) {
    	loadCertificates(requestSpec);
    	RestAssured
    		.given()
    			.spec(requestSpec)
    		.when()
    			.get(getConfigParamValue("baseUrl")+ relativePath)
    		.then()
    			.spec(responseSpec);
    			
    	return this;
    }
    
    public RestClient post_xml_verify_status(String relativePath, String requestBody, int statusCode) {
    	RestAssured
    		.given()
    			.contentType(ContentType.XML)
    			.body(requestBody)
    		.expect()
    			.statusCode(statusCode)
    		.when()
    			.post(getConfigParamValue("baseUrl")+ relativePath);
    			
    	return this;
    }
    
    public RestClient post_xml_verify_content(String relativePath, String requestBody, String xpath, String value ) {
    	RestAssured
    		.given()
    			.contentType(ContentType.XML)
    			.body(requestBody)
    		.when()
    			.post(getConfigParamValue("baseUrl")+ relativePath)
    		.then()
    			.assertThat()
    			.body(hasXPath(xpath), containsString(value));
    			
    	return this;
    }
        
    public RestClient post_xml_verify_status_content(String relativePath, String requestBody, int statusCode, String xpath, String value ) {
    	RestAssured
    		.given()
    			.contentType(ContentType.XML)
    			.body(requestBody)
    		.expect()
    			.statusCode(statusCode)
    			.body(hasXPath(xpath), containsString(value))
    		.when()
    			.post(getConfigParamValue("baseUrl")+ relativePath);
    			
    	return this;
    }
    
    public RestClient post_json_verify_status(String relativePath, String requestBody, int statusCode) {
    	RestAssured
    		.given()
    			.contentType(ContentType.JSON)
    			.body(requestBody)
    		.expect()
    			.statusCode(statusCode)
    		.when()
    			.post(getConfigParamValue("baseUrl")+ relativePath);
    			
    	return this;
    }
    
    public RestClient post_json_verify_content(String relativePath, String requestBody, String jsonPath, String value ) {
    	RestAssured
    		.given()
    			.contentType(ContentType.JSON)
    			.body(requestBody)
    		.when()
    			.post(getConfigParamValue("baseUrl")+ relativePath)
    		.then()
    			.assertThat()
    			.body(jsonPath, containsString(value));
    			
    	return this;
    }
        
    public RestClient post_json_verify_status_content(String relativePath, String requestBody, int statusCode, String jsonPath, String value ) {
    	RestAssured
    		.given()
    			.contentType(ContentType.JSON)
    			.body(requestBody)
    		.expect()
    			.statusCode(statusCode)
    			.body(jsonPath, containsString(value))
    		.when()
    			.post(getConfigParamValue("baseUrl")+ relativePath);
    			
    	return this;
    }
    
    public RestClient post_verify_status_content(String relativePath, RequestSpecification requestSpec,ResponseSpecification responseSpec) {
    	loadCertificates(requestSpec);
    	RestAssured
    		.given()
    			.spec(requestSpec)
    		.when()
    			.post(getConfigParamValue("baseUrl")+ relativePath)
    		.then()
    			.spec(responseSpec);
    			
    	return this;
    }
    
    public RestClient put_xml_verify_status(String relativePath, String requestBody, int statusCode) {
    	RestAssured
    		.given()
    			.contentType(ContentType.XML)
    			.body(requestBody)
    		.expect()
    			.statusCode(statusCode)
    		.when()
    			.put(getConfigParamValue("baseUrl")+ relativePath);
    			
    	return this;
    }
    
    public RestClient put_xml_verify_content(String relativePath, String requestBody, String xpath, String value ) {
    	RestAssured
    		.given()
    			.contentType(ContentType.XML)
    			.body(requestBody)
    		.when()
    			.put(getConfigParamValue("baseUrl")+ relativePath)
    		.then()
    			.assertThat()
    			.body(hasXPath(xpath), containsString(value));
    			
    	return this;
    }
        
    public RestClient put_xml_verify_status_content(String relativePath, String requestBody, int statusCode, String xpath, String value ) {
    	RestAssured
    		.given()
    			.contentType(ContentType.XML)
    			.body(requestBody)
    		.expect()
    			.statusCode(statusCode)
    			.body(hasXPath(xpath), containsString(value))
    		.when()
    			.put(getConfigParamValue("baseUrl")+ relativePath);
    			
    	return this;
    }
    
    public RestClient put_json_verify_status(String relativePath, String requestBody, int statusCode) {
    	RestAssured
    		.given()
    			.contentType(ContentType.JSON)
    			.body(requestBody)
    		.expect()
    			.statusCode(statusCode)
    		.when()
    			.put(getConfigParamValue("baseUrl")+ relativePath);
    			
    	return this;
    }
    
    public RestClient put_json_verify_content(String relativePath, String requestBody, String jsonPath, String value ) {
    	RestAssured
    		.given()
    			.contentType(ContentType.JSON)
    			.body(requestBody)
    		.when()
    			.put(getConfigParamValue("baseUrl")+ relativePath)
    		.then()
    			.assertThat()
    			.body(jsonPath, containsString(value));

    	return this;
    }
        
    public RestClient put_json_verify_status_content(String relativePath, String requestBody, int statusCode, String jsonPath, String value ) {
    	RestAssured
    		.given()
    			.contentType(ContentType.JSON)
    			.body(requestBody)
    		.expect()
    			.statusCode(statusCode)
    			.body(jsonPath, containsString(value))
    		.when()
    			.put(getConfigParamValue("baseUrl")+ relativePath);
    			
    	return this;
    }
    
    public RestClient put_verify_status_content(String relativePath, RequestSpecification requestSpec,ResponseSpecification responseSpec) {
    	loadCertificates(requestSpec);
    	RestAssured
    		.given()
    			.spec(requestSpec)
    		.when()
    			.put(getConfigParamValue("baseUrl")+ relativePath)
    		.then()
    			.spec(responseSpec);
    			
    	return this;
    }
    
    public RestClient delete_verify_status(String relativePath, int statusCode) {
    	RestAssured
    		.expect()
    			.statusCode(statusCode)
    		.when()
    			.delete(getConfigParamValue("baseUrl")+ relativePath);
    			
    	return this;
    }
    
    public RestClient delete_verify_content_xml(String relativePath,String xpath, String value ) {
    	RestAssured
    		.when()
    			.delete(getConfigParamValue("baseUrl")+ relativePath)
    		.then()
    			.assertThat()
    			.body(hasXPath(xpath), containsString(value));
    			
    	return this;
    }
    
    public RestClient delete_verify_content_json(String relativePath,String jsonPath, String value ) {
    	RestAssured
    		.when()
    			.delete(getConfigParamValue("baseUrl")+ relativePath)
    		.then()
    			.assertThat()
    			.body(jsonPath, containsString(value));
    			
    	return this;
    }
        
    public RestClient delete_verify_status_content_xml(String relativePath, int statusCode, String xpath, String value ) {
    	RestAssured
    		.expect()
    			.statusCode(statusCode)
    			.body(hasXPath(xpath), containsString(value))
    		.when()
    			.delete(getConfigParamValue("baseUrl")+ relativePath);
    			
    	return this;
    }
    
    public RestClient delete_verify_status_content_json(String relativePath, int statusCode, String jsonPath, String value ) {
    	RestAssured
    		.expect()
    			.statusCode(statusCode)
    			.body(jsonPath, containsString(value))
    		.when()
    			.delete(getConfigParamValue("baseUrl")+ relativePath);
    			
    	return this;
    }
    
    public RestClient delete_verify_status_content(String relativePath, RequestSpecification requestSpec,ResponseSpecification responseSpec) {
    	loadCertificates(requestSpec);
    	RestAssured
    		.given()
    			.spec(requestSpec)
    		.when()
    			.delete(getConfigParamValue("baseUrl")+ relativePath)
    		.then()
    			.spec(responseSpec);
    			
    	return this;
    }
    
    public RequestSpecification build_request_spec() {
    	return new RequestSpecBuilder().build();
    }
    
    public ResponseSpecification build_response_spec() {
    	return new ResponseSpecBuilder().build();
    }
    
    public RestClient add_header(String name, String value) {
    	RestAssured
    		.given()	
    			.header(name, value);
    	return this;
    }
    
    public RestClient add_url_param(String name, String value) {
    	RestAssured
    		.given()
    			.queryParam(name, value);
    	
    	return this;
    }
    
    public RestClient add_form_data(String name, String value) {
    	RestAssured
    		.given()
    			.multiPart(name, value);
    	
    	return this;
    }
    
    public RestClient add_form_data(String name, String value, String mimeType) {
    	RestAssured
    		.given()
    			.multiPart(name, value, mimeType);
    	
    	return this;
    }
    
    public RestClient add_form_file(String name, String filePath) {
    	RestAssured
    		.given()
    			.multiPart(name, new File(filePath));
    	return this;
    }
    
    public RestClient add_to_body(String content) {
    	RestAssured
    		.given()
    			.body(content);
    	
    	return this;
    }
    
    public RestClient add_file_content_to_body(String path) {
    	RestAssured
    		.given()
    			.body(new File(path));
    	
    	return this;
    }
    
    public RestClient basic_authentication(String username, String password) {
    	RestAssured
    		.given()
    			.auth().basic(username, password);
    	
    	return this;
    }
    
    private void loadCertificates() {
    	Map<String,String> certificateStores =  this.envConfig.get("CERTIFICATES");
    	Set<String> keys = certificateStores.keySet();
    	Iterator<String> itr = keys.iterator();
    	
    	while(itr.hasNext()) {
    		String key = itr.next();
    		if (StringUtils.isNotBlank(key) && !(key.contains("Password"))) {
        		RestAssured.keystore(getConfigParamValue("CERTIFICATES", key), getConfigParamValue("CERTIFICATES", key + "Password"));
    		}
    	}
    }
    
    private void loadCertificates(RequestSpecification requestSpec) {
    	Map<String,String> certificateStores =  this.envConfig.get("CERTIFICATES");
    	Set<String> keys = certificateStores.keySet();
    	Iterator<String> itr = keys.iterator();
    	
    	while(itr.hasNext()) {
    		String key = itr.next();
    		if (StringUtils.isNotBlank(key) && !(key.contains("Password"))) {
    			requestSpec.keystore(getConfigParamValue("CERTIFICATES", key), getConfigParamValue("CERTIFICATES", key + "Password"));
    		}
    	}
    }
    
    private String getConfigParamValue(String param) {
    	return getConfigParamValue(this.section,param);
    }
    
    private String getConfigParamValue(String section, String param) {
    	String paramValue =  this.envConfig.get(section.toUpperCase()).get(param);
    	Assert.assertNotNull(StringUtils.defaultIfBlank(paramValue, null),
                "No configuration provided for the param: " + param
                        + ",under section: " + section.toUpperCase());
    	return paramValue;
    }

}
