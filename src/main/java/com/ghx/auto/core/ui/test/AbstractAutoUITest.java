/*
 * Copyright (c) 2013 Global Healthcare Exchange, LLC. All rights reserved.
 */

package com.ghx.auto.core.ui.test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.custommonkey.xmlunit.DetailedDiff;
import org.custommonkey.xmlunit.XMLUnit;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.BeforeClass;
import org.xml.sax.SAXException;

import com.ghx.auto.core.db.domain.Scenario;
import com.ghx.auto.core.db.mapper.Mapper;
import com.ghx.auto.core.support.DBClient;
import com.ghx.auto.core.support.FileAccessClient;
import com.ghx.auto.core.support.MongoDBClient;
import com.ghx.auto.core.support.RestClient;
import com.ghx.auto.core.support.S3Client;
import com.ghx.auto.core.support.excel.ExcelClient;
import com.ghx.auto.core.ui.page.AbstractPage;
import com.ghx.auto.core.ui.support.Alert;
import com.ghx.auto.core.ui.support.DriverContext;
import com.ghx.auto.core.ui.support.EnvConfig;
import com.ghx.auto.core.ui.support.Environment;
import com.ghx.auto.core.ui.support.Utils;

public abstract class AbstractAutoUITest {

	private DriverContext driverContext;

    private EnvConfig envConfig;
    
    private ITestContext context;
    
	protected Logger logger = LoggerFactory.getLogger(getClass().getSimpleName());

    protected SqlSessionFactory sessionFactory;
    
    @BeforeClass(alwaysRun=true)
    public void init(ITestContext context) {
    	this.context = context;
    	
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
    	return get(pageClass,this.driverContext.getPrimaryDriver());
    }
    
