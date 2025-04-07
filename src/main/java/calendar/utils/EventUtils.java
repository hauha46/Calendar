package calendar.utils;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

import calendar.model.IEvent;

/**
 * Utility class for event-related operations in the calendar application.
 * Provides methods for checking conflicts between events.
 */
public class EventUtils {
  /**
   * Check if an event has any conflict with the current calendar.
   *
   * @param event the given event.
   * @return boolean value whether the event has any conflicts or not.
   */
  public boolean hasConflict(Map<LocalDate, Set<IEvent>> calendar, IEvent event) {
    LocalDate date = event.getStartTime().toLocalDate();
    if (calendar.containsKey(date)) {
      for (IEvent existingEvent : calendar.get(date)) {
        if (event.isConflicted(existingEvent)) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Check if a list of event has any conflict with the current calendar.
   *
   * @param events the given list of event.
   * @return boolean value whether the list has any conflicts or not.
   */
  public boolean hasAnyConflict(Map<LocalDate, Set<IEvent>> calendar, List<IEvent> events) {
    for (IEvent event : events) {
      if (hasConflict(calendar, event)) {
        return true;
      }
    }
    return false;
  }
}
