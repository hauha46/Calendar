package calendar;

import calendar.controller.CommandController;
import calendar.manager.CalendarManager;
import calendar.view.Interpreter;

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
 *   <li>Model: {@link calendar.manager.CalendarManager} - Manages the data and business logic</li>
 *   <li>View: {@link calendar.view.Interpreter} - Handles user input and output</li>
 *   <li>Controller: {@link calendar.controller.CommandController} -
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
   * <p>The application supports two operation modes:</p>
   * <ul>
   *   <li>Interactive Mode: For manual command input</li>
   *   <li>Headless Mode: For processing commands from a text file</li>
   * </ul>
   *
   * @param args Command line arguments (not used)
   */
  public static void main(String[] args) {
    // Initialize the View component
    Interpreter view = new Interpreter();
    // Initialize the Model component
    CalendarManager model = new CalendarManager();
    // Initialize the Controller component with the Model and View
    CommandController controller = new CommandController(model, view);
    // Start the application
    controller.start();
  }
}