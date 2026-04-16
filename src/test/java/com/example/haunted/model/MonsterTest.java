package com.example.haunted.model;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class MonsterTest {

    private Monster monster;

    @BeforeEach
    void setUp() {
        monster = new Monster("sleep deprived TA", 50, 8, 3, new ArrayList<>());
    }

    @Test
    @DisplayName("constructor initialize all fields and set health to maxHealth")
    void constructorInitialState() {
        assertEquals("Sleep-Deprived TA", monster.getName());
        assertEquals(50, monster.getMaxHealth());
        assertEquals(50, monster.getHealth());
        assertEquals(8, monster.getAttack());
        assertEquals(3, monster.getDefense());
        assertTrue(monster.isAlive());
        assertTrue(monster.getLoot().isEmpty());
    }

    @Test
    @DisplayName("null name throws NullPointerException")
    void nullNameThrows() {
        assertThrows(NullPointerException.class,() -> new Monster(null, 50, 8, 3, new ArrayList<>()));
    }

    @Test
    @DisplayName("null loot list throws NullPointerException")
    void nullLootThrows() {
        assertThrows(NullPointerException.class,() -> new Monster("X", 50, 8, 3, null));
    }

    @Test
    @DisplayName("constructor makes defensive copy of the loot list")
    void constructorDefensiveCopiesLoot() {
        List<Item> input = new ArrayList<>();
        input.add(new Weapon("Pen", "A red pen", 1));
        Monster m = new Monster("X", 10, 1, 1, input);
        input.clear();  
        assertEquals(1, m.getLoot().size(),"monsters loot must be unaffected by external mutation");
    }

    @Test
    @DisplayName("loot contents and order preserved")
    void lootPreservesOrderAndContents() {
        Weapon w1 = new Weapon("Pen", "A red pen", 1);
        Weapon w2 = new Weapon("Pencil", "A #2 pencil", 2);
        List<Item> loot = new ArrayList<>();
        loot.add(w1);
        loot.add(w2);
        Monster m = new Monster("X", 10, 1, 1, loot);
        assertEquals(2, m.getLoot().size());
        assertSame(w1, m.getLoot().get(0));
        assertSame(w2, m.getLoot().get(1));
    }

    @Test
    @DisplayName("getLoot returns an unmodifiable view")
    void getLootIsUnmodifiable() {
        List<Item> view = monster.getLoot();
        assertThrows(UnsupportedOperationException.class,() -> view.add(new Weapon("Pen", "A red pen", 1)));
    }

    @ParameterizedTest(name = "takeDamage({0}) on 50 HP -> {1}")
    @CsvSource({
            "0,    50",   // zero damage
            "1,    49",   // minimum 
            "25,   25",   // normal
            "49,   1",    // 1 hp clutch
            "50,   0",    // exact kill boundary
            "51,   0",    // overkill clamps
            "9999, 0"     // large overkill clamps
    })
    @DisplayName("takeDamage subtracts and clamps to 0 floor")
    void takeDamageClamps(int dmg, int expected) {
        monster.takeDamage(dmg);
        assertEquals(expected, monster.getHealth());
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, -50, Integer.MIN_VALUE})
    @DisplayName("negative damage is treated as zero")
    void negativeDamageIgnored(int dmg) {
        monster.takeDamage(dmg);
        assertEquals(50, monster.getHealth());
    }

    @Test
    @DisplayName("maxHealth doesnt changes when taking damage")
    void maxHealthUnchangedAfterDamage() {
        monster.takeDamage(30);
        assertEquals(50, monster.getMaxHealth());
    }

    @Test
    @DisplayName("isAlive true 1 HP, false 0 HP")
    void isAliveBoundary() {
        monster.takeDamage(49);
        assertTrue(monster.isAlive());
        assertEquals(1, monster.getHealth());
        monster.takeDamage(1);
        assertFalse(monster.isAlive());
        assertEquals(0, monster.getHealth());
    }
}