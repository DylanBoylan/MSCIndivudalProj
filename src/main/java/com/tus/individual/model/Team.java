package com.tus.individual.model;

import jakarta.persistence.*;

@Entity
@Table(name = "teams", schema = "individual_project")
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TeamID")  // ✅ Matches MySQL column name
    private Integer teamId;

    @Column(name = "Name", nullable = false, unique = true)  // ✅ Matches MySQL column name
    private String name;

    // Getters and Setters
    public Integer getTeamId() {
        return teamId;
    }

    public void setTeamId(Integer teamId) {
        this.teamId = teamId;
    }

    public String getName() {  // ✅ Renamed to match database
        return name;
    }

    public void setName(String name) {  // ✅ Renamed to match database
        this.name = name;
    }
}
