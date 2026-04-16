package com.example.haunted.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class InvintoryTesting {

	private static Key i1;
	private static Key i2;
	private static Key i3;
	private static Key i4;
	private static Key i5;
	private static Inventory negCap;
	private static Inventory cap0;
	private static Inventory cap1;
	private static Inventory cap5;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		i1 = new Key("Item 1", "Test Item");
		i2 = new Key("Item 2", "Test Item");
		i3 = new Key("Item 3", "Test Item");
		i4 = new Key("Item 4", "Test Item");
		i5 = new Key("Item 5", "Test Item");
		negCap = new Inventory(-5);
		cap0 = new Inventory(0);
		cap1 = new Inventory(1);
		cap5 = new Inventory(5);
	}

	@BeforeEach
	void setUp() throws Exception {
		negCap = new Inventory(-5);
		cap0 = new Inventory(0);
		cap1 = new Inventory(1);
		cap5 = new Inventory(5);
	}

	private static Stream<Arguments> capacityCases() {
		return Stream.of(Arguments.of(negCap, true, -5), Arguments.of(cap0, true, 0), Arguments.of(cap1, false, 1),
				Arguments.of(cap5, false, 5));
	}

	@ParameterizedTest
	@MethodSource("capacityCases")
	void baseCapacityTest(Inventory inventory, boolean shouldBeFull, int expectedCapacity) {
		assertEquals(inventory.isFull(), shouldBeFull,
				String.format("isFull should be %b, was %b", shouldBeFull, !shouldBeFull));
		assertEquals(inventory.getCapacity(), expectedCapacity, "Wrong capacity");
	}

	private static Stream<Arguments> fillStates() {
		cap5.addItem(i1);
		return Stream.of(Arguments.of(cap0), Arguments.of(cap1), Arguments.of(cap5));
	}

	@ParameterizedTest
	@MethodSource("fillStates")
	void InvalidFindItems(Inventory inventory) {
		assertEquals(inventory.findItem("Not Here").orElse(null), null);
	}

	@Test
	void FindNull() {
		assertEquals(cap1.findItem(null).orElse(null), null);
	}

	@Nested
	class ItemAdditonTests {

		@BeforeAll
		static void setUpBeforeClass() throws Exception {
			i1 = new Key("Item 1", "Test Item");
			i2 = new Key("Item 2", "Test Item");
			i3 = new Key("Item 3", "Test Item");
			i4 = new Key("Item 4", "Test Item");
			i5 = new Key("Item 5", "Test Item");
			negCap = new Inventory(-5);
			cap0 = new Inventory(0);
			cap1 = new Inventory(1);
			cap5 = new Inventory(5);
		}

		@BeforeEach
		void setUp() throws Exception {
			negCap = new Inventory(-5);
			cap0 = new Inventory(0);
			cap1 = new Inventory(1);
			cap5 = new Inventory(5);
		}

		private static Stream<Arguments> emptyListAddition() {
			cap1.addItem(i1);
			cap5.addItem(i1);
			cap5.addItem(i2);
			return Stream.of(Arguments.of(cap1, i1, i1.getName()), Arguments.of(cap5, i1, i1.getName()),
					Arguments.of(cap5, i2, i2.getName()));
		}

		@ParameterizedTest
		@MethodSource("emptyListAddition")
		void addsItemToEmptyList(Inventory inventory, Item itemToFind, String itemName) {
			assertEquals(inventory.findItem(itemName).get(), itemToFind);
		}

		private static Stream<Arguments> fullListAddition() {
			cap0.addItem(i1);
			cap0.addItem(i2);
			cap0.addItem(i3);
			return Stream.of(Arguments.of(cap0, i1.getName()), Arguments.of(cap0, i2.getName()),
					Arguments.of(cap0, i3.getName()));
		}

		@ParameterizedTest
		@MethodSource("fullListAddition")
		void fullListDoesNotAdd(Inventory inventory, String toFind) {
			assertEquals(inventory.findItem(toFind).orElse(null), null);
		}
	}

	@Nested
	class getItemsTests {

		@Test
		void getEmpty() {
			List<Item> expectedItems = new ArrayList<>();

			assertEquals(cap0.getItems(), expectedItems);
		}

		@Test
		void getFilled() {
			cap5.addItem(i1);
			cap5.addItem(i1);
			cap5.addItem(i2);

			List<Item> expectedItems = new ArrayList<>();
			expectedItems.add(i1);
			expectedItems.add(i1);
			expectedItems.add(i2);

			assertEquals(cap5.getItems(), expectedItems);
		}
	}

	@Nested
	class containsTests {

		@Test
		void containsOnEmpty() {

			assertEquals(cap0.contains("Nothing"), false);
		}

		@Test
		void containsOnFilled() {
			cap5.addItem(i1);
			cap5.addItem(i2);


			assertEquals(cap5.contains(i1.getName()), true);

			assertEquals(cap5.contains(i2.getName()), true);
			
			assertEquals(cap5.contains(i3.getName()), false);
		}
	}

	@Nested
	class ItemRemovalTests {

		@Test
		void removeFromEmpty() {
			assertEquals(cap0.removeItem("Nothing"), null);
		}

		private static Stream<Arguments> removalCases() {
			cap1.addItem(i1);
			cap5.addItem(i1);
			cap5.addItem(i2);
			List<Item> cap1expectedItems = new ArrayList<>();
			List<Item> cap5expectedItems = new ArrayList<>();
			cap5expectedItems.add(i2);
			return Stream.of(Arguments.of(cap1, i1, cap1expectedItems), Arguments.of(cap5, i1, cap5expectedItems));
		}

		@ParameterizedTest
		@MethodSource("removalCases")
		void removeNotIn(Inventory inventory) {
			assertEquals(inventory.removeItem("Not Here"), null);
		}

		@ParameterizedTest
		@MethodSource("removalCases")
		void removeSingular(Inventory inventory, Item toRemove, List<Item> expectedItems) {
			inventory.removeItem(toRemove.getName());

			assertEquals(inventory.getItems(), expectedItems);
		}

		@Test
		void removesOneDuplicate() {
			cap5.addItem(i1);
			cap5.addItem(i1);

			List<Item> cap5expectedItems = new ArrayList<>();
			cap5expectedItems.add(i1);

			cap5.removeItem(i1.getName());

			assertEquals(cap5.getItems(), cap5expectedItems);
		}

		@Test
		void removeMultiple() {

			cap5.addItem(i1);
			cap5.addItem(i2);

			List<Item> cap5expectedItems = new ArrayList<>();

			cap5.removeItem(i1.getName());
			cap5.removeItem(i2.getName());

			assertEquals(cap5.getItems(), cap5expectedItems);
		}
	}
}
