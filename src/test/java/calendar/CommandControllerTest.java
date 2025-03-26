package calendar;

import static org.junit.Assert.*;

import java.time.LocalDateTime;
import java.time.ZoneId;

import calendar.controller.CommandController;

import org.junit.Before;
import org.junit.Test;

/**
 * Test for the CommandController, checking its functions.
 */
public class CommandControllerTest {
  private CommandController commandController;
  private MockCalendarManager mockManager;
  private MockInterpreter mockInterpreter;

  /**
   * Setting up the required base inputs used by the tests.
   */
  @Before
  public void setUp() {
    mockManager = new MockCalendarManager();
    mockInterpreter = new MockInterpreter();
    commandController = new CommandController(mockManager, mockInterpreter);
  }

  /**
   * Test to check the correct ParseCreateCalendar method.
   */
  @Test
  public void testParseCreateCalendarCommand() {
    String input = "create calendar --name TestCalendar --timezone America/New_York";
    commandController.parseCommand(input);

    assertEquals("createCalendar", mockManager.lastMethodCalled);
    assertEquals("TestCalendar", mockManager.lastMethodArgs[0]);
    assertEquals(ZoneId.of("America/New_York"), mockManager.lastMethodArgs[1]);
  }

  /**
   * Test to check the correct ParseUseCalendar method.
   */
  @Test
  public void testParseUseCalendarCommand() {
    String input = "use calendar --name TestCalendar";
    commandController.parseCommand(input);

    assertEquals("useCalendar", mockManager.lastMethodCalled);
    assertEquals("TestCalendar", mockManager.lastMethodArgs[0]);
  }

  /**
   * Test to check the correct ParseEditCalendar method.
   */
  @Test
  public void testParseEditCalendarCommand() {
    String input = "edit calendar --name TestCalendar --property timezone Asia/Kolkata";
    commandController.parseCommand(input);

    assertEquals("editCalendarProperty", mockManager.lastMethodCalled);
    assertEquals("TestCalendar", mockManager.lastMethodArgs[0]);
    assertEquals("timezone", mockManager.lastMethodArgs[1]);
    assertEquals("Asia/Kolkata", mockManager.lastMethodArgs[2]);
  }

  /**
   * Test to check the correct ParseEventCalendar method.
   */
  @Test
  public void testParseCopyEventCommand() {
    String input = "copy event Meeting on 2023-12-01T10:00 --target Work to 2023-12-02T10:00";
    commandController.parseCommand(input);

    assertEquals("copyCalendarEvent", mockManager.lastMethodCalled);
    assertEquals("Meeting", mockManager.lastMethodArgs[0]);
    assertEquals(LocalDateTime.of(2023, 12, 1, 10, 0), mockManager.lastMethodArgs[1]);
    assertEquals("Work", mockManager.lastMethodArgs[2]);
    assertEquals(LocalDateTime.of(2023, 12, 2, 10, 0), mockManager.lastMethodArgs[3]);
  }

  /**
   * Test to check the incorrect Command.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testParseInvalidCommand() {
    String input = "invalid command";
    commandController.parseCommand(input);
  }

  /**
   * Test to check the start method, which initiates the Interpreter.
   */
  @Test
  public void testStartMethod() {
    commandController.start();
    // Verify interpreter was called
    assertNotNull(mockInterpreter.lastModeSelected);
  }
}