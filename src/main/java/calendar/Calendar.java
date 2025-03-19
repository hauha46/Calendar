//problem is that the calendar inoration of events is stored here
//  would be needing a new calendar storage class to store the calendar objects.

// the problem here is the heirarchy of commands that are being given, as create and use calendar are comming first.

// create a class which creates the calendar object and give it back for usage.
import java.io.FileWriter;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
// create the calendar constructor to include the name and timezone.
//parsing the date to the currnt timezone.
/**
 * The class for managing events in a calendar. The main attribute is calendar, which stores events
 * for each date.
 */
public class Calendar {

  private String name;
  private ZoneId timeZone;
  private Map<LocalDate, Set<EventInterface>> calendar;
  private final DateTimeFormatter DATE_TIME_FORMATTER =
          DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
  private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
  private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");
  private boolean autoDeclineConflicts = false;

  /**
   * Construct a calendar with a new instance of TreeMap.
   */
  public Calendar(String name,ZoneId timeZone) {

    //name
    //timezone
    this.name=name;
    this.timeZone=timeZone;
    calendar = new TreeMap<>(Comparator.naturalOrder());
  }

  /**
   * Set the autoDecline flag with a boolean value.
   *
   * @param autoDeclineConflicts the given boolean autoDecline value.
   */
  public void setAutoDeclineConflicts(boolean autoDeclineConflicts) {
    this.autoDeclineConflicts = autoDeclineConflicts;
  }

  /**
   * Add an event into calendar with its respective date. Handling both single event and multiple
   * spanning days event.
   *
   * @param subject   the given subject.
   * @param startTime the given start time.
   * @param endTime   the given end time.
   * @throws IllegalArgumentException throws error if the input is invalid.
   */
  public void addEvent(String subject, String description, LocalDateTime startTime,
                       LocalDateTime endTime) throws IllegalArgumentException {
    List<EventInterface> events = new ArrayList<>();
    LocalDate startDate = startTime.toLocalDate();
    LocalDate endDate = endTime.toLocalDate();

    if (startDate.isAfter(endDate)) {
      throw new IllegalArgumentException("Start date cannot be after end date");
    } else if (endDate.isEqual(startDate)) {
      events.add(new OneTimeEvent(subject, description, startTime, endTime));
    } else {
      LocalDateTime currentStartTime = startTime;
      LocalDateTime currentEndTime = startTime.toLocalDate().atTime(LocalTime.of(23, 59));
      while (currentStartTime.isBefore(endTime) || currentStartTime.isEqual(endTime)) {
        currentStartTime = currentStartTime.toLocalDate().equals(startDate) ? startTime :
                currentStartTime;
        currentEndTime = currentEndTime.toLocalDate().equals(endDate) ? endTime : currentEndTime;
        events.add(new OneTimeEvent(subject, description, currentStartTime, currentEndTime));
        currentStartTime = currentStartTime.toLocalDate().atStartOfDay().plusDays(1);
        currentEndTime = currentEndTime.plusDays(1);
      }
    }

    for (EventInterface event : events) {
      if (hasConflict(event) && autoDeclineConflicts) {
        throw new IllegalArgumentException("Conflicted event and auto-decline is enabled.");
      }
    }

    for (EventInterface event : events) {
      calendar.computeIfAbsent(
              event.getStartTime().toLocalDate(), k -> new HashSet<>()).add(event);
    }
  }

  /**
   * Add a recurring events by splitting it into multiple single events, based on the provided
   * input.
   *
   * @param subject       the given subject.
   * @param description   the given description.
   * @param startTime     the given start time.
   * @param endTime       the given end time.
   * @param endRecurring  the given end recurring date time.
   * @param recurringDays the given recurring days.
   * @param occurrences   the given occurrences.
   * @throws IllegalArgumentException throws error if the input is invalid
   */
  public void addRecurringEvents(
          String subject, String description, LocalDateTime startTime, LocalDateTime endTime,
          LocalDateTime endRecurring, String recurringDays, int occurrences)
          throws IllegalArgumentException {
    List<EventInterface> events = generateRecurringEvents(subject, description, startTime, endTime,
            endRecurring, recurringDays, occurrences);
    if (hasAnyConflict(events)) {
      throw new IllegalArgumentException("Recurring event series conflicts with existing events.");
    }

    for (EventInterface event : events) {
      calendar.computeIfAbsent(
              event.getStartTime().toLocalDate(), k -> new HashSet<>()).add(event);
    }
  }

