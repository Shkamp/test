package com.enigma.slotmachine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
    public static void main(String[] args) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        SlotMachine slotMachine = new SlotMachine(100); // Starting balance
        int freeSpins = 0;
        System.out.println("Welcome to the Java Slot Machine!");
        boolean running = true;
        try {
            while (running) {
                printMenu(slotMachine, freeSpins);
                String input = reader.readLine();
                switch (input) {
                    case "1":
                        freeSpins = handleSpin(slotMachine, freeSpins, reader);
                        break;
                    case "2":
                        slotMachine.printPayoutTable();
                        break;
                    case "3":
                        running = false;
                        System.out.println("Thanks for playing!");
                        break;
                    case "4":
                        changeBetAmount(reader, slotMachine);
                        break;
                    default:
                        System.out.println("Invalid option. Try again.");
                }
            }
        } catch (IOException e) {
            System.out.println("Input/output error. Exiting game.");
        }
    }

    private static void printMenu(SlotMachine slotMachine, int freeSpins) {
        if (freeSpins > 0) {
            System.out.printf("%nBalance: %d (Free Spins left: %d)%n", slotMachine.getBalance(), freeSpins);
        } else {
            System.out.printf("%nBalance: %d%n", slotMachine.getBalance());
        }
        System.out.printf("Current Bet: %d%n", slotMachine.getBetAmount());
        System.out.println("1. Spin");
        System.out.println("2. View Payout Table");
        System.out.println("3. Exit");
        System.out.println("4. Change Bet Amount");
        System.out.println("Choose an option: ");
    }

    private static int handleSpin(SlotMachine slotMachine, int freeSpins, BufferedReader reader) throws IOException {
        if (freeSpins == 0 && slotMachine.getBalance() < 10) {
            System.out.println("Not enough balance to spin. Each spin costs 10.");
            return freeSpins;
        }
        if (freeSpins == 0) slotMachine.deductBalance(slotMachine.getBetAmount());
        else if (freeSpins > 0) System.out.println("Using free spin...");
        else freeSpins--;
        Symbol[][] grid = slotMachine.spin();
        System.out.println("\n--- Spin Result ---");
        slotMachine.printGrid(grid);
        int payout = slotMachine.calculatePayout(grid);
        int scatters = slotMachine.countScatters(grid);
        if (payout > 0) {
            System.out.printf("You win: %d!%n", payout);
            slotMachine.addBalance(payout);
        } else {
            System.out.println("No win this time.");
        }
        if (scatters >= 3) {
            System.out.printf("Bonus! You triggered 10 free spins with %d Scatters!%n", scatters);
            freeSpins += 10;
        }
        return freeSpins;
    }

    private static void changeBetAmount(BufferedReader reader, SlotMachine slotMachine) throws IOException {
        int[] options = slotMachine.getBetOptions();
        System.out.println("Choose your bet amount:");
        for (int i = 0; i < options.length; i++) {
            System.out.printf("%d. %d%n", i + 1, options[i]);
        }
        System.out.print("Enter option number: ");
        String input = reader.readLine();
        try {
            int choice = Integer.parseInt(input);
            if (choice >= 1 && choice <= options.length) {
                slotMachine.setBetAmount(options[choice - 1]);
                System.out.printf("Bet amount set to %d%n", options[choice - 1]);
            } else {
                System.out.println("Invalid bet option.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
        }
    }
}
