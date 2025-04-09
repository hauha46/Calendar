package calendar.utils;

import calendar.manager.IEventManager;

/**
 * Interface to which supports multiple forms of exports.
 */
public interface ExportEvents {
  /**
   * Exports the given events into a formatted String.
   *
   * @param eventManger the calendar to export.
   * @return the calendar data as a formatted String.
   */
  String exportEvents(IEventManager eventManger);
}
