package calendar;

import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;

public class CalendarTest {
  Calendar calendar;

  /**
   * Initilizing a Calander instance to check the methods.
   */
  @Before
  public void setUp() {
    calendar = new Calendar();
  }

  /**
   * Testing for event creation.
   */
  @Test
  public void testForEventCreation() {
    LocalDateTime starttime = LocalDateTime.of(2024,3,10,10,30,0);
    LocalDateTime endtime = LocalDateTime.of(2024,3,13,11,30,0);
    calendar.addEvent("trail1","to check if the event is getting created",starttime,endtime);
    calendar.printEvents();
  }

  /**
   * adding event with illegal time,no start date.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testAddEventIllegalStartTime() {
    LocalDateTime starttime =null;//= LocalDateTime.of(2024,3,10,10,30,0);
    LocalDateTime endtime = LocalDateTime.of(2024,3,10,11,30,0);
    calendar.addEvent("trail1","to check if the event is getting created",starttime,endtime);
    calendar.printEvents();
  }

  //failing as the event is getting created
  /**
   * adding event with illegal time,no end date.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testAddEventIllegalEndTime() {
    LocalDateTime starttime = LocalDateTime.of(2024,3,10,10,30,0);
    LocalDateTime endtime =null;// LocalDateTime.of(2024,3,10,11,30,0);
    calendar.addEvent("trail1","to check if the event is getting created",starttime,endtime);
    calendar.printEvents();
  }
  /**
   * adding event on same day,without conflict.
   */
  @Test
  public void testAddEventWithMultipleEventsOnSameDday() {
    LocalDateTime starttime = LocalDateTime.of(2024,3,10,10,30,0);
    LocalDateTime endtime = LocalDateTime.of(2024,3,10,11,30,0);
    calendar.addEvent("trail1","to check if the event is getting created",starttime,endtime);
    LocalDateTime starttime2 = LocalDateTime.of(2024,3,10,10,30,0);
    LocalDateTime endtime2 = LocalDateTime.of(2024,3,10,11,30,0);
    calendar.addEvent("trail2","to check if the event is getting created",starttime2,endtime2);
    calendar.printEvents();
  }

  /**
   * adding event on different day,without conflict.
   */
  @Test
  public void testAddEventWithMultipleEventsOnDifferentday() {
    LocalDateTime starttime = LocalDateTime.of(2024,3,10,10,30,0);
    LocalDateTime endtime = LocalDateTime.of(2024,3,10,11,30,0);
    calendar.addEvent("trail1","to check if the event is getting created",starttime,endtime);
    LocalDateTime starttime2 = LocalDateTime.of(2024,3,11,10,30,0);
    LocalDateTime endtime2 = LocalDateTime.of(2024,3,11,11,30,0);
    calendar.addEvent("trail2","to check if the event is getting created",starttime2,endtime2);
    calendar.printEvents();
  }

  /**
   * adding event on different day,with conflict.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testAddEventWithMultipleEventsWithCOnflict() {
    calendar.setAutoDeclineConflicts(true);
    LocalDateTime starttime = LocalDateTime.of(2024,3,10,10,30,0);
    LocalDateTime endtime = LocalDateTime.of(2024,3,10,11,30,0);
    calendar.addEvent("trail1","to check if the event is getting created",starttime,endtime);
    LocalDateTime starttime2 = LocalDateTime.of(2024,3,10,10,40,0);
    LocalDateTime endtime2 = LocalDateTime.of(2024,3,10,10,50,0);
    calendar.addEvent("trail2","to check if the event is getting created",starttime2,endtime2);
    calendar.printEvents();
  }

  //here the n is the day or the week , as a week can have multiple recurring days.
  /**
   * Generating recurring events.
   */
  @Test
  public void testAddRecurringEvents()
  {
    LocalDateTime starttime = LocalDateTime.of(2024, 3, 5, 9, 0);
    LocalDateTime endtime = LocalDateTime.of(2024, 3, 5, 10, 0);
    LocalDateTime endRecurring = LocalDateTime.of(2024, 3, 28, 23, 59);
    String recurringDays = "M";

    calendar.addRecurringEvents("trailRecurring1", "trying out recurring event1", starttime, endtime, endRecurring, recurringDays, 3);

    calendar.printEvents();
  }

  /**
   * Generating recurring multiple events.
   */
  @Test
  public void testAddRecurringMultipleEvents()
  {
    LocalDateTime starttime = LocalDateTime.of(2024, 3, 5, 9, 0);
    LocalDateTime endtime = LocalDateTime.of(2024, 3, 5, 10, 0);
    LocalDateTime endRecurring = LocalDateTime.of(2024, 3, 28, 23, 59);
    String recurringDays = "MRU";

    LocalDateTime starttime2 = LocalDateTime.of(2024, 3, 5, 11, 0);
    LocalDateTime endtime2 = LocalDateTime.of(2024, 3, 5, 12, 0);
    LocalDateTime endRecurring2 = LocalDateTime.of(2024, 3, 28, 23, 59);
   // List<String> recurringDays2 = Arrays.asList("MONDAY", "WEDNESDAY", "FRIDAY");

    calendar.addRecurringEvents("trailRecurring1", "trying out recurring event1", starttime, endtime, endRecurring, recurringDays, 3);
    calendar.addRecurringEvents("trailRecurring2", "trying out recurring event2", starttime2, endtime2, endRecurring2, recurringDays, 3);

    calendar.printEvents();
  }