  /**
   * Edit an existing event on the calendar based on the given input. Also handle the multiple
   * spanning days event case.
   *
   * @param subject   the given subject.
   * @param startTime the given start time.
   * @param endTime   the given end time.
   * @param property  the name of the target property.
   * @param newValue  the new value for the target property.
   * @throws IllegalArgumentException throws error if the input is invalid
   */
  public void editEventSingle(String subject, LocalDateTime startTime, LocalDateTime endTime,
                              String property, String newValue) throws IllegalArgumentException {
    List<EventInterface> events = new ArrayList<>();
    LocalDate startDate = startTime.toLocalDate();
    LocalDate endDate = endTime.toLocalDate();

    if (startDate.isAfter(endDate)) {
      throw new IllegalArgumentException("Start date cannot be after end date");
    } else if (startDate.isEqual(endDate)) {
      EventInterface foundEvent = searchEvent(subject, startTime, endTime);
      if (foundEvent != null) {
        events.add(foundEvent);
      }
    } else {
      LocalDateTime currentStartTime = startTime;
      LocalDateTime currentEndTime = startTime.toLocalDate().atTime(LocalTime.of(23, 59));
      while (currentStartTime.isBefore(endTime)) {
        currentStartTime = currentStartTime.toLocalDate().equals(startDate) ? startTime :
                currentStartTime;
        currentEndTime = currentEndTime.toLocalDate().equals(endDate) ? endTime : currentEndTime;
        EventInterface foundEvent = searchEvent(subject, currentStartTime, currentEndTime);
        if (foundEvent != null) {
          events.add(foundEvent);
        }
        currentStartTime = currentStartTime.toLocalDate().atStartOfDay().plusDays(1);
        currentEndTime = currentEndTime.plusDays(1);
      }
    }

    if (events.size() > 0) {
      for (EventInterface event : events) {
        removeEvent(event);
      }

      String newSubject = subject;
      String newDescription = events.get(0).getDescription();
      LocalDateTime newStartTime = startTime;
      LocalDateTime newEndTime = endTime;

      switch (property) {
        case "name": {
          newSubject = newValue;
          break;
        }
        case "startTime": {
          newStartTime = LocalDateTime.parse(newValue);
          break;
        }
        case "endTime": {
          newEndTime = LocalDateTime.parse(newValue);
          break;
        }
        case "description": {
          newDescription = newValue;
          break;
        }
        default: {
          throw new IllegalArgumentException("Unsupported property");
        }
      }

      try {
        addEvent(newSubject, newDescription, newStartTime, newEndTime);
      } catch (IllegalArgumentException e) {
        addEvent(subject, events.get(0).getDescription(), startTime, endTime);
      }
    }
  }


  /**
   * Edit an existing recurring event on the calendar based on the given input. Handle both cases
   * where either start time is provided or not.
   *
   * @param subject   the given subject.
   * @param startTime the given start time.
   * @param property  the name of the target property.
   * @param newValue  the new value for the target property.
   */
  public void editEventRecurring(String subject, LocalDateTime startTime, String property,
                                 String newValue) {
    List<EventInterface> events = searchEvents(subject, startTime);
    if (!events.isEmpty()) {
      for (EventInterface event : events) {
        removeEvent(event);
      }
      RecurringEvent foundEvent = (RecurringEvent) events.get(0);
      String newSubject = foundEvent.getSubject();
      LocalDateTime newStartTime = foundEvent.getStartTime();
      LocalDateTime newEndTime = foundEvent.getEndTime();
      String newDescription = foundEvent.getDescription();
      LocalDateTime newEndRecurring = foundEvent.getEndRecurring();
      String newRecurringDays = foundEvent.getRecurringDays();
      int newOccurrences = foundEvent.getOccurrences();

      switch (property) {
        case "name": {
          newSubject = newValue;
          break;
        }
        case "startTime": {
          newStartTime = LocalDateTime.parse(newValue);
          break;
        }
        case "endTime": {
          newEndTime = LocalDateTime.parse(newValue);
          break;
        }
        case "description": {
          newDescription = newValue;
          break;
        }
        case "endRecurring": {
          newEndRecurring = LocalDateTime.parse(newValue);
          break;
        }
        case "recurringDays": {
          newRecurringDays = newValue;
          break;
        }
        case "occurrences": {
          newOccurrences = Integer.parseInt(newValue);
          break;
        }
        default: {
          throw new IllegalArgumentException("Unsupported property");
        }
      }
      try {
        addRecurringEvents(newSubject, newDescription, newStartTime, newEndTime, newEndRecurring,
                newRecurringDays, newOccurrences);
      } catch (IllegalArgumentException e) {
        addRecurringEvents(foundEvent.getSubject(), foundEvent.getDescription(),
                foundEvent.getStartTime(), foundEvent.getEndTime(), foundEvent.getEndRecurring(),
                foundEvent.getRecurringDays(), foundEvent.getOccurrences());
      }
    }
  }

