package com.tus.individual.model;

import jakarta.persistence.*;

@Entity
@Table(name = "Actions", schema = "PremierLeague")
public class Action {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(nullable = false)
    private Integer playerId;
    
    @Column(nullable = false)
    private Integer matchId;
    
    @Column(nullable = false)
    private Integer teamId;
    
    private Integer firstGoal;
    private Integer winningGoal;
    private Integer shotsOnTarget;
    private Integer savesMade;
    private Integer timePlayed;
    private Integer positionId;
    private Integer starts;
    private Integer substituteOn;
    private Integer substituteOff;
    private Integer goals;
    
    private String team1;
    private String team2;
    
    private Long shotEfficiency;
    private Long passesEfficiency;
    private Long tackleEfficiency;
    private Long dribbleEfficiency;
    
    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Integer playerId) {
        this.playerId = playerId;
    }

    public Integer getMatchId() {
        return matchId;
    }

    public void setMatchId(int matchId) {
        this.matchId = matchId;
    }

    public Integer getTeamId() {
        return teamId;
    }

    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }
    
    public Integer getFirstGoal() {
        return firstGoal;
    }

    public void setFirstGoal(Integer firstGoal) {
        this.firstGoal = firstGoal;
    }

    public Integer getWinningGoal() {
        return winningGoal;
    }

    public void setWinningGoal(Integer winningGoal) {
        this.winningGoal = winningGoal;
    }

    public Integer getShotsOnTarget() {
        return shotsOnTarget;
    }

    public void setShotsOnTarget(Integer shotsOnTarget) {
        this.shotsOnTarget = shotsOnTarget;
    }

    public Integer getSavesMade() {
        return savesMade;
    }

    public void setSavesMade(Integer savesMade) {
        this.savesMade = savesMade;
    }

    public Integer getTimePlayed() {
        return timePlayed;
    }

    public void setTimePlayed(Integer timePlayed) {
        this.timePlayed = timePlayed;
    }

    public Integer getPositionId() {
        return positionId;
    }

    public void setPositionId(Integer positionId) {
        this.positionId = positionId;
    }

    public Integer getStarts() {
        return starts;
    }

    public void setStarts(Integer starts) {
        this.starts = starts;
    }

    public Integer getSubstituteOn() {
        return substituteOn;
    }

    public void setSubstituteOn(Integer substituteOn) {
        this.substituteOn = substituteOn;
    }

    public Integer getSubstituteOff() {
        return substituteOff;
    }

    public void setSubstituteOff(Integer substituteOff) {
        this.substituteOff = substituteOff;
    }

    public Integer getGoals() {
        return goals;
    }

    public void setGoals(Integer goals) {
        this.goals = goals;
    }

    public String getTeam1() {
        return team1;
    }

    public void setTeam1(String team1) {
        this.team1 = team1;
    }

    public String getTeam2() {
        return team2;
    }

    public void setTeam2(String team2) {
        this.team2 = team2;
    }

    public Long getShotEfficiency() {
        return shotEfficiency;
    }

    public void setShotEfficiency(Long long1) {
        this.shotEfficiency = long1;
    }

    public Long getPassesEfficiency() {
        return passesEfficiency;
    }

    public void setPassesEfficiency(Long passesEfficiency) {
        this.passesEfficiency = passesEfficiency;
    }

    public Long getTackleEfficiency() {
        return tackleEfficiency;
    }

    public void setTackleEfficiency(Long tackleEfficiency) {
        this.tackleEfficiency = tackleEfficiency;
    }

    public Long getDribbleEfficiency() {
        return dribbleEfficiency;
    }

    public void setDribbleEfficiency(Long dribbleEfficiency) {
        this.dribbleEfficiency = dribbleEfficiency;
    }
}
