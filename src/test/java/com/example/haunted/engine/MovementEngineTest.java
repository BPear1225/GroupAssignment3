package com.example.haunted.engine;

import com.example.haunted.events.MoveResult;
import com.example.haunted.model.*;
import com.example.haunted.rules.TrapResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MovementEngineTest {

    private MovementEngine movementEngine;
    private Player player;
    private Room startRoom;
    private Room northRoom;
    private Room eastRoom;
    private Room westRoom;

    @BeforeEach
    void setUp() {
        // Initialize TrapResolver and MovementEngine
        TrapResolver trapResolver = new TrapResolver();
        movementEngine = new MovementEngine(trapResolver);

        // Initialize Player
        Inventory inventory = new Inventory(10);
        player = new Player("Hero", 100, 10, 5, inventory);

        // Initialize Rooms
        startRoom = new Room("r1", "Start Room", "The beginning.");
        northRoom = new Room("r2", "North Room", "A safe room.");
        eastRoom = new Room("r3", "East Room", "A locked room.");
        westRoom = new Room("r4", "West Room", "A dangerous room.");

        // Connect Rooms
        startRoom.connect(Direction.NORTH, northRoom);
        startRoom.connect(Direction.EAST, eastRoom);
        startRoom.connect(Direction.WEST, westRoom);

        // Set Player's initial room
        player.setCurrentRoom(startRoom);
    }

    @Test
    void testMove_SuccessNoTrap() {
        MoveResult result = movementEngine.move(player, Direction.NORTH);

        assertTrue(result.isSuccess());
        assertEquals("Moved to North Room.", result.getMessage());
        assertFalse(result.isTrapTriggered());
        assertEquals(0, result.getTrapDamage());
        
        // Verify player actually moved
        assertEquals(northRoom, player.getCurrentRoom());
    }

    @Test
    void testMove_NoRoomInDirection() {
        // There is no room connected to the SOUTH of startRoom
        MoveResult result = movementEngine.move(player, Direction.SOUTH);

        assertFalse(result.isSuccess());
        assertEquals("There is no room in that direction.", result.getMessage());
        assertFalse(result.isTrapTriggered());
        
        // Verify player did not move
        assertEquals(startRoom, player.getCurrentRoom());
    }

    @Test
    void testMove_RoomIsLocked() {
        // Lock the east room
        eastRoom.setLocked(true, "Brass Key");

        MoveResult result = movementEngine.move(player, Direction.EAST);

        assertFalse(result.isSuccess());
        assertEquals("The room is locked.", result.getMessage());
        assertFalse(result.isTrapTriggered());
        
        // Verify player did not move
        assertEquals(startRoom, player.getCurrentRoom());
    }

    @Test
    void testMove_SuccessWithArmedTrap() {
        // Add an armed trap to the west room
        Trap spikes = new Trap("Spike Pit", TrapType.ELECTRIC, 15, true, true);
        westRoom.setTrap(spikes);

        assertEquals(100, player.getHealth()); // Initial health

        MoveResult result = movementEngine.move(player, Direction.WEST);

        assertTrue(result.isSuccess());
        assertEquals("Moved to West Room. Trap 'Spike Pit' triggered for 15 damage.", result.getMessage());
        assertTrue(result.isTrapTriggered());
        assertEquals(15, result.getTrapDamage());
        
        // Verify player moved and took damage
        assertEquals(westRoom, player.getCurrentRoom());
        assertEquals(85, player.getHealth()); // 100 - 15
    }

    @Test
    void testMove_SuccessWithDisarmedTrap() {
        // Add a disarmed trap to the west room
        Trap spikes = new Trap("Spike Pit", TrapType.ELECTRIC, 15, false, true);
        westRoom.setTrap(spikes);

        MoveResult result = movementEngine.move(player, Direction.WEST);

        assertTrue(result.isSuccess());
        // Should use standard move message since trap is not armed
        assertEquals("Moved to West Room.", result.getMessage()); 
        assertFalse(result.isTrapTriggered());
        assertEquals(0, result.getTrapDamage());
        
        // Verify player moved and took NO damage
        assertEquals(westRoom, player.getCurrentRoom());
        assertEquals(100, player.getHealth());
    }
}