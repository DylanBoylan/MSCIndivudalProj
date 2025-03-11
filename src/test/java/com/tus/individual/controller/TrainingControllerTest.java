package com.tus.individual.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.tus.individual.service.IQueryControllerService;
import com.tus.individual.service.impl.OpenAIService;

@ExtendWith(MockitoExtension.class)
public class TrainingControllerTest {

    private MockMvc mockMvc;

    @Mock
    private IQueryControllerService queryControllerService;

    @Mock
    private OpenAIService openAiService;

    @InjectMocks
    private TrainingController trainingController;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(trainingController).build();
    }

    // ✅ Test for Dribbling Training Plan (Valid Case)
    @Test
    void testGenerateTrainingPlan_Dribbling_Success() throws Exception {
        when(queryControllerService.findDribblingEfficiencyByTeamName("Team A")).thenReturn(75.5);
        when(openAiService.getTrainingPlan(anyString())).thenReturn("Dribbling Training Plan Content");

        mockMvc.perform(get("/api/training/plan")
                .param("teamName", "Team A")
                .param("type", "Dribbling"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.teamName").value("Team A"))
                .andExpect(jsonPath("$.trainingType").value("Dribbling"))
                .andExpect(jsonPath("$.trainingPlan").value("Dribbling Training Plan Content"));

        verify(queryControllerService, times(1)).findDribblingEfficiencyByTeamName("Team A");
        verify(openAiService, times(1)).getTrainingPlan(anyString());
    }

    // ✅ Test for Passing Training Plan (Valid Case)
    @Test
    void testGenerateTrainingPlan_Passing_Success() throws Exception {
        when(queryControllerService.findPassingEfficiencyByTeamName("Team A")).thenReturn(82.3);
        when(openAiService.getTrainingPlan(anyString())).thenReturn("Passing Training Plan Content");

        mockMvc.perform(get("/api/training/plan")
                .param("teamName", "Team A")
                .param("type", "Passing"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.teamName").value("Team A"))
                .andExpect(jsonPath("$.trainingType").value("Passing"))
                .andExpect(jsonPath("$.trainingPlan").value("Passing Training Plan Content"));

        verify(queryControllerService, times(1)).findPassingEfficiencyByTeamName("Team A");
        verify(openAiService, times(1)).getTrainingPlan(anyString());
    }

    // ✅ Test for Strength Training (Valid Case, No Efficiency Stat Required)
    @Test
    void testGenerateTrainingPlan_StrengthTraining_Success() throws Exception {
        when(openAiService.getTrainingPlan(anyString())).thenReturn("Strength Training Plan Content");

        mockMvc.perform(get("/api/training/plan")
                .param("teamName", "Team A")
                .param("type", "Strength Training"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.teamName").value("Team A"))
                .andExpect(jsonPath("$.trainingType").value("Strength Training"))
                .andExpect(jsonPath("$.trainingPlan").value("Strength Training Plan Content"));

        verify(openAiService, times(1)).getTrainingPlan(anyString());
    }

    // ✅ Test for Missing Stats (Failure Case)
    @Test
    void testGenerateTrainingPlan_MissingStats_Failure() throws Exception {
        when(queryControllerService.findShootingEfficiencyByTeamName("Team A")).thenReturn(0.0);

        mockMvc.perform(get("/api/training/plan")
                .param("teamName", "Team A")
                .param("type", "Shooting"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("No stats found for this team in Shooting"));

        verify(queryControllerService, times(1)).findShootingEfficiencyByTeamName("Team A");
        verify(openAiService, never()).getTrainingPlan(anyString());
    }

    // ✅ Test for Invalid Training Type (Failure Case)
    @Test
    void testGenerateTrainingPlan_InvalidType_Failure() throws Exception {
        mockMvc.perform(get("/api/training/plan")
                .param("teamName", "Team A")
                .param("type", "InvalidType"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid training type"));

        verify(queryControllerService, never()).findDribblingEfficiencyByTeamName(anyString());
        verify(openAiService, never()).getTrainingPlan(anyString());
    }
}
