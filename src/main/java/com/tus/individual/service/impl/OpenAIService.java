package com.tus.individual.service.impl;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class OpenAIService {
    private final RestTemplate restTemplate;

    // ✅ Correct constructor
    public OpenAIService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String getTrainingPlan(String prompt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth("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");

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(Map.of(
            "model", "gpt-4",
            "messages", List.of(
                Map.of("role", "system", "content", "You are a football coach."),
                Map.of("role", "user", "content", prompt)
            ),
            "temperature", 0.7
        ), headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    "https://api.openai.com/v1/chat/completions",
                    HttpMethod.POST,
                    request,
                    Map.class
            );

            Map<String, Object> responseBody = response.getBody();
            if (responseBody != null && responseBody.containsKey("choices")) {
                List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");
                if (!choices.isEmpty() && choices.get(0).containsKey("message")) {
                    Map<String, String> messageContent = (Map<String, String>) choices.get(0).get("message");
                    return messageContent.get("content");
                }
            }
            return "❌ Failed to generate training plan: No response from OpenAI.";
        } catch (Exception e) {
            return "❌ Failed to generate training plan: " + e.getMessage();
        }
    }
}
