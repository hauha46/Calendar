package calendar;

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
   * <p>The application supports two operation modes:</p>
   * <ul>
   *   <li>Interactive Mode: For manual command input</li>
   *   <li>Headless Mode: For processing commands from a text file</li>
   * </ul>
   *
   * @param args Command line arguments (not used)
   */
    public static void main(String[] args) {
      SwingUtilities.invokeLater(() -> {
        // Instantiate the model manager and controller.
        CalendarManager manager = new CalendarManager();
        SwingController controller = new SwingController(manager);

        // Create a default calendar.
        TimeZone timeZone = TimeZone.getDefault();
        controller.createCalendar("Default_Calendar", timeZone.getID());

        // Launch the UI with the injected controller.
        new SwingUI(controller);
      });
    }
  }
