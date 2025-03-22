package calendar;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
/**
 * The class for handling I/O input from keyboard.
 */
public class Interpreter {
  private static final CommandParser commandParser = new CommandParser();

  /**
   * Main class for I/O operations handling.
   */
  public static void main(String[] args) {
    Scanner scanner = new Scanner(System.in);
    while (true) {
      System.out.println("Welcome to the Calendar Application!");
      System.out.println("Select mode: 'interactive', 'headless', or 'exit'");
      System.out.print("> ");
      String modeChoice = scanner.nextLine().trim().toLowerCase();

      if (modeChoice.equals("exit")) {
        System.out.println("Exiting the application. Goodbye!");
        break;
      } else if (modeChoice.equals("interactive")) {
        runInteractiveMode(scanner);
      } else if (modeChoice.equals("headless")) {
        System.out.println("Enter the path to the command file:");
        System.out.print("> ");
        String filePath = scanner.nextLine().trim();
        runHeadlessMode(filePath);
      } else {
        System.out.println("Invalid mode. Please try again.");
      }
    }

    scanner.close();
  }

  /**
   * Interactive mode handling. Will take commands from user input.
   *
   * @param scanner I/O scanner
   */
  private static void runInteractiveMode(Scanner scanner) {
    System.out.println("Interactive mode. Type 'mode' to return to mode selection.");
    while (true) {
      System.out.print("> ");
      String input = scanner.nextLine().trim();

      if (input.equalsIgnoreCase("mode")) {
        System.out.println("Returning to mode selection.");
        return;
      }

      try {
        commandParser.parseCommand(input);
      } catch (IllegalArgumentException e) {
        System.out.println("Error: " + e.getMessage());
      }
    }
  }

  /**
   * Headless mode handling. Will take commands from file input.
   *
   * @param filename Input file name/path.
   */
  private static void runHeadlessMode(String filename) {
    try (Scanner fileScanner = new Scanner(new File(filename))) {
      System.out.println("Running in headless mode with file: " + filename);

      int lineNumber = 0;

      while (fileScanner.hasNextLine()) {
        lineNumber++;
        String command = fileScanner.nextLine().trim();
        System.out.println("Command: " + command);
        if (command.isEmpty()) {
          continue;
        }

        if (command.equalsIgnoreCase("exit")) {
          System.out.println("Exit command found. Terminating headless mode.");
          return;
        }

        try {
          commandParser.parseCommand(command);
        } catch (Exception e) {
          System.out.println("Error at line " + lineNumber + ": " + command);
          System.out.println("Reason: " + e.getMessage());
          return;
        }
      }

      System.out.println("Error: Missing exit command at end of file.");
    } catch (FileNotFoundException e) {
      System.out.println("Error: Could not find file '" + filename + "'");
    }
  }
}