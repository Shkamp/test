package com.enigma.slotmachine;

import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
import org.junit.jupiter.api.Test;
import com.enigma.slotmachine.SpinResult;

/**
 * Unit test for Main.getHighlightMatrix to verify highlighting for custom paylines.
 */
public class MainHighlightTest {
    @Test
    void testHighlightMatrixForVShapePayline() {
        // V shape payline: {0,1,2,1,0}
        int[][] paylines = new int[][] {
            {0,1,2,1,0}
        };
        Symbol[][] grid = new Symbol[][] {
            {Symbol.Q, Symbol.TEN, Symbol.J, Symbol.K, Symbol.Q},
            {Symbol.P1, Symbol.Q, Symbol.P2, Symbol.Q, Symbol.P3},
            {Symbol.P4, Symbol.P2, Symbol.Q, Symbol.P4, Symbol.P2}
        };
        // Simulate a win on the V-shape payline (all Q)
        List<SpinResult.LineWin> wins = new ArrayList<>();
        wins.add(new SpinResult.LineWin(1, Symbol.Q, 5, 4));
        boolean[][] highlight = Main.getHighlightMatrix(grid, wins, paylines);
        // Only the V-shape cells should be highlighted
        int[][] expected = new int[][] {
            {1,0,0,0,1},
            {0,1,0,1,0},
            {0,0,1,0,0}
        };
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 5; col++) {
                assertEquals(expected[row][col] == 1, highlight[row][col], "Mismatch at ("+row+","+col+")");
            }
        }
    }

    @Test
    void testHighlightMatrixForCustomPayline() {
        // Custom zigzag payline: {0,2,0,2,0}
        int[][] paylines = new int[][] {
            {0,2,0,2,0}
        };
        Symbol[][] grid = new Symbol[][] {
            {Symbol.A, Symbol.A, Symbol.A, Symbol.A, Symbol.A},
            {Symbol.P1, Symbol.P2, Symbol.P3, Symbol.P4, Symbol.P1},
            {Symbol.K, Symbol.K, Symbol.K, Symbol.K, Symbol.K}
        };
        List<SpinResult.LineWin> wins = new ArrayList<>();
        wins.add(new SpinResult.LineWin(1, Symbol.A, 5, 10));
        boolean[][] highlight = Main.getHighlightMatrix(grid, wins, paylines);
        int[][] expected = new int[][] {
            {1,0,1,0,1},
            {0,0,0,0,0},
            {0,1,0,1,0}
        };
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 5; col++) {
                assertEquals(expected[row][col] == 1, highlight[row][col], "Mismatch at ("+row+","+col+")");
            }
        }
    }
}
