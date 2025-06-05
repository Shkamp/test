/* package com.enigma.slotmachine;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SlotMachineTest {
    @Test
    void testInitialBalance() {
        SlotMachine sm = new SlotMachine(100);
        assertEquals(100, sm.getBalance());
    }

    @Test
    void testAddAndDeductBalance() {
        SlotMachine sm = new SlotMachine(50);
        sm.addBalance(25);
        assertEquals(75, sm.getBalance());
        sm.deductBalance(30);
        assertEquals(45, sm.getBalance());
    }

    @Test
    void testSpinReturnsGrid() {
        SlotMachine sm = new SlotMachine(100);
        Symbol[][] grid = sm.spin();
        assertEquals(3, grid.length);
        assertEquals(5, grid[0].length);
    }

    @Test
    void testCountScatters() {
        SlotMachine sm = new SlotMachine(100);
        Symbol[][] grid = new Symbol[][] {
            {Symbol.SCATTER, Symbol.TEN, Symbol.J, Symbol.SCATTER, Symbol.Q},
            {Symbol.K, Symbol.A, Symbol.SCATTER, Symbol.P1, Symbol.P2},
            {Symbol.P3, Symbol.P4, Symbol.Q, Symbol.J, Symbol.K}
        };
        assertEquals(3, sm.countScatters(grid));
    }

    @Test
    void testCalculatePayoutLineWin() {
        SlotMachine sm = new SlotMachine(100);
        // Middle row is all P1 (payline 2)
        Symbol[][] grid = new Symbol[][] {
            {Symbol.TEN, Symbol.J, Symbol.Q, Symbol.K, Symbol.A},
            {Symbol.P1, Symbol.P1, Symbol.P1, Symbol.P1, Symbol.P1},
            {Symbol.P2, Symbol.P2, Symbol.P2, Symbol.P2, Symbol.P2}
        };
        int payout = sm.calculatePayout(grid);
        // 5 in a row for P1: payout should be 16
        assertTrue(payout >= 16);
    }

    @Test
    void testCalculatePayoutScatterWin() {
        SlotMachine sm = new SlotMachine(100);
        Symbol[][] grid = new Symbol[][] {
            {Symbol.SCATTER, Symbol.SCATTER, Symbol.SCATTER, Symbol.TEN, Symbol.J},
            {Symbol.K, Symbol.A, Symbol.P1, Symbol.P2, Symbol.P3},
            {Symbol.P4, Symbol.Q, Symbol.J, Symbol.K, Symbol.A}
        };
        int payout = sm.calculatePayout(grid);
        // 3 scatters anywhere: payout should be at least 2
        assertTrue(payout >= 2);
    }
}
 */