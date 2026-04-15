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

class PotionTests {

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
		assertThrows(NullPointerException.class, () -> new Potion(name, description, defense));
	}

	private static Stream<Arguments> successfulCreationCases() {
		return Stream.of(Arguments.of(new Potion("Water", "Some refreshing water", 0), 0),
				Arguments.of(new Potion("Health Potion", "Heals 10 Health", 10), 10),
				Arguments.of(new Potion("Damage Potion", "Heals -10 Health", -10), -10));
	}

	@ParameterizedTest
	@MethodSource("successfulCreationCases")
	void testGetHealingAmount(Potion item, int expectedOutput) {
		assertEquals(item.getHealingAmount(), expectedOutput);
	}
	
	private static Stream<Arguments> healParams() {
		Player p1 = new Player("p1", 100, 0, 0, new Inventory(10));
		Player p2 = new Player("p2", 100, 0, 0, new Inventory(10));
		p2.takeDamage(50);
		Potion pot1 = new Potion("pot1","Potion", -100);
		Potion pot2 = new Potion("pot2","Potion", 20);
		Potion pot3 = new Potion("pot3","Potion", 0);
		Potion pot4 = new Potion("pot4","Potion", 1000);
		Potion potions[] = {pot1,pot2,pot3,pot4};
		return Stream.of(Arguments.of(p1,potions,new int[] {100,100,100,100}), Arguments.of(p2,potions,new int[] {50,70,70,100}));
	}
	
	@ParameterizedTest
	@MethodSource("healParams")
	void testPotionUse(Player player, Potion[] potions, int[] expectedHealth ) {
		for (int i = 0; i < potions.length; i++) {
			potions[i].use(player);
			assertEquals(player.getHealth(), expectedHealth[i]);
		}
		
	}

}
