package calendar.view.components;

import calendar.controller.SwingController;

import javax.swing.*;

import java.awt.*;
import java.io.File;

/**
 * Panel with functionality to import and export calendar data from/to CSV.
 */
public class CsvImportExportPanel extends JPanel {
  private final SwingController controller;
  private final JFrame parentFrame;

  /**
   * Constructs a new CsvImportExportPanel.
   *
   * @param controller  the controller
   * @param parentFrame the parent frame
   */
  public CsvImportExportPanel(SwingController controller, JFrame parentFrame) {
    this.controller = controller;
    this.parentFrame = parentFrame;
    setLayout(new BorderLayout());
    setBorder(BorderFactory.createTitledBorder("CSV Import/Export"));
    JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 0));
    JButton importButton = new JButton("Import from CSV");
    JButton exportButton = new JButton("Export to CSV");
    buttonPanel.add(importButton);
    buttonPanel.add(exportButton);
    importButton.addActionListener(e -> importFromCsv());
    exportButton.addActionListener(e -> exportToCsv());
    add(buttonPanel, BorderLayout.CENTER);
  }

  /**
   * Shows a file chooser dialog and imports events from a CSV file.
   */
  private void importFromCsv() {
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setDialogTitle("Select CSV File to Import");
    fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
    int returnValue = fileChooser.showOpenDialog(this);
    if (returnValue == JFileChooser.APPROVE_OPTION) {
      File selectedFile = fileChooser.getSelectedFile();
      try {
        String currentCalendar = controller.getCurrentCalendar();
        if (currentCalendar == null) {
          throw new IllegalStateException("No calendar selected for import");
        }
        controller.importCalendarFromCSV(selectedFile.getAbsolutePath());
        JOptionPane.showMessageDialog(parentFrame,
                "Successfully imported events from " + selectedFile.getName(),
                "Import Successful", JOptionPane.INFORMATION_MESSAGE);
      } catch (Exception e) {
        JOptionPane.showMessageDialog(parentFrame,
                "Error importing CSV: " + e.getMessage(),
                "Import Error", JOptionPane.ERROR_MESSAGE);
      }
    }
  }

  /**
   * Shows a file chooser dialog and exports events to a CSV file.
   */
  private void exportToCsv() {
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setDialogTitle("Save CSV File");
    fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
    int returnValue = fileChooser.showSaveDialog(this);
    if (returnValue == JFileChooser.APPROVE_OPTION) {
      File selectedFile = fileChooser.getSelectedFile();

      String filePath = selectedFile.getAbsolutePath();
      if (!filePath.toLowerCase().endsWith(".csv")) {
        filePath += ".csv";
      }
      try {
        String currentCalendar = controller.getCurrentCalendar();
        if (currentCalendar == null) {
          throw new IllegalStateException("No calendar selected for export");
        }
        controller.exportCalendarToCSV(filePath);
        JOptionPane.showMessageDialog(parentFrame,
                "Successfully exported events to " + filePath,
                "Export Successful", JOptionPane.INFORMATION_MESSAGE);
      } catch (Exception e) {
        JOptionPane.showMessageDialog(parentFrame,
                "Error exporting to CSV: " + e.getMessage(),
                "Export Error", JOptionPane.ERROR_MESSAGE);
      }
    }
  }
}
