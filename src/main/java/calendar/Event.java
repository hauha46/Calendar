package calendar;

import java.time.LocalDateTime;

public class Event {
  private String subject;
  private LocalDateTime startTime;
  private LocalDateTime endTime;
  private String description;

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

  public LocalDateTime getStartTime() {
    return startTime;
  }

  public LocalDateTime getEndTime() {
    return endTime;
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