/*
 * Copyright (c) 2013 Global Healthcare Exchange, LLC. All rights reserved.
 */

package com.ghx.auto.core.ui.support;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

public class DriverContext {
	
	private EnvConfig envConfig;
	private String env;
	private String parentWindow;
	private WebDriver driver;
	private WebDriver secondaryDriver;
	
	public DriverContext(EnvConfig config, String env) {
		this.envConfig = config;
		this.env = env;
		this.driver = createDriver();
		this.parentWindow = driver.getWindowHandle();
        maximize();
	}
	
	public WebDriver getDriver() {
        return driver;
	}
	
	public WebDriver getSecondaryDriver() {
        if (secondaryDriver == null) {
        	secondaryDriver = createDriver();
        }
        return secondaryDriver;
	}
	
	private WebDriver createDriver() {
		Map<String, String> capabilities = new HashMap<String, String>(this.envConfig.get(env));
        DesiredCapabilities desiredCapabilities = new DesiredCapabilities(capabilities);
		String browser = capabilities.get("browser").toLowerCase();

        if (this.envConfig.getInstance().equals("remote")) {
        	try {
        		desiredCapabilities.setBrowserName(browser);
        		desiredCapabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
				return new RemoteWebDriver(new URL(envConfig.getCloudUrl()), desiredCapabilities);
			} 
        	catch (MalformedURLException ex) {
                Assert.fail("Unable to create remote driver for: " + envConfig.getCloudUrl(), ex);
			}
        }
        else {
        	switch(browser) {
            case "internet explorer":
                desiredCapabilities.setCapability(InternetExplorerDriver.NATIVE_EVENTS, false);
                desiredCapabilities.setCapability(InternetExplorerDriver.IE_ENSURE_CLEAN_SESSION, true);
            	return new InternetExplorerDriver(desiredCapabilities);
            case "firefox":
            	return new FirefoxDriver(desiredCapabilities);
            case "chrome":
            	return new ChromeDriver(desiredCapabilities);
            default:
                // throw exception
            }
        }
        
        Assert.assertNotNull(driver, "Unable to create driver instance:, ");
        // should not happen
        return null;
	}
	
	public WebDriver getParentWindow() {
		return driver.switchTo().window(this.parentWindow);
	}
	
	public WebDriver getPopupWindow() {
		new WebDriverWait(driver, envConfig.getTimeout(), 100).until(new ExpectedCondition<Boolean> () {
    		@Override
    		public Boolean apply(WebDriver driver) {
    			Set<String> windowHandles = driver.getWindowHandles();
    			return (windowHandles.size() > 1 && !windowHandles.contains(""));
    		}
    		
    		@Override
	    	public String toString() {
	    		return String.format("focusing on the pop-up window");
	    	} 
    	});
		
		WebDriver popup = null;
		Set<String> windowHandles = driver.getWindowHandles();
		for (String windowHandle : windowHandles) {
			if (!windowHandle.equals(this.parentWindow)) {
				popup = driver.switchTo().window(windowHandle);
			}
		}
		
        Assert.assertNotNull(popup, "Unable to focus on pop-up window:, ");
        
        return  popup;
	}
	
  	public void quitDrivers() {
        if (secondaryDriver != null) {
        	secondaryDriver.quit();
        }
        if (driver != null) {
        	driver.quit();
        }
	}
  	
    private WebDriver maximize() {
        driver.manage().window().maximize();
        return driver;
    }

}
