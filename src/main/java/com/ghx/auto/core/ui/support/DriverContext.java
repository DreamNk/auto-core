/*
 * Copyright (c) 2013 Global Healthcare Exchange, LLC. All rights reserved.
 */

package com.ghx.auto.core.ui.support;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
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
	private WebDriver primaryDriver;
	private WebDriver secondaryDriver;
	private String parentWindow;
	private String popupWindow_1;
	private String popupWindow_2;
	private String secondaryParentWindow;
	private String secondaryPopupWindow_1;
	private String secondaryPopupWindow_2;
	
	public DriverContext(EnvConfig config, String env) {
		this.envConfig = config;
		this.env = env;
	}

	public WebDriver getPrimaryDriver() {
        if (this.primaryDriver == null) {
        	this.primaryDriver = createDriver();
        	this.parentWindow = primaryDriver.getWindowHandle();
        	maximize(this.primaryDriver);
        }	
        return primaryDriver;
	}
	
	public WebDriver getSecondaryDriver() {
        if (this.secondaryDriver == null) {
        	this.secondaryDriver = createDriver();
        	this.secondaryParentWindow = secondaryDriver.getWindowHandle();
        	maximize(this.secondaryDriver);
        }
        return secondaryDriver;
	}
	
	public WebDriver getParentWindow() {
		return primaryDriver.switchTo().window(this.parentWindow);
	}
	
	public WebDriver getSecondaryDriverParentWindow() {
		return secondaryDriver.switchTo().window(this.secondaryParentWindow);
	}
	
	public WebDriver getPopupWindow_1() {
		new WebDriverWait(primaryDriver, envConfig.getTimeout(), 100).until(new ExpectedCondition<Boolean> () {
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
		Set<String> windowHandles = primaryDriver.getWindowHandles();
		
		if (StringUtils.isBlank(this.popupWindow_1) || !(windowHandles.contains(this.popupWindow_1))) {
			for (String windowHandle : windowHandles) {
				if (!windowHandle.equals(this.parentWindow)) {
					this.popupWindow_1 = windowHandle;
				}
		    }
		}
		
		popup = primaryDriver.switchTo().window(this.popupWindow_1);
        Assert.assertNotNull(popup, "Unable to focus on pop-up window:, ");
        
        return  popup;
	}
	
	public void closePopupWindow_1() {
		if (StringUtils.isNotBlank(this.popupWindow_1)) {
			getPopupWindow_1().close();
			this.popupWindow_1 = null;
		}
	}
	
	public WebDriver getSecondaryDriverPopupWindow_1() {
		new WebDriverWait(secondaryDriver, envConfig.getTimeout(), 100).until(new ExpectedCondition<Boolean> () {
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
		Set<String> windowHandles = secondaryDriver.getWindowHandles();
		
		if (StringUtils.isBlank(this.secondaryPopupWindow_1) || !(windowHandles.contains(this.secondaryPopupWindow_1))) {
			for (String windowHandle : windowHandles) {
				if (!windowHandle.equals(this.secondaryParentWindow)) {
					this.secondaryPopupWindow_1 = windowHandle;
				}
		    }
		}
		
		popup = secondaryDriver.switchTo().window(this.secondaryPopupWindow_1);
        Assert.assertNotNull(popup, "Unable to focus on pop-up window:, ");
        
        return  popup;
	}
	
	public void closeSecondaryDriverPopupWindow_1() {
		if (StringUtils.isNotBlank(this.secondaryPopupWindow_1)) {
			getSecondaryDriverPopupWindow_1().close();
			this.secondaryPopupWindow_1 = null;
		}
	}
	
	public WebDriver getPopupWindow_2() {
		new WebDriverWait(primaryDriver, envConfig.getTimeout(), 100).until(new ExpectedCondition<Boolean> () {
    		@Override
    		public Boolean apply(WebDriver driver) {
    			Set<String> windowHandles = driver.getWindowHandles();
    			return (windowHandles.size() > 2 && !windowHandles.contains(""));
    		}
    		
    		@Override
	    	public String toString() {
	    		return String.format("focusing on the pop-up window");
	    	} 
    	});
		
		WebDriver popup = null;
		Set<String> windowHandles = primaryDriver.getWindowHandles();

		if (StringUtils.isBlank(this.popupWindow_2) || !(windowHandles.contains(this.popupWindow_2))) {
			for (String windowHandle : windowHandles) {
				if ((!windowHandle.equals(this.parentWindow)) && (!windowHandle.equals(this.popupWindow_1))) {
					this.popupWindow_2 = windowHandle;
				}
		    }
		}
		
		popup = primaryDriver.switchTo().window(this.popupWindow_2);
        Assert.assertNotNull(popup, "Unable to focus on pop-up window:, ");
        
        return  popup;
	}
	
	public void closePopupWindow_2() {
		if (StringUtils.isNotBlank(this.popupWindow_2)) {
			getPopupWindow_2().close();
			this.popupWindow_2 = null;
		}
	}
	
	public WebDriver getSecondaryDriverPopupWindow_2() {
		new WebDriverWait(secondaryDriver, envConfig.getTimeout(), 100).until(new ExpectedCondition<Boolean> () {
    		@Override
    		public Boolean apply(WebDriver driver) {
    			Set<String> windowHandles = driver.getWindowHandles();
    			return (windowHandles.size() > 2 && !windowHandles.contains(""));
    		}
    		
    		@Override
	    	public String toString() {
	    		return String.format("focusing on the pop-up window");
	    	} 
    	});
		
		WebDriver popup = null;
		Set<String> windowHandles = secondaryDriver.getWindowHandles();

		if (StringUtils.isBlank(this.secondaryPopupWindow_2) || !(windowHandles.contains(this.secondaryPopupWindow_2))) {
			for (String windowHandle : windowHandles) {
				if ((!windowHandle.equals(this.secondaryParentWindow)) && (!windowHandle.equals(this.secondaryPopupWindow_1))) {
					this.secondaryPopupWindow_2 = windowHandle;
				}
		    }
		}
		
		popup = secondaryDriver.switchTo().window(this.secondaryPopupWindow_2);
        Assert.assertNotNull(popup, "Unable to focus on pop-up window:, ");
        
        return  popup;
	}
	
	public void closeSecondaryDriverPopupWindow_2() {
		if (StringUtils.isNotBlank(this.secondaryPopupWindow_2)) {
			getSecondaryDriverPopupWindow_2().close();
			this.secondaryPopupWindow_2 = null;
		}
	}
	
  	public void quitDrivers() {
        quitPrimaryDriver();
        quitSecondaryDriver();
	}
  	
  	public void quitPrimaryDriver() {
        if (primaryDriver != null) {
        	primaryDriver.quit();
        	this.primaryDriver = null;
        	this.parentWindow = null;
        	this.popupWindow_1 = null;
        	this.popupWindow_2 = null;
        }
	}
  	
  	public void quitSecondaryDriver() {
        if (secondaryDriver != null) {
        	secondaryDriver.quit();
        	this.secondaryDriver = null;
        	this.secondaryParentWindow = null;
        	this.secondaryPopupWindow_1 = null;
        	this.secondaryPopupWindow_2 = null;
        }
	}
  	
	public boolean isPrimaryDriverExists() {
		return this.primaryDriver != null;
	}
	
	public boolean isSecondaryDriverExists() {
		return this.secondaryDriver != null;
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
                //desiredCapabilities.setCapability(InternetExplorerDriver.NATIVE_EVENTS, false);
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
        
        Assert.fail("Unable to create driver instance:, ");
        
        //this will not happen
        return null;
	}
  	
    private WebDriver maximize(WebDriver driver) {
        driver.manage().window().maximize();
        return driver;
    }

}
