package com.tus.individual.dto;


public class FileUploadResponse {
    private int rowsProcessed;
    private int rowsSkipped;
    private String message;

    public FileUploadResponse(int rowsProcessed, int rowsSkipped, String message) {
        this.rowsProcessed = rowsProcessed;
        this.rowsSkipped = rowsSkipped;
        this.message = message;
    }

    public int getRowsProcessed() {
        return rowsProcessed;
    }

    public int getRowsSkipped() {
        return rowsSkipped;
    }

    public String getMessage() {
        return message;
    }

}
