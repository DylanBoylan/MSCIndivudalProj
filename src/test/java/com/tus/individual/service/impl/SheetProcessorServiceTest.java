package com.tus.individual.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.any;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.internal.verification.Times;

import com.tus.individual.dao.CallFailureRepository;
import com.tus.individual.dao.EventCauseRepository;
import com.tus.individual.dao.FailureClassRepository;
import com.tus.individual.dao.MarketOperatorRepository;
import com.tus.individual.dao.UERepository;
import com.tus.individual.model.EventCause;
import com.tus.individual.model.EventCauseKey;
import com.tus.individual.model.FailureClass;
import com.tus.individual.model.FileProcessingResult;
import com.tus.individual.model.MarketOperator;
import com.tus.individual.model.UE;
import com.tus.individual.validation.CallFailureValidator;


class SheetProcessorServiceTest {
	private SheetProcessorService sheetProcessor;
	
    private CallFailureRepository callFailureRepo;
    private EventCauseRepository eventCauseRepo;
    private FailureClassRepository failureClassRepo;
    private UERepository ueRepo;
    private MarketOperatorRepository marketOperatorRepo;
    
	private List<EventCause> eventCauses;
	private List<FailureClass> failureClasses;
	private List<UE> ues;
	private List<MarketOperator> marketOperators;
	
	private static final String TEST_BASE_DATA_FOLDER = "src/test/resources/dataset_test_data/base_dataset";
	private static final String TEST_EVENT_CAUSE_FOLDER = "src/test/resources/dataset_test_data/event_causes_dataset";
	private static final String TEST_FAILURE_CLASS_FOLDER = "src/test/resources/dataset_test_data/failure_classes_dataset";
	private static final String TEST_UE_FOLDER = "src/test/resources/dataset_test_data/ues_dataset";
	private static final String TEST_MARKET_OPERATOR_FOLDER = "src/test/resources/dataset_test_data/market_operators_dataset";
	private static final int BATCH_SIZE = 1000;
	
	
	private CallFailureValidator callFailureValidator;  // Add this

	@BeforeEach
	void setup() {
	    callFailureValidator = mock(CallFailureValidator.class); // Mock the validator

	    callFailureRepo = mock(CallFailureRepository.class);
	    eventCauseRepo = mock(EventCauseRepository.class);
	    failureClassRepo = mock(FailureClassRepository.class);
	    ueRepo = mock(UERepository.class);
	    marketOperatorRepo = mock(MarketOperatorRepository.class);

	    sheetProcessor = new SheetProcessorService(
	        callFailureRepo,
	        eventCauseRepo,
	        failureClassRepo,
	        ueRepo,
	        marketOperatorRepo,
	        callFailureValidator  // Pass the mocked validator
	    );

	    // Event Causes
	    eventCauses = new ArrayList<>();
	    eventCauses.add(new EventCause(new EventCauseKey(), ""));

	    // Failure Classes
	    failureClasses = new ArrayList<>();

	    // UEs
	    ues = new ArrayList<>();

	    // Market Operators
	    marketOperators = new ArrayList<>();
	    
	    // Batch size
	    sheetProcessor.setBATCH_SIZE(BATCH_SIZE);
	}

	
	
	@ParameterizedTest(name = "File \"{0}\" has {1}")
	@MethodSource("generateFilenameFpResults")
	void testBaseDataProcessing(String filename, FileProcessingResult expectedResult) {
	    // Read Sheet from workbook
	    Path filepath = Paths.get(TEST_BASE_DATA_FOLDER, filename);
	    File file = filepath.toFile();

	    try (FileInputStream fis = new FileInputStream(file);
	         Workbook workbook = WorkbookFactory.create(fis)) {
	        
	        Sheet sheet = workbook.getSheetAt(0);

	        // Process base data
	        FileProcessingResult actualResult = sheetProcessor.processBaseData(
	            sheet, eventCauses, failureClasses, ues, marketOperators
	        );

	        // Assertions
	        assertEquals(expectedResult.getRowsProcessed(), actualResult.getRowsProcessed(),
	                "Mismatch in processed row count for file: " + filename);
	        assertEquals(expectedResult.getRowsSkipped(), actualResult.getRowsSkipped(),
	                "Mismatch in skipped row count for file: " + filename);

	        // Verify that saveAll was called expected times
	        int numberOfBatchSaves = (int) Math.ceil((double) actualResult.getRowsProcessed() / BATCH_SIZE);
	        verify(callFailureRepo, new Times(numberOfBatchSaves)).saveAll(any());

	    } catch (IOException e) {
	        fail("IOException reading file: " + filename);
	    } catch (Exception e) {
	        fail("Unexpected exception: " + e.getMessage());
	    }
	}

	
	
	@ParameterizedTest(name = "File \"{0}\" has {1} invalid rows")
	@MethodSource("generateEventCausesFilenamesInvalidRows")
	void testEventCausesProcessed(String filename, int numInvalidRows) {
		// Read Sheet from workbook
		Path filepath = Paths.get(TEST_EVENT_CAUSE_FOLDER, filename);
		File file = filepath.toFile();
				
		try (Workbook workbook = WorkbookFactory.create(new FileInputStream(file))){
			Sheet sheet = workbook.getSheetAt(0);
			
			List<EventCause> eventCausesResult = sheetProcessor.processEventCauses(sheet);
			
			assertEquals(sheet.getLastRowNum() - numInvalidRows, eventCausesResult.size());
			
			for (EventCause eventCause : eventCausesResult) {
				verify(eventCauseRepo, new Times(1)).save(eventCause);
			}
			
		} catch (IOException e) {
			fail("IOException reading file");
		}
	}
	
