/*
 * Copyright (c) 2014 Global Healthcare Exchange, LLC. All rights reserved.
 */

package com.ghx.auto.core.support.excel;



public class ExcelRow {
	
    private Object[] rowData;
    
    public ExcelRow(Object[] rowData) {
    	this.rowData = rowData;
    }
    
    public int get_column_int_value(int position) {
    	return ((Double)rowData[--position]).intValue();
    }
    
    public String get_column_string_value(int position) {
    	return (String)rowData[--position];
    }
    
    public boolean get_column_boolean_value(int position) {
    	return (boolean)rowData[--position];
    }

}
