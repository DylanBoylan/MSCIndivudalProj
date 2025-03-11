package com.tus.individual.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.repository.query.Param;

import com.tus.individual.model.Action;
import com.tus.individual.model.Match;
import com.tus.individual.model.Player;
import com.tus.individual.model.Team;

public interface IQueryControllerService {
	
	public List<Match> getAllMatches();
	public List<Action> getAllActions();
	public List<Player> getAllPlayers();
	public List<Team> getAllTeams();
	public List<Object[]> getTotalGoalsByTeam();
	public List<Object[]> getTotalPointsByTeam();
	public Map<String, Double> getTeamEfficiencyStats(Integer teamId);
	public List<String> getAllTeamNames();
    public Double findDribblingEfficiencyByTeamName(String teamName);
    public Double findPassingEfficiencyByTeamName(String teamName);
    public Double findShootingEfficiencyByTeamName(String teamName);
    public Integer getTotalGamesWon(String teamName);
    public List<Object[]> getMatchResults();
    public List<Object[]> getPlayersByTeamName(String teamName);

}
