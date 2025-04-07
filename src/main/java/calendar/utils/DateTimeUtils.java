package calendar.utils;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.zone.ZoneRulesException;

/**
 * Utility class for date and time operations in the calendar application.
 * Provides methods for parsing, formatting, and manipulating date and time values.
 */
public class DateTimeUtils {
  private static final DateTimeFormatter dateTimeFormatter =
          DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

  /**
   * Parse ZoneId object from timezone string input.
   *
   * @param timezoneStr the given timezone string.
   * @return parsed ZoneId object.
   * @throws IllegalArgumentException throws error if the string does not have appropriate format.
   */
  public ZoneId parseZoneId(String timezoneStr) {
    try {
      return ZoneId.of(timezoneStr);
    } catch (DateTimeParseException | ZoneRulesException e) {
      throw new IllegalArgumentException("Invalid timezone format: " + timezoneStr);
    }
  }

  /**
   * Parse LocalDateTime object from date time string input.
   *
   * @param dateTimeStr the given date time string.
   * @return parsed LocalDateTime object.
   * @throws IllegalArgumentException throws error if the string does not have appropriate format.
   */
  public LocalDateTime parseDateTime(String dateTimeStr) {
    try {
      return LocalDateTime.parse(dateTimeStr);
    } catch (DateTimeParseException e) {
      throw new IllegalArgumentException("Invalid date/time format: " + dateTimeStr);
    }
  }

  /**
   * Parse LocalDateTime object from date string input.
   *
   * @param dateStr the given date string.
   * @return parsed LocalDateTime object.
   * @throws IllegalArgumentException throws error if the string does not have appropriate format.
   */
  public LocalDateTime parseDateToDateTime(String dateStr) {
    try {
      return LocalDateTime.parse(dateStr + "T00:00", dateTimeFormatter);
    } catch (DateTimeParseException e) {
      throw new IllegalArgumentException("Invalid date format: " + dateStr);
    }
  }

  /**
   * Converts a LocalDateTime to the start of the day (midnight).
   *
   * @param dateTime the given date time.
   * @return a LocalDateTime set to the start of the day (00:00).
   */
  public LocalDateTime convertToSODDateTime(LocalDateTime dateTime) {
    return dateTime.toLocalDate().atStartOfDay();
  }

  /**
   * Converts a LocalDateTime to the end of the day (23:59).
   *
   * @param dateTime the given date time.
   * @return a LocalDateTime set to the end of the day (23:59).
   */
  public LocalDateTime convertToEODDateTime(LocalDateTime dateTime) {
    return dateTime.toLocalDate().atTime(LocalTime.of(23, 59));
  }

  /**
   * Converts a LocalDateTime from one timezone to another.
   *
   * @param dateTime   the LocalDateTime to convert
   * @param sourceZone the source timezone of the dateTime
   * @param targetZone the target timezone to convert to
   * @return the converted LocalDateTime in the target timezone
   */
  public LocalDateTime convertTimeZone(
          LocalDateTime dateTime, ZoneId sourceZone, ZoneId targetZone) {
    if (dateTime == null || sourceZone == null || targetZone == null) {
      throw new IllegalArgumentException("DateTime and timezones cannot be null");
    }
    ZonedDateTime zonedDateTime = dateTime.atZone(sourceZone);
    ZonedDateTime targetZonedDateTime = zonedDateTime.withZoneSameInstant(targetZone);
    return targetZonedDateTime.toLocalDateTime();
  }
}
