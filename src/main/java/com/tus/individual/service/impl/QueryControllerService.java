package com.tus.individual.service.impl;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tus.individual.dao.ActionRepository;
import com.tus.individual.dao.MatchRepository;
import com.tus.individual.dao.PlayerRepository;
import com.tus.individual.dao.TeamRepository;
import com.tus.individual.model.Action;
import com.tus.individual.model.Match;
import com.tus.individual.model.Player;
import com.tus.individual.model.Team;
import com.tus.individual.service.IQueryControllerService;

@Service
public class QueryControllerService implements IQueryControllerService {

	private ActionRepository actionRepository;
	private MatchRepository matchRepository;
	private PlayerRepository playerRepository;
	private TeamRepository teamRepository;

	@Autowired
	public QueryControllerService(ActionRepository actionRepository, MatchRepository matchRepository,
			PlayerRepository playerRepository, TeamRepository teamRepository) {
		this.actionRepository = actionRepository;
		this.matchRepository = matchRepository;
		this.playerRepository = playerRepository;
		this.teamRepository = teamRepository;
	}

	@Override
	public List<Match> getAllMatches() {
		return matchRepository.getAllMatches();
	}

	@Override
	public List<Action> getAllActions() {
		return actionRepository.getAllActions();
	}

	@Override
	public List<Player> getAllPlayers() {
		return playerRepository.getAllPlayers();
	}

	@Override
	public List<Team> getAllTeams() {
		return teamRepository.getAllTeams();
	}

	@Override
	public List<Object[]> getTotalGoalsByTeam() {
		return teamRepository.getTotalGoalsByTeam();
	}

	@Override
	public List<Object[]> getTotalPointsByTeam() {
		return teamRepository.getTotalPointsByTeam();
	}

	@Override
	public Map<String, Double> getTeamEfficiencyStats(Integer teamId) {
		return actionRepository.getTeamEfficiencyStats(teamId);
	}

	@Override
	public List<String> getAllTeamNames() {
		return teamRepository.getAllTeamNames();
	}

	@Override
	public Double findDribblingEfficiencyByTeamName(String teamName) {
		return teamRepository.findDribblingEfficiencyByTeamName(teamName).orElse(0.0);
	}

	@Override
	public Double findPassingEfficiencyByTeamName(String teamName) {
		return teamRepository.findPassingEfficiencyByTeamName(teamName).orElse(0.0);
	}

	@Override
	public Double findShootingEfficiencyByTeamName(String teamName) {
		return teamRepository.findShootingEfficiencyByTeamName(teamName).orElse(0.0);
	}

	@Override
	public Integer getTotalGamesWon(String teamName) {
	    return teamRepository.getTotalGamesWon(teamName);
	}
	
	@Override
	public List<Object[]> getMatchResults() {
	    return matchRepository.getMatchResults();
	}
	
	@Override
	public List<Object[]> getPlayersByTeamName(String teamName) {
	    return playerRepository.getPlayersByTeamName(teamName);
	}

}
