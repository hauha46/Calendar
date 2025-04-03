package calendar.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

import calendar.manager.ICalendarManager;
import calendar.model.ICalendar;
import calendar.model.IEvent;
import calendar.utils.DateTimeUtils;

public class SwingController {
  private DateTimeUtils dateTimeUtils;
  private ICalendarManager calendarManager;
  private String currentCalendar;
  private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

  public SwingController(ICalendarManager calendarManager) {
    this.calendarManager = calendarManager;
    this.dateTimeUtils = new DateTimeUtils();
    List<String> calendarNames = calendarManager.getAllCalendarNames();
    this.currentCalendar = calendarNames.isEmpty() ? null : calendarNames.get(0);
    if (this.currentCalendar != null) {
      calendarManager.useCalendar(this.currentCalendar);
    }
  }

  public void createCalendar(String calendarName, String timezone) throws IllegalArgumentException {
    ZoneId calendarTimeZone = dateTimeUtils.parseZoneId(timezone);
    calendarManager.createCalendar(calendarName, calendarTimeZone);
    if (currentCalendar == null) {
      currentCalendar = calendarName;
      calendarManager.useCalendar(calendarName);
    }
  }
  
  public void editCalendarProperty(String calendarName, String property, String value) throws IllegalArgumentException {
    calendarManager.editCalendarProperty(calendarName, property, value);
    if (property.equals("name") && currentCalendar.equals(calendarName)) {
      currentCalendar = value;
    }
  }

  public List<String> getAllCalendarNames() {
    return calendarManager.getAllCalendarNames();
  }
  
  public void setCurrentCalendar(String calendarName) {
    if (!calendarManager.getAllCalendarNames().contains(calendarName)) {
      throw new IllegalArgumentException("Calendar does not exist: " + calendarName);
    }
    this.currentCalendar = calendarName;
    calendarManager.useCalendar(calendarName);
  }
  
  public String getCurrentCalendar() {
    return currentCalendar;
  }
  
  public ZoneId getCurrentCalendarTimezone() {
    return calendarManager.getActiveCalendar().getTimeZone();
  }
  
  public void addEvent(String subject, String description, String startTimeStr, String endTimeStr) {
    ICalendar calendar = calendarManager.getActiveCalendar();
    LocalDateTime startTime = LocalDateTime.parse(startTimeStr, formatter);
    LocalDateTime endTime = LocalDateTime.parse(endTimeStr, formatter);
    calendar.addEvent(subject, description, startTime, endTime);
  }
  
  public void addRecurringEvent(String subject, String description, String startTimeStr, 
                              String endTimeStr, String endRecurringStr, String recurringDays, int occurrences) {
    ICalendar calendar = calendarManager.getActiveCalendar();
    LocalDateTime startTime = LocalDateTime.parse(startTimeStr, formatter);
    LocalDateTime endTime = LocalDateTime.parse(endTimeStr, formatter);
    LocalDateTime endRecurring = endRecurringStr.isEmpty() ? null : LocalDateTime.parse(endRecurringStr, formatter);
    calendar.addRecurringEvents(subject, description, startTime, endTime, endRecurring, recurringDays, occurrences);
  }
  
  public void editEventSingle(String subject, String startTimeStr, String endTimeStr, 
                            String property, String newValue) {
    ICalendar calendar = calendarManager.getActiveCalendar();
    LocalDateTime startTime = LocalDateTime.parse(startTimeStr, formatter);
    LocalDateTime endTime = LocalDateTime.parse(endTimeStr, formatter);
    calendar.editEventSingle(subject, startTime, endTime, property, newValue);
  }
  
  public List<IEvent> getEventsForDay(LocalDate date) {
    ICalendar calendar = calendarManager.getActiveCalendar();
    LocalDateTime dayStart = date.atStartOfDay();
    LocalDateTime dayEnd = date.atTime(23, 59);
    return calendar.searchEvents(null, dayStart, dayEnd);
  }
  
  public List<IEvent> getEventsForMonth(LocalDate monthStart, LocalDate monthEnd) {
    ICalendar calendar = calendarManager.getActiveCalendar();
    LocalDateTime start = monthStart.atStartOfDay();
    LocalDateTime end = monthEnd.atTime(23, 59);
    return calendar.searchEvents(null, start, end);
  }
  
  public void exportCalendarToCSV(String fileName) {
    ICalendar calendar = calendarManager.getActiveCalendar();
    calendar.exportCSV(fileName);
  }
  
  public void importCalendarFromCSV(String fileName) {
    // This will be implemented in the future
    throw new UnsupportedOperationException("CSV import not implemented yet");
  }
}
