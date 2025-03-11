package com.tus.individual.model;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "actions", schema = "individual_project")
@IdClass(ActionId.class) // ✅ Use Composite Key Class
public class Action implements Serializable {

    @Id
    @Column(name = "PlayerID") // ✅ Match database column name
    private Integer playerId;

    @Id
    @Column(name = "MatchID") // ✅ Match database column name
    private Integer matchId;

    @Column(name = "TeamID")
    private Integer teamId;

    @Column(name = "FirstGoal")
    private Integer firstGoal;

    @Column(name = "WinningGoal")
    private Integer winningGoal;

    @Column(name = "ShotsOnTargetincgoals")
    private Integer shotsOnTarget;

    @Column(name = "SavesMade")
    private Integer savesMade;

    @Column(name = "TimePlayed")
    private Integer timePlayed;

    @Column(name = "PositionID")
    private Integer positionId;

    @Column(name = "Starts")
    private Integer starts;

    @Column(name = "SubstituteOn")
    private Integer substituteOn;

    @Column(name = "SubstituteOff")
    private Integer substituteOff;

    @Column(name = "Goals")
    private Integer goals;

    @Column(name = "Team1")
    private String team1;

    @Column(name = "Team2")
    private String team2;

    @Column(name = "shot_eff")
    private Double shotEfficiency;

    @Column(name = "passes_eff")
    private Double passesEfficiency;

    @Column(name = "tackle_eff")
    private Double tackleEfficiency;

    @Column(name = "dribble_eff")
    private Double dribbleEfficiency;

    // ✅ Getters and Setters
    public Integer getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Integer playerId) {
        this.playerId = playerId;
    }

    public Integer getMatchId() {
        return matchId;
    }

    public void setMatchId(Integer matchId) {
        this.matchId = matchId;
    }

    public Integer getTeamId() {
        return teamId;
    }

    public void setTeamId(Integer teamId) {
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

    public Double getShotEfficiency() {
        return shotEfficiency;
    }

    public void setShotEfficiency(Double shotEfficiency) {
        this.shotEfficiency = shotEfficiency;
    }

    public Double getPassesEfficiency() {
        return passesEfficiency;
    }

    public void setPassesEfficiency(Double passesEfficiency) {
        this.passesEfficiency = passesEfficiency;
    }

    public Double getTackleEfficiency() {
        return tackleEfficiency;
    }

    public void setTackleEfficiency(Double tackleEfficiency) {
        this.tackleEfficiency = tackleEfficiency;
    }

    public Double getDribbleEfficiency() {
        return dribbleEfficiency;
    }

    public void setDribbleEfficiency(Double dribbleEfficiency) {
        this.dribbleEfficiency = dribbleEfficiency;
    }
}
