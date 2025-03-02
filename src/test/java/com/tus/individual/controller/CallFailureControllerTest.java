package com.tus.individual.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.tus.individual.service.ICallFailureService;

@ExtendWith(MockitoExtension.class)
class CallFailureControllerTest {

	@Mock
	private ICallFailureService callFailureService;

	private CallFailureController callFailureController;

	private MockMvc mockMvc;

	@BeforeEach
	void setUp() {
		callFailureController = new CallFailureController(callFailureService); // Pass mocked service via constructor
		mockMvc = MockMvcBuilders.standaloneSetup(callFailureController).build();
	}

	@Test
	void testGetFailureCount() {
		String phoneModel = "SamsungS22";
		String startDate = "2024-02-01 00:00:00";
		String endDate = "2024-02-04 23:59:59";

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		LocalDateTime startDateTime = LocalDateTime.parse(startDate, formatter);
		LocalDateTime endDateTime = LocalDateTime.parse(endDate, formatter);

		// Mock response data
		List<Object[]> mockFailures = Arrays.asList(
				new Object[] { 1L, 100, "123456789", LocalDateTime.parse("2024-02-01 12:30:00", formatter) },
				new Object[] { 2L, 200, "987654321", LocalDateTime.parse("2024-02-02 14:30:00", formatter) });
		when(callFailureService.getCallFailureCount(phoneModel, startDateTime, endDateTime)).thenReturn(mockFailures);

		// Execute controller method
		ResponseEntity<Map<String, Object>> responseEntity = callFailureController.getFailureCount(phoneModel,
				startDate, endDate);

		// Extract response body
		Map<String, Object> responseBody = responseEntity.getBody();

		// Assertions
		assertThat(responseEntity.getStatusCodeValue()).isEqualTo(200); // Check HTTP Status is OK (200)
		assertThat(responseBody).isNotNull();
		assertThat(responseBody).containsEntry("phoneModel", phoneModel); // Check phone model is correct

		// Validate "failures" list
		assertThat(responseBody).containsKey("failures");
		List<Map<String, Object>> failures = (List<Map<String, Object>>) responseBody.get("failures");
		assertThat(failures).isNotEmpty().hasSize(2); // Ensure there are exactly 2 failures

		// Validate first failure record
		Map<String, Object> firstFailure = failures.get(0);
		assertThat(firstFailure.get("eventId")).isEqualTo(1L);
		assertThat(firstFailure.get("causeCode")).isEqualTo(100);
		assertThat(firstFailure.get("imsi")).isEqualTo("123456789");
		assertThat(firstFailure.get("dateTime")).isEqualTo("2024-02-01 12:30:00");

		// Validate second failure record
		Map<String, Object> secondFailure = failures.get(1);
		assertThat(secondFailure.get("eventId")).isEqualTo(2L);
		assertThat(secondFailure.get("causeCode")).isEqualTo(200);
		assertThat(secondFailure.get("imsi")).isEqualTo("987654321");
		assertThat(secondFailure.get("dateTime")).isEqualTo("2024-02-02 14:30:00");

		System.out.println("✅ Test Passed: getFailureCount()");

	}

