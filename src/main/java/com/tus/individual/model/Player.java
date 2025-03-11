package com.tus.individual.model;

import jakarta.persistence.*;

@Entity
@Table(name = "players", schema = "individual_project")
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PlayerID")  // ✅ Matches MySQL column name
    private Integer playerId;

    @Column(name = "Name", nullable = false)  // ✅ Matches MySQL column name
    private String name;
        
    // Getters and Setters
    public Integer getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Integer playerId) {
        this.playerId = playerId;
    }

    public String getName() {  // ✅ Renamed to match database
        return name;
    }

    public void setName(String name) {  // ✅ Renamed to match database
        this.name = name;
    }
}
