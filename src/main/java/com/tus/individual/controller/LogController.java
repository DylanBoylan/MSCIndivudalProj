package com.tus.individual.controller;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.io.File;

@RestController
@RequestMapping("/logs")
public class LogController {

    private static final String LOG_FILE_PATH = "logs/skipped_rows.log";
   
    @GetMapping("/skipped_rows.log")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<Resource> downloadLogFile() {
        File file = new File(LOG_FILE_PATH);
        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = new FileSystemResource(file);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=skipped_rows.log")
                .body(resource);
    }
}




