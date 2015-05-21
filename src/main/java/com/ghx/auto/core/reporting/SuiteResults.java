package com.ghx.auto.core.reporting;

import java.util.Date;
import java.util.List;

public class SuiteResults {

	private String suiteName;
	private Date suiteStartTime;
	private Date suiteEndTime;
	private int suiteDuration;
	private int totalTests;
	private int totalPassed;
	private int totalFailed;
	private int totalSkipped;
	private List<TestResults> passedTests;
	private List<TestResults> failedTests;
	
	public String getSuiteName() {
		return suiteName;
	}
	public void setSuiteName(String suiteName) {
		this.suiteName = suiteName;
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
	public int getSuiteDuration() {
		return suiteDuration;
	}
	public void setSuiteDuration(int suiteDuration) {
		this.suiteDuration = suiteDuration;
	}
	public int getTotalTests() {
		return totalTests;
	}
	public void setTotalTests(int totalTests) {
		this.totalTests = totalTests;
	}
	public int getTotalPassed() {
		return totalPassed;
	}
	public void setTotalPassed(int totalPassed) {
		this.totalPassed = totalPassed;
	}
	public int getTotalFailed() {
		return totalFailed;
	}
	public void setTotalFailed(int totalFailed) {
		this.totalFailed = totalFailed;
	}
	public int getTotalSkipped() {
		return totalSkipped;
	}
	public void setTotalSkipped(int totalSkipped) {
		this.totalSkipped = totalSkipped;
	}

	public List<TestResults> getPassedTests() {
		return passedTests;
	}
	public void setPassedTests(List<TestResults> passedTests) {
		this.passedTests = passedTests;
	}
	public List<TestResults> getFailedTests() {
		return failedTests;
	}
	public void setFailedTests(List<TestResults> failedTests) {
		this.failedTests = failedTests;
	}

}
