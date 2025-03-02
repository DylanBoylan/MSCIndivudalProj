package com.tus.individual.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.tus.group_project.model.CallFailure;
import com.tus.group_project.service.ICallFailureService;

@RestController
@RequestMapping("/api/call-failures")
public class CallFailureController {
	private static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private ICallFailureService callFailureService;
    
    @Autowired // Injected via constructor
    public CallFailureController(ICallFailureService callFailureService) {
        this.callFailureService = callFailureService;
    }
    
    /* A valid Query with VEA3 model and the time stamp's 2020-11-01 17:15:00/2020-11-01%2017:30:00 returns 258 records.
     * http://localhost:8081/api/call-failures/count?phoneModel=VEA3&startDate=2020-11-01 17:15:00&endDate=2020-11-01%2017:30:00 */
    @PreAuthorize("hasRole('CUSTOMER_SERVICE')")
    @GetMapping("/count")
    public ResponseEntity<Map<String, Object>> getFailureCount(@RequestParam String phoneModel,
                                               @RequestParam String startDate,
                                               @RequestParam String endDate) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime startDateTime = LocalDateTime.parse(startDate, formatter);
        LocalDateTime endDateTime = LocalDateTime.parse(endDate, formatter);
        
        List<Object[]> results = callFailureService.getCallFailureCount(phoneModel, startDateTime, endDateTime);
        
