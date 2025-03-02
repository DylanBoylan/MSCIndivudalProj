package com.tus.individual.model;

public class FileProcessingResult {
    private final int rowsProcessed;
    private final int rowsSkipped;

    public FileProcessingResult(int rowsProcessed, int rowsSkipped) {
        this.rowsProcessed = rowsProcessed;
        this.rowsSkipped = rowsSkipped;
    }

    public int getRowsProcessed() {
        return rowsProcessed;
    }

    public int getRowsSkipped() {
        return rowsSkipped;
    }
    
    @Override
    public String toString() {
        return "Processed: " + rowsProcessed + ", Skipped: " + rowsSkipped;
    }
}
