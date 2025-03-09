package calendar;

import java.time.LocalDateTime;

public interface EventInterface {


  public String getSubject();

  public LocalDateTime getStartTime();

  public LocalDateTime getEndTime();

  public String getDescription();

  public boolean isConflicted(EventInterface otherEvent);
}
