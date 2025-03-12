package calendar;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * A JUnit test class for the Command Parser class.
 */

class CommandParserTest {
  private CommandParser commandParser;
  private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
  private final PrintStream originalOut = System.out;

  @BeforeEach
  void setUp() {
    commandParser = new CommandParser();
    System.setOut(new PrintStream(outputStream));
  }

  @Test
  void testCreateSingleEvent() {
    String createCommand = "create event Meeting from 2023-10-10T09:00 to 2023-10-10T10:00";
    assertDoesNotThrow(() -> commandParser.parseCommand(createCommand));

    String printCommand = "print events on 2023-10-10";
    assertDoesNotThrow(() -> commandParser.parseCommand(printCommand));

    String expectedOutput = "Date: 10/10/2023\n" +
            "  -Subject :  Meeting\n" +
            "  -Description :  \n" +
            "  -Start Time :  2023-10-10T09:00\n" +
            "  -End Time :  2023-10-10T10:00\n";
    assertEquals(expectedOutput, outputStream.toString());
  }

  @Test
  void testCreateSingleEventAllDay() {
    String input = "create event Conference on 2023-10-12T00:00";
    assertDoesNotThrow(() -> commandParser.parseCommand(input));

    String printCommand = "print events on 2023-10-12";
    assertDoesNotThrow(() -> commandParser.parseCommand(printCommand));

    String expectedOutput = "Date: 10/12/2023\n" +
            "  -Subject :  Conference\n" +
            "  -Description :  \n" +
            "  -Start Time :  2023-10-12T00:00\n" +
            "  -End Time :  2023-10-12T23:59\n";
    assertEquals(expectedOutput, outputStream.toString());
  }

  @Test
  void testCreateSingleEventAutoDeclined() {
    String createCommand =
            "create event --autoDecline Meeting from 2023-10-10T09:00 to 2023-10-10T10:00";
    assertDoesNotThrow(() -> commandParser.parseCommand(createCommand));

    String conflictCommand =
            "create event --autoDecline Conflict from 2023-10-10T09:30 to 2023-10-10T10:30";
    Exception exception = assertThrows(
            IllegalArgumentException.class, () -> commandParser.parseCommand(conflictCommand));
    assertEquals("Conflicted event and auto-decline is enabled.", exception.getMessage());
  }

  @Test
  void testCreateSingleEventMultipleSpanningDays() {
    String createCommand = "create event Conference from 2023-10-10T09:00 to 2023-10-12T17:00";
    assertDoesNotThrow(() -> commandParser.parseCommand(createCommand));

    String printCommand = "print events from 2023-10-10T00:00 to 2023-10-12T23:59";
    assertDoesNotThrow(() -> commandParser.parseCommand(printCommand));

    String output = outputStream.toString();
    assertTrue(output.contains("Date: 10/10/2023"));
    assertTrue(output.contains("Date: 10/11/2023"));
    assertTrue(output.contains("Date: 10/12/2023"));
  }

  @Test
  void testCreateSingleEventMultipleSpanningDaysMidnight() {
    String createCommand = "create event LateEvent from 2023-10-10T22:00 to 2023-10-11T00:00";
    assertDoesNotThrow(() -> commandParser.parseCommand(createCommand));

    String printCommand = "print events from 2023-10-10T00:00 to 2023-10-11T23:59";
    assertDoesNotThrow(() -> commandParser.parseCommand(printCommand));

    String output = outputStream.toString();
    assertTrue(output.contains("Date: 10/10/2023"));
    assertTrue(output.contains("-Start Time :  2023-10-10T22:00"));
    assertTrue(output.contains("-End Time :  2023-10-10T23:59"));
    assertTrue(output.contains("Date: 10/11/2023"));
    assertTrue(output.contains("-Start Time :  2023-10-11T00:00"));
    assertTrue(output.contains("-End Time :  2023-10-11T00:00"));
  }