  /**
   * Print all events in the calendar from start time to end time. Handle both cases where
   * either end time is provided or not.
   *
   * @param startTime the given start time.
   * @param endTime   the given end time.
   */
  public void printEvents(LocalDateTime startTime, LocalDateTime endTime) {
    LocalDate currentDate = startTime.toLocalDate();
    LocalDate endDate = endTime != null ? endTime.toLocalDate() : currentDate;
    endTime = endTime != null ? endTime : currentDate.atTime(LocalTime.of(23, 59));
    while (currentDate.isEqual(endDate) || currentDate.isBefore(endDate)) {
      if (calendar.containsKey(currentDate) && !calendar.get(currentDate).isEmpty()) {
        System.out.println("Date: " + dateFormatter.format(currentDate));
        for (EventInterface event : calendar.get(currentDate)) {
          if ((event.getStartTime().isEqual(startTime) || event.getStartTime().isAfter(startTime))
                  && (event.getEndTime().isEqual(endTime) ||
                  event.getEndTime().isBefore(endTime))) {
            System.out.println("  -Subject :  " + event.getSubject());
            System.out.println("  -Description :  " + event.getDescription());
            System.out.println("  -Start Time :  " + event.getStartTime());
            System.out.println("  -End Time :  " + event.getEndTime().format(DATE_TIME_FORMATTER));
          }
        }
      }
      currentDate = currentDate.plusDays(1);
    }
  }

  /**
   * Export all the events in the current calendar into a csv file for Google calendar import.
   *
   * @param fileName the given file name.
   */
  public void exportCSV(String fileName) {
    try (FileWriter writer = new FileWriter(fileName)) {
      writer.write("Subject,Start Date,Start Time,End Date,End Time,Description\n");

      for (Map.Entry<LocalDate, Set<EventInterface>> entry : calendar.entrySet()) {
        for (EventInterface event : entry.getValue()) {
          String subject = escapeCSV(event.getSubject());
          String description = escapeCSV(event.getDescription());
          String startDate = event.getStartTime().format(dateFormatter);
          String startTime = event.getStartTime().format(timeFormatter);
          String endDate = event.getEndTime().format(dateFormatter);
          String endTime = event.getEndTime().format(timeFormatter);

          writer.write(String.format("%s,%s,%s,%s,%s,%s\n",
                  subject, startDate, startTime, endDate, endTime, description));
        }
      }
      System.out.println("Calendar exported successfully to " + fileName);
    } catch (IOException e) {
      System.err.println("Error exporting calendar to CSV: " + e.getMessage());
    }
  }

  /**
   * Print out the status based on a given date time, whether it's busy or available.
   *
   * @param dateTime the given date time.
   */
  public void isBusy(LocalDateTime dateTime) {
    String result = "available";
    LocalDate currentDate = dateTime.toLocalDate();
    for (EventInterface event : calendar.get(currentDate)) {
      if (event.getStartTime().isEqual(dateTime) || event.getStartTime().isBefore(dateTime)
              && event.getEndTime().isAfter(dateTime)) {
        result = "busy";
      }
    }
    System.out.println(result);
  }

  // Helper functions

  /**
   * Remove an event from the current calendar.
   *
   * @param event the given event.
   */
  private void removeEvent(EventInterface event) {
    if (event != null) {
      Set<EventInterface> events = calendar.get(event.getStartTime().toLocalDate());
      events.remove(event);
    }
  }

  /**
   * Search an event from the current calendar based on the given info.
   *
   * @param subject   the given subject.
   * @param startTime the given start time.
   * @param endTime   the given end time.
   * @return the found event.
   */
  private EventInterface searchEvent(
          String subject, LocalDateTime startTime, LocalDateTime endTime) {
    for (Map.Entry<LocalDate, Set<EventInterface>> entry : calendar.entrySet()) {
      Set<EventInterface> events = entry.getValue();
      for (EventInterface event : events) {
        if (event.getSubject().equals(subject) && event.getStartTime().equals(startTime)
                && event.getEndTime().equals(endTime)) {
          return event;
        }
      }
    }
    return null;
  }

