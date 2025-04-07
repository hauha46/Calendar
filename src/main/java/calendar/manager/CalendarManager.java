package calendar.manager;

import calendar.model.Calendar;
import calendar.model.IEvent;
import calendar.utils.DateTimeUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manager class for handling multiple calendars.
 */
public class CalendarManager implements ICalendarManager {
  private Map<String, Calendar> calendarMap;
  private DateTimeUtils dateTimeUtils;
  private String activeCalendarName;

  /**
   * Create a new calendar manager.
   */
  public CalendarManager() {
    calendarMap = new HashMap<>();
    dateTimeUtils = new DateTimeUtils();
    activeCalendarName = null;
  }

  /**
   * Create a new calendar with the specified name and timezone.
   *
   * @param name     The calendar name
   * @param timezone The calendar timezone
   * @throws IllegalArgumentException if a calendar with the name already exists or timezone
   *                                  is invalid
   */
  public void createCalendar(String name, ZoneId timezone) throws IllegalArgumentException {
    this.notIncludeCalendar(name);
    Calendar calendar = new Calendar(timezone);
    calendarMap.put(name, calendar);
  }

  /**
   * Set a calendar as the active calendar.
   *
   * @param name The name of the calendar to set as active
   * @throws IllegalArgumentException if no calendar with the given name exists
   */
  public void useCalendar(String name) throws IllegalArgumentException {

    this.hasCalendar(name);
    activeCalendarName = name;
  }

  /**
   * Edit a property of a calendar.
   *
   * @param name     The calendar name
   * @param property The property to edit ("name" or "timezone")
   * @param value    The new value for the property
   * @throws IllegalArgumentException if the calendar doesn't exist, property is invalid, or value
   *                                  is invalid
   */
  public void editCalendarProperty(
          String name, String property, String value) throws IllegalArgumentException {
    this.hasCalendar(name);
    Calendar calendar = calendarMap.get(name);
    switch (property.toLowerCase()) {
      case "name":
        this.notIncludeCalendar(value);
        calendarMap.remove(name);
        calendarMap.put(value, calendar);
        if (activeCalendarName.equals(name)) {
          activeCalendarName = value;
        }
        break;
      case "timezone":
        ZoneId newTimezone = this.dateTimeUtils.parseZoneId(value);
        calendar.setAutoDeclineConflicts(false);
        List<IEvent> foundEvents = calendar.getAllEvents();
        for (IEvent event : foundEvents) {
          calendar.removeEvent(event);
          LocalDateTime convertedStartDateTime = dateTimeUtils.convertTimeZone(
                  event.getStartTime(), calendar.getTimeZone(), newTimezone);
          LocalDateTime convertedEndDateTime = dateTimeUtils.convertTimeZone(
                  event.getEndTime(), calendar.getTimeZone(), newTimezone);
          calendar.addEvent(
                  event.getSubject(), event.getDescription(), convertedStartDateTime,
                  convertedEndDateTime);
        }
        calendar.setAutoDeclineConflicts(true);
        calendar.setTimeZone(newTimezone);
        break;
      default:
        throw new IllegalArgumentException("Invalid property: " + property);
    }
  }

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
  public void copyCalendarEvent(
          String eventName, LocalDateTime startDateTime, String targetCalendarName,
          LocalDateTime targetDateTime) throws IllegalArgumentException {
    this.hasCalendar(targetCalendarName);
    LocalDateTime endDateTime = dateTimeUtils.convertToEODDateTime(startDateTime);
    List<IEvent> foundEvents = getActiveCalendar().searchEvents(
            eventName, startDateTime, endDateTime);

    if (foundEvents.isEmpty()) {
      throw new IllegalArgumentException("No events found for " + eventName);
    }

    IEvent targetEvent = foundEvents.get(0);
    Duration duration = Duration.between(targetEvent.getStartTime(), targetEvent.getEndTime());
    Calendar targetCalendarInstance = calendarMap.get(targetCalendarName);
    targetCalendarInstance.addEvent(targetEvent.getSubject(), targetEvent.getDescription(),
            targetDateTime, targetDateTime.plus(duration));
  }

  /**
   * Copy multiple events from a time range in the active calendar to another calendar.
   *
   * @param startDateTime      The start date and time of the range
   * @param endDateTime        The end date and time of the range
   * @param targetCalendarName The name of the target calendar
   * @param targetDateTime     The target date and time for the copied events
   * @throws IllegalArgumentException if the target calendar doesn't exist
   */
  public void copyCalendarEvents(
          LocalDateTime startDateTime, LocalDateTime endDateTime, String targetCalendarName,
          LocalDateTime targetDateTime) throws IllegalArgumentException {
    this.hasCalendar(targetCalendarName);
    Calendar targetCalendarInstance = calendarMap.get(targetCalendarName);
    Calendar currentCalendarInstance = getActiveCalendar();
    List<IEvent> foundEvents = currentCalendarInstance.searchEvents(
            null, startDateTime, endDateTime);
    LocalDateTime eventInitialDateTime = dateTimeUtils.convertToSODDateTime(startDateTime);
    LocalDateTime targetInitialDateTime = dateTimeUtils.convertTimeZone(
            targetDateTime, getActiveCalendar().getTimeZone(),
            targetCalendarInstance.getTimeZone());
    List<IEvent> addedEvents = new ArrayList<>();
    try {
      for (IEvent event : foundEvents) {
        Duration duratiomFromStartTime =
                Duration.between(eventInitialDateTime, event.getStartTime());
        Duration duratiomFromEndTime = Duration.between(eventInitialDateTime, event.getEndTime());
        targetCalendarInstance.addEvent(
                event.getSubject(), event.getDescription(),
                targetInitialDateTime.plus(duratiomFromStartTime),
                targetInitialDateTime.plus(duratiomFromEndTime));
        addedEvents.add(event);
      }
    } catch (IllegalArgumentException e) {
      for (IEvent event : addedEvents) {
        targetCalendarInstance.removeEvent(event);
      }
      throw e;
    }

  }

  /**
   * Get the active calendar.
   *
   * @return The active calendar
   * @throws IllegalStateException if no calendar is active
   */
  public Calendar getActiveCalendar() throws IllegalStateException {
    if (activeCalendarName == null) {
      throw new IllegalStateException("No active calendar selected");
    }

    return calendarMap.get(activeCalendarName);
  }

  public List<String> getAllCalendarNames() {
    return new ArrayList<>(calendarMap.keySet());
  }

  /**
   * Check if a calendar with the given name exists.
   *
   * @param name The calendar name
   * @throws IllegalArgumentException if no calendar with the given name exists
   */
  private void hasCalendar(String name) throws IllegalArgumentException {
    if (!calendarMap.containsKey(name)) {
      throw new IllegalArgumentException("Calendar '" + name + "' does not exist");
    }
  }

  /**
   * Check if a calendar with the given name does not exist.
   *
   * @param name The calendar name
   * @throws IllegalArgumentException if a calendar with the given name already exists
   */
  private void notIncludeCalendar(String name) throws IllegalArgumentException {
    if (calendarMap.containsKey(name)) {
      throw new IllegalArgumentException("Calendar '" + name + "' already exist");
    }
  }
}