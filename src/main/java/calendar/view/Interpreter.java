package calendar.view;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import javax.swing.*;

import calendar.controller.CommandController;
import calendar.controller.SwingController;
import calendar.manager.ICalendarManager;

/**
 * The class for handling I/O input from keyboard.
 */
public class Interpreter {

  /**
   * Main class for I/O operations handling.
   */
  public void run(CommandController commandController) {
    Scanner scanner = new Scanner(System.in);

    while (true) {
      System.out.println("Welcome to the Calendar Application!");
      System.out.println("Select mode: 'interactive', 'headless', 'gui' or 'exit'");
      System.out.print("> ");
      String modeChoice = scanner.nextLine().trim().toLowerCase();

      if (modeChoice.equals("exit")) {
        System.out.println("Exiting the application. Goodbye!");
        break;
      }else if (modeChoice.equals("gui")) {
        System.out.println("Launching GUI mode. Close the GUI window to return to the main menu.");
        try {
          ICalendarManager model = commandController.getCalendarManager();
          SwingController swingController = new SwingController(model);

          final SwingUI[] guiHolder = new SwingUI[1];
          SwingUtilities.invokeAndWait(() -> {
            guiHolder[0] = SwingUI.createAndShowGUI(swingController);
          });
          guiHolder[0].waitForClose();
        } catch (Exception e) {
          System.out.println("Error launching GUI: " + e.getMessage());
        }
      }
      else if (modeChoice.equals("interactive")) {
        runInteractiveMode(scanner, commandController);
      } else if (modeChoice.equals("headless")) {
        System.out.println("Enter the path to the command file:");
        System.out.print("> ");
        String filePath = scanner.nextLine().trim();
        runHeadlessMode(filePath, commandController);
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
  private void runInteractiveMode(Scanner scanner, CommandController commandController) {
    //CommandController COMMAND_CONTROLLER=commandController;

    System.out.println("Interactive mode. Type 'mode' to return to mode selection.");
    while (true) {
      System.out.print("> ");
      String input = scanner.nextLine().trim();

      if (input.equalsIgnoreCase("mode")) {
        System.out.println("Returning to mode selection.");
        return;
      }


      try {
        commandController.parseCommand(input);
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
  private void runHeadlessMode(String filename, CommandController commandController) {
    //CommandController COMMAND_CONTROLLER=commandController;

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
          commandController.parseCommand(command);
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