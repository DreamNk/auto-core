package com.ghx.auto.core.ui.support;

import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;
import org.testng.Assert;

public class Utils {

	public static TreeSet<Integer> getTestSeqNums(String filter) {
    	String[] tokens = filter.split(",");
    	TreeSet<Integer> testSeqNums = new TreeSet<Integer>();
    	int startIndex;
    	int endIndex;
    	String[] testRange;
    	for(int j=0; j < tokens.length; j++){
	    	if(tokens[j].contains("-")){ //tests range say 2-6
	    	   testRange = tokens[j].split("-");
	    	   if (((testRange.length <= 1) || StringUtils.isBlank(testRange[0]) 
	    			   || ( StringUtils.isBlank(testRange[1])))) {
	    		   continue;
	    	   } 
	    	   else {
		    	    Assert.assertTrue((StringUtils.isNumeric(testRange[0]) && 
		    		       			   (StringUtils.isNumeric(testRange[1]))),"Tests range configured is not numeric, " );
	    		    startIndex = Integer.parseInt(testRange[0]);
    	   		    endIndex = Integer.parseInt(testRange[1]);
    	   			while(startIndex <= endIndex ){
    	   				  testSeqNums.add(startIndex++);
    	   			} // end of while
	    	   }
	    	}
	    	else {
	    	   if (StringUtils.isBlank(tokens[j])) {
	    		   continue;
	    	   }
		       Assert.assertTrue((StringUtils.isNumeric(tokens[j])),"Tests configured is not numeric, " );
	    	   testSeqNums.add(Integer.parseInt(tokens[j]));
	    	} // end of if-else for tests range
    	} // end of for loop
        return testSeqNums;	
    }
	
}
