package com.tus.individual.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.tus.individual.service.IQueryControllerService;
import com.tus.individual.service.impl.OpenAIService;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/training")
public class TrainingController {
    private final IQueryControllerService queryControllerService;
    private final OpenAIService openAiService;

    @Autowired
    public TrainingController(IQueryControllerService queryControllerService, OpenAIService openAiService) {
        this.queryControllerService = queryControllerService;
        this.openAiService = openAiService;
    }

    @GetMapping("/plan")
    public ResponseEntity<Map<String, Object>> generateTrainingPlan(
            @RequestParam String teamName,
            @RequestParam String type) {
        
        System.out.println("📡 Received request for training plan: Team=" + teamName + ", Type=" + type);

        // 1️⃣ Determine which stat to fetch
        Double efficiency;
        String statType;
        
        switch (type) {
            case "Dribbling":
                efficiency = queryControllerService.findDribblingEfficiencyByTeamName(teamName);
                statType = "Dribbling Efficiency";
                break;
            case "Passing":
                efficiency = queryControllerService.findPassingEfficiencyByTeamName(teamName);
                statType = "Passing Efficiency";
                break;
            case "Shooting":
                efficiency = queryControllerService.findShootingEfficiencyByTeamName(teamName);
                statType = "Shooting Efficiency";
                break;
            case "Cardio":
                efficiency = 0.0; // No stat needed for cardio training
                statType = "Cardio Training";
                break;
            case "Strength Training":
                efficiency = 0.0; // No stat needed for general strength training
                statType = "Strength Training";
                break;
            default:
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Invalid training type"));
        }

        // 2️⃣ Debug output
        System.out.println("📊 " + statType + " for " + teamName + ": " + efficiency);

        // 3️⃣ If the stat is 0.0 and it's a technical skill (not Cardio/Strength), return an error
        if (efficiency == 0.0 && !type.equals("Cardio") && !type.equals("Strength Training")) {
            System.out.println("❌ No stats found for " + type + "!");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "No stats found for this team in " + type));
        }

        // 4️⃣ Create the training prompt
        String trainingPrompt = createTrainingPrompt(efficiency, statType, type);

        // 5️⃣ Send request to OpenAI API
        String trainingPlan = openAiService.getTrainingPlan(trainingPrompt);

        // 6️⃣ Return JSON Response
        Map<String, Object> response = new HashMap<>();
        response.put("teamName", teamName);
        response.put("trainingType", type);
        response.put("trainingPlan", trainingPlan);
        return ResponseEntity.ok(response);
    }

    private String createTrainingPrompt(Double efficiency, String statType, String trainingType) {
        StringBuilder prompt = new StringBuilder();

        // General introduction
        prompt.append("Create a training session to improve ").append(statType).append(".\n");

        // If it's NOT Strength Training or Cardio, include the stats
        if (!trainingType.equals("Strength Training") && !trainingType.equals("Cardio")) {
            // Convert negative efficiency values into a percentage
            String efficiencyFormatted = efficiency < 0 ? String.format("%.2f%%", efficiency * -100) : String.format("%.2f", efficiency);
            prompt.append(statType).append(": ").append(efficiencyFormatted).append(".\n");
            prompt.append("Explain what this stat represents and how improving it will benefit the team.\n");
        }

        // Add general training instructions
        prompt.append("Use bullet points and clear formatting. Also don't refer to the sport as soccer, its football\n");

        // Special handling for Strength Training & Cardio
        if (trainingType.equals("Strength Training") || trainingType.equals("Cardio")) {
            prompt.append("At the end, justify why this training plan is important for overall performance in football.\n");
        } else {
            prompt.append("Provide a summary at the end, making sure you include the actual stat and what it means.\n");
        }

        return prompt.toString();
    }

}

