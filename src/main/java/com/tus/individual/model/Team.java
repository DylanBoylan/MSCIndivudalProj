package com.tus.individual.model;

import jakarta.persistence.*;

@Entity
@Table(name = "Teams", schema = "PremierLeague")
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer teamId;
    
    @Column(nullable = false, unique = true)
    private String teamName;
    
    // Getters and Setters
    public Integer getTeamId() {
        return teamId;
    }

    public void setTeamId(Integer teamId) {
        this.teamId = teamId;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }
}
