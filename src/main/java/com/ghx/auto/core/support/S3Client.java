package com.ghx.auto.core.support;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.testng.Assert;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.ghx.auto.core.ui.support.EnvConfig;

public class S3Client {
	private EnvConfig envConfig;
	private String section;
	private AmazonS3 conn;

	public S3Client(EnvConfig envConfig, String section) {
		this.envConfig = envConfig;
		this.section = section;
		createS3Connetion();
	}

	public String read_file_from_s3(String bucket, String fileName) {
		S3Object object = conn.getObject(new GetObjectRequest(bucket, fileName));
		InputStream objectData = object.getObjectContent();
		try {
			return IOUtils.toString(objectData, "ISO-8859-1");
		} catch (IOException ie) {
			Assert.fail("Failure reading the file from the bucket: " + bucket + ", file name:  " + fileName
					+ ", error message: " + ie.getMessage());
		} finally {
			try {
				objectData.close();
			} catch (IOException ex) {
				Assert.fail("Failed closing InputStream while reading the file: " + fileName + ", from S3 error message: "
						+ ex.getMessage());
			}
		}
		return null; //should not happen
	}

	public void write_file_to_s3(String bucketName, String fileName, String content) {
		ObjectMetadata metadata = new ObjectMetadata();
		try {
			InputStream is = IOUtils.toInputStream(content, "UTF-8");
			metadata.setContentLength(Long.valueOf(is.available()));
			conn.putObject(bucketName, fileName, is, metadata);
			is.close();
		} catch (AmazonServiceException ase) {
			Assert.fail("Unable to upload file into the bucket: " + bucketName + ", file: " + fileName + " , error message: "
					+ ase.getMessage());
		} catch (AmazonClientException ace) {
			Assert.fail("Unable to upload file due to some internal error, bucket: "
					+ bucketName + " , file:" + fileName + " , error message: " + ace.getMessage());
		} catch (IOException ie) {
			Assert.fail("Unable to upload file due to some internal error, bucket: "
					+ bucketName + ", file:" + fileName + " , error message: " + ie.getMessage());
		}
	}

	private void createS3Connetion() {
		AWSCredentials credentials = new BasicAWSCredentials(getConfigParamValue("accessKey"),
				getConfigParamValue("secretKey"));
		ClientConfiguration clientConfig = new ClientConfiguration();
		clientConfig.setProtocol(Protocol.HTTPS);
		conn = new AmazonS3Client(credentials, clientConfig);
		//conn.setEndpoint(getConfigParamValue("baseUrl"));
	}

	private String getConfigParamValue(String param) {
		return getConfigParamValue(this.section, param);
	}

	private String getConfigParamValue(String section, String param) {
		return this.envConfig.getConfigParamValue(section, param);
	}

}
