package calendar.view.components;

import javax.swing.*;

import java.awt.*;
import java.util.TimeZone;
import java.util.function.Consumer;

import calendar.controller.SwingController;

/**
 * Form for creating and editing calendars.
 */
public class CalendarForm {

  /**
   * Opens the dialog for creating a new calendar.
   *
   * @param parentFrame      the parent frame
   * @param controller       the controller
   * @param onUpdateComplete callback to run after a successful creation/update
   */
  public static void openCreateCalendarForm(JFrame parentFrame,
                                            SwingController controller,
                                            Runnable onUpdateComplete) {
    openCalendarForm(parentFrame, controller, null, onUpdateComplete);
  }

  /**
   * Opens the dialog for editing an existing calendar.
   *
   * @param parentFrame      the parent frame
   * @param controller       the controller
   * @param calendarName     the name of the calendar to edit
   * @param onUpdateComplete callback to run after a successful update
   */
  public static void openEditCalendarForm(JFrame parentFrame,
                                          SwingController controller,
                                          String calendarName,
                                          Runnable onUpdateComplete) {
    if (calendarName == null || calendarName.isEmpty()) {
      JOptionPane.showMessageDialog(parentFrame,
              "No calendar selected to edit",
              "Error", JOptionPane.ERROR_MESSAGE);
      return;
    }
    openCalendarForm(parentFrame, controller, calendarName, onUpdateComplete);
  }

  /**
   * Opens a calendar form for creating or editing a calendar.
   *
   * @param parentFrame      the parent frame
   * @param controller       the controller
   * @param calendarName     the name of the calendar to edit, or null for creation
   * @param onUpdateComplete callback to run after a successful creation/update
   */
  private static void openCalendarForm(JFrame parentFrame,
                                       SwingController controller,
                                       String calendarName,
                                       Runnable onUpdateComplete) {
    boolean isEditMode = calendarName != null && !calendarName.isEmpty();
    String title = isEditMode ? "Edit Calendar: " + calendarName : "Create New Calendar";

    JFrame calendarFrame = new JFrame(title);
    calendarFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    calendarFrame.setSize(400, 200);
    calendarFrame.setLayout(new GridLayout(3, 2, 10, 10));

    JLabel nameLabel = new JLabel("Calendar Name:");
    JTextField nameField = new JTextField(isEditMode ? calendarName : "", 20);
    JLabel timezoneLabel = new JLabel("Timezone:");
    JComboBox<String> timezoneComboBox = new JComboBox<>(getAllTimeZones());

    if (isEditMode) {
      String currentTimeZone = controller.getCurrentCalendarTimezone().getId();
      timezoneComboBox.setSelectedItem(currentTimeZone);
    } else {
      String defaultTimezone = TimeZone.getDefault().getID();
      timezoneComboBox.setSelectedItem(defaultTimezone);
    }

    JButton submitButton = new JButton(isEditMode ? "Update" : "Create");
    JButton cancelButton = new JButton("Cancel");

    calendarFrame.add(nameLabel);
    calendarFrame.add(nameField);
    calendarFrame.add(timezoneLabel);
    calendarFrame.add(timezoneComboBox);
    calendarFrame.add(submitButton);
    calendarFrame.add(cancelButton);

    submitButton.addActionListener(e -> {
      String newName = nameField.getText();
      String selectedTimezone = (String) timezoneComboBox.getSelectedItem();

      if (newName.isEmpty()) {
        JOptionPane.showMessageDialog(calendarFrame,
                "Calendar name is required!",
                "Error", JOptionPane.ERROR_MESSAGE);
        return;
      }

      if (selectedTimezone == null || selectedTimezone.isEmpty()) {
        JOptionPane.showMessageDialog(calendarFrame,
                "Timezone is required!",
                "Error", JOptionPane.ERROR_MESSAGE);
        return;
      }

      try {
        if (isEditMode) {
          if (!calendarName.equals(newName)) {
            controller.editCalendarProperty(calendarName, "name", newName);// view doing the part of the controller
          }

          String currentTimeZone = controller.getCurrentCalendarTimezone().getId();
          if (!currentTimeZone.equals(selectedTimezone)) {
            controller.editCalendarProperty(newName, "timezone", selectedTimezone);// view editiing the components of the property
          }

          JOptionPane.showMessageDialog(calendarFrame,
                  "Calendar updated successfully!\nName: " + newName + "\nTimezone: " + selectedTimezone);
        } else {
          controller.createCalendar(newName, selectedTimezone);// editing the properyt of the view
          JOptionPane.showMessageDialog(calendarFrame,
                  "Calendar created successfully!\nName: " + newName + "\nTimezone: " + selectedTimezone);
        }

        if (onUpdateComplete != null) {
          onUpdateComplete.run();
        }

        calendarFrame.dispose();
      } catch (Exception ex) {
        JOptionPane.showMessageDialog(calendarFrame,
                (isEditMode ? "Error updating calendar: " : "Error creating calendar: ") + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
      }
    });

    cancelButton.addActionListener(e -> calendarFrame.dispose());
    calendarFrame.setLocationRelativeTo(parentFrame);
    calendarFrame.setVisible(true);
  }

  /**
   * Get all available timezone IDs.
   *
   * @return array of timezone IDs
   */
  private static String[] getAllTimeZones() {
    return TimeZone.getAvailableIDs();
  }
}
