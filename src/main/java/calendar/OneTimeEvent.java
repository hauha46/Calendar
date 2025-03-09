package calendar;

import java.time.LocalDateTime;

public class OneTimeEvent extends AbstractEvent{


  private String subject;
  private LocalDateTime startTime;
  private LocalDateTime endTime;
  private String description;

  public OneTimeEvent(String subject, String description, LocalDateTime startTime, LocalDateTime endTime) {
    super(subject,description,startTime,endTime);
  }



  public boolean isConflicted(EventInterface otherEvent) {
    //AbstractEvent otherEvent = (OneTimeEvent) otherEvent1;
    if (otherEvent == null) {
      return false;
    }
    System.out.println(this.startTime);
    System.out.println(this.endTime);

    LocalDateTime otherStart = otherEvent.getStartTime();
    System.out.println(otherEvent.getStartTime());
    LocalDateTime otherEnd = otherEvent.getEndTime();
    System.out.println(otherEvent.getEndTime());


    // Check if the events overlap
    return !(this.getEndTime() != null && this.getEndTime().isBefore(otherStart)) &&
            !(otherEnd != null && otherEnd.isBefore(this.getStartTime()));
  }

//  @Override
//  void addEvent() {
//
//  }
}
