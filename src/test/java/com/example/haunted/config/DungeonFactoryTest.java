package com.example.haunted.config;

import com.example.haunted.engine.GameEngine;

import com.example.haunted.model.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Optional;
import java.util.stream.Stream;

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

	@Nested
	class testCreateGame_MapLayoutAndConnections {
		private static GameEngine engine;
		private static Room stairwell;
		private static Room lectureHall;
		private static Room labStorage;
		private static Room brokenElevator;
		private static Room serverCloset;
		private static Room examArchive;
		private static Room deanVault;
		private static Room finalChamber;

		@BeforeAll
		static void setUpBeforeClass() throws Exception {
			engine = DungeonFactory.createGame();
			stairwell = engine.getCurrentRoom();
			lectureHall = stairwell.getExit(Direction.EAST);
			labStorage = lectureHall.getExit(Direction.EAST);
			brokenElevator = lectureHall.getExit(Direction.SOUTH);
			serverCloset = labStorage.getExit(Direction.NORTH);
			examArchive = lectureHall.getExit(Direction.NORTH);
			deanVault = examArchive.getExit(Direction.EAST);
			finalChamber = deanVault.getExit(Direction.NORTH);
		}

		@Test
		void stairwellIsStartingRoom() {
			assertEquals("Maintenance Stairwell", stairwell.getName());
		}

		private static Stream<Arguments> stairwellExpectations() {
			return Stream.of(Arguments.of(Direction.NORTH, null), Arguments.of(Direction.SOUTH, null),
					Arguments.of(Direction.EAST, "Abandoned Lecture Hall"), Arguments.of(Direction.WEST, null));
		}

		@ParameterizedTest
		@MethodSource("stairwellExpectations")
		void stairwellConnections(Direction direction, String expectedName) {
			if (expectedName == null) {
				assertEquals(stairwell.getExit(direction), null);
			} else {
				assertEquals(stairwell.getExit(direction).getName(), expectedName);
			}

		}

		private static Stream<Arguments> lectureHallExpectations() {

			return Stream.of(Arguments.of(Direction.NORTH, "Exam Archive"),
					Arguments.of(Direction.SOUTH, "Broken Elevator"), Arguments.of(Direction.EAST, "Lab Storage"),
					Arguments.of(Direction.WEST, "Maintenance Stairwell"));
		}

		@ParameterizedTest
		@MethodSource("lectureHallExpectations")
		void lectureHallConnections(Direction direction, String expectedName) {

			assertEquals(lectureHall.getExit(direction).getName(), expectedName);

		}

		private static Stream<Arguments> labStorageExpectations() {

			return Stream.of(Arguments.of(Direction.NORTH, "Server Closet"), Arguments.of(Direction.SOUTH, ""),
					Arguments.of(Direction.EAST, ""), Arguments.of(Direction.WEST, "Abandoned Lecture Hall"));
		}

		@ParameterizedTest
		@MethodSource("labStorageExpectations")
		void labStorageConnections(Direction direction, String expectedName) {
			if (expectedName.equals("")) {
				assertEquals(labStorage.getExit(direction), null);
			} else {
				assertEquals(labStorage.getExit(direction).getName(), expectedName);
			}

		}

		private static Stream<Arguments> brokenElevatorExpectations() {

			return Stream.of(Arguments.of(Direction.NORTH, "Abandoned Lecture Hall"), Arguments.of(Direction.SOUTH, ""),
					Arguments.of(Direction.EAST, ""), Arguments.of(Direction.WEST, ""));
		}

		@ParameterizedTest
		@MethodSource("brokenElevatorExpectations")
		void brokenElevatorConnections(Direction direction, String expectedName) {
			if (expectedName.equals("")) {
				assertEquals(brokenElevator.getExit(direction), null);
			} else {
				assertEquals(brokenElevator.getExit(direction).getName(), expectedName);
			}

		}

		private static Stream<Arguments> serverClosetExpectations() {

			return Stream.of(Arguments.of(Direction.NORTH, ""), Arguments.of(Direction.SOUTH, "Lab Storage"),
					Arguments.of(Direction.EAST, "Dean Vault"), Arguments.of(Direction.WEST, ""));
		}

		@ParameterizedTest
		@MethodSource("serverClosetExpectations")
		void serverClosetConnections(Direction direction, String expectedName) {
			if (expectedName.equals("")) {
				assertEquals(serverCloset.getExit(direction), null);
			} else {
				assertEquals(serverCloset.getExit(direction).getName(), expectedName);
			}

		}

		private static Stream<Arguments> examArchiveExpectations() {

			return Stream.of(Arguments.of(Direction.NORTH, ""), Arguments.of(Direction.SOUTH, "Abandoned Lecture Hall"),
					Arguments.of(Direction.EAST, "Dean Vault"), Arguments.of(Direction.WEST, ""));
		}

		@ParameterizedTest
		@MethodSource("examArchiveExpectations")
		void examArchiveConnections(Direction direction, String expectedName) {
			if (expectedName.equals("")) {
				assertEquals(examArchive.getExit(direction), null);
			} else {
				assertEquals(examArchive.getExit(direction).getName(), expectedName);
			}

		}

		private static Stream<Arguments> deanVaultExpectations() {

			return Stream.of(Arguments.of(Direction.NORTH, "Final Chamber"),
					Arguments.of(Direction.SOUTH, "Server Closet"), Arguments.of(Direction.EAST, ""),
					Arguments.of(Direction.WEST, "Exam Archive"));
		}

		@ParameterizedTest
		@MethodSource("deanVaultExpectations")
		void deanVaultConnections(Direction direction, String expectedName) {
			if (expectedName.equals("")) {
				assertEquals(deanVault.getExit(direction), null);
			} else {
				assertEquals(deanVault.getExit(direction).getName(), expectedName);
			}

		}

		private static Stream<Arguments> finalChamberExpectations() {

			return Stream.of(Arguments.of(Direction.NORTH, ""), Arguments.of(Direction.SOUTH, "Dean Vault"),
					Arguments.of(Direction.EAST, ""), Arguments.of(Direction.WEST, ""));
		}

		@ParameterizedTest
		@MethodSource("finalChamberExpectations")
		void finalChamberConnections(Direction direction, String expectedName) {
			if (expectedName.equals("")) {
				assertEquals(finalChamber.getExit(direction), null);
			} else {
				assertEquals(finalChamber.getExit(direction).getName(), expectedName);
			}

		}

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
		deanVault = examArchive.getExit(Direction.EAST);
		Room finalChamber = deanVault.getExit(Direction.NORTH);

		assertTrue(finalChamber.isLocked());
		assertEquals("Vault Key", finalChamber.getRequiredKeyName());
	}

	@Nested
	class testCreateGame_ItemAndMonsterPlacements {

		private static GameEngine engine;
		private static Room stairwell;
		private static Room lectureHall;
		private static Room labStorage;
		private static Room serverCloset;
		private static Room examArchive;
		private static Room deanVault;
		private static Room finalChamber;

		@BeforeAll
		static void setUpBeforeClass() throws Exception {
			engine = DungeonFactory.createGame();
			stairwell = engine.getCurrentRoom();
			lectureHall = stairwell.getExit(Direction.EAST);
			labStorage = lectureHall.getExit(Direction.EAST);
			serverCloset = labStorage.getExit(Direction.NORTH);
			examArchive = lectureHall.getExit(Direction.NORTH);
			deanVault = examArchive.getExit(Direction.EAST);
			finalChamber = deanVault.getExit(Direction.NORTH);
		}

		private static Stream<Arguments> allItems() {
			return Stream.of(
					Arguments.of(lectureHall, "Coffee Potion"), 
					Arguments.of(labStorage, "Archive Key"),
					Arguments.of(labStorage, "Calculator Shield"), 
					Arguments.of(serverCloset, "Stapler of Justice"), 
					Arguments.of(examArchive, "Lost Gradebook"), 
					Arguments.of(deanVault, "Vault Key"));
		}

		@ParameterizedTest
		@MethodSource("allItems")
		void ItemChecks(Room room, String itemName) {
			assertTrue(room.findItem(itemName).isPresent());
		}

		private static Stream<Arguments> basicMonsters() {
			return Stream.of(
					Arguments.of(lectureHall, "Sleep-Deprived TA", 18, 6, 1,1), 
					Arguments.of(serverCloset, "Spreadsheet Golem", 28, 7, 4,1),
					Arguments.of(examArchive, "Plagiarism Ghost", 22, 8, 2,0), 
					Arguments.of(deanVault, "Registrar Wraith", 30, 9, 3,1));
		}

		@ParameterizedTest
		@MethodSource("basicMonsters")
		void basicMonsterChecks(Room room, String monsterName,int hp, int att, int def, int lootAmount) {
			// Check a specific monster and its attributes
			Optional<Monster> monsterOptional = room.findMonster(monsterName);

			assertTrue(monsterOptional.isPresent());
			Monster monster = monsterOptional.get();
			assertEquals(hp, monster.getHealth());
			assertEquals(att, monster.getAttack());
			assertEquals(def, monster.getDefense());
			assertEquals(lootAmount, monster.getLoot().size());
		}
		
		@Test
		void checkBossMonster() {
			// Check Boss placement
			Optional<Monster> bossOptional = finalChamber.findMonster("Final Exam Phantom");
			
			assertTrue(bossOptional.isPresent());
			assertTrue(bossOptional.get() instanceof BossMonster);
			Monster monster = bossOptional.get();
			assertEquals(40, monster.getHealth());
			assertEquals(10, monster.getAttack());
			assertEquals(4, monster.getDefense());
			assertEquals(0, monster.getLoot().size());
			

			
		}
		
	}
}