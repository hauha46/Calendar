package calendar;

import calendar.manager.ICalendarManager;
import calendar.model.Calendar;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class mock for the CalendarManager , used for isolating classes.
 */
public class MockCalendarManager implements ICalendarManager {
  public Map<String, Calendar> calendars = new HashMap<>();
  public Calendar activeCalendar;
  public String lastMethodCalled = "";
  public Object[] lastMethodArgs = new Object[0];

  @Override
  public void createCalendar(String name, ZoneId timeZone) {
    lastMethodCalled = "createCalendar";
    lastMethodArgs = new Object[]{name, timeZone};
    Calendar calendar = new Calendar(timeZone);
    calendars.put(name, calendar);
  }

  @Override
  public void useCalendar(String name) {
    lastMethodCalled = "useCalendar";
    lastMethodArgs = new Object[]{name};
    activeCalendar = calendars.get(name);
  }

  @Override
  public void editCalendarProperty(String name, String property, String value) {
    lastMethodCalled = "editCalendarProperty";
    lastMethodArgs = new Object[]{name, property, value};
  }

  @Override
  public void copyCalendarEvent(String eventName, LocalDateTime startDateTime,
                                String targetCalendarName, LocalDateTime targetStartDateTime) {
    lastMethodCalled = "copyCalendarEvent";
    lastMethodArgs =
            new Object[]{eventName, startDateTime, targetCalendarName, targetStartDateTime};
  }

  @Override
  public void copyCalendarEvents(LocalDateTime startDateTime, LocalDateTime endDateTime,
                                 String targetCalendarName, LocalDateTime targetStartDateTime) {
    lastMethodCalled = "copyCalendarEvents";
    lastMethodArgs =
            new Object[]{startDateTime, endDateTime, targetCalendarName, targetStartDateTime};
  }

  @Override
  public Calendar getActiveCalendar() {
    lastMethodCalled = "getActiveCalendar";
    return activeCalendar;
  }

  @Override
  public List<String> getAllCalendarNames() {
    return List.of();
  }
}