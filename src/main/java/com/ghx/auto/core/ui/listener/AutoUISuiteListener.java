/*
 * Copyright (c) 2013 Global Healthcare Exchange, LLC. All rights reserved.
 */

package com.ghx.auto.core.ui.listener;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.ini4j.Ini;
import org.ini4j.Wini;
import org.testng.Assert;
import org.testng.ISuite;
import org.testng.ISuiteListener;

import com.ghx.auto.core.ui.support.EnvConfig;

public class AutoUISuiteListener implements ISuiteListener {

    @Override
    public void onStart(ISuite suite) {
        String envFileName = "env-config.ini";
        String mybatisConfig = "mybatis-config.xml";

        if (suite.getXmlSuite().getFileName().endsWith("local.xml")) {
            envFileName = "env-config-local.ini";
            mybatisConfig = "mybatis-config-local.xml";
        }
        
        String targetEnv = System.getProperty("TargetEnv");
        if (StringUtils.isBlank(targetEnv)) {
        	Assert.fail("Target Environment is not configured");
        }

        // load environment settings.
        try {
            Ini ini = new Wini(this.getClass().getResourceAsStream('/' + targetEnv + '/' + envFileName));
            EnvConfig envConfig = new EnvConfig(ini);
            envConfig.setTargetEnv(targetEnv);
            suite.setAttribute(EnvConfig.class.getName(), envConfig);
        }
        catch (IOException ex) { // also catches InvalidFormatException
            Assert.fail("Unable to load " + envFileName, ex);
        }

        suite.setAttribute("mybatisConfig", mybatisConfig);
    }

    @Override
    @SuppressWarnings("unused")
    public void onFinish(ISuite suite) {
        // nothing to do right now.
    }

}
