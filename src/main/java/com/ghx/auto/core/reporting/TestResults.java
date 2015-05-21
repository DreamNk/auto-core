package com.ghx.auto.core.reporting;


public class TestResults {

	private String testName;
	private String sectionName;
	private int errlineNo;
	private String errorMessage;
	private String screenshotLink;
	private String testStartTime;
	
	public String getTestStartTime() {
		return testStartTime;
	}
	public String getTestEndTime() {
		return testEndTime;
	}
	private String testEndTime;
	private long testDuration;
	
	public void setTestStartTime(String testStartTime) {
		this.testStartTime = testStartTime;
	}
	public void setTestEndTime(String testEndTime) {
		this.testEndTime = testEndTime;
	}
	public String getTestName() {
		return testName;
	}
	public void setTestName(String testName) {
		this.testName = testName;
	}
	public String getSectionName() {
		return sectionName;
	}
	public void setSectionName(String sectionName) {
		this.sectionName = sectionName;
	}
	public int getErrlineNo() {
		return errlineNo;
	}
	public void setErrlineNo(int errlineNo) {
		this.errlineNo = errlineNo;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public String getScreenshotLink() {
		return screenshotLink;
	}
	public void setScreenshotLink(String screenshotLink) {
		this.screenshotLink = screenshotLink;
	}
	public long getTestDuration() {
		return testDuration;
	}
	public void setTestDuration(long testDuration) {
		this.testDuration = testDuration;
	}
	
}
