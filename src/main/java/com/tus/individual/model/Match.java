package com.tus.individual.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "matches", schema = "individual_project")
public class Match {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MatchID")  
    private Integer matchId;

    @Column(name = "TeamHomeID", nullable = false)
    private Integer teamHomeID;

    @Column(name = "TeamAwayID", nullable = false)
    private Integer teamAwayID;

    @Column(name = "TeamHomeFormation", nullable = false)
    private Integer teamHomeFormation;

    @Column(name = "TeamAwayFormation", nullable = false)
    private Integer teamAwayFormation;

    @Column(name = "ResultOfTeamHome", nullable = false)
    private Integer resultOfTeamHome;

    @Column(name = "Date", nullable = false)
    private String date;  // Change from Date to String since MySQL stores it as TEXT
    
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
