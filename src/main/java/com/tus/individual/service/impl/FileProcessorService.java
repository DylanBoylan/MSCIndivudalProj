package com.tus.individual.service.impl;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tus.individual.model.EventCause;
import com.tus.individual.model.FailureClass;
import com.tus.individual.model.FileProcessingResult;
import com.tus.individual.model.MarketOperator;
import com.tus.individual.model.UE;
import com.tus.individual.service.IFileProcessorService;
import com.tus.individual.service.ISheetProcessorService;
import com.tus.individual.config.FilesConfig;
import com.tus.individual.exception.FileFormatException;
import com.tus.individual.exception.FileStorageException;



@Service
public class FileProcessorService implements IFileProcessorService {
	
	private final FilesConfig filesConfig;
	
	private final ISheetProcessorService sheetProcessor;
	
	
	@Autowired
	public FileProcessorService(FilesConfig filesConfig, ISheetProcessorService sheetProcessor) {
		super();
		this.filesConfig = filesConfig;
		this.sheetProcessor = sheetProcessor;
	}

	@Override
	public FileProcessingResult uploadDataset(String filename) throws FileFormatException, FileStorageException {
		// Validate File Format - Excel
		if (!filename.endsWith(".xlsx") && !filename.endsWith(".xls")) {
			throw new FileFormatException("File format error: Non-excel file provided: " + filename);
		}
	
		Path filePath = filesConfig.getLocation().resolve(filename).normalize();
	    File file = filePath.toFile();

	    // Check if file exists
	    if (!file.exists()) {
	        throw new FileStorageException("File not found: " + filename);
	    }
		
		
		// Upload to Data set
		try (
			Workbook workbook = WorkbookFactory.create(new FileInputStream(file));
		) {
			// We require there to be 5 sheets in the workbook 
			// (Base Data, Event-Cause Table, Failure class Table, UE Table, and MCC- MNC Table)
			int numSheets = workbook.getNumberOfSheets();
			if (numSheets != 5) {
				throw new FileFormatException("File format error: Expected 5 sheets but got " + numSheets);
			}
			
			List<EventCause> eventCauses = sheetProcessor.processEventCauses(workbook.getSheetAt(1));
			List<FailureClass> failureClasses = sheetProcessor.processFailureClasses(workbook.getSheetAt(2));
			List<UE> ues = sheetProcessor.processUEs(workbook.getSheetAt(3));
			List<MarketOperator> marketOperators = sheetProcessor.processMarketOperators(workbook.getSheetAt(4));

			return sheetProcessor.processBaseData(workbook.getSheetAt(0), eventCauses, failureClasses, ues, marketOperators);
	        
		} catch (EncryptedDocumentException | IOException e) {
			throw new FileStorageException("Failure reading from Excel file");
		}
	}
	
	@Override
	public List<String> getAllExcelFiles() {
		List<String> filenames = new ArrayList<>();
		
		Path pathToExcelFiles = filesConfig.getLocation();
		
		File folder = pathToExcelFiles.toFile();
		
		File[] files = folder.listFiles();
		
		for (File file : files) {
			String filename = file.getName();
			if (filename.endsWith(".xls") || filename.endsWith(".xlsx")) {
				filenames.add(filename);
			}
		}
		
		return filenames;
	}
}
