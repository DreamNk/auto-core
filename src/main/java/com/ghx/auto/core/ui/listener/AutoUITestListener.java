/*
 * Copyright (c) 2013 Global Healthcare Exchange, LLC. All rights reserved.
 */

package com.ghx.auto.core.ui.listener;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.springframework.util.CollectionUtils;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

import com.ghx.auto.core.ui.support.DriverContext;
import com.ghx.auto.core.ui.support.EnvConfig;

public class AutoUITestListener extends TestListenerAdapter {

	DriverContext driverContext;
	TakesScreenshot screenShot;
	EnvConfig envConfig;
	File file;
	String method;
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS");
	
    @Override
    public void onStart(ITestContext context) {
        super.onStart(context);

        String env = context.getCurrentXmlTest().getParameter("env");
        Assert.assertNotNull(StringUtils.defaultIfBlank(env, null), "Test parameter 'env' must not be blank,");

        // load appropriate ini preferences from suite attribute
        envConfig = (EnvConfig) context.getSuite().getAttribute(EnvConfig.class.getName());
        
        driverContext = new DriverContext(envConfig,env);
        // save driverContext in testContext
        context.setAttribute(DriverContext.class.getName(), driverContext);

        //save mybatis SessionFactory in testContext
        if (envConfig.getTestDataSource().equalsIgnoreCase("db")) {
        	String mybatisConfig = (String) context.getSuite().getAttribute("mybatisConfig");
            context.setAttribute(SqlSessionFactory.class.getName(), createSessionFactory(mybatisConfig));
        }
    }
    
    @Override
    public void onTestStart(ITestResult result) {
    	method = result.getName();
    	System.out.println("Test started: " + method);
    }
    
    @Override
    public void onTestSuccess(ITestResult result) {
    	method = result.getName();
    	System.out.println("Test passed: " + method);
    }
    
    @Override
    public void onTestFailure(ITestResult result) {
    	method = result.getName();
    	System.out.println("Test failed: " + method);
    	String screenShotsLocation = getConfigParamValue("REPORTING", "screenShotsLocation");
    	if (StringUtils.isNotBlank(screenShotsLocation)) {
    		screenShot = ((TakesScreenshot) driverContext.getPrimaryDriver());
    		file = screenShot.getScreenshotAs(OutputType.FILE);
    		try {
    			FileUtils.copyFile(file, new File(screenShotsLocation + "/" + method + "_" + sdf.format(new Date(result.getEndMillis())) + ".png"));
    		} catch (IOException e) {
    			System.out.println("Unable to take screenshot for test: " + method);
    		}
    	}	
    		
    }
    
    @Override
    public void onTestSkipped(ITestResult result) {
    	System.out.println("Test skipped: " + result.getName());
    }

    @Override
    public void onFinish(ITestContext testContext) {
        super.onFinish(testContext);
        
        EnvConfig envConfig = (EnvConfig) testContext.getSuite().getAttribute(EnvConfig.class.getName());
        DriverContext driverContext = (DriverContext) testContext.getAttribute(DriverContext.class.getName());
        if (envConfig.isCloseBrowserOnFinish()) {
        	driverContext.quitDrivers();
        }
    }

    private SqlSessionFactory createSessionFactory(String mybatisConfig) {
    	
    	 // creating mybatis sessionfactory and set in the context 
    	SqlSessionFactory sessionFactory = null;
        try {
        	InputStream inputstream = Resources.getResourceAsStream(mybatisConfig);
        	SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
        	sessionFactory = builder.build(inputstream);
		}  
        catch (IOException ioe) {
        	Assert.fail("Unable to read mybatis configuration file: " + mybatisConfig, ioe);
        } 
        
        return sessionFactory;
    }
    
    private String getConfigParamValue(String section, String param) {
    	Map<String, String> configSection = this.envConfig.get(section.toUpperCase());
    	if (!CollectionUtils.isEmpty(configSection)) {
        	return configSection.get(param);
    	}
    	return null;
    }
    
}