  @Test
  void testEditSingleEventName() {
    String createCommand = "create event Workshop from 2023-10-11T14:00 to 2023-10-11T16:00";
    assertDoesNotThrow(() -> commandParser.parseCommand(createCommand));

    String editCommand =
            "edit event name Workshop from 2023-10-11T14:00 to 2023-10-11T16:00 "
                    + "with UpdatedWorkshop";
    assertDoesNotThrow(() -> commandParser.parseCommand(editCommand));

    String printCommand = "print events on 2023-10-11";
    assertDoesNotThrow(() -> commandParser.parseCommand(printCommand));

    String expectedOutput = "Date: 10/11/2023\n" +
            "  -Subject :  UpdatedWorkshop\n" +
            "  -Description :  \n" +
            "  -Start Time :  2023-10-11T14:00\n" +
            "  -End Time :  2023-10-11T16:00\n";
    assertEquals(expectedOutput, outputStream.toString());
  }

  @Test
  void testEditSingleEventStartTime() {
    String createCommand = "create event Meeting from 2023-10-10T09:00 to 2023-10-10T10:00";
    assertDoesNotThrow(() -> commandParser.parseCommand(createCommand));

    String editCommand =
            "edit event startTime Meeting from 2023-10-10T09:00 to 2023-10-10T10:00 "
                    + "with 2023-10-10T09:30";
    assertDoesNotThrow(() -> commandParser.parseCommand(editCommand));

    String printCommand = "print events on 2023-10-10";
    assertDoesNotThrow(() -> commandParser.parseCommand(printCommand));

    assertTrue(outputStream.toString().contains("-Start Time :  2023-10-10T09:30"));
  }

  @Test
  void testEditSingleEventEndTime() {
    String createCommand = "create event Meeting from 2023-10-10T09:00 to 2023-10-10T10:00";
    assertDoesNotThrow(() -> commandParser.parseCommand(createCommand));

    String editCommand =
            "edit event endTime Meeting from 2023-10-10T09:00 to 2023-10-10T10:00 "
                    + "with 2023-10-10T11:00";
    assertDoesNotThrow(() -> commandParser.parseCommand(editCommand));

    String printCommand = "print events on 2023-10-10";
    assertDoesNotThrow(() -> commandParser.parseCommand(printCommand));

    assertTrue(outputStream.toString().contains("-End Time :  2023-10-10T11:00"));
  }

  @Test
  void testEditSingleEventDescription() {
    String createCommand = "create event Meeting from 2023-10-10T09:00 to 2023-10-10T10:00";
    assertDoesNotThrow(() -> commandParser.parseCommand(createCommand));

    String editCommand =
            "edit event description Meeting from 2023-10-10T09:00 to 2023-10-10T10:00 with Weekly";
    assertDoesNotThrow(() -> commandParser.parseCommand(editCommand));

    String printCommand = "print events on 2023-10-10";
    assertDoesNotThrow(() -> commandParser.parseCommand(printCommand));

    System.out.println(outputStream.toString());
    assertTrue(outputStream.toString().contains("-Description :  Weekly"));
  }

  @Test
  void testEditSingleEventInvalidProperty() {
    String createCommand = "create event Meeting from 2023-10-10T09:00 to 2023-10-10T10:00";
    assertDoesNotThrow(() -> commandParser.parseCommand(createCommand));

    String editCommand =
            "edit event locationABC Meeting from 2023-10-10T09:00 to 2023-10-10T10:00 "
                    + "with Conference";
    Exception exception = assertThrows(
            IllegalArgumentException.class, () -> commandParser.parseCommand(editCommand));
    assertEquals("Unsupported property", exception.getMessage());
  }


  @Test
  void testEditSingleEventNonExistentEvent() {
    String editCommand =
            "edit event name NonExistentEvent from 2023-10-11T14:00 to 2023-10-11T16:00 "
                    + "with UpdatedName";
    assertDoesNotThrow(() -> commandParser.parseCommand(editCommand));

    String printCommand = "print events on 2023-10-11";
    assertDoesNotThrow(() -> commandParser.parseCommand(printCommand));

    assertFalse(outputStream.toString().contains("-Subject :"));
  }

