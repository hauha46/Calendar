package calendar.controller;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

import calendar.manager.ICalendarManager;
import calendar.model.Calendar;
import calendar.model.ICalendar;
import calendar.model.IEvent;
import calendar.utils.DateTimeUtils;
import calendar.utils.EventsExporterFactory;
import calendar.utils.ExportEvents;

/**
 * The class for managing inputs commands to actual calendar operations coming from GUI.
 */
public class SwingController {
  private DateTimeUtils dateTimeUtils;
  private ICalendarManager calendarManager;
  private String currentCalendar;
  private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

  /**
   * Initiating the controller taking in the CalendarManager.
   * @param calendarManager object to access the date and the features offered by the calendar.
   */
  public SwingController(ICalendarManager calendarManager) {
    this.calendarManager = calendarManager;
    this.dateTimeUtils = new DateTimeUtils();
    List<String> calendarNames = calendarManager.getAllCalendarNames();
    this.currentCalendar = calendarNames.isEmpty() ? null : calendarNames.get(0);
    if (this.currentCalendar != null) {
      calendarManager.useCalendar(this.currentCalendar);
    }
  }

  /**
   * Input mapping to the create calendar command.
   * @param calendarName the calendar name.
   * @param timezone the timezone of the calendar.
   * @throws IllegalArgumentException
   */
  public void createCalendar(String calendarName, String timezone) throws IllegalArgumentException {
    ZoneId calendarTimeZone = dateTimeUtils.parseZoneId(timezone);
    calendarManager.createCalendar(calendarName, calendarTimeZone);
    if (currentCalendar == null) {
      currentCalendar = calendarName;
      calendarManager.useCalendar(calendarName);
    }
  }

  /**
   * INput maping to edit the property of the calendar.
   * @param calendarName the calendar name.
   * @param property the value to be changed.
   * @param value the new value replacing the old property value.
   * @throws IllegalArgumentException
   */
  public void editCalendarProperty(String calendarName, String property, String value) throws IllegalArgumentException {
    calendarManager.editCalendarProperty(calendarName, property, value);
    if (property.equals("name") && currentCalendar.equals(calendarName)) {
      currentCalendar = value;
    }
  }

  /**
   * Returns all the calendar present in the Map.
   * @return list of calendars.
   */
  public List<String> getAllCalendarNames() {
    return calendarManager.getAllCalendarNames();
  }

  /**
   * To search and make use of the calendar asked.
   * @param calendarName to use.
   */
  public void setCurrentCalendar(String calendarName) {
    if (!calendarManager.getAllCalendarNames().contains(calendarName)) {
      throw new IllegalArgumentException("Calendar does not exist: " + calendarName);
    }
    this.currentCalendar = calendarName;
    calendarManager.useCalendar(calendarName);
  }

  /**
   * To get the current calendar in use.
   * @return calendar in use.
   */
  public String getCurrentCalendar() {
    return currentCalendar;
  }

  /**
   * To get the current calendars timeZone.
   * @return zoneID of the calendar in use.
   */
  public ZoneId getCurrentCalendarTimezone() {
    return calendarManager.getActiveCalendar().getTimeZone();
  }

  /**
   * Input mapping to adding an event to the calendar.
   * @param subject of the event.
   * @param description of the event.
   * @param startTimeStr start time of the event.
   * @param endTimeStr end time of the event.
   */
  public void addEvent(String subject, String description, String startTimeStr, String endTimeStr) {
    ICalendar calendar = calendarManager.getActiveCalendar();
    LocalDateTime startTime = LocalDateTime.parse(startTimeStr, formatter);
    LocalDateTime endTime = LocalDateTime.parse(endTimeStr, formatter);
    calendar.addEvent(subject, description, startTime, endTime);
  }

  /**
   * Input mapping to adding recurring event.
   * @param subject of the event.
   * @param description of the event.
   * @param startTimeStr start time of the event.
   * @param endTimeStr end time of the event.
   * @param endRecurringStr end date for recurring.
   * @param recurringDays the number of days when recurring ends.
   * @param occurrences the number of occurrences the recurring event might have.
   */
  public void addRecurringEvent(String subject, String description, String startTimeStr, 
                              String endTimeStr, String endRecurringStr, String recurringDays, int occurrences) {
    ICalendar calendar = calendarManager.getActiveCalendar();
    LocalDateTime startTime = LocalDateTime.parse(startTimeStr, formatter);
    LocalDateTime endTime = LocalDateTime.parse(endTimeStr, formatter);
    LocalDateTime endRecurring = endRecurringStr.isEmpty() ? null : LocalDateTime.parse(endRecurringStr, formatter);
    calendar.addRecurringEvents(subject, description, startTime, endTime, endRecurring, recurringDays, occurrences);
  }

