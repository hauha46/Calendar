package calendar;

import calendar.controller.SwingController;
import calendar.manager.CalendarManager;
import calendar.model.IEvent;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


/**
 * Test for the SwingController Class.
 */
public class SwingControllerTest {

  private CalendarManager calendarManager;
  private SwingController controller;

  /**
   * Setting up before each test run.
   */
  @Before
  public void setUp() {
    calendarManager = new CalendarManager();
    controller = new SwingController(calendarManager);
  }

  /**
   * After each test was run executes.
   */
  @After
  public void tearDown() {
    // do nothing.
  }

  /**
   * Testing the Create calendar function.
   */
  @Test
  public void testCreateCalendarSuccess() {
    controller.createCalendar("TestCalendar", "UTC");
    List<String> names = controller.getAllCalendarNames();
    assertTrue("Calendar list should contain 'TestCalendar'", names.contains("TestCalendar"));
    assertEquals("Current calendar should be 'TestCalendar'", "TestCalendar", controller.getCurrentCalendar());
  }

  /**
   * Test to check if duplicate calendar with same name can be created.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testCreateCalendarDuplicate() {
    controller.createCalendar("DuplicateCal", "UTC");
    controller.createCalendar("DuplicateCal", "UTC");
  }

  /**
   * Test to edit property of the calendar name.
   */
  @Test
  public void testEditCalendarPropertyName() {
    controller.createCalendar("Cal1", "UTC");
    controller.editCalendarProperty("Cal1", "name", "RenamedCal");
    assertEquals("Calendar should be renamed", "RenamedCal", controller.getCurrentCalendar());
    List<String> names = controller.getAllCalendarNames();
    assertFalse("Old calendar name should not exist", names.contains("Cal1"));
    assertTrue("New calendar name should exist", names.contains("RenamedCal"));
  }

  /**
   * Test to edit property of the calendar timezone.
   */
  @Test
  public void testEditCalendarPropertyTimezone() {
    controller.createCalendar("CalTZ", "UTC");
    controller.editCalendarProperty("CalTZ", "timezone", "America/New_York");
    assertEquals("Timezone should be updated", "America/New_York", controller.getCurrentCalendarTimezone().getId());
  }

  /**
   * Checking the calendar nonexistent.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testSetCurrentCalendarInvalid() {
    controller.setCurrentCalendar("NonExistent");
  }

  /**
   * Using the calendar present in the Map.
   */
  @Test
  public void testSetCurrentCalendarSuccess() {
    controller.createCalendar("CalA", "UTC");
    controller.createCalendar("CalB", "UTC");
    controller.setCurrentCalendar("CalB");
    assertEquals("Current calendar should be set to CalB", "CalB", controller.getCurrentCalendar());
  }

  /**
   * Test for creating  an event.
   */
  @Test
  public void testAddEvent() {
    controller.createCalendar("EventCal", "UTC");
    String start = "2025-04-10 09:00";
    String end = "2025-04-10 10:00";
    controller.addEvent("Meeting", "Team meeting", start, end);
    List<IEvent> events = controller.getEventsForDay(LocalDate.of(2025, 4, 10));
    assertFalse("There should be at least one event", events.isEmpty());
    assertEquals("Event subject should match", "Meeting", events.get(0).getSubject());
  }

  /**
   * Test for creating a recurring event.
   */
  @Test
  public void testAddRecurringEvent() {
    controller.createCalendar("RecurringCal", "UTC");
    String start = "2025-04-10 09:00";
    String end = "2025-04-10 10:00";
    controller.addRecurringEvent("DailyStandup", "Daily meeting", start, end, "2025-04-12 23:59", "MTWRFSU", 3);
    List<IEvent> events = controller.getEventsForMonth(LocalDate.of(2025, 4, 1), LocalDate.of(2025, 4, 30));
    long count = events.stream().filter(e -> e.getSubject().equals("DailyStandup")).count();
    assertEquals("There should be 3 recurring events", 3, count);
  }

  /**
   * Test to edit single event.
   */
  @Test
  public void testEditEventSingle() {
    controller.createCalendar("EditCal", "UTC");
    String start = "2025-04-10 11:00";
    String end = "2025-04-10 12:00";
    controller.addEvent("Workshop", "Initial description", start, end);
    // Edit the event’s description.
    controller.editEventSingle("Workshop", start, end, "description", "Updated description");
    List<IEvent> events = controller.getEventsForDay(LocalDate.of(2025, 4, 10));
    assertFalse("Event list should not be empty", events.isEmpty());
    assertEquals("Description should be updated", "Updated description", events.get(0).getDescription());
  }

  /**
   * Get all events on a day.
   */
  @Test
  public void testGetEventsForDay() {
    controller.createCalendar("DayCal", "UTC");
    String start = "2025-04-10 14:00";
    String end = "2025-04-10 15:00";
    controller.addEvent("Lunch", "Quick meal", start, end);
    List<IEvent> events = controller.getEventsForDay(LocalDate.of(2025, 4, 10));
    assertEquals("There should be one event for the day", 1, events.size());
  }

  /**
   * Get all events for a month.
   */
  @Test
  public void testGetEventsForMonth() {
    controller.createCalendar("MonthCal", "UTC");
    String start = "2025-04-15 10:00";
    String end = "2025-04-15 11:00";
    controller.addEvent("Review", "Project review", start, end);
    List<IEvent> events = controller.getEventsForMonth(LocalDate.of(2025, 4, 1), LocalDate.of(2025, 4, 30));
    assertTrue("Month events should contain the review", events.stream().anyMatch(e -> "Review".equals(e.getSubject())));
  }

  /**
   * Export the calendar to csv.
   * @throws Exception
   */
  @Test
  public void testExportCalendarToCSV() throws Exception {
    controller.createCalendar("CSVCal", "UTC");
    controller.addEvent("ExportTest", "Export description", "2025-04-10 08:00", "2025-04-10 09:00");
    File tempFile = File.createTempFile("calendarTest", ".csv");
    tempFile.deleteOnExit();
    controller.exportCalendarToCSV(tempFile.getAbsolutePath());
    String content = new String(Files.readAllBytes(tempFile.toPath()));
    assertTrue("CSV header should be present", content.contains("Subject,Start Date,Start Time,End Date,End Time,Description"));
    assertTrue("Exported event subject should be present", content.contains("ExportTest"));
  }

  /**
   * Test for checking Import function.
   * @throws Exception
   */
  @Test
  public void testImportCalendarFromCSV() throws Exception {
    controller.createCalendar("ImportCal", "UTC");
    // Create CSV content with header and one event row.
    String csvContent = "Subject,Start Date,Start Time,End Date,End Time,Description\n" +
            "ImportedEvent,04/10/2025,09:00 AM,04/10/2025,10:00 AM,Imported description\n";
    File tempFile = File.createTempFile("importCalendar", ".csv");
    tempFile.deleteOnExit();
    Files.write(tempFile.toPath(), csvContent.getBytes());
    controller.importCalendarFromCSV(tempFile.getAbsolutePath());
    List<IEvent> events = controller.getEventsForDay(LocalDate.of(2025, 4, 10));
    assertFalse("There should be at least one event imported", events.isEmpty());
    assertTrue("Imported event subject should match", events.stream().anyMatch(e -> "ImportedEvent".equals(e.getSubject())));
  }
}
