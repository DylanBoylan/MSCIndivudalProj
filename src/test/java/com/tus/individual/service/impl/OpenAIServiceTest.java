package com.tus.individual.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@ExtendWith(MockitoExtension.class)
public class OpenAIServiceTest {

    @Mock
    private RestTemplate restTemplate;  // ✅ Properly mocked

    @InjectMocks
    private OpenAIService openAIService;

    private final String MOCK_PROMPT = "Create a football training plan";

    @BeforeEach
    void setUp() {
        openAIService = new OpenAIService(restTemplate);  // ✅ Ensure dependency is injected
    }

    // ✅ Test for successful OpenAI response
    @Test
    void testGetTrainingPlan_Success() {
        Map<String, Object> mockResponse = new HashMap<>();
        Map<String, String> message = Map.of("content", "Here is your football training plan.");
        Map<String, Object> choice = Map.of("message", message);
        mockResponse.put("choices", List.of(choice));

        ResponseEntity<Map> responseEntity = new ResponseEntity<>(mockResponse, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(), eq(Map.class)))
                .thenReturn(responseEntity);

        String result = openAIService.getTrainingPlan(MOCK_PROMPT);

        assertNotNull(result);
        assertEquals("Here is your football training plan.", result);
    }

    // ✅ Test for OpenAI returning an empty response
    @Test
    void testGetTrainingPlan_EmptyResponse() {
        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("choices", List.of()); // Empty choices list

        ResponseEntity<Map> responseEntity = new ResponseEntity<>(mockResponse, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(), eq(Map.class)))
                .thenReturn(responseEntity);

        String result = openAIService.getTrainingPlan(MOCK_PROMPT);

        assertEquals("❌ Failed to generate training plan: No response from OpenAI.", result);
    }

    // ✅ Test for OpenAI API throwing an exception
    @Test
    void testGetTrainingPlan_ApiError() {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(), eq(Map.class)))
                .thenThrow(new RuntimeException("API is down"));  // ✅ Force an exception

        String result = openAIService.getTrainingPlan(MOCK_PROMPT);

        // ✅ Debugging output
        System.out.println("Actual Response: " + result);

        // ✅ Ensure error message is correctly handled
        assertTrue(result.contains("❌ Failed to generate training plan"));
    }
}
