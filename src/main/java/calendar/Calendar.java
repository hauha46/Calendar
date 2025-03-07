package calendar;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Calendar {
  private Map<String, Set<Event>> calendar;

  private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
  private boolean autoDeclineConflicts = false;

  public Calendar() {
    calendar = new TreeMap<>(Comparator.comparing((String date) -> LocalDate.parse(date, dateFormatter)));
  }

  public void setAutoDeclineConflicts(boolean autoDeclineConflicts) {
    this.autoDeclineConflicts = autoDeclineConflicts;
  }

  public void editEventSingle(String subject, LocalDateTime startTime, LocalDateTime endTime, String property, String newValue) {
    // Handle edge case multiple date spanning event
    Event foundEvent = searchEvent(subject, startTime, endTime);
    if (foundEvent != null) {
      removeEvent(foundEvent);
      String newSubject = foundEvent.getSubject();
      LocalDateTime newStartTime = foundEvent.getStartTime();
      LocalDateTime newEndTime = foundEvent.getEndTime();
      String newDescription = foundEvent.getDescription();

      switch (property) {
        case "name": {
          newSubject = newValue;
        }
        case "startTime": {
          newStartTime = LocalDateTime.parse(newValue);
        }
        case "endTime": {
          newEndTime = LocalDateTime.parse(newValue);
        }
        case "description": {
          newDescription = newValue;
        }
      }

      try {
        addEvent(newSubject, newDescription, newStartTime, newEndTime);
      }
      catch (Exception e) {
        addEvent(foundEvent.getSubject(), foundEvent.getDescription(), foundEvent.getStartTime(), foundEvent.getEndTime());
      }
    }
  }

  public void editEventRecurring(String subject, LocalDateTime startTime, String property, String newValue) {
    List<Event> events = searchEvents(subject, startTime);
    if (!events.isEmpty()) {
      for (Event event : events) {
        removeEvent(event);
      }
      Event foundEvent = events.get(0);
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
        }
        case "startTime": {
          newStartTime = LocalDateTime.parse(newValue);
        }
        case "endTime": {
          newEndTime = LocalDateTime.parse(newValue);
        }
        case "description": {
          newDescription = newValue;
        }
        case "endRecurring": {
          newEndRecurring = LocalDateTime.parse(newValue);
        }
        case "recurringDays": {
          newRecurringDays = newValue;
        }
        case "occurrences": {
          newOccurrences = Integer.parseInt(newValue);
        }
      }
      try {
        addRecurringEvents(newSubject, newDescription, newStartTime, newEndTime, newEndRecurring, newRecurringDays, newOccurrences);
      }
      catch (Exception e) {
        addRecurringEvents(foundEvent.getSubject(), foundEvent.getDescription(), foundEvent.getStartTime(), foundEvent.getEndTime(), foundEvent.getEndRecurring(), foundEvent.getRecurringDays(), foundEvent.getOccurrences());
      }
    }
  }

  private void removeEvent(Event event) {
    String date = dateFormatter.format(event.getStartTime());
    Set<Event> events = calendar.get(date);
    events.remove(event);
  }

  private Event searchEvent(String subject, LocalDateTime startTime, LocalDateTime endTime) {
    for (Map.Entry<String, Set<Event>> entry : calendar.entrySet()) {
      Set<Event> events = entry.getValue();
      for (Event event : events) {
        if (event.getSubject().equals(subject) && event.getStartTime().equals(startTime) && event.getEndTime().equals(endTime)) {
          return event;
        }
      }
    }
    return null;
  }

  private List<Event> searchEvents(String subject, LocalDateTime startTime) {
    List<Event> foundEvents = new ArrayList<>();
    for (Map.Entry<String, Set<Event>> entry : calendar.entrySet()) {
      Set<Event> events = entry.getValue();
      for (Event event : events) {
        LocalDate currentDate = event.getStartTime().toLocalDate();
        LocalDate searchDate = startTime.toLocalDate();
        LocalTime currentTime = event.getStartTime().toLocalTime();
        LocalTime searchTime = startTime.toLocalTime();
        if (event.getSubject().equals(subject)) {
          if (startTime != null) {
            if ((currentDate.equals(searchDate) || currentDate.isAfter(searchDate)) && currentTime.equals(searchTime)) {
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

  public void addEvent(String subject, String description, LocalDateTime startTime, LocalDateTime endTime) {
    Event event = new Event(subject, description, startTime, endTime);
    String date = dateFormatter.format(event.getStartTime());
    if (hasConflict(event) && autoDeclineConflicts) {
      throw new IllegalArgumentException("Conflicted event and auto-decline is enabled.");
    }
    // handle special case for multiple day, a loop for each day from start to end
    calendar.computeIfAbsent(date, k -> new HashSet<>()).add(event);
  }

  public void addRecurringEvents(String subject, String description, LocalDateTime startTime, LocalDateTime endTime,
                                 LocalDateTime endRecurring, String recurringDays, int occurrences) {
    List<Event> events = generateRecurringEvents(subject, description, startTime, endTime, endRecurring, recurringDays, occurrences);
    if (hasAnyConflict(events)) {
      throw new IllegalArgumentException("Recurring event series conflicts with existing events.");
    }

    for (Event event : events) {
      String date = dateFormatter.format(event.getStartTime());
      calendar.computeIfAbsent(date, k -> new HashSet<>()).add(event);
    }
  }

  private boolean hasConflict(Event event) {
    String date = dateFormatter.format(event.getStartTime());
    if (calendar.containsKey(date)) {
      for (Event existingEvent : calendar.get(date)) {
        if (event.isConflicted(existingEvent)) {
          return true;
        }
      }
    }
    return false;
  }

  private boolean hasAnyConflict(List<Event> events) {
    for (Event event : events) {
      if (hasConflict(event)) {
        return true;
      }
    }
    return false;
  }

  private List<Event> generateRecurringEvents(String subject, String description, LocalDateTime startTime, LocalDateTime endTime,
                                              LocalDateTime endRecurring, String recurringDays, int occurrences) {
    List<Event> result = new ArrayList<>();
    LocalDateTime currentStartTime = startTime;
    LocalDateTime currentEndTime = endTime;
    int count = 0;

    while (currentStartTime != null && (occurrences == 0 || count < occurrences)) {
      if (currentStartTime.isAfter(endRecurring)) {
        break;
      }

      if (recurringDays.contains(currentStartTime.getDayOfWeek().toString())) { // Update for new enum to parse day of the week to characters
        result.add(new Event(subject, description, currentStartTime, currentEndTime, endRecurring, recurringDays, occurrences));
        count++;
      }

      currentStartTime = currentStartTime.plusDays(1);
      currentEndTime = currentEndTime.plusDays(1);
      if (occurrences > 0) {
        occurrences--;
      }
    }

    return result;
  }
}