import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

//this can act as a new factory method which can be used to create new calendar object types(gregorian,chinese etc) when implemented
public class CalendarStorage {

  private final Map<String, Calendar> calendars;
  private Calendar currentCalendar;

  public CalendarStorage() {
    this.calendars = new HashMap<>();
  }

  public void createCalendar(String name, String timeZone) {
    if (calendars.containsKey(name)) {
      throw new IllegalArgumentException("Calendar with name '" + name + "' already exists.");
    }
    ZoneId timezone = ZoneId.of(timeZone);
    Calendar newCalendar = new Calendar(name, timezone);
    calendars.put(name, newCalendar);
    System.out.println("Calendar '" + name + "' created with timezone: " + timezone);
  }

  public Calendar useCalendar(String name) {
    if (!calendars.containsKey(name)) {
      throw new IllegalArgumentException("Calendar with name '" + name + "' does not exist.");
    }
    currentCalendar = calendars.get(name);
    return currentCalendar;
  }



}
