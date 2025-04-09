package calendar.utils;

public class EventsExporterFactory {

  /**
   * Returns an instance of CalendarExporter based on the provided format.
   *
   * @param format the desired format (current implementations csv)
   * @return a CalendarExporter instance
   */
  public static ExportEvents getExporter(String format) {
    if ("csv".equalsIgnoreCase(format)) {
      return new ExportCSV();
    }
    return null;
  }
}
