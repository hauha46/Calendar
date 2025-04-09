package calendar.model;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import calendar.manager.EventManager;
import calendar.manager.IEventManager;

/**
 * A model class representing a calendar with events and timezone.
 */
public class Calendar implements ICalendar {
  private ZoneId timeZone;
  private boolean autoDeclineConflicts;
  private final IEventManager eventManager;

  /**
   * Construct a calendar with a name and timezone.
   *
   * @param timeZone The timezone of the calendar
   */
  public Calendar(ZoneId timeZone) {
    this.timeZone = timeZone;
    this.eventManager = new EventManager();
    this.autoDeclineConflicts = true;
  }

  /**
   * Get the timeZone value.
   *
   * @return The calendar timezone
   */
  public ZoneId getTimeZone() {
    return this.timeZone;
  }

  /**
   * Set the timeZone value.
   *
   * @param timeZone the given timezone value.
   */
  public void setTimeZone(ZoneId timeZone) {
    this.timeZone = timeZone;
  }

  /**
   * Set the autoDecline flag with a boolean value.
   *
   * @param autoDeclineConflicts the given boolean autoDecline value.
   */
  public void setAutoDeclineConflicts(boolean autoDeclineConflicts) {
    this.autoDeclineConflicts = autoDeclineConflicts;
  }

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
  public void addEvent(String subject, String description, LocalDateTime startTime,
                       LocalDateTime endTime) throws IllegalArgumentException {
    eventManager.addEvent(subject, description, startTime, endTime, this.autoDeclineConflicts);
  }

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
  public void addRecurringEvents(
          String subject, String description, LocalDateTime startTime, LocalDateTime endTime,
          LocalDateTime endRecurring, String recurringDays, int occurrences)
          throws IllegalArgumentException {
    eventManager.addRecurringEvents(subject, description, startTime, endTime,
            endRecurring, recurringDays, occurrences);
  }

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
  public void editEventSingle(String subject, LocalDateTime startTime, LocalDateTime endTime,
                              String property, String newValue) throws IllegalArgumentException {
    eventManager.editEventSingle(
            subject, startTime, endTime, property, newValue, this.autoDeclineConflicts);
  }

  /**
   * Edit an existing recurring event on the calendar based on the given input. Handle both cases
   * where either start time is provided or not.
   *
   * @param subject   the given subject.
   * @param startTime the given start time.
   * @param property  the name of the target property.
   * @param newValue  the new value for the target property.
   */
  public void editEventRecurring(String subject, LocalDateTime startTime, String property,
                                 String newValue) {
    eventManager.editEventRecurring(subject, startTime, property, newValue);
  }

  /**
   * Print all events in the calendar from start time to end time. Handle both cases where
   * either end time is provided or not.
   *
   * @param startTime the given start time.
   * @param endTime   the given end time.
   */
  public void printEvents(LocalDateTime startTime, LocalDateTime endTime) {
    eventManager.printEvents(startTime, endTime);
  }

  /**
   * Export all the events in the current calendar into a csv file for Google calendar import.
   *
   * @param fileName the given file name.
   */
  public void exportCSV(String fileName) {
    eventManager.exportCSV(fileName);
  }

  /**
   * Print out the status based on a given date time, whether it's busy or available.
   *
   * @param dateTime the given date time.
   */
  public void isBusy(LocalDateTime dateTime) {
    eventManager.isBusy(dateTime);
  }

  /**
   * Search events that belong to a recurring event based on the given info.
   *
   * @param subject   the given subject.
   * @param startTime the given start time.
   * @param endTime   the given end time.
   * @return the list of found events.
   */
  public List<IEvent> searchEvents(String subject, LocalDateTime startTime, LocalDateTime endTime) {
    return eventManager.searchEvents(subject, startTime, endTime);
  }

  /**
   * Get all active events in the current calendar.
   *
   * @return the list of found events.
   */
  public List<IEvent> getAllEvents() {
    return eventManager.getAllEvents();
  }

  /**
   * Get eventManger.
   *
   * @return the eventManager.
   */
  public IEventManager getEventManager() {
    return eventManager;
  }

  /**
   * Remove an event from the current calendar.
   *
   * @param event the given event.
   */
  public void removeEvent(IEvent event) {
    eventManager.removeEvent(event);
  }
}