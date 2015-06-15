/*
 * Copyright (c) 2014 Global Healthcare Exchange, LLC. All rights reserved.
 */

package com.ghx.auto.core.ui.support;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.ini4j.Ini;
import org.ini4j.Profile.Section;
import org.testng.Assert;

public class EnvConfig {

    private Ini ini;

    private Section defaultSection;
    
    private String targetEnv;

    public EnvConfig(Ini ini) {
        this.ini = ini;
        this.defaultSection = ini.get("DEFAULT");
    }

    public String getInstance() {
        return this.defaultSection.get("instance", "local");
    }
    
    public int getTimeout() {
        return Integer.parseInt(this.defaultSection.get("timeout"));
    }

    public boolean isCloseBrowserOnFinish() {
        return Boolean.parseBoolean(this.defaultSection.get("closeBrowserOnFinish"));
    }
    
    public String getTests() {
        return this.defaultSection.get("tests");
    }
    
    public String getCloudUrl() {
        return this.defaultSection.get("cloudUrl");
    }
    
    public String getTestDataSource() {
        return this.defaultSection.get("testDataSource");
    }
    
    public void setTargetEnv(String targetEnv) {
        this.targetEnv = targetEnv;
    }
    
    public String getTargetEnv() {
        return this.targetEnv;
    }
    
    public Map<String, String> get(String section) {
        return this.ini.get(section);
    }
    
    public String getConfigParamValue(String section, String param) {
    	Map<String, String> configSection = get(section.toUpperCase());
    	Assert.assertNotNull(configSection, "No configuration section provided in configuration file with the name: " + section.toUpperCase());
    	String paramValue =  configSection.get(param);
    	Assert.assertNotNull(StringUtils.defaultIfBlank(paramValue, null),
                "No configuration provided for the param: " + param
                        + ",under section: " + section.toUpperCase());
    	return paramValue;
    }

}
