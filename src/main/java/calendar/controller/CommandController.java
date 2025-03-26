package calendar.controller;

import java.time.LocalTime;
import java.time.LocalDateTime;
import java.time.ZoneId;

import calendar.manager.CalendarManager;
import calendar.manager.ICalendarManager;
import calendar.model.Calendar;
import calendar.model.ICalendar;
import calendar.utils.DateTimeUtils;
import calendar.view.Interpreter;

/**
 * The class for managing inputs commands to actual calendar operations.
 */
public class CommandController {
  private ICalendarManager calendarManager;
  private DateTimeUtils dateTimeUtils;
  private Interpreter interpreter;

  /**
   * Constructs a CommandController with dependencies injected.
   */
  public CommandController(ICalendarManager calendarManager, Interpreter intrepreter) {
    this.calendarManager = calendarManager;
    this.dateTimeUtils = new DateTimeUtils();
    this.interpreter = intrepreter;
  }



  /**
   * Starts the interpreter for input handling.
   */
  public void start() {
    interpreter.run(this);
  }

  /**
   * Main function for mapping commands to its correct execution function.
   *
   * @param input the given command.
   * @throws IllegalArgumentException throws error if the command is invalid.
   */
  public void parseCommand(String input) throws IllegalArgumentException {
    if (input == null || input.trim().isEmpty()) {
      throw new IllegalArgumentException("Input command cannot be empty.");
    }

    String[] tokens = input.trim().split("\\s+");
    if (tokens.length < 2) {
      throw new IllegalArgumentException("Invalid command format.");
    }

    String commandType = tokens[0] + " " + tokens[1];

    switch (commandType) {
      case "create calendar":
        parseCreateCalendarCommand(tokens);
        break;
      case "edit calendar":
        parseEditCalendarCommand(tokens);
        break;
      case "use calendar":
        parseUseCalendarCommand(tokens);
        break;
      case "copy event":
        parseCopyEventCommand(tokens);
        break;
      case "copy events":
        parseCopyEventsCommand(tokens);
        break;
      case "create event":
        parseCreateEventCommand(tokens);
        break;
      case "edit event":
        parseEditEventCommand(tokens);
        break;
      case "edit events":
        parseEditEventsCommand(tokens);
        break;
      case "print events":
        parsePrintEventsCommand(tokens);
        break;
      case "export cal":
        parseExportCalCommand(tokens);
        break;
      case "show status":
        parseShowStatusCommand(tokens);
        break;
      default:
        throw new IllegalArgumentException("Unknown command: " + commandType);
    }
  }

  private void parseCreateCalendarCommand(String[] tokens) {
    int index = 3;
    String calendarName = tokens[index++];
    index++;
    ZoneId calendarTimeZone = dateTimeUtils.parseZoneId(tokens[index]);
    calendarManager.createCalendar(calendarName, calendarTimeZone);
  }

  private void parseUseCalendarCommand(String[] tokens) {
    int index = 3;
    String calendarName = tokens[index];
    calendarManager.useCalendar(calendarName);
  }

  private void parseEditCalendarCommand(String[] tokens) {
    int index = 3;
    String calendarName = tokens[index++];
    index++;
    String propertyName = tokens[index++];
    String newValue = tokens[index];
    calendarManager.editCalendarProperty(calendarName, propertyName, newValue);
  }

  private void parseCopyEventCommand(String[] tokens) {
    int index = 2;
    String eventName = tokens[index++];
    index++;
    LocalDateTime startDateTime = dateTimeUtils.parseDateTime(tokens[index++]);
    index++;
    String targetCalendarName = tokens[index++];
    index++;
    LocalDateTime targetStartDateTime = dateTimeUtils.parseDateTime(tokens[index]);
    calendarManager.copyCalendarEvent(eventName, startDateTime, targetCalendarName, targetStartDateTime);

  }

  private void parseCopyEventsCommand(String[] tokens) {
    int index = 2;
    LocalDateTime startDateTime;
    LocalDateTime endDateTime;
    if (tokens[index].equals("on")) {
      index++;
      startDateTime = dateTimeUtils.parseDateToDateTime(tokens[index++]);
      endDateTime = startDateTime.toLocalDate().atTime(LocalTime.of(23, 59));
    }
    else if (tokens[index].equals("between")) {
      index++;
      startDateTime = dateTimeUtils.parseDateToDateTime(tokens[index++]);
      index++;
      endDateTime = dateTimeUtils.parseDateToDateTime(tokens[index++]).toLocalDate().atTime(LocalTime.of(23, 59));
    }
    else {
      throw new IllegalArgumentException("Unknown command: " + tokens[index]);
    }
    index++;
    String targetCalendarName = tokens[index++];
    index++;
    LocalDateTime targetStartDateTime = dateTimeUtils.parseDateToDateTime((tokens[index]));
    calendarManager.copyCalendarEvents(startDateTime, endDateTime, targetCalendarName, targetStartDateTime);

  }