  /**
   * Input mapping for the editing the single event.
   * @param subject subject of the event.
   * @param startTimeStr the startTime of the event.
   * @param endTimeStr the endTime of te event.
   * @param property the property o the event to be edited.
   * @param newValue the value replacing the property to be edited.
   */
  public void editEventSingle(String subject, String startTimeStr, String endTimeStr, 
                            String property, String newValue) {
    ICalendar calendar = calendarManager.getActiveCalendar();
    LocalDateTime startTime = LocalDateTime.parse(startTimeStr, formatter);
    LocalDateTime endTime = LocalDateTime.parse(endTimeStr, formatter);
    calendar.editEventSingle(subject, startTime, endTime, property, newValue);
  }

  /**
   * The total events present for one day are to be returned.
   * @param date the date where events are to be returned.
   * @return the total list of event on a date.
   */
  public List<IEvent> getEventsForDay(LocalDate date) {
    ICalendar calendar = calendarManager.getActiveCalendar();
    LocalDateTime dayStart = date.atStartOfDay();
    LocalDateTime dayEnd = date.atTime(23, 59);
    return calendar.searchEvents(null, dayStart, dayEnd);
  }

  /**
   * Get the events for a month.
   * @param monthStart the start of the month.
   * @param monthEnd the end of the month.
   * @return the list of events in a month.
   */
  public List<IEvent> getEventsForMonth(LocalDate monthStart, LocalDate monthEnd) {
    ICalendar calendar = calendarManager.getActiveCalendar();
    LocalDateTime start = monthStart.atStartOfDay();
    LocalDateTime end = monthEnd.atTime(23, 59);
    return calendar.searchEvents(null, start, end);
  }

  /**
   * Exports the current calendar into the csv format.
   * @param fileName the name of the file to be named.
   */
  public void exportCalendarToCSV(String fileName) {
    Calendar calendar = calendarManager.getActiveCalendar();
    String format = "csv";
    ExportEvents exporter = EventsExporterFactory.getExporter(format);
    String exportedContent = exporter.exportEvents(calendar.getEventManager());

    try (java.io.PrintWriter out = new java.io.PrintWriter(fileName)) {
      out.print(exportedContent);
    } catch (Exception e) {
      throw new IllegalArgumentException("Error exporting calendar: " + e.getMessage(), e);
    }
  }

  /**
   * Importing the csv file into the calendar, to make events according to the csv file.
   * @param fileName
   */
  public void importCalendarFromCSV(String fileName) {
    ICalendar calendar = calendarManager.getActiveCalendar();
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");

    try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
      String line;
      boolean isFirstLine = true;
      while ((line = br.readLine()) != null) {
        if (isFirstLine) {
          isFirstLine = false;
          continue;
        }
        if (line.trim().isEmpty()) {
          continue;
        }
        String[] tokens = line.split(",", -1);
        if (tokens.length < 6) {
          throw new IllegalArgumentException("Invalid CSV format: " + line);
        }

        String subject = tokens[0].trim();
        String startDateStr = tokens[1].trim();
        String startTimeStr = tokens[2].trim();
        String endDateStr = tokens[3].trim();
        String endTimeStr = tokens[4].trim();
        String description = tokens[5].trim();

        LocalDate startDate = LocalDate.parse(startDateStr, dateFormatter);
        LocalTime startTime = LocalTime.parse(startTimeStr, timeFormatter);
        LocalDateTime startDateTime = LocalDateTime.of(startDate, startTime);

        LocalDate endDate = LocalDate.parse(endDateStr, dateFormatter);
        LocalTime endTime = LocalTime.parse(endTimeStr, timeFormatter);
        LocalDateTime endDateTime = LocalDateTime.of(endDate, endTime);

        calendar.addEvent(subject, description, startDateTime, endDateTime);
      }
    } catch (IOException | RuntimeException ex) {
      throw new RuntimeException(ex.getMessage(), ex);
    }
  }

  /**
   *
   * Method , used to pass the calendarManager to the Interpreter to maintain sync.
   * @return
   */
  public ICalendarManager getCalendarManager() {

    return calendarManager;
  }
}
