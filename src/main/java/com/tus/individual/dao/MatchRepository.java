package com.tus.individual.dao;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.tus.individual.model.Match;

@Repository
public interface MatchRepository extends JpaRepository<Match, Integer> {
    
    @Query("SELECT m FROM Match m")
    List<Match> getAllMatches();
    
    @Query("SELECT m.matchId, t1.name AS homeTeam, t2.name AS awayTeam, " +
    	       "CASE WHEN m.resultOfTeamHome = 1 THEN 'W' " +
    	       "WHEN m.resultOfTeamHome = 0 THEN 'D' " +
    	       "WHEN m.resultOfTeamHome = -1 THEN 'L' END AS homeResult " +
    	       "FROM Match m " +
    	       "JOIN Team t1 ON m.teamHomeID = t1.teamId " +
    	       "JOIN Team t2 ON m.teamAwayID = t2.teamId")
    	List<Object[]> getMatchResults();

}

