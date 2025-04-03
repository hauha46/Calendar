package calendar.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import calendar.controller.SwingController;
import calendar.manager.CalendarManager;
import calendar.model.IEvent;
import calendar.view.components.CalendarForm;
import calendar.view.components.DayViewPanel;
import calendar.view.components.CsvImportExportPanel;

/**
 * A graphical user interface for the calendar application using Swing.
 */
public class SwingUI {
  private JFrame frame;
  private JPanel calendarPanel;
  private JLabel monthLabel;
  private JComboBox<String> calendarDropdown;
  private JPanel dayViewContainer;
  private DayViewPanel dayViewPanel;
  private YearMonth currentMonth;
  private LocalDate selectedDate;
  private SwingController controller;
  private final Color[] CALENDAR_COLORS = {
    new Color(200, 230, 255), // Light blue
    new Color(255, 200, 200), // Light pink
    new Color(200, 255, 200), // Light green
    new Color(255, 255, 200), // Light yellow
    new Color(255, 200, 255)  // Light purple
  };
  private Map<String, Color> calendarColorMap = new HashMap<>();

  /**
   * Constructs a new SwingUI.
   */
  public SwingUI() {
    CalendarManager manager = new CalendarManager();
    controller = new SwingController(manager);

    TimeZone timeZone = TimeZone.getDefault();
    controller.createCalendar("Default Calendar", timeZone.getID());

    setupMainFrame();
    setupTopPanel();
    setupCalendarPanel();
    setupDayViewPanel();
    setupBottomPanel();

    currentMonth = YearMonth.now();
    selectedDate = LocalDate.now();
    refreshUI();
    frame.setVisible(true);
  }

