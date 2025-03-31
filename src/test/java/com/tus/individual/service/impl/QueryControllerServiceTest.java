package com.tus.individual.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.tus.individual.dao.*;
import com.tus.individual.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

@ExtendWith(MockitoExtension.class)
public class QueryControllerServiceTest {

    @Mock private ActionRepository actionRepository;
    @Mock private MatchRepository matchRepository;
    @Mock private PlayerRepository playerRepository;
    @Mock private TeamRepository teamRepository;

    @InjectMocks private QueryControllerService queryControllerService;

    @BeforeEach
    void setUp() {
        queryControllerService = new QueryControllerService(actionRepository, matchRepository, playerRepository, teamRepository);
    }

    // ✅ Test retrieving all matches
    @Test
    void testGetAllMatches() {
        List<Match> mockMatches = Arrays.asList(new Match(), new Match());
        when(matchRepository.getAllMatches()).thenReturn(mockMatches);

        List<Match> result = queryControllerService.getAllMatches();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(matchRepository, times(1)).getAllMatches();
    }

    // ✅ Test retrieving all actions
    @Test
    void testGetAllActions() {
        List<Action> mockActions = Arrays.asList(new Action(), new Action());
        when(actionRepository.getAllActions()).thenReturn(mockActions);

        List<Action> result = queryControllerService.getAllActions();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(actionRepository, times(1)).getAllActions();
    }

    // ✅ Test retrieving all players
    @Test
    void testGetAllPlayers() {
        List<Player> mockPlayers = Arrays.asList(new Player(), new Player());
        when(playerRepository.getAllPlayers()).thenReturn(mockPlayers);

        List<Player> result = queryControllerService.getAllPlayers();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(playerRepository, times(1)).getAllPlayers();
    }

    // ✅ Test retrieving all teams
    @Test
    void testGetAllTeams() {
        List<Team> mockTeams = Arrays.asList(new Team(), new Team());
        when(teamRepository.getAllTeams()).thenReturn(mockTeams);

        List<Team> result = queryControllerService.getAllTeams();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(teamRepository, times(1)).getAllTeams();
    }

    // ✅ Test getting total goals by team
    @Test
    void testGetTotalGoalsByTeam() {
        List<Object[]> mockResults = new ArrayList<>();
        mockResults.add(new Object[]{1, "Team A", 10});  // ✅ No casting needed

        when(teamRepository.getTotalGoalsByTeam()).thenReturn(mockResults);

        List<Object[]> result = queryControllerService.getTotalGoalsByTeam();

        // 🔍 Debugging output
        System.out.println("Total Goals Results: " + result.size() + " entries");
        for (Object[] entry : result) {
            System.out.println(Arrays.toString(entry));
        }

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1, result.get(0)[0]);  // ✅ Team ID
        assertEquals("Team A", result.get(0)[1]);  // ✅ Team Name
        assertEquals(10, result.get(0)[2]);  // ✅ Total Goals

