// SlotMachine.java
// Main game logic for the slot machine
package com.enigma.slotmachine;

public class SlotMachine {
    private static final int REELS = 5;
    private static final int ROWS = 3;
    private static final int[][] PAYLINES = {
        {0, 0, 0, 0, 0}, // Top row
        {1, 1, 1, 1, 1}, // Middle row
        {2, 2, 2, 2, 2}, // Bottom row
        {0, 1, 2, 1, 0}, // V shape
        {2, 1, 0, 1, 2}  // Inverted V
    };
    private final Reel[] slotReels;
    private int balance;
    private int betAmount = 1;
    private static final int[] BET_OPTIONS = {1, 2, 5, 10};

    public SlotMachine(int startingBalance) {
        this.balance = startingBalance;
        slotReels = new Reel[REELS];
        for (int i = 0; i < REELS; i++) {
            slotReels[i] = new Reel();
        }
    }

    public int getBalance() {
        return balance;
    }

    public void addBalance(int amount) {
        balance += amount;
    }

    public void deductBalance(int amount) {
        balance -= amount;
    }

    public int getBetAmount() {
        return betAmount;
    }

    public void setBetAmount(int betAmount) {
        this.betAmount = betAmount;
    }

    public int[] getBetOptions() {
        return BET_OPTIONS;
    }

    // Spins the reels and returns the resulting grid
    public Symbol[][] spin() {
        Symbol[][] grid = new Symbol[ROWS][REELS];
        for (int col = 0; col < REELS; col++) {
            Symbol[] window = slotReels[col].spin();
            for (int row = 0; row < ROWS; row++) {
                grid[row][col] = window[row];
            }
        }
        return grid;
    }

    // Returns the number of scatter symbols in the grid
    public int countScatters(Symbol[][] grid) {
        int scatterCount = 0;
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < REELS; col++) {
                if (grid[row][col] == Symbol.SCATTER) scatterCount++;
            }
        }
        return scatterCount;
    }

    // Checks paylines and scatter wins, returns total payout (scatter payout included)
    public int calculatePayout(Symbol[][] grid) {
        int totalPayout = 0;
        // Check paylines for 3, 4, 5 consecutive matches
        for (int[] payline : PAYLINES) {
            Symbol first = grid[payline[0]][0];
            if (first == Symbol.SCATTER) continue;
            int match = 1;
            for (int col = 1; col < REELS; col++) {
                if (grid[payline[col]][col] == first) {
                    match++;
                } else {
                    break;
                }
            }
            if (match >= 3) {
                totalPayout += first.getPayout(match);
            }
        }
        // Scatter payout (anywhere on grid)
        int scatterCount = countScatters(grid);
        if (scatterCount >= 3) {
            totalPayout += Symbol.SCATTER.getPayout(scatterCount > 5 ? 5 : scatterCount);
        }
        return totalPayout * betAmount;
    }

    // Returns a string representation of the grid
    public String gridToString(Symbol[][] grid) {
        StringBuilder sb = new StringBuilder();
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < REELS; col++) {
                sb.append(String.format("%-8s ", grid[row][col].getName()));
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    // Returns a string representation of the payout table
    public String payoutTableToString() {
        StringBuilder sb = new StringBuilder();
        sb.append("--- Payout Table ---\n");
        sb.append(String.format("%-7s  %8s  %8s  %8s%3s%8s%8s%n", "Symbol", "3 in Row", "4 in Row", "5 in Row", "3x", "4x", "5x"));
        for (Symbol s : Symbol.values()) {
            String scatter = s == Symbol.SCATTER ? String.format("%6d%8d%8d", s.getPayout(3), s.getPayout(4), s.getPayout(5)) : String.format("%6s%8s%8s", "-", "-", "-");
            sb.append(String.format("%-7s%8d%8d%8d   %s%n", s.getName(), s.getPayout(3), s.getPayout(4), s.getPayout(5), scatter));
        }
        sb.append("--------------------\n");
        return sb.toString();
    }

    // Print methods for backward compatibility
    public void printGrid(Symbol[][] grid) {
        System.out.println("\n" + gridToString(grid));
    }

    public void printPayoutTable() {
        System.out.println(payoutTableToString());
    }
}