  /**
   * Search events that belong to a recurring event based on the given info.
   *
   * @param subject   the given subject.
   * @param startTime the given start time.
   * @return the list of found events.
   */
  private List<EventInterface> searchEvents(String subject, LocalDateTime startTime) {
    List<EventInterface> foundEvents = new ArrayList<>();
    for (Map.Entry<LocalDate, Set<EventInterface>> entry : calendar.entrySet()) {
      Set<EventInterface> events = entry.getValue();
      for (EventInterface event : events) {
        if (event.getSubject().equals(subject)) {
          if (startTime != null) {
            LocalDate currentDate = event.getStartTime().toLocalDate();
            LocalDate searchDate = startTime.toLocalDate();
            LocalTime currentTime = event.getStartTime().toLocalTime();
            LocalTime searchTime = startTime.toLocalTime();
            if ((currentDate.equals(searchDate) || currentDate.isAfter(searchDate))
                    && currentTime.equals(searchTime)) {
              foundEvents.add(event);
            }
          } else {
            foundEvents.add(event);
          }
        }
      }
    }
    return foundEvents;
  }

  /**
   * Check if an event has any conflict with the current calendar.
   *
   * @param event the given event.
   * @return boolean value whether the event has any conflicts or not.
   */
  private boolean hasConflict(EventInterface event) {
    LocalDate date = event.getStartTime().toLocalDate();
    if (calendar.containsKey(date)) {
      for (EventInterface existingEvent : calendar.get(date)) {
        if (event.isConflicted(existingEvent)) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Check if a list of event has any conflict with the current calendar.
   *
   * @param events the given list of event.
   * @return boolean value whether the list has any conflicts or not.
   */
  private boolean hasAnyConflict(List<EventInterface> events) {
    for (EventInterface event : events) {
      if (hasConflict(event)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Generate single events from the recurring event input in order to put in the calendar.
   *
   * @param subject       the given subject.
   * @param description   the given description.
   * @param startTime     the given start time.
   * @param endTime       the given end time.
   * @param endRecurring  the given end recurring date time.
   * @param recurringDays the given recurring days.
   * @param occurrences   the given occurrences.
   * @return List of generated events.
   * @throws IllegalArgumentException throws error if the input is invalid.
   */
  private List<EventInterface> generateRecurringEvents(
          String subject, String description, LocalDateTime startTime, LocalDateTime endTime,
          LocalDateTime endRecurring, String recurringDays, int occurrences)
          throws IllegalArgumentException {
    List<EventInterface> result = new ArrayList<>();
    LocalDateTime currentStartTime = startTime;
    LocalDateTime currentEndTime = endTime;
    int count = 0;
    Set<DayOfWeek> recurringDaySet = new HashSet<>();
    for (char c : recurringDays.toCharArray()) {
      switch (c) {
        case 'M':
          recurringDaySet.add(DayOfWeek.MONDAY);
          break;
        case 'T':
          recurringDaySet.add(DayOfWeek.TUESDAY);
          break;
        case 'W':
          recurringDaySet.add(DayOfWeek.WEDNESDAY);
          break;
        case 'R':
          recurringDaySet.add(DayOfWeek.THURSDAY);
          break;
        case 'F':
          recurringDaySet.add(DayOfWeek.FRIDAY);
          break;
        case 'S':
          recurringDaySet.add(DayOfWeek.SATURDAY);
          break;
        case 'U':
          recurringDaySet.add(DayOfWeek.SUNDAY);
          break;
        default:
          throw new IllegalArgumentException("Invalid day character: " + c);
      }
    }

    while (currentStartTime != null && (occurrences == 0 || count < occurrences)) {
      if (endRecurring != null && currentStartTime.isAfter(endRecurring)) {
        break;
      }

      if (recurringDaySet.contains(currentStartTime.getDayOfWeek())) {
        result.add(new RecurringEvent(subject, description, currentStartTime,
                currentEndTime, endRecurring, recurringDays, occurrences));
        count++;
      }

      currentStartTime = currentStartTime.plusDays(1);
      currentEndTime = currentEndTime.plusDays(1);
    }

    return result;
  }

  /**
   * Safeguard function for subject and description in case the format is incorrect for csv export.
   *
   * @param field the given field value.
   * @return safe string for subject or description.
   */
  private String escapeCSV(String field) {
    if (field == null) {
      return "";
    }

    if (field.contains(",") || field.contains("\"") || field.contains("\n")) {
      field = field.replace("\"", "\"\"");
      return "\"" + field + "\"";
    }
    return field;
  }

}