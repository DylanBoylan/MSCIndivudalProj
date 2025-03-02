package com.tus.individual.formatter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;

import com.tus.individual.exception.CellFormatException;

public class CellFormatter {
    private static final String NULL_ENTRY = "(null)";
    private final DataFormatter dataFormatter;
    private static final String DATE_FORMAT = "dd/MM/yyyy HH:mm"; // Adjust format as needed

    public CellFormatter() {
        this.dataFormatter = new DataFormatter();
    }
    
    public LocalDateTime formatToLocalDateTime(Cell cell, String dateTimeFormat) throws CellFormatException {
        String cellValue = dataFormatter.formatCellValue(cell).trim();
         
        if (NULL_ENTRY.equals(cellValue) || cellValue.isEmpty()) return null;
         
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateTimeFormat);
            return LocalDateTime.parse(cellValue, formatter);
        } catch (DateTimeParseException e) {
            throw new CellFormatException("Cannot convert '" + cellValue + "' to LocalDateTime.");
        }
    }
 
    public Integer formatToInteger(Cell cell) throws CellFormatException {
        String cellValue = dataFormatter.formatCellValue(cell).trim();
         
        if (NULL_ENTRY.equals(cellValue) || cellValue.isEmpty()) return null;
        try {
            return Integer.parseInt(cellValue);
        } catch (NumberFormatException e) {
            throw new CellFormatException("Cannot convert '" + cellValue + "' to Integer.");
        }
    }
 
    public String formatToString(Cell cell) {
        String cellValue = dataFormatter.formatCellValue(cell).trim();
         
        return (NULL_ENTRY.equals(cellValue) || cellValue.isEmpty()) ? null : cellValue;
    }

    public Long formatToLong(Cell cell) throws CellFormatException {
        String cellValue = dataFormatter.formatCellValue(cell).trim();
         
        if (NULL_ENTRY.equals(cellValue) || cellValue.isEmpty()) return null;
         
        try {
            return Long.parseLong(cellValue);
        } catch (NumberFormatException e) {
            throw new CellFormatException("Cannot convert '" + cellValue + "' to Long.");
        }
    }
    
    public Date formatToDate(Cell cell) throws CellFormatException {
        if (cell == null || cell.getCellType() == CellType.BLANK) {
            throw new CellFormatException("Cell is empty or null");
        }

        try {
            if (cell.getCellType() == CellType.NUMERIC) {
                return cell.getDateCellValue();
            } else if (cell.getCellType() == CellType.STRING) {
                SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
                return sdf.parse(cell.getStringCellValue());
            } else {
                throw new CellFormatException("Invalid date format in cell");
            }
        } catch (ParseException e) {
            throw new CellFormatException("Error parsing date: " + e.getMessage());
        }
    }

}
