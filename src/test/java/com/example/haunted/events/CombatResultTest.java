package com.example.haunted.events;

import com.example.haunted.model.Item;
import com.example.haunted.model.Key;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CombatResultTest {

	// Create a concrete subclass of Item since Item is abstract
	private static class TestItem extends Item {
		public TestItem(String name, String description) {
			super(name, description);
		}
	}

	@Test
	void testConstructorAndGetters() {
		// Setup initial loot list
		List<Item> loot = new ArrayList<>();
		Item sword = new TestItem("Rusty Sword", "An old, chipped sword.");
		loot.add(sword);

		// Initialize the CombatResult
		CombatResult result = new CombatResult(true, "You struck a mighty blow against the Goblin!", 15, 5, true, loot);

		// Verify standard getters
		assertTrue(result.isSuccess());
		assertEquals("You struck a mighty blow against the Goblin!", result.getMessage());
		assertEquals(15, result.getDamageToMonster());
		assertEquals(5, result.getDamageToPlayer());
		assertTrue(result.isMonsterDefeated());

		// Verify items list
		List<Item> dropped = result.getDroppedItems();
		assertEquals(1, dropped.size());
		assertEquals("Rusty Sword", dropped.get(0).getName());
	}

	@Test
	void testConstructorMakesDefensiveCopy() {
		List<Item> originalList = new ArrayList<>();
		Item potion = new TestItem("Health Potion", "Heals 10 HP.");
		originalList.add(potion);

		CombatResult result = new CombatResult(true, "Hit", 10, 0, false, originalList);

		// Modify the original list AFTER passing it to the constructor
		originalList.add(new TestItem("Gold Coin", "A shiny coin."));

		// The CombatResult's internal list should remain unchanged
		assertEquals(1, result.getDroppedItems().size());
		assertEquals("Health Potion", result.getDroppedItems().get(0).getName());
	}

	@Test
	void testDroppedItemsListIsUnmodifiable() {
		List<Item> loot = new ArrayList<>();
		loot.add(new TestItem("Monster Bone", "A creepy bone."));

		CombatResult result = new CombatResult(true, "Miss", 0, 10, false, loot);

		List<Item> returnedList = result.getDroppedItems();

		// Attempting to modify the returned list should throw an
		// UnsupportedOperationException
		assertThrows(UnsupportedOperationException.class,
				() -> returnedList.add(new Key("Hacked Item", "This shouldn't be here.")));

		assertThrows(UnsupportedOperationException.class, returnedList::clear);
	}
}