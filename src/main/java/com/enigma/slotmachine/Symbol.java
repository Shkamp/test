// Symbol.java
// Represents a symbol on the slot machine reels
package com.enigma.slotmachine;

public enum Symbol {
    TEN("10", new int[]{1, 2, 4}),
    J("J", new int[]{1, 2, 4}),
    Q("Q", new int[]{1, 2, 4}),
    K("K", new int[]{2, 4, 8}),
    A("A", new int[]{2, 4, 8}),
    P1("P1", new int[]{4, 8, 16}),
    P2("P2", new int[]{4, 8, 16}),
    P3("P3", new int[]{8, 16, 32}),
    P4("P4", new int[]{8, 16, 32}),
    SCATTER("Scatter", new int[]{2, 5, 20});

    private final String name;
    private final int[] payouts; // payouts[0]=3, [1]=4, [2]=5

    Symbol(String name, int[] payouts) {
        this.name = name;
        this.payouts = payouts;
    }

    public String getName() {
        return name;
    }

    public int getPayout(int count) {
        if (count < 3 || count > 5) return 0;
        return payouts[count - 3];
    }
}