  @Test
  void testCreateRecurringEventAutoDecline() {
    String createSingleCommand = "create event Meeting from 2023-10-10T09:00 to 2023-10-10T10:00";
    assertDoesNotThrow(() -> commandParser.parseCommand(createSingleCommand));

    String createRecurringCommand =
            "create event Standup from 2023-10-10T09:00 to 2023-10-10T09:30 repeats TR for 5";
    Exception exception = assertThrows(
            IllegalArgumentException.class, () -> commandParser.parseCommand(createRecurringCommand)
    );
    assertEquals("Recurring event series conflicts with existing events.",
            exception.getMessage());

    String printCommand = "print events from 2023-10-10T00:00 to 2023-10-20T23:59";
    assertDoesNotThrow(() -> commandParser.parseCommand(printCommand));

    String expectedOutput = "Date: 10/10/2023\n"
            + "  -Subject :  Meeting\n"
            + "  -Description :  \n"
            + "  -Start Time :  2023-10-10T09:00\n"
            + "  -End Time :  2023-10-10T10:00\n";
    assertEquals(expectedOutput, outputStream.toString());
  }

  @Test
  void testCreateRecurringEventForNTimes() {
    String input =
            "create event Standup from 2023-10-10T09:00 to 2023-10-10T09:30 repeats MWF for 5";
    assertDoesNotThrow(() -> commandParser.parseCommand(input));

    String printCommand = "print events from 2023-10-10T00:00 to 2023-10-20T23:59";
    assertDoesNotThrow(() -> commandParser.parseCommand(printCommand));

    String expectedOutput = "Date: 10/11/2023\n"
            + "  -Subject :  Standup\n"
            + "  -Description :  \n"
            + "  -Start Time :  2023-10-11T09:00\n"
            + "  -End Time :  2023-10-11T09:30\n"
            + "Date: 10/13/2023\n"
            + "  -Subject :  Standup\n"
            + "  -Description :  \n"
            + "  -Start Time :  2023-10-13T09:00\n"
            + "  -End Time :  2023-10-13T09:30\n"
            + "Date: 10/16/2023\n"
            + "  -Subject :  Standup\n"
            + "  -Description :  \n"
            + "  -Start Time :  2023-10-16T09:00\n"
            + "  -End Time :  2023-10-16T09:30\n"
            + "Date: 10/18/2023\n"
            + "  -Subject :  Standup\n"
            + "  -Description :  \n"
            + "  -Start Time :  2023-10-18T09:00\n"
            + "  -End Time :  2023-10-18T09:30\n"
            + "Date: 10/20/2023\n"
            + "  -Subject :  Standup\n"
            + "  -Description :  \n"
            + "  -Start Time :  2023-10-20T09:00\n"
            + "  -End Time :  2023-10-20T09:30\n";
    assertEquals(expectedOutput, outputStream.toString());
  }

  @Test
  void testRecurringEventUntil() {
    String input = "create event Review on 2023-10-10 repeats TR until 2023-10-31";
    assertDoesNotThrow(() -> commandParser.parseCommand(input));

    String printCommand = "print events from 2023-10-10T00:00 to 2023-10-31T23:59";
    assertDoesNotThrow(() -> commandParser.parseCommand(printCommand));

    String expectedOutput = "Date: 10/10/2023\n"
            + "  -Subject :  Review\n"
            + "  -Description :  \n"
            + "  -Start Time :  2023-10-10T00:00\n"
            + "  -End Time :  2023-10-10T23:59\n"
            + "Date: 10/12/2023\n"
            + "  -Subject :  Review\n"
            + "  -Description :  \n"
            + "  -Start Time :  2023-10-12T00:00\n"
            + "  -End Time :  2023-10-12T23:59\n"
            + "Date: 10/17/2023\n"
            + "  -Subject :  Review\n"
            + "  -Description :  \n"
            + "  -Start Time :  2023-10-17T00:00\n"
            + "  -End Time :  2023-10-17T23:59\n"
            + "Date: 10/19/2023\n"
            + "  -Subject :  Review\n"
            + "  -Description :  \n"
            + "  -Start Time :  2023-10-19T00:00\n"
            + "  -End Time :  2023-10-19T23:59\n"
            + "Date: 10/24/2023\n"
            + "  -Subject :  Review\n"
            + "  -Description :  \n"
            + "  -Start Time :  2023-10-24T00:00\n"
            + "  -End Time :  2023-10-24T23:59\n"
            + "Date: 10/26/2023\n"
            + "  -Subject :  Review\n"
            + "  -Description :  \n"
            + "  -Start Time :  2023-10-26T00:00\n"
            + "  -End Time :  2023-10-26T23:59\n"
            + "Date: 10/31/2023\n"
            + "  -Subject :  Review\n"
            + "  -Description :  \n"
            + "  -Start Time :  2023-10-31T00:00\n"
            + "  -End Time :  2023-10-31T23:59\n";
    assertEquals(expectedOutput, outputStream.toString());
  }

