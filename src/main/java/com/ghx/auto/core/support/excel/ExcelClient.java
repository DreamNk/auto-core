/*
 * Copyright (c) 2014 Global Healthcare Exchange, LLC. All rights reserved.
 */

package com.ghx.auto.core.support.excel;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.testng.Assert;

import com.ghx.auto.core.ui.support.EnvConfig;

public class ExcelClient {
	
    private EnvConfig envConfig;
    private String section;
    private Map<Integer, ExcelRow> sheet;
    
    public ExcelClient(EnvConfig envConfig, String section) {
    	this.envConfig = envConfig;
    	this.section = section;
    	try {
			loadSheet(new FileInputStream(getConfigParamValue("path")), getConfigParamValue("sheetName"));
		} catch (FileNotFoundException fnf) {
			Assert.fail("Unable to find the configured excel sheet, path: " + getConfigParamValue("path"));
		}
    }
    
    public Object[][] get_all_rows() {
    	Object[][] array = new Object[sheet.size()][2];
    	int count = 0;
    	for(Map.Entry<Integer,ExcelRow> entry : sheet.entrySet()){
    	    array[count][0] = entry.getKey();
    	    array[count][1] = entry.getValue();
    	    count++;
    	}
    	return array;
    }
    
    public Object[][] get_all_rows_except_header() {
        return get_multiple_rows(2, sheet.size());  //assuming the header on row 1
    }
    
    public Map<Integer, ExcelRow> get_all_rows_as_map() {
    	return sheet;
    }
    
    public Object[][] get_single_row(int row) {
    	validate_row_no(row);
    	Object[][] array = new Object[1][2];
    	array[0][0] = row;
    	array[0][1] = sheet.get(row);
    	return array;
    }
    
    public ExcelRow get_single_row_as_obj(int row) {
    	validate_row_no(row);
    	return sheet.get(row);
    }
    
    public Object[][] get_multiple_rows(int rowBegin, int rowEnd) {
      	Object[][] array = new Object[(rowEnd-rowBegin)+1][2];
    	int count = 0;
    	for(Map.Entry<Integer,ExcelRow> entry : sheet.entrySet()){
    		if ((entry.getKey() >= rowBegin) && (entry.getKey() <= rowEnd)) {
    			array[count][0] = entry.getKey();
        	    array[count][1] = entry.getValue();
        	    count++;
    		}
    	}
    	return array;
    }
    
    public Map<Integer, ExcelRow> get_multiple_rows_as_map(int rowBegin, int rowEnd) {
    	Map<Integer, ExcelRow> map = new HashMap<Integer, ExcelRow>();
    	for(Map.Entry<Integer,ExcelRow> entry : sheet.entrySet()){
    		if ((entry.getKey() >= rowBegin) && (entry.getKey() <= rowEnd)) {
    			map.put(entry.getKey(), entry.getValue());
    		}
    	}
    	return map;
    }
    
    public int get_total_no_of_rows() {
    	return sheet.size();
    }
    
    private void validate_row_no(int row) {
    	if (!sheet.containsKey(row)) {
    		Assert.fail("row does not exist in the excel sheet, row no. is: " + row);
    	}
    }
    
    private void loadSheet(InputStream content, String sheetName) {
    	Map<Integer, ExcelRow> sheetData = new HashMap<Integer, ExcelRow>();
    	String headerArray[] = new String[1];
    	try {
    		XSSFWorkbook workbook = new XSSFWorkbook(content);
            XSSFSheet sheet = workbook.getSheet(sheetName);
       	 	
            Assert.assertNotNull(sheet, "Configured excel sheet is not existing, path: " + sheetName);
            int firstRowNumber = sheet.getFirstRowNum();
            Iterator<Row> rowIterator = sheet.iterator();
            while (rowIterator.hasNext()) {
            	Row row = rowIterator.next();
            	Iterator<Cell> cellIterator = row.cellIterator();
            	
            	Object[] rowData = new Object[row.getLastCellNum()];
                while (cellIterator.hasNext()) {
                	Cell cell = cellIterator.next();
                    switch (cell.getCellType()) {
                        case Cell.CELL_TYPE_NUMERIC:
                        	rowData[cell.getColumnIndex()] = cell.getNumericCellValue();
                            break;
                        case Cell.CELL_TYPE_STRING:
                        	rowData[cell.getColumnIndex()] = cell.getStringCellValue();
                            break;
                        case Cell.CELL_TYPE_BLANK:
                        	//rowData[cell.getColumnIndex()] = "BLANK";
                            break;    
                        case Cell.CELL_TYPE_BOOLEAN:
                        	rowData[cell.getColumnIndex()] = cell.getBooleanCellValue();
                            break;
                    } //end of switch case
                } //loop for cells in a row
                if (row.getRowNum() == firstRowNumber) {
					headerArray = Arrays.copyOf(rowData, rowData.length, String[].class);
				}
				sheetData.put(row.getRowNum() + 1, new ExcelRow(rowData, headerArray));  
                
             } // loop for rows
           	 }catch (IOException ioe) {
           		Assert.fail("Unable to load the configured excel sheet, path: " + sheetName);
         }
    	
    	Assert.assertFalse(sheetData.isEmpty(), "Configured excel sheet is empty, path: " + sheetName);
    	 this.sheet = sheetData; 
    }
    
    private String getConfigParamValue(String param) {
    	return getConfigParamValue(this.section,param);
    }
    
    private String getConfigParamValue(String section, String param) {
    	return this.envConfig.getConfigParamValue(section, param);
    }

}
