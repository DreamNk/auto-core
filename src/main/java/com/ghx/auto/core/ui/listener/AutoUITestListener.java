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

	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS");
	
    @Override
    public void onStart(ITestContext context) {
        super.onStart(context);

        String env = context.getCurrentXmlTest().getParameter("env");
        Assert.assertNotNull(StringUtils.defaultIfBlank(env, null), "Test parameter 'env' must not be blank,");

        // load appropriate ini preferences from suite attribute
        EnvConfig envConfig = (EnvConfig) context.getSuite().getAttribute(EnvConfig.class.getName());
        
        // save driverContext in testContext
        context.setAttribute(DriverContext.class.getName(), new DriverContext(envConfig,env));

        //save mybatis SessionFactory in testContext
        if (envConfig.getTestDataSource().equalsIgnoreCase("db")) {
        	String mybatisConfig = (String) context.getSuite().getAttribute("mybatisConfig");
            context.setAttribute(SqlSessionFactory.class.getName(), createSessionFactory(mybatisConfig));
        }
    }
    
    @Override
    public void onTestStart(ITestResult result) {
    	System.out.println("running... " + result.getTestClass().getRealClass().getSimpleName() + " --> " + result.getName());
    }
    
    @Override
    public void onTestSuccess(ITestResult result) {
    	System.out.println("passed... " + result.getTestClass().getRealClass().getSimpleName() + " --> " + result.getName());
    }
    
    @Override
    public void onTestFailure(ITestResult result) {
    	System.out.println("FAILED... " + result.getTestClass().getRealClass().getSimpleName() + " --> " + result.getName());
        EnvConfig envConfig = (EnvConfig) result.getTestContext().getSuite().getAttribute(EnvConfig.class.getName());
    	String screenShotsLocation = getConfigParamValue(envConfig, "REPORTING", "screenShotsLocation");
        DriverContext driverContext = (DriverContext) result.getTestContext().getAttribute(DriverContext.class.getName());
    	
        if (StringUtils.isNotBlank(screenShotsLocation) && driverContext.isPrimaryDriverExists()) {
    		TakesScreenshot screenShot = ((TakesScreenshot) driverContext.getPrimaryDriver());
    		File file = screenShot.getScreenshotAs(OutputType.FILE);
    		try {
    			FileUtils.copyFile(file, new File(screenShotsLocation + "/" + result.getName() + "_" + sdf.format(new Date(result.getEndMillis())) + ".png"));
    		} catch (IOException e) {
    			System.out.println("Unable to take screenshot for test: " + result.getName());
    		}
    	}	
    		
    }
    
    @Override
    public void onTestSkipped(ITestResult result) {
    	System.out.println("skipped... " + result.getTestClass().getRealClass().getSimpleName() + " --> " + result.getName());
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
    
    private String getConfigParamValue(EnvConfig envConfig, String section, String param) {
    	Map<String, String> configSection = envConfig.get(section.toUpperCase());
    	if (!CollectionUtils.isEmpty(configSection)) {
        	return configSection.get(param);
    	}
    	return null;
    }
    
}
