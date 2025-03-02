package com.tus.individual.controller;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tus.individual.dto.FileUploadResponse;
import com.tus.individual.exception.FileFormatException;
import com.tus.individual.exception.FileStorageException;
import com.tus.individual.model.FileProcessingResult;
import com.tus.individual.service.IFileProcessorService;


@RestController
public class FileController {
	private IFileProcessorService fileProcessor;
	
	@Autowired
	public FileController(IFileProcessorService fileProcessor) {
		this.fileProcessor = fileProcessor;
	}
	

	@PostMapping("/import/{filename:.+}")
    @PreAuthorize("hasRole('ADMINISTRATOR')")

	public ResponseEntity<FileUploadResponse> handleFileUpload(@PathVariable String filename) {
	    try {
	        FileProcessingResult result = fileProcessor.uploadDataset(filename);

	        FileUploadResponse response = new FileUploadResponse(
	            result.getRowsProcessed(),
	            result.getRowsSkipped(),
	            "File uploaded successfully"
	        );

	        return ResponseEntity.ok(response);

	    } catch (FileFormatException | FileStorageException e) {
	        return ResponseEntity.badRequest().body(new FileUploadResponse(0, 0, e.getMessage()));
	    } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new FileUploadResponse(0, 0, e.getMessage()));
        }
	}
	
	@GetMapping("/excel-files")
    @PreAuthorize("hasRole('ADMINISTRATOR')")

    public ResponseEntity<List<String>> getExcelFiles() {
        try {
            List<String> files = fileProcessor.getAllExcelFiles();
            return ResponseEntity.ok(files);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Collections.emptyList());
        }
    }

}
