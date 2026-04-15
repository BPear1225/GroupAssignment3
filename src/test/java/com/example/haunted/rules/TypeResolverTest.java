package com.example.haunted.rules;

import com.example.haunted.events.InteractionResult;
import com.example.haunted.model.Inventory;
import com.example.haunted.model.Player;
import com.example.haunted.model.Trap;
import com.example.haunted.model.TrapType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class TrapResolverTest {

    private TrapResolver resolver;
    private Player player;

    @BeforeEach
    void setUp() {
        resolver = new TrapResolver();
        player = new Player("Tester", 100, 10, 5, new Inventory(10));
    }

    // trap == null

    @Test
    @DisplayName("Null trap returns failure and player takes no damage")
    void nullTrapReturnsFailure() {
        int before = player.getHealth();

        InteractionResult result = resolver.resolveTrap(player, null);

        assertFalse(result.isSuccess());
        assertEquals("No trap was triggered.", result.getMessage());
        assertEquals(before, player.getHealth());
    }

    // trap exists but not armed 

    @Test
    @DisplayName("Unarmed trap returns failure and player takes no damage")
    void unarmedTrapReturnsFailure() {
        Trap trap = new Trap("Old Wire", TrapType.ELECTRIC, 25, false, true);
        int before = player.getHealth();

        InteractionResult result = resolver.resolveTrap(player, trap);

        assertFalse(result.isSuccess());
        assertEquals("No trap was triggered.", result.getMessage());
        assertEquals(before, player.getHealth());
        assertFalse(trap.isArmed());
    }

    // armed one-time trigger

    @Test
    @DisplayName("Armed one-time trap deals damage and disarms itself")
    void armedOneTimeTrapTriggersAndDisarms() {
        Trap trap = new Trap("Live Wire", TrapType.ELECTRIC, 20, true, true);
        int before = player.getHealth();

        InteractionResult result = resolver.resolveTrap(player, trap);

        assertTrue(result.isSuccess());
        assertEquals("Trap 'Live Wire' triggered for 20 damage.", result.getMessage());
        assertEquals(before - 20, player.getHealth());
        assertFalse(trap.isArmed());
    }

    @Test
    @DisplayName("One-time trap fires exactly once across multiple resolve calls")
    void oneTimeTrapFiresOnlyOnce() {
        Trap trap = new Trap("Bear Trap", TrapType.STEAM, 10, true, true);
        int before = player.getHealth();

        InteractionResult first = resolver.resolveTrap(player, trap);
        InteractionResult second = resolver.resolveTrap(player, trap);

        assertTrue(first.isSuccess());
        assertFalse(second.isSuccess());
        assertEquals(before - 10, player.getHealth());
    }

    // armed persistent (NOT one-time) 

    @Test
    @DisplayName("Persistent trap stays armed after firing")
    void persistentTrapStaysArmed() {
        Trap trap = new Trap("Steam Vent", TrapType.STEAM, 8, true, false);

        InteractionResult result = resolver.resolveTrap(player, trap);

        assertTrue(result.isSuccess());
        assertTrue(trap.isArmed());
    }

    @Test
    @DisplayName("Persistent trap deals damage on every trigger")
    void persistentTrapDealsRepeatDamage() {
        Trap trap = new Trap("Steam Vent", TrapType.STEAM, 8, true, false);
        int before = player.getHealth();

        resolver.resolveTrap(player, trap);
        resolver.resolveTrap(player, trap);
        resolver.resolveTrap(player, trap);

        assertEquals(before - 24, player.getHealth());
        assertTrue(trap.isArmed());
    }

    // Damage boundary values 

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 50, 99, 100})
    @DisplayName("Damage values are applied exactly (boundary check)")
    void damageBoundariesApplied(int dmg) {
        Trap trap = new Trap("X", TrapType.ELECTRIC, dmg, true, true);
        int before = player.getHealth();

        resolver.resolveTrap(player, trap);

        assertEquals(before - dmg, player.getHealth());
    }

    @Test
    @DisplayName("Overkill damage clamps HP to 0 (player dies, does not go negative)")
    void overkillDamageClampsToZero() {
        Trap trap = new Trap("Mega", TrapType.ELECTRIC, 9999, true, true);
        resolver.resolveTrap(player, trap);
        assertEquals(0, player.getHealth());
        assertFalse(player.isAlive());
    }

    // Message formatting 

    @ParameterizedTest
    @EnumSource(TrapType.class)
    @DisplayName("Success message format is consistent across trap types")
    void messageFormatRegardlessOfType(TrapType type) {
        Trap trap = new Trap("Zapper", type, 3, true, true);
        InteractionResult result = resolver.resolveTrap(player, trap);
        assertEquals("Trap 'Zapper' triggered for 3 damage.", result.getMessage());
    }
}