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

    // Build the reel strip with correct symbol counts and scatter spacing
    private List<Symbol> buildReelStrip() {
        List<Symbol> temp = new ArrayList<>();
        addSymbols(temp, Symbol.TEN, 15);
        addSymbols(temp, Symbol.J, 15);
        addSymbols(temp, Symbol.Q, 15);
        addSymbols(temp, Symbol.K, 10);
        addSymbols(temp, Symbol.A, 10);
        addSymbols(temp, Symbol.P1, 6);
        addSymbols(temp, Symbol.P2, 6);
        addSymbols(temp, Symbol.P3, 3);
        addSymbols(temp, Symbol.P4, 3);
        Collections.shuffle(temp, random);
        removeTwoRandomSymbols(temp);
        placeScatters(temp);
        return temp;
    }

    // Removes two random symbols from the list to make space for scatters
    private void removeTwoRandomSymbols(List<Symbol> temp) {
        // Remove the first two elements after shuffle (randomized)
        temp.remove(1);
        temp.remove(0);
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
}