    protected <T extends AbstractPage<T>> T get(Class<T> pageClass, WebDriver pageDriver) {
        try {
            T page = pageClass.newInstance();

            // little bit of reflection, let's keep it minimal.
            // inject driver
            Field driver = AbstractPage.class.getDeclaredField("driver");
            driver.setAccessible(true);
            driver.set(page, pageDriver);
            // inject env config
            Field envConfig = AbstractPage.class.getDeclaredField("envConfig");
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
    
    protected Environment getEnvironment() {
    	return Environment.valueOf(envConfig.getTargetEnv().toUpperCase());
    }
    
    
    /**
     * Open an existing secondary driver or create one if not available.
     *
     */
    protected WebDriver open_or_focus_secondary_browser() {
    	return driverContext.getSecondaryDriver();
    }
    
    /**
     * focus on the parent window of the driver.
     *
     */
    protected WebDriver focus_on_parent_window() {
    	return driverContext.getParentWindow();
    }
    
    /**
     * focus on the parent window of the secondary driver.
     *
     */
    protected WebDriver focus_on_secondary_browser_parent_window() {
    	return driverContext.getSecondaryDriverParentWindow();
    }
    
    /**
     * focus on the pop-up window 1 of the driver.
     *
     */
    protected WebDriver focus_on_popup_window() {
    	return driverContext.getPopupWindow_1();
    }
    
    /**
     * close pop-up window 1 of the driver.
     *
     */
    protected void close_popup_window() {
    	driverContext.closePopupWindow_1();
    }
    
    /**
     * focus on the pop-up window 1 of the secondary driver.
     *
     */
    protected WebDriver focus_on_secondary_browser_popup_window() {
    	return driverContext.getSecondaryDriverPopupWindow_1();
    }
    
    /**
     * close pop-up window 1 of the secondary driver.
     *
     */
    protected void close_secondary_browser_popup_window() {
    	driverContext.closeSecondaryDriverPopupWindow_1();
    }
    
    /**
     * focus on the pop-up window 2 of the driver.
     *
     */
    protected WebDriver focus_on_popup_window_2() {
    	return driverContext.getPopupWindow_2();
    }
    
    /**
     * close pop-up window 2 of the driver.
     *
     */
    protected void close_popup_window_2() {
    	driverContext.closePopupWindow_2();
    }
    
    /**
     * focus on the pop-up window 2 of the secondary driver.
     *
     */
    protected WebDriver focus_on_secondary_browser_popup_window_2() {
    	return driverContext.getSecondaryDriverPopupWindow_2();
    }
    
    /**
     * close pop-up window 2 of the secondary driver.
     *
     */
    protected void close_secondary_browser_popup_window_2() {
    	driverContext.closeSecondaryDriverPopupWindow_2();
    }
    
    /**
     * Get File Access Utility.
     *
     * @param section Environment Configuration Section to be referred.
     */
    protected FileAccessClient getFileAccessClient(String section) {
    	return new FileAccessClient(envConfig, section);
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
     * Get DB Client Utility.
     *
     * @param section Environment Configuration Section to be referred.
     */
    protected DBClient getDBClient(String section) {
    	return new DBClient(envConfig, section);
    }
    
    /**
     * Get MongoDB Client Utility.
     *
     * @param section Environment Configuration Section to be referred.
     */
    protected MongoDBClient getMongoDBClient(String section) {
    	return new MongoDBClient(envConfig, section);
    }
    
    /**
     * Get MongoDB Client Utility.
     *
     * @param section Environment Configuration Section to be referred.
     */
    protected MongoDBClient getMongoDBClient(String section, String dbName) {
    	return new MongoDBClient(envConfig, section, dbName);
    }
    
    /**
     * Get Excel Client Utility.
     *
     * @param section Environment Configuration Section to be referred.
     */
    protected ExcelClient getExcelClient(String section) {
    	return new ExcelClient(envConfig, section);
    }
    
	/**
	 * Get Excel Client Utility.
	 *
	 * @param section
	 *            Environment Configuration Section to be referred.
	 */
	protected ExcelClient getExcelClient(String content, String sheetName) {
		return new ExcelClient(envConfig, content, sheetName);
	}
    
    /**
     * Get S3 Client Utility.
     *
     * @param section Environment Configuration Section to be referred.
     */
    protected S3Client getS3Client(String section) {
    	return new S3Client(envConfig, section);
    }
    
    /**
     * Get configuration param value from env-config.ini.
     *
     * @param section Configuration Section to be referred.
     * @param param Configuration parameter under a section to be referred.
     */
    protected String getConfigParamValue(String section, String param) {
    	return envConfig.getConfigParamValue(section, param);
    }
    
    /**
     * Get the current environment the tests are running in.
     *
     */
    protected String getTargetEnv() {
    	return envConfig.getTargetEnv();
    }
    
    /**
     * Get the absolute path of the project root directory.
     *
     */
    protected String getProjectRootDirectory() {
    	return System.getProperty("user.dir");
    }
    
    /**
     * Store the new attribute in test context.
     *
     * @param name String Attribute name.
     * @param value String Attribute value.
     */
    protected void store_test_data(String name, String value) {
    	context.setAttribute(name, value);
    }
    
    /**
     * Retrieve the attribute from to test context.
     *
     * @param name String Attribute name.
     */
    protected String retrieve_test_data(String name) {
    	return (String) context.getAttribute(name);
    }
    
    /**
     * Compare XML.
     *
     */
    protected void compare_xml(String expectedXml,String actualXml) {
    	XMLUnit.setIgnoreWhitespace(true);
		XMLUnit.setIgnoreComments(false);
        XMLUnit.setIgnoreAttributeOrder(true);

        DetailedDiff diff;
		try {
			diff = new DetailedDiff(XMLUnit.compareXML(expectedXml, actualXml));
	        List<String> allDifferences = diff.getAllDifferences();
	        Assert.assertTrue(allDifferences.size() == 0, "XMLs DID NOT MATCH!!!, " +  StringUtils.join(allDifferences.iterator(), ","));
		} catch (SAXException e) {
			Assert.fail("Unable to compare xmls, error is: " + e.getMessage());
		} catch (IOException e) {
			Assert.fail("Unable to compare xmls, error is: " + e.getMessage());
		}
        
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
