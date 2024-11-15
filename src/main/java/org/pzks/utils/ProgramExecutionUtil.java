package org.pzks.utils;

import java.util.Scanner;

public class ProgramExecutionUtil {
    public static boolean confirmAndProceed() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Are you sure you want to proceed? [Yes/Y]: ");

        String userInput = scanner.nextLine().trim().toLowerCase();

        return userInput.isEmpty() || userInput.equals("yes") || userInput.equals("y");
    }
}
