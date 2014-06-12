/*
 * Copyright (c) 2013 Global Healthcare Exchange, LLC. All rights reserved.
 */

package com.ghx.auto.core.ui.support;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.Assert;

public class DriverContext {
	
	private EnvConfig envConfig;
	private String env;
	private WebDriver driver;
	private WebDriver secondaryDriver;

	public DriverContext(EnvConfig config, String env) {
		this.envConfig = config;
		this.env = env;
		this.driver = createDriver();
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
	
	public void quitDrivers() {
        if (secondaryDriver != null) {
        	secondaryDriver.quit();
        }
        if (driver != null) {
        	driver.quit();
        }
	}
}
