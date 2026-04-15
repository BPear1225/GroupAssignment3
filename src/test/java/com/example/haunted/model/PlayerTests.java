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

	private static void ResetPlayers() {
		Inventory mickyInventory = new Inventory(1);
		Inventory tianyiInventory = new Inventory(1);
		Inventory brettInventory = new Inventory(1);
		micky = new Player("Micky", 100, 100, -100, mickyInventory);
		brett = new Player("Brett", -100, -100, 100, brettInventory);
		tianyi = new Player("Tianyi", 0, 0, 0, tianyiInventory);
	}

	@Nested
	class IntantiationTests {
		private static Stream<Arguments> creationFailCases() {
			return Stream.of(Arguments.of(null, 1, 1, 1, new Inventory(1)), Arguments.of("", 1, 1, 1, null),
					Arguments.of(null, 1, 1, 1, null));
		}

		@ParameterizedTest
		@MethodSource("creationFailCases")
		public void testImproperCreation(String name, int maxHealth, int baseAttack, int baseDefense,
				Inventory inventory) {
			assertThrows(NullPointerException.class,
					() -> new Player(name, maxHealth, baseAttack, baseDefense, inventory));
		}

		private static Stream<Arguments> successfulCreationCases() {
			Inventory mickyInventory = new Inventory(1);
			Inventory tianyiInventory = new Inventory(1);
			Inventory brettInventory = new Inventory(1);
			return Stream.of(
					Arguments.of(new Player("Micky", 1, 1, 1, mickyInventory), "Micky", 1, 1, 1, mickyInventory),
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
	}

	@Nested
	class PropertyTests {
		@BeforeAll
		static void setUpBeforeClass() throws Exception {
			ResetPlayers();
		}

		@AfterAll
		static void tearDownAfterClass() throws Exception {
		}

		@BeforeEach
		void setUp() throws Exception {
			ResetPlayers();

		}

		@AfterEach
		void tearDown() throws Exception {
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
						Arguments.of(tianyi, 10, 0), Arguments.of(brett, 10, -100), Arguments.of(brett, -10, -100));
			}

			@ParameterizedTest
			@MethodSource("heal")
			void testHeal(Player player, int heal, int expectedFinalHealth) {

				player.heal(heal);
				assertEquals(player.getHealth(), expectedFinalHealth);
			}

			private static Stream<Arguments> alive() {
				return Stream.of(Arguments.of(micky, true), Arguments.of(tianyi, false), Arguments.of(brett, false));
			}

			@ParameterizedTest
			@MethodSource("alive")
			void testIsAlive(Player player, boolean alive) {
				assertEquals(player.isAlive(), alive);
			}
		}

		@Nested
		class PowerCalculationTests {

			private static Stream<Arguments> attack() {
				Weapon weapons[] = new Weapon[3];

				weapons[0] = new Weapon("Pike", "It's a type of Spear", 10);
				weapons[1] = new Weapon("Claymore", "Medieval longsword", 0);
				weapons[2] = new Weapon("Training Gloves", "You don't want to hurt your trainer", -10);
				return Stream.of(Arguments.of(micky, weapons, new int[] { 100, 110, 100, 90 }),
						Arguments.of(tianyi, weapons, new int[] { 0, 10, 00, -10 }),
						Arguments.of(brett, weapons, new int[] { -100, -90, -100, -110 }));
			}

			@ParameterizedTest
			@MethodSource("attack")
			void testAttackPower(Player player, Weapon[] weapons, int[] expectedAttacks) {
				assertEquals(player.getAttackPower(), expectedAttacks[0]);
				for (int i = 0; i < weapons.length; i++) {
					player.equipWeapon(weapons[i]);
					assertEquals(player.getAttackPower(), expectedAttacks[i + 1]);
				}

			}

			private static Stream<Arguments> defence() {
				Armor armors[] = new Armor[3];

				armors[0] = new Armor("Leggings", "Armor worn on the legs", 10);
				armors[1] = new Armor("Chestplate", "Armor worn on the chest", 0);
				armors[2] = new Armor("Anti-Leggings", "Armor worn on the legs", -10);
				return Stream.of(Arguments.of(micky, armors, new int[] { -100, -90, -100, -110 }),
						Arguments.of(tianyi, armors, new int[] { 0, 10, 00, -10 }),
						Arguments.of(brett, armors, new int[] { 100, 110, 100, 90 }));
			}

			@ParameterizedTest
			@MethodSource("defence")
			void testDefencePower(Player player, Armor[] armor, int[] expectedAttacks) {
				assertEquals(player.getDefensePower(), expectedAttacks[0]);
				for (int i = 0; i < armor.length; i++) {
					player.equipArmor(armor[i]);
					assertEquals(player.getDefensePower(), expectedAttacks[i + 1]);
				}

			}

		}

		@Nested
		class RoomTests {
			@Test
			void InvalidRoomTest() {
				assertThrows(NullPointerException.class, () -> micky.setCurrentRoom(null));
			}
			
			@Test
			void ValidRoomTest() {
				Room room = new Room("1", "Room", "It's a room");
				micky.setCurrentRoom(room);
				assertEquals(micky.getCurrentRoom(), room);
			}
			
			
		}
	}
}
