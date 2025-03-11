package com.tus.individual.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.tus.individual.model.Team;

@Repository
public interface TeamRepository extends JpaRepository<Team, Integer> { // ✅ Correct entity and primary key type

	@Query("SELECT t FROM Team t") // ✅ Retrieves all teams
	List<Team> getAllTeams();

	@Query("SELECT t.teamId, t.name, COALESCE(SUM(a.goals), 0) AS totalGoals " + "FROM Action a "
			+ "RIGHT JOIN Team t ON a.teamId = t.teamId " + // Use RIGHT JOIN to ensure all teams are included
			"GROUP BY t.teamId, t.name " + "ORDER BY totalGoals DESC")
	List<Object[]> getTotalGoalsByTeam();

	@Query("SELECT t.teamId, t.name, " + "SUM(CASE " + "WHEN m.resultOfTeamHome = -1 THEN 0 "
			+ "WHEN m.resultOfTeamHome = 0 THEN 1 " + "WHEN m.resultOfTeamHome = 1 THEN 3 " + "END) AS totalPoints "
			+ "FROM Match m " + "JOIN Team t ON m.teamHomeID = t.teamId OR m.teamAwayID = t.teamId "
			+ "GROUP BY t.teamId, t.name " + "ORDER BY totalPoints DESC")
	List<Object[]> getTotalPointsByTeam();

	@Query("SELECT t.name FROM Team t ORDER BY t.name ASC")
	List<String> getAllTeamNames();

	@Query("SELECT COALESCE(AVG(a.dribbleEfficiency), 0.0) " + "FROM Action a JOIN Team t ON a.teamId = t.teamId "
			+ "WHERE t.name = :teamName")
	Optional<Double> findDribblingEfficiencyByTeamName(@Param("teamName") String teamName);

	@Query("SELECT COALESCE(AVG(a.passesEfficiency), 0.0) " + "FROM Action a JOIN Team t ON a.teamId = t.teamId "
			+ "WHERE t.name = :teamName")
	Optional<Double> findPassingEfficiencyByTeamName(@Param("teamName") String teamName);

	@Query("SELECT COALESCE(AVG(a.shotEfficiency), 0.0) " + "FROM Action a JOIN Team t ON a.teamId = t.teamId "
			+ "WHERE t.name = :teamName")
	Optional<Double> findShootingEfficiencyByTeamName(@Param("teamName") String teamName);

	@Query("SELECT COUNT(m) " +
		       "FROM Match m " +
		       "WHERE (m.teamHomeID = (SELECT t.teamId FROM Team t WHERE t.name = :teamName) AND m.resultOfTeamHome = 1) " +
		       "   OR (m.teamAwayID = (SELECT t.teamId FROM Team t WHERE t.name = :teamName) AND m.resultOfTeamHome = -1)")
		Integer getTotalGamesWon(@Param("teamName") String teamName);

}
