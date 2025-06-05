package com.enigma.slotmachine;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * Main entry point for the Java console slot machine game.
 * Handles user interaction, menu, and game loop.
 */
public class Main {
    private static final String[] PAYLINE_NAMES = {
        "Middle Row",
        "Top Row",
        "Bottom Row",
        "V-Shape",
        "Inverted V-Shape"
    };

    /**
     * The main method to start the slot machine game.
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        boolean payAllWins = true;
        String symbolConfig = null;
        String paylinesConfig = null;
        try (FileInputStream configStream = new FileInputStream("slotmachine.properties")) {
            Properties config = new Properties();
            config.load(configStream);
            String payAll = config.getProperty("payAllWins");
            if (payAll != null) payAllWins = Boolean.parseBoolean(payAll);
            symbolConfig = config.getProperty("symbols");
            paylinesConfig = config.getProperty("paylines");
        } catch (IOException e) {
            System.out.println("Config file not found or unreadable, using default payout mode (pay all wins).");
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        SlotMachine slotMachine = new SlotMachine(100, payAllWins, symbolConfig, paylinesConfig); // Updated constructor
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

    /**
     * Prints the main menu, including balance, bet, and free spins.
     * @param slotMachine The slot machine instance
     * @param freeSpins Number of free spins left
     */
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

    /**
     * Handles a spin, including deducting balance, evaluating wins, and displaying results.
     * @param slotMachine The slot machine instance
     * @param freeSpins Number of free spins left
     * @param reader BufferedReader for user input
     * @return Updated free spins count
     * @throws IOException If an input or output exception occurred
     */
    private static int handleSpin(SlotMachine slotMachine, int freeSpins, BufferedReader reader) throws IOException {
        if (freeSpins == 0 && slotMachine.getBalance() < slotMachine.getBetAmount()) {
            System.out.println("Not enough balance to spin. Each spin costs " + slotMachine.getBetAmount() + ".");
            return freeSpins;
        }
        if (freeSpins == 0) slotMachine.deductBalance(slotMachine.getBetAmount());
        else if (freeSpins > 0) System.out.println("Using free spin...");
        else freeSpins--;
        SlotMachine.SpinResult result = slotMachine.spinAndEvaluate();
        System.out.println("\n--- Spin Result ---");
        printHighlightedGrid(result.grid, result.lineWins);
        printSpinSummary(result);
        if (result.totalPayout > 0) {
            System.out.printf("You win: %d!%n", result.totalPayout);
            slotMachine.addBalance(result.totalPayout);
        } else {
            System.out.println("No win this time.");
        }
        if (result.scatterCount >= 3) {
            System.out.printf("Bonus! You triggered 10 free spins with %d Scatters!%n", result.scatterCount);
            freeSpins += 10;
        }
        return freeSpins;
    }

    /**
     * Prints the slot grid, highlighting winning lines and scatters.
     * @param grid The slot grid
     * @param lineWins List of winning lines
     */
    private static void printHighlightedGrid(Symbol[][] grid, java.util.List<SlotMachine.LineWin> lineWins) {
        boolean[][] highlight = new boolean[grid.length][grid[0].length];
        for (SlotMachine.LineWin win : lineWins) {
            int[] payline = getPayline(win.lineIndex - 1);
            for (int col = 0; col < win.count; col++) {
                highlight[payline[col]][col] = true;
            }
        }
        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[0].length; col++) {
                String symbol = grid[row][col].getName();
                if (highlight[row][col]) {
                    System.out.print("*" + symbol + "* ");
                } else if (grid[row][col] == Symbol.SCATTER) {
                    System.out.print("#" + symbol + "# ");
                } else {
                    System.out.print(symbol + "  ");
                }
            }
            System.out.println();
        }
    }

    /**
     * Returns the payline definition by index.
     * @param index The payline index
     * @return The payline array
     */
    private static int[] getPayline(int index) {
        // Use dynamic paylines from slotMachine if possible
        // This method is only used for highlighting, so we can fetch from the current slotMachine instance
        // For now, fallback to the default if not available
        int[][] PAYLINES = {
            {1, 1, 1, 1, 1}, // Middle row
            {0, 0, 0, 0, 0}, // Top row
            {2, 2, 2, 2, 2}, // Bottom row
            {0, 1, 2, 1, 0}, // V shape
            {2, 1, 0, 1, 2}  // Inverted V
        };
        return PAYLINES[index];
    }

    /**
     * Prints a summary of the spin, including all line and scatter wins.
     * @param result The result of the spin
     */
    private static void printSpinSummary(SlotMachine.SpinResult result) {
        if (!result.lineWins.isEmpty()) {
            for (SlotMachine.LineWin win : result.lineWins) {
                String lineName = win.lineIndex >= 1 && win.lineIndex <= PAYLINE_NAMES.length ? PAYLINE_NAMES[win.lineIndex - 1] : ("Line " + win.lineIndex);
                System.out.printf("%s: %dx %s, pays %d%n", lineName, win.count, win.symbol.getName(), win.payout);
            }
        }
        if (result.scatterPayout > 0) {
            System.out.printf("Scatter: %dx, pays %d%n", result.scatterCount, result.scatterPayout);
        }
        if (result.lineWins.isEmpty() && result.scatterPayout == 0) {
            System.out.println("No winning lines or scatters.");
        }
    }

    /**
     * Allows the user to change the bet amount interactively.
     * @param reader BufferedReader for user input
     * @param slotMachine The slot machine instance
     * @throws IOException If an input or output exception occurred
     */
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
