package com.tus.individual.model;

import java.io.Serializable;
import java.util.Objects;

public class ActionId implements Serializable {
    private Integer playerId;
    private Integer matchId;

    // ✅ Default Constructor
    public ActionId() {}

    // ✅ Parameterized Constructor
    public ActionId(Integer playerId, Integer matchId) {
        this.playerId = playerId;
        this.matchId = matchId;
    }

    // ✅ Getters & Setters
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

    // ✅ Override `equals` & `hashCode` (Required for composite keys)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ActionId actionId = (ActionId) o;
        return Objects.equals(playerId, actionId.playerId) &&
               Objects.equals(matchId, actionId.matchId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(playerId, matchId);
    }
}
