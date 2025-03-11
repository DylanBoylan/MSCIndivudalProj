package com.tus.individual.models;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.tus.individual.model.ActionId;

public class ActionIdTest {

    @Test
    void testEqualsAndHashCode() {
        ActionId id1 = new ActionId(10, 5);
        ActionId id2 = new ActionId(10, 5);
        ActionId id3 = new ActionId(11, 5);

        // ✅ Test equality
        assertEquals(id1, id2);  // Same values → should be equal
        assertNotEquals(id1, id3);  // Different playerId → should NOT be equal

        // ✅ Test hash codes are consistent
        assertEquals(id1.hashCode(), id2.hashCode());  // Same object values → same hashCode
        assertNotEquals(id1.hashCode(), id3.hashCode());  // Different object values → different hashCode
    }
}
