package calendar;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Calendar {
  private Map<LocalDate, Set<EventInterface>> calendar;
  private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
  private DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
  private boolean autoDeclineConflicts = false;

  public Calendar() {
    calendar = new TreeMap<>(Comparator.naturalOrder());
  }

  public void setAutoDeclineConflicts(boolean autoDeclineConflicts) {
    this.autoDeclineConflicts = autoDeclineConflicts;
  }

  public void addEvent(String subject, String description, LocalDateTime startTime, LocalDateTime endTime) {
    List<EventInterface> events = new ArrayList<>();
    //nullpointer exception if passed wrong date here.
    LocalDate startDate = startTime.toLocalDate();
    LocalDate endDate = endTime.toLocalDate();

    if (startDate.isAfter(endDate)) {
      throw new IllegalArgumentException("Start date cannot be after end date");
    } else if (endDate.isEqual(startDate)) {
      events.add(new OneTimeEvent(subject, description, startTime, endTime));
    } else {
      LocalDateTime currentStartTime = startTime;
      LocalDateTime currentEndTime = startTime.toLocalDate().atTime(LocalTime.MAX);
      while (currentStartTime.isBefore(endTime)) {
        currentStartTime = currentStartTime.toLocalDate().equals(startDate) ? startTime : currentStartTime;
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
      calendar.computeIfAbsent(event.getStartTime().toLocalDate(), k -> new HashSet<>()).add(event);
    }
  }

  public void addRecurringEvents(String subject, String description, LocalDateTime startTime, LocalDateTime endTime,
                                 LocalDateTime endRecurring, String recurringDays, int occurrences) {
    List<EventInterface> events = generateRecurringEvents(subject, description, startTime, endTime, endRecurring, recurringDays, occurrences);
    if (hasAnyConflict(events)) {
      throw new IllegalArgumentException("Recurring event series conflicts with existing events.");
    }

    for (EventInterface event : events) {
      calendar.computeIfAbsent(event.getStartTime().toLocalDate(), k -> new HashSet<>()).add(event);
    }
  }

  public void editEventSingle(String subject, LocalDateTime startTime, LocalDateTime endTime, String property, String newValue) {
    List<EventInterface> events = new ArrayList<>();
    LocalDate startDate = startTime.toLocalDate();
    LocalDate endDate = endTime.toLocalDate();

    if (startDate.isAfter(endDate)) {
      throw new IllegalArgumentException("Start date cannot be after end date");
    } else if (startDate.isEqual(endDate)) {
      events.add(searchEvent(subject, startTime, endTime));
    } else {
      LocalDateTime currentStartTime = startTime;
      LocalDateTime currentEndTime = startTime.toLocalDate().atTime(LocalTime.MAX);
      while (currentStartTime.isBefore(endTime)) {
        currentStartTime = currentStartTime.toLocalDate().equals(startDate) ? startTime : currentStartTime;
        currentEndTime = currentEndTime.toLocalDate().equals(endDate) ? endTime : currentEndTime;
        events.add(searchEvent(subject, currentStartTime, currentEndTime));
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
      }

      try {
        addEvent(newSubject, newDescription, newStartTime, newEndTime);
      } catch (Exception e) {
        addEvent(subject, events.get(0).getDescription(), startTime, endTime);
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
      }
      try {
        addRecurringEvents(newSubject, newDescription, newStartTime, newEndTime, newEndRecurring, newRecurringDays, newOccurrences);
      } catch (Exception e) {
        addRecurringEvents(foundEvent.getSubject(), foundEvent.getDescription(), foundEvent.getStartTime(), foundEvent.getEndTime(), foundEvent.getEndRecurring(), foundEvent.getRecurringDays(), foundEvent.getOccurrences());
      }
    }
  }

  public void printEvents() {
    for (Map.Entry<LocalDate, Set<EventInterface>> entry : calendar.entrySet()) {
      System.out.println("Date: " + entry.getKey());
      for (EventInterface event : entry.getValue()) {
        System.out.println("  -Subject :  " + event.getSubject());
        System.out.println("  -Description :  " + event.getDescription());
        System.out.println("  -Start Time :  " + event.getStartTime());
        System.out.println("  -End Time :  " + event.getEndTime());

      }
    }
  }

  public void printEvents(LocalDate startDate, LocalDate endDate) {
    LocalDate currentDate = startDate;
    while (currentDate.isEqual(endDate) || currentDate.isBefore(endDate)) {
      if (calendar.containsKey(currentDate)){
        System.out.println(dateFormatter.format(currentDate));
        for (EventInterface event : calendar.get(currentDate)) {
          System.out.println("Event Name: " + event.getSubject() + ", start time: " + timeFormatter.format(event.getStartTime()) + ", end time: " + timeFormatter.format(event.getEndTime()));
        }
      }
      currentDate = currentDate.plusDays(1);
    }
  }

  public void isBusy(LocalDate date){
    String result = calendar.containsKey(date) ? "busy" : "available";
    System.out.println(result);
  }

  // Helper methods
  private void removeEvent(EventInterface event) {
    if (event != null){
      Set<EventInterface> events = calendar.get(event.getStartTime().toLocalDate());
      events.remove(event);
    }
  }

  private EventInterface searchEvent(String subject, LocalDateTime startTime, LocalDateTime endTime) {
    for (Map.Entry<LocalDate, Set<EventInterface>> entry : calendar.entrySet()) {
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
    for (Map.Entry<LocalDate, Set<EventInterface>> entry : calendar.entrySet()) {
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
        result.add(new RecurringEvent(subject, description, currentStartTime, currentEndTime, endRecurring, recurringDays, occurrences));
        count++;
      }

      currentStartTime = currentStartTime.plusDays(1);
      currentEndTime = currentEndTime.plusDays(1);
    }

    return result;
  }
}