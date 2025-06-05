package com.enigma.slotmachine;

/**
 * Main game logic for the slot machine.
 * Handles reels, paylines, symbol distribution, spins, payouts, and configuration.
 */
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
    private final boolean payAllWins;

    /**
     * Constructs a SlotMachine with a starting balance and payout mode.
     * @param startingBalance Initial player balance
     * @param payAllWins If true, pay all winning lines; if false, only pay the highest line win
     */
    public SlotMachine(int startingBalance, boolean payAllWins) {
        this.balance = startingBalance;
        this.payAllWins = payAllWins;
        slotReels = new Reel[REELS];
        for (int i = 0; i < REELS; i++) {
            slotReels[i] = new Reel();
        }
    }
    /**
     * Constructs a SlotMachine with a starting balance, defaulting to pay all wins.
     * @param startingBalance Initial player balance
     */
    public SlotMachine(int startingBalance) {
        this(startingBalance, true);
    }

    /**
     * Returns the current balance.
     * @return Player balance
     */
    public int getBalance() {
        return balance;
    }

    /**
     * Adds to the current balance.
     * @param amount Amount to add
     */
    public void addBalance(int amount) {
        balance += amount;
    }

    /**
     * Deducts from the current balance.
     * @param amount Amount to deduct
     */
    public void deductBalance(int amount) {
        balance -= amount;
    }

    /**
     * Returns the current bet amount.
     * @return Bet amount
     */
    public int getBetAmount() {
        return betAmount;
    }

    /**
     * Sets the bet amount.
     * @param betAmount New bet amount
     */
    public void setBetAmount(int betAmount) {
        this.betAmount = betAmount;
    }

    /**
     * Returns the available bet options.
     * @return Array of bet options
     */
    public int[] getBetOptions() {
        return BET_OPTIONS;
    }

    /**
     * Spins the reels and returns the resulting grid.
     * @return 3x5 grid of symbols
     */
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

    /**
     * Counts the number of scatter symbols in the grid.
     * @param grid The symbol grid
     * @return Number of scatters
     */
    public int countScatters(Symbol[][] grid) {
        int scatterCount = 0;
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < REELS; col++) {
                if (grid[row][col] == Symbol.SCATTER) scatterCount++;
            }
        }
        return scatterCount;
    }

    /**
     * Calculates the total payout for a grid, including line and scatter wins.
     * @param grid The symbol grid
     * @return Total payout
     */
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

    /**
     * Returns a string representation of the grid.
     * @param grid The symbol grid
     * @return String representation
     */
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

    /**
     * Returns a string representation of the payout table.
     * @return String representation
     */
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
    /**
     * Prints the grid to the console.
     * @param grid The symbol grid
     */
    public void printGrid(Symbol[][] grid) {
        System.out.println("\n" + gridToString(grid));
    }

    /**
     * Prints the payout table to the console.
     */
    public void printPayoutTable() {
        System.out.println(payoutTableToString());
    }

    /**
     * Data class representing the result of a spin, including grid, wins, and payout.
     */
    public static class SpinResult {
        public final Symbol[][] grid;
        public final java.util.List<LineWin> lineWins;
        public final int scatterCount;
        public final int scatterPayout;
        public final int totalPayout;

        public SpinResult(Symbol[][] grid, java.util.List<LineWin> lineWins, int scatterCount, int scatterPayout, int totalPayout) {
            this.grid = grid;
            this.lineWins = lineWins;
            this.scatterCount = scatterCount;
            this.scatterPayout = scatterPayout;
            this.totalPayout = totalPayout;
        }
    }

    /**
     * Data class representing a single line win (payline, symbol, count, payout).
     */
    public static class LineWin {
        public final int lineIndex;
        public final Symbol symbol;
        public final int count;
        public final int payout;
        public LineWin(int lineIndex, Symbol symbol, int count, int payout) {
            this.lineIndex = lineIndex;
            this.symbol = symbol;
            this.count = count;
            this.payout = payout;
        }
    }

    /**
     * Spins the reels and evaluates all wins, returning detailed results.
     * @return SpinResult containing grid, line wins, scatter info, and total payout
     */
    public SpinResult spinAndEvaluate() {
        Symbol[][] grid = spin();
        java.util.List<LineWin> lineWins = new java.util.ArrayList<>();
        int totalPayout = 0;
        LineWin highest = null;
        // Check paylines for 3, 4, 5 consecutive matches
        for (int i = 0; i < PAYLINES.length; i++) {
            int[] payline = PAYLINES[i];
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
                int payout = first.getPayout(match) * betAmount;
                LineWin win = new LineWin(i + 1, first, match, payout);
                if (payAllWins) {
                    lineWins.add(win);
                    totalPayout += payout;
                } else {
                    if (highest == null || payout > highest.payout) highest = win;
                }
            }
        }
        if (!payAllWins && highest != null) {
            lineWins.add(highest);
            totalPayout += highest.payout;
        }
        // Scatter payout (anywhere on grid)
        int scatterCount = countScatters(grid);
        int scatterPayout = 0;
        if (scatterCount >= 3) {
            scatterPayout = Symbol.SCATTER.getPayout(Math.min(scatterCount, 5)) * betAmount;
            totalPayout += scatterPayout;
        }
        return new SpinResult(grid, lineWins, scatterCount, scatterPayout, totalPayout);
    }
}
