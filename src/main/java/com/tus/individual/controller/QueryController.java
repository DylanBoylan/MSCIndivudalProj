package com.tus.individual.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tus.individual.model.Action;
import com.tus.individual.model.Match;
import com.tus.individual.model.Player;
import com.tus.individual.model.Team;
import com.tus.individual.service.IQueryControllerService;

@RestController
@RequestMapping("/api")
public class QueryController {
	private static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private IQueryControllerService queryControllerService;
    
    @Autowired // Injected via constructor
    public QueryController(IQueryControllerService queryControllerService) {
        this.queryControllerService = queryControllerService;
    }
    
    @GetMapping("/matches")
    public ResponseEntity<Map<String, Object>> getAllMatches() {
        List<Match> matches = queryControllerService.getAllMatches();

        Map<String, Object> response = new HashMap<>();
        response.put("totalMatches", matches.size());
        response.put("matches", matches);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/actions")
    public ResponseEntity<Map<String, Object>> getAllActions() {
        try {
            List<Action> actions = queryControllerService.getAllActions();
            Map<String, Object> response = new HashMap<>();
            response.put("totalActions", actions.size());
            response.put("actions", actions);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            e.printStackTrace(); // Log the actual issue
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch actions", "message", e.getMessage()));
        }
    }
    @GetMapping("/teams")
    public ResponseEntity<Map<String, Object>> getAllTeams() {
        List<Team> teams = queryControllerService.getAllTeams();

        Map<String, Object> response = new HashMap<>();
        response.put("totalTeams", teams.size());
        response.put("teams", teams);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/players")
    public ResponseEntity<Map<String, Object>> getAllPlayers() {
        List<Player> players = queryControllerService.getAllPlayers();

        Map<String, Object> response = new HashMap<>();
        response.put("totalPlayers", players.size());
        response.put("players", players);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    
    @GetMapping("/teams/goals")
    public ResponseEntity<Map<String, Object>> getTotalGoalsByTeam() {
        List<Object[]> results = queryControllerService.getTotalGoalsByTeam(); // Fetch query results

        List<Map<String, Object>> teams = new ArrayList<>();
        for (Object[] result : results) {
            Map<String, Object> teamData = new HashMap<>();
            teamData.put("teamId", result[0]);
            teamData.put("teamName", result[1]);
            teamData.put("totalGoals", result[2]);
            teams.add(teamData);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("totalTeams", teams.size());
        response.put("teams", teams);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/teams/points")
    public ResponseEntity<Map<String, Object>> getTotalPointsByTeam() {
        List<Object[]> results = queryControllerService.getTotalPointsByTeam();

        List<Map<String, Object>> teams = new ArrayList<>();
        for (Object[] result : results) {
            Map<String, Object> teamData = new HashMap<>();
            teamData.put("teamId", result[0]);
            teamData.put("teamName", result[1]);
            teamData.put("totalPoints", result[2]);
            teams.add(teamData);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("totalTeams", teams.size());
        response.put("teams", teams);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/teams/names")
    public ResponseEntity<Map<String, Object>> getAllTeamNames() {
        List<String> teamNames = queryControllerService.getAllTeamNames(); // Fetch only names

        List<Map<String, Object>> teams = new ArrayList<>();
        for (String name : teamNames) {
            Map<String, Object> teamData = new HashMap<>();
            teamData.put("teamName", name); // Add name to response
            teams.add(teamData);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("totalTeams", teams.size());
        response.put("teams", teams);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    
    @GetMapping("/matches/results")
    public ResponseEntity<Map<String, Object>> getMatchResults() {
        List<Object[]> results = queryControllerService.getMatchResults();

        List<Map<String, Object>> matches = new ArrayList<>();
        for (Object[] result : results) {
            Map<String, Object> matchData = new HashMap<>();
            matchData.put("matchId", result[0]);
            matchData.put("homeTeam", result[1]);
            matchData.put("awayTeam", result[2]);
            matchData.put("homeResult", result[3]); 
            matches.add(matchData);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("totalMatches", matches.size());
        response.put("matches", matches);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    
    @GetMapping("/players/team")
    public ResponseEntity<Map<String, Object>> getAverageStatsByTeam(@RequestParam String teamName) {
        List<Object[]> results = queryControllerService.getPlayersByTeamName(teamName);

        List<Map<String, Object>> players = new ArrayList<>();
        for (Object[] result : results) {
            Map<String, Object> playerData = new HashMap<>();
            playerData.put("playerId", result[0]);
            playerData.put("playerName", result[1]);
            playerData.put("goals", roundAndAbs((double) result[2]));
            playerData.put("shotsOnTarget", roundAndAbs((double) result[3]));
            playerData.put("passesEfficiency", roundAndAbs((double) result[4]));
            playerData.put("tackleEfficiency", roundAndAbs((double) result[5]));
            playerData.put("dribbleEfficiency", roundAndAbs((double) result[6]));
            players.add(playerData);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("totalPlayers", players.size());
        response.put("players", players);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // ✅ Helper function to round to 2 decimal places and remove negatives
    private double roundAndAbs(double value) {
        return Math.round(Math.abs(value) * 100.0) / 100.0;
    }

}
