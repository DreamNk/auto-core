package com.ghx.auto.core.reporting;

import java.util.Date;

public class SuitResults {

	private String suiteName;
	int totalTests; 					
	private String testSection;
    Date suiteStartTime;
    Date suiteEndTime;
    Date suiteDuration;
	int noPassed;					
	int noFailed;
	
	public String getSuiteName() {
		return suiteName;
	}
	public void setSuiteName(String suiteName) {
		this.suiteName = suiteName;
	}
	public int getTotalTests() {
		return totalTests;
	}
	public void setTotalTests(int totalTests) {
		this.totalTests = totalTests;
	}
	public String getTestSection() {
		return testSection;
	}
	public void setTestSection(String testSection) {
		this.testSection = testSection;
	}
	public Date getSuiteStartTime() {
		return suiteStartTime;
	}
	public void setSuiteStartTime(Date suiteStartTime) {
		this.suiteStartTime = suiteStartTime;
	}
	public Date getSuiteEndTime() {
		return suiteEndTime;
	}
	public void setSuiteEndTime(Date suiteEndTime) {
		this.suiteEndTime = suiteEndTime;
	}
	public Date getSuiteDuration() {
		return suiteDuration;
	}
	public void setSuiteDuration(Date suiteDuration) {
		this.suiteDuration = suiteDuration;
	}
	public int getNoPassed() {
		return noPassed;
	}
	public void setNoPassed(int noPassed) {
		this.noPassed = noPassed;
	}
	public int getNoFailed() {
		return noFailed;
	}
	public void setNoFailed(int noFailed) {
		this.noFailed = noFailed;
	}

	
}
