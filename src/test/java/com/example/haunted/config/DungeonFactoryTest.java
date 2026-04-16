package com.example.haunted.config;

import com.example.haunted.engine.GameEngine;
import com.example.haunted.model.*;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class DungeonFactoryTest {

    @Test
    void testCreateGame_EngineAndBaseComponentsNotNull() {
        GameEngine engine = DungeonFactory.createGame();

        assertNotNull(engine);
        assertNotNull(engine.getPlayer());
        assertNotNull(engine.getQuest());
        assertNotNull(engine.getCurrentRoom());
    }

    @Test
    void testCreateGame_PlayerStatsAndInventory() {
        GameEngine engine = DungeonFactory.createGame();
        Player player = engine.getPlayer();

        assertEquals("Student Explorer", player.getName());
        assertEquals(50, player.getMaxHealth());
        assertEquals(50, player.getHealth());
        assertEquals(7, player.getBaseAttack());
        assertEquals(2, player.getBaseDefense());
        assertNotNull(player.getInventory());
        assertEquals(8, player.getInventory().getCapacity());
        assertTrue(player.getInventory().getItems().isEmpty());
    }

    @Test
    void testCreateGame_QuestSetup() {
        GameEngine engine = DungeonFactory.createGame();
        Quest quest = engine.getQuest();

        assertEquals("Escape the Basement", quest.getName());
        assertEquals("Recover the Lost Gradebook and defeat the Final Exam Phantom.", quest.getDescription());
        assertFalse(quest.isComplete());
    }

    @Test
    void testCreateGame_MapLayoutAndConnections() {
        GameEngine engine = DungeonFactory.createGame();
        
        // Starting Room: Stairwell
        Room stairwell = engine.getCurrentRoom();
        assertEquals("Maintenance Stairwell", stairwell.getName());

        // Move East -> Lecture Hall
        Room lectureHall = stairwell.getExit(Direction.EAST);
        assertNotNull(lectureHall);
        assertEquals("Abandoned Lecture Hall", lectureHall.getName());
        assertEquals(stairwell, lectureHall.getExit(Direction.WEST)); // Backwards connection check

        // Lecture Hall connections
        Room labStorage = lectureHall.getExit(Direction.EAST);
        assertEquals("Lab Storage", labStorage.getName());

        Room brokenElevator = lectureHall.getExit(Direction.SOUTH);
        assertEquals("Broken Elevator", brokenElevator.getName());

        Room examArchive = lectureHall.getExit(Direction.NORTH);
        assertEquals("Exam Archive", examArchive.getName());

        // Deeper connections check
        Room serverCloset = labStorage.getExit(Direction.NORTH);
        assertEquals("Server Closet", serverCloset.getName());

        Room deanVault = serverCloset.getExit(Direction.EAST);
        assertEquals("Dean Vault", deanVault.getName());

        Room finalChamber = deanVault.getExit(Direction.NORTH);
        assertEquals("Final Chamber", finalChamber.getName());
    }

    @Test
    void testCreateGame_LocksAndTraps() {
        GameEngine engine = DungeonFactory.createGame();
        Room stairwell = engine.getCurrentRoom();
        Room lectureHall = stairwell.getExit(Direction.EAST);

        // Trap Check
        Room brokenElevator = lectureHall.getExit(Direction.SOUTH);
        Trap trap = brokenElevator.getTrap();
        assertNotNull(trap);
        assertEquals("Loose Wires Trap", trap.getName());
        assertEquals(TrapType.ELECTRIC, trap.getType());
        assertEquals(8, trap.getDamage());
        assertTrue(trap.isArmed());
        assertTrue(trap.isOneTimeTrigger());

        // Lock Check 1
        Room examArchive = lectureHall.getExit(Direction.NORTH);
        assertTrue(examArchive.isLocked());
        assertEquals("Archive Key", examArchive.getRequiredKeyName());

        // Lock Check 2
        Room labStorage = lectureHall.getExit(Direction.EAST);
        Room serverCloset = labStorage.getExit(Direction.NORTH);
        Room deanVault = serverCloset.getExit(Direction.EAST);
        Room finalChamber = deanVault.getExit(Direction.NORTH);
        
        assertTrue(finalChamber.isLocked());
        assertEquals("Vault Key", finalChamber.getRequiredKeyName());
    }

    @Test
    void testCreateGame_ItemAndMonsterPlacements() {
        GameEngine engine = DungeonFactory.createGame();
        Room stairwell = engine.getCurrentRoom();
        Room lectureHall = stairwell.getExit(Direction.EAST);
        Room labStorage = lectureHall.getExit(Direction.EAST);
        Room serverCloset = labStorage.getExit(Direction.NORTH);
        Room deanVault = serverCloset.getExit(Direction.EAST);
        Room finalChamber = deanVault.getExit(Direction.NORTH);

        // Check specific items
        assertTrue(labStorage.findItem("Archive Key").isPresent());
        assertTrue(labStorage.findItem("Calculator Shield").isPresent());
        assertTrue(serverCloset.findItem("Stapler of Justice").isPresent());
        assertTrue(deanVault.findItem("Vault Key").isPresent());

        // Check a specific monster and its attributes
        Optional<Monster> taOptional = lectureHall.findMonster("Sleep-Deprived TA");
        assertTrue(taOptional.isPresent());
        Monster ta = taOptional.get();
        assertEquals(18, ta.getHealth());
        assertEquals(6, ta.getAttack());
        assertEquals(1, ta.getLoot().size());
        assertEquals("Coffee Potion", ta.getLoot().get(0).getName());

        // Check Boss placement
        Optional<Monster> bossOptional = finalChamber.findMonster("Final Exam Phantom");
        assertTrue(bossOptional.isPresent());
        assertTrue(bossOptional.get() instanceof BossMonster);
        assertEquals(40, bossOptional.get().getHealth());
    }
}