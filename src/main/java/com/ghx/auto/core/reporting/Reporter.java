package com.ghx.auto.core.reporting;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.testng.IReporter;
import org.testng.ISuite;
import org.testng.ISuiteResult;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.xml.XmlSuite;

public class Reporter implements IReporter {

	@Override
	public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> suites,
			String outputDirectory) {

        DateFormat df = new SimpleDateFormat("HH:mm:ss");
        
		//Iterating over each suite included in the test
        for (ISuite suite : suites) {
            String suiteName = suite.getName();
            int total = 0, passed = 0, failed = 0, skipped = 0;
            System.out.println("Suite name is: " + suiteName);

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

	        for (ITestResult tr : pass) {
	            System.out.println("passed method: " + tr.getName());
	            System.out.println("Name: " + tr.getTestClass().getName());
	            System.out.println("Section: " + tr.getName());
	            System.out.println("Start time: " + df.format(new Date(tr.getStartMillis())));
	            System.out.println("End time: " + df.format(new Date(tr.getStartMillis())));
	            System.out.println("Duration (in secs): " + TimeUnit.MILLISECONDS.toSeconds(tr.getEndMillis()-tr.getStartMillis()));
	        }
	        
	        for (ITestResult tr : fail) {
	            System.out.println("Name: " + tr.getTestClass().getName());
	            System.out.println("Section: " + tr.getName());
	            StackTraceElement[] elements = tr.getThrowable().getStackTrace();
	            for(StackTraceElement ste : elements) {
	            	if (ste.getClassName().equalsIgnoreCase(tr.getTestClass().getName())) {
	    	            System.out.println("Line#: " + ste.getLineNumber());
	            	}
	            }
	            System.out.println("Error message: " + tr.getThrowable().getMessage());
	            System.out.println("Start time: " + df.format(new Date(tr.getStartMillis())));
	            System.out.println("End time: " + df.format(new Date(tr.getStartMillis())));
	            System.out.println("Duration (in secs): " + TimeUnit.MILLISECONDS.toSeconds(tr.getEndMillis()-tr.getStartMillis()));

	        }
	        
	        for (ITestResult tr : skip) {
	            System.out.println("skipped method: " + tr.getName());
	        }

	      }
        System.out.println("Total tests: " + total);
        System.out.println("#passed: " + passed);
        System.out.println("#failed: " + failed);
        System.out.println("#skipped: " + skipped);
        
        }
	}

}
