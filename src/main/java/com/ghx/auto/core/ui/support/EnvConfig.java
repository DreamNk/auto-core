/*
 * Copyright (c) 2014 Global Healthcare Exchange, LLC. All rights reserved.
 */

package com.ghx.auto.core.ui.support;

import java.util.Map;

import org.ini4j.Ini;
import org.ini4j.Profile.Section;

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

}
