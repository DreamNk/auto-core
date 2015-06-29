/*
 * Copyright (c) 2014 Global Healthcare Exchange, LLC. All rights reserved.
 */

package com.ghx.auto.core.support.excel;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
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
    	loadSheet();
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
    
    public Map<Integer, ExcelRow> get_all_rows_as_map() {
    	return sheet;
    }
    
    public Object[][] get_single_row(int row) {
    	Object[][] array = new Object[1][2];
    	array[0][0] = row;
    	array[0][1] = sheet.get(row);
    	return array;
    }
    
    public ExcelRow get_single_row_as_obj(int row) {
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
    
    private void loadSheet() {
    	Map<Integer, ExcelRow> sheetData = new HashMap<Integer, ExcelRow>();
    	try {
    		FileInputStream file = new FileInputStream(getConfigParamValue("path"));
            XSSFWorkbook workbook = new XSSFWorkbook(file);
            XSSFSheet sheet = workbook.getSheet(getConfigParamValue("sheetName"));
       	 	
            Assert.assertNotNull(sheet, "Configured excel sheet is not existing, path: " + getConfigParamValue("path"));

            Iterator<Row> rowIterator = sheet.iterator();
            while (rowIterator.hasNext()) {
            	Row row = rowIterator.next();
            	Iterator<Cell> cellIterator = row.cellIterator();
            	
            	Object[] rowData = new Object[row.getLastCellNum()-row.getFirstCellNum()];
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
                        	rowData[cell.getColumnIndex()] = "BLANK";
                            break;    
                        case Cell.CELL_TYPE_BOOLEAN:
                        	rowData[cell.getColumnIndex()] = cell.getBooleanCellValue();
                            break;
                    } //end of switch case
                } //loop for cells in a row
                sheetData.put(row.getRowNum()+1, new ExcelRow(rowData));   
                
             } // loop for rows
             file.close();
    	 } catch (FileNotFoundException fnf) {
         	 Assert.fail("Unable to find the configured excel sheet, path: " + getConfigParamValue("path"));
         } catch (IOException ioe) {
        	 Assert.fail("Unable to load the configured excel sheet, path: " + getConfigParamValue("path"));
         }
    	
    	 Assert.assertFalse(sheetData.isEmpty(), "Configured excel sheet is empty, path: " + getConfigParamValue("path"));
    	 this.sheet = sheetData; 
    }
    
    private String getConfigParamValue(String param) {
    	return getConfigParamValue(this.section,param);
    }
    
    private String getConfigParamValue(String section, String param) {
    	return this.envConfig.getConfigParamValue(section, param);
    }

}