	@ParameterizedTest(name = "File \"{0}\" has {1} invalid rows")
	@MethodSource("generateFailureClassesFilenamesInvalidRows")
	void testFailureClassesProcessed(String filename, int numInvalidRows) {
		// Read Sheet from workbook
		Path filepath = Paths.get(TEST_FAILURE_CLASS_FOLDER, filename);
		File file = filepath.toFile();
				
		try (Workbook workbook = WorkbookFactory.create(new FileInputStream(file))){
			Sheet sheet = workbook.getSheetAt(0);
			
			List<FailureClass> failureClassesResult = sheetProcessor.processFailureClasses(sheet);
			
			assertEquals(sheet.getLastRowNum() - numInvalidRows, failureClassesResult.size());
			
			for (FailureClass failureClass : failureClassesResult) {
				verify(failureClassRepo, new Times(1)).save(failureClass);
			}
			
		} catch (IOException e) {
			fail("IOException reading file");
		}
	}
	
	@ParameterizedTest(name = "File \"{0}\" has {1} invalid rows")
	@MethodSource("generateUEsFilenamesInvalidrows")
	void testUEsProcessed(String filename, int numInvalidRows) {
		// Read Sheet from workbook
		Path filepath = Paths.get(TEST_UE_FOLDER, filename);
		File file = filepath.toFile();
				
		try (Workbook workbook = WorkbookFactory.create(new FileInputStream(file))){
			Sheet sheet = workbook.getSheetAt(0);
			
			List<UE> uesResult = sheetProcessor.processUEs(sheet);
			
			assertEquals(sheet.getLastRowNum() - numInvalidRows, uesResult.size());
			
			for (UE ue : uesResult) {
				verify(ueRepo, new Times(1)).save(ue);
			}
			
		} catch (IOException e) {
			fail("IOException reading file");
		}
	}
	
	
	@ParameterizedTest(name = "File \"{0}\" has {1} invalid rows")
	@MethodSource("generateMarketOperatorsFilenamesInvalidRows")
	void testMarketOperatorsProcessed(String filename, int numInvalidRows) {
		// Read Sheet from workbook
		Path filepath = Paths.get(TEST_MARKET_OPERATOR_FOLDER, filename);
		File file = filepath.toFile();
				
		try (Workbook workbook = WorkbookFactory.create(new FileInputStream(file))){
			Sheet sheet = workbook.getSheetAt(0);
			
			List<MarketOperator> marketOperatorResult = sheetProcessor.processMarketOperators(sheet);
			
			assertEquals(sheet.getLastRowNum() - numInvalidRows, marketOperatorResult.size());
			
			for (MarketOperator marketOperator : marketOperatorResult) {
				verify(marketOperatorRepo, new Times(1)).save(marketOperator);
			}
			
		} catch (IOException e) {
			fail("IOException reading file");
		}
	}
	
	
	
	
	
	// Helper methods
	// Base Data
	private static Stream<Arguments> generateFilenameFpResults() {
		return Stream.of(
				Arguments.of("dataset_test_all_data_valid.xlsx", new FileProcessingResult(15, 0)),
				Arguments.of("dataset_test_1_invalid_row.xlsx", new FileProcessingResult(14, 1)),
				Arguments.of("dataset_test_3_invalid_rows.xlsx", new FileProcessingResult(12, 3))
				);
	}
	
	// Event Causes
	private static Stream<Arguments> generateEventCausesFilenamesInvalidRows() {
		return Stream.of(
				Arguments.of("event_cause_dataset_all_data_valid.xlsx", 0),
				Arguments.of("event_cause_dataset_1_invalid_row.xlsx", 1),
				Arguments.of("event_cause_dataset_3_invalid_rows.xlsx", 3)
				);
	}
	
	// FailureClasses
	private static Stream<Arguments> generateFailureClassesFilenamesInvalidRows() {
		return Stream.of(
				Arguments.of("failure_class_dataset_all_data_valid.xlsx", 0),
				Arguments.of("failure_class_dataset_1_invalid_row.xlsx", 1),
				Arguments.of("failure_class_dataset_3_invalid_rows.xlsx", 3)
				);
	}
	
	
	// UEs
	private static Stream<Arguments> generateUEsFilenamesInvalidrows() {
		return Stream.of(
				Arguments.of("ue_dataset_all_data_valid.xlsx", 0),
				Arguments.of("ue_dataset_1_invalid_row.xlsx", 1),
				Arguments.of("ue_dataset_3_invalid_rows.xlsx", 3)
				);
	}
	
	
	// Market Operators
	private static Stream<Arguments> generateMarketOperatorsFilenamesInvalidRows() {
		return Stream.of(
				Arguments.of("market_operator_dataset_all_data_valid.xlsx", 0),
				Arguments.of("market_operator_dataset_1_invalid_row.xlsx", 1),
				Arguments.of("market_operator_dataset_3_invalid_rows.xlsx", 3)
				);
	}
	
	
	
	
}
