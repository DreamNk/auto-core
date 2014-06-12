/*
 * Copyright (c) 2014 Global Healthcare Exchange, LLC. All rights reserved.
 */

package com.ghx.auto.core.ui.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.testng.Assert;

/**
 * TODO
 */
public class Alert {

    private Logger logger = LoggerFactory.getLogger(getClass().getSimpleName());

    private String componentName;

    private String message;

    private Throwable exception;

    /**
     * Constructor.
     *
     * @param componentName Name of component requesting an alert.
     * @param message Alert message.
     */
    public Alert(String componentName, String message) {
        this.componentName = componentName;
        this.message = message;
    }

    /**
     * Constructor.
     *
     * @param componentName Name of component requesting an alert.
     * @param message Alert message.
     * @param exception Exception causing the alert to be sent.
     */
    public Alert(String componentName, String message, Throwable exception) {
        this.componentName = componentName;
        this.message = message;
        this.exception = exception;
    }

    /**
     * Allows dynaTrace to "listen" for an alert event.
     */
    public void send() {
        Marker marker = MarkerFactory.getMarker(this.componentName);
        this.logger.error(marker, this.message, this.exception);
        Assert.fail(this.componentName + " - " + this.message, this.exception);
    }

}
