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

class KeyTest {

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
		return Stream.of(Arguments.of(null, null), Arguments.of(null, "Description"), Arguments.of("Name", null));
	}

	@ParameterizedTest
	@MethodSource("creationFailCases")
	public void testImproperCreation(String name, String description) {
		assertThrows(NullPointerException.class, () -> new Key(name, description));
	}

	private static Stream<Arguments> nameCases() {
		return Stream.of(Arguments.of(new Key("Key1", "Example Item 1"), "Key1"),
				Arguments.of(new Key("Hallway", "Random Test"), "Hallway"));
	}

	@ParameterizedTest
	@MethodSource("nameCases")
	public void testGetName(Key item, String expectedOutput) {
		assertEquals(item.getName(), expectedOutput);
	}

	private static Stream<Arguments> descriptionCases() {
		return Stream.of(Arguments.of(new Key("Key1", "Example Item 1"), "Example Item 1"),
				Arguments.of(new Key("Hallway", "Random Test"), "Random Test"));
	}

	@ParameterizedTest
	@MethodSource("descriptionCases")
	void testGetDescription(Key item, String expectedOutput) {
		assertEquals(item.getDescription(), expectedOutput);
	}

	private static Stream<Arguments> toStringCases() {
		return Stream.of(Arguments.of(new Key("Key1", "Example Item 1"), "Key1"),
				Arguments.of(new Key("Hallway", "Random Test"), "Hallway"));
	}

	@ParameterizedTest
	@MethodSource("toStringCases")
	void testToString(Key item, String expectedOutput) {
		assertEquals(item.toString(), expectedOutput);
	}

}
