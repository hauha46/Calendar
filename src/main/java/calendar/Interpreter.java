package calendar;

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
    System.out.println("Welcome to the Calendar Application!");
    System.out.println("Type 'exit' to quit.");

    while (true) {
      System.out.print("> ");
      String input = scanner.nextLine().trim();

      if (input.equalsIgnoreCase("exit")) {
        System.out.println("Exiting the application. Goodbye!");
        break;
      }

      try {
        commandParser.parseCommand(input);
      } catch (IllegalArgumentException e) {
        System.err.println("Error: " + e.getMessage());
      }
    }

    scanner.close();
  }
}
