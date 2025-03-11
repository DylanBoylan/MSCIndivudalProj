package com.tus.individual.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.tus.individual.model.Action;
import com.tus.individual.model.Match;
import com.tus.individual.model.Player;
import com.tus.individual.model.Team;
import com.tus.individual.service.IQueryControllerService;

@ExtendWith(MockitoExtension.class)
public class QueryControllerTest {

    private MockMvc mockMvc;

    @Mock
    private IQueryControllerService queryControllerService;

    @InjectMocks
    private QueryController queryController;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(queryController).build();
    }

    // ✅ Test for /matches endpoint
    @Test
    void testGetAllMatches_Success() throws Exception {
        List<Match> matches = List.of(new Match(), new Match()); // Dummy matches
        when(queryControllerService.getAllMatches()).thenReturn(matches);

        mockMvc.perform(get("/api/matches"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalMatches").value(2))
                .andExpect(jsonPath("$.matches").isArray());

        verify(queryControllerService, times(1)).getAllMatches();
    }

    // ✅ Test for /actions endpoint
    @Test
    void testGetAllActions_Success() throws Exception {
        List<Action> actions = List.of(new Action(), new Action());
        when(queryControllerService.getAllActions()).thenReturn(actions);

        mockMvc.perform(get("/api/actions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalActions").value(2))
                .andExpect(jsonPath("$.actions").isArray());

        verify(queryControllerService, times(1)).getAllActions();
    }

    // ✅ Test for /teams endpoint
    @Test
    void testGetAllTeams_Success() throws Exception {
        List<Team> teams = List.of(new Team(), new Team());
        when(queryControllerService.getAllTeams()).thenReturn(teams);

        mockMvc.perform(get("/api/teams"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalTeams").value(2))
                .andExpect(jsonPath("$.teams").isArray());

        verify(queryControllerService, times(1)).getAllTeams();
    }

    // ✅ Test for /players endpoint
    @Test
    void testGetAllPlayers_Success() throws Exception {
        List<Player> players = List.of(new Player(), new Player());
        when(queryControllerService.getAllPlayers()).thenReturn(players);

        mockMvc.perform(get("/api/players"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPlayers").value(2))
                .andExpect(jsonPath("$.players").isArray());

        verify(queryControllerService, times(1)).getAllPlayers();
    }

    // ✅ Test for /teams/goals endpoint
    @Test
    void testGetTotalGoalsByTeam_Success() throws Exception {
        List<Object[]> results = new ArrayList<>();
        results.add(new Object[]{1, "Team A", 10});

        when(queryControllerService.getTotalGoalsByTeam()).thenReturn(results);

        mockMvc.perform(get("/api/teams/goals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalTeams").value(1))
                .andExpect(jsonPath("$.teams[0].teamName").value("Team A"));
    }

    // ✅ Test for /teams/points endpoint
    @Test
    void testGetTotalPointsByTeam_Success() throws Exception {
        List<Object[]> results = new ArrayList<>();
        results.add(new Object[]{1, "Team A", 30});

        when(queryControllerService.getTotalPointsByTeam()).thenReturn(results);

        mockMvc.perform(get("/api/teams/points"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalTeams").value(1))
                .andExpect(jsonPath("$.teams[0].totalPoints").value(30));

        verify(queryControllerService, times(1)).getTotalPointsByTeam();
    }

    // ✅ Test for /matches/results endpoint
    @Test
    void testGetMatchResults_Success() throws Exception {
        List<Object[]> results = new ArrayList<>();
        results.add(new Object[]{1, "Team A", "Team B", "Win"});

        when(queryControllerService.getMatchResults()).thenReturn(results);

        mockMvc.perform(get("/api/matches/results"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalMatches").value(1))
                .andExpect(jsonPath("$.matches[0].homeTeam").value("Team A"));
    }

    // ✅ Test for /players/team endpoint
    @Test
    void testGetAverageStatsByTeam_Success() throws Exception {
        List<Object[]> results = new ArrayList<>();
        results.add(new Object[]{10, "Player X", 5.0, 10.0, 80.5, 70.2, 90.0});

        when(queryControllerService.getPlayersByTeamName("Team A")).thenReturn(results);

        mockMvc.perform(get("/api/players/team").param("teamName", "Team A"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPlayers").value(1))
                .andExpect(jsonPath("$.players[0].playerName").value("Player X"));
    }

    // ✅ Test Error Handling - Internal Server Error for /actions
    @Test
    void testGetAllActions_Failure() throws Exception {
        when(queryControllerService.getAllActions()).thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(get("/api/actions"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Failed to fetch actions"));

        verify(queryControllerService, times(1)).getAllActions();
    }

    // ✅ Test Error Handling - Invalid Team Name in /players/team
    @Test
    void testGetAverageStatsByTeam_InvalidTeam() throws Exception {
        when(queryControllerService.getPlayersByTeamName("InvalidTeam")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/players/team").param("teamName", "InvalidTeam"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPlayers").value(0));

        verify(queryControllerService, times(1)).getPlayersByTeamName("InvalidTeam");
    }
}
