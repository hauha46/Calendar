package calendar.utils;

/**
 * Utility class for exporting data from the calendar application.
 * Provides methods for formatting and escaping strings for export formats like CSV.
 */
public class ExportUtils {

  /**
   * Safeguard function for subject and description in case the format is incorrect for csv export.
   *
   * @param field the given field value.
   * @return safe string for subject or description.
   */
  public String escapeCSV(String field) {
    if (field == null) {
      return "";
    }

    if (field.contains(",") || field.contains("\"") || field.contains("\n")) {
      field = field.replace("\"", "\"\"");
      return "\"" + field + "\"";
    }
    return field;
  }
}