  @Test
  void testCreateRecurringAllDays() {
    String input =
            "create event DailyCheck from 2023-10-16T09:00 to 2023-10-16T09:30 repeats MTWRFSU "
                    + "for 7";
    assertDoesNotThrow(() -> commandParser.parseCommand(input));

    String printCommand = "print events from 2023-10-16T00:00 to 2023-10-22T23:59";
    assertDoesNotThrow(() -> commandParser.parseCommand(printCommand));

    String output = outputStream.toString();
    assertTrue(output.contains("Date: 10/16/2023")); // Monday
    assertTrue(output.contains("Date: 10/17/2023")); // Tuesday
    assertTrue(output.contains("Date: 10/18/2023")); // Wednesday
    assertTrue(output.contains("Date: 10/19/2023")); // Thursday
    assertTrue(output.contains("Date: 10/20/2023")); // Friday
    assertTrue(output.contains("Date: 10/21/2023")); // Saturday
    assertTrue(output.contains("Date: 10/22/2023")); // Sunday
  }

  @Test
  void testEditRecurringEvent() {
    String createCommand =
            "create event Standup from 2023-10-10T09:00 to 2023-10-10T09:30 repeats MWF for 5";
    assertDoesNotThrow(() -> commandParser.parseCommand(createCommand));

    String editCommand = "edit events description Standup NewDescription";
    assertDoesNotThrow(() -> commandParser.parseCommand(editCommand));

    String printCommand = "print events from 2023-10-10T00:00 to 2023-10-20T23:59";
    assertDoesNotThrow(() -> commandParser.parseCommand(printCommand));

    String expectedOutput = "Date: 10/11/2023\n"
            + "  -Subject :  Standup\n"
            + "  -Description :  NewDescription\n"
            + "  -Start Time :  2023-10-11T09:00\n"
            + "  -End Time :  2023-10-11T09:30\n"
            + "Date: 10/13/2023\n"
            + "  -Subject :  Standup\n"
            + "  -Description :  NewDescription\n"
            + "  -Start Time :  2023-10-13T09:00\n"
            + "  -End Time :  2023-10-13T09:30\n"
            + "Date: 10/16/2023\n"
            + "  -Subject :  Standup\n"
            + "  -Description :  NewDescription\n"
            + "  -Start Time :  2023-10-16T09:00\n"
            + "  -End Time :  2023-10-16T09:30\n"
            + "Date: 10/18/2023\n"
            + "  -Subject :  Standup\n"
            + "  -Description :  NewDescription\n"
            + "  -Start Time :  2023-10-18T09:00\n"
            + "  -End Time :  2023-10-18T09:30\n"
            + "Date: 10/20/2023\n"
            + "  -Subject :  Standup\n"
            + "  -Description :  NewDescription\n"
            + "  -Start Time :  2023-10-20T09:00\n"
            + "  -End Time :  2023-10-20T09:30\n";
    assertEquals(expectedOutput, outputStream.toString());
  }

