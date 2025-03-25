package calendar.model;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

/**
 * Interface representing a calendar with events and timezone functionality.
 * Provides operations for managing events within a specific calendar context.
 */
public interface ICalendar {
  /**
   * Get the timeZone value.
   *
   * @return The calendar timezone
   */
  ZoneId getTimeZone();

  /**
   * Set the timeZone value.
   *
   * @param timeZone the given timezone value.
   */
  void setTimeZone(ZoneId timeZone);

  /**
   * Set the autoDecline flag with a boolean value.
   *
   * @param autoDeclineConflicts the given boolean autoDecline value.
   */
  void setAutoDeclineConflicts(boolean autoDeclineConflicts);

  /**
   * Add an event into calendar with its respective date. Handling both single event and multiple
   * spanning days event.
   *
   * @param subject     the given subject.
   * @param description the given description.
   * @param startTime   the given start time.
   * @param endTime     the given end time.
   * @throws IllegalArgumentException throws error if the input is invalid.
   */
  void addEvent(String subject, String description, LocalDateTime startTime,
                LocalDateTime endTime) throws IllegalArgumentException;

  /**
   * Add a recurring events by splitting it into multiple single events, based on the provided
   * input.
   *
   * @param subject       the given subject.
   * @param description   the given description.
   * @param startTime     the given start time.
   * @param endTime       the given end time.
   * @param endRecurring  the given end recurring date time.
   * @param recurringDays the given recurring days.
   * @param occurrences   the given occurrences.
   * @throws IllegalArgumentException throws error if the input is invalid
   */
  void addRecurringEvents(
          String subject, String description, LocalDateTime startTime, LocalDateTime endTime,
          LocalDateTime endRecurring, String recurringDays, int occurrences)
          throws IllegalArgumentException;

  /**
   * Edit an existing event on the calendar based on the given input. Also handle the multiple
   * spanning days event case.
   *
   * @param subject   the given subject.
   * @param startTime the given start time.
   * @param endTime   the given end time.
   * @param property  the name of the target property.
   * @param newValue  the new value for the target property.
   * @throws IllegalArgumentException throws error if the input is invalid
   */
  void editEventSingle(String subject, LocalDateTime startTime, LocalDateTime endTime,
                        String property, String newValue) throws IllegalArgumentException;

  /**
   * Edit an existing recurring event on the calendar based on the given input. Handle both cases
   * where either start time is provided or not.
   *
   * @param subject   the given subject.
   * @param startTime the given start time.
   * @param property  the name of the target property.
   * @param newValue  the new value for the target property.
   */
  void editEventRecurring(String subject, LocalDateTime startTime, String property,
                           String newValue);

  /**
   * Print all events in the calendar from start time to end time. Handle both cases where
   * either end time is provided or not.
   *
   * @param startTime the given start time.
   * @param endTime   the given end time.
   */
  void printEvents(LocalDateTime startTime, LocalDateTime endTime);

  /**
   * Export all the events in the current calendar into a csv file for Google calendar import.
   *
   * @param fileName the given file name.
   */
  void exportCSV(String fileName);

  /**
   * Print out the status based on a given date time, whether it's busy or available.
   *
   * @param dateTime the given date time.
   */
  void isBusy(LocalDateTime dateTime);

  /**
   * Search events that belong to a recurring event based on the given info.
   *
   * @param subject   the given subject.
   * @param startTime the given start time.
   * @param endTime   the given end time.
   * @return the list of found events.
   */
  List<IEvent> searchEvents(String subject, LocalDateTime startTime, LocalDateTime endTime);

  /**
   * Get all active events in the current calendar.
   *
   * @return the list of found events.
   */
  List<IEvent> getAllEvents();

  /**
   * Remove an event from the current calendar.
   *
   * @param event the given event.
   */
  void removeEvent(IEvent event);
}
