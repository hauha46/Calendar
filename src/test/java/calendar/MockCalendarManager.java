package calendar;

import calendar.manager.ICalendarManager;
import calendar.model.Calendar;
import calendar.model.ICalendar;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is a mock for the CalendarManager used for isolating classes.
 */
public class MockCalendarManager implements ICalendarManager {
  public Map<String, ICalendar> calendars = new HashMap<>();
  public Calendar activeCalendar;
  public String lastMethodCalled = "";
  public Object[] lastMethodArgs = new Object[0];

  @Override
  public void createCalendar(String name, ZoneId timeZone) {
    lastMethodCalled = "createCalendar";
    lastMethodArgs = new Object[]{name, timeZone};
    Calendar calendar = new Calendar(timeZone);
    calendars.put(name, calendar);
    // Set activeCalendar if not already assigned.
    if (activeCalendar == null) {
      activeCalendar = calendar;
    }
  }

  @Override
  public void useCalendar(String name) {
    lastMethodCalled = "useCalendar";
    lastMethodArgs = new Object[]{name};
    activeCalendar = (Calendar)calendars.get(name);
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
    lastMethodArgs = new Object[]{eventName, startDateTime, targetCalendarName, targetStartDateTime};
  }

  @Override
  public void copyCalendarEvents(LocalDateTime startDateTime, LocalDateTime endDateTime,
                                 String targetCalendarName, LocalDateTime targetStartDateTime) {
    lastMethodCalled = "copyCalendarEvents";
    lastMethodArgs = new Object[]{startDateTime, endDateTime, targetCalendarName, targetStartDateTime};
  }

  @Override
  public Calendar getActiveCalendar() {
    lastMethodCalled = "getActiveCalendar";
    return  activeCalendar;
  }

  @Override
  public List<String> getAllCalendarNames() {
    return new ArrayList<>(calendars.keySet());
  }
}
