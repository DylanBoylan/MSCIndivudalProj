package com.tus.individual.service.impl;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OpenAIService {
    private final String OPENAI_API_KEY = "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"; // 🔴 Replace with your actual API key
    private final String OPENAI_URL = "https://api.openai.com/v1/chat/completions";

    public String getTrainingPlan(String prompt) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(OPENAI_API_KEY);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-4");
        requestBody.put("messages", List.of(
                Map.of("role", "system", "content", "You are a football coach."),
                Map.of("role", "user", "content", prompt)
        ));
        requestBody.put("temperature", 0.7);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(OPENAI_URL, HttpMethod.POST, request, Map.class);
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
