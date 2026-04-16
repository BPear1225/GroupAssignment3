package com.example.haunted.rules;

import com.example.haunted.model.BossMonster;
import com.example.haunted.model.Inventory;
import com.example.haunted.model.Monster;
import com.example.haunted.model.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DamageCalculatorTest {

    private DamageCalculator calculator;
    private Player player;

    @BeforeEach
    void setUp() {
        calculator = new DamageCalculator();
        
        // FIXED: Inventory now correctly initializes with a capacity (e.g., 10 slots)
        Inventory inventory = new Inventory(10); 
        
        // Setup Player: 100 Max HP, 15 Base Attack, 10 Base Defense
        player = new Player("Hero", 100, 15, 10, inventory);
    }

    @Test
    void testCalculatePlayerDamage_Normal() {
        // Monster has 5 defense. Player attack (15) - 5 = 10 damage.
        Monster monster = new Monster("Slime", 30, 5, 5, new ArrayList<>());
        
        int damage = calculator.calculatePlayerDamage(player, monster);
        
        assertEquals(10, damage);
    }

    @Test
    void testCalculatePlayerDamage_MinimumOne() {
        // Monster has 20 defense. Player attack (15) - 20 = -5. 
        // Should enforce a minimum damage of 1.
        Monster armoredMonster = new Monster("Iron Golem", 50, 10, 20, new ArrayList<>());
        
        int damage = calculator.calculatePlayerDamage(player, armoredMonster);
        
        assertEquals(1, damage);
    }

    @Test
    void testCalculateMonsterDamage_Normal() {
        // Monster attack 18. Player defense (10). 18 - 10 = 8 damage.
        Monster monster = new Monster("Orc", 40, 18, 5, new ArrayList<>());
        
        int damage = calculator.calculateMonsterDamage(monster, player);
        
        assertEquals(8, damage);
    }

    @Test
    void testCalculateMonsterDamage_MinimumOne() {
        // Monster attack 5. Player defense (10). 5 - 10 = -5. 
        // Should enforce a minimum damage of 1.
        Monster weakMonster = new Monster("Bat", 10, 5, 2, new ArrayList<>());
        
        int damage = calculator.calculateMonsterDamage(weakMonster, player);
        
        assertEquals(1, damage);
    }

    @Test
    void testCalculateBossMonsterDamage_NotEnraged() {
        // Boss: 100 HP, 20 Base Attack, +10 Enraged Bonus. 
        BossMonster boss = new BossMonster("Vampire Lord", 100, 20, 10, new ArrayList<>(), 10);
        
        // Health is at 100% (greater than 50%), so attack is 20. 
        // 20 Boss Attack - 10 Player Defense = 10 damage.
        int damage = calculator.calculateMonsterDamage(boss, player);
        
        assertEquals(10, damage);
    }

    @Test
    void testCalculateBossMonsterDamage_Enraged() {
        // Boss: 100 HP, 20 Base Attack, +10 Enraged Bonus. 
        BossMonster boss = new BossMonster("Vampire Lord", 100, 20, 10, new ArrayList<>(), 10);
        
        // Deal 50 damage to drop Boss to 50 HP (half health triggers enrage)
        boss.takeDamage(50);
        
        // Now attack is 20 + 10 = 30. 
        // 30 Boss Attack - 10 Player Defense = 20 damage.
        int damage = calculator.calculateMonsterDamage(boss, player);
        
        assertEquals(20, damage);
    }
}