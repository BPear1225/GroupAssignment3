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

class QuestTesting {

	@Nested
	class IntantiationTests {
		private static Stream<Arguments> creationFailCases() {
			return Stream.of(Arguments.of(null, null), Arguments.of("", null), Arguments.of(null, ""));
		}

		@ParameterizedTest
		@MethodSource("creationFailCases")
		public void testImproperCreation(String name, String description) {
			assertThrows(NullPointerException.class, () -> new Quest(name, description));
		}

		@Test
		void testSuccessfullCreation() {
			String name = "Test Quest";
			String desc = "Testing Test instantiation";
			Quest quest = new Quest(name, desc);

			assertEquals(quest.getName(), name);
			assertEquals(quest.getDescription(), desc);
			assertEquals(quest.getStatus(), QuestStatus.NOT_STARTED);
		}
	}

	@Nested
	class completionTesting {
		private static Quest gradeBook;
		private static Quest phantomDead;
		private static Quest complete;
		private static Quest none;

		@BeforeAll
		static void setUpBeforeClass() throws Exception {
			gradeBook = new Quest("Grade book", "Got the grade book");
			phantomDead = new Quest("Phantom dead", "The phantom is dead");
			complete = new Quest("Complete", "This Quest is complete");
			none = new Quest("None", "Nothing has been done for this quest");
			gradeBook.markGradebookRecovered();
			phantomDead.markPhantomDefeated();
			complete.markGradebookRecovered();
			complete.markPhantomDefeated();
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

		private static Stream<Arguments> gradeBookCases() {
			return Stream.of(Arguments.of(gradeBook, true), Arguments.of(phantomDead, false),
					Arguments.of(complete, true), Arguments.of(none, false));
		}

		@ParameterizedTest
		@MethodSource("gradeBookCases")
		void testGradeBookMarking(Quest quest, boolean hasGradeBook) {
			assertEquals(quest.isGradebookRecovered(), hasGradeBook);
		}

		private static Stream<Arguments> phantomCases() {
			return Stream.of(Arguments.of(gradeBook, false), Arguments.of(phantomDead, true),
					Arguments.of(complete, true), Arguments.of(none, false));
		}

		@ParameterizedTest
		@MethodSource("phantomCases")
		void testPhantomMarking(Quest quest, boolean phantomIsDead) {
			assertEquals(quest.isPhantomDefeated(), phantomIsDead);
		}

		private static Stream<Arguments> completeCases() {
			return Stream.of(Arguments.of(gradeBook, false), Arguments.of(phantomDead, false),
					Arguments.of(complete, true), Arguments.of(none, false));
		}

		@ParameterizedTest
		@MethodSource("completeCases")
		void testIsComplete(Quest quest, boolean isComplete) {
			assertEquals(quest.isComplete(), isComplete);
		}

		private static Stream<Arguments> completionStatusCases() {
			return Stream.of(Arguments.of(gradeBook, QuestStatus.IN_PROGRESS), Arguments.of(phantomDead, QuestStatus.IN_PROGRESS),
					Arguments.of(complete, QuestStatus.COMPLETED), Arguments.of(none, QuestStatus.NOT_STARTED));
		}

		@ParameterizedTest
		@MethodSource("completionStatusCases")
		void testQuestStatus(Quest quest, QuestStatus status) {
			assertEquals(quest.getStatus(), status);
		}

	}

}
