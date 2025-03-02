package com.tus.individual.service;

import java.io.IOException;
import java.util.List;

import org.apache.poi.ss.usermodel.Sheet;

import com.tus.individual.model.EventCause;
import com.tus.individual.model.FailureClass;
import com.tus.individual.model.FileProcessingResult;
import com.tus.individual.model.MarketOperator;
import com.tus.individual.model.UE;

public interface ISheetProcessorService {
	/**
     * Processes Call Failure Data.
     */
    public FileProcessingResult processBaseData(
    		Sheet sheet, 
    		List<EventCause> eventCauses, 
    		List<FailureClass> failureClasses, 
    		List<UE> ues, 
    		List<MarketOperator> marketOperators) throws IOException;
    
    // Event Causes
    public List<EventCause> processEventCauses(Sheet sheet);

    // Failure Classes
    public List<FailureClass> processFailureClasses(Sheet sheet);

    // UEs
    public List<UE> processUEs(Sheet sheet);


    // Market Operators
    public List<MarketOperator> processMarketOperators(Sheet sheet);
}
