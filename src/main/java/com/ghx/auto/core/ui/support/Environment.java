/*
 * Copyright (c) 2014 Global Healthcare Exchange, LLC. All rights reserved.
 */

package com.ghx.auto.core.ui.support;

public enum Environment {

	DEV("D"), DEVINT("DI"), STAGE("S"), INTEGRATION("I"), PRODUCTION("P");
	
	private String envId;
	
	private Environment(String envId) {
		this.envId = envId;
	}
	
	public String getEnvId() {
		return envId;
	}
	
}
