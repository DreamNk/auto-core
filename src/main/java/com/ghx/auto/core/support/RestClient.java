/*
 * Copyright (c) 2014 Global Healthcare Exchange, LLC. All rights reserved.
 */

package com.ghx.auto.core.support;

import static org.hamcrest.core.StringContains.containsString;
import static org.hamcrest.xml.HasXPath.hasXPath;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.testng.Assert;

import com.ghx.auto.core.ui.support.EnvConfig;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.builder.ResponseSpecBuilder;
import com.jayway.restassured.response.ValidatableResponse;
import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;

public class RestClient {
	
    private EnvConfig envConfig;
    private String section;
    private RequestSpecification reqSpec;
    private ResponseSpecification resSpec;
    private ValidatableResponse response;
    
    public RestClient(EnvConfig envConfig, String section) {
    	this.envConfig = envConfig;
    	this.section = section;
    	this.reqSpec = build_request_spec();
    	this.resSpec = build_response_spec();
    	loadCertificates(reqSpec);
    	reqSpec.baseUri(getConfigParamValue("baseUrl"));
    }
    
    public RestClient OPTIONS(String resource) {
    	response = RestAssured
			    		.given()
			    			.spec(reqSpec)
			    		.when()
			    			.options(resource)
			    		.then()
			    			.spec(resSpec);
    	return this;
    }
    
    public RestClient OPTIONS(String resource, RequestSpecification requestSpec,ResponseSpecification responseSpec) {
    	loadCertificates(requestSpec);
    	requestSpec.baseUri(getConfigParamValue("baseUrl"));

    	response = RestAssured
			    		.given()
			    			.spec(requestSpec)
			    		.when()
			    			.options(resource)
			    		.then()
			    			.spec(responseSpec);
    	return this;
    }
    
    public RestClient HEAD(String resource) {
    	response = RestAssured
			    		.given()
			    			.spec(reqSpec)
			    		.when()
			    			.head(resource)
			    		.then()
			    			.spec(resSpec);
    	return this;
    }
    
    public RestClient HEAD(String resource, RequestSpecification requestSpec,ResponseSpecification responseSpec) {
    	loadCertificates(requestSpec);
    	requestSpec.baseUri(getConfigParamValue("baseUrl"));

    	response = RestAssured
			    		.given()
			    			.spec(requestSpec)
			    		.when()
			    			.head(resource)
			    		.then()
			    			.spec(responseSpec);
    	return this;
    }
    
    public RestClient GET(String resource) {
    	response = RestAssured
    					.given()
    						.spec(reqSpec)
    					.when()
    					    .get(resource)
    					.then()
    						.spec(resSpec);
    	return this;
    }
    
    public RestClient GET(String resource, RequestSpecification requestSpec,ResponseSpecification responseSpec) {
    	loadCertificates(requestSpec);
    	requestSpec.baseUri(getConfigParamValue("baseUrl"));

    	response = RestAssured
			    		.given()
			    			.spec(requestSpec)
			    		.when()
			    			.get(resource)
			    		.then()
			    			.spec(responseSpec);
    	return this;
    }
    
    public RestClient POST(String resource) {
    	response = RestAssured
			    		.given()
			    			.spec(reqSpec)
			    		.when()
			    			.post(resource)
			    		.then()
			    			.spec(resSpec);
    	return this;
    }
    
    public RestClient POST(String resource, RequestSpecification requestSpec,ResponseSpecification responseSpec) {
    	loadCertificates(requestSpec);
    	requestSpec.baseUri(getConfigParamValue("baseUrl"));

    	response = RestAssured
			    		.given()
			    			.spec(requestSpec)
			    		.when()
			    			.post(resource)
			    		.then()
			    			.spec(responseSpec);
    	return this;
    }
    
    public RestClient PUT(String resource) {
    	response = RestAssured
			    		.given()
			    			.spec(reqSpec)
			    		.when()
			    			.put(resource)
			    		.then()
			    			.spec(resSpec);
    	return this;
    }
    
