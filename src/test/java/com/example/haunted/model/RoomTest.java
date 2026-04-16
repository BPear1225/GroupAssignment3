package com.example.haunted.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class RoomTest {

    private Room room;
    private Item testItem;
    private Monster testMonster;
    private Trap testTrap;

    // Create a concrete subclass of Item since Item is abstract
    private static class TestItem extends Item {
        public TestItem(String name, String description) {
            super(name, description);
        }
    }

    @BeforeEach
    void setUp() {
        // Initialize a fresh Room
        room = new Room("r1", "Dungeon", "A dark and damp dungeon cell.");
        
        // Initialize concrete instances 
        testItem = new TestItem("Golden Key", "A shiny golden key.");
        testMonster = new Monster("Goblin", 50, 10, 5, new ArrayList<>());
        
        // Using your actual TrapType enum
        testTrap = new Trap("Live Wire", TrapType.ELECTRIC, 20, true, true); 
    }

    @Test
    void testConstructorAndGetters() {
        assertEquals("r1", room.getId());
        assertEquals("Dungeon", room.getName());
        assertEquals("A dark and damp dungeon cell.", room.getDescription());
        assertTrue(room.getExits().isEmpty());
        assertTrue(room.getItems().isEmpty());
        assertTrue(room.getMonsters().isEmpty());
    }

    @Test
    void testConnectAndGetExits() {
        Room adjacentRoom = new Room("r2", "Hallway", "A long hallway.");
        
        room.connect(Direction.NORTH, adjacentRoom);
        
        assertEquals(adjacentRoom, room.getExit(Direction.NORTH));
        assertTrue(room.getExits().containsKey(Direction.NORTH));
        assertEquals(1, room.getExits().size());
    }

    @Test
    void testItemManagement() {
        room.addItem(testItem);
        assertEquals(1, room.getItems().size());
        
        // Test finding the item (case-insensitive)
        Optional<Item> foundItem = room.findItem("golden key");
        assertTrue(foundItem.isPresent());
        assertEquals(testItem, foundItem.get());
        
        // Test removing the item
        Item removedItem = room.removeItemByName("Golden Key");
        assertEquals(testItem, removedItem);
        assertTrue(room.getItems().isEmpty());
    }

    @Test
    void testMonsterManagement() {
        room.addMonster(testMonster);
        assertEquals(1, room.getMonsters().size());
        
        // Test finding the monster (case-insensitive)
        Optional<Monster> foundMonster = room.findMonster("goblin");
        assertTrue(foundMonster.isPresent());
        assertEquals(testMonster, foundMonster.get());
        
        // Test living monsters check
        assertTrue(room.hasLivingMonsters());
        
        // Actually deal damage to kill the monster
        testMonster.takeDamage(50); 
        assertFalse(room.hasLivingMonsters());
    }

    
    @Test
    void testLockMechanism() {
        assertFalse(room.isLocked());
        
        room.setLocked(true, "Brass Key");
        assertTrue(room.isLocked());
        assertEquals("Brass Key", room.getRequiredKeyName());
        
        // Attempt unlock with wrong key
        assertFalse(room.unlock("Silver Key"));
        assertTrue(room.isLocked());
        
        // Attempt unlock with correct key (testing case-insensitivity)
        assertTrue(room.unlock("brass key"));
        assertFalse(room.isLocked());
        
        // Attempt unlock while already unlocked and incorrect key name
        assertTrue(room.unlock("random key"));
        
        room.setLocked(true, null);
        
        // Attempt unlock while required key is null
        assertFalse(room.unlock(null));
        
    }

    @Test
    void testTrapManagement() {
        assertNull(room.getTrap());
        
        room.setTrap(testTrap);
        assertEquals(testTrap, room.getTrap());
    }
}