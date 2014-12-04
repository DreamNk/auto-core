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
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.testng.Assert;

import com.ghx.auto.core.ui.support.EnvConfig;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

public class FileAccessClient {
	
	public static final String FILE_PATH_APPENDER = "/"; 
	
    private EnvConfig envConfig;
    private String section;
    
    public FileAccessClient(EnvConfig envConfig, String section) {
    	this.envConfig = envConfig;
    	this.section = section;
    }
    
	public FileAccessClient send_file_via_sFtp(File srcFile,String destFileName) {
		String srcFileStr = "";
		try {
			srcFileStr = FileUtils.readFileToString(srcFile);
		} catch (IOException e) {
			Assert.fail("Failure reading file " + srcFile.getName() + " from path: " + srcFile.getPath() + ".");
		}
		return send_file_via_sFtp(srcFileStr,destFileName);
	}
	
	public FileAccessClient send_file_via_sFtp(String srcFile,String destFileName) {

		InputStream stream = new ByteArrayInputStream(srcFile.getBytes());

		Session mySession = null;
		Channel myChannel = null;
		ChannelSftp myChannelSftp = null;

		try {
			JSch jsch = new JSch();
			mySession = jsch.getSession(getConfigParamValue("userName"), getConfigParamValue("host"));
			mySession.setPassword(getConfigParamValue("password"));
			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			config.put("PreferredAuthentications", "publickey,keyboard-interactive,password");
			mySession.setConfig(config);
			mySession.connect();
			myChannel = mySession.openChannel("sftp");
			myChannel.connect();
			myChannelSftp = (ChannelSftp) myChannel;
			myChannelSftp.cd(getConfigParamValue("destFilePath"));
			myChannelSftp.put(stream, destFileName);

		} catch (Exception ex) {
			Assert.fail("Failure uploading file " + destFileName + " to host " + getConfigParamValue("host") + ".");
		} finally {
			if (myChannel != null) {
				if (myChannel.isConnected()) {
					myChannel.disconnect();
				}
			}
		}
		return this;
	}
	
	public FileAccessClient send_file_via_ftp(File srcFile,String destFileName) {
		String content = "";
		try {
			content = FileUtils.readFileToString(srcFile);
		} catch (IOException e) {
			Assert.fail("Failure reading file " + srcFile.getName() + " from path: " + srcFile.getPath() + ".");
		}
		return send_file_via_ftp(content,destFileName);
	}
	
	public FileAccessClient send_file_via_ftp(String content,String destFileName) {
		Boolean loginSuccess = Boolean.FALSE;
		Boolean changeDirSuccess = Boolean.FALSE;
		Boolean storeFileSuccess = Boolean.FALSE;

		FTPClient client = new FTPClient();
		
		try {
			client.connect(getConfigParamValue("host"));
			client.enterLocalPassiveMode();
			loginSuccess = client.login(getConfigParamValue("userName"), getConfigParamValue("password"));
			changeDirSuccess = client.changeWorkingDirectory(getConfigParamValue("destFilePath"));
			storeFileSuccess = client.storeFile(destFileName, new ByteArrayInputStream(content.getBytes()));
		} catch (IOException ioe) {
			Assert.fail("Failure connecting to the host: " + getConfigParamValue("host"));
		} finally {
			if(client.isConnected()) {
				try {
					client.disconnect();
				} catch (IOException ioe) {
					Assert.fail("Unable to disconnect the host: " + getConfigParamValue("host"));
				}
			}
		}
		
		if (!loginSuccess) {
			Assert.fail("Login failed to the host: " + getConfigParamValue("host") + " with the user: " + getConfigParamValue("userName"));
		}
		
		if (!changeDirSuccess) {
			Assert.fail("Unable to change the directory to: " + getConfigParamValue("destFilePath") + " on the host: " + getConfigParamValue("host"));
		}
		
		if (!storeFileSuccess) {
			Assert.fail("Unable to transfer the file to the directory: " + getConfigParamValue("destFilePath") + " on the host: " + getConfigParamValue("host"));
		}
		
		return this;
	}
	
	public FileAccessClient store_file_to_local(File srcFile,String destFileName) {
		File destFile = new File(getConfigParamValue("destFilePath") + FILE_PATH_APPENDER + destFileName);
		try {
			FileUtils.copyFile(srcFile, destFile);
		} catch (IOException e) {
			Assert.fail("Failure uploading file " + destFileName + ", path: " + getConfigParamValue("destFilePath") + ".");
		}
		return this;
	}
	
	public FileAccessClient store_file_to_local(String srcFile, String destFileName) {
		Writer output = null;
		File file = new File(getConfigParamValue("destFilePath") + FILE_PATH_APPENDER + destFileName);
		try {
			output = new BufferedWriter(new FileWriter(file));
			output.write(srcFile);
			output.close();
		} catch (IOException e) {
			Assert.fail("Failure uploading file " + destFileName + ", path: " + getConfigParamValue("destFilePath") + ".");
		}
		return this;
	}
	
	public String read_local_file(String srcFileName) {
		return read_local_file(getConfigParamValue("srcFilePath"), srcFileName);
	}
	
	public String read_local_file(String srcFilePath, String srcFileName) {
		File srcFile = new File(srcFilePath + FILE_PATH_APPENDER + srcFileName);
		try {
			return FileUtils.readFileToString(srcFile);
		} catch (IOException e) {
			Assert.fail("Failure reading file " + srcFileName + ", path: " + srcFilePath + ".");
		}
		return null; // this should not happen
	}
	
	public String replace_local_file_content(String srcFileName, String searchString, String replaceString) {
		return replace_local_file_content(getConfigParamValue("srcFilePath"), srcFileName, searchString, replaceString);
	}
	
	public String replace_local_file_content(String srcFilePath, String srcFileName, String searchString, String replaceString) {
		File srcFile = new File(srcFilePath + FILE_PATH_APPENDER + srcFileName);
		try {
			return StringUtils.replace(FileUtils.readFileToString(srcFile), searchString, replaceString);
		} catch (IOException e) {
			Assert.fail("Failure reading file " + srcFileName + ", path: " + srcFilePath + ".");
		}
		
		return null; // this should not happen
	}
	
    private String getConfigParamValue(String param) {
    	Map<String, String> section = this.envConfig.get(this.section.toUpperCase());
    	Assert.assertNotNull(section, "No configuration section provided in configuration file with the name: " + this.section.toUpperCase());
    	String paramValue =  section.get(param);
    	Assert.assertNotNull(StringUtils.defaultIfBlank(paramValue, null),
                "No configuration provided for the param: " + param
                        + ",under section: " + this.section.toUpperCase());
    	return paramValue;
    }

}
