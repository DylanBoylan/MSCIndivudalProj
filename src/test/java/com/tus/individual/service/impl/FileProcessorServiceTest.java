package com.tus.individual.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.internal.verification.Times;

import com.tus.individual.config.FilesConfig;
import com.tus.individual.exception.FileFormatException;
import com.tus.individual.exception.FileStorageException;
import com.tus.individual.model.FileProcessingResult;


class FileProcessorServiceTest {
	@TempDir
    private Path tempDir;
    
	private FileProcessorService fileProcessor;
	private FilesConfig filesConfig;
	private SheetProcessorService sheetProcessor;
	
	private final int NUM_VALID_SHEETS = 5;
	
	
	@BeforeEach
	void setup() {
		filesConfig = new FilesConfig();
		filesConfig.setLocation(tempDir.toAbsolutePath().toString());
		
		sheetProcessor = mock(SheetProcessorService.class);
		
		fileProcessor = new FileProcessorService(filesConfig, sheetProcessor);
	}
	
	@ParameterizedTest(name = "The filename \"{0}\" should be considered invalid")
	@ValueSource(strings = {"test.pdf", "otherTest.xlsm", "weird", "dlkfh-34"})
	void testInvalidFileType(String filename) {
		Throwable e = assertThrows(FileFormatException.class, () -> {
			fileProcessor.uploadDataset(filename);
		});
		
		assertEquals("File format error: Non-excel file provided: " + filename, e.getMessage());	
		
		verify(sheetProcessor, new Times(0)).processEventCauses(any());
		verify(sheetProcessor, new Times(0)).processFailureClasses(any());
		verify(sheetProcessor, new Times(0)).processUEs(any());
		verify(sheetProcessor, new Times(0)).processMarketOperators(any());
		verify(sheetProcessor, new Times(0)).processBaseData(any(), any(), any(), any(), any());
	}
	
	@ParameterizedTest(name = "The file \"{0}\" should not exist in the directory")
	@ValueSource(strings = {"test72.xls", "otherTest.xls", "anotherTest.xlsx", "g43.xlsx"})
	void testNonExistentFile(String filename) {
		Throwable e = assertThrows(FileStorageException.class, () -> {
			fileProcessor.uploadDataset(filename);
		});
		
		assertEquals("File not found: " + filename, e.getMessage());
		
		verify(sheetProcessor, new Times(0)).processEventCauses(any());
		verify(sheetProcessor, new Times(0)).processFailureClasses(any());
		verify(sheetProcessor, new Times(0)).processUEs(any());
		verify(sheetProcessor, new Times(0)).processMarketOperators(any());
		verify(sheetProcessor, new Times(0)).processBaseData(any(), any(), any(), any(), any());
	}
	
	@ParameterizedTest(name = "A workbook with {0} sheets is invalid.")
	@ValueSource(ints = {3, 4, 6, 7})
	void testInvalidNumberOfSheets(int numSheets) {
		String filename = "test.xls";
				
		try {
			createWorkbook(filename, numSheets);
		} catch (IOException e) {
			fail(e.getMessage());
		}
		
		Throwable e = assertThrows(FileFormatException.class, () -> {
			fileProcessor.uploadDataset(filename);
		});
		
		assertEquals("File format error: Expected 5 sheets but got " + numSheets, e.getMessage());
		
		verify(sheetProcessor, new Times(0)).processEventCauses(any());
		verify(sheetProcessor, new Times(0)).processFailureClasses(any());
		verify(sheetProcessor, new Times(0)).processUEs(any());
		verify(sheetProcessor, new Times(0)).processMarketOperators(any());
		verify(sheetProcessor, new Times(0)).processBaseData(any(), any(), any(), any(), any());
	}
	
	@Test
	void testBaseDataProcessed() {
		String filename = "test.xls";
		
		try {
			createWorkbook(filename, NUM_VALID_SHEETS);
		} catch (IOException e) {
			fail(e.getMessage()); // Is this the best way to deal with an IOException here?
		}
		
		FileProcessingResult fpResult1 = new FileProcessingResult(5, 4);
		when(sheetProcessor.processBaseData(any(), any(), any(), any(), any())).thenReturn(fpResult1);
		
		FileProcessingResult fpResult2 = null;
		try {
			fpResult2 = fileProcessor.uploadDataset(filename);
		} catch (FileFormatException e) {
			fail("FileFormatException should not have been called");
		}
		assertEquals(fpResult1.getRowsProcessed(), fpResult2.getRowsProcessed());
		assertEquals(fpResult1.getRowsSkipped(), fpResult2.getRowsSkipped());
		
		verify(sheetProcessor, new Times(1)).processEventCauses(any());
		verify(sheetProcessor, new Times(1)).processFailureClasses(any());
		verify(sheetProcessor, new Times(1)).processUEs(any());
		verify(sheetProcessor, new Times(1)).processMarketOperators(any());
		verify(sheetProcessor, new Times(1)).processBaseData(any(), any(), any(), any(), any());

	}
	
	
	
	// Test getAllExcelFiles()
	@ParameterizedTest(name = "Excel files should return {0} filenames {1}")
	@MethodSource("generateFiles")
	void testGetAllExcelFiles(int numFiles, String[] filenames) throws IOException {
		filesConfig = new FilesConfig();
		filesConfig.setLocation(tempDir.toAbsolutePath().toString());
		
		// Add Excel files
		for (int i = 0; i < numFiles; i++) {
			createWorkbook(filenames[i], 1);
		}
		
		// Add some other non-excel files to make sure it doesn't add those
		Files.createFile(tempDir.resolve("nonExcelFile1.txt"));
		Files.createFile(tempDir.resolve("nonExcelFile2.pdf"));
		
		List<String> files = fileProcessor.getAllExcelFiles();
		
		assertEquals(numFiles, files.size());
		
		for (int i = 0; i < numFiles; i++) {
			assertEquals(true, files.contains(filenames[i]));
		}
	} 
	
	
	// Helper methods
	private static Stream<Arguments> generateFiles() {
		return Stream.of(
				Arguments.of(0, new String[] {}),
				Arguments.of(1, new String[]{"test.xls"}),
				Arguments.of(2, new String[]{"test.xls", "anotherTest.xlsx"})
				);
	}
	
	private void createWorkbook(String filename, int numSheets) throws IOException {
		Path filepath = tempDir.resolve(filename);
		File file = filepath.toFile();
		
		try (Workbook workbook = WorkbookFactory.create(true)) { // true -> creates .xlsx format
            // Add sheets
			for (int i = 0; i < numSheets; i++) {
				workbook.createSheet();
			}

            // Write workbook to file
            try (FileOutputStream fos = new FileOutputStream(file)) {
                workbook.write(fos);
            }
        }
	}

}
