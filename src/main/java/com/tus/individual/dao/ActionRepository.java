package com.tus.individual.dao;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.tus.individual.model.Action;
import com.tus.individual.model.ActionId;
import com.tus.individual.model.Match;

@Repository
public interface ActionRepository extends JpaRepository<Action, ActionId> {

	 @Query("SELECT a FROM Action a")
	    List<Action> getAllActions();
	 
	 @Query("SELECT AVG(a.shotEfficiency), AVG(a.passesEfficiency), AVG(a.tackleEfficiency), AVG(a.dribbleEfficiency) "
		        + "FROM Action a WHERE a.teamId = :teamId")
		Map<String, Double> getTeamEfficiencyStats(@Param("teamId") Integer teamId);


}
