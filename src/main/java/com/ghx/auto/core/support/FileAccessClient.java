/*
 * Copyright (c) 2013 Global Healthcare Exchange, LLC. All rights reserved.
 */

package com.ghx.auto.core.support;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Map;

import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.utils.IOUtils;
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
    
	public FileAccessClient upload_file_via_sFtp(File srcFile,String destFileName) {
		String srcFileStr = "";
		try {
			srcFileStr = FileUtils.readFileToString(srcFile);
		} catch (IOException e) {
			Assert.fail("Failure reading file " + srcFile.getName() + " from path: " + srcFile.getPath() + ".");
		}
		return upload_file_via_sFtp(srcFileStr,destFileName);
	}
	
	public FileAccessClient upload_file_via_sFtp(String srcFile,String destFileName) {

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
	
	public FileAccessClient upload_file_via_ftp(File srcFile,String destFileName) {
		String content = "";
		try {
			content = FileUtils.readFileToString(srcFile);
		} catch (IOException e) {
			Assert.fail("Failure reading file " + srcFile.getName() + " from path: " + srcFile.getPath() + ".");
		}
		return upload_file_via_ftp(content,destFileName);
	}
	
	public FileAccessClient upload_file_via_ftp(String content,String destFileName) {
		Boolean loginSuccess = Boolean.FALSE;
		Boolean changeDirSuccess = Boolean.FALSE;
		Boolean storeFileSuccess = Boolean.FALSE;

		FTPClient client = new FTPClient();
		
		try {
			client.connect(getConfigParamValue("host"));
			client.enterLocalPassiveMode();
			loginSuccess = client.login(getConfigParamValue("userName"), getConfigParamValue("password"));
			if (!loginSuccess) {
				Assert.fail("Login failed to the host: " + getConfigParamValue("host") + " with the user: " + getConfigParamValue("userName"));
			}
			
			changeDirSuccess = client.changeWorkingDirectory(getConfigParamValue("destFilePath"));
			if (!changeDirSuccess) {
				Assert.fail("Unable to change the directory to: " + getConfigParamValue("destFilePath") + " on the host: " + getConfigParamValue("host"));
			}
			
			storeFileSuccess = client.storeFile(destFileName, new ByteArrayInputStream(content.getBytes()));
			if (!storeFileSuccess) {
				Assert.fail("Unable to transfer the file to the directory: " + getConfigParamValue("destFilePath") + " on the host: " + getConfigParamValue("host"));
			}
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
		return this;
	}
	
	public FileAccessClient download_file_via_ftp(String srcFileName) {
		Boolean loginSuccess = Boolean.FALSE;
		Boolean changeDirSuccess = Boolean.FALSE;

		FTPClient client = new FTPClient();
		try {
			client.connect(getConfigParamValue("host"));
			client.enterLocalPassiveMode();
			
			loginSuccess = client.login(getConfigParamValue("userName"), getConfigParamValue("password"));
			if (!loginSuccess) {
				Assert.fail("Login failed to the host: " + getConfigParamValue("host") + " with the user: " + getConfigParamValue("userName"));
			}
				
			changeDirSuccess = client.changeWorkingDirectory(getConfigParamValue("srcFilePath"));
			if (!changeDirSuccess) {
				Assert.fail("Unable to change the directory to: " + getConfigParamValue("srcFilePath") + " on the host: " + getConfigParamValue("host"));
			}
				
			InputStream inputStream = client.retrieveFileStream(srcFileName);
			if(inputStream != null){
				FileUtils.copyInputStreamToFile(inputStream, new File(getConfigParamValue("destFilePath") + FILE_PATH_APPENDER + srcFileName));
			}else{
				Assert.fail("Unable to download the file to the directory: " + getConfigParamValue("destFilePath"));
			}
			
		} catch (IOException ioe) {
			Assert.fail("Failure connecting to the host: " + getConfigParamValue("host"));
		} finally {
			try {
				if(client.isConnected()) 
					client.disconnect();
			} catch (IOException ioe) {
					Assert.fail("Unable to disconnect the host: " + getConfigParamValue("host"));
			}
		}
		return this;
	}
	
	public FileAccessClient delete_file_on_ftp(String fileName) {
		Boolean loginSuccess = Boolean.FALSE;
		Boolean changeDirSuccess = Boolean.FALSE;
		Boolean deleteFileSuccess = Boolean.FALSE;


		FTPClient client = new FTPClient();
		try {
			client.connect(getConfigParamValue("host"));
			client.enterLocalPassiveMode();
			
			loginSuccess = client.login(getConfigParamValue("userName"), getConfigParamValue("password"));
			if (!loginSuccess) {
				Assert.fail("Login failed to the host: " + getConfigParamValue("host") + " with the user: " + getConfigParamValue("userName"));
			}
				
			changeDirSuccess = client.changeWorkingDirectory(getConfigParamValue("destFilePath"));
			if (!changeDirSuccess) {
				Assert.fail("Unable to change the directory to: " + getConfigParamValue("destFilePath") + " on the host: " + getConfigParamValue("host"));
			}
				
			deleteFileSuccess = client.deleteFile(fileName);
			if (!deleteFileSuccess) {
				Assert.fail("File not found, path is: " + getConfigParamValue("destFilePath") + FILE_PATH_APPENDER + 
									fileName + " , host: " + getConfigParamValue("host"));
			}
			
		} catch (IOException ioe) {
			Assert.fail("Failure connecting to the host: " + getConfigParamValue("host"));
		} finally {
			try {
				if(client.isConnected()) 
					client.disconnect();
			} catch (IOException ioe) {
					Assert.fail("Unable to disconnect the host: " + getConfigParamValue("host"));
			}
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
	
	public byte[] read_local_fileToByteArray(String srcFilePath, String srcFileName) {
		File srcFile = new File(srcFilePath + FILE_PATH_APPENDER + srcFileName);
		try {
			return FileUtils.readFileToByteArray(srcFile);
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
	
	public FileAccessClient compare_files_content(File file1,File file2) {
		boolean contentEquals = Boolean.FALSE;
		try {
			contentEquals = FileUtils.contentEquals(file1, file2);
		} catch (IOException e) {
			Assert.fail("Failure comparing files: " + file1.getName() + " and " + file2.getName());
		}
		Assert.assertTrue(contentEquals, "Content of the files " + file1.getName() + " and "+ file2.getName() + " is not same");
		return this;
	}
	
	
	public FileAccessClient zip_string(String strToZip, String zipFilePath, String zipFileName, String zipFileEntry) {
		try {
			OutputStream zip_output = new FileOutputStream(new File(zipFilePath + FILE_PATH_APPENDER + zipFileName));
            ArchiveOutputStream logical_zip = new ArchiveStreamFactory().createArchiveOutputStream(ArchiveStreamFactory.ZIP, zip_output);

			logical_zip.putArchiveEntry(new ZipArchiveEntry(zipFileEntry));
            IOUtils.copy(org.apache.commons.io.IOUtils.toInputStream(strToZip), logical_zip);
            logical_zip.closeArchiveEntry();
            logical_zip.finish(); 
            zip_output.close();
		} catch (IOException e) {
			Assert.fail("Unable to create zip file: " + zipFilePath + FILE_PATH_APPENDER + zipFileName);
		}catch (ArchiveException e) {
			Assert.fail("Failure compressing String(zip): " + strToZip);
		}
		
		return this;
	}
	
	public FileAccessClient zip_file(String path, String srcFileName, String zipFileName) {

		try {
			OutputStream zip_output = new FileOutputStream(new File(path + FILE_PATH_APPENDER + zipFileName));
            ArchiveOutputStream logical_zip = new ArchiveStreamFactory().createArchiveOutputStream(ArchiveStreamFactory.ZIP, zip_output);

			logical_zip.putArchiveEntry(new ZipArchiveEntry(srcFileName));
            IOUtils.copy(new FileInputStream(new File(path + FILE_PATH_APPENDER + srcFileName)), logical_zip);
            logical_zip.closeArchiveEntry();
            logical_zip.finish(); 
            zip_output.close();
            
		} catch (IOException e) {
			Assert.fail("Unable to find the file specified: " + path + FILE_PATH_APPENDER + srcFileName);
		}catch (ArchiveException e) {
			Assert.fail("Failure compressing file(zip): " + path + FILE_PATH_APPENDER + srcFileName);
		}
		
		return this;
		
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
