package com.example.haunted.rules;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.example.haunted.model.Item;
import com.example.haunted.model.Monster;
import com.example.haunted.model.Quest;
import com.example.haunted.model.QuestItem;
import com.example.haunted.model.QuestStatus;

class QuestTrackerTest {
	private static QuestTracker tracker;
	private static Quest quest;
	private static List<Item> loot;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		tracker = new QuestTracker();
		loot = new ArrayList<>();
		loot.add(new QuestItem("Gradebook", "A standard gradebook"));
		
	}

	@BeforeEach
	void setUp() throws Exception {
		quest = new Quest("Test", "Test");

	}

	private static Stream<Arguments> badItemCases() {
		return Stream.of(Arguments.of((Item) null), Arguments.of(new QuestItem("Gradebook", "A standard gradebook")));
	}

	@ParameterizedTest
	@MethodSource("badItemCases")
	void badItemTests(Item item) {
		tracker.updateQuestForItem(quest, item);
		assertEquals(quest.getStatus(), QuestStatus.NOT_STARTED);
	}

	
	private static Stream<Arguments> badMonsterCases() {
		return Stream.of(Arguments.of((Monster) null), Arguments.of(new Monster("Big Monster", 0, 0, 0, loot)), Arguments.of(new Monster("Final Exam Phantom", 100, 100, 100, loot)));
	}
	
	@ParameterizedTest
	@MethodSource("badMonsterCases")
	void badMonsterTests(Monster monster) {
		tracker.updateQuestForMonster(quest, monster);
		assertEquals(quest.getStatus(), QuestStatus.NOT_STARTED);
	}

	@Test
	void gradeBookButNoPhantom() {
		tracker.updateQuestForItem(quest, new QuestItem("Lost Gradebook", "The Lost Gradebook"));
		assertEquals(quest.getStatus(), QuestStatus.IN_PROGRESS);
	}

	@Test
	void PhantomButNoGradeBook() {
		tracker.updateQuestForMonster(quest, new Monster("Final Exam Phantom", 0, 0, 0, loot));
		assertEquals(quest.getStatus(), QuestStatus.IN_PROGRESS);
	}
}
