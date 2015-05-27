package com.ghx.auto.core.reporting.data;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class FailedTestResultsDataset {
	private Iterator<TestResults> itr;
	private List<TestResults> failedTestResultsLst;
	
	@SuppressWarnings("unchecked")
	public void open(Object obj, Map<String,Object> map) {
		this.failedTestResultsLst = (List<TestResults>) map.get("failedTests");
	}

	public Object next() {
		if (itr == null)
	    	itr = failedTestResultsLst.iterator();
	    if (itr.hasNext())
	        return itr.next();
	    
	    return null;
	}

	public void close() {

	}
}