	@Test
	void testGetFailureCountRestAPI() throws Exception {
		String phoneModel = "SamsungS22";
		String startDate = "2024-02-01 00:00:00";
		String endDate = "2024-02-04 23:59:59";

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		LocalDateTime startDateTime = LocalDateTime.parse(startDate, formatter);
		LocalDateTime endDateTime = LocalDateTime.parse(endDate, formatter);

		// Mock response data
		List<Object[]> mockFailures = Arrays.asList(
				new Object[] { 1L, 100, "123456789", LocalDateTime.parse("2024-02-01 12:30:00", formatter) },
				new Object[] { 2L, 200, "987654321", LocalDateTime.parse("2024-02-02 14:30:00", formatter) });

		when(callFailureService.getCallFailureCount(phoneModel, startDateTime, endDateTime)).thenReturn(mockFailures);

		mockMvc.perform(get("/api/call-failures/count").param("phoneModel", phoneModel).param("startDate", startDate)
				.param("endDate", endDate).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$.phoneModel").value(phoneModel)).andExpect(jsonPath("$.failures").isArray())
				.andExpect(jsonPath("$.failures.length()").value(2)) // Expecting 2 records
				.andExpect(jsonPath("$.failures[0].eventId").value(1))
				.andExpect(jsonPath("$.failures[0].causeCode").value(100))
				.andExpect(jsonPath("$.failures[0].imsi").value("123456789"))
				.andExpect(jsonPath("$.failures[0].dateTime").value("2024-02-01 12:30:00"))
				.andExpect(jsonPath("$.failures[1].eventId").value(2))
				.andExpect(jsonPath("$.failures[1].causeCode").value(200))
				.andExpect(jsonPath("$.failures[1].imsi").value("987654321"))
				.andExpect(jsonPath("$.failures[1].dateTime").value("2024-02-02 14:30:00"));
	}

	@Test
	void testGetImsisWithFailuresResetAPI() throws Exception {
		String startTime = "2024-02-01 00:00:00";
		String endTime = "2024-02-04 23:59:59";
		List<Long> mockImsis = List.of(123456789012345L, 987654321098765L);

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		LocalDateTime startDateTime = LocalDateTime.parse(startTime, formatter);
		LocalDateTime endDateTime = LocalDateTime.parse(endTime, formatter);

		when(callFailureService.getImsisWithFailures(startDateTime, endDateTime)).thenReturn(mockImsis);

		mockMvc.perform(get("/api/call-failures/imsis").param("startTime", startTime).param("endTime", endTime)
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$.startTime").value(startTime)).andExpect(jsonPath("$.endTime").value(endTime))
				.andExpect(jsonPath("$.imsis").isArray()).andExpect(jsonPath("$.imsis[0]").value(123456789012345L))
				.andExpect(jsonPath("$.imsis[1]").value(987654321098765L));
	}

	@Test
	void testGetFailuresByImsi_ValidImsiWithNullCauseCode() throws Exception {
		Long imsi = 123456789012345L;
		List<Object[]> mockResults = new ArrayList<>();
		mockResults.add(new Object[] { 1001, 500, "Description for event 1001", "2020-11-01T17:19:00" });
		mockResults.add(new Object[] { 1002, null, "Description for event 1002", "2020-11-01T17:19:01" });

		when(callFailureService.getFailuresByImsi(imsi)).thenReturn(mockResults);

		mockMvc.perform(get("/api/call-failures/by-imsi").param("imsi", imsi.toString())
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$.imsi").value(imsi)).andExpect(jsonPath("$.failures").isArray())
				.andExpect(jsonPath("$.failures[0].eventId").value(1001))
				.andExpect(jsonPath("$.failures[0].causeCode").value(500))
				.andExpect(jsonPath("$.failures[0].description").value("Description for event 1001"))
				.andExpect(jsonPath("$.failures[0].dateTime").value("2020-11-01T17:19:00"))
				.andExpect(jsonPath("$.failures[1].eventId").value(1002))
				.andExpect(jsonPath("$.failures[1].causeCode").doesNotExist())
				.andExpect(jsonPath("$.failures[1].description").value("Description for event 1002"))
				.andExpect(jsonPath("$.failures[1].dateTime").value("2020-11-01T17:19:01"));
	}

	@Test
	void testGetFailuresByImsi_ValidImsi() throws Exception {
		Long imsi = 123456789012345L;
		List<Object[]> mockResults = new ArrayList<>();
		mockResults.add(new Object[] { 1001, 500, "Description for event 1001", "2020-11-01T17:19:00" });
		mockResults.add(new Object[] { 1002, 600, "Description for event 1002", "2020-11-01T17:19:01" });
		mockResults.add(new Object[] { 1003, 700, "Description for event 1003", "2020-11-01T17:19:02" });

		when(callFailureService.getFailuresByImsi(imsi)).thenReturn(mockResults);

		mockMvc.perform(get("/api/call-failures/by-imsi").param("imsi", imsi.toString())
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$.imsi").value(imsi)).andExpect(jsonPath("$.failures").isArray())
				.andExpect(jsonPath("$.failures[0].eventId").value(1001))
				.andExpect(jsonPath("$.failures[0].causeCode").value(500))
				.andExpect(jsonPath("$.failures[0].description").value("Description for event 1001"))
				.andExpect(jsonPath("$.failures[0].dateTime").value("2020-11-01T17:19:00"))
				.andExpect(jsonPath("$.failures[1].eventId").value(1002))
				.andExpect(jsonPath("$.failures[1].causeCode").value(600))
				.andExpect(jsonPath("$.failures[1].description").value("Description for event 1002"))
				.andExpect(jsonPath("$.failures[1].dateTime").value("2020-11-01T17:19:01"))
				.andExpect(jsonPath("$.failures[2].eventId").value(1003))
				.andExpect(jsonPath("$.failures[2].causeCode").value(700))
				.andExpect(jsonPath("$.failures[2].description").value("Description for event 1003"))
				.andExpect(jsonPath("$.failures[2].dateTime").value("2020-11-01T17:19:02"));
	}

	@Test
	void testFailuresByImsi_ValidImsiAndDates_ShouldReturnList() throws Exception {
		Long imsi = 1234567890L;
		String startDate = "2024-02-01 00:00:00";
		String endDate = "2024-02-04 23:59:59";

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		LocalDateTime startDateTime = LocalDateTime.parse(startDate, formatter);
		LocalDateTime endDateTime = LocalDateTime.parse(endDate, formatter);

		// Mock response data
		List<Object[]> mockFailures = Arrays.asList(
				new Object[] { 1L, 100, LocalDateTime.parse("2024-02-01 12:30:00", formatter) },
				new Object[] { 2L, 200, LocalDateTime.parse("2024-02-02 14:30:00", formatter) });

		when(callFailureService.findFailuresByImsiAndTimePeriod(imsi, startDateTime, endDateTime))
				.thenReturn(mockFailures);

		mockMvc.perform(get("/api/call-failures/by-imsi-time").param("imsi", imsi.toString())
				.param("startDate", startDate).param("endDate", endDate).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.imsi").value(imsi))
				.andExpect(jsonPath("$.failures").isArray()).andExpect(jsonPath("$.failures.length()").value(2))
				.andExpect(jsonPath("$.failures[0].eventId").value(1))
				.andExpect(jsonPath("$.failures[0].causeCode").value(100))
				.andExpect(jsonPath("$.failures[0].dateTime").value("2024-02-01 12:30:00"))
				.andExpect(jsonPath("$.failures[1].eventId").value(2))
				.andExpect(jsonPath("$.failures[1].causeCode").value(200))
				.andExpect(jsonPath("$.failures[1].dateTime").value("2024-02-02 14:30:00"));

		verify(callFailureService, times(1)).findFailuresByImsiAndTimePeriod(imsi, startDateTime, endDateTime);
	}
	@Test
	void testFailuresByImsi_NoFailures_ShouldReturnEmptyList() throws Exception {
        Long imsi = 1234567890L;
        String startDate = "2024-02-01 00:00:00";
        String endDate = "2024-02-04 23:59:59";

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime startDateTime = LocalDateTime.parse(startDate, formatter);
        LocalDateTime endDateTime = LocalDateTime.parse(endDate, formatter);

        when(callFailureService.findFailuresByImsiAndTimePeriod(imsi, startDateTime, endDateTime))
            .thenReturn(List.of()); // No failures

        mockMvc.perform(get("/api/call-failures/by-imsi-time")
                .param("imsi", imsi.toString())
                .param("startDate", startDate)
                .param("endDate", endDate)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.imsi").value(imsi))
                .andExpect(jsonPath("$.failures").isArray())
                .andExpect(jsonPath("$.failures.length()").value(0));
    }
	
	@Test
	void testFailuresByImsi_InvalidDateRange_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/api/call-failures/by-imsi-time")
                .param("imsi", "1234567890")
                .param("startDate", "2024-02-04 23:59:59")  // End date before start date
                .param("endDate", "2024-02-01 00:00:00")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid date range"));
    }
    
    @Test
    void testGetFailuresByImsi_InvalidImsi() throws Exception {
        Long imsi = -1L;
        when(callFailureService.getFailuresByImsi(imsi)).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/api/call-failures/by-imsi")
                .param("imsi", imsi.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.imsi").value(imsi))
                .andExpect(jsonPath("$.failures").isEmpty());
    }

    @Test
    void testGetFailuresByImsi_NoRecords() throws Exception {
        Long imsi = 999999999999999L;
        when(callFailureService.getFailuresByImsi(imsi)).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/api/call-failures/by-imsi")
                .param("imsi", imsi.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.imsi").value(imsi))
                .andExpect(jsonPath("$.failures").isEmpty());
    }
    
    @Test
    void testGetFailureStatsByImsi_Success() throws Exception {
        // Define test parameters
        String startDate = "2020-11-01 17:15:00";
        String endDate = "2020-11-01 17:30:00";

        // Prepare mock service response
        List<Object[]> mockResults = List.of(
            new Object[]{"123456789012345", 5L, 300L}, // IMSI, Failures, Duration
            new Object[]{"987654321098765", 2L, 120L}
        );

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime startDateTime = LocalDateTime.parse(startDate, formatter);
        LocalDateTime endDateTime = LocalDateTime.parse(endDate, formatter);

        when(callFailureService.getCallFailureStatsByImsi(startDateTime, endDateTime))
                .thenReturn(mockResults);

        // Execute controller method
        ResponseEntity<Map<String, Object>> responseEntity =
            callFailureController.getFailureStatsByImsi(startDate, endDate);

        // Extract response body
        Map<String, Object> responseBody = responseEntity.getBody();

        // Assertions
        assertThat(responseEntity.getStatusCodeValue()).isEqualTo(200);
        assertThat(responseBody).isNotNull();
        assertThat(responseBody).containsEntry("startDate", startDate);
        assertThat(responseBody).containsEntry("endDate", endDate);
        assertThat(responseBody).containsKey("failureStats");

        List<Map<String, Object>> failureStats = (List<Map<String, Object>>) responseBody.get("failureStats");
        assertThat(failureStats).isNotEmpty().hasSize(2);

        // Validate first record
        Map<String, Object> firstFailure = failureStats.get(0);
        assertThat(firstFailure.get("imsi")).isEqualTo("123456789012345");
        assertThat(firstFailure.get("failureCount")).isEqualTo(5L);
        assertThat(firstFailure.get("totalDuration")).isEqualTo(300L);

        // Validate second record
        Map<String, Object> secondFailure = failureStats.get(1);
        assertThat(secondFailure.get("imsi")).isEqualTo("987654321098765");
        assertThat(secondFailure.get("failureCount")).isEqualTo(2L);
        assertThat(secondFailure.get("totalDuration")).isEqualTo(120L);

        System.out.println("✅ Test Passed: getFailureStatsByImsi()");
    }

    @Test
    void testGetFailureStatsByImsi_RestAPI() throws Exception {
        String startDate = "2020-11-01 17:15:00";
        String endDate = "2020-11-01 17:30:00";

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime startDateTime = LocalDateTime.parse(startDate, formatter);
        LocalDateTime endDateTime = LocalDateTime.parse(endDate, formatter);

        // Mock response
        List<Object[]> mockResults = List.of(
            new Object[]{"123456789012345", 5L, 300L},
            new Object[]{"987654321098765", 2L, 120L}
        );

        when(callFailureService.getCallFailureStatsByImsi(startDateTime, endDateTime)).thenReturn(mockResults);

        mockMvc.perform(get("/api/call-failures/count-by-imsi")
                .param("startDate", startDate)
                .param("endDate", endDate)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.startDate").value(startDate))
                .andExpect(jsonPath("$.endDate").value(endDate))
                .andExpect(jsonPath("$.failureStats").isArray())
                .andExpect(jsonPath("$.failureStats.length()").value(2))
                .andExpect(jsonPath("$.failureStats[0].imsi").value("123456789012345"))
                .andExpect(jsonPath("$.failureStats[0].failureCount").value(5))
                .andExpect(jsonPath("$.failureStats[0].totalDuration").value(300))
                .andExpect(jsonPath("$.failureStats[1].imsi").value("987654321098765"))
                .andExpect(jsonPath("$.failureStats[1].failureCount").value(2))
                .andExpect(jsonPath("$.failureStats[1].totalDuration").value(120));
    }
    
    @Test
    void testGetFailureSummaryByModel_Success() throws Exception {
        String phoneModel = "VEA3";

        // Prepare mock results as would be returned from the service
        List<Object[]> mockResults = List.of(
            new Object[] { "E001", "C001", 10L },
            new Object[] { "E002", "C002", 5L }
        );
        when(callFailureService.getFailureSummaryByModel(phoneModel)).thenReturn(mockResults);

        // Perform GET request and assert the JSON response
        mockMvc.perform(get("/api/call-failures/summary")
                .param("phoneModel", phoneModel)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.phoneModel").value(phoneModel))
            .andExpect(jsonPath("$.failures").isArray())
            .andExpect(jsonPath("$.failures.length()").value(2))
            .andExpect(jsonPath("$.failures[0].eventId").value("E001"))
            .andExpect(jsonPath("$.failures[0].causeCode").value("C001"))
            .andExpect(jsonPath("$.failures[0].occurrences").value(10))
            .andExpect(jsonPath("$.failures[1].eventId").value("E002"))
            .andExpect(jsonPath("$.failures[1].causeCode").value("C002"))
            .andExpect(jsonPath("$.failures[1].occurrences").value(5));
    }

    @Test
    void testGetFailureSummaryByModel_EmptyList() throws Exception {
        String phoneModel = "NON_EXISTENT_MODEL";
        
        // Simulate no records found
        when(callFailureService.getFailureSummaryByModel(phoneModel)).thenReturn(List.of());

        mockMvc.perform(get("/api/call-failures/summary")
                .param("phoneModel", phoneModel)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.phoneModel").value(phoneModel))
            .andExpect(jsonPath("$.failures").isArray())
            .andExpect(jsonPath("$.failures.length()").value(0));
    }

    @Test
    void testGetFailureSummaryByModel_MissingParameter() throws Exception {
        // When no phoneModel parameter is provided, the request should fail (typically with a 400 Bad Request)
        mockMvc.perform(get("/api/call-failures/summary")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }
}	
