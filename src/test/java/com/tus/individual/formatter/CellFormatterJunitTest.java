package com.tus.individual.formatter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.time.LocalDateTime;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.tus.individual.exception.CellFormatException;

class CellFormatterJunitTest {

    private CellFormatter cellFormatter;
    private Workbook workbook;
    private Sheet testSheet;
    private Row testRow;
    private Cell testCell;

    @BeforeEach
    void setUp() throws IOException {
        cellFormatter = new CellFormatter();
        workbook = WorkbookFactory.create(true); // Creates an in-memory workbook
        testSheet = workbook.createSheet("TestSheet");
        testRow = testSheet.createRow(0);
        testCell = testRow.createCell(0);
    }

    @Test
    void testFormatToLocalDateTime_ValidDate() throws Exception {
        testCell.setCellValue("12/3/24 15:30");

        LocalDateTime result = cellFormatter.formatToLocalDateTime(testCell, "d/M/yy HH:mm");

        assertNotNull(result);
        assertEquals(LocalDateTime.of(2024, 3, 12, 15, 30), result);
    }

    @Test
    void testFormatToLocalDateTime_InvalidDate_ThrowsException() {
        testCell.setCellValue("Invalid Date");

        CellFormatException exception = assertThrows(CellFormatException.class, () -> {
            cellFormatter.formatToLocalDateTime(testCell, "d/M/yy HH:mm");
        });

        assertEquals("Cannot convert Invalid Date to LocalDateTime.", exception.getMessage());
    }

    @Test
    void testFormatToLocalDateTime_NullValue_ReturnsNull() throws Exception {
        testCell.setCellValue("(null)");

        LocalDateTime result = cellFormatter.formatToLocalDateTime(testCell, "d/M/yy HH:mm");

        assertNull(result);
    }

    @Test
    void testFormatToInteger_ValidInteger() throws Exception {
        testCell.setCellValue("123");

        Integer result = cellFormatter.formatToInteger(testCell);

        assertNotNull(result);
        assertEquals(123, result);
    }

    @Test
    void testFormatToInteger_InvalidInteger_ThrowsException() {
        testCell.setCellValue("ABC");

        CellFormatException exception = assertThrows(CellFormatException.class, () -> {
            cellFormatter.formatToInteger(testCell);
        });

        assertEquals("Cannot convert ABC to Integer.", exception.getMessage());
    }

    @Test
    void testFormatToInteger_NullValue_ReturnsNull() throws Exception {
        testCell.setCellValue("(null)");

        Integer result = cellFormatter.formatToInteger(testCell);

        assertNull(result);
    }

    @Test
    void testFormatToLong_ValidLong() throws Exception {
        testCell.setCellValue("9876543210");

        Long result = cellFormatter.formatToLong(testCell);

        assertNotNull(result);
        assertEquals(9876543210L, result);
    }

    @Test
    void testFormatToLong_InvalidLong_ThrowsException() {
        testCell.setCellValue("XYZ");

        CellFormatException exception = assertThrows(CellFormatException.class, () -> {
            cellFormatter.formatToLong(testCell);
        });

        assertEquals("Cannot convert XYZ to Long.", exception.getMessage());
    }

    @Test
    void testFormatToLong_NullValue_ReturnsNull() throws Exception {
        testCell.setCellValue("(null)");

        Long result = cellFormatter.formatToLong(testCell);

        assertNull(result);
    }

    @Test
    void testFormatToString_ValidString() {
        testCell.setCellValue("Valid String");

        String result = cellFormatter.formatToString(testCell);

        assertNotNull(result);
        assertEquals("Valid String", result);
    }

    @Test
    void testFormatToString_NullValue_ReturnsNull() {
        testCell.setCellValue("(null)");

        String result = cellFormatter.formatToString(testCell);

        assertNull(result);
    }

    @Test
    void testFormatToString_EmptyString_ReturnsEmpty() {
        testCell.setCellValue("");

        String result = cellFormatter.formatToString(testCell);

        assertNotNull(result);
        assertEquals("", result);
    }
}
