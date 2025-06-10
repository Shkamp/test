package com.enigma.slotmachine;

import com.enigma.slotmachine.SpinResult;

/**
 * Interface for a slot machine, allowing for swappable implementations.
 */
public interface ISlotMachine {
    Symbol[][] spin();
    int getBalance();
    void addBalance(int amount);
    void deductBalance(int amount);
    int getBetAmount();
    void setBetAmount(int betAmount);
    int[] getBetOptions();
    int[][] getPaylines();
    int countScatters(Symbol[][] grid);
    int calculatePayout(Symbol[][] grid);
    SpinResult spinAndEvaluate();
    void printPayoutTable();

}
