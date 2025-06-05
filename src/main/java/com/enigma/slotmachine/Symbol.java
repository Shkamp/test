// Symbol.java
// Represents a symbol on the slot machine reels
package com.enigma.slotmachine;

/**
 * Enum representing all possible symbols on the slot machine reels.
 * Each symbol has a display name and payout values for 3, 4, or 5 in a row.
 */
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
    SCATTER("S", new int[]{2, 5, 20});

    private final String name;
    private final int[] payouts; // payouts[0]=3, [1]=4, [2]=5

    Symbol(String name, int[] payouts) {
        this.name = name;
        this.payouts = payouts;
    }

    /**
     * @return the display name of the symbol
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the payout for a given count of consecutive symbols (3, 4, or 5).
     * @param count Number of consecutive symbols
     * @return Payout value, or 0 if count is not 3-5
     */
    public int getPayout(int count) {
        if (count < 3 || count > 5) return 0;
        return payouts[count - 3];
    }
}
