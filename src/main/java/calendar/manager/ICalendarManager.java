package calendar.manager;

import calendar.model.Calendar;

import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Interface for managing multiple calendars. Provides operations for creating, editing,
 * and managing calendars and copying events between them.
 */
public interface ICalendarManager {
  /**
   * Create a new calendar with the specified name and timezone.
   *
   * @param name     The calendar name
   * @param timezone The calendar timezone
   * @throws IllegalArgumentException if a calendar with the name already exists or timezone
   *                                  is invalid
   */
  void createCalendar(String name, ZoneId timezone) throws IllegalArgumentException;

  /**
   * Set a calendar as the active calendar.
   *
   * @param name The name of the calendar to set as active
   * @throws IllegalArgumentException if no calendar with the given name exists
   */
  void useCalendar(String name) throws IllegalArgumentException;

  /**
   * Edit a property of a calendar.
   *
   * @param name     The calendar name
   * @param property The property to edit ("name" or "timezone")
   * @param value    The new value for the property
   * @throws IllegalArgumentException if the calendar doesn't exist, property is invalid, or value
   *                                  is invalid
   */
  void editCalendarProperty(String name, String property, String value)
          throws IllegalArgumentException;

  /**
   * Copy a specific event from the active calendar to another calendar.
   *
   * @param eventName          The name of the event to copy
   * @param startDateTime      The start date and time of the event
   * @param targetCalendarName The name of the target calendar
   * @param targetDateTime     The target date and time for the copied event
   * @throws IllegalArgumentException if the target calendar doesn't exist or the event can't
   *                                  be found
   */
  void copyCalendarEvent(String eventName, LocalDateTime startDateTime, String targetCalendarName,
                         LocalDateTime targetDateTime) throws IllegalArgumentException;

  /**
   * Copy multiple events from a time range in the active calendar to another calendar.
   *
   * @param startDateTime      The start date and time of the range
   * @param endDateTime        The end date and time of the range
   * @param targetCalendarName The name of the target calendar
   * @param targetDateTime     The target date and time for the copied events
   * @throws IllegalArgumentException if the target calendar doesn't exist
   */
  void copyCalendarEvents(LocalDateTime startDateTime, LocalDateTime endDateTime,
                          String targetCalendarName, LocalDateTime targetDateTime)
          throws IllegalArgumentException;

  /**
   * Get the active calendar.
   *
   * @return The active calendar
   * @throws IllegalStateException if no calendar is active
   */
  Calendar getActiveCalendar() throws IllegalStateException;
}
