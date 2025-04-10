package calendar.utils;

import java.time.format.DateTimeFormatter;

import calendar.manager.IEventManager;
import calendar.model.IEvent;

/**
 * Class exports the calendar into CSV format.
 */
public class ExportCSV implements ExportEvents{

  @Override
  public String exportEvents(IEventManager eventManager) {
    StringBuilder sb = new StringBuilder();
    sb.append("Subject,Start Date,Start Time,End Date,End Time,Description\n");

    // Assuming that IEventManager provides the list of all events:
    for (IEvent event : eventManager.getAllEvents()) {
      // Escape commas or use a proper CSV-escape if needed.
      ExportUtils exportUtils= new ExportUtils();

      String subject = exportUtils.escapeCSV(event.getSubject());;
      String description = exportUtils.escapeCSV(event.getDescription());

      DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
      DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");
      String startDate = event.getStartTime().toLocalDate().format(dateFormatter);
      String startTime = event.getStartTime().toLocalTime().format(timeFormatter);
      String endDate = event.getEndTime().toLocalDate().format(dateFormatter);
      String endTime = event.getEndTime().toLocalTime().format(timeFormatter);

      sb.append(String.format("%s,%s,%s,%s,%s,%s\n",
              subject, startDate, startTime, endDate, endTime, description));
    }
    return sb.toString();

  }
}
