package com.tus.individual.formatter;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.tus.individual.exception.RowFormatException;
import com.tus.individual.model.CallFailure;
import com.tus.individual.model.EventCause;
import com.tus.individual.model.FailureClass;
import com.tus.individual.model.MarketOperator;
import com.tus.individual.model.UE;

class RowFormatterJunitTest {

    private RowFormatter rowFormatter;
    private Workbook workbook;
    private Sheet testSheet;
    private Row testRow;

    @BeforeEach
    void setUp() throws IOException {
        rowFormatter = new RowFormatter();
        workbook = WorkbookFactory.create(true);
        testSheet = workbook.createSheet("TestSheet");
        testRow = testSheet.createRow(0);
    }

    // CALL FAILURES
    @Test
    void testFormatToCallFailure_ValidRow() throws Exception {
        for (int i = 0; i < 14; i++) {
            testRow.createCell(i).setCellValue("123");
        }
        testRow.getCell(0).setCellValue("12/29/24 5:30");

        CallFailure callFailure = rowFormatter.formatToCallFailure(testRow);

        assertNotNull(callFailure);
        assertEquals(123, callFailure.getEventId());
    }

    @Test
    void testFormatToCallFailure_InvalidCellCount_ThrowsException() {
        for (int i = 0; i < 10; i++) {
            testRow.createCell(i).setCellValue("123");
        }

        RowFormatException exception = assertThrows(RowFormatException.class, () -> {
            rowFormatter.formatToCallFailure(testRow);
        });

        assertEquals("Could not format row: Expected 14 cells, but found 10", exception.getMessage());
    }

    @Test
    void testFormatToCallFailure_CellFormatException_ThrowsException() {
        for (int i = 0; i < 14; i++) {
            testRow.createCell(i).setCellValue("123");
        }
        testRow.getCell(1).setCellValue("Invalid");

        RowFormatException exception = assertThrows(RowFormatException.class, () -> {
            rowFormatter.formatToCallFailure(testRow);
        });

        assertTrue(exception.getMessage().contains("Could not format row: "));
    }

    @Test
    void testFormatToCallFailure_NullRow_ThrowsException() {
        RowFormatException exception = assertThrows(RowFormatException.class, () -> {
            rowFormatter.formatToCallFailure(null);
        });
        assertEquals("Row is null", exception.getMessage());
    }
    
    
    
    // EVENT CAUSE
    @Test
    void testEventCauseNullRow() {
        RowFormatException e = assertThrows(RowFormatException.class, () -> {
            rowFormatter.formatToEventCause(null);
        });
        assertEquals("Row is null", e.getMessage());
    }

    @ParameterizedTest(name = "An EventCause row should not have {0} cells")
    @ValueSource(ints = {0, 1, 2, 4, 5})
    void testEventCauseInvalidNumberOfRows(int numCells) {
        setupTestRow(generateDummyValues(numCells));
        RowFormatException exception = assertThrows(RowFormatException.class, () -> {
            rowFormatter.formatToEventCause(testRow);
        });

        assertTrue(exception.getMessage().contains("Expected 3 cells"));
    }
    
    @Test
    void testEventCauseInvalidFormat() {
    	setupTestRow("", "", "");
    	
    	Throwable e = assertThrows(RowFormatException.class, () -> {
    		rowFormatter.formatToEventCause(testRow);
    	});
    	assertTrue(e.getMessage().contains("Could not format row"));
    }

    @Test
    void testEventCauseValidFormat() {
        setupTestRow("1001", "2002", "Network Failure");

        EventCause eventCause = assertDoesNotThrow(() -> rowFormatter.formatToEventCause(testRow));

        assertNotNull(eventCause);
        assertEquals(1001, eventCause.getKey().getCauseCode());
        assertEquals(2002, eventCause.getKey().getEventId());
        assertEquals("Network Failure", eventCause.getDescription());
    }

    // FAILURE CLASS
    @Test
    void testFailureClassRowNull() {
        RowFormatException e = assertThrows(RowFormatException.class, () -> {
            rowFormatter.formatToFailureClass(null);
        });
        assertEquals("Row is null", e.getMessage());
    }

    @ParameterizedTest(name = "A FailureClass row should not have {0} cells")
    @ValueSource(ints = {0, 1, 3, 4})
    void testFailureClassInvalidNumberOfRows(int numCells) {
        setupTestRow(generateDummyValues(numCells));

        RowFormatException exception = assertThrows(RowFormatException.class, () -> {
            rowFormatter.formatToFailureClass(testRow);
        });

        assertTrue(exception.getMessage().contains("Expected 2 cells"));
    }
    
