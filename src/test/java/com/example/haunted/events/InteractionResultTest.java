package com.example.haunted.events;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InteractionResultTest {

    @Test
    void testSuccessfulInteraction() {
        InteractionResult result = new InteractionResult(true, "You successfully picked the lock.");

        assertTrue(result.isSuccess());
        assertEquals("You successfully picked the lock.", result.getMessage());
    }

    @Test
    void testFailedInteraction() {
        InteractionResult result = new InteractionResult(false, "The door won't budge.");

        assertFalse(result.isSuccess());
        assertEquals("The door won't budge.", result.getMessage());
    }

    @Test
    void testInteractionWithNullMessage() {
        // While generally best to avoid null messages, it's good to ensure 
        // the class doesn't crash if one is passed in.
        InteractionResult result = new InteractionResult(false, null);

        assertFalse(result.isSuccess());
        assertNull(result.getMessage());
    }
}