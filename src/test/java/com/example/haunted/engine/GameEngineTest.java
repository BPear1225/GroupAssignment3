package com.example.haunted.engine;

import com.example.haunted.events.CombatResult;
import com.example.haunted.events.InteractionResult;
import com.example.haunted.events.MoveResult;
import com.example.haunted.model.*;
import com.example.haunted.rules.DamageCalculator;
import com.example.haunted.rules.QuestTracker;
import com.example.haunted.rules.TrapResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class GameEngineTest {

    private GameEngine gameEngine;
    private Player player;
    private Quest quest;
    private Room startRoom;
    private Room northRoom;

    @BeforeEach
    void setUp() {
        // 1. Initialize lowest-level models
        Inventory inventory = new Inventory(10);
        player = new Player("Hero", 100, 15, 10, inventory);
        quest = new Quest("Final Exam", "Pass the haunted final exam.");

        startRoom = new Room("r1", "Entrance", "The spooky entrance.");
        northRoom = new Room("r2", "Hallway", "A dark hallway.");
        startRoom.connect(Direction.NORTH, northRoom);
        player.setCurrentRoom(startRoom);

        // 2. Initialize Rules
        TrapResolver trapResolver = new TrapResolver();
        DamageCalculator damageCalculator = new DamageCalculator();
        QuestTracker questTracker = new QuestTracker();

        // 3. Initialize Sub-Engines
        MovementEngine movementEngine = new MovementEngine(trapResolver);
        CombatEngine combatEngine = new CombatEngine(damageCalculator, questTracker);
        InteractionEngine interactionEngine = new InteractionEngine(questTracker);

        // 4. Initialize the main GameEngine
        gameEngine = new GameEngine(player, quest, movementEngine, combatEngine, interactionEngine);
    }

    @Test
    void testGettersAndInitialState() {
        assertEquals(player, gameEngine.getPlayer());
        assertEquals(quest, gameEngine.getQuest());
        assertEquals(startRoom, gameEngine.getCurrentRoom());
        assertFalse(gameEngine.isGameOver());
        assertFalse(gameEngine.isGameWon());
    }

    @Test
    void testMoveDelegation() {
        MoveResult result = gameEngine.move(Direction.NORTH);
        
        assertTrue(result.isSuccess());
        assertEquals(northRoom, gameEngine.getCurrentRoom());
    }

    @Test
    void testPickUpItemDelegation() {
        Key key = new Key("Brass Key", "Opens standard doors.");
        startRoom.addItem(key);

        InteractionResult result = gameEngine.pickUpItem("Brass Key");
        
        assertTrue(result.isSuccess());
        assertTrue(player.getInventory().contains("Brass Key"));
        assertTrue(startRoom.getItems().isEmpty());
    }

    @Test
    void testEquipItemDelegation() {
        Weapon sword = new Weapon("Iron Sword", "A sturdy blade.", 10);
        player.getInventory().addItem(sword);

        InteractionResult result = gameEngine.equipItem("Iron Sword");
        
        assertTrue(result.isSuccess());
        assertEquals(sword, player.getEquippedWeapon());
        assertEquals(25, player.getAttackPower()); // 15 Base + 10 Bonus
    }

    @Test
    void testUseItemDelegation() {
        Potion potion = new Potion("Health Potion", "Heals 30 HP.", 30);
        player.getInventory().addItem(potion);
        
        // Damage player first so they can be healed
        player.takeDamage(50); 
        assertEquals(50, player.getHealth());

        InteractionResult result = gameEngine.useItem("Health Potion");
        
        assertTrue(result.isSuccess());
        assertEquals(80, player.getHealth()); // 50 + 30
        assertFalse(player.getInventory().contains("Health Potion"));
    }

    @Test
    void testUnlockRoomDelegation() {
        northRoom.setLocked(true, "Brass Key");
        
        // Give player the required key
        Key key = new Key("Brass Key", "Unlocks the hallway.");
        player.getInventory().addItem(key);

        InteractionResult result = gameEngine.unlockRoom(Direction.NORTH);
        
        assertTrue(result.isSuccess());
        assertFalse(northRoom.isLocked());
    }

    @Test
    void testAttackDelegation() {
        Monster slime = new Monster("Slime", 30, 5, 2, new ArrayList<>());
        startRoom.addMonster(slime);

        CombatResult result = gameEngine.attack("Slime");
        
        assertTrue(result.isSuccess());
        assertEquals(13, result.getDamageToMonster()); // 15 Player Atk - 2 Monster Def
    }

    @Test
    void testGameEndStates() {
        // Verify we aren't dead or winning at the start
        assertFalse(gameEngine.isGameOver());
        assertFalse(gameEngine.isGameWon());

        // Fulfill quest conditions manually
        quest.markGradebookRecovered();
        quest.markPhantomDefeated();
        
        // Should be a win
        assertTrue(gameEngine.isGameWon());
        assertFalse(gameEngine.isGameOver());

        // Kill the player
        player.takeDamage(1000);
        
        // Dead players can't win, and the game is over
        assertTrue(gameEngine.isGameOver());
        assertFalse(gameEngine.isGameWon());
    }
}