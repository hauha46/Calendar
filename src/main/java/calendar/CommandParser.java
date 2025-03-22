package calendar;

import java.time.Duration;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The class for managing inputs commands to actual calendar operations.
 */
public class CommandParser {
  private Calendar calendar;
  private Map<String, Calendar> calendarMap;
  private static final DateTimeFormatter dateTimeFormatter =
          DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

  /**
   * Construct a Command Parser with a new instance of calendar.
   */
  public CommandParser() {
    calendarMap = new HashMap<>();
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
    int index = 2;
    String calendarName = tokens[index++];
    index++;
    ZoneId calendarTimeZone = parseZoneId(tokens[index++]);

    if (calendarMap.containsKey(calendarName)) {
      throw new IllegalArgumentException("Duplicate calendar name: " + calendarName);
    }

    Calendar newCalendar = new Calendar(calendarTimeZone);
    calendarMap.put(calendarName, newCalendar);
  }

  private void parseUseCalendarCommand(String[] tokens) {
    int index = 3;
    String calendarName = tokens[index];

    if (!calendarMap.containsKey(calendarName)) {
      throw new IllegalArgumentException("Calendar name does not exist " + calendarName);
    }

    this.calendar = calendarMap.get(calendarName);
  }

  private void parseEditCalendarCommand(String[] tokens) {
    int index = 3;
    String calendarName = tokens[index++];
    index++;
    String propertyName = tokens[index++];
    String newValue = tokens[index++];

    switch (propertyName) {
      case "name": {
        Calendar calendarInstance = calendarMap.get(calendarName);
        calendarMap.put(propertyName, calendarInstance);
        break;
      }
      case "timezone":{
        this.calendar.setTimeZone(parseZoneId(newValue));
        break;
      }
      default: {
        throw new IllegalArgumentException("Unknown property: " + propertyName);
      }
    }
  }

  private void parseCopyEventCommand(String[] tokens) {
    int index = 2;
    String eventName = tokens[index++];
    index++;
    LocalDateTime startDateTime = parseDateTime(tokens[index++]);
    index++;
    String targetCalendarName = tokens[index++];
    index++;
    LocalDateTime targetStartDateTime = parseDateTime(tokens[index++]);
    if (!calendarMap.containsKey(targetCalendarName)) {
      throw new IllegalArgumentException("Calendar name does not exist: " + targetCalendarName);
    }
    LocalDateTime endDateTime = startDateTime.toLocalDate().atTime(LocalTime.of(23, 59));;
    List<EventInterface> foundEvents = calendar.searchEvents(eventName, startDateTime, endDateTime);

    if (foundEvents.isEmpty()) {
      throw new IllegalArgumentException("No events found for " + eventName);
    }

    EventInterface targetEvent = foundEvents.get(0);
    Duration duration = Duration.between(targetEvent.getStartTime(), targetEvent.getEndTime());
    Calendar targetCalendarInstance = calendarMap.get(targetCalendarName);
    targetCalendarInstance.addEvent(targetEvent.getSubject(), targetEvent.getDescription(), targetStartDateTime, targetStartDateTime.plus(duration));
  }

  private void parseCopyEventsCommand(String[] tokens) {
    int index = 2;
    LocalDateTime startDateTime;
    LocalDateTime endDateTime;
    if (tokens[index++].equals("on")) {
      startDateTime = parseDateToDateTime(tokens[index++]);
      endDateTime = startDateTime.toLocalDate().atTime(LocalTime.of(23, 59));
    }
    else if (tokens[index++].equals("between")) {
      startDateTime = parseDateToDateTime(tokens[index++]);
      index++;
      endDateTime = parseDateToDateTime(tokens[index++]).toLocalDate().atTime(LocalTime.of(23, 59));
    }
    else {
      throw new IllegalArgumentException("Unknown command: " + tokens[index]);
    }

    index++;
    String targetCalendarName = tokens[index++];
    if (!calendarMap.containsKey(targetCalendarName)) {
      throw new IllegalArgumentException("Calendar name does not exist: " + targetCalendarName);
    }
    index++;
    LocalDateTime targetStartDateTime = parseDateToDateTime(tokens[index]);
    List<EventInterface> foundEvents = calendar.searchEvents(null, startDateTime, endDateTime);
    Calendar targetCalendarInstance = calendarMap.get(targetCalendarName);
    LocalDateTime eventInitialDateTime = startDateTime.toLocalDate().atStartOfDay();
    for (EventInterface event : foundEvents) {
      Duration duratiomFromStartTime = Duration.between(eventInitialDateTime, event.getStartTime());
      Duration duratiomFromEndTime = Duration.between(eventInitialDateTime, event.getEndTime());

      // Determine logic for selecting start time
      targetCalendarInstance.addEvent(event.getSubject(), event.getDescription(), targetStartDateTime.plus(duratiomFromStartTime), targetStartDateTime.plus(duratiomFromEndTime));
    }

  }

