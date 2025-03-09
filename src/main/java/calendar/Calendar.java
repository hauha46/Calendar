package calendar;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Calendar {
  private Map<String, Set<EventInterface>> calendar;

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
    EventInterface foundEvent = searchEvent(subject, startTime, endTime);
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
    List<EventInterface> events = searchEvents(subject, startTime);
    if (!events.isEmpty()) {
      for (EventInterface event : events) {
        removeEvent(event);
      }
      RecurringEvent foundEvent = (RecurringEvent)events.get(0);
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

  private void removeEvent(EventInterface event) {
    String date = dateFormatter.format(event.getStartTime());
    Set<EventInterface> events = calendar.get(date);
    events.remove(event);
  }

  private EventInterface searchEvent(String subject, LocalDateTime startTime, LocalDateTime endTime) {
    for (Map.Entry<String, Set<EventInterface>> entry : calendar.entrySet()) {
      Set<EventInterface> events = entry.getValue();
      for (EventInterface event : events) {
        if (event.getSubject().equals(subject) && event.getStartTime().equals(startTime) && event.getEndTime().equals(endTime)) {
          return event;
        }
      }
    }
    return null;
  }

  private List<EventInterface> searchEvents(String subject, LocalDateTime startTime) {
    List<EventInterface> foundEvents = new ArrayList<>();
    for (Map.Entry<String, Set<EventInterface>> entry : calendar.entrySet()) {
      Set<EventInterface> events = entry.getValue();
      for (EventInterface event : events) {
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
    EventInterface event = new OneTimeEvent(subject, description, startTime, endTime);
    String date = dateFormatter.format(event.getStartTime());
    if (hasConflict(event) && autoDeclineConflicts) {
      throw new IllegalArgumentException("Conflicted event and auto-decline is enabled.");
    }
    // handle special case for multiple day, a loop for each day from start to end
    calendar.computeIfAbsent(date, k -> new HashSet<>()).add(event);
  }

  public void addRecurringEvents(String subject, String description, LocalDateTime startTime, LocalDateTime endTime,
                                 LocalDateTime endRecurring, String recurringDays, int occurrences) {
    List<EventInterface> events = generateRecurringEvents(subject, description, startTime, endTime, endRecurring, recurringDays, occurrences);
    if (hasAnyConflict(events)) {
      throw new IllegalArgumentException("Recurring event series conflicts with existing events.");
    }

    for (EventInterface event : events) {
      String date = dateFormatter.format(event.getStartTime());
      calendar.computeIfAbsent(date, k -> new HashSet<>()).add(event);
    }
  }

  private boolean hasConflict(EventInterface event) {
    String date = dateFormatter.format(event.getStartTime());
    if (calendar.containsKey(date)) {
      for (EventInterface existingEvent : calendar.get(date)) {
        if (event.isConflicted(existingEvent)) {
          return true;
        }
      }
    }
    return false;
  }

  private boolean hasAnyConflict(List<EventInterface> events) {
    for (EventInterface event : events) {
      if (hasConflict(event)) {
        return true;
      }
    }
    return false;
  }

  private List<EventInterface> generateRecurringEvents(String subject, String description, LocalDateTime startTime, LocalDateTime endTime,
                                              LocalDateTime endRecurring, String recurringDays, int occurrences) {
    List<EventInterface> result = new ArrayList<>();
    LocalDateTime currentStartTime = startTime;
    LocalDateTime currentEndTime = endTime;
    int count = 0;

    while (currentStartTime != null && (occurrences == 0 || count < occurrences)) {
      if (currentStartTime.isAfter(endRecurring)) {
        break;
      }

      if (recurringDays.contains(currentStartTime.getDayOfWeek().toString())) { // Update for new enum to parse day of the week to characters
        result.add(new RecurringEvent(subject, description, currentStartTime, currentEndTime, endRecurring, recurringDays, occurrences));
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

  public void printEvents() {
    for (Map.Entry<String, Set<EventInterface>> entry : calendar.entrySet()) {
      System.out.println("Date: " + entry.getKey());
      for (EventInterface event : entry.getValue()) {
        System.out.println("  -Subject :  " + event.getSubject());
        System.out.println("  -Description :  " + event.getDescription());
        System.out.println("  -Start Time :  " + event.getStartTime());
        System.out.println("  -End Time :  " + event.getEndTime());

      }
    }
  }
}