    public RestClient PUT(String resource, RequestSpecification requestSpec,ResponseSpecification responseSpec) {
    	loadCertificates(requestSpec);
    	requestSpec.baseUri(getConfigParamValue("baseUrl"));

    	response = RestAssured
			    		.given()
			    			.spec(requestSpec)
			    		.when()
			    			.put(resource)
			    		.then()
			    			.spec(responseSpec);
    	return this;
    }
   
    public RestClient DELETE(String resource) {
    	response = RestAssured
			    		.given()
			    			.spec(reqSpec)
			    		.when()
			    			.delete(resource)
			    		.then()
			    			.spec(resSpec);
    	return this;
    }
    
    public RestClient DELETE(String resource, RequestSpecification requestSpec,ResponseSpecification responseSpec) {
    	loadCertificates(requestSpec);
    	requestSpec.baseUri(getConfigParamValue("baseUrl"));

    	response = RestAssured
			    		.given()
			    			.spec(requestSpec)
			    		.when()
			    			.delete(resource)
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
    
    public RestClient verify_status(int statusCode) {
    	resSpec.statusCode(statusCode);
    	return this;
    }
    
    public RestClient verify_header(String name, String value) {
    	resSpec.header(name, containsString(value));
    	return this;
    }
    
    public RestClient verify_content(String value ) {
    	resSpec.body(containsString(value));
    	return this;
    }
    
    public RestClient verify_content_with_xPath(String xpath, String value ) {
    	resSpec.body(hasXPath(xpath), containsString(value));
    	return this;
    }
    
    public RestClient verify_content_with_jsonPath(String jsonPath, String value ) {
    	resSpec.body(jsonPath, containsString(value));
    	return this;
    }
    
    public RestClient add_proxy() {
    	reqSpec.proxy(getConfigParamValue("proxyHost"), Integer.parseInt(getConfigParamValue("proxyPort")));
    	return this;
    }
    
    public RestClient add_header(String name, String value) {
    	reqSpec.header(name, value);
    	return this;
    }
    
    public RestClient add_url_param(String name, String value) {
    	reqSpec.queryParam(name, value);
    	return this;
    }
    
    public RestClient add_body(String content) {
    	reqSpec.body(content);
    	return this;
    }
    
    public RestClient add_file_content_to_body(String path) {
    	reqSpec.body(getFile(path));
    	return this;
    }
    
    public RestClient add_form_data(String name, String value) {
    	reqSpec.multiPart(name, value);
    	return this;
    }
    
    public RestClient add_form_data(String name, String value, String mimeType) {
    	reqSpec.multiPart(name, value, mimeType);
    	return this;
    }
    
    public RestClient add_form_file(String name, String path) {
    	reqSpec.multiPart(name, getFile(path));
    	return this;
    }
    
    public RestClient basic_authentication(String username, String password) {
    	reqSpec.auth().basic(username, password);
    	return this;
    }
    
    public int get_status_code() {
    	return response.extract().statusCode();
    }
    
    public String get_body() {
    	return response.extract().body().asString() != null ? response.extract().body().asString() : "";
    }
    
    public void download_body(String path) {
    	InputStream in = response.extract().body().asInputStream();
    	try {
			FileUtils.copyInputStreamToFile(in, getFile(path));
		} catch (IOException e) {
			Assert.fail("Failure downloading body to file " + path + ".");
		}
    }
    
    public String get_headers() {
    	return response.extract().headers().toString();
    }
    
    public String get_header(String name) {
    	return response.extract().header(name);
    }
    
    public String get_header_trail(String name) {
    	return response.extract().header(name).replaceFirst(".*/([^/?]+).*", "$1");
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
    	Map<String, String> configSection = this.envConfig.get(section.toUpperCase());
    	Assert.assertNotNull(configSection, "No configuration section provided in configuration file with the name: " + section.toUpperCase());
    	String paramValue =  configSection.get(param);
    	Assert.assertNotNull(StringUtils.defaultIfBlank(paramValue, null),
                "No configuration provided for the param: " + param
                        + ",under section: " + section.toUpperCase());
    	return paramValue;
    	
    }
    
    private File getFile(String path) {
    	return new File(path);
    }

}
