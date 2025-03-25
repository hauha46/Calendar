package calendar.model;

import java.time.LocalDateTime;

/**
 * This interface represents a recurring event.
 */
public interface IRecurringEvent extends IEvent {
  /**
   * Retrieve the date time recurring ends of the current recurring event.
   *
   * @return the date time recurring ends.
   */
  LocalDateTime getEndRecurring();

  /**
   * Retrieve the number of occurrences of the current recurring event.
   *
   * @return the number of occurrences.
   */
  int getOccurrences();

  /**
   * Retrieve the recurring days of the current recurring event.
   *
   * @return the recurring days.
   */
  String getRecurringDays();
}
