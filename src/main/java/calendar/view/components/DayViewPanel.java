package calendar.view.components;

import calendar.controller.SwingController;
import calendar.model.IEvent;

import javax.swing.*;

import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Panel to display events for a specific day.
 */
public class DayViewPanel extends JPanel {
  private final SwingController controller;
  private final JFrame parentFrame;
  private LocalDate currentDate;
  private final JPanel eventsPanel;
  private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
  private Runnable refreshListener;

  /**
   * Constructs a new DayViewPanel.
   *
   * @param controller  the controller
   * @param parentFrame the parent frame
   */
  public DayViewPanel(SwingController controller, JFrame parentFrame) {
    this.controller = controller;
    this.parentFrame = parentFrame;
    this.currentDate = LocalDate.now();

    setLayout(new BorderLayout(5, 5));
    setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    JPanel headerPanel = new JPanel(new BorderLayout());
    JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    JButton addEventButton = new JButton("Add Event");
    JButton editEventsButton = new JButton("Edit Multiple Events");

    actionPanel.add(addEventButton);
    actionPanel.add(editEventsButton);
    headerPanel.add(actionPanel, BorderLayout.EAST);

    eventsPanel = new JPanel();
    eventsPanel.setLayout(new BoxLayout(eventsPanel, BoxLayout.Y_AXIS));
    JScrollPane scrollPane = new JScrollPane(eventsPanel);
    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

    addEventButton.addActionListener(e -> {
      openCreateEventForm();
    });

    editEventsButton.addActionListener(e -> {
      openBatchEditForm();
    });

    add(headerPanel, BorderLayout.NORTH);
    add(scrollPane, BorderLayout.CENTER);
    refreshView();
  }

  /**
   * Sets a listener to be called when the UI needs to be refreshed.
   *
   * @param listener the refresh listener
   */
  public void setRefreshListener(Runnable listener) {
    this.refreshListener = listener;
  }

  /**
   * Updates the current date and refreshes the view.
   *
   * @param date the new date to display
   */
  public void setDate(LocalDate date) {
    this.currentDate = date;
    refreshView();
  }

  /**
   * Refreshes the day view with events for the current date.
   */
  public void refreshView() {
    eventsPanel.removeAll();
    List<IEvent> dayEvents = controller.getEventsForDay(currentDate);
    if (dayEvents.isEmpty()) {
      JPanel noEventsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
      noEventsPanel.add(new JLabel("No events scheduled for this day"));
      eventsPanel.add(noEventsPanel);
    } else {
      for (IEvent event : dayEvents) {
        JPanel eventPanel = createEventPanel(event);
        eventsPanel.add(eventPanel);
        eventsPanel.add(Box.createVerticalStrut(5));
      }
    }

    revalidate();
    repaint();
  }

  /**
   * Creates a panel to display a single event.
   *
   * @param event the event to display
   * @return a panel containing the event information
   */
  private JPanel createEventPanel(IEvent event) {
    JPanel eventPanel = new JPanel();
    eventPanel.setLayout(new BorderLayout());
    eventPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
    ));

    JPanel titlePanel = new JPanel(new BorderLayout());
    JLabel titleLabel = new JLabel(event.getSubject());
    titleLabel.setFont(new Font("Sans-Serif", Font.BOLD, 14));

    String timeString = event.getStartTime().format(timeFormatter) +
            " - " +
            event.getEndTime().format(timeFormatter);
    JLabel timeLabel = new JLabel(timeString);
    timeLabel.setFont(new Font("Sans-Serif", Font.PLAIN, 12));

    titlePanel.add(titleLabel, BorderLayout.NORTH);
    titlePanel.add(timeLabel, BorderLayout.CENTER);

    JLabel descLabel = new JLabel(event.getDescription());
    descLabel.setFont(new Font("Sans-Serif", Font.PLAIN, 12));

    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    JButton editButton = new JButton("Edit");
    buttonPanel.add(editButton);

    editButton.addActionListener(e -> {
      openEditEventForm(event);
    });

    eventPanel.add(titlePanel, BorderLayout.NORTH);
    eventPanel.add(descLabel, BorderLayout.CENTER);
    eventPanel.add(buttonPanel, BorderLayout.SOUTH);

    return eventPanel;
  }

  /**
   * Opens the event creation form.
   */
  private void openCreateEventForm() {
    EventForm.openCreateEventForm(parentFrame, controller, currentDate);
    refreshListener.run();
  }

  /**
   * Opens the batch event editing form.
   */
  private void openBatchEditForm() {
    EventForm.openBatchEditForm(parentFrame, controller);
    refreshListener.run();
  }

  /**
   * Opens the event editing form.
   *
   * @param event the event to edit
   */
  private void openEditEventForm(IEvent event) {
    EventForm.openEditEventForm(parentFrame, controller, event);
    refreshListener.run();
  }
}
