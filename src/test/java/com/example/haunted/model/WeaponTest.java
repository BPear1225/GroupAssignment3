package com.example.haunted.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class WeaponTest {


	private static Stream<Arguments> creationFailCases() {
		return Stream.of(Arguments.of(null, null, 0), Arguments.of(null, "Description", 0),
				Arguments.of("Name", null, 0));
	}

	@ParameterizedTest
	@MethodSource("creationFailCases")
	public void testImproperCreation(String name, String description, int defense) {
		assertThrows(NullPointerException.class, () -> new Weapon(name, description, defense));
	}

	private static Stream<Arguments> successfulCreationCases() {
		return Stream.of(Arguments.of(new Weapon("Claymore", "Medieval longsword", 0), 0),
				Arguments.of(new Weapon("Pike", "It's a type of Spear", 10), 10),
				Arguments.of(new Weapon("Training Gloves", "You don't want to hurt your trainer", -10), -10));
	}

	@ParameterizedTest
	@MethodSource("successfulCreationCases")
	void testGetDefenseBonus(Weapon item, int expectedOutput) {
		assertEquals(item.getAttackBonus(), expectedOutput);
	}

}
