package com.example.haunted.engine;

import com.example.haunted.events.InteractionResult;
import com.example.haunted.model.*;
import com.example.haunted.rules.QuestTracker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InteractionEngineTest {

    private InteractionEngine interactionEngine;
    private Player player;
    private Room currentRoom;
    private Quest quest;

    @BeforeEach
    void setUp() {
        QuestTracker questTracker = new QuestTracker();
        interactionEngine = new InteractionEngine(questTracker);

        // Set up player with a small inventory capacity for testing full inventory
        Inventory inventory = new Inventory(2);
        player = new Player("Hero", 100, 10, 5, inventory);

        currentRoom = new Room("r1", "Study", "A dusty study.");
        player.setCurrentRoom(currentRoom);

        quest = new Quest("Final Exam", "Find the lost gradebook.");
    }

    // --- pickUpItem Tests ---

    @Test
    void testPickUpItem_Success() {
        Key key = new Key("Brass Key", "A shiny key.");
        currentRoom.addItem(key);

        InteractionResult result = interactionEngine.pickUpItem(player, quest, "Brass Key");

        assertTrue(result.isSuccess());
        assertEquals("Picked up Brass Key.", result.getMessage());
        assertTrue(player.getInventory().contains("Brass Key"));
        assertTrue(currentRoom.getItems().isEmpty());
    }

    @Test
    void testPickUpItem_NotFound() {
        InteractionResult result = interactionEngine.pickUpItem(player, quest, "Ghost Sword");

        assertFalse(result.isSuccess());
        assertEquals("Item not found in the room.", result.getMessage());
    }

    @Test
    void testPickUpItem_InventoryFull() {
        // Fill the inventory (capacity is 2)
        player.getInventory().addItem(new Key("Key 1", "desc"));
        player.getInventory().addItem(new Key("Key 2", "desc"));

        // Add a third item to the room
        Weapon sword = new Weapon("Heavy Sword", "Too heavy to carry now.", 10);
        currentRoom.addItem(sword);

        InteractionResult result = interactionEngine.pickUpItem(player, quest, "Heavy Sword");

        assertFalse(result.isSuccess());
        assertEquals("Inventory is full.", result.getMessage());
        assertFalse(player.getInventory().contains("Heavy Sword"));
        assertTrue(currentRoom.findItem("Heavy Sword").isPresent()); // Item should go back to the room
    }

    @Test
    void testPickUpItem_QuestItemUpdatesQuest() {
        // "Lost Gradebook" is the hardcoded quest item in QuestTracker
        Key gradebook = new Key("Lost Gradebook", "The teacher's gradebook.");
        currentRoom.addItem(gradebook);

        assertFalse(quest.isGradebookRecovered());

        InteractionResult result = interactionEngine.pickUpItem(player, quest, "Lost Gradebook");

        assertTrue(result.isSuccess());
        assertTrue(quest.isGradebookRecovered()); // QuestTracker should have intercepted this
    }

    // --- useItem Tests ---

    @Test
    void testUseItem_Success() {
        Potion potion = new Potion("Health Potion", "Heals 30 HP.", 30);
        player.getInventory().addItem(potion);
        
        // Damage player so we can see the heal
        player.takeDamage(50); 
        assertEquals(50, player.getHealth());

        InteractionResult result = interactionEngine.useItem(player, "Health Potion");

        assertTrue(result.isSuccess());
        assertEquals("Used Health Potion.", result.getMessage());
        assertEquals(80, player.getHealth());
        assertFalse(player.getInventory().contains("Health Potion")); // Potion consumed
    }

    @Test
    void testUseItem_NotFound() {
        InteractionResult result = interactionEngine.useItem(player, "Health Potion");

        assertFalse(result.isSuccess());
        assertEquals("Item not found in inventory.", result.getMessage());
    }

    @Test
    void testUseItem_NotUsable() {
        Weapon sword = new Weapon("Sword", "Pointy.", 10);
        player.getInventory().addItem(sword);

        InteractionResult result = interactionEngine.useItem(player, "Sword");

        assertFalse(result.isSuccess());
        assertEquals("That item cannot be used.", result.getMessage());
        assertTrue(player.getInventory().contains("Sword")); // Still in inventory
    }

    // --- equipItem Tests ---

    @Test
    void testEquipItem_WeaponSuccess() {
        Weapon sword = new Weapon("Iron Sword", "Sharp.", 15);
        player.getInventory().addItem(sword);

        InteractionResult result = interactionEngine.equipItem(player, "Iron Sword");

        assertTrue(result.isSuccess());
        assertEquals("Equipped weapon Iron Sword.", result.getMessage());
        assertEquals(sword, player.getEquippedWeapon());
        assertEquals(25, player.getAttackPower()); // 10 Base + 15 Bonus
    }

    @Test
    void testEquipItem_ArmorSuccess() {
        Armor shield = new Armor("Wooden Shield", "Blocky.", 10);
        player.getInventory().addItem(shield);

        InteractionResult result = interactionEngine.equipItem(player, "Wooden Shield");

        assertTrue(result.isSuccess());
        assertEquals("Equipped armor Wooden Shield.", result.getMessage());
        assertEquals(shield, player.getEquippedArmor());
        assertEquals(15, player.getDefensePower()); // 5 Base + 10 Bonus
    }

    @Test
    void testEquipItem_NotEquippable() {
        Potion potion = new Potion("Health Potion", "Drink me.", 10);
        player.getInventory().addItem(potion);

        InteractionResult result = interactionEngine.equipItem(player, "Health Potion");

        assertFalse(result.isSuccess());
        assertEquals("That item cannot be equipped.", result.getMessage());
    }

    // --- unlockRoom Tests ---

    @Test
    void testUnlockRoom_Success() {
        Room northRoom = new Room("r2", "Hallway", "A dark hallway.");
        northRoom.setLocked(true, "Brass Key");
        currentRoom.connect(Direction.NORTH, northRoom);

        player.getInventory().addItem(new Key("Brass Key", "Unlocks doors."));

        InteractionResult result = interactionEngine.unlockRoom(player, Direction.NORTH);

        assertTrue(result.isSuccess());
        assertEquals("Unlocked Hallway.", result.getMessage());
        assertFalse(northRoom.isLocked());
    }

    @Test
    void testUnlockRoom_NoRoom() {
        InteractionResult result = interactionEngine.unlockRoom(player, Direction.SOUTH);

        assertFalse(result.isSuccess());
        assertEquals("There is no room in that direction.", result.getMessage());
    }

    @Test
    void testUnlockRoom_AlreadyUnlocked() {
        Room northRoom = new Room("r2", "Hallway", "A dark hallway.");
        currentRoom.connect(Direction.NORTH, northRoom);

        InteractionResult result = interactionEngine.unlockRoom(player, Direction.NORTH);

        assertTrue(result.isSuccess());
        assertEquals("The room is already unlocked.", result.getMessage());
    }

    @Test
    void testUnlockRoom_MissingKey() {
        Room northRoom = new Room("r2", "Hallway", "A dark hallway.");
        northRoom.setLocked(true, "Brass Key");
        currentRoom.connect(Direction.NORTH, northRoom);

        InteractionResult result = interactionEngine.unlockRoom(player, Direction.NORTH);

        assertFalse(result.isSuccess());
        assertEquals("You do not have the correct key.", result.getMessage());
        assertTrue(northRoom.isLocked());
    }

    @Test
    void testUnlockRoom_WrongItemType() {
        Room northRoom = new Room("r2", "Hallway", "A dark hallway.");
        northRoom.setLocked(true, "Brass Key");
        currentRoom.connect(Direction.NORTH, northRoom);

        // Add a weapon named "Brass Key" to see if the engine correctly enforces the 'Key' instance type
        Weapon fakeKey = new Weapon("Brass Key", "It's actually a sword.", 5);
        player.getInventory().addItem(fakeKey);

        InteractionResult result = interactionEngine.unlockRoom(player, Direction.NORTH);

        assertFalse(result.isSuccess());
        assertEquals("You do not have the correct key.", result.getMessage());
        assertTrue(northRoom.isLocked());
    }
}