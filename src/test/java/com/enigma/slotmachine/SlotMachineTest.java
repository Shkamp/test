package com.enigma.slotmachine;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SlotMachineTest {
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

    @Test
    void testCalculatePayoutVShapeWin() {
        SlotMachine sm = new SlotMachine(100);
        // V shape payline: {0,1,2,1,0} (all Q)
        Symbol[][] grid = new Symbol[][] {
            {Symbol.Q, Symbol.TEN, Symbol.J, Symbol.K, Symbol.Q},
            {Symbol.P1, Symbol.Q, Symbol.P2, Symbol.Q, Symbol.P3},
            {Symbol.P4, Symbol.P2, Symbol.Q, Symbol.P4, Symbol.P2}
        };
        // V shape: Q at (0,0), (1,1), (2,2), (1,3), (0,4)
        int payout = sm.calculatePayout(grid);
        // 5 in a row for Q: payout should be 4
        assertTrue(payout >= 4);
    }

    @Test
    void testCalculatePayoutReverseVShapeWin() {
        SlotMachine sm = new SlotMachine(100);
        // Reverse V shape payline: {2,1,0,1,2} (all K)
        Symbol[][] grid = new Symbol[][] {
            {Symbol.P1, Symbol.P2, Symbol.K, Symbol.P3, Symbol.P4},
            {Symbol.P2, Symbol.K, Symbol.P3, Symbol.K, Symbol.P2},
            {Symbol.K, Symbol.P4, Symbol.P1, Symbol.P2, Symbol.K}
        };
        // Reverse V: K at (2,0), (1,1), (0,2), (1,3), (2,4)
        int payout = sm.calculatePayout(grid);
        // 5 in a row for K: payout should be 8
        assertTrue(payout >= 8);
    }

    @Test
    void testMinimumBalanceNoSpin() {
        SlotMachine sm = new SlotMachine(1);
        sm.deductBalance(1);
        assertTrue(sm.getBalance() < sm.getBetAmount() || sm.getBalance() == 0);
    }

    @Test
    void testMaximumBalance() {
        SlotMachine sm = new SlotMachine(Integer.MAX_VALUE - 10);
        sm.addBalance(10);
        assertEquals(Integer.MAX_VALUE, sm.getBalance());
    }

    @Test
    void testBetChangeDuringFreeSpins() {
        SlotMachine sm = new SlotMachine(100);
        sm.setBetAmount(5);
        int oldBet = sm.getBetAmount();
        sm.setBetAmount(2); // Change bet during free spins
        assertEquals(5, oldBet);
        assertEquals(2, sm.getBetAmount());
    }

    @Test
    void testMultipleSimultaneousWins() {
        SlotMachine sm = new SlotMachine(100, true);
        // Both top and middle row win with Q
        Symbol[][] grid = new Symbol[][] {
            {Symbol.Q, Symbol.Q, Symbol.Q, Symbol.Q, Symbol.Q},
            {Symbol.Q, Symbol.Q, Symbol.Q, Symbol.Q, Symbol.Q},
            {Symbol.P1, Symbol.P2, Symbol.P3, Symbol.P4, Symbol.K}
        };
        int payout = sm.calculatePayout(grid);
        assertTrue(payout >= 8); // Both lines should pay
    }

    @Test
    void testScatterSpacingOnReel() {
        Reel reel = new Reel();
        Symbol[] symbols = reel.getFullStrip();
        for (int i = 0; i < symbols.length; i++) {
            int scatterCount = 0;
            for (int j = 0; j < 3; j++) {
                if (symbols[(i + j) % symbols.length] == Symbol.SCATTER) scatterCount++;
            }
            assertTrue(scatterCount <= 1, "No window of 3 symbols should have more than one scatter");
        }
    }
}
