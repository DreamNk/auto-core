/*
 * Copyright (c) 2013 Global Healthcare Exchange, LLC. All rights reserved.
 */

package com.ghx.auto.core.ui.listener;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.TestListenerAdapter;

import com.ghx.auto.core.ui.support.DriverContext;
import com.ghx.auto.core.ui.support.EnvConfig;

public class AutoUITestListener extends TestListenerAdapter {

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
        catch (Exception e) {
        	e.printStackTrace();
        } 
        
        return sessionFactory;
    }
    
}
