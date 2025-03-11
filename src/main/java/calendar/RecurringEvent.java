package calendar;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class RecurringEvent extends AbstractEvent {


  private String subject;
  private LocalDateTime startTime;
  private LocalDateTime endTime;
  private String description;
  private LocalDateTime endRecurring;
  private String recurringDays;
  private int occurrences;

  public RecurringEvent(String subject, String description, LocalDateTime startTime, LocalDateTime endTime, LocalDateTime endRecurring, String recurringDays, int occurrences) {
    super(subject,description,startTime,endTime);
    this.endRecurring = endRecurring;
    this.recurringDays = recurringDays;
    this.occurrences = occurrences;
  }




  public LocalDateTime getEndRecurring() {
    return endRecurring;
  }

  public int getOccurrences() {
    return occurrences;
  }

  public String getRecurringDays() {
    return recurringDays;
  }



  public boolean isConflicted(EventInterface otherEvent) {
    //RecurringEvent otherEvent = (RecurringEvent) otherEvent1;
    if (otherEvent == null) {
      return false;
    }
    LocalDateTime otherStart = otherEvent.getStartTime();
    LocalDateTime otherEnd = otherEvent.getEndTime();

    // Check if the events overlap
    return !(this.getStartTime() != null && this.getEndTime().isBefore(otherStart)) &&
            !(otherEnd != null && otherEnd.isBefore(this.getStartTime()));
  }

//  @Override
//  void addEvent() {
//
//  }
}