    @Test
    void testFailureClassInvalidFormat() {
    	setupTestRow("", "");
    	
    	Throwable e = assertThrows(RowFormatException.class, () -> {
    		rowFormatter.formatToFailureClass(testRow);
    	});
    	assertTrue(e.getMessage().contains("Could not format row"));
    }

    @Test
    void testFailureClassValidFormat() {
        setupTestRow("1", "Critical Failure");

        FailureClass failureClass = assertDoesNotThrow(() -> rowFormatter.formatToFailureClass(testRow));

        assertNotNull(failureClass);
        assertEquals(1, failureClass.getFailureClassId());
        assertEquals("Critical Failure", failureClass.getDescription());
    }

    // UE
    @Test
    void testUeRowNull() {
        RowFormatException e = assertThrows(RowFormatException.class, () -> {
            rowFormatter.formatToUE(null);
        });
        assertEquals("Row is null", e.getMessage());
    }

    @ParameterizedTest(name = "A UE row should not have {0} cells")
    @ValueSource(ints = {7, 8, 10, 11})
    void testUEInvalidNumberOfRows(int numCells) {
        setupTestRow(generateDummyValues(numCells));

        RowFormatException exception = assertThrows(RowFormatException.class, () -> {
            rowFormatter.formatToUE(testRow);
        });

        assertTrue(exception.getMessage().contains("Expected 9 cells"));
    }
    
    @Test
    void testUEInvalidFormat() {
    	setupTestRow("", "", "", "", "", "", "", "", "");
    	
    	Throwable e = assertThrows(RowFormatException.class, () -> {
    		rowFormatter.formatToUE(testRow);
    	});
    	
    	assertTrue(e.getMessage().contains("Could not format row"));
    }

    @Test
    void testUEValidFormat() {
        setupTestRow("1001", "iPhone", "Apple", "5G", "12 Pro", "Apple", "Smartphone", "iOS", "Touch");

        UE ue = assertDoesNotThrow(() -> rowFormatter.formatToUE(testRow));

        assertNotNull(ue);
        assertEquals(1001, ue.getTac());
        assertEquals("iPhone", ue.getMarketingName());
        assertEquals("Apple", ue.getManufacturer());
    }

    // MARKET OPERATOR
    @Test
    void testMarketOperatorRowNull() {
        RowFormatException e = assertThrows(RowFormatException.class, () -> {
            rowFormatter.formatToMarketOperator(null);
        });
        assertEquals("Row is null", e.getMessage());
    }

    @ParameterizedTest(name = "A MarketOperator row should not have {0} cells")
    @ValueSource(ints = {2, 3, 5, 6})
    void testMarketOperatorInvalidNumberOfRows(int numCells) {
        setupTestRow(generateDummyValues(numCells));

        RowFormatException exception = assertThrows(RowFormatException.class, () -> {
            rowFormatter.formatToMarketOperator(testRow);
        });

        assertTrue(exception.getMessage().contains("Expected 4 cells"));
    }
    
    @Test
    void testMarketOperatorInvalidFormat() {
    	setupTestRow("", "", "", "");
    	
    	Throwable e = assertThrows(RowFormatException.class, () -> {
    		rowFormatter.formatToMarketOperator(testRow);
    	});
    	assertTrue(e.getMessage().contains("Could not format row"));
    }

    @Test
    void testMarketOperatorValidFormat() {
        setupTestRow("310", "260", "USA", "T-Mobile");

        MarketOperator marketOperator = assertDoesNotThrow(() -> rowFormatter.formatToMarketOperator(testRow));

        assertNotNull(marketOperator);
        assertEquals(310, marketOperator.getKey().getMcc());
        assertEquals(260, marketOperator.getKey().getMnc());
        assertEquals("T-Mobile", marketOperator.getOperator());
    }

    // Helper Methods
    private void setupTestRow(String... values) {
        for (int i = 0; i < values.length; i++) {
            testRow.createCell(i).setCellValue(values[i]);
        }
    }

    private String[] generateDummyValues(int count) {
        String[] values = new String[count];
        for (int i = 0; i < count; i++) {
            values[i] = "dummy" + i;
        }
        return values;
    }
}
