package calendar;

import java.io.File;
import java.util.TimeZone;

import javax.swing.*;

import calendar.controller.CommandController;
import calendar.controller.SwingController;
import calendar.manager.CalendarManager;
import calendar.view.Interpreter;
import calendar.view.SwingUI;

/**
 * Main entry point for the Calendar Application.
 * 
 * <p>This application provides a text-based interface for managing multiple calendars
 * and their events. Key features include:</p>
 * <ul>
 *   <li>Support for multiple calendars with unique names</li>
 *   <li>Timezone management for each calendar</li>
 *   <li>Event creation, editing, and querying</li>
 *   <li>Support for single and recurring events</li>
 *   <li>Conflict detection and resolution</li>
 *   <li>Event copying functionality across calendars</li>
 *   <li>Calendar export to CSV format compatible with Google Calendar</li>
 * </ul>
 * 
 * <p>The application follows the Model-View-Controller (MVC) architectural pattern:</p>
 * <ul>
 *   <li>Model: {@link CalendarManager} - Manages the data and business logic</li>
 *   <li>View: {@link Interpreter} - Handles user input and output</li>
 *   <li>Controller: {@link CommandController} -
 *   Processes commands and coordinates
 *       interactions between the model and view</li>
 * </ul>
 */
public class Main {
  /**
   * The application entry point.
   * 
   * <p>Creates and initializes the MVC components (Model, View, Controller) 
   * and starts the application by launching the controller.</p>
   *
   * <p>The application supports three operation modes through command-line arguments:</p>
   * <ul>
   *   <li>GUI Mode (default): java -jar Program.jar</li>
   *   <li>Interactive Mode: java -jar Program.jar --mode interactive</li>
   *   <li>Headless Mode: java -jar Program.jar --mode headless path-of-script-file</li>
   * </ul>
   *
   * @param args Command line arguments (--mode [interactive|headless] [script-file-path])
   */
  public static void main(String[] args) {
    // Create the shared calendar manager (model)
    CalendarManager manager = new CalendarManager();
    
    // Parse command line arguments
    if (args.length == 0) {
      // No arguments - launch GUI mode
      launchGuiMode(manager);
    } else if (args.length >= 2 && args[0].equals("--mode")) {
      switch (args[1]) {
        case "interactive":
          if (args.length == 2) {
            // Interactive CLI mode
            launchInteractiveMode(manager);
          } else {
            displayInvalidArgsError();
          }
          break;
        case "headless":
          if (args.length == 3) {
            // Headless mode with script file
            String scriptFilePath = args[2];
            launchHeadlessMode(manager, scriptFilePath);
          } else {
            displayInvalidArgsError();
          }
          break;
        default:
          displayInvalidArgsError();
      }
    } else {
      displayInvalidArgsError();
    }
  }
  
  /**
   * Launches the application in GUI mode with the Swing interface.
   * 
   * @param manager The calendar manager instance to use
   */
  private static void launchGuiMode(CalendarManager manager) {
    SwingUtilities.invokeLater(() -> {
      // Instantiate the controller with the manager
      SwingController controller = new SwingController(manager);

      // Create a default calendar
      TimeZone timeZone = TimeZone.getDefault();
      controller.createCalendar("Default_Calendar", timeZone.getID());

      // Launch the UI with the injected controller
      SwingUI.createAndShowGUI(controller);
    });
  }
  
  /**
   * Launches the application in interactive command-line mode.
   * 
   * @param manager The calendar manager instance to use
   */
  private static void launchInteractiveMode(CalendarManager manager) {
    // Create the interpreter and controller
    Interpreter interpreter = new Interpreter();
    CommandController controller = new CommandController(manager, interpreter);
    
    // Start directly in interactive mode using the interpreter
    interpreter.startInteractiveMode(controller);
  }
  
  /**
   * Launches the application in headless mode, executing commands from a script file.
   * 
   * @param manager The calendar manager instance to use
   * @param scriptFilePath Path to the script file containing commands
   */
  private static void launchHeadlessMode(CalendarManager manager, String scriptFilePath) {
    File scriptFile = new File(scriptFilePath);
    if (!scriptFile.exists() || !scriptFile.isFile()) {
      System.err.println("Error: Script file not found: " + scriptFilePath);
      System.exit(1);
    }
    
    // Create the interpreter and controller
    Interpreter interpreter = new Interpreter();
    CommandController controller = new CommandController(manager, interpreter);
    
    // Execute the script file using the interpreter
    interpreter.executeScriptFile(scriptFilePath, controller);
  }
  
  /**
   * Displays an error message for invalid command-line arguments and exits.
   */
  private static void displayInvalidArgsError() {
    System.err.println("Error: Invalid command-line arguments");
    System.err.println("Usage:");
    System.err.println("  java -jar Program.jar                            # Launch GUI mode");
    System.err.println("  java -jar Program.jar --mode interactive         # Launch interactive CLI mode");
    System.err.println("  java -jar Program.jar --mode headless script.txt # Execute script file in headless mode");
    System.exit(1);
  }
}
