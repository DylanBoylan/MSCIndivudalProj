package com.tus.individual.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import java.util.HashMap;
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
public class SeasonAnalyzerControllerTest {

    private MockMvc mockMvc;

    @Mock
    private IQueryControllerService queryControllerService;

    @Mock
    private OpenAIService openAiService;

    @InjectMocks
    private SeasonAnalyzerController seasonAnalyzerController;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(seasonAnalyzerController).build();
    }

    // ✅ Test for General Season Analysis (Valid Case)
    @Test
    void testAnalyzeSeason_General_Success() throws Exception {
        when(queryControllerService.getTotalGamesWon("Team A")).thenReturn(18);
        when(openAiService.getTrainingPlan(anyString())).thenReturn("AI Analysis Report");

        mockMvc.perform(get("/api/season/analyze")
                .param("teamName", "Team A")
                .param("analysisType", "General Season"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.teamName").value("Team A"))
                .andExpect(jsonPath("$.gamesWon").value(18))
                .andExpect(jsonPath("$.aiReport").value("AI Analysis Report"));

        verify(queryControllerService, times(1)).getTotalGamesWon("Team A");
        verify(openAiService, times(1)).getTrainingPlan(anyString());
    }

    // ✅ Test for Player Analysis (Valid Case)
    @Test
    void testAnalyzeSeason_Players_Success() throws Exception {
        when(queryControllerService.getTotalGamesWon("Team B")).thenReturn(25);
        when(openAiService.getTrainingPlan(anyString())).thenReturn("AI Player Analysis Report");

        mockMvc.perform(get("/api/season/analyze")
                .param("teamName", "Team B")
                .param("analysisType", "Players"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.teamName").value("Team B"))
                .andExpect(jsonPath("$.gamesWon").value(25))
                .andExpect(jsonPath("$.aiReport").value("AI Player Analysis Report"));

        verify(queryControllerService, times(1)).getTotalGamesWon("Team B");
        verify(openAiService, times(1)).getTrainingPlan(anyString());
    }

    // ✅ Test for Missing Team Data (Failure Case)
    @Test
    void testAnalyzeSeason_TeamNotFound() throws Exception {
        when(queryControllerService.getTotalGamesWon("Unknown Team")).thenReturn(null);

        mockMvc.perform(get("/api/season/analyze")
                .param("teamName", "Unknown Team")
                .param("analysisType", "General Season"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("No data found for this team."));

        verify(queryControllerService, times(1)).getTotalGamesWon("Unknown Team");
        verify(openAiService, never()).getTrainingPlan(anyString());
    }

    // ✅ Test for Invalid Analysis Type (Failure Case)
    @Test
    void testAnalyzeSeason_InvalidType() throws Exception {
        mockMvc.perform(get("/api/season/analyze")
                .param("teamName", "Team C")
                .param("analysisType", "InvalidType"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid analysis type."));

        verify(queryControllerService, never()).getTotalGamesWon(anyString());
        verify(openAiService, never()).getTrainingPlan(anyString());
    }
}
