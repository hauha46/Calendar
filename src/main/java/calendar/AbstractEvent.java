package calendar;

import java.time.LocalDateTime;

abstract public class AbstractEvent implements EventInterface{

  private String subject;
  private LocalDateTime startTime;
  private LocalDateTime endTime;
  private String description;

  public AbstractEvent(String subject, String description, LocalDateTime startTime, LocalDateTime endTime) {
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

  abstract public boolean isConflicted(EventInterface otherEvent);

}
