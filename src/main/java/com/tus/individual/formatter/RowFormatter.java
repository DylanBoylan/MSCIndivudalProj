package com.tus.individual.formatter;

import org.apache.poi.ss.usermodel.Row;

import com.tus.individual.exception.CellFormatException;
import com.tus.individual.exception.RowFormatException;
import com.tus.individual.model.CallFailure;
import com.tus.individual.model.EventCause;
import com.tus.individual.model.EventCauseKey;
import com.tus.individual.model.FailureClass;
import com.tus.individual.model.MarketOperator;
import com.tus.individual.model.MarketOperatorKey;
import com.tus.individual.model.UE;


public class RowFormatter {
	private static final String NULL_ROW_ERROR_MESSAGE = "Row is null";
	private static final String COULD_NOT_FORMAT_ROW_ERROR_MESSAGE = "Could not format row: ";
	private CellFormatter cellFormatter;
	
	public RowFormatter() {
		cellFormatter = new CellFormatter();
	}
	
	public CallFailure formatToCallFailure(Row row) throws RowFormatException {
		if (row == null) {
	        throw new RowFormatException(NULL_ROW_ERROR_MESSAGE);
	    }
		
        if (row.getLastCellNum() != 14) {
        	throw new RowFormatException("Could not format row: Expected 14 cells, but found " + row.getLastCellNum());
        }
        
        try {
        	CallFailure callFailure = new CallFailure();
	    
			callFailure.setDateTime(cellFormatter.formatToLocalDateTime(row.getCell(0), "M/d/yy H:mm"));
			callFailure.setEventId(cellFormatter.formatToInteger(row.getCell(1)));
			callFailure.setFailureClass(cellFormatter.formatToInteger(row.getCell(2)));
			callFailure.setUeType(cellFormatter.formatToInteger(row.getCell(3)));
			callFailure.setMarket(cellFormatter.formatToInteger(row.getCell(4)));
			callFailure.setOperator(cellFormatter.formatToInteger(row.getCell(5)));
			callFailure.setCellId((cellFormatter.formatToInteger(row.getCell(6))));
			callFailure.setDuration(cellFormatter.formatToInteger(row.getCell(7)));
			callFailure.setCauseCode(cellFormatter.formatToInteger(row.getCell(8)));
			callFailure.setNeVersion(cellFormatter.formatToString(row.getCell(9)));
			callFailure.setImsi(cellFormatter.formatToLong(row.getCell(10)));
			callFailure.setHier3Id(cellFormatter.formatToLong((row.getCell(11))));
			callFailure.setHier32Id(cellFormatter.formatToLong(row.getCell(12)));
			callFailure.setHier321Id(cellFormatter.formatToLong((row.getCell(13))));
			
			return callFailure;
        } catch (CellFormatException e) {
        	throw new RowFormatException(COULD_NOT_FORMAT_ROW_ERROR_MESSAGE + e.getMessage());
        }
	}
	
	public EventCause formatToEventCause(Row row) throws RowFormatException {
		if (row == null) {
	        throw new RowFormatException(NULL_ROW_ERROR_MESSAGE);
	    }
		
        if (row.getLastCellNum() != 3) {
        	throw new RowFormatException("Could not format row: Expected 3 cells, but found " + row.getLastCellNum());
        }
        
        try {
        	EventCause eventCause = new EventCause();
	    
			Integer causeCode = cellFormatter.formatToInteger(row.getCell(0));
			Integer eventId = cellFormatter.formatToInteger(row.getCell(1));
			eventCause.setKey(new EventCauseKey(causeCode, eventId));
			eventCause.setDescription(cellFormatter.formatToString(row.getCell(2)));
			
			return eventCause;
        } catch (CellFormatException e) {
        	throw new RowFormatException(COULD_NOT_FORMAT_ROW_ERROR_MESSAGE + e.getMessage());
        }
	}
	
	
	public FailureClass formatToFailureClass(Row row) throws RowFormatException {
		if (row == null) {
	        throw new RowFormatException(NULL_ROW_ERROR_MESSAGE);
	    }
		
        if (row.getLastCellNum() != 2) {
        	throw new RowFormatException("Could not format row: Expected 2 cells, but found " + row.getLastCellNum());
        }
        
        try {
        	FailureClass failureClass = new FailureClass();
	    
        	failureClass.setFailureClassId(cellFormatter.formatToInteger(row.getCell(0)));
        	failureClass.setDescription(cellFormatter.formatToString(row.getCell(1)));
			
			return failureClass;
        } catch (CellFormatException e) {
        	throw new RowFormatException(COULD_NOT_FORMAT_ROW_ERROR_MESSAGE + e.getMessage());
        }
	}
	
	public UE formatToUE(Row row) throws RowFormatException {
		if (row == null) {
	        throw new RowFormatException(NULL_ROW_ERROR_MESSAGE);
	    }

        if (row.getLastCellNum() != 9) {
        	throw new RowFormatException("Could not format row: Expected 9 cells, but found " + row.getLastCellNum());
        }
        
        try {
        	UE ue = new UE();
	    
			ue.setTac(cellFormatter.formatToInteger(row.getCell(0)));
			ue.setMarketingName(cellFormatter.formatToString(row.getCell(1)));
			ue.setManufacturer(cellFormatter.formatToString(row.getCell(2)));
			ue.setAccessCapability(cellFormatter.formatToString(row.getCell(3)));
			ue.setModel(cellFormatter.formatToString(row.getCell(4)));
			ue.setVendorName(cellFormatter.formatToString(row.getCell(4)));
			ue.setUeType(cellFormatter.formatToString(row.getCell(4)));
			ue.setOs(cellFormatter.formatToString(row.getCell(4)));
			ue.setInputMode(cellFormatter.formatToString(row.getCell(4)));
			
			return ue;
        } catch (CellFormatException e) {
        	throw new RowFormatException(COULD_NOT_FORMAT_ROW_ERROR_MESSAGE + e.getMessage());
        }
	}
	
	public MarketOperator formatToMarketOperator(Row row) throws RowFormatException {
		if (row == null) {
	        throw new RowFormatException(NULL_ROW_ERROR_MESSAGE);
	    }
		
        if (row.getLastCellNum() != 4) {
        	throw new RowFormatException("Could not format row: Expected 4 cells, but found " + row.getLastCellNum());
        }
        
        try {
        	MarketOperator marketOperator = new MarketOperator();
        	
        	Integer mcc = cellFormatter.formatToInteger(row.getCell(0));
        	Integer mnc = cellFormatter.formatToInteger(row.getCell(1));
        	
        	marketOperator.setKey(new MarketOperatorKey(mcc, mnc));
        	marketOperator.setCountry(cellFormatter.formatToString(row.getCell(2)));
        	marketOperator.setOperator(cellFormatter.formatToString(row.getCell(3)));
			
			return marketOperator;
        } catch (CellFormatException e) {
        	throw new RowFormatException(COULD_NOT_FORMAT_ROW_ERROR_MESSAGE + e.getMessage());
        }
	}

}