        verify(teamRepository, times(1)).getTotalGoalsByTeam();
    }


    // ✅ Test getting total points by team
    @Test
    void testGetTotalPointsByTeam() {
        List<Object[]> mockResults = new ArrayList<>();
        mockResults.add(new Object[]{1, "Team A", 30});  // ✅ No casting needed

        when(teamRepository.getTotalPointsByTeam()).thenReturn(mockResults);

        List<Object[]> result = queryControllerService.getTotalPointsByTeam();

        // 🔍 Debugging output
        System.out.println("Total Points Results: " + result.size() + " entries");
        for (Object[] entry : result) {
            System.out.println(Arrays.toString(entry));
        }

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1, result.get(0)[0]);  // ✅ Team ID
        assertEquals("Team A", result.get(0)[1]);  // ✅ Team Name
        assertEquals(30, result.get(0)[2]);  // ✅ Total Points
        
        verify(teamRepository, times(1)).getTotalPointsByTeam();
    }


    // ✅ Test getting team efficiency stats
    @Test
    void testGetTeamEfficiencyStats() {
        Map<String, Double> mockStats = Map.of("passing", 85.5, "shooting", 72.3);
        when(actionRepository.getTeamEfficiencyStats(1)).thenReturn(mockStats);

        Map<String, Double> result = queryControllerService.getTeamEfficiencyStats(1);

        assertNotNull(result);
        assertEquals(85.5, result.get("passing"));
        assertEquals(72.3, result.get("shooting"));
        verify(actionRepository, times(1)).getTeamEfficiencyStats(1);
    }

    // ✅ Test retrieving all team names
    @Test
    void testGetAllTeamNames() {
        List<String> mockNames = List.of("Team A", "Team B");
        when(teamRepository.getAllTeamNames()).thenReturn(mockNames);

        List<String> result = queryControllerService.getAllTeamNames();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Team A", result.get(0));
        verify(teamRepository, times(1)).getAllTeamNames();
    }

    // ✅ Test dribbling efficiency lookup
    @Test
    void testFindDribblingEfficiencyByTeamName() {
        when(teamRepository.findDribblingEfficiencyByTeamName("Team A")).thenReturn(Optional.of(78.2));

        Double result = queryControllerService.findDribblingEfficiencyByTeamName("Team A");

        assertNotNull(result);
        assertEquals(78.2, result);
        verify(teamRepository, times(1)).findDribblingEfficiencyByTeamName("Team A");
    }

    // ✅ Test passing efficiency lookup
    @Test
    void testFindPassingEfficiencyByTeamName() {
        when(teamRepository.findPassingEfficiencyByTeamName("Team A")).thenReturn(Optional.of(82.6));

        Double result = queryControllerService.findPassingEfficiencyByTeamName("Team A");

        assertNotNull(result);
        assertEquals(82.6, result);
        verify(teamRepository, times(1)).findPassingEfficiencyByTeamName("Team A");
    }

    // ✅ Test shooting efficiency lookup
    @Test
    void testFindShootingEfficiencyByTeamName() {
        when(teamRepository.findShootingEfficiencyByTeamName("Team A")).thenReturn(Optional.of(67.3));

        Double result = queryControllerService.findShootingEfficiencyByTeamName("Team A");

        assertNotNull(result);
        assertEquals(67.3, result);
        verify(teamRepository, times(1)).findShootingEfficiencyByTeamName("Team A");
    }

    // ✅ Test getting total games won
    @Test
    void testGetTotalGamesWon() {
        when(teamRepository.getTotalGamesWon("Team A")).thenReturn(12);

        Integer result = queryControllerService.getTotalGamesWon("Team A");

        assertNotNull(result);
        assertEquals(12, result);
        verify(teamRepository, times(1)).getTotalGamesWon("Team A");
    }

    // ✅ Test retrieving match results
    @Test
    void testGetMatchResults() {
        List<Object[]> mockResults = new ArrayList<>();
        mockResults.add(new Object[]{1, "Team A", "Team B", 3, 2});

        when(matchRepository.getMatchResults()).thenReturn(mockResults);

        List<Object[]> result = queryControllerService.getMatchResults();

        // 🔍 Debugging output
        System.out.println("Match Results: " + result.size() + " entries");
        for (Object[] entry : result) {
            System.out.println(Arrays.toString(entry));
        }

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Team A", result.get(0)[1]);
        assertEquals("Team B", result.get(0)[2]);
        assertEquals(3, result.get(0)[3]);
        assertEquals(2, result.get(0)[4]);
        
        verify(matchRepository, times(1)).getMatchResults();
    }

    // ✅ Test retrieving players by team
    @Test
    void testGetPlayersByTeamName() {
        List<Object[]> mockResults = new ArrayList<>();
        mockResults.add(new Object[]{10, "Player X", 5});

        when(playerRepository.getPlayersByTeamName("Team A")).thenReturn(mockResults);

        List<Object[]> result = queryControllerService.getPlayersByTeamName("Team A");

        // 🔍 Debugging output
        System.out.println("Player Results: " + result.size() + " entries");
        for (Object[] entry : result) {
            System.out.println(Arrays.toString(entry));
        }

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(10, result.get(0)[0]);  // ✅ Player ID
        assertEquals("Player X", result.get(0)[1]);  // ✅ Player Name
        assertEquals(5, result.get(0)[2]);  // ✅ Goals or another stat

        verify(playerRepository, times(1)).getPlayersByTeamName("Team A");
    }

}
