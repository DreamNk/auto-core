/*
 * Copyright (c) 2013 Global Healthcare Exchange, LLC. All rights reserved.
 */

package com.ghx.auto.core.support;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;

import org.apache.commons.lang3.StringUtils;
import org.testng.Assert;

import com.ghx.auto.core.ui.support.EnvConfig;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

public class FileTransfer {
	
    private EnvConfig envConfig;
    private String section;
    
    public FileTransfer(EnvConfig envConfig, String section) {
    	this.envConfig = envConfig;
    	this.section = section;
    }
    
	public FileTransfer send_file_via_sFtp(File inFile,String destFileName) {
		create_send_file_via_sFtp(inFile.toString(),destFileName);
		return this;
	}
	
	public FileTransfer create_send_file_via_sFtp(String inFile,String destFileName) {

		InputStream stream = new ByteArrayInputStream(inFile.getBytes());

		Session mySession = null;
		Channel myChannel = null;
		ChannelSftp myChannelSftp = null;

		try {
			JSch jsch = new JSch();
			mySession = jsch.getSession(getConfigParamValue(section,"userName"), getConfigParamValue(section,"host"));
			mySession.setPassword(getConfigParamValue(section,"password"));
			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			mySession.setConfig(config);
			mySession.setConfig("PreferredAuthentications", "publickey,keyboard-interactive,password");
			mySession.connect();
			myChannel = mySession.openChannel("sftp");
			myChannel.connect();
			myChannelSftp = (ChannelSftp) myChannel;
			myChannelSftp.cd(getConfigParamValue(section,"destFilePath"));
			myChannelSftp.put(stream, destFileName);

		} catch (Exception ex) {
			Assert.fail("Failure uploading file " + destFileName + " to host " + getConfigParamValue(section,"host") + ".");
		} finally {
			if (myChannel != null) {
				if (myChannel.isConnected()) {
					myChannel.disconnect();
				}
			}
		}
		return this;
	}
	
	public FileTransfer store_file_to_local(File inFile,String destFileName) {
		create_send_file_via_sFtp(inFile.toString(),destFileName);
		return this;
	}
	
	public FileTransfer create_store_file_to_local(String inFile, String destFileName) {
		Writer output = null;
		File file = new File(getConfigParamValue(section,"destFilePath") + destFileName);
		try {
			output = new BufferedWriter(new FileWriter(file));
			output.write(inFile);
			output.close();
		} catch (IOException e) {
			Assert.fail("Failure uploading file " + destFileName + " to local system, path: " + getConfigParamValue(section,"destFilePath") + ".");
		}
		return this;
	}
	
    protected String getConfigParamValue(String section, String param) {
    	String paramValue =  this.envConfig.get(section.toUpperCase()).get(param);
    	Assert.assertNotNull(StringUtils.defaultIfBlank(paramValue, null),
                "No configuration provided for the param: " + param
                        + ",under section: " + section.toUpperCase());
    	return paramValue;
    }

}
