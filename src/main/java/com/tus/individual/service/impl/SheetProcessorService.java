package com.tus.individual.service.impl;

import java.util.*;
import java.util.stream.StreamSupport;
import java.util.List;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tus.individual.dao.*;
import com.tus.individual.exception.CallFailureValidationException;
import com.tus.individual.exception.RowFormatException;
import com.tus.individual.formatter.RowFormatter;
import com.tus.individual.model.*;
import com.tus.individual.service.ISheetProcessorService;
import com.tus.individual.validation.CallFailureValidator;

import lombok.Setter;

@Service
public class SheetProcessorService implements ISheetProcessorService {
	@Value("${spring.jpa.properties.hibernate.jdbc.batch_size}")
	@Setter
	private int BATCH_SIZE; 
    private final CallFailureValidator callFailureValidator;
    private final CallFailureRepository callFailureRepo;
    private final EventCauseRepository eventCauseRepo;
    private final FailureClassRepository failureClassRepo;
    private final UERepository ueRepo;
    private final MarketOperatorRepository marketOperatorRepo;
    private final RowFormatter rowFormatter;

    @Autowired
    public SheetProcessorService(
            CallFailureRepository callFailureRepo,
            EventCauseRepository eventCauseRepo,
            FailureClassRepository failureClassRepo,
            UERepository ueRepo,
            MarketOperatorRepository marketOperatorRepo,
            CallFailureValidator callFailureValidator) {
        this.callFailureRepo = callFailureRepo;
        this.eventCauseRepo = eventCauseRepo;
        this.failureClassRepo = failureClassRepo;
        this.ueRepo = ueRepo;
        this.marketOperatorRepo = marketOperatorRepo;
        this.callFailureValidator = callFailureValidator;
        this.rowFormatter = new RowFormatter(); // Single instance
    }
    

    /**
     * Processes Call Failure Data.
     */
    @Override
    public FileProcessingResult processBaseData(Sheet sheet, List<EventCause> eventCauses, List<FailureClass> failureClasses, List<UE> ues, List<MarketOperator> marketOperators) {
        int processed = 0;
        int skipped = 0;
        int lineNumber = 0;
        
        Logger skippedLogger = LoggerFactory.getLogger("SkippedRowsLogger");
        // Delete the skipped_rows.log file before processing a new file
        String filePath = "logs/skipped_rows.log";
        try (FileWriter writer = new FileWriter(filePath, false)) { 
            // Empty file
            writer.write("");
        } catch (IOException e) {
            System.err.println("Error clearing the log file: " + e.getMessage());
        }
        
        List<CallFailure> batch = new ArrayList<>();
        for (Row row : sheet) {
            lineNumber++;
            if (processed++ == 0) continue; // Skip headers
            try {
                CallFailure callFailure = rowFormatter.formatToCallFailure(row);
                callFailureValidator.validateCallFailure(callFailure, eventCauses, failureClasses, ues, marketOperators);
                batch.add(callFailure);
                
                if (batch.size() >= BATCH_SIZE) {
                    callFailureRepo.saveAll(batch);
                    batch.clear();
                }
            } catch (RowFormatException | CallFailureValidationException e) {
                skipped++;
                processed--;
                skippedLogger.warn("Skipped line {} - Reason: {}", lineNumber, e.getMessage()); 
            }
        }
        
        // Save any remaining records in the last batch
        if (!batch.isEmpty()) {
            callFailureRepo.saveAll(batch);
        }
        
        return new FileProcessingResult(processed - 1, skipped);
    }
    
    // Event Causes
    @Override
    public List<EventCause> processEventCauses(Sheet sheet) {
        return StreamSupport.stream(sheet.spliterator(), false)
                .skip(1) // Skip header row
                .map(row -> {
                    try {
                    	EventCause eventCause = rowFormatter.formatToEventCause(row);
                    	
                    	// Check null Id
                    	if (eventCause.getKey().isNull()) {
                    		throw new RowFormatException("Id is null");
                    	}
                    	
                    	// Save object
                    	eventCauseRepo.save(eventCause);
                    	return eventCause;
                    	
                    } catch (RowFormatException e) {
                        System.err.println("Error processing row " + row.getRowNum() + ": " + e.getMessage());
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList();
    }

    // Failure Classes
    @Override
    public List<FailureClass> processFailureClasses(Sheet sheet) {
        return StreamSupport.stream(sheet.spliterator(), false)
                .skip(1) // Skip header row
                .map(row -> {
                    try {
                    	FailureClass failureClass = rowFormatter.formatToFailureClass(row);
                    	
                    	// Check null Id
                    	if (failureClass.getFailureClassId() == null) {
                    		throw new RowFormatException("Id is null");
                    	}
                    	
                    	// Save object
                    	failureClassRepo.save(failureClass);
                    	return failureClass;
                    } catch (RowFormatException e) {
                        System.err.println("Error processing row " + row.getRowNum() + ": " + e.getMessage());
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList();
    }

    // UEs
    public List<UE> processUEs(Sheet sheet) {
        return StreamSupport.stream(sheet.spliterator(), false)
                .skip(1) // Skip header row
                .map(row -> {
                    try {
                    	UE ue = rowFormatter.formatToUE(row);
                    	
                    	// Check null Id
                    	if (ue.getTac() == null) {
                    		throw new RowFormatException("Id is null");
                    	}
                    	
                    	ueRepo.save(ue);
                        return ue;
                    } catch (RowFormatException e) {
                        System.err.println("Error processing row " + row.getRowNum() + ": " + e.getMessage());
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList();
    }


    // Market Operators
    @Override
    public List<MarketOperator> processMarketOperators(Sheet sheet) {
        return StreamSupport.stream(sheet.spliterator(), false)
                .skip(1) // Skip header row
                .map(row -> {
                    try {
                    	MarketOperator marketOperator = rowFormatter.formatToMarketOperator(row);
                    	
                    	// Check null Id
                    	if (marketOperator.getKey().isNull()) {
                    		throw new RowFormatException("Id is null");
                    	}

                        marketOperatorRepo.save(marketOperator);
                        return marketOperator;
                    } catch (RowFormatException e) {
                        System.err.println("Error processing row " + row.getRowNum() + ": " + e.getMessage());
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList();
    }
}
