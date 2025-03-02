package com.tus.individual.formatter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;

import com.tus.individual.exception.CellFormatException;

public class CellFormatter {
	private static final String NULL_ENTRY = "(null)";
	private DataFormatter dataFormatter;
	 
	public CellFormatter() {
		dataFormatter = new DataFormatter();
	}
	    
	 
	public LocalDateTime formatToLocalDateTime(Cell cell, String dateTimeFormat) throws CellFormatException {
        String cellValue = dataFormatter.formatCellValue(cell).trim();
         
        if (cellValue.equals(NULL_ENTRY)) return null;
         
        try {
         	DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateTimeFormat);
         	return LocalDateTime.parse(cellValue, formatter);
        } catch (DateTimeParseException e) {
        	throw new CellFormatException("Cannot convert " + cellValue + " to LocalDateTime.");
        }
    }
	 
    public Integer formatToInteger(Cell cell) throws CellFormatException{
        String cellValue = dataFormatter.formatCellValue(cell).trim();
         
        if (cellValue.equals(NULL_ENTRY)) return null;
        try {
         	return Integer.parseInt(cellValue);
        } catch (NumberFormatException e) {
         	throw new CellFormatException("Cannot convert " + cellValue + " to Integer.");
        }
    }
     
    public String formatToString(Cell cell) {
        String cellValue = dataFormatter.formatCellValue(cell).trim();
         
        if (cellValue.equals(NULL_ENTRY)) return null;
         	
        return cellValue;
    }

    public Long formatToLong(Cell cell) throws CellFormatException{
        String cellValue = dataFormatter.formatCellValue(cell).trim();
         
        if (cellValue.equals(NULL_ENTRY)) return null;
         
        try {
         	return Long.parseLong(cellValue);
        } catch (NumberFormatException e) {
         	throw new CellFormatException("Cannot convert " + cellValue + " to Long.");
        }
    }
}
