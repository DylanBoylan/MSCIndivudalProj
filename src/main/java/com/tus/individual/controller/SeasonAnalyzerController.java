package com.tus.individual.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.tus.individual.service.IQueryControllerService;
import com.tus.individual.service.impl.OpenAIService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/season")
public class SeasonAnalyzerController {
    private final IQueryControllerService queryControllerService;
    private final OpenAIService openAiService;

    @Autowired
    public SeasonAnalyzerController(IQueryControllerService queryControllerService, OpenAIService openAiService) {
        this.queryControllerService = queryControllerService;
        this.openAiService = openAiService;
    }

    @GetMapping("/analyze")
    public ResponseEntity<Map<String, Object>> analyzeSeason(
            @RequestParam String teamName, @RequestParam String analysisType) {
        
        Integer gamesWon = queryControllerService.getTotalGamesWon(teamName);
        
        if (gamesWon == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "No data found for this team."));
        }

        String aiPrompt;
        if ("General Season".equals(analysisType)) {
            aiPrompt = createSeasonAnalysisPrompt(teamName, gamesWon);
        } else if ("Players".equals(analysisType)) {
            aiPrompt = createPlayerAnalysisPrompt(teamName, gamesWon);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Invalid analysis type."));
        }

        System.out.println("📡 AI Prompt Sent: " + aiPrompt); // ✅ Debugging AI prompt

        String aiReport = openAiService.getTrainingPlan(aiPrompt); // Sends the prompt to OpenAI

        Map<String, Object> response = new HashMap<>();
        response.put("teamName", teamName);
        response.put("gamesWon", gamesWon);
        response.put("aiReport", aiReport);

        return ResponseEntity.ok(response);
    }


    // ✅ Creates AI prompt for General Season Performance
    private String createSeasonAnalysisPrompt(String teamName, int gamesWon) {
        return "Analyze the season performance of " + teamName + " in 2013. " +
               "The team won " + gamesWon + " out of 38 games. " +
               "Provide insights into how they performed, key highlights, and areas for improvement. anything less than 20 wins isnt a great performance,  anything less than 15 is bad";
    }

    // ✅ Creates AI prompt for Player Analysis
    private String createPlayerAnalysisPrompt(String teamName, int gamesWon) {
        return "Pick 3 key players from the 2013 season for " + teamName + ". " +
               "Analyze their contributions based on goals, assists, passing, and defensive work. " +
               "Also, discuss their impact on the " + gamesWon + "-win season and how they influenced the team's success.";
    }
}
