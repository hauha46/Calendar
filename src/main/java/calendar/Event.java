package calendar;

import java.time.LocalDateTime;
import java.util.List;

public class Event {
  private String subject;
  private LocalDateTime startTime;
  private LocalDateTime endTime;
  private String description;
  private LocalDateTime endRecurring;
  private String recurringDays;
  private int occurrences;

  public Event(String subject, String description, LocalDateTime startTime, LocalDateTime endTime) {
    if (startTime == null) {
      throw new IllegalArgumentException("Start time cannot be null.");
    }
    if (endTime != null && endTime.isBefore(startTime)) {
      throw new IllegalArgumentException("End time cannot be before start time.");
    }
    this.subject = subject;
    this.description = description;
    this.startTime = startTime;
    this.endTime = endTime;
  }

  public Event(String subject, String description, LocalDateTime startTime, LocalDateTime endTime, LocalDateTime endRecurring, String recurringDays, int occurrences) {
    if (startTime == null) {
      throw new IllegalArgumentException("Start time cannot be null.");
    }
    if (endTime != null && endTime.isBefore(startTime)) {
      throw new IllegalArgumentException("End time cannot be before start time.");
    }
    this.subject = subject;
    this.description = description;
    this.startTime = startTime;
    this.endTime = endTime;
    this.endRecurring = endRecurring;
    this.recurringDays = recurringDays;
    this.occurrences = occurrences;
  }

  public String getSubject() {
    return subject;
  }

  public LocalDateTime getStartTime() {
    return startTime;
  }

  public LocalDateTime getEndTime() {
    return endTime;
  }

  public String getDescription() {
    return description;
  }

  public LocalDateTime getEndRecurring() {
    return endRecurring;
  }

  public String getRecurringDays() {
    return recurringDays;
  }

  public int getOccurrences() {
    return occurrences;
  }

  public boolean isConflicted(Event otherEvent) {
    if (otherEvent == null) {
      return false;
    }
    LocalDateTime otherStart = otherEvent.getStartTime();
    LocalDateTime otherEnd = otherEvent.getEndTime();

    // Check if the events overlap
    return !(this.endTime != null && this.endTime.isBefore(otherStart)) &&
            !(otherEnd != null && otherEnd.isBefore(this.startTime));
  }
}