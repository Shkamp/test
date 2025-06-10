package com.enigma.slotmachine;

/**
 * Interface for a slot machine reel, allowing for swappable implementations.
 */
public interface IReel {
    Symbol[] spin();
    Symbol[] getFullStrip();
}
