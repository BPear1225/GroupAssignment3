package com.example.haunted.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.stream.Stream;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class ArmorTest {

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	private static Stream<Arguments> creationFailCases() {
		return Stream.of(Arguments.of(null, null, 0), Arguments.of(null, "Description", 0),
				Arguments.of("Name", null, 0));
	}

	@ParameterizedTest
	@MethodSource("creationFailCases")
	public void testImproperCreation(String name, String description, int defense) {
		assertThrows(NullPointerException.class, () -> new Armor(name, description, defense));
	}

	private static Stream<Arguments> successfulCreationCases() {
		return Stream.of(Arguments.of(new Armor("Chestplate", "Armor worn on the chest", 0), 0),
				Arguments.of(new Armor("Leggings", "Armor worn on the legs", 10), 10),
				Arguments.of(new Armor("Anti-Leggings", "Armor worn on the legs", -10), -10));
	}

	@ParameterizedTest
	@MethodSource("successfulCreationCases")
	void testGetDefenseBonus(Armor item, int expectedOutput) {
		assertEquals(item.getDefenseBonus(), expectedOutput);
	}

}
