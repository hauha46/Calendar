package calendar.view.components;

import calendar.controller.SwingController;
import calendar.model.IEvent;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Form for creating and editing events.
 */
public class EventForm {
  private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

  /**
   * Opens a dialog to create a new event with a refresh callback.
   *
   * @param parentFrame the parent frame
   * @param controller  the controller
   * @param selectedDate the date where the event should be created
   */
  public static void openCreateEventForm(JFrame parentFrame, 
                                       SwingController controller, 
                                       LocalDate selectedDate) {
    JDialog eventDialog = new JDialog(parentFrame, "Create Event", true);
    eventDialog.setSize(500, 400);
    eventDialog.setLayout(new BorderLayout());

    JPanel formPanel = new JPanel(new GridLayout(0, 2, 5, 5));

    JLabel subjectLabel = new JLabel("Subject:");
    JTextField subjectField = new JTextField(20);

    JLabel descriptionLabel = new JLabel("Description:");
    JTextField descriptionField = new JTextField(20);

    JLabel startDateLabel = new JLabel("Start Date (yyyy-MM-dd):");
    JTextField startDateField = new JTextField(selectedDate.toString());

    JLabel startTimeLabel = new JLabel("Start Time (HH:mm):");
    JTextField startTimeField = new JTextField("09:00");

    JLabel endDateLabel = new JLabel("End Date (yyyy-MM-dd):");
    JTextField endDateField = new JTextField(selectedDate.toString());

    JLabel endTimeLabel = new JLabel("End Time (HH:mm):");
    JTextField endTimeField = new JTextField("10:00");

    JLabel recurringLabel = new JLabel("Recurring Event:");
    JCheckBox recurringCheckBox = new JCheckBox();

    JLabel recurringDaysLabel = new JLabel("Recurring Days (MTWRFSU):");
    JTextField recurringDaysField = new JTextField("M");
    recurringDaysField.setEnabled(false);

    JLabel occurrencesLabel = new JLabel("Occurrences (0 for unlimited):");
    JTextField occurrencesField = new JTextField("0");
    occurrencesField.setEnabled(false);

    JLabel endRecurringLabel = new JLabel("End Recurring Date (yyyy-MM-dd):");
    JTextField endRecurringField = new JTextField(selectedDate.plusMonths(1).toString());
    endRecurringField.setEnabled(false);

    formPanel.add(subjectLabel);
    formPanel.add(subjectField);
    formPanel.add(descriptionLabel);
    formPanel.add(descriptionField);
    formPanel.add(startDateLabel);
    formPanel.add(startDateField);
    formPanel.add(startTimeLabel);
    formPanel.add(startTimeField);
    formPanel.add(endDateLabel);
    formPanel.add(endDateField);
    formPanel.add(endTimeLabel);
    formPanel.add(endTimeField);
    formPanel.add(recurringLabel);
    formPanel.add(recurringCheckBox);
    formPanel.add(recurringDaysLabel);
    formPanel.add(recurringDaysField);
    formPanel.add(occurrencesLabel);
    formPanel.add(occurrencesField);
    formPanel.add(endRecurringLabel);
    formPanel.add(endRecurringField);

    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    JButton createButton = new JButton("Create");
    JButton cancelButton = new JButton("Cancel");
    buttonPanel.add(createButton);
    buttonPanel.add(cancelButton);

    recurringCheckBox.addActionListener(e -> {
      boolean isRecurring = recurringCheckBox.isSelected();
      recurringDaysField.setEnabled(isRecurring);
      occurrencesField.setEnabled(isRecurring);
      endRecurringField.setEnabled(isRecurring);
    });

    createButton.addActionListener(e -> {
      try {
        String subject = subjectField.getText();
        String description = descriptionField.getText();
        String startDate = startDateField.getText();
        String startTime = startTimeField.getText();
        String endDate = endDateField.getText();
        String endTime = endTimeField.getText();
        String startDateTime = startDate + " " + startTime;
        String endDateTime = endDate + " " + endTime;
        
        if (subject.isEmpty()) {
          throw new IllegalArgumentException("Subject is required");
        }
        
        if (recurringCheckBox.isSelected()) {
          String recurringDays = recurringDaysField.getText();
          int occurrences = Integer.parseInt(occurrencesField.getText());
          String endRecurringDate = endRecurringField.getText() + " 23:59";
          
          if (recurringDays.isEmpty()) {
            throw new IllegalArgumentException("Recurring days are required for recurring events");
          }
          
          controller.addRecurringEvent(subject, description, startDateTime, endDateTime, 
                                     endRecurringDate, recurringDays, occurrences);
        } else {
          controller.addEvent(subject, description, startDateTime, endDateTime);
        }
        
        eventDialog.dispose();
        JOptionPane.showMessageDialog(parentFrame, "Event created successfully!");
      } catch (Exception ex) {
        JOptionPane.showMessageDialog(parentFrame, "Error creating event: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
      }
    });
    
    cancelButton.addActionListener(e -> eventDialog.dispose());
    eventDialog.add(formPanel, BorderLayout.CENTER);
    eventDialog.add(buttonPanel, BorderLayout.SOUTH);
    eventDialog.setLocationRelativeTo(parentFrame);
    eventDialog.setVisible(true);
  }

  /**
   * Opens a dialog to edit an existing event with a refresh callback.
   *
   * @param parentFrame the parent frame
   * @param controller  the controller
   * @param event       the event to edit
   */
  public static void openEditEventForm(JFrame parentFrame, 
                                     SwingController controller, 
                                     IEvent event) {
    JDialog eventDialog = new JDialog(parentFrame, "Edit Event", true);
    eventDialog.setSize(500, 400);
    eventDialog.setLayout(new BorderLayout());

    JPanel formPanel = new JPanel(new GridLayout(0, 2, 5, 5));

    JLabel subjectLabel = new JLabel("Subject:");
    JTextField subjectField = new JTextField(event.getSubject(), 20);
    
    JLabel descriptionLabel = new JLabel("Description:");
    JTextField descriptionField = new JTextField(event.getDescription(), 20);
    
    LocalDateTime startTime = event.getStartTime();
    LocalDateTime endTime = event.getEndTime();
    
    JLabel startDateLabel = new JLabel("Start Date (yyyy-MM-dd):");
    JTextField startDateField = new JTextField(startTime.toLocalDate().toString());
    
    JLabel startTimeLabel = new JLabel("Start Time (HH:mm):");
    JTextField startTimeField = new JTextField(
        startTime.format(DateTimeFormatter.ofPattern("HH:mm")));
    
    JLabel endDateLabel = new JLabel("End Date (yyyy-MM-dd):");
    JTextField endDateField = new JTextField(endTime.toLocalDate().toString());
    
    JLabel endTimeLabel = new JLabel("End Time (HH:mm):");
    JTextField endTimeField = new JTextField(
        endTime.format(DateTimeFormatter.ofPattern("HH:mm")));

    formPanel.add(subjectLabel);
    formPanel.add(subjectField);
    formPanel.add(descriptionLabel);
    formPanel.add(descriptionField);
    formPanel.add(startDateLabel);
    formPanel.add(startDateField);
    formPanel.add(startTimeLabel);
    formPanel.add(startTimeField);
    formPanel.add(endDateLabel);
    formPanel.add(endDateField);
    formPanel.add(endTimeLabel);
    formPanel.add(endTimeField);

    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    JButton saveButton = new JButton("Save");
    JButton cancelButton = new JButton("Cancel");
    buttonPanel.add(saveButton);
    buttonPanel.add(cancelButton);

    saveButton.addActionListener(e -> {
      try {
        String oldStartDateTimeStr = event.getStartTime().format(formatter);
        String oldEndDateTimeStr = event.getEndTime().format(formatter);
        
        String newSubject = subjectField.getText();
        String newDescription = descriptionField.getText();
        String newStartDate = startDateField.getText();
        String newStartTime = startTimeField.getText();
        String newEndDate = endDateField.getText();
        String newEndTime = endTimeField.getText();
        
        String newStartDateTime = newStartDate + " " + newStartTime;
        String newEndDateTime = newEndDate + " " + newEndTime;

        if (!event.getSubject().equals(newSubject)) {
          controller.editEventSingle(event.getSubject(), oldStartDateTimeStr, oldEndDateTimeStr, "name", newSubject);
        }
        
        if (!event.getDescription().equals(newDescription)) {
          controller.editEventSingle(newSubject, oldStartDateTimeStr, oldEndDateTimeStr, "description", newDescription);
        }
        
        if (!oldStartDateTimeStr.equals(newStartDateTime)) {
          controller.editEventSingle(newSubject, oldStartDateTimeStr, oldEndDateTimeStr, "startTime", newStartDateTime);
        }
        
        if (!oldEndDateTimeStr.equals(newEndDateTime)) {
          controller.editEventSingle(newSubject, oldStartDateTimeStr, oldEndDateTimeStr, "endTime", newEndDateTime);
        }
        
        eventDialog.dispose();
        JOptionPane.showMessageDialog(parentFrame, "Event updated successfully!");

      } catch (Exception ex) {
        JOptionPane.showMessageDialog(parentFrame, "Error updating event: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
      }
    });
    
    cancelButton.addActionListener(e -> eventDialog.dispose());
    eventDialog.add(formPanel, BorderLayout.CENTER);
    eventDialog.add(buttonPanel, BorderLayout.SOUTH);
    eventDialog.setLocationRelativeTo(parentFrame);
    eventDialog.setVisible(true);
  }

  /**
   * Opens a dialog to search and edit events by name within a time period with a refresh callback.
   * 
   * @param parentFrame the parent frame
   * @param controller the controller
   */
  public static void openBatchEditForm(JFrame parentFrame, 
                                     SwingController controller) {
    JDialog batchEditDialog = new JDialog(parentFrame, "Edit Multiple Events", true);
    batchEditDialog.setSize(500, 250);
    batchEditDialog.setLayout(new BorderLayout());
    
    JPanel formPanel = new JPanel(new GridLayout(0, 2, 5, 5));
    
    JLabel eventNameLabel = new JLabel("Event Name:");
    JTextField eventNameField = new JTextField(20);
    
    JLabel startDateLabel = new JLabel("Start Date (yyyy-MM-dd):");
    JTextField startDateField = new JTextField(LocalDate.now().toString());
    
    JLabel endDateLabel = new JLabel("End Date (yyyy-MM-dd):");
    JTextField endDateField = new JTextField(LocalDate.now().plusMonths(1).toString());
    
    formPanel.add(eventNameLabel);
    formPanel.add(eventNameField);
    formPanel.add(startDateLabel);
    formPanel.add(startDateField);
    formPanel.add(endDateLabel);
    formPanel.add(endDateField);
    
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    JButton searchButton = new JButton("Search & Edit");
    JButton cancelButton = new JButton("Cancel");
    buttonPanel.add(searchButton);
    buttonPanel.add(cancelButton);
    
    searchButton.addActionListener(e -> {
      try {
        String eventName = eventNameField.getText();
        if (eventName.isEmpty()) {
          throw new IllegalArgumentException("Event name is required");
        }
        
        LocalDateTime startDateTime = LocalDate.parse(startDateField.getText()).atStartOfDay();
        LocalDateTime endDateTime = LocalDate.parse(endDateField.getText()).atTime(23, 59, 59);

        List<IEvent> foundEvents = controller.getEventsForMonth(
            startDateTime.toLocalDate(), endDateTime.toLocalDate());
        
        List<IEvent> matchingEvents = new ArrayList<>();
        for (IEvent event : foundEvents) {
          if (event.getSubject().equals(eventName)) {
            matchingEvents.add(event);
          }
        }
        
        if (matchingEvents.isEmpty()) {
          JOptionPane.showMessageDialog(batchEditDialog, 
              "No events found with name '" + eventName + "' in the specified date range.",
              "No Results", JOptionPane.INFORMATION_MESSAGE);
          return;
        }

        JDialog editDialog = new JDialog(batchEditDialog, "Edit Events", true);
        editDialog.setSize(500, 300);
        editDialog.setLayout(new BorderLayout());
        
        JPanel eventsPanel = new JPanel();
        eventsPanel.setLayout(new BoxLayout(eventsPanel, BoxLayout.Y_AXIS));
        eventsPanel.setBorder(BorderFactory.createTitledBorder("Found Events"));
        
        JScrollPane scrollPane = new JScrollPane(eventsPanel);
        
        for (IEvent event : matchingEvents) {
          JPanel eventPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
          eventPanel.add(new JLabel(event.getSubject() + " - " + 
              event.getStartTime().format(formatter) + " to " + 
              event.getEndTime().format(formatter)));
          eventsPanel.add(eventPanel);
        }

        JPanel propertyPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        propertyPanel.setBorder(BorderFactory.createTitledBorder("Edit Property"));
        
        JLabel propertyLabel = new JLabel("Property to Edit:");
        String[] properties = {"name", "description", "startTime", "endTime"};
        JComboBox<String> propertyComboBox = new JComboBox<>(properties);
        
        JLabel valueLabel = new JLabel("New Value:");
        JTextField valueField = new JTextField(20);
        
        propertyPanel.add(propertyLabel);
        propertyPanel.add(propertyComboBox);
        propertyPanel.add(valueLabel);
        propertyPanel.add(valueField);
        
        JPanel batchButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton applyButton = new JButton("Apply to All");
        JButton closeButton = new JButton("Close");
        batchButtonPanel.add(applyButton);
        batchButtonPanel.add(closeButton);
        
        applyButton.addActionListener(ev -> {
          try {
            String property = (String) propertyComboBox.getSelectedItem();
            String newValue = valueField.getText();
            
            if (newValue.isEmpty()) {
              throw new IllegalArgumentException("New value cannot be empty");
            }

            for (IEvent event : matchingEvents) {
              controller.editEventSingle(
                  event.getSubject(),
                  event.getStartTime().format(formatter),
                  event.getEndTime().format(formatter),
                  property,
                  newValue
              );
            }
            
            editDialog.dispose();
            batchEditDialog.dispose();
            
            JOptionPane.showMessageDialog(parentFrame, 
                "Successfully updated " + matchingEvents.size() + " events.",
                "Success", JOptionPane.INFORMATION_MESSAGE);
          } catch (Exception ex) {
            JOptionPane.showMessageDialog(editDialog, 
                "Error updating events: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
          }
        });
        closeButton.addActionListener(ev -> editDialog.dispose());
        editDialog.add(scrollPane, BorderLayout.NORTH);
        editDialog.add(propertyPanel, BorderLayout.CENTER);
        editDialog.add(batchButtonPanel, BorderLayout.SOUTH);
        editDialog.setLocationRelativeTo(batchEditDialog);
        editDialog.setVisible(true);
      } catch (Exception ex) {
        JOptionPane.showMessageDialog(batchEditDialog, 
            "Error searching for events: " + ex.getMessage(),
            "Error", JOptionPane.ERROR_MESSAGE);
      }
    });
    
    cancelButton.addActionListener(e -> batchEditDialog.dispose());
    batchEditDialog.add(formPanel, BorderLayout.CENTER);
    batchEditDialog.add(buttonPanel, BorderLayout.SOUTH);
    batchEditDialog.setLocationRelativeTo(parentFrame);
    batchEditDialog.setVisible(true);
  }
}
