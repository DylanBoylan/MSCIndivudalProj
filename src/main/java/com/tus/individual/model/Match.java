package com.tus.individual.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "Match", schema = "PremierLeague")
public class Match {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer matchId;
    
    @Column(nullable = false)
    private Integer teamHomeID;
    
    @Column(nullable = false)
    private Integer teamAwayID;
    
    @Column(nullable = false)
    private Integer teamHomeFormation;
    
    @Column(nullable = false)
    private Integer teamAwayFormation;
    
    @Column(nullable = false)
    private Integer resultOfTeamHome;
    
    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;
    
    // Getters and Setters
    public Integer getMatchId() {
        return matchId;
    }

    public void setMatchId(Integer matchId) {
        this.matchId = matchId;
    }

    public Integer getTeamHomeID() {
        return teamHomeID;
    }

    public void setTeamHomeID(Integer teamHomeID) {
        this.teamHomeID = teamHomeID;
    }

    public Integer getTeamAwayID() {
        return teamAwayID;
    }

    public void setTeamAwayID(Integer teamAwayID) {
        this.teamAwayID = teamAwayID;
    }

    public Integer getTeamHomeFormation() {
        return teamHomeFormation;
    }

    public void setTeamHomeFormation(Integer teamHomeFormation) {
        this.teamHomeFormation = teamHomeFormation;
    }

    public Integer getTeamAwayFormation() {
        return teamAwayFormation;
    }

    public void setTeamAwayFormation(Integer teamAwayFormation) {
        this.teamAwayFormation = teamAwayFormation;
    }

    public Integer getResultOfTeamHome() {
        return resultOfTeamHome;
    }

    public void setResultOfTeamHome(Integer resultOfTeamHome) {
        this.resultOfTeamHome = resultOfTeamHome;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
