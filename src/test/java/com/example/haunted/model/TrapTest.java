package com.example.haunted.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class TrapTest {
    // happy path 
    @ParameterizedTest(name = "[{index}] type={0}, dmg={1}, armed={2}, oneTime={3}")
    @CsvSource({
            "ELECTRIC, 10, true,  true",
            "ELECTRIC, 0,  false, true",
            "STEAM,    5,  true,  false",
            "STEAM,    1,  false, false"
    })
    @DisplayName("constructor stores all fields as provided")
    void constructorStoresFields(TrapType type, int damage, boolean armed, boolean oneTime) {
        Trap trap = new Trap("tripwire", type, damage, armed, oneTime);
        assertEquals("tripwire", trap.getName());
        assertEquals(type, trap.getType());
        assertEquals(damage, trap.getDamage());
        assertEquals(armed, trap.isArmed());
        assertEquals(oneTime, trap.isOneTimeTrigger());
    }

    @ParameterizedTest
    @ValueSource(ints = {Integer.MIN_VALUE, -1, 0, 1, 100, Integer.MAX_VALUE})
    @DisplayName("damage is stored unchanged across boundary values")
    void damageBoundaryValues(int dmg) {
        Trap trap = new Trap("X", TrapType.ELECTRIC, dmg, true, true);
        assertEquals(dmg, trap.getDamage());
    }

    @ParameterizedTest
    @EnumSource(TrapType.class)
    @DisplayName("all TrapType enum values accepted")
    void acceptsEveryTrapType(TrapType type) {
        Trap trap = new Trap("X", type, 1, true, true);
        assertEquals(type, trap.getType());
    }

    // null guards 

    @Test
    @DisplayName("null name throws NullPointerException")
    void nullNameThrows() {
        assertThrows(NullPointerException.class,() -> new Trap(null, TrapType.ELECTRIC, 5, true, true));
    }

    @Test
    @DisplayName("null type throws NullPointerException")
    void nullTypeThrows() {
        assertThrows(NullPointerException.class,() -> new Trap("X", null, 5, true, true));
    }

    // disarm() behavior 
    @Test
    @DisplayName("disarm() flips an armed trap to unarmed")
    void disarmArmedTrap() {
        Trap trap = new Trap("X", TrapType.STEAM, 5, true, true);
        assertTrue(trap.isArmed());
        trap.disarm();
        assertFalse(trap.isArmed());
    }

    @Test
    @DisplayName("disarm() on unarmed trap stays unarmed")
    void disarmAlreadyDisarmed() {
        Trap trap = new Trap("X", TrapType.STEAM, 5, false, true);
        trap.disarm();
        assertFalse(trap.isArmed());
    }

    @Test
    @DisplayName("disarm() does not mutate other fields")
    void disarmLeavesOtherFieldsAlone() {
        Trap trap = new Trap("Snare", TrapType.ELECTRIC, 7, true, false);
        trap.disarm();
        assertEquals("Snare", trap.getName());
        assertEquals(TrapType.ELECTRIC, trap.getType());
        assertEquals(7, trap.getDamage());
        assertFalse(trap.isOneTimeTrigger());
    }
}