  @Test
  void testEditFractionOfRecurringEvents() {
    String createCommand =
            "create event Review from 2023-10-10T10:00 to 2023-10-10T11:00 repeats TR "
                    + "until 2023-10-31";
    assertDoesNotThrow(() -> commandParser.parseCommand(createCommand));

    String editCommand = "edit events name Review from 2023-10-17T10:00 with NewName";
    assertDoesNotThrow(() -> commandParser.parseCommand(editCommand));

    String printCommand = "print events from 2023-10-10T00:00 to 2023-10-31T23:59";
    assertDoesNotThrow(() -> commandParser.parseCommand(printCommand));

    String expectedOutput = "Date: 10/10/2023\n"
            + "  -Subject :  Review\n"
            + "  -Description :  \n"
            + "  -Start Time :  2023-10-10T10:00\n"
            + "  -End Time :  2023-10-10T11:00\n"
            + "Date: 10/12/2023\n"
            + "  -Subject :  Review\n"
            + "  -Description :  \n"
            + "  -Start Time :  2023-10-12T10:00\n"
            + "  -End Time :  2023-10-12T11:00\n"
            + "Date: 10/17/2023\n"
            + "  -Subject :  NewName\n"
            + "  -Description :  \n"
            + "  -Start Time :  2023-10-17T10:00\n"
            + "  -End Time :  2023-10-17T11:00\n"
            + "Date: 10/19/2023\n"
            + "  -Subject :  NewName\n"
            + "  -Description :  \n"
            + "  -Start Time :  2023-10-19T10:00\n"
            + "  -End Time :  2023-10-19T11:00\n"
            + "Date: 10/24/2023\n"
            + "  -Subject :  NewName\n"
            + "  -Description :  \n"
            + "  -Start Time :  2023-10-24T10:00\n"
            + "  -End Time :  2023-10-24T11:00\n"
            + "Date: 10/26/2023\n"
            + "  -Subject :  NewName\n"
            + "  -Description :  \n"
            + "  -Start Time :  2023-10-26T10:00\n"
            + "  -End Time :  2023-10-26T11:00\n"
            + "Date: 10/31/2023\n"
            + "  -Subject :  NewName\n"
            + "  -Description :  \n"
            + "  -Start Time :  2023-10-31T10:00\n"
            + "  -End Time :  2023-10-31T11:00\n";
    assertEquals(expectedOutput, outputStream.toString());
  }

  @Test
  void testEditRecurringEventStartTime() {
    String createCommand =
            "create event Standup from 2023-10-10T09:00 to 2023-10-10T09:30 repeats MWF for 5";
    assertDoesNotThrow(() -> commandParser.parseCommand(createCommand));

    String editCommand =
            "edit events startTime Standup from 2023-10-10T09:00 with 2023-10-10T09:15";
    assertDoesNotThrow(() -> commandParser.parseCommand(editCommand));

    String printCommand = "print events from 2023-10-10T00:00 to 2023-10-20T23:59";
    assertDoesNotThrow(() -> commandParser.parseCommand(printCommand));

    String output = outputStream.toString();
    assertTrue(output.contains("-Start Time :  2023-10-11T09:15"));
    assertTrue(output.contains("-Start Time :  2023-10-13T09:15"));
  }

  @Test
  void testEditRecurringEventOccurrences() {
    String createCommand =
            "create event Standup from 2023-10-10T09:00 to 2023-10-10T09:30 repeats MWF for 3";
    assertDoesNotThrow(() -> commandParser.parseCommand(createCommand));

    String editCommand = "edit events occurrences Standup 5";
    assertDoesNotThrow(() -> commandParser.parseCommand(editCommand));

    String printCommand = "print events from 2023-10-10T00:00 to 2023-10-20T23:59";
    assertDoesNotThrow(() -> commandParser.parseCommand(printCommand));

    String output = outputStream.toString();
    int count = 0;
    int index = -1;
    while ((index = output.indexOf("-Subject :  Standup", index + 1)) != -1) {
      count++;
    }
    assertEquals(5, count); // Should have 5 occurrences now
  }

