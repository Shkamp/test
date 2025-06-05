// Reel.java
// Represents a single reel in the slot machine
package com.enigma.slotmachine;

import java.util.*;

public class Reel {
    private final List<Symbol> strip;
    private final Random random;

    public Reel() {
        this.random = new Random();
        this.strip = buildReelStrip();
    }

    public Reel(Map<Symbol, Integer> symbolDistribution) {
        this.random = new Random();
        this.strip = buildReelStrip(symbolDistribution);
    }

    // Build the reel strip with correct symbol counts and scatter spacing
    private List<Symbol> buildReelStrip() {
        Map<Symbol, Integer> defaultDist = new EnumMap<>(Symbol.class);
        defaultDist.put(Symbol.TEN, 15); defaultDist.put(Symbol.J, 15); defaultDist.put(Symbol.Q, 15);
        defaultDist.put(Symbol.K, 10); defaultDist.put(Symbol.A, 10);
        defaultDist.put(Symbol.P1, 6); defaultDist.put(Symbol.P2, 6);
        defaultDist.put(Symbol.P3, 3); defaultDist.put(Symbol.P4, 3); defaultDist.put(Symbol.SCATTER, 2);
        return buildReelStrip(defaultDist);
    }

    private List<Symbol> buildReelStrip(Map<Symbol, Integer> symbolDistribution) {
        List<Symbol> temp = new ArrayList<>();
        for (Map.Entry<Symbol, Integer> entry : symbolDistribution.entrySet()) {
            if (entry.getKey() != Symbol.SCATTER) {
                addSymbols(temp, entry.getKey(), entry.getValue());
            }
        }
        Collections.shuffle(temp, random);
        placeScatters(temp, symbolDistribution.getOrDefault(Symbol.SCATTER, 2));
        return temp;
    }

    // Places two scatters at valid, spaced positions in the list
    private void placeScatters(List<Symbol> temp) {
        int size = temp.size();
        int scatter1 = random.nextInt(size - 6) + 3; // avoid edges
        int scatter2 = (scatter1 + size / 2) % size;
        if (Math.abs(scatter1 - scatter2) < 3) scatter2 = (scatter2 + 3) % size;
        temp.add(scatter1, Symbol.SCATTER);
        temp.add(scatter2 < scatter1 ? scatter2 : scatter2 + 1, Symbol.SCATTER); // adjust for earlier insert
    }

    // Overload for custom scatter count
    private void placeScatters(List<Symbol> temp, int scatterCount) {
        int size = temp.size();
        if (scatterCount == 2) {
            int scatter1 = random.nextInt(size - 6) + 3;
            int scatter2 = (scatter1 + size / 2) % size;
            if (Math.abs(scatter1 - scatter2) < 3) scatter2 = (scatter2 + 3) % size;
            temp.add(scatter1, Symbol.SCATTER);
            temp.add(scatter2 < scatter1 ? scatter2 : scatter2 + 1, Symbol.SCATTER);
        } else {
            // Evenly space scatters
            for (int i = 0; i < scatterCount; i++) {
                int pos = (i * size) / scatterCount + i;
                temp.add(Math.min(pos, temp.size()), Symbol.SCATTER);
            }
        }
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
}
