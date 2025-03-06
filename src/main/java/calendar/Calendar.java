package calendar;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Calendar {
  private Map<String, Set<Event>> calendar;

  private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
  private boolean autoDeclineConflicts = false;

  public Calendar() {
    calendar = new TreeMap<>(Comparator.comparing((String date) -> LocalDate.parse(date)));
  }

  public void setAutoDeclineConflicts(boolean autoDeclineConflicts) {
    this.autoDeclineConflicts = autoDeclineConflicts;
  }

  public void editEventsFromTo(String subject, LocalDateTime startTime, LocalDateTime endTime) {
    LocalDate startDate = startTime.toLocalDate();
    LocalDate endDate = endTime.toLocalDate();


    for (Map.Entry<String, Set<Event>> entry : calendar.entrySet()) {
      LocalDate date = LocalDate.parse(entry.getKey());
      if (( date.isEqual(startDate) || date.isAfter(startDate)) && (date.isEqual(endDate) ||date.isBefore(endDate))) {
        Set<Event> events = entry.getValue();
        for (Event event : events) {

        }
      }
    }
  }

  public void editEventsFrom(String subject, LocalDateTime startTime) {

  }

  public void editEvents(String subject) {

  }

  public Event removeEvent() {}

  public Event searchEvent(){}

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
                                 LocalDateTime endRecurring, List<String> recurringDays, int occurrences) {
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
                                              LocalDateTime endRecurring, List<String> recurringDays, int occurrences) {
    List<Event> result = new ArrayList<>();
    LocalDateTime currentStartTime = startTime;
    LocalDateTime currentEndTime = endTime;
    int count = 0;

    while (currentStartTime != null && (occurrences == 0 || count < occurrences)) {
      if (currentStartTime.isAfter(endRecurring)) {
        break;
      }

      if (recurringDays.contains(currentStartTime.getDayOfWeek().toString())) {
        result.add(new Event(subject, description, currentStartTime, currentEndTime));
        count++;
      }

      currentStartTime = currentStartTime.plusDays(1);
      currentEndTime = currentEndTime.plusDays(1);
    }

    return result;
  }
}