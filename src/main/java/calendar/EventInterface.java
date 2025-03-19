import java.time.LocalDateTime;

/**
 * This interface represents an event.
 */
public interface EventInterface {
  /**
   * Retrieve the subject of the current event.
   *
   * @return the subject of the current event.
   */
  String getSubject();

  /**
   * Retrieve the start time of the current event.
   *
   * @return the start time of the current event.
   */
  LocalDateTime getStartTime();

  /**
   * Retrieve the end time of the current event.
   *
   * @return the end time of the current event.
   */
  LocalDateTime getEndTime();

  /**
   * Retrieve the description of the current event.
   *
   * @return the description of the current event.
   */
  String getDescription();

  /**
   * Check if the current event has conflict with another event.
   *
   * @param otherEvent the other event that need to be checked if there are any conflicts
   * @return true if there is conflict, false if there isn't.
   */
  boolean isConflicted(EventInterface otherEvent);
}
