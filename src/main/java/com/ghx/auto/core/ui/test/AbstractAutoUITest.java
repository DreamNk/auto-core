/*
 * Copyright (c) 2013 Global Healthcare Exchange, LLC. All rights reserved.
 */

package com.ghx.auto.core.ui.test;

import java.lang.reflect.Field;
import java.util.List;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.BeforeClass;

import com.ghx.auto.core.db.domain.Scenario;
import com.ghx.auto.core.db.mapper.Mapper;
import com.ghx.auto.core.support.FileTransfer;
import com.ghx.auto.core.support.RestClient;
import com.ghx.auto.core.ui.page.AbstractPage;
import com.ghx.auto.core.ui.support.Alert;
import com.ghx.auto.core.ui.support.DriverContext;
import com.ghx.auto.core.ui.support.EnvConfig;
import com.ghx.auto.core.ui.support.Environment;
import com.ghx.auto.core.ui.support.Utils;

public abstract class AbstractAutoUITest {

	private DriverContext driverContext;

    private EnvConfig envConfig;

    protected SqlSessionFactory sessionFactory;

    @BeforeClass(alwaysRun=true)
    public void beforeTest(ITestContext context) {
        Object driverContext = context.getAttribute(DriverContext.class.getName());
        Assert.assertNotNull(driverContext, "No driver context provided in test context,");
        this.driverContext = (DriverContext) driverContext;

        Object envConfig = context.getSuite().getAttribute(EnvConfig.class.getName());
        Assert.assertNotNull(envConfig, "No env-config provided in suite context,");
        this.envConfig = (EnvConfig) envConfig;
        
        if (this.envConfig.getTestDataSource().equalsIgnoreCase("db")) {
        	Object sessionFactory = context.getAttribute(SqlSessionFactory.class.getName());
        	Assert.assertNotNull(sessionFactory, "No session factory provided in test context,");
        	this.sessionFactory = (SqlSessionFactory) sessionFactory;
        }
    }

    protected <T extends AbstractPage<T>> T get(Class<T> pageClass) {
    	return get(pageClass,this.driverContext.getDriver());
    }
    
    protected <T extends AbstractPage<T>> T get(Class<T> pageClass, WebDriver pageDriver) {
        try {
            T page = pageClass.newInstance();

            // little bit of reflection, let's keep it minimal.
            // inject driver
            Field driver = pageClass.getSuperclass().getSuperclass().getDeclaredField("driver");
            driver.setAccessible(true);
            driver.set(page, pageDriver);
            // inject env config
            Field envConfig = pageClass.getSuperclass().getSuperclass().getDeclaredField("envConfig");
            envConfig.setAccessible(true);
            envConfig.set(page, this.envConfig);

            return page;
        }
        catch (InstantiationException | IllegalAccessException | IllegalArgumentException | SecurityException
                | NoSuchFieldException ex) {
            Assert.fail("Unable to create page instance: " + pageClass.getSimpleName(), ex);
        }
        // should not happen
        return null;
    }

    protected WebDriver open_or_focus_secondary_browser() {
    	return driverContext.getSecondaryDriver();
    }
    
    protected  <T extends Mapper<T>> Object[][] get_test_scenario_data(Class<T> mapperClass) {
    	SqlSession session = sessionFactory.openSession();
    	T mapper = session.getMapper(mapperClass);
    	List<? extends Object> data = mapper.getData();
		session.close();
		Assert.assertNotNull(data,"Mapper data doesn't exist, ");
		return filterData(data);
    }
    
    private Object[][] filterData(List<? extends Object> data) {
    	String filter = envConfig.getTests();
    	Object[][] scenarioData;
    	
    	if((StringUtils.equalsIgnoreCase(filter, "All")) || (StringUtils.isBlank(filter))) {
           scenarioData = new Object[data.size()][2];
    	   for (int i=0;i<data.size();i++) {
  	  		    scenarioData[i][0] = ((Scenario)data.get(i)).getId();
  	  		    scenarioData[i][1] = data.get(i);
  		   }
    	}
    	else {
    	   int index = 0;
    	   int i = 0;
    	   TreeSet<Integer> testSeqNums = Utils.getTestSeqNums(filter);
    	   scenarioData = new Object[testSeqNums.size()][2];
    	   for (Integer testSeqNum : testSeqNums) {
    		    for (;i<data.size();i++) {
    			     if(testSeqNum.equals(Integer.parseInt(((Scenario)data.get(i)).getId()))){
    				    scenarioData[index][0] =	((Scenario)data.get(i)).getId();
		   	  		    scenarioData[index][1] =	data.get(i);
		   	  		    index++;
		   	  		    i++;
		   	  		    break;
    				 }
    				 else if (Integer.parseInt(((Scenario)data.get(i)).getId()) > testSeqNum 
	    					  || i == data.size()-1) {
	    				      Assert.fail("Not able to find test records for the given configuration, ");
    				 } // end of if-else if
    			} //end of inner for loop	
			} // end of outer for loop
    	} // end of if-else
 		
    	return scenarioData;
    } 
    
    protected String getEnvId() {
    	return Environment.valueOf(envConfig.getTargetEnv().toUpperCase()).getEnvId();
    }
    
    /**
     * Get File Transfer Utility.
     *
     * @param section Environment Configuration Section to be referred.
     */
    protected FileTransfer getFileTransfer(String section) {
    	return new FileTransfer(envConfig, section);
    }

    /**
     * Get Rest Client Utility.
     *
     * @param section Environment Configuration Section to be referred.
     */
    protected RestClient getRestClient(String section) {
    	return new RestClient(envConfig, section);
    }
    
    /**
     * Send an alert.
     *
     * @param name Name of component requesting an alert.
     * @param message Alert message.
     */
    protected void send_alert(String name, String message) {
        new Alert(name, message).send();
    }

    /**
     * Send an alert with a cause of exception.
     *
     * @param name Name of component requesting an alert.
     * @param message Alert message.
     * @param exception Exception resulting in the alert being requested.
     */
    protected void send_alert(String name, String message, Throwable exception) {
        new Alert(name, message, exception).send();
    }

    
}
