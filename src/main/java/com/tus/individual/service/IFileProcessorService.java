package com.tus.individual.service;

import java.util.List;

import com.tus.individual.exception.FileFormatException;
import com.tus.individual.exception.FileStorageException;
import com.tus.individual.model.FileProcessingResult;

public interface IFileProcessorService {

	public FileProcessingResult uploadDataset(String filename) throws FileFormatException, FileStorageException;
	
	public List<String> getAllExcelFiles();
}
