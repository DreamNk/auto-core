package com.ghx.auto.core.reporting;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.testng.ISuite;
import org.testng.ISuiteResult;
import org.testng.ITestContext;
import org.testng.ITestResult;

public class SuiteResultsDataset {
	List<ISuite> suites;
	public Iterator<SuiteResults> itr;
	private List<SuiteResults> suiteResultsLst;
	
	public SuiteResultsDataset(List<ISuite> suites){
		this.suites = suites;
	}
	
	public SuiteResultsDataset(){
		
	}

	
	public List<SuiteResults> getSuiteResultsLst() {
		return suiteResultsLst;
	}
	public void setSuiteResultsLst(List<SuiteResults> suiteResultsLst) {
		this.suiteResultsLst = suiteResultsLst;
	}
	public void getSuiteResultsData(){
		
		suiteResultsLst = new ArrayList<>();
        DateFormat df = new SimpleDateFormat("HH:mm:ss");
        SuiteResults suiteData;
		//Iterating over each suite included in the test
        for (ISuite suite : suites) {
        	suiteData = new SuiteResults();
            String suiteName = suite.getName();
            int total = 0, passed = 0, failed = 0, skipped = 0;
            System.out.println("Suite name is: " + suiteName);
            suiteData.setSuiteName(suiteName);
           /* TODO suiteData.setSuiteDuration(suite.);
            suiteData.setSuiteEndTime(suiteEndTime);
            suiteData.setSuiteStartTime(suiteStartTime);
            */
	    //Getting the results for the said suite
	    Map<String, ISuiteResult> suiteResults = suite.getResults();
	    
        for (ISuiteResult sr : suiteResults.values()) {
	        ITestContext tc = sr.getTestContext();
	        Set<ITestResult> pass = tc.getPassedTests().getAllResults();
	        Set<ITestResult> fail = tc.getFailedTests().getAllResults();
	        Set<ITestResult> skip = tc.getSkippedTests().getAllResults();
	        
	        passed+=pass.size();
	        failed+=fail.size();
	        skipped+=skip.size();
	        total=passed+failed+skipped;

	        TestResults tstResult;
	        List<TestResults> tstResultLst = new ArrayList<>();
	        for (ITestResult tr : pass) {
	        	tstResult = new TestResults();
	            System.out.println("passed method: " + tr.getName());
	            System.out.println("Name: " + tr.getTestClass().getName());
	            System.out.println("Section: " + tr.getName());
	            System.out.println("Start time: " + df.format(new Date(tr.getStartMillis())));
	            System.out.println("End time: " + df.format(new Date(tr.getEndMillis())));
	            System.out.println("Duration (in secs): " + TimeUnit.MILLISECONDS.toSeconds(tr.getEndMillis()-tr.getStartMillis()));
	            tstResult.setTestName(tr.getTestClass().getName());
	            tstResult.setSectionName(tr.getName());
	            tstResult.setTestStartTime(df.format(new Date(tr.getStartMillis())));
	            tstResult.setTestEndTime(df.format(new Date(tr.getEndMillis())));
	            tstResult.setTestDuration(TimeUnit.MILLISECONDS.toSeconds(tr.getEndMillis()-tr.getStartMillis()));
	            
	            tstResultLst.add(tstResult);
	        }
	        suiteData.setPassedTests(tstResultLst);
	        
	        tstResultLst = new ArrayList<>();
	        for (ITestResult tr : fail) {
	        	tstResult = new TestResults();
	            System.out.println("Name: " + tr.getTestClass().getName());
	            System.out.println("Section: " + tr.getName());
	            StackTraceElement[] elements = tr.getThrowable().getStackTrace();
	            for(StackTraceElement ste : elements) {
	            	if (ste.getClassName().equalsIgnoreCase(tr.getTestClass().getName())) {
	    	            System.out.println("Line#: " + ste.getLineNumber());
	    	            tstResult.setErrlineNo(ste.getLineNumber());
	            	}
	            }
	            System.out.println("Error message: " + tr.getThrowable().getMessage());
	            System.out.println("Start time: " + df.format(new Date(tr.getStartMillis())));
	            System.out.println("End time: " + df.format(new Date(tr.getStartMillis())));
	            System.out.println("Duration (in secs): " + TimeUnit.MILLISECONDS.toSeconds(tr.getEndMillis()-tr.getStartMillis()));
	            
	            tstResult.setTestName(tr.getTestClass().getName());
	            tstResult.setSectionName(tr.getName());
	            tstResult.setTestStartTime(df.format(new Date(tr.getStartMillis())));
	            tstResult.setTestEndTime(df.format(new Date(tr.getEndMillis())));
	            tstResult.setTestDuration(TimeUnit.MILLISECONDS.toSeconds(tr.getEndMillis()-tr.getStartMillis()));
	            tstResult.setErrorMessage(tr.getThrowable().getMessage());
	            
	            tstResultLst.add(tstResult);

	        }
	        suiteData.setFailedTests(tstResultLst);
	        
	        
	        for (ITestResult tr : skip) {
	            System.out.println("skipped method: " + tr.getName());
	        }

	      }
        
        System.out.println("Total tests: " + total);
        System.out.println("#passed: " + passed);
        System.out.println("#failed: " + failed);
        System.out.println("#skipped: " + skipped);
        suiteData.setTotalTests(total);
        suiteData.setTotalFailed(failed);
        suiteData.setTotalPassed(passed);
        suiteData.setTotalSkipped(skipped);
        //Adding to list of suites
        suiteResultsLst.add(suiteData);
        }
	
	}
	
	public void open(Object obj, Map<String,Object> map) {
        System.out.println("content is:..." + obj.toString());
		suiteResultsLst = (List<SuiteResults>) map.get("myKey");
	}

	public Object next() {
		
	    if (itr == null)
	        itr = suiteResultsLst.iterator();
	    if (itr.hasNext())
	        return itr.next();
	    return null;
	}

	public void close() {
		
	}
}
