/*
 * Copyright (c) 2014 Global Healthcare Exchange, LLC. All rights reserved.
 */

package com.ghx.auto.core.support.excel;

import org.apache.commons.lang3.StringUtils;

import com.ibm.icu.impl.Assert;



public class ExcelRow {
	
    private Object[] rowData;
    
    public ExcelRow(Object[] rowData) {
    	this.rowData = rowData;
    }
    
    public int get_column_int_value(int position) {
    	validate_column_position(position);
    	return ((Double)rowData[--position]).intValue();
    }
    
    public String get_column_string_value(int position) {
    	validate_column_position(position);
    	return (String)rowData[--position];
    }
    
    public boolean get_column_boolean_value(int position) {
    	validate_column_position(position);
    	return (boolean)rowData[--position];
    }
    
    public int get_total_no_of_columns() {
    	return rowData.length;
    }
    
    public boolean does_column_contain_value(int position) {
    	validate_column_position(position);
        if(rowData[--position] == null)
           return Boolean.FALSE;
        return Boolean.TRUE;
     }
    
    private void validate_column_position(int position) {
    	if (position > rowData.length) {
    		Assert.fail("column referred does not exist in the excel sheet, column position is: " + position);
    	}
    }

}
