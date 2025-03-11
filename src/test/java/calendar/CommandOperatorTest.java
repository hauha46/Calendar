package calendar;

import org.junit.Before;
import org.junit.Test;

public class CommandOperatorTest {
  CommandOperator operator;

  /**
   * Initilizing a CommandOperator instance to check the methods.
   */
  @Before
  public void setUp() {
    operator = new CommandOperator();
  }

  /**
   * creating single event via the CommandOperator Class.
   */
  @Test
  public void testCreateEventSingle() {

    String subject = "Meeting";
    String startTime = "2023-01-01T10:00";
    String endTime = "2023-01-01T11:00:00";
    String subject1 = "Meeting2";
    String startTime1 = "2023-01-02T10:00:00";
    String endTime1 = "2023-01-02T11:00:00";
    String subject2 = "Meeting3";
    String startTime2 = "2023-01-03T10:00:00";
    String endTime2 = "2023-01-03T11:00:00";

    String start = "2023-01-01";
    String end = "2023-01-02";

    operator.createEventSingle(subject, startTime, endTime);
    operator.createEventSingle(subject1, startTime1, endTime1);
    operator.createEventSingle(subject2, startTime2, endTime2);


    operator.printEvents(start, end);
  }

  /**
   * Creating event endTime with empty.
   */
  @Test
  public void testCreateEventSingleWithEmptyEndTime() {

    String subject = "Meeting";
    String startTime = "2023-01-01T10:00:00";
    String endTime = "";
    String start = "2023-01-01";
    operator.createEventSingle(subject, startTime, endTime);

    operator.printEvents(start, "");
  }

  /**
   * Creating recurring events.
   */
  @Test
  public void testCreateEventRecurring() {
    CommandOperator operator = new CommandOperator();

    String subject = "Weekly Meeting";
    String startTime = "2023-01-02T10:00";
    String endTime = "2023-01-02T11:00";
    String endRecurring = "2023-01-30T23:59";
    String recurringDay = "M";
    String occurrences = "";

    operator.createEventRecurring(subject, startTime, endTime, endRecurring, recurringDay, occurrences);
    String start = "2023-01-02";
    String end = "2023-01-30";
    operator.printEvents(start, end);
  }

  /**
   * Creating events with occurrences and not endDate.
   */
  @Test
  public void testCreateEventRecurringWithOccurrences() {
    CommandOperator operator = new CommandOperator();

    String subject = "Weekly Meeting";
    String startTime = "2023-01-02T10:00";
    String endTime = "2023-01-02T11:00";
    String endRecurring = "";
    String recurringDay = "M";
    String occurrences = "3";

    operator.createEventRecurring(subject, startTime, endTime, endRecurring, recurringDay, occurrences);
    String start = "2023-01-02";
    String end = "2023-01-30";
    operator.printEvents(start, end);
  }

  /**
   * create reccuring events all day, which should create all day events till the end date.
   */
  @Test
  public void testCreateEventRecurringAllDay() {
    CommandOperator operator = new CommandOperator();

    String subject = "Weekly Meeting";
    String startDate = "2023-01-02";
    String endRecurring = "2023-01-30T23:59";
    String recurringDay = "M";
    String occurrences = "";

    operator.createEventRecurringAllDay(subject, startDate, endRecurring, recurringDay, occurrences);
    String start = "2023-01-02";
    String end = "2023-01-30";
    operator.printEvents(start, end);
  }

  /**
   * edit single event property.
   */
  @Test
  public void testEditEventSingle() {
    CommandOperator operator = new CommandOperator();

    String subject = "Meeting";
    String startTime = "2023-01-01T10:00";
    String endTime = "2023-01-01T17:00";

    operator.createEventSingle(subject, startTime, endTime);
    operator.printEvents("2023-01-01", "2023-01-01");

    String property = "startTime";
    String newValue = "2023-01-01T13:00";

    operator.editEventSingle(subject, startTime, endTime, property, newValue);
    operator.printEvents("2023-01-01", "2023-01-01");

  }

  /**
   * edit single event property startTime after endTime.
   */
  @Test
  public void testEditEventSingleWrongStartTime() {
    CommandOperator operator = new CommandOperator();

    String subject = "Meeting";
    String startTime = "2023-01-01T10:00";
    String endTime = "2023-01-01T17:00";

    operator.createEventSingle(subject, startTime, endTime);
    operator.printEvents("2023-01-01", "2023-01-01");

    String property = "startTime";
    String newValue = "2023-01-01T19:00";

    operator.editEventSingle(subject, startTime, endTime, property, newValue);
    operator.printEvents("2023-01-01", "2023-01-01");

  }
//not updating for minutes
  @Test
  public void testEditEventRecurring() {
    CommandOperator operator = new CommandOperator();

    // Create a recurring event
    String subject = "Weekly Meeting";
    String startTime = "2023-01-02T10:00";
    String endTime = "2023-01-02T11:00";
    String endRecurring = "2023-01-30T23:59";
    String recurringDay = "M";
    String occurrences = "";

    String start = "2023-01-02";
    String end = "2023-01-30";
    operator.createEventRecurring(subject, startTime, endTime, endRecurring, recurringDay, occurrences);
    System.out.println("name");
    //edit name property
    String property = "name";
    String newValue = "Updated Weekly Meeting";
      operator.printEvents(start, end);
    operator.editEventRecurring(subject, startTime, property, newValue);
    operator.printEvents(start, end);
    System.out.println("time");

    //edit time property wrong
   property = "startTime";
    newValue = "2023-01-02T15:00";
    operator.printEvents(start, end);
    operator.editEventRecurring(subject, startTime, property, newValue);
    operator.printEvents(start, end);
    System.out.println("time correct");

    property = "startTime";
    newValue = "2023-01-02T10:30";
    operator.printEvents(start, end);
    operator.editEventRecurring(subject, startTime, property, newValue);
    operator.printEvents(start, end);
    //edit endReccuring property


    //edit reccuringDay property

    //edit occurrences property
  }

  /**
   * Testing for availiable or not on a specific day
   */
  @Test
  public void testIsBusy() {

    String date = "2023-01-01";
    //currently availiable
    operator.isBusy(date);

    String subject = "Meeting";
    String startTime = "2023-01-01T10:00";
    String endTime = "2023-01-01T11:00";

    operator.createEventSingle(subject, startTime, endTime);
    //should print busy
    operator.isBusy(date);

  }

}