  @Test
  void testEditRecurringEventRecurringDays() {
    String createCommand =
            "create event Standup from 2023-10-10T09:00 to 2023-10-10T09:30 repeats TR for 5";
    assertDoesNotThrow(() -> commandParser.parseCommand(createCommand));

    String editCommand = "edit events recurringDays Standup MWF";
    assertDoesNotThrow(() -> commandParser.parseCommand(editCommand));

    String printCommand = "print events from 2023-10-10T00:00 to 2023-10-20T23:59";
    assertDoesNotThrow(() -> commandParser.parseCommand(printCommand));

    String output = outputStream.toString();
    assertTrue(output.contains("Date: 10/11/2023")); // Wednesday
    assertTrue(output.contains("Date: 10/13/2023")); // Friday
    assertTrue(output.contains("Date: 10/16/2023")); // Monday
    assertTrue(output.contains("Date: 10/18/2023")); // Wednesday
    assertTrue(output.contains("Date: 10/20/2023")); // Friday

    assertFalse(output.contains("Date: 10/10/2023")); // Tuesday
    assertFalse(output.contains("Date: 10/12/2023")); // Thursday
  }

  @Test
  void testPrintEventsOnlyShowsEventsInTimeRange() {
    String createCommand1 = "create event Morning from 2023-10-10T09:00 to 2023-10-10T10:00";
    String createCommand2 = "create event Afternoon from 2023-10-10T14:00 to 2023-10-10T15:00";
    assertDoesNotThrow(() -> commandParser.parseCommand(createCommand1));
    assertDoesNotThrow(() -> commandParser.parseCommand(createCommand2));

    String printCommand = "print events from 2023-10-10T08:00 to 2023-10-10T12:00";
    assertDoesNotThrow(() -> commandParser.parseCommand(printCommand));

    String output = outputStream.toString();
    assertTrue(output.contains("-Subject :  Morning"));
    assertFalse(output.contains("-Subject :  Afternoon"));
  }

  @Test
  void testPrintEventsEmptyDay() {
    String printCommand = "print events on 2023-10-10";
    assertDoesNotThrow(() -> commandParser.parseCommand(printCommand));

    String output = outputStream.toString();
    assertFalse(output.contains("-Subject :"));
  }

  @Test
  void testPrintEventsDateRangeWithNoEvents() {
    String printCommand = "print events from 2023-10-10T00:00 to 2023-10-12T23:59";
    assertDoesNotThrow(() -> commandParser.parseCommand(printCommand));

    String output = outputStream.toString();
    assertFalse(output.contains("-Subject :"));
  }

  @Test
  void testShowStatus() {
    String createCommand = "create event Meeting from 2023-10-10T09:00 to 2023-10-10T10:00";
    assertDoesNotThrow(() -> commandParser.parseCommand(createCommand));

    String statusCommand1 = "show status on 2023-10-10T09:30";
    assertDoesNotThrow(() -> commandParser.parseCommand(statusCommand1));
    assertEquals("busy\n", outputStream.toString());

    outputStream.reset();

    String statusCommand2 = "show status on 2023-10-10T11:00";
    assertDoesNotThrow(() -> commandParser.parseCommand(statusCommand2));
    assertEquals("available\n", outputStream.toString());
  }

  @Test
  void testExportCalCommandEmpty() {
    String exportCommand = "export cal testfile.csv";
    assertDoesNotThrow(() -> commandParser.parseCommand(exportCommand));
  }

  @Test
  void testExportCal() {
    String createCommand = "create event Workshop from 2023-10-11T14:00 to 2023-10-11T16:00";
    assertDoesNotThrow(() -> commandParser.parseCommand(createCommand));

    String exportCommand = "export cal mycalendar.csv";
    assertDoesNotThrow(() -> commandParser.parseCommand(exportCommand));
  }

  // Invalid Tests
  @Test
  void testCreateEventInvalidStartDateAfterEndDate() {
    String input = "create event Meeting from 2023-10-11T09:00 to 2023-10-10T10:00";
    Exception exception = assertThrows(
            IllegalArgumentException.class, () -> commandParser.parseCommand(input));
    assertEquals("Start date cannot be after end date", exception.getMessage());
  }

  @Test
  void testCreateEventInvalidDateFormat() {
    String input = "create event Meeting from 2023-10-1123 to 2023-10-10T10:00";
    Exception exception = assertThrows(
            IllegalArgumentException.class, () -> commandParser.parseCommand(input));
    assertEquals("Invalid date/time format: 2023-10-1123", exception.getMessage());
  }

