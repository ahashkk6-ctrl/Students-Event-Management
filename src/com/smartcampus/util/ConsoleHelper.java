package com.smartcampus.util;

import java.util.Scanner;

/**
 * Utility for formatted console rendering, ANSI banners, and robust user input.
 */
public class ConsoleHelper {
    private static final Scanner scanner = new Scanner(System.in);

    // ANSI Colors for standard terminal presentation
    public static final String RESET = "\u001B[0m";
    public static final String CYAN = "\u001B[36m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String RED = "\u001B[31m";
    public static final String BOLD = "\u001B[1m";
    public static final String BLUE = "\u001B[34m";

    public static void printHeader(String title) {
        System.out.println();
        System.out.println(CYAN + BOLD + "==========================================================================================" + RESET);
        System.out.println(CYAN + BOLD + "   " + title + RESET);
        System.out.println(CYAN + BOLD + "==========================================================================================" + RESET);
    }

    public static void printSection(String section) {
        System.out.println(YELLOW + "\n--- " + section + " ---" + RESET);
    }

    public static void printSuccess(String message) {
        System.out.println(GREEN + "[SUCCESS] " + message + RESET);
    }

    public static void printError(String message) {
        System.out.println(RED + "[ERROR] " + message + RESET);
    }

    public static void printWarning(String message) {
        System.out.println(YELLOW + "[WARNING] " + message + RESET);
    }

    public static void printDivider() {
        System.out.println("------------------------------------------------------------------------------------------");
    }

    public static String readString(String prompt) {
        System.out.print(BOLD + prompt + ": " + RESET);
        String input = scanner.nextLine().trim();
        while (input.isEmpty()) {
            System.out.print(RED + "Input cannot be empty. " + prompt + ": " + RESET);
            input = scanner.nextLine().trim();
        }
        return input;
    }

    public static int readInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(BOLD + prompt + " (" + min + "-" + max + "): " + RESET);
            String line = scanner.nextLine().trim();
            try {
                int val = Integer.parseInt(line);
                if (val >= min && val <= max) {
                    return val;
                }
                printWarning("Please enter a value between " + min + " and " + max + ".");
            } catch (NumberFormatException e) {
                printError("Invalid integer input. Try again.");
            }
        }
    }

    public static void pause() {
        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
    }
}
