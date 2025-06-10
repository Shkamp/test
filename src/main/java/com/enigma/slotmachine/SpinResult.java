package com.enigma.slotmachine;

import java.util.List;

/**
 * Data class representing the result of a spin, including grid, wins, and payout.
 */
public class SpinResult {
    public final Symbol[][] grid;
    public final List<LineWin> lineWins;
    public final int scatterCount;
    public final int scatterPayout;
    public final int totalPayout;

    public SpinResult(Symbol[][] grid, List<LineWin> lineWins, int scatterCount, int scatterPayout, int totalPayout) {
        this.grid = grid;
        this.lineWins = lineWins;
        this.scatterCount = scatterCount;
        this.scatterPayout = scatterPayout;
        this.totalPayout = totalPayout;
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
}
