package com.enigma.slotmachine;

import java.util.EnumMap;
import java.util.Map;

/**
 * Main game logic for the slot machine.
 * <p>
 * Handles reels, paylines, symbol distribution, spins, payouts, configuration, and user balance.
 * Supports configurable symbol distribution, paylines, and minimum scatter distance via properties.
 * Implements payout logic for paylines and scatters, including free spins and bonus features.
 * <p>
 * All configuration is loaded from slotmachine.properties, and the game is designed for extensibility and testability.
 */
public class SlotMachine implements ISlotMachine {
    private static final int REELS = 5;
    private static final int ROWS = 3;
    private final IReel[] slotReels;
    private int balance;
    private int betAmount = 1;
    private static final int[] BET_OPTIONS = {1, 2, 5, 10};
    private final boolean payAllWins;
    private final Map<Symbol, Integer> symbolDistribution;
    private final int[][] paylines;
    private final int minScatterDistance;

    /**
     * Constructs a SlotMachine with a starting balance and payout mode.
     * @param startingBalance Initial player balance
     * @param payAllWins If true, pay all winning lines; if false, only pay the highest line win
     */
    public SlotMachine(int startingBalance, boolean payAllWins) {
        this(startingBalance, payAllWins, null, null, 3);
    }
    /**
     * Constructs a SlotMachine with a starting balance, defaulting to pay all wins.
     * @param startingBalance Initial player balance
     */
    public SlotMachine(int startingBalance) {
        this(startingBalance, true, null, null, 3);
    }
    /**
     * Constructs a SlotMachine with a starting balance, payout mode, symbol distribution, and paylines.
     * @param startingBalance Initial player balance
     * @param payAllWins If true, pay all winning lines; if false, only pay the highest line win
     * @param symbolConfig Symbol distribution string (e.g., TEN:15,J:15,...)
     * @param paylinesConfig Paylines string (e.g., 1,1,1,1,1;0,0,0,0,0;...)
     */
    public SlotMachine(int startingBalance, boolean payAllWins, String symbolConfig, String paylinesConfig) {
        this(startingBalance, payAllWins, symbolConfig, paylinesConfig, 3);
    }
    /**
     * Constructs a SlotMachine with a starting balance, payout mode, symbol distribution, paylines, and min scatter distance.
     * @param startingBalance Initial player balance
     * @param payAllWins If true, pay all winning lines; if false, only pay the highest line win
     * @param symbolConfig Symbol distribution string (e.g., TEN:15,J:15,...)
     * @param paylinesConfig Paylines string (e.g., 1,1,1,1,1;0,0,0,0,0;...)
     * @param minScatterDistance Minimum distance between scatters on a reel
     */
    public SlotMachine(int startingBalance, boolean payAllWins, String symbolConfig, String paylinesConfig, int minScatterDistance) {
        this.balance = startingBalance;
        this.payAllWins = payAllWins;
        this.symbolDistribution = Reel.parseSymbolDistribution(symbolConfig);
        this.paylines = parsePaylines(paylinesConfig);
        this.minScatterDistance = minScatterDistance;
        slotReels = new IReel[REELS];
        for (int i = 0; i < REELS; i++) {
            slotReels[i] = new Reel(symbolDistribution, minScatterDistance);
        }
    }
    /**
     * Constructs a SlotMachine with externally provided reels (for dependency injection).
     * @param startingBalance Initial player balance
     * @param payAllWins If true, pay all winning lines; if false, only pay the highest line win
     * @param symbolConfig Symbol distribution string (e.g., TEN:15,J:15,...)
     * @param paylinesConfig Paylines string (e.g., 1,1,1,1,1;0,0,0,0,0;...)
     * @param minScatterDistance Minimum distance between scatters on a reel
     * @param reels Array of IReel to use (must be length 5)
     */
    public SlotMachine(int startingBalance, boolean payAllWins, String symbolConfig, String paylinesConfig, int minScatterDistance, IReel[] reels) {
        this.balance = startingBalance;
        this.payAllWins = payAllWins;
        this.symbolDistribution = Reel.parseSymbolDistribution(symbolConfig);
        this.paylines = parsePaylines(paylinesConfig);
        this.minScatterDistance = minScatterDistance;
        if (reels == null || reels.length != REELS) {
            throw new IllegalArgumentException("Reels array must be non-null and of length " + REELS);
        }
        this.slotReels = reels;
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
     * Returns the current paylines as a 2D array.
     * @return Paylines array
     */
    public int[][] getPaylines() {
        return paylines;
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
        for (int[] payline : paylines) {
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
     * Spins the reels and evaluates all wins, returning detailed results.
     * @return SpinResult containing grid, line wins, scatter info, and total payout
     */
    public SpinResult spinAndEvaluate() {
        Symbol[][] grid = spin();
        java.util.List<SpinResult.LineWin> lineWins = new java.util.ArrayList<>();
        int totalPayout = 0;
        SpinResult.LineWin highest = null;
        for (int i = 0; i < paylines.length; i++) {
            int[] payline = paylines[i];
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
                SpinResult.LineWin win = new SpinResult.LineWin(i + 1, first, match, payout);
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

    private int[][] parsePaylines(String config) {
        if (config == null) {
            return new int[][] {
                {1, 1, 1, 1, 1},
                {0, 0, 0, 0, 0},
                {2, 2, 2, 2, 2},
                {0, 1, 2, 1, 0},
                {2, 1, 0, 1, 2}
            };
        }
        String[] lines = config.split(";");
        int[][] result = new int[lines.length][5];
        for (int i = 0; i < lines.length; i++) {
            String[] nums = lines[i].split(",");
            for (int j = 0; j < 5; j++) {
                result[i][j] = Integer.parseInt(nums[j]);
            }
        }
        return result;
    }
}