        List<Map<String, Object>> failures = new ArrayList<>();
        for (Object[] result : results) {
            Map<String, Object> failureData = new HashMap<>();
            failureData.put("eventId", result[0]);
            failureData.put("causeCode", result[1]);
            failureData.put("imsi", result[2]);
            failureData.put("dateTime", ((LocalDateTime) result[3]).format(formatter));  // Convert LocalDateTime to String
            failures.add(failureData);
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("phoneModel", phoneModel);
        response.put("failures", failures);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    
    /* A valid Query with time stamp's 2020-11-01 17:15:00/2020-11-01%2017:30:00 returns 6 results.
     * http://localhost:8081/api/call-failures/imsis?startTime=2020-11-01 17:15:00&endTime=2020-11-01%2017:30:00 */
    @GetMapping("/imsis")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'SUPPORT_ENGINEER')")
    public ResponseEntity<Map<String, Object>> getImsisWithFailures(
            @RequestParam String startTime,
            @RequestParam String endTime) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATETIME_FORMAT);
        LocalDateTime startDateTime = LocalDateTime.parse(startTime, formatter);
        LocalDateTime endDateTime = LocalDateTime.parse(endTime, formatter);

        List<Long> imsis = callFailureService.getImsisWithFailures(startDateTime, endDateTime);

        return ResponseEntity.ok(Map.of(
            "startTime", startTime,
            "endTime", endTime,
            "imsis", imsis
        ));
    }
    
    /* http://localhost:8081/api/call-failures/by-imsi?imsi=344930000000011 */
    @PreAuthorize("hasRole('CUSTOMER_SERVICE')")
    @GetMapping("/by-imsi")
    public ResponseEntity<Map<String, Object>> getFailuresByImsi(@RequestParam Long imsi) {
        List<Object[]> results = callFailureService.getFailuresByImsi(imsi);
        
        List<Map<String, Object>> failures = new ArrayList<>();
        for (Object[] result : results) {
            Map<String, Object> failureData = new HashMap<>();
            failureData.put("eventId", result[0]);
            failureData.put("causeCode", result[1]);
            failureData.put("description", result[2]);
            failureData.put("dateTime", result[3]);
            failures.add(failureData);
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("imsi", imsi);
        response.put("failures", failures);
        
        return ResponseEntity.ok(response);
    }
    
    // http://localhost:8081/api/call-failures/count-by-imsi?startDate=2020-11-01%2017:15:00&endDate=2020-11-01%2017:30:00
    @PreAuthorize("hasAnyRole('SUPPORT_ENGINEER', 'NETWORK_MANAGEMENT_ENGINEER')")
    @GetMapping("/count-by-imsi")
    public ResponseEntity<Map<String, Object>> getFailureStatsByImsi(
            @RequestParam String startDate,
            @RequestParam String endDate) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATETIME_FORMAT);
        LocalDateTime startDateTime = LocalDateTime.parse(startDate, formatter);
        LocalDateTime endDateTime = LocalDateTime.parse(endDate, formatter);

        List<Object[]> results = callFailureService.getCallFailureStatsByImsi(startDateTime, endDateTime);

        List<Map<String, Object>> failureStats = new ArrayList<>();
        for (Object[] result : results) {
            Map<String, Object> data = new HashMap<>();
            data.put("imsi", result[0]);
            data.put("failureCount", result[1]);
            data.put("totalDuration", result[2]);
            failureStats.add(data);
        }

        return ResponseEntity.ok(Map.of(
            "startDate", startDate,
            "endDate", endDate,
            "failureStats", failureStats
        ));
    }
    
    // US_12
 	//http://localhost:8081/api/call-failures/by-imsi-time?imsi=344930000000011&startDate=2020-01-11 17:15:00&endDate=2020-01-11 17:30:00
 	@GetMapping("/by-imsi-time")
 	@PreAuthorize("hasRole('CUSTOMER_SERVICE')")
 	public ResponseEntity<?> failuresByImsi(@RequestParam Long imsi, @RequestParam String startDate,
 			@RequestParam String endDate) {
 		try {
 			/*if (imsi == null || String.valueOf(imsi).length() < 15) { 
             	throw new IllegalArgumentException("Invalid IMSI");
         	}*/
 			
 			DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATETIME_FORMAT);
 			LocalDateTime startDateTime = LocalDateTime.parse(startDate, formatter);
 			LocalDateTime endDateTime = LocalDateTime.parse(endDate, formatter);
 			
 			// Validate Date Range
 	        if (startDateTime.isAfter(endDateTime)) {
 	            throw new IllegalArgumentException("Invalid date range");
 	        }
 			
 			List<Object[]> results = callFailureService.findFailuresByImsiAndTimePeriod(imsi, startDateTime, endDateTime);
 			List<Map<String, Object>> failures = new ArrayList<>();
 			for (Object[] result : results) {
 				Map<String, Object> failureData = new HashMap<>();
 				failureData.put("eventId", result[0]);
 				failureData.put("causeCode", result[1]);
 				failureData.put("dateTime", ((LocalDateTime) result[2]).format(formatter)); // Convert LocalDateTime to String
 				failures.add(failureData);
 			}

 			Map<String, Object> response = new HashMap<>();
 			response.put("imsi", imsi);
 			response.put("failures", failures);
 			return ResponseEntity.status(HttpStatus.OK).body(response);
 		} catch (IllegalArgumentException e) {
 			return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
 		} catch (Exception e) {
 			return ResponseEntity.internalServerError().body(Map.of("error", "Unexpected error occurred"));
 		}
 	}
 	
 	
 	//User Story 8
 	//http://localhost:8081/api/call-failures/summary?phoneModel=VEA3
 	@PreAuthorize("hasRole('NETWORK_MANAGEMENT_ENGINEER')")
    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getFailureSummaryByModel(@RequestParam String phoneModel) {
        List<Object[]> results = callFailureService.getFailureSummaryByModel(phoneModel);
        List<Map<String, Object>> failures = new ArrayList<>();
        
        for (Object[] result : results) {
            Map<String, Object> failureData = new HashMap<>();
            failureData.put("eventId", result[0]);
            failureData.put("causeCode", result[1]);
            failureData.put("occurrences", result[2]);
            failures.add(failureData);
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("phoneModel", phoneModel);
        response.put("failures", failures);
        
        return ResponseEntity.ok(response);
    }

 	//http://localhost:8081/api/call-failures/top10?startDate=2020-02-19 17:15:00&endDate=2020-02-25 17:30:00
 	@PreAuthorize("hasRole('NETWORK_MANAGEMENT_ENGINEER')")
 	@GetMapping("/top10")
    public ResponseEntity<Map<String, Object>> getTop10Failures(@RequestParam String startDate, 
    		@RequestParam String endDate) {
 		try {
 			
 			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
 			LocalDateTime startDateTime = LocalDateTime.parse(startDate, formatter);
 			LocalDateTime endDateTime = LocalDateTime.parse(endDate, formatter);

 	
	        List<Object[]> results = callFailureService.getTop10Failures(startDateTime, endDateTime);
	        List<Map<String, Object>> failures = new ArrayList<>();
 			for (Object[] result : results) {
 				Map<String, Object> failureData = new HashMap<>();
 				failureData.put("eventId", result[0]);
 				failureData.put("causeCode", result[1]);
 				failureData.put("market", result[2]);
 				failureData.put("operator", result[3]);
 				failureData.put("cellId", result[4]);
 				failureData.put("failureCount", result[5]);
 				failures.add(failureData);
 			}

 			return ResponseEntity.ok(Map.of(
 		            "startDate", startDate,
 		            "endDate", endDate,
 		            "failures", failures
 		        ));
 			
	    }catch (DateTimeParseException e) {
			return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
		} catch (Exception e) {
			return ResponseEntity.internalServerError().body(Map.of("error", "Unexpected error occurred"));
		}
 	}
 	
 	//US18
 	@PreAuthorize("hasRole('NETWORK_MANAGEMENT_ENGINEER')")
 	@GetMapping("/top10-imsis")
 	public ResponseEntity<List<Map<String, Object>>> getTop10Imsis(@RequestParam String startDate, @RequestParam String endDate) {
 	    LocalDateTime start = LocalDateTime.parse(startDate);
 	    LocalDateTime end = LocalDateTime.parse(endDate);

 	    List<Object[]> results = callFailureService.getTop10ImsisWithFailures(start, end);
 	    List<Map<String, Object>> formattedResults = new ArrayList<>();

 	    for (Object[] result : results) {
 	        Map<String, Object> map = new HashMap<>();
 	        map.put("imsi", result[0]);
 	        map.put("failureCount", result[1]);
 	        formattedResults.add(map);
 	    }

 	    return ResponseEntity.ok(formattedResults);
 	}

}