  /**
   * Generating recurring multiple events with conflict .
   */
  @Test(expected = IllegalArgumentException.class)
  public void testAddRecurringMultipleEventsWIthConflict()
  {
    calendar.setAutoDeclineConflicts(true);
    LocalDateTime starttime = LocalDateTime.of(2024, 3, 5, 9, 0);
    LocalDateTime endtime = LocalDateTime.of(2024, 3, 5, 10, 0);
    LocalDateTime endRecurring = LocalDateTime.of(2024, 3, 28, 23, 59);
    String recurringDays = "MT";

    LocalDateTime starttime2 = LocalDateTime.of(2024, 3, 5, 9, 0);
    LocalDateTime endtime2 = LocalDateTime.of(2024, 3, 5, 12, 0);
    LocalDateTime endRecurring2 = LocalDateTime.of(2024, 3, 28, 23, 59);
    // List<String> recurringDays2 = Arrays.asList("MONDAY", "WEDNESDAY", "FRIDAY");

    calendar.addRecurringEvents("trailRecurring1", "trying out recurring event1", starttime, endtime, endRecurring, recurringDays, 3);
    calendar.addRecurringEvents("trailRecurring2", "trying out recurring event2", starttime2, endtime2, endRecurring2, recurringDays, 3);

    calendar.printEvents();
  }

  /**
   * Generating recurring event when one time event exists.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testEventsWIthConflict()
  {
    calendar.setAutoDeclineConflicts(true);

    LocalDateTime starttime1 = LocalDateTime.of(2024, 3, 11, 9, 0);
    LocalDateTime endtime1 = LocalDateTime.of(2024, 3, 11, 10, 0);

    LocalDateTime starttime = LocalDateTime.of(2024, 3, 5, 9, 0);
    LocalDateTime endtime = LocalDateTime.of(2024, 3, 5, 10, 0);
    //calendar.printEvents();
    LocalDateTime endRecurring = LocalDateTime.of(2024, 3, 28, 23, 59);
    String recurringDays = "M";


    calendar.addEvent("trail1","add onetime event",starttime1,endtime1);
    calendar.addRecurringEvents("trailRecurring1", "trying out recurring event1", starttime, endtime, endRecurring, recurringDays, 3);
    //calendar.addRecurringEvents("trailRecurring2", "trying out recurring event2", starttime2, endtime2, endRecurring2, recurringDays, 3);

    calendar.printEvents();
  }


  /**
   * performing check for editing a single event.
   */
  @Test
  public void testEditEventSingle() {
    LocalDateTime start = LocalDateTime.of(2025, 3, 15, 14, 0);
    LocalDateTime end = LocalDateTime.of(2025, 3, 15, 15, 0);
    calendar.addEvent("Doctor Appointment", "Routine checkup", start, end);
    calendar.printEvents();

    calendar.editEventSingle("Doctor Appointment", start, end, "name", "Dentist Visit");
    calendar.printEvents();

  }

  /**
   * performing check for editing a recurring event.
   */
  @Test
  public void testEditEventRecurring() {
    LocalDateTime start = LocalDateTime.of(2025, 3, 10, 9, 0);
    LocalDateTime end = LocalDateTime.of(2025, 3, 10, 10, 0);
    LocalDateTime endRecurring = LocalDateTime.of(2025, 3, 30, 9, 0);
    calendar.addRecurringEvents("Gym", "Morning Workout", start, end, endRecurring, "MTW", 0);
    calendar.printEvents();

    calendar.editEventRecurring("Gym", start, "name", "Yoga");
    calendar.printEvents();


  }


  /**
   * checking if availiable or not, this case tests for busy.
   */
  @Test
  public void testIsBusy() {
    LocalDateTime start = LocalDateTime.of(2025, 3, 20, 10, 0);
    LocalDateTime end = LocalDateTime.of(2025, 3, 20, 11, 0);
    calendar.addEvent("Meeting", "Project discussion", start, end);

    LocalDate date = LocalDate.of(2025, 3, 20);
    calendar.isBusy(date);
  }

  /**
   * checking if availiable or not, this case tests for available.
   */
  @Test
  public void testIsBusy2() {
    LocalDateTime start = LocalDateTime.of(2025, 3, 20, 10, 0);
    LocalDateTime end = LocalDateTime.of(2025, 3, 20, 11, 0);
    calendar.addEvent("Meeting", "Project discussion", start, end);

    LocalDate date = LocalDate.of(2025, 3, 21);
    calendar.isBusy(date);
  }

  /**
   * testing the print events all.
   */
  @Test
  public void testPrintEvents() {
    LocalDateTime start = LocalDateTime.of(2025, 3, 22, 14, 0);
    LocalDateTime end = LocalDateTime.of(2025, 3, 22, 15, 0);
    calendar.addEvent("Conference Call", "Client meeting", start, end);

    calendar.printEvents();
  }

  /**
   * testing the print events from certian time to time.
   */
@Test
  public void testPrintEventsfromTo()
{
  LocalDateTime starttime = LocalDateTime.of(2024, 3, 5, 9, 0);
  LocalDateTime endtime = LocalDateTime.of(2024, 3, 5, 10, 0);
  LocalDate start = LocalDate.of(2024, 3, 11);
  LocalDate end = LocalDate.of(2024, 3, 23);
  LocalDateTime endRecurring = LocalDateTime.of(2024, 3, 28, 23, 59);
  String recurringDays = "M";

  calendar.addRecurringEvents("trailRecurring1", "trying out recurring event1", starttime, endtime, endRecurring, recurringDays, 3);

  calendar.printEvents(start,end);
}






}