  @Test
  void testCreateEventMissingToKeyword() {
    String input = "create event Meeting from 2023-10-10T09:00 2023-10-10T10:00";
    Exception exception = assertThrows(
            IllegalArgumentException.class, () -> commandParser.parseCommand(input));
    assertEquals("Expected 'to' after start date/time.", exception.getMessage());
  }

  @Test
  void testCreateEventMissingFromOrOnKeyword() {
    String input = "create event Meeting 2023-10-10T09:00 to 2023-10-10T10:00";
    Exception exception = assertThrows(
            IllegalArgumentException.class, () -> commandParser.parseCommand(input));
    assertEquals("Expected 'from' or 'on' after event name.", exception.getMessage());
  }

  @Test
  void testCreateEventRecurringWithInvalidDays() {
    String input =
            "create event Standup from 2023-10-10T09:00 to 2023-10-10T09:30 repeats XYZ for 5";
    Exception exception = assertThrows(
            IllegalArgumentException.class, () -> commandParser.parseCommand(input));
    assertTrue(exception.getMessage().contains("Invalid day character"));
  }

  @Test
  void testCreateEventRecurringMissingForOrUntil() {
    String input = "create event Standup from 2023-10-10T09:00 to 2023-10-10T09:30 repeats MWF";
    Exception exception = assertThrows(
            IllegalArgumentException.class, () -> commandParser.parseCommand(input));
    assertEquals("Expected 'for' or 'until' after weekdays.", exception.getMessage());
  }

  @Test
  void testEditEventMissingWithKeyword() {
    String input =
            "edit event name Meeting from 2023-10-10T09:00 to 2023-10-10T10:00 UpdatedMeeting";
    Exception exception = assertThrows(
            IllegalArgumentException.class, () -> commandParser.parseCommand(input));
    assertEquals("Expected 'with' after end date/time.", exception.getMessage());
  }

  @Test
  void testPrintEventsInvalidCommandFormat() {
    String input = "print events 2023-10-10";
    Exception exception = assertThrows(
            IllegalArgumentException.class, () -> commandParser.parseCommand(input));
    assertEquals("Expected 'on' or 'from' after 'print events'.", exception.getMessage());
  }

  @Test
  void testShowStatusInvalidDateTimeFormat() {
    String input = "show status on 2023-10-146";
    Exception exception = assertThrows(
            IllegalArgumentException.class, () -> commandParser.parseCommand(input));
    assertEquals("Invalid date/time format: 2023-10-146", exception.getMessage());
  }

  @Test
  void testCommandWithExtraWhitespace() {
    String command =
            "  create   event    Meeting   from    2023-10-10T09:00  to 2023-10-10T10:00  ";
    assertDoesNotThrow(() -> commandParser.parseCommand(command));

    String printCommand = "print events on 2023-10-10";
    assertDoesNotThrow(() -> commandParser.parseCommand(printCommand));

    assertTrue(outputStream.toString().contains("-Subject :  Meeting"));
  }

  @Test
  void testInvalidCommandStructure() {
    String command = "create";
    Exception exception = assertThrows(
            IllegalArgumentException.class, () -> commandParser.parseCommand(command));
    assertEquals("Invalid command format.", exception.getMessage());
  }

  @Test
  void testUnknownCommand() {
    String input = "unknown command";
    Exception exception = assertThrows(
            IllegalArgumentException.class, () -> commandParser.parseCommand(input));
    assertEquals("Unknown command: unknown command", exception.getMessage());
  }

  @Test
  void testEmptyCommand() {
    String input = "";
    Exception exception = assertThrows(
            IllegalArgumentException.class, () -> commandParser.parseCommand(input));
    assertEquals("Input command cannot be empty.", exception.getMessage());
  }

  @Test
  void testNullCommand() {
    Exception exception = assertThrows(
            IllegalArgumentException.class, () -> commandParser.parseCommand(null));
    assertEquals("Input command cannot be empty.", exception.getMessage());
  }
}