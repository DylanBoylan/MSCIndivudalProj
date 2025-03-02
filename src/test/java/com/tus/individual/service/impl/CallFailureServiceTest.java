package com.tus.individual.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.tus.individual.dao.CallFailureRepository;

@ExtendWith(MockitoExtension.class)
class CallFailureServiceTest {

	@Mock
	private CallFailureRepository callFailureRepository;

	private CallFailureService callFailureService;

	@BeforeEach
	void setUp() {
		callFailureService = new CallFailureService(callFailureRepository); // Constructor injection
	}

	@Test
	void testGetCallFailureCount() {
		String phoneModel = "SamsungS22";
		LocalDateTime startDate = LocalDateTime.of(2024, 2, 1, 0, 0);
		LocalDateTime endDate = LocalDateTime.of(2024, 2, 4, 23, 59);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		// Mock response data
        List<Object[]> mockFailures = Arrays.asList(
            new Object[]{1L, 100, "123456789", LocalDateTime.parse("2024-02-01 00:00:00", formatter)},
            new Object[]{2L, 200, "987654321", LocalDateTime.parse("2024-02-04 23:59:00", formatter)}
        );
		when(callFailureRepository.countFailuresByPhoneModelAndTimeRange(phoneModel, startDate, endDate))
				.thenReturn(mockFailures);

		List<Object[]>failureCount = callFailureService.getCallFailureCount(phoneModel, startDate, endDate);

		assertEquals(2, failureCount.size());
	}
	
	@Test
	void testGetImsisWithFailures() {

        LocalDateTime startTime = LocalDateTime.of(2024, 2, 1, 0, 0);
        LocalDateTime endTime = LocalDateTime.of(2024, 2, 4, 23, 59);

        List<Long> mockImsis = List.of(123456789012345L, 987654321098765L);
        when(callFailureRepository.findDistinctImsisWithFailures(startTime, endTime)).thenReturn(mockImsis);

        List<Long> result = callFailureService.getImsisWithFailures(startTime, endTime);

        assertEquals(2, result.size());
        assertTrue(result.contains(123456789012345L));
        assertTrue(result.contains(987654321098765L));
	}
}
