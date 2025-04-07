package calendar.model;

import java.time.LocalDateTime;

/**
 * The class for Single Event object.
 */
public class OneTimeEvent extends AbstractSingleIEvent {

  /**
   * Construct an event based on given info.
   *
   * @param subject     the given subject.
   * @param description the given description.
   * @param startTime   the given start time.
   * @param endTime     the given end time.
   */
  public OneTimeEvent(String subject, String description, LocalDateTime startTime,
                      LocalDateTime endTime) {
    super(subject, description, startTime, endTime);
  }
}