  /**
   * Sets up the main JFrame.
   */
  private void setupMainFrame() {
    frame = new JFrame("Calendar Application");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(1000, 700);
    frame.setMinimumSize(new Dimension(800, 600));
    frame.setLayout(new BorderLayout(10, 10));
    ((JComponent) frame.getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));
  }

  /**
   * Sets up the top panel with navigation controls and calendar selection.
   */
  private void setupTopPanel() {
    JPanel topPanel = new JPanel(new BorderLayout(10, 0));
    topPanel.setBorder(new EmptyBorder(0, 0, 10, 0));
    JPanel navigationPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    JButton prevButton = new JButton("◀ Previous");
    monthLabel = new JLabel("", SwingConstants.CENTER);
    monthLabel.setFont(new Font("Sans-Serif", Font.BOLD, 16));
    JButton nextButton = new JButton("Next ▶");
    JButton todayButton = new JButton("Today");
    
    navigationPanel.add(prevButton);
    navigationPanel.add(monthLabel);
    navigationPanel.add(nextButton);
    navigationPanel.add(todayButton);

    JPanel calendarSelectionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    JLabel calendarLabel = new JLabel("Calendar: ");
    calendarDropdown = new JComboBox<>();
    JButton addCalendarButton = new JButton("Add Calendar");
    JButton editCalendarButton = new JButton("Edit Calendar");
    
    calendarSelectionPanel.add(calendarLabel);
    calendarSelectionPanel.add(calendarDropdown);
    calendarSelectionPanel.add(addCalendarButton);
    calendarSelectionPanel.add(editCalendarButton);
    
    topPanel.add(navigationPanel, BorderLayout.WEST);
    topPanel.add(calendarSelectionPanel, BorderLayout.EAST);

    prevButton.addActionListener(e -> {
      currentMonth = currentMonth.minusMonths(1);
      renderCalendar();
    });
    
    nextButton.addActionListener(e -> {
      currentMonth = currentMonth.plusMonths(1);
      renderCalendar();
    });
    
    todayButton.addActionListener(e -> {
      currentMonth = YearMonth.now();
      selectedDate = LocalDate.now();
      renderCalendar();
      dayViewPanel.setDate(selectedDate);
    });

    calendarDropdown.addActionListener(e -> {
      if (calendarDropdown.getSelectedItem() != null) {
        String selectedCalendar = (String) calendarDropdown.getSelectedItem();
        try {
          controller.setCurrentCalendar(selectedCalendar);
          refreshUI();
        } catch (IllegalArgumentException ex) {
          JOptionPane.showMessageDialog(frame, 
              "Error selecting calendar: " + ex.getMessage(),
              "Error", JOptionPane.ERROR_MESSAGE);
        }
      }
    });

    addCalendarButton.addActionListener(e -> {
      CalendarForm.openCreateCalendarForm(frame, controller, this::refreshUI);
    });

    editCalendarButton.addActionListener(e -> {
      String currentCalendarName = controller.getCurrentCalendar();
      CalendarForm.openEditCalendarForm(frame, controller, currentCalendarName, this::refreshUI);
    });
    
    frame.add(topPanel, BorderLayout.NORTH);
  }

  /**
   * Sets up the calendar panel displaying the month view.
   */
  private void setupCalendarPanel() {
    JPanel leftPanel = new JPanel(new BorderLayout());
    leftPanel.setPreferredSize(new Dimension(550, 0));
    calendarPanel = new JPanel();
    leftPanel.add(calendarPanel, BorderLayout.CENTER);
    frame.add(leftPanel, BorderLayout.CENTER);
  }

  /**
   * Sets up the day view panel for displaying and editing events on a selected day.
   */
  private void setupDayViewPanel() {
    dayViewContainer = new JPanel(new BorderLayout());
    dayViewContainer.setPreferredSize(new Dimension(400, 0));
    dayViewPanel = new DayViewPanel(controller, frame);
    dayViewPanel.setRefreshListener(this::refreshUI);
    dayViewContainer.add(dayViewPanel, BorderLayout.CENTER);
    frame.add(dayViewContainer, BorderLayout.EAST);
  }

  /**
   * Sets up the bottom panel with additional controls.
   */
  private void setupBottomPanel() {
    JPanel bottomPanel = new JPanel(new BorderLayout());
    bottomPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
    CsvImportExportPanel csvPanel = new CsvImportExportPanel(controller, frame);
    bottomPanel.add(csvPanel, BorderLayout.EAST);
    JLabel statusLabel = new JLabel("Ready");
    bottomPanel.add(statusLabel, BorderLayout.WEST);
    frame.add(bottomPanel, BorderLayout.SOUTH);
  }

  /**
   * Refreshes the entire UI including calendar list, month view, and day view.
   * Use this after operations that change the calendar or event data.
   */
  public void refreshUI() {
    populateCalendarDropdown();
    renderCalendar();
    dayViewPanel.setDate(selectedDate);
  }

  /**
   * Refreshes the calendar dropdown with available calendars.
   */
  private void populateCalendarDropdown() {
    String currentSelection = controller.getCurrentCalendar();
    calendarDropdown.removeAllItems();
    List<String> calendars = controller.getAllCalendarNames();
    for (String calendar : calendars) {
      calendarDropdown.addItem(calendar);
      if (!calendarColorMap.containsKey(calendar)) {
        int colorIndex = calendarColorMap.size() % CALENDAR_COLORS.length;
        calendarColorMap.put(calendar, CALENDAR_COLORS[colorIndex]);
      }
    }
    
    if (currentSelection != null && calendars.contains(currentSelection)) {
      calendarDropdown.setSelectedItem(currentSelection);
    }
  }

  private void renderCalendar() {
    calendarPanel.removeAll();
    calendarPanel.setLayout(new BorderLayout());
    monthLabel.setText(currentMonth.getMonth().toString() + " " + currentMonth.getYear());

    JPanel gridPanel = new JPanel(new GridLayout(0, 7));
    String[] daysOfWeek = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
    for (String day : daysOfWeek) {
      JLabel dayLabel = new JLabel(day, SwingConstants.CENTER);
      dayLabel.setFont(new Font("Sans-Serif", Font.BOLD, 12));
      dayLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
      gridPanel.add(dayLabel);
    }
    LocalDate firstDayOfMonth = currentMonth.atDay(1);
    int firstDayOfWeek = firstDayOfMonth.getDayOfWeek().getValue() % 7;

    List<IEvent> monthEvents = new ArrayList<>();
    try {
      LocalDate monthStart = currentMonth.atDay(1);
      LocalDate monthEnd = currentMonth.atEndOfMonth();
      monthEvents = controller.getEventsForMonth(monthStart, monthEnd);
    } catch (Exception e) {
      JOptionPane.showMessageDialog(frame, 
          "Error retrieving events: " + e.getMessage(),
          "Error", JOptionPane.ERROR_MESSAGE);
    }

    Map<LocalDate, List<IEvent>> eventsByDay = new HashMap<>();
    for (IEvent event : monthEvents) {
      LocalDate eventDate = event.getStartTime().toLocalDate();
      eventsByDay.computeIfAbsent(eventDate, k -> new ArrayList<>()).add(event);
    }

    for (int i = 0; i < firstDayOfWeek; i++) {
      JPanel emptyPanel = new JPanel();
      emptyPanel.setBackground(Color.LIGHT_GRAY);
      gridPanel.add(emptyPanel);
    }

    for (int day = 1; day <= currentMonth.lengthOfMonth(); day++) {
      LocalDate date = currentMonth.atDay(day);
      JPanel dayPanel = createDayPanel(day, date, eventsByDay.getOrDefault(date, new ArrayList<>()));
      gridPanel.add(dayPanel);
    }
    
    calendarPanel.add(gridPanel, BorderLayout.CENTER);
    calendarPanel.revalidate();
    calendarPanel.repaint();
  }

  /**
   * Creates a panel representing a single day in the calendar.
   *
   * @param dayNumber the day number
   * @param date      the date
   * @param events    list of events for this day
   * @return the day panel
   */
  private JPanel createDayPanel(int dayNumber, LocalDate date, List<IEvent> events) {
    JPanel dayPanel = new JPanel();
    dayPanel.setLayout(new BorderLayout());
    dayPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
    if (date.equals(selectedDate)) {
      dayPanel.setBackground(new Color(173, 216, 230));
    } else {
      dayPanel.setBackground(Color.WHITE);
    }

    JLabel dayLabel = new JLabel(String.valueOf(dayNumber), SwingConstants.RIGHT);
    dayLabel.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));

    if (date.equals(LocalDate.now())) {
      dayLabel.setFont(new Font("Sans-Serif", Font.BOLD, 12));
      dayLabel.setForeground(Color.RED);
    }

    JPanel eventsPanel = new JPanel();
    eventsPanel.setLayout(new BoxLayout(eventsPanel, BoxLayout.Y_AXIS));
    eventsPanel.setBackground(dayPanel.getBackground());
    int maxDisplayEvents = 3;

    for (int i = 0; i < Math.min(events.size(), maxDisplayEvents); i++) {
      IEvent event = events.get(i);
      JLabel eventLabel = createEventLabel(event);
      eventsPanel.add(eventLabel);
    }

    if (events.size() > maxDisplayEvents) {
      JLabel moreLabel = new JLabel("+" + (events.size() - maxDisplayEvents) + " more", SwingConstants.CENTER);
      moreLabel.setFont(new Font("Sans-Serif", Font.ITALIC, 10));
      eventsPanel.add(moreLabel);
    }
    
    dayPanel.add(dayLabel, BorderLayout.NORTH);
    dayPanel.add(eventsPanel, BorderLayout.CENTER);

    dayPanel.addMouseListener(new java.awt.event.MouseAdapter() {
      @Override
      public void mouseClicked(java.awt.event.MouseEvent evt) {
        selectedDate = date;
        dayViewPanel.setDate(selectedDate);
        renderCalendar();
      }
    });
    
    return dayPanel;
  }

  /**
   * Creates a label representing an event in the calendar.
   *
   * @param event the event
   * @return the event label
   */
  private JLabel createEventLabel(IEvent event) {
    JLabel eventLabel = new JLabel(" • " + event.getSubject());
    eventLabel.setFont(new Font("Sans-Serif", Font.PLAIN, 10));

    String currentCalendar = controller.getCurrentCalendar();
    if (currentCalendar != null && calendarColorMap.containsKey(currentCalendar)) {
      Color calendarColor = calendarColorMap.get(currentCalendar);
      eventLabel.setForeground(new Color(calendarColor.getRGB()).darker());
      eventLabel.setBackground(calendarColor);
      eventLabel.setOpaque(true);
    }
    
    return eventLabel;
  }

  /**
   * Application entry point.
   *
   * @param args command line arguments
   */
  public static void main(String[] args) {
    /**
     * Runs the GUI asynchronously without blocking.
     */
    SwingUtilities.invokeLater(() -> new SwingUI());
  }
}
