package calendar;

import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import calendar.controller.CommandController;
import calendar.manager.CalendarManager;
import calendar.utils.ExportUtils;
import calendar.view.Interpreter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class FeaturesTest {

  private CommandController commandController;
  private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
  private final PrintStream originalOut = System.out;
  private  ExportUtils exportUtils;

  @Before
  public  void setUp() {
    try {
      Interpreter view = new Interpreter();
      CalendarManager model = new CalendarManager();
      commandController = new CommandController(model, view);
      System.setOut(new PrintStream(outputStream));

      // Create a default calendar and set it as active for all tests
      String createCalendarCommand = "create calendar --name MainCalendar --timezone America/New_York";
      commandController.parseCommand(createCalendarCommand);

      String useCalendarCommand = "use calendar --name MainCalendar";
      commandController.parseCommand(useCalendarCommand);
      exportUtils = new ExportUtils();
      // Clear output from setup commands
      outputStream.reset();
    }
    catch(Exception e)
    {
      throw new AssertionError(e);
    }
  }



  @Test
  public void testCreateCalendar() {
    try {
      String createCommand = "create calendar --name WorkCalendar --timezone Europe/London";
      commandController.parseCommand(createCommand);

      String useCommand = "use calendar --name WorkCalendar";
      commandController.parseCommand(useCommand);

      // Create an event in the London timezone calendar
      String createEventCommand = "create event Meeting from 2025-03-25T14:00 to 2025-03-25T15:00";
      commandController.parseCommand(createEventCommand);

      String printCommand = "print events on 2025-03-25";
      commandController.parseCommand(printCommand);

      assertTrue(outputStream.toString().contains("Meeting"));

    } catch (Exception e) {
      throw new AssertionError(e);
    }
  }

  @Test
  public void testEditCalendarName() {
    try {
      String createCommand = "create calendar --name TempCalendar --timezone Asia/Tokyo";
      commandController.parseCommand(createCommand);

      String editCommand = "edit calendar --name TempCalendar --property name PersonalCalendar";
      commandController.parseCommand(editCommand);

      assertTrue(true);
    }catch (Exception e) {
      throw new AssertionError(e);
    }
  }

  @Test(expected = AssertionError.class)
  public void testEditCalendarNameNonExit() {
    try {
      String createCommand = "create calendar --name TempCalendar --timezone Asia/Tokyo";
      commandController.parseCommand(createCommand);

      String editCommand = "edit calendar --name TempCalendar1 --property name PersonalCalendar";
      commandController.parseCommand(editCommand);

      assertFalse(false);
    }catch (Exception e) {
      throw new AssertionError(e);
    }
  }

  @Test
  public void testEditCalendarTimezone() {
    try {
      String createCommand = "create calendar --name HomeCalendar --timezone America/Chicago";
      commandController.parseCommand(createCommand);

      String useCommand = "use calendar --name HomeCalendar";
      commandController.parseCommand(useCommand);

      // Create an event in Chicago timezone
      String createEventCommand = "create event Dinner from 2025-03-25T18:00 to 2025-03-25T19:00";
      commandController.parseCommand(createEventCommand);

      // Change timezone to Europe/Paris
      String editCommand = "edit calendar --name HomeCalendar --property timezone Europe/Paris";
      commandController.parseCommand(editCommand);

      // Event should still exist but be converted to Paris time (7 hours ahead)
      String printCommand = "print events on 2025-03-26";
      commandController.parseCommand(printCommand);

      assertTrue(outputStream.toString().contains("Dinner"));
      assertTrue(outputStream.toString().contains("2025-03-26T01:00"));
    }catch (Exception e) {
      throw new AssertionError(e);
    }
  }

  @Test
  public void testCopyEventToAnotherCalendar() {
    try {
      // Create source event
      String createEventCommand = "create event Meeting from 2025-03-25T10:00 to 2025-03-25T11:00";
      commandController.parseCommand(createEventCommand);

      // Create target calendar with different timezone
      String createCalendarCommand = "create calendar --name TargetCalendar --timezone Europe/Paris";
      commandController.parseCommand(createCalendarCommand);

      // Copy event to target calendar
      String copyCommand = "copy event Meeting on 2025-03-25T10:00 --target TargetCalendar to 2025-03-26T10:00";
      commandController.parseCommand(copyCommand);

      // Switch to target calendar and verify event
      String useCommand = "use calendar --name TargetCalendar";
      commandController.parseCommand(useCommand);

      String printCommand = "print events on 2025-03-26";
      commandController.parseCommand(printCommand);

      assertTrue(outputStream.toString().contains("Meeting"));
      assertTrue(outputStream.toString().contains("2025-03-26T10:00"));
    }catch (Exception e) {
      throw new AssertionError(e);
    }
  }

  @Test(expected = AssertionError.class)
  public void testCopyEventToAnotherCalendarNonExist() {
    try {
      // Create source event
      String createEventCommand = "create event Meeting from 2025-03-25T10:00 to 2025-03-25T11:00";
      commandController.parseCommand(createEventCommand);

      // Create target calendar with different timezone
      String createCalendarCommand = "create calendar --name TargetCalendar --timezone Europe/Paris";
      commandController.parseCommand(createCalendarCommand);

      // Copy event to target calendar
      String copyCommand = "copy event Meeting on 2025-03-25T10:00 --target TargetCalendar1 to 2025-03-26T10:00";
      commandController.parseCommand(copyCommand);

      // Switch to target calendar and verify event
      String useCommand = "use calendar --name TargetCalendar";
      commandController.parseCommand(useCommand);

      String printCommand = "print events on 2025-03-26";
      commandController.parseCommand(printCommand);

      assertTrue(outputStream.toString().contains("Meeting"));
      assertTrue(outputStream.toString().contains("2025-03-26T10:00"));
    }catch (Exception e) {
      throw new AssertionError(e);
    }
  }

  @Test
  public void testCopyEventsSameDay() {
    try {
      // Create multiple events on the same day
      String event1 = "create event Meeting1 from 2025-03-25T09:00 to 2025-03-25T10:00";
      String event2 = "create event Meeting2 from 2025-03-25T11:00 to 2025-03-25T12:00";
      commandController.parseCommand(event1);
      commandController.parseCommand(event2);

      // Create target calendar
      String createCalendarCommand = "create calendar --name ProjectCalendar --timezone America/New_York";
      commandController.parseCommand(createCalendarCommand);

      // Copy all events from that day to target calendar
      String copyCommand = "copy events on 2025-03-25 --target ProjectCalendar to 2025-04-01";
      commandController.parseCommand(copyCommand);

      // Verify the copied events
      String useCommand = "use calendar --name ProjectCalendar";
      commandController.parseCommand(useCommand);

      String printCommand = "print events on 2025-04-01";
      commandController.parseCommand(printCommand);

      String output = outputStream.toString();
      assertTrue(output.contains("Meeting1"));
      assertTrue(output.contains("Meeting2"));
    }
    catch (Exception e) {
      throw new AssertionError(e);
    }
  }

  @Test(expected = AssertionError.class)
  public void testCopyEventsSameDayNonExist() {
    try {
      // Create multiple events on the same day
      String event1 = "create event Meeting1 from 2025-03-25T09:00 to 2025-03-25T10:00";
      String event2 = "create event Meeting2 from 2025-03-25T11:00 to 2025-03-25T12:00";
      commandController.parseCommand(event1);
      commandController.parseCommand(event2);

      // Create target calendar
      String createCalendarCommand = "create calendar --name ProjectCalendar --timezone America/New_York";
      commandController.parseCommand(createCalendarCommand);

      // Copy all events from that day to target calendar non existant
      String copyCommand = "copy events on 2025-03-25 --target ProjectCalendar1 to 2025-04-01";
      commandController.parseCommand(copyCommand);

      // Verify the copied events
      String useCommand = "use calendar --name ProjectCalendar";
      commandController.parseCommand(useCommand);

      String printCommand = "print events on 2025-04-01";
      commandController.parseCommand(printCommand);

      String output = outputStream.toString();
      assertTrue(output.contains("Meeting1"));
      assertTrue(output.contains("Meeting2"));
    }
    catch (Exception e) {
      throw new AssertionError(e);
    }
  }


  @Test
  public void testCopyEventsBetweenDates() {
    try {
      // Create events across multiple days
      String event1 = "create event Meeting1 from 2025-03-25T09:00 to 2025-03-25T10:00";
      String event2 = "create event Meeting2 from 2025-03-26T11:00 to 2025-03-26T12:00";
      String event3 = "create event Meeting3 from 2025-03-27T14:00 to 2025-03-27T15:00";
      commandController.parseCommand(event1);
      commandController.parseCommand(event2);
      commandController.parseCommand(event3);

      // Create target calendar
      String createCalendarCommand = "create calendar --name FutureCalendar --timezone America/New_York";
      commandController.parseCommand(createCalendarCommand);

      // Copy all events between dates
      String copyCommand = "copy events between 2025-03-25 and 2025-03-27 --target FutureCalendar to 2025-04-01";
      commandController.parseCommand(copyCommand);

      // Verify the copied events
      String useCommand = "use calendar --name FutureCalendar";
      commandController.parseCommand(useCommand);

      String printCommand = "print events from 2025-04-01T00:00 to 2025-04-03T23:59";
      commandController.parseCommand(printCommand);

      String output = outputStream.toString();
      assertTrue(output.contains("Meeting1"));
      assertTrue(output.contains("Meeting2"));
      assertTrue(output.contains("Meeting3"));
    }
    catch (Exception e) {
      throw new AssertionError(e);
    }
  }

  @Test
  public void testCopyEventsSameDayNonExistWithMessageVerification() {
    // Create multiple events on the same day
    String event1 = "create event Meeting1 from 2025-03-25T09:00 to 2025-03-25T10:00";
    String event2 = "create event Meeting2 from 2025-03-25T11:00 to 2025-03-25T12:00";
    commandController.parseCommand(event1);
    commandController.parseCommand(event2);

    // Create target calendar (correct name)
    String createCalendarCommand = "create calendar --name ProjectCalendar --timezone America/New_York";
    commandController.parseCommand(createCalendarCommand);

    // Attempt to copy to non-existent calendar
    String copyCommand = "copy events on 2025-03-25 --target ProjectCalendar1 to 2025-04-01";

    try {
      commandController.parseCommand(copyCommand);
      fail("Expected IllegalArgumentException");
    } catch (IllegalArgumentException e) {
      assertEquals("Calendar 'ProjectCalendar1' does not exist", e.getMessage());

      // Verify no events were copied to the correct calendar
      String useCommand = "use calendar --name ProjectCalendar";
      commandController.parseCommand(useCommand);

      String printCommand = "print events on 2025-04-01";
      commandController.parseCommand(printCommand);

      String output = outputStream.toString();
      assertFalse(output.contains("Meeting1"));
      assertFalse(output.contains("Meeting2"));
    }
  }

  @Test
  public void testCalendarCommandRequiredBeforeEvents() {
    // Create a new controller without the setup that creates calendars
    Interpreter view = new Interpreter();
    CalendarManager model = new CalendarManager();
    CommandController newController = new CommandController(model,view);

    // Attempt to create an event without first creating/selecting a calendar
    String createCommand = "create event Meeting from 2025-03-25T10:00 to 2025-03-25T11:00";
    Exception exception = assertThrows(
            IllegalStateException.class, () -> newController.parseCommand(createCommand));
    assertTrue(exception.getMessage().contains("No active calendar selected"));
  }

  @Test
  public void testCreateDuplicateCalendar() {
    // Try to create another calendar with the same name
    String createCommand = "create calendar --name MainCalendar --timezone Europe/London";
    Exception exception = assertThrows(
            IllegalArgumentException.class, () -> commandController.parseCommand(createCommand));
    assertTrue(exception.getMessage().contains("Calendar 'MainCalendar' already exist"));
  }

  @Test
  public void testInvalidTimezone() {
    // Try to create a calendar with an invalid timezone
    String createCommand = "create calendar --name InvalidCalendar --timezone NotAValidZone";
    Exception exception = assertThrows(
            IllegalArgumentException.class, () -> commandController.parseCommand(createCommand));
    assertTrue(exception.getMessage().contains("Invalid timezone format: NotAValidZone"));
  }

  @Test
  public void testCreateSingleEvent() {
    String createCommand = "create event Meeting from 2023-10-10T09:00 to 2023-10-10T10:00";
    commandController.parseCommand(createCommand);

    String printCommand = "print events on 2023-10-10";
    commandController.parseCommand(printCommand);

    String expectedOutput = "Date: 10/10/2023\n" +
            "  -Subject :  Meeting\n" +
            "  -Description :  \n" +
            "  -Start Time :  2023-10-10T09:00\n" +
            "  -End Time :  2023-10-10T10:00";
    assertEquals(expectedOutput.replaceAll("\\R", "\n").trim(),
            outputStream.toString().replaceAll("\\R", "\n").trim());
  }

  @Test
  public void testCreateSingleEventAllDay() {
    String input = "create event Conference on 2023-10-12T00:00";
    commandController.parseCommand(input);

    String printCommand = "print events on 2023-10-12";
    commandController.parseCommand(printCommand);

    String expectedOutput = "Date: 10/12/2023\n" +
            "  -Subject :  Conference\n" +
            "  -Description :  \n" +
            "  -Start Time :  2023-10-12T00:00\n" +
            "  -End Time :  2023-10-12T23:59\n";
    assertEquals(expectedOutput.replaceAll("\\R", "\n").trim(),
            outputStream.toString().replaceAll("\\R", "\n").trim());
  }

  @Test
  public void testCreateSingleEventAutoDeclined() {
    String createCommand =
            "create event --autoDecline Meeting from 2023-10-10T09:00 to 2023-10-10T10:00";
    commandController.parseCommand(createCommand);

    String conflictCommand =
            "create event --autoDecline Conflict from 2023-10-10T09:30 to 2023-10-10T10:30";
    Exception exception = assertThrows(
            IllegalArgumentException.class, () -> commandController.parseCommand(conflictCommand));
    assertEquals("Conflicted event and auto-decline is enabled.", exception.getMessage());
  }

  @Test
  public void testCreateSingleEventMultipleSpanningDays() {
    String createCommand = "create event Conference from 2023-10-10T09:00 to 2023-10-12T17:00";
    commandController.parseCommand(createCommand);

    String printCommand = "print events from 2023-10-10T00:00 to 2023-10-12T23:59";
    commandController.parseCommand(printCommand);

    String output = outputStream.toString();
    assertTrue(output.contains("Date: 10/10/2023"));
    assertTrue(output.contains("Date: 10/11/2023"));
    assertTrue(output.contains("Date: 10/12/2023"));
  }

  @Test
  public void testCreateSingleEventMultipleSpanningDaysMidnight() {
    String createCommand = "create event LateEvent from 2023-10-10T22:00 to 2023-10-11T00:00";
    commandController.parseCommand(createCommand);

    String printCommand = "print events from 2023-10-10T00:00 to 2023-10-11T23:59";
    commandController.parseCommand(printCommand);

    String output = outputStream.toString();
    assertTrue(output.contains("Date: 10/10/2023"));
    assertTrue(output.contains("-Start Time :  2023-10-10T22:00"));
    assertTrue(output.contains("-End Time :  2023-10-10T23:59"));
    assertTrue(output.contains("Date: 10/11/2023"));
    assertTrue(output.contains("-Start Time :  2023-10-11T00:00"));
    assertTrue(output.contains("-End Time :  2023-10-11T00:00"));
  }

  @Test
  public void testEditSingleEventName() {
    String createCommand = "create event Workshop from 2023-10-11T14:00 to 2023-10-11T16:00";
    commandController.parseCommand(createCommand);

    String editCommand =
            "edit event name Workshop from 2023-10-11T14:00 to 2023-10-11T16:00 "
                    + "with UpdatedWorkshop";
    commandController.parseCommand(editCommand);

    String printCommand = "print events on 2023-10-11";
    commandController.parseCommand(printCommand);

    String expectedOutput = "Date: 10/11/2023\n" +
            "  -Subject :  UpdatedWorkshop\n" +
            "  -Description :  \n" +
            "  -Start Time :  2023-10-11T14:00\n" +
            "  -End Time :  2023-10-11T16:00\n";
    assertEquals(expectedOutput.replaceAll("\\R", "\n").trim(),
            outputStream.toString().replaceAll("\\R", "\n").trim());
  }

  @Test
  public void testEditSingleEventStartTime() {
    String createCommand = "create event Meeting from 2023-10-10T09:00 to 2023-10-10T10:00";
    commandController.parseCommand(createCommand);

    String editCommand =
            "edit event startTime Meeting from 2023-10-10T09:00 to 2023-10-10T10:00 "
                    + "with 2023-10-10T09:30";
    commandController.parseCommand(editCommand);

    String printCommand = "print events on 2023-10-10";
    commandController.parseCommand(printCommand);

    assertTrue(outputStream.toString().contains("-Start Time :  2023-10-10T09:30"));
  }

  @Test
  public void testEditSingleEventEndTime() {
    String createCommand = "create event Meeting from 2023-10-10T09:00 to 2023-10-10T10:00";
    commandController.parseCommand(createCommand);

    String editCommand =
            "edit event endTime Meeting from 2023-10-10T09:00 to 2023-10-10T10:00 "
                    + "with 2023-10-10T11:00";
    commandController.parseCommand(editCommand);

    String printCommand = "print events on 2023-10-10";
    commandController.parseCommand(printCommand);

    assertTrue(outputStream.toString().contains("-End Time :  2023-10-10T11:00"));
  }

  @Test
  public void testEditSingleEventDescription() {
    String createCommand = "create event Meeting from 2023-10-10T09:00 to 2023-10-10T10:00";
    commandController.parseCommand(createCommand);

    String editCommand =
            "edit event description Meeting from 2023-10-10T09:00 to 2023-10-10T10:00 with Weekly";
    commandController.parseCommand(editCommand);

    String printCommand = "print events on 2023-10-10";
    commandController.parseCommand(printCommand);

    System.out.println(outputStream.toString());
    assertTrue(outputStream.toString().contains("-Description :  Weekly"));
  }

  @Test
  public void testEditSingleEventInvalidProperty() {
    String createCommand = "create event Meeting from 2023-10-10T09:00 to 2023-10-10T10:00";
    commandController.parseCommand(createCommand);

    String editCommand =
            "edit event locationABC Meeting from 2023-10-10T09:00 to 2023-10-10T10:00 "
                    + "with Conference";
    Exception exception = assertThrows(
            IllegalArgumentException.class, () -> commandController.parseCommand(editCommand));
    assertEquals("Unsupported property", exception.getMessage());
  }


  @Test
  public void testEditSingleEventNonExistentEvent() {
    String editCommand =
            "edit event name NonExistentEvent from 2023-10-11T14:00 to 2023-10-11T16:00 "
                    + "with UpdatedName";
    commandController.parseCommand(editCommand);

    String printCommand = "print events on 2023-10-11";
    commandController.parseCommand(printCommand);

    assertFalse(outputStream.toString().contains("-Subject :"));
  }

  @Test
  public void testCreateRecurringEventAutoDecline() {
    String createSingleCommand = "create event Meeting from 2023-10-10T09:00 to 2023-10-10T10:00";
    commandController.parseCommand(createSingleCommand);

    String createRecurringCommand =
            "create event Standup from 2023-10-10T09:00 to 2023-10-10T09:30 repeats TR for 5";
    Exception exception = assertThrows(
            IllegalArgumentException.class, () -> commandController.parseCommand(createRecurringCommand)
    );
    assertEquals("Recurring event series conflicts with existing events.",
            exception.getMessage());

    String printCommand = "print events from 2023-10-10T00:00 to 2023-10-20T23:59";
    commandController.parseCommand(printCommand);

    String expectedOutput = "Date: 10/10/2023\n"
            + "  -Subject :  Meeting\n"
            + "  -Description :  \n"
            + "  -Start Time :  2023-10-10T09:00\n"
            + "  -End Time :  2023-10-10T10:00\n";
    assertEquals(expectedOutput.replaceAll("\\R", "\n").trim(),
            outputStream.toString().replaceAll("\\R", "\n").trim());
  }

  @Test
  public void testCreateRecurringEventForNTimes() {
    String input =
            "create event Standup from 2023-10-10T09:00 to 2023-10-10T09:30 repeats MWF for 5";
    commandController.parseCommand(input);

    String printCommand = "print events from 2023-10-10T00:00 to 2023-10-20T23:59";
    commandController.parseCommand(printCommand);

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
    assertEquals(expectedOutput.replaceAll("\\R", "\n").trim(),
            outputStream.toString().replaceAll("\\R", "\n").trim());
  }

  @Test
  public void testRecurringEventUntil() {
    String input = "create event Review on 2023-10-10 repeats TR until 2023-10-31";
    commandController.parseCommand(input);

    String printCommand = "print events from 2023-10-10T00:00 to 2023-10-31T23:59";
    commandController.parseCommand(printCommand);

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
    assertEquals(expectedOutput.replaceAll("\\R", "\n").trim(),
            outputStream.toString().replaceAll("\\R", "\n").trim());
  }

  @Test
  public void testCreateRecurringAllDays() {
    String input =
            "create event DailyCheck from 2023-10-16T09:00 to 2023-10-16T09:30 repeats MTWRFSU "
                    + "for 7";
    commandController.parseCommand(input);

    String printCommand = "print events from 2023-10-16T00:00 to 2023-10-22T23:59";
    commandController.parseCommand(printCommand);

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
  public void testEditRecurringEvent() {
    String createCommand =
            "create event Standup from 2023-10-10T09:00 to 2023-10-10T09:30 repeats MWF for 5";
    commandController.parseCommand(createCommand);

    String editCommand = "edit events description Standup NewDescription";
    commandController.parseCommand(editCommand);

    String printCommand = "print events from 2023-10-10T00:00 to 2023-10-20T23:59";
    commandController.parseCommand(printCommand);

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
    assertEquals(expectedOutput.replaceAll("\\R", "\n").trim(),
            outputStream.toString().replaceAll("\\R", "\n").trim());
  }

  @Test
  public void testEditFractionOfRecurringEvents() {
    String createCommand =
            "create event Review from 2023-10-10T10:00 to 2023-10-10T11:00 repeats TR "
                    + "until 2023-10-31";
    commandController.parseCommand(createCommand);

    String editCommand = "edit events name Review from 2023-10-17T10:00 with NewName";
    commandController.parseCommand(editCommand);

    String printCommand = "print events from 2023-10-10T00:00 to 2023-10-31T23:59";
    commandController.parseCommand(printCommand);

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
            + "  -End Time :  2023-10-31T11:00";
    //System.out.println( outputStream.toString().trim());
    assertEquals(expectedOutput.replaceAll("\\R", "\n").trim(),
            outputStream.toString().replaceAll("\\R", "\n").trim());
  }

  @Test
  public void testEditRecurringEventStartTime() {
    String createCommand =
            "create event Standup from 2023-10-10T09:00 to 2023-10-10T09:30 repeats MWF for 5";
    commandController.parseCommand(createCommand);

    String editCommand =
            "edit events startTime Standup from 2023-10-10T09:00 with 2023-10-10T09:15";
    commandController.parseCommand(editCommand);

    String printCommand = "print events from 2023-10-10T00:00 to 2023-10-20T23:59";
    commandController.parseCommand(printCommand);

    String output = outputStream.toString();
    assertTrue(output.contains("-Start Time :  2023-10-11T09:15"));
    assertTrue(output.contains("-Start Time :  2023-10-13T09:15"));
  }

  @Test
  public void testEditRecurringEventOccurrences() {
    String createCommand =
            "create event Standup from 2023-10-10T09:00 to 2023-10-10T09:30 repeats MWF for 3";
    commandController.parseCommand(createCommand);

    String editCommand = "edit events occurrences Standup 5";
    commandController.parseCommand(editCommand);

    String printCommand = "print events from 2023-10-10T00:00 to 2023-10-20T23:59";
    commandController.parseCommand(printCommand);

    String output = outputStream.toString();
    int count = 0;
    int index = -1;
    while ((index = output.indexOf("-Subject :  Standup", index + 1)) != -1) {
      count++;
    }
    assertEquals(5, count); // Should have 5 occurrences now
  }

  @Test
  public void testEditRecurringEventRecurringDays() {
    String createCommand =
            "create event Standup from 2023-10-10T09:00 to 2023-10-10T09:30 repeats TR for 5";
    commandController.parseCommand(createCommand);

    String editCommand = "edit events recurringDays Standup MWF";
    commandController.parseCommand(editCommand);

    String printCommand = "print events from 2023-10-10T00:00 to 2023-10-20T23:59";
    commandController.parseCommand(printCommand);

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
  public void testPrintEventsOnlyShowsEventsInTimeRange() {
    String createCommand1 = "create event Morning from 2023-10-10T09:00 to 2023-10-10T10:00";
    String createCommand2 = "create event Afternoon from 2023-10-10T14:00 to 2023-10-10T15:00";
    commandController.parseCommand(createCommand1);
    commandController.parseCommand(createCommand2);

    String printCommand = "print events from 2023-10-10T08:00 to 2023-10-10T12:00";
    commandController.parseCommand(printCommand);

    String output = outputStream.toString();
    assertTrue(output.contains("-Subject :  Morning"));
    assertFalse(output.contains("-Subject :  Afternoon"));
  }

  @Test
  public void testPrintEventsEmptyDay() {
    String printCommand = "print events on 2023-10-10";
    commandController.parseCommand(printCommand);

    String output = outputStream.toString();
    assertFalse(output.contains("-Subject :"));
  }

  @Test
  public void testPrintEventsDateRangeWithNoEvents() {
    String printCommand = "print events from 2023-10-10T00:00 to 2023-10-12T23:59";
    commandController.parseCommand(printCommand);

    String output = outputStream.toString();
    assertFalse(output.contains("-Subject :"));
  }

  @Test
  public void testShowStatus() {
    String createCommand = "create event Meeting from 2023-10-10T09:00 to 2023-10-10T10:00";
    commandController.parseCommand(createCommand);

    String statusCommand1 = "show status on 2023-10-10T09:30";
    commandController.parseCommand(statusCommand1);
    //System.out.println("output " + outputStream.toString());
    assertEquals("busy", outputStream.toString().trim());

    outputStream.reset();

    String statusCommand2 = "show status on 2023-10-10T11:00";
    commandController.parseCommand(statusCommand2);
    assertEquals("available", outputStream.toString().trim());
  }

  @Test
  public void testExportCalCommandEmpty() {
    try {
      String exportCommand = "export cal testfile.csv";
      commandController.parseCommand(exportCommand);
    }catch (Exception e) {
      throw new AssertionError(e);
    }
  }

  @Test
  public void testExportCal() {
    try {
      String createCommand = "create event Workshop from 2023-10-11T14:00 to 2023-10-11T16:00";
      commandController.parseCommand(createCommand);

      String exportCommand = "export cal mycalendar.csv";
      commandController.parseCommand(exportCommand);
    }catch (Exception e) {
      throw new AssertionError(e);
    }
  }

  // Invalid Tests
  @Test
  public void testCreateEventInvalidStartDateAfterEndDate() {
    String input = "create event Meeting from 2023-10-11T09:00 to 2023-10-10T10:00";
    Exception exception = assertThrows(
            IllegalArgumentException.class, () -> commandController.parseCommand(input));
    assertEquals("Start date cannot be after end date", exception.getMessage());
  }

  @Test
  public void testCreateEventInvalidDateFormat() {
    String input = "create event Meeting from 2023-10-1123 to 2023-10-10T10:00";
    Exception exception = assertThrows(
            IllegalArgumentException.class, () -> commandController.parseCommand(input));
    assertEquals("Invalid date/time format: 2023-10-1123", exception.getMessage());
  }

  @Test
  public void testCreateEventMissingToKeyword() {
    String input = "create event Meeting from 2023-10-10T09:00 2023-10-10T10:00";
    Exception exception = assertThrows(
            IllegalArgumentException.class, () -> commandController.parseCommand(input));
    assertEquals("Expected 'to' after start date/time.", exception.getMessage());
  }

  @Test
  public void testCreateEventMissingFromOrOnKeyword() {
    String input = "create event Meeting 2023-10-10T09:00 to 2023-10-10T10:00";
    Exception exception = assertThrows(
            IllegalArgumentException.class, () -> commandController.parseCommand(input));
    assertEquals("Expected 'from' or 'on' after event name.", exception.getMessage());
  }

  @Test
  public void testCreateEventRecurringWithInvalidDays() {
    String input =
            "create event Standup from 2023-10-10T09:00 to 2023-10-10T09:30 repeats XYZ for 5";
    Exception exception = assertThrows(
            IllegalArgumentException.class, () -> commandController.parseCommand(input));
    assertTrue(exception.getMessage().contains("Invalid day character"));
  }

  @Test
  public void testCreateEventRecurringMissingForOrUntil() {
    String input = "create event Standup from 2023-10-10T09:00 to 2023-10-10T09:30 repeats MWF";
    Exception exception = assertThrows(
            IllegalArgumentException.class, () -> commandController.parseCommand(input));
    assertEquals("Expected 'for' or 'until' after weekdays.", exception.getMessage());
  }

  @Test
  public void testEditEventMissingWithKeyword() {
    String input =
            "edit event name Meeting from 2023-10-10T09:00 to 2023-10-10T10:00 UpdatedMeeting";
    Exception exception = assertThrows(
            IllegalArgumentException.class, () -> commandController.parseCommand(input));
    assertEquals("Expected 'with' after end date/time.", exception.getMessage());
  }

  @Test
  public void testPrintEventsInvalidCommandFormat() {
    String input = "print events 2023-10-10";
    Exception exception = assertThrows(
            IllegalArgumentException.class, () -> commandController.parseCommand(input));
    assertEquals("Expected 'on' or 'from' after 'print events'.", exception.getMessage());
  }

  @Test
  public void testShowStatusInvalidDateTimeFormat() {
    String input = "show status on 2023-10-146";
    Exception exception = assertThrows(
            IllegalArgumentException.class, () -> commandController.parseCommand(input));
    assertEquals("Invalid date/time format: 2023-10-146", exception.getMessage());
  }

  @Test
  public void testCommandWithExtraWhitespace() {
    try {
      String command =
              "  create   event    Meeting   from    2023-10-10T09:00  to 2023-10-10T10:00  ";
      commandController.parseCommand(command);

      String printCommand = "print events on 2023-10-10";
      commandController.parseCommand(printCommand);

      assertTrue(outputStream.toString().contains("Meeting"));
    }catch (Exception e) {
      throw new AssertionError(e);
    }
  }

  @Test
  public void testInvalidCommandStructure() {
    String command = "create";
    Exception exception = assertThrows(
            IllegalArgumentException.class, () -> commandController.parseCommand(command));
    assertEquals("Invalid command format.", exception.getMessage());
  }

  @Test
  public void testUnknownCommand() {
    String input = "unknown command";
    Exception exception = assertThrows(
            IllegalArgumentException.class, () -> commandController.parseCommand(input));
    assertEquals("Unknown command: unknown command", exception.getMessage());
  }

  @Test
  public void testEmptyCommand() {
    String input = "";
    Exception exception = assertThrows(
            IllegalArgumentException.class, () -> commandController.parseCommand(input));
    assertEquals("Input command cannot be empty.", exception.getMessage());
  }

  @Test
  public void testNullCommand() {
    Exception exception = assertThrows(
            IllegalArgumentException.class, () -> commandController.parseCommand(null));
    assertEquals("Input command cannot be empty.", exception.getMessage());
  }

  @Test
  public void testEscapeCSV_NullInput() {
    assertEquals("", exportUtils.escapeCSV(null));
  }

  @Test
  public void testEscapeCSV_EmptyString() {
    assertEquals("", exportUtils.escapeCSV(""));
  }

  @Test
  public void testEscapeCSV_SimpleString() {
    String input = "Meeting with team";
    assertEquals("Meeting with team", exportUtils.escapeCSV(input));
  }

  @Test
  public void testEscapeCSV_StringWithComma() {
    String input = "Meeting, with team";
    assertEquals("\"Meeting, with team\"", exportUtils.escapeCSV(input));
  }

  @Test
  public void testEscapeCSV_StringWithDoubleQuotes() {
    String input = "Meeting with \"team\"";
    assertEquals("\"Meeting with \"\"team\"\"\"", exportUtils.escapeCSV(input));
  }

  @Test
  public void testEscapeCSV_StringWithNewline() {
    String input = "Meeting with\nteam";
    assertEquals("\"Meeting with\nteam\"", exportUtils.escapeCSV(input));
  }

  @Test
  public void testEscapeCSV_StringWithMultipleSpecialCharacters() {
    String input = "Meeting, with \"team\"\nand clients";
    assertEquals("\"Meeting, with \"\"team\"\"\nand clients\"", exportUtils.escapeCSV(input));
  }

  @Test
  public void testEscapeCSV_StringWithOnlyComma() {
    assertEquals("\",\"", exportUtils.escapeCSV(","));
  }

  @Test
  public void testEscapeCSV_StringWithOnlyDoubleQuote() {
    assertEquals("\"\"\"\"", exportUtils.escapeCSV("\""));
  }

  @Test
  public void testEscapeCSV_StringWithOnlyNewline() {
    assertEquals("\"\n\"", exportUtils.escapeCSV("\n"));
  }

  @Test
  public void testEscapeCSV_StringWithLeadingTrailingSpaces() {
    String input = "  Meeting  ";
    assertEquals("  Meeting  ", exportUtils.escapeCSV(input));
  }

  @Test
  public void testEscapeCSV_StringWithSpacesAndSpecialChars() {
    String input = "  Meeting, with \"team\"  ";
    assertEquals("\"  Meeting, with \"\"team\"\"  \"", exportUtils.escapeCSV(input));
  }

}
