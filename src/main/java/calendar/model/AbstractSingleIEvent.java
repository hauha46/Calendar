package calendar.model;

import java.time.LocalDateTime;

/**
 * The abstract class for Single Event object. It has subjects, startTime, endTIme and description.
 */
abstract public class AbstractSingleIEvent implements IEvent {
  private String subject;
  private LocalDateTime startTime;
  private LocalDateTime endTime;
  private String description;

  /**
   * Construct an event based on given info.
   *
   * @param subject     the given subject.
   * @param description the given description.
   * @param startTime   the given start time.
   * @param endTime     the given end time.
   */
  public AbstractSingleIEvent(String subject, String description, LocalDateTime startTime,
                              LocalDateTime endTime) {
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

  @Override
  public String getSubject() {
    return subject;
  }

  @Override
  public LocalDateTime getStartTime() {
    return startTime;
  }

  @Override
  public LocalDateTime getEndTime() {
    return endTime;
  }

  @Override
  public String getDescription() {
    return description;
  }

  @Override
  public boolean isConflicted(IEvent otherEvent) {
    if (otherEvent == null) {
      return false;
    }

    LocalDateTime otherStart = otherEvent.getStartTime();
    LocalDateTime otherEnd = otherEvent.getEndTime();

    return !(this.getEndTime() != null && (this.getEndTime().isBefore(otherStart) ||
            this.getEndTime().isEqual(otherStart))) && !(otherEnd != null &&
            (otherEnd.isBefore(this.getStartTime()) || otherEnd.isEqual(this.getStartTime())));
  }
}