  /**
   * Input mapping functions for create event commands.
   *
   * @param tokens the given input parameters.
   */
  private void parseCreateEventCommand(String[] tokens) {
    if (calendar == null) {
      throw new IllegalArgumentException("Please specify a calendar first.");
    }
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
      startDateTime = parseDateTime(tokens[index++]);
      if (!tokens[index].equals("to")) {
        throw new IllegalArgumentException("Expected 'to' after start date/time.");
      }
      index++;
      endDateTime = parseDateTime(tokens[index++]);
    } else if (tokens[index].equals("on")) {
      // Handle "create event on ..."
      index++;
      String startDateTimeString = tokens[index++];
      try {
        startDateTime = parseDateTime(startDateTimeString);
      } catch (IllegalArgumentException e) {
        startDateTime = parseDateToDateTime(startDateTimeString);
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
          endRecurringDateTime = parseDateTime(endDateTimeString);
        } catch (IllegalArgumentException e) {
          endRecurringDateTime = parseDateToDateTime(endDateTimeString);
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
    if (calendar == null) {
      throw new IllegalArgumentException("Please specify a calendar first.");
    }
    int index = 2;
    String propertyName = tokens[index++];
    String eventName = tokens[index++];
    LocalDateTime startDateTime;
    LocalDateTime endDateTime;

    if (tokens[index].equals("from")) {
      index++;
      startDateTime = parseDateTime(tokens[index++]);
      if (!tokens[index].equals("to")) {
        throw new IllegalArgumentException("Expected 'to' after start date/time.");
      }
      index++;
      endDateTime = parseDateTime(tokens[index++]);
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
    if (calendar == null) {
      throw new IllegalArgumentException("Please specify a calendar first.");
    }
    int index = 2;
    String propertyName = tokens[index++];
    String eventName = tokens[index++];
    LocalDateTime startDateTime = null;
    String newValue = "";

    if (tokens[index].equals("from")) {
      index++;
      startDateTime = parseDateTime(tokens[index++]);
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
    if (calendar == null) {
      throw new IllegalArgumentException("Please specify a calendar first.");
    }
    int index = 2;
    LocalDateTime startDateTime;
    LocalDateTime endDateTime = null;

    if (tokens[index].equals("on")) {
      index++;
      startDateTime = parseDateToDateTime(tokens[index++]);
    } else if (tokens[index].equals("from")) {
      index++;
      startDateTime = parseDateTime(tokens[index++]);
      if (!tokens[index].equals("to")) {
        throw new IllegalArgumentException("Expected 'to' after start date/time.");
      }
      index++;
      endDateTime = parseDateTime(tokens[index++]);
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
    if (calendar == null) {
      throw new IllegalArgumentException("Please specify a calendar first.");
    }
    String fileName = tokens[2];
    calendar.exportCSV(fileName);
  }

  /**
   * Input mapping functions for show status commands.
   *
   * @param tokens the given input parameters.
   */
  private void parseShowStatusCommand(String[] tokens) {
    if (calendar == null) {
      throw new IllegalArgumentException("Please specify a calendar first.");
    }
    LocalDateTime date = parseDateTime((tokens[3]));
    calendar.isBusy(date);
  }

  // Helper functions
  /**
   * Parse ZoneId object from timezone string input.
   *
   * @param timezoneStr the given timezone string.
   * @return parsed ZoneId object.
   * @throws IllegalArgumentException throws error if the string does not have appropriate format.
   */
  private ZoneId parseZoneId(String timezoneStr) {
    try {
      return ZoneId.of(timezoneStr);
    } catch (DateTimeParseException e) {
      throw new IllegalArgumentException("Invalid timezone format: " + timezoneStr);
    }
  }

  /**
   * Parse LocalDateTime object from date time string input.
   *
   * @param dateTimeStr the given date time string.
   * @return parsed LocalDateTime object.
   * @throws IllegalArgumentException throws error if the string does not have appropriate format.
   */
  private LocalDateTime parseDateTime(String dateTimeStr) {
    try {
      return LocalDateTime.parse(dateTimeStr);
    } catch (DateTimeParseException e) {
      throw new IllegalArgumentException("Invalid date/time format: " + dateTimeStr);
    }
  }

  /**
   * Parse LocalDateTime object from date string input.
   *
   * @param dateStr the given date string.
   * @return parsed LocalDateTime object.
   * @throws IllegalArgumentException throws error if the string does not have appropriate format.
   */
  private LocalDateTime parseDateToDateTime(String dateStr) {
    try {
      return LocalDateTime.parse(dateStr + "T00:00", dateTimeFormatter);
    } catch (DateTimeParseException e) {
      throw new IllegalArgumentException("Invalid date format: " + dateStr);
    }
  }
}