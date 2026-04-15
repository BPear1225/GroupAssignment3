package com.example.haunted.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.stream.Stream;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class PlayerTests {

	private static Player micky;
	private static Player brett;
	private static Player tianyi;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		Inventory mickyInventory = new Inventory(1);
		Inventory tianyiInventory = new Inventory(1);
		Inventory brettInventory = new Inventory(1);
		micky = new Player("Micky", 100, 100, 100, mickyInventory);
		brett = new Player("Brett", -100, -100, -100, brettInventory);
		tianyi = new Player("Tianyi", 0, 0, 0, tianyiInventory);
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
		Inventory mickyInventory = new Inventory(1);
		Inventory tianyiInventory = new Inventory(1);
		Inventory brettInventory = new Inventory(1);
		micky = new Player("Micky", 100, 100, 100, mickyInventory);
		brett = new Player("Brett", -100, -100, -100, brettInventory);
		tianyi = new Player("Tianyi", 0, 0, 0, tianyiInventory);

	}

	@AfterEach
	void tearDown() throws Exception {
	}

	private static Stream<Arguments> creationFailCases() {
		return Stream.of(Arguments.of(null, 1, 1, 1, new Inventory(1)), Arguments.of("", 1, 1, 1, null),
				Arguments.of(null, 1, 1, 1, null));
	}

	@ParameterizedTest
	@MethodSource("creationFailCases")
	public void testImproperCreation(String name, int maxHealth, int baseAttack, int baseDefense, Inventory inventory) {
		assertThrows(NullPointerException.class, () -> new Player(name, maxHealth, baseAttack, baseDefense, inventory));
	}

	private static Stream<Arguments> successfulCreationCases() {
		Inventory mickyInventory = new Inventory(1);
		Inventory tianyiInventory = new Inventory(1);
		Inventory brettInventory = new Inventory(1);
		return Stream.of(Arguments.of(new Player("Micky", 1, 1, 1, mickyInventory), "Micky", 1, 1, 1, mickyInventory),
				Arguments.of(new Player("Tianyi", 0, 0, 0, tianyiInventory), "Tianyi", 0, 0, 0, tianyiInventory),
				Arguments.of(new Player("Brett", -1, -1, -1, brettInventory), "Brett", -1, -1, -1, brettInventory));
	}

	@ParameterizedTest
	@MethodSource("successfulCreationCases")
	void testSuccessfullCreation(Player player, String expectedName, int expectedHealth, int expectedAttk,
			int expectedDef, Inventory expectedInventory) {
		assertEquals(player.getName(), expectedName);
		assertEquals(player.getMaxHealth(), expectedHealth);
		assertEquals(player.getHealth(), expectedHealth);
		assertEquals(player.getBaseAttack(), expectedAttk);
		assertEquals(player.getBaseDefense(), expectedDef);
		assertEquals(player.getInventory(), expectedInventory);
	}

	@Nested
	class ItemTests {
		@Test
		void testEquipWeaponFail() {
			assertThrows(NullPointerException.class, () -> micky.equipWeapon(null));
		}

		private static Stream<Arguments> weaponCases() {
			Weapon Pike = new Weapon("Pike", "It's a type of Spear", 10);
			Weapon Claymore = new Weapon("Claymore", "Medieval longsword", 0);

			micky.equipWeapon(Pike);
			tianyi.equipWeapon(Claymore);
			return Stream.of(Arguments.of(micky, Pike), Arguments.of(tianyi, Claymore), Arguments.of(brett, null));
		}

		@ParameterizedTest
		@MethodSource("weaponCases")
		void testGetEquipedWeapon(Player player, Weapon expectedWeapon) {
			assertEquals(player.getEquippedWeapon(), expectedWeapon);
		}

		@Test
		void testEquipArmorFail() {
			assertThrows(NullPointerException.class, () -> micky.equipArmor(null));
		}

		private static Stream<Arguments> armorCases() {
			Armor Leggings = new Armor("Leggings", "Armor worn on the legs", 10);
			Armor Chestplate = new Armor("Chestplate", "Armor worn on the chest", 0);

			micky.equipArmor(Leggings);
			tianyi.equipArmor(Chestplate);
			return Stream.of(Arguments.of(micky, Leggings), Arguments.of(tianyi, Chestplate),
					Arguments.of(brett, null));
		}

		@ParameterizedTest
		@MethodSource("armorCases")
		void testGetEquipedArmor(Player player, Armor expectedArmor) {
			assertEquals(player.getEquippedArmor(), expectedArmor);
		}
	}

	@Nested
	class HealthTests {

		private static Stream<Arguments> damage() {
			return Stream.of(Arguments.of(micky, 10, 90), Arguments.of(micky, -10, 90), Arguments.of(micky, 0, 90),
					Arguments.of(tianyi, 10, 0), Arguments.of(brett, 10, 0));
		}

		@ParameterizedTest
		@MethodSource("damage")
		void testDamage(Player player, int damage, int expectedFinalHealth) {
			player.takeDamage(damage);
			assertEquals(player.getHealth(), expectedFinalHealth);
		}
		
		private static Stream<Arguments> heal() {
			micky.takeDamage(30);
			return Stream.of(Arguments.of(micky, 10, 80), Arguments.of(micky, -10, 80), Arguments.of(micky, 0, 80),
					Arguments.of(tianyi, 10, 0), Arguments.of(brett, 10, -100),Arguments.of(brett, -10, -100));
		}

		@ParameterizedTest
		@MethodSource("heal")
		void testHeal(Player player, int heal, int expectedFinalHealth) {
			
			player.heal(heal);
			assertEquals(player.getHealth(), expectedFinalHealth);
		}
		
		private static Stream<Arguments> alive() {
			return Stream.of(Arguments.of(micky,true),Arguments.of(tianyi,false),Arguments.of(brett,false));
		}

		@ParameterizedTest
		@MethodSource("alive")
		void testIsAlive(Player player, boolean alive) {
			assertEquals(player.isAlive(), alive);
		}
	}

	@Nested
	class Tests {

	}

}
