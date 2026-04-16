package com.example.haunted.events;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MoveResultTest {

    @Test
    void testSuccessfulMoveNoTrap() {
        MoveResult result = new MoveResult(true, "You moved North into the Hallway.", false, 0);

        assertTrue(result.isSuccess());
        assertEquals("You moved North into the Hallway.", result.getMessage());
        assertFalse(result.isTrapTriggered());
        assertEquals(0, result.getTrapDamage());
    }

    @Test
    void testSuccessfulMoveWithTrap() {
        MoveResult result = new MoveResult(true, "You stepped into the room, but triggered a trap!", true, 15);

        assertTrue(result.isSuccess());
        assertEquals("You stepped into the room, but triggered a trap!", result.getMessage());
        assertTrue(result.isTrapTriggered());
        assertEquals(15, result.getTrapDamage());
    }

    @Test
    void testFailedMove() {
        MoveResult result = new MoveResult(false, "The door is locked. You need a Brass Key.", false, 0);

        assertFalse(result.isSuccess());
        assertEquals("The door is locked. You need a Brass Key.", result.getMessage());
        assertFalse(result.isTrapTriggered());
        assertEquals(0, result.getTrapDamage());
    }
}