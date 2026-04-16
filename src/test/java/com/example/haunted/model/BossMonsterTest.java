package com.example.haunted.model;

import java.util.ArrayList;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class BossMonsterTest {
    private BossMonster bossAtHealth(int maxHealth, int targetHealth, int attack, int bonus) {
        BossMonster boss = new BossMonster("final exam phantom", maxHealth, attack, 0, new ArrayList<>(), bonus);
        boss.takeDamage(maxHealth - targetHealth);
        return boss;
    }

    @Test
    @DisplayName("boss inherits Monster initial state")
    void inheritsMonsterFields() {
        BossMonster boss = new BossMonster("Phantom", 80, 12, 4, new ArrayList<>(), 6);
        assertEquals("Phantom", boss.getName());
        assertEquals(80, boss.getMaxHealth());
        assertEquals(80, boss.getHealth());
        assertEquals(12, boss.getAttack());
        assertEquals(4, boss.getDefense());
        assertTrue(boss.isAlive());
    }

    @Test
    @DisplayName("boss inherits takeDamage and isAlive")
    void monsterBehaviorInherited() {
        BossMonster boss = new BossMonster("phantom", 50, 10, 0, new ArrayList<>(), 5);
        boss.takeDamage(50);
        assertEquals(0, boss.getHealth());
        assertFalse(boss.isAlive());
    }

    @ParameterizedTest(name = "maxHP=100, hp={0} -> currentAttack={1}")
    @CsvSource({
            "100, 10",  // full HP, not enraged
            "75,  10",  // safely above
            "51,  10",  // just above threshold (<= -> <, <= -> >=)
            "50,  15",  // at threshold, enraged (<= -> <)
            "49,  15",  // just below
            "1,   15",  // near death
            "0,   15"   // dead, still runs
    })
    @DisplayName("getCurrentAttack adds bonus if health <= maxHealth/2")
    void getCurrentAttackBoundaries(int hp, int expectedAttack) {
        BossMonster boss = bossAtHealth(100, hp, 10, 5);
        assertEquals(expectedAttack, boss.getCurrentAttack());
    }

    @ParameterizedTest(name = "maxHP=99, hp={0} -> currentAttack={1}")
    @CsvSource({
            "99, 10",
            "50, 10",  // above 49
            "49, 15",  // threshold
            "48, 15"   // below 
    })
    @DisplayName("maxHealth/2")
    void oddMaxHealthIntegerDivision(int hp, int expectedAttack) {
        BossMonster boss = bossAtHealth(99, hp, 10, 5);
        assertEquals(expectedAttack, boss.getCurrentAttack());
    }

    @Test
    @DisplayName("enraged attack = base + bonus (for + -> - and + -> *)")
    void enragedBonusIsAdditive() {
        BossMonster boss = bossAtHealth(100, 25, 10, 7);
        assertEquals(17, boss.getCurrentAttack(),"enraged attack must be base(10) + bonus(7) not bonus alone or subtracted");
    }

    @Test
    @DisplayName("currentAttack equals base attack")
    void zeroBonusYieldsBaseAttack() {
        BossMonster lowHp = bossAtHealth(100, 30, 10, 0);
        BossMonster fullHp = new BossMonster("Phantom", 100, 10, 0, new ArrayList<>(), 0);
        assertEquals(10, lowHp.getCurrentAttack());
        assertEquals(10, fullHp.getCurrentAttack());
    }
}