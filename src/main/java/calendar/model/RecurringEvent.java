package calendar.model;

import java.time.LocalDateTime;

/**
 * The class for Recurring Event object. It has endRecurring, recurringDays and
 * occurrences.
 */
public class RecurringEvent extends AbstractSingleIEvent implements IRecurringEvent {
  private LocalDateTime endRecurring;
  private String recurringDays;
  private int occurrences;

  /**
   * Construct a recurring event based on given info.
   *
   * @param subject       the given subject.
   * @param description   the given description.
   * @param startTime     the given start time.
   * @param endTime       the given end time.
   * @param endRecurring  the given end recurring date time.
   * @param recurringDays the given recurring days.
   * @param occurrences   the given occurrences.
   */
  public RecurringEvent(String subject, String description, LocalDateTime startTime,
                        LocalDateTime endTime, LocalDateTime endRecurring, String recurringDays,
                        int occurrences) {
    super(subject, description, startTime, endTime);
    this.endRecurring = endRecurring;
    this.recurringDays = recurringDays;
    this.occurrences = occurrences;
  }

  @Override
  public LocalDateTime getEndRecurring() {
    return endRecurring;
  }

  @Override
  public int getOccurrences() {
    return occurrences;
  }

  @Override
  public String getRecurringDays() {
    return recurringDays;
  }
}
