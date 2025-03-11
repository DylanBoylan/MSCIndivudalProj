package com.tus.individual.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.tus.individual.model.Player;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Integer> {  // ✅ Fix: Use `Player` entity and `Integer` for ID

    @Query("SELECT p FROM Player p")  // ✅ Fix: Query `Player` table instead of `Action`
    List<Player> getAllPlayers();
    
    @Query("SELECT p.playerId, p.name, " +
    	       "AVG(a.goals), AVG(a.shotsOnTarget), " +
    	       "AVG(a.passesEfficiency), AVG(a.tackleEfficiency), AVG(a.dribbleEfficiency) " +
    	       "FROM Player p " +
    	       "JOIN Action a ON p.playerId = a.playerId " +
    	       "JOIN Team t ON a.teamId = t.teamId " +
    	       "WHERE t.name = :teamName " +
    	       "GROUP BY p.playerId, p.name")
    	List<Object[]> getPlayersByTeamName(@Param("teamName") String teamName);

}
