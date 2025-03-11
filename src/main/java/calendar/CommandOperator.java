package calendar;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class CommandOperator {
  private Calendar calendar;
  private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm");
  private DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

  public CommandOperator() {
    this.calendar = new Calendar();
  }

  public void setAutoDecline(boolean t)
  {calendar.setAutoDeclineConflicts(t);
  }

  // for handling single events with auto decline, startDate, endDate and allDay event check.
  public void createEventSingle(String subject, String startTime, String endTime) {
    LocalDateTime startDateTime = LocalDateTime.parse(startTime);
    LocalDateTime endDateTime = endTime.isEmpty() ? startDateTime.toLocalDate().atTime(LocalTime.MAX) : LocalDateTime.parse(endTime);
    calendar.addEvent(subject, "",startDateTime, endDateTime);
  }


//changed end recurring from reccuringdays in ine 32
  public void createEventRecurring(String subject, String startTime, String endTime, String endRecurring, String recurringDay, String occurrences) {
    int occurrencesParse = occurrences.isEmpty() ? 0 : Integer.parseInt(occurrences);
    LocalDateTime endRecurringParse = endRecurring.isEmpty() ? null : LocalDateTime.parse(endRecurring);
    LocalDateTime startDateTime = LocalDateTime.parse(startTime);
    LocalDateTime endDateTime =  LocalDateTime.parse(endTime);
    calendar.addRecurringEvents(subject, "", startDateTime, endDateTime, endRecurringParse, recurringDay, occurrencesParse);
  }

  public void createEventRecurringAllDay(String subject, String startDate, String endRecurring, String recurringDay, String occurrences) {
    int occurrencesParse = occurrences.isEmpty() ? 0 : Integer.parseInt(occurrences);
    LocalDateTime endRecurringParse = recurringDay.isEmpty() ? null : LocalDateTime.parse(endRecurring);
    LocalDateTime startDateTime = LocalDate.parse(startDate).atStartOfDay();
    LocalDateTime endDateTime =  LocalDate.parse(startDate).atTime(LocalTime.MAX);
    calendar.addRecurringEvents(subject, "", startDateTime, endDateTime, endRecurringParse, recurringDay, occurrencesParse);
  }

  public void editEventSingle(String subject, String startTime, String endTime, String property, String newValue) {
    LocalDateTime startDateTime = LocalDateTime.parse(startTime);
    LocalDateTime endDateTime =  LocalDateTime.parse(endTime);
    calendar.editEventSingle(subject, startDateTime, endDateTime, property, newValue);
  }

  public void editEventRecurring(String subject, String startTime, String property, String newValue) {
    LocalDateTime startDateTime = startTime.isEmpty() ? null : LocalDateTime.parse(startTime);
    calendar.editEventRecurring(subject, startDateTime, property, newValue);
  }

  // Handling both start
  public void printEvents(String startDate, String endDate) {
    LocalDate startDateParse = LocalDate.parse(startDate);
    LocalDate endDateParse = endDate.isEmpty() ? startDateParse : LocalDate.parse(endDate);
    calendar.printEvents(startDateParse, endDateParse);
  }

  public void isBusy(String date) {
    LocalDate dateParse = LocalDate.parse(date);
    calendar.isBusy(dateParse);
  }
}




