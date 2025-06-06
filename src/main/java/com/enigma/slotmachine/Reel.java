// Reel.java
// Represents a single reel in the slot machine
package com.enigma.slotmachine;

import java.util.*;

/**
 * Represents a single reel in the slot machine.
 * <p>
 * Handles symbol distribution, random spinning, and scatter placement with configurable minimum distance.
 * Used by SlotMachine to build the 5x3 slot grid.
 */

public class Reel {
    private final List<Symbol> strip;
    private final Random random;

    public Reel() {
        this(new EnumMap<>(Map.of(
            Symbol.TEN, 15, Symbol.J, 15, Symbol.Q, 15,
            Symbol.K, 10, Symbol.A, 10,
            Symbol.P1, 6, Symbol.P2, 6,
            Symbol.P3, 3, Symbol.P4, 3, Symbol.SCATTER, 2
        )), 3);
    }

    public Reel(Map<Symbol, Integer> symbolDistribution) {
        this(symbolDistribution, 3);
    }

    public Reel(Map<Symbol, Integer> symbolDistribution, int minScatterDistance) {
        this.random = new Random();
        checkScatterFeasibility(symbolDistribution, minScatterDistance);
        this.strip = buildReelStrip(symbolDistribution, minScatterDistance);
    }

    // Build the reel strip with correct symbol counts and scatter spacing
    private List<Symbol> buildReelStrip(Map<Symbol, Integer> symbolDistribution, int minScatterDistance) {
        List<Symbol> temp = new ArrayList<>();
        for (Map.Entry<Symbol, Integer> entry : symbolDistribution.entrySet()) {
            if (entry.getKey() != Symbol.SCATTER) {
                addSymbols(temp, entry.getKey(), entry.getValue());
            }
        }
        Collections.shuffle(temp, random);
        placeScatters(temp, symbolDistribution.getOrDefault(Symbol.SCATTER, symbolDistribution.get(Symbol.SCATTER)), minScatterDistance);
        return temp;
    }

    private void addSymbols(List<Symbol> list, Symbol symbol, int count) {
        for (int i = 0; i < count; i++) list.add(symbol);
    }

    // Spins the reel and returns the 3-symbol visible window
    public Symbol[] spin() {
        int start = random.nextInt(strip.size());
        Symbol[] window = new Symbol[3];
        for (int i = 0; i < 3; i++) {
            window[i] = strip.get((start + i) % strip.size());
        }
        // Ensure at most one scatter in the window
        int scatterCount = 0;
        for (Symbol s : window) if (s == Symbol.SCATTER) scatterCount++;
        if (scatterCount > 1) return spin(); // re-spin if more than one scatter
        return window;
    }

    /**
     * Returns the full reel strip as an array (for testing scatter spacing).
     */
    public Symbol[] getFullStrip() {
        return strip.toArray(new Symbol[0]);
    }

    //Checks if the requested number of scatters and minimum distance is feasible
    private void checkScatterFeasibility(Map<Symbol, Integer> symbolDistribution, int minDistance) {
        int scatterCount = symbolDistribution.getOrDefault(Symbol.SCATTER, 2);
        int size = 0;
        for (Map.Entry<Symbol, Integer> entry : symbolDistribution.entrySet()) {
            if (entry.getKey() != Symbol.SCATTER) size += entry.getValue();
        }
        if (scatterCount > 1 && scatterCount * minDistance > size) {
            throw new IllegalArgumentException("Impossible to place " + scatterCount + " scatters with minimum distance " + minDistance + " on a reel of size " + size);
        }
    }

    /**
     * Places scatter symbols on the reel strip with a minimum distance between them.
     * <p>
     * This method ensures that no two scatters are placed closer than the configured minimum distance (minScatterDistance),
     * which prevents clusters of scatters and enforces fair distribution. The algorithm first checks if the requested
     * number of scatters and minimum distance is feasible for the reel size. It then calculates evenly spaced positions
     * for scatters, checks the circular (wrap-around) distance between them, and throws an exception if the constraint
     * cannot be satisfied. This logic is critical for game balance and is tested in SlotMachineTest.
     */
    private void placeScatters(List<Symbol> temp, int scatterCount, int minDistance) {
        int size = temp.size();
        if (scatterCount == 0) return;
        if (scatterCount == 1) {
            temp.add(random.nextInt(size + 1), Symbol.SCATTER);
            return;
        }
        List<Integer> positions = new ArrayList<>();
        int start = random.nextInt(size);
        for (int i = 0; i < scatterCount; i++) {
            int pos = (start + i * (size / scatterCount)) % size;
            positions.add(pos);
        }
        Collections.sort(positions);
        for (int i = 0; i < positions.size(); i++) {
            int next = positions.get((i + 1) % positions.size());
            int curr = positions.get(i);
            int dist = (next - curr + size) % size;
            if (dist < minDistance) {
                throw new IllegalArgumentException("Cannot place " + scatterCount + " scatters with minimum distance " + minDistance + " on a reel of size " + size);
            }
        }
        for (int i = 0; i < positions.size(); i++) {
            int pos = positions.get(i) + i; // adjust for previous inserts
            temp.add(pos, Symbol.SCATTER);
        }
    }
}
