package com.example.haunted.engine;

import com.example.haunted.events.CombatResult;
import com.example.haunted.model.Inventory;
import com.example.haunted.model.Item;
import com.example.haunted.model.Monster;
import com.example.haunted.model.Player;
import com.example.haunted.model.Quest;
import com.example.haunted.model.Room;
import com.example.haunted.rules.DamageCalculator;
import com.example.haunted.rules.QuestTracker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CombatEngineTest {

    private CombatEngine combatEngine;
    private Player player;
    private Room room;
    private Quest quest;

    // Concrete subclass of Item for loot testing
    private static class TestItem extends Item {
        public TestItem(String name, String description) {
            super(name, description);
        }
    }

    @BeforeEach
    void setUp() {
        // Initialize the rules and engine
        DamageCalculator damageCalculator = new DamageCalculator();
        QuestTracker questTracker = new QuestTracker();
        combatEngine = new CombatEngine(damageCalculator, questTracker);

        // Setup the Player with an Inventory
        Inventory inventory = new Inventory(10);
        player = new Player("Student", 100, 20, 10, inventory); // 20 Attack, 10 Defense

        // Set up the Room and place the player in it (crucial for loot dropping)
        room = new Room("r1", "Haunted Classroom", "Desks are floating.");
        player.setCurrentRoom(room);

        // Setup the Quest
        quest = new Quest("Final Exam", "Survive the final exam.");
    }

    @Test
    void testAttack_NullMonster() {
        CombatResult result = combatEngine.attack(player, quest, null);

        assertFalse(result.isSuccess());
        assertEquals("Monster not found.", result.getMessage());
    }

    @Test
    void testAttack_PlayerDead() {
        // Kill the player first
        player.takeDamage(100);
        Monster monster = new Monster("Ghost", 50, 10, 5, new ArrayList<>());

        CombatResult result = combatEngine.attack(player, quest, monster);

        assertFalse(result.isSuccess());
        assertEquals("Player is defeated.", result.getMessage());
    }

    @Test
    void testAttack_MonsterAlreadyDead() {
        Monster monster = new Monster("Ghost", 50, 10, 5, new ArrayList<>());
        // Kill the monster first
        monster.takeDamage(50);

        CombatResult result = combatEngine.attack(player, quest, monster);

        assertFalse(result.isSuccess());
        assertEquals("Monster is already defeated.", result.getMessage());
        assertTrue(result.isMonsterDefeated()); // Should reflect that the monster is dead
    }

    @Test
    void testAttack_MonsterSurvives_PlayerTakesDamage() {
        // Monster has 100 HP, 15 Attack, 5 Defense
        Monster monster = new Monster("Tough Ghost", 100, 15, 5, new ArrayList<>());

        CombatResult result = combatEngine.attack(player, quest, monster);

        // Calculations:
        // Player deals: 20 (Player Atk) - 5 (Monster Def) = 15 Damage
        // Monster deals: 15 (Monster Atk) - 10 (Player Def) = 5 Damage
        
        assertTrue(result.isSuccess());
        assertEquals("Attacked Tough Ghost.", result.getMessage());
        assertEquals(15, result.getDamageToMonster());
        assertEquals(5, result.getDamageToPlayer());
        assertFalse(result.isMonsterDefeated());
        
        // Verify state changes
        assertEquals(85, monster.getHealth()); // 100 - 15
        assertEquals(95, player.getHealth());  // 100 - 5
        assertTrue(room.getItems().isEmpty()); // No loot dropped yet
    }

    @Test
    void testAttack_MonsterDefeated_LootDropsAndQuestUpdates() {
        // Setup loot
        List<Item> loot = new ArrayList<>();
        TestItem magicalChalk = new TestItem("Magical Chalk", "Draws perfectly straight lines.");
        loot.add(magicalChalk);

        // Monster is the quest target and has low HP so the player one-shots it
        // 10 HP, 15 Attack, 5 Defense
        Monster phantom = new Monster("Final Exam Phantom", 10, 15, 5, loot);

        assertFalse(quest.isPhantomDefeated()); // Pre-condition check

        CombatResult result = combatEngine.attack(player, quest, phantom);

        // Player deals 15 damage (20 - 5), which is > 10 HP. Monster dies.
        assertTrue(result.isSuccess());
        assertEquals("Defeated Final Exam Phantom.", result.getMessage());
        assertEquals(15, result.getDamageToMonster());
        assertEquals(0, result.getDamageToPlayer()); // Player shouldn't take damage if they killed it
        assertTrue(result.isMonsterDefeated());

        // Verify state changes
        assertFalse(phantom.isAlive());
        assertEquals(100, player.getHealth()); // Player took no damage

        // Verify Loot dropped into the room and the result object
        assertEquals(1, room.getItems().size());
        assertEquals(magicalChalk, room.getItems().get(0));
        assertEquals(1, result.getDroppedItems().size());

        // Verify QuestTracker successfully updated the quest
        assertTrue(quest.isPhantomDefeated());
    }
}