  /**
   * Input mapping functions for create event commands.
   *
   * @param tokens the given input parameters.
   */
  private void parseCreateEventCommand(String[] tokens) {
    ICalendar calendar = calendarManager.getActiveCalendar();
    int index = 2;
    boolean autoDecline = false;
    String eventName;
    LocalDateTime startDateTime;
    LocalDateTime endDateTime;
    String recurringDays;
    int occurrences = 0;
    LocalDateTime endRecurringDateTime = null;

    if (tokens[index].equals("--autoDecline")) {
      autoDecline = true;
      index++;
    }

    eventName = tokens[index++];

    if (tokens[index].equals("from")) {
      // Handle "create event from ... to ..."
      index++;
      startDateTime =dateTimeUtils.parseDateTime(tokens[index++]);
      if (!tokens[index].equals("to")) {
        throw new IllegalArgumentException("Expected 'to' after start date/time.");
      }
      index++;
      endDateTime =dateTimeUtils.parseDateTime(tokens[index++]);
    } else if (tokens[index].equals("on")) {
      // Handle "create event on ..."
      index++;
      String startDateTimeString = tokens[index++];
      try {
        startDateTime =dateTimeUtils.parseDateTime(startDateTimeString);
      } catch (IllegalArgumentException e) {
        startDateTime =dateTimeUtils.parseDateToDateTime(startDateTimeString);
      }
      endDateTime = startDateTime.toLocalDate().atTime(LocalTime.of(23, 59));
    } else {
      throw new IllegalArgumentException("Expected 'from' or 'on' after event name.");
    }

    // Handle recurring events
    if (index < tokens.length && tokens[index].equals("repeats")) {
      index++;
      recurringDays = tokens[index++];
      if (index >= tokens.length || (!tokens[index].equals("for") &&
              !tokens[index].equals("until"))) {
        throw new IllegalArgumentException("Expected 'for' or 'until' after weekdays.");
      }
      if (tokens[index].equals("for")) {
        index++;
        occurrences = Integer.parseInt(tokens[index++]);
      } else {
        index++;
        String endDateTimeString = tokens[index++];
        try {
          endRecurringDateTime =dateTimeUtils.parseDateTime(endDateTimeString);
        } catch (IllegalArgumentException e) {
          endRecurringDateTime =dateTimeUtils.parseDateToDateTime(endDateTimeString);
          endRecurringDateTime = endRecurringDateTime.toLocalDate().atTime(LocalTime.of(23, 59));
        }
      }
      calendar.setAutoDeclineConflicts(autoDecline);
      calendar.addRecurringEvents(eventName, "", startDateTime, endDateTime,
              endRecurringDateTime, recurringDays, occurrences);
    } else {
      calendar.setAutoDeclineConflicts(autoDecline);
      calendar.addEvent(eventName, "", startDateTime, endDateTime);
    }
  }


  /**
   * Input mapping functions for edit event commands.
   *
   * @param tokens the given input parameters.
   */
  private void parseEditEventCommand(String[] tokens) {
    ICalendar calendar = calendarManager.getActiveCalendar();
    int index = 2;
    String propertyName = tokens[index++];
    String eventName = tokens[index++];
    LocalDateTime startDateTime;
    LocalDateTime endDateTime;

    if (tokens[index].equals("from")) {
      index++;
      startDateTime =dateTimeUtils.parseDateTime(tokens[index++]);
      if (!tokens[index].equals("to")) {
        throw new IllegalArgumentException("Expected 'to' after start date/time.");
      }
      index++;
      endDateTime =dateTimeUtils.parseDateTime(tokens[index++]);
    } else {
      throw new IllegalArgumentException("Expected 'from' after event name.");
    }

    if (!tokens[index].equals("with")) {
      throw new IllegalArgumentException("Expected 'with' after end date/time.");
    }
    index++;
    String newValue = tokens[index++];
    calendar.editEventSingle(eventName, startDateTime, endDateTime, propertyName, newValue);
  }

  /**
   * Input mapping functions for edit recurring event commands.
   *
   * @param tokens the given input parameters.
   */
  private void parseEditEventsCommand(String[] tokens) {
    ICalendar calendar = calendarManager.getActiveCalendar();
    int index = 2;
    String propertyName = tokens[index++];
    String eventName = tokens[index++];
    LocalDateTime startDateTime = null;
    String newValue = "";

    if (tokens[index].equals("from")) {
      index++;
      startDateTime =dateTimeUtils.parseDateTime(tokens[index++]);
      if (!tokens[index].equals("with")) {
        throw new IllegalArgumentException("Expected 'with' after start date/time.");
      }
      index++;
    }

    newValue = tokens[index++];
    calendar.editEventRecurring(eventName, startDateTime, propertyName, newValue);
  }

  /**
   * Input mapping functions for print commands.
   *
   * @param tokens the given input parameters.
   */
  private void parsePrintEventsCommand(String[] tokens) {
    ICalendar calendar = calendarManager.getActiveCalendar();
    int index = 2;
    LocalDateTime startDateTime;
    LocalDateTime endDateTime = null;

    if (tokens[index].equals("on")) {
      index++;
      startDateTime =dateTimeUtils.parseDateToDateTime(tokens[index++]);
    } else if (tokens[index].equals("from")) {
      index++;
      startDateTime =dateTimeUtils.parseDateTime(tokens[index++]);
      if (!tokens[index].equals("to")) {
        throw new IllegalArgumentException("Expected 'to' after start date/time.");
      }
      index++;
      endDateTime =dateTimeUtils.parseDateTime(tokens[index++]);
    } else {
      throw new IllegalArgumentException("Expected 'on' or 'from' after 'print events'.");
    }

    calendar.printEvents(startDateTime, endDateTime);
  }

  /**
   * Input mapping functions for export commands.
   *
   * @param tokens the given input parameters.
   */
  private void parseExportCalCommand(String[] tokens) {
    ICalendar calendar = calendarManager.getActiveCalendar();
    String fileName = tokens[2];
    calendar.exportCSV(fileName);
  }

  /**
   * Input mapping functions for show status commands.
   *
   * @param tokens the given input parameters.
   */
  private void parseShowStatusCommand(String[] tokens) {
    ICalendar calendar = calendarManager.getActiveCalendar();
    LocalDateTime date =dateTimeUtils.parseDateTime((tokens[3]));
    calendar.isBusy(date);
  }
}