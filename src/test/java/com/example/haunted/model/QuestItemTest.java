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

class QuestItemTest {

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
		assertThrows(NullPointerException.class, () -> new QuestItem(name, description));
	}
	
	private static Stream<Arguments> descriptionCases() {
		return Stream.of(Arguments.of(new QuestItem("Key1", "Example Item 1"), "Example Item 1"),
				Arguments.of(new QuestItem("Hallway", "Random Test"), "Random Test"));
	}

	@ParameterizedTest
	@MethodSource("descriptionCases")
	void testSuccessfullCreation(QuestItem item, String expectedOutput) {
		assertEquals(item.getDescription(), expectedOutput);
	}

}
