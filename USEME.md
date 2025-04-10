# GUI Usage Instructions

This document provides a step-by-step guide for using the Calendar Application's GUI to perform the supported operations. Screenshots can be added later to further illustrate each step.

## Navigating the Calendar
* **Switch Months:**
    * Click the **"◀ Previous"** button to view the previous month.
    * Click the **"Next ▶"** button to view the next month.
* **Return to Today:**
    * Click the **"Today"** button to quickly navigate back to the current date.
* **Select a Specific Day:**
    * Click on any day cell in the month view to display that day's events in the day view panel.

## Calendar Management
* **Select a Calendar:**
    * Use the dropdown list at the top-right corner of the UI to switch between available calendars.
* **Add a New Calendar:**
    * Click the **"Add Calendar"** button.
    * In the popup form, enter the new calendar's name.
    * Select the appropriate timezone from the list.
    * Click **"Create"** to add the new calendar.
* **Edit an Existing Calendar:**
    * With a calendar selected, click the **"Edit Calendar"** button.
    * Update the calendar's properties (name and/or timezone) in the dialog.
    * Click **"Update"** to save the changes.

## Event Management
* **Add a New Event**
    * In the day view panel, click the **"Add Event"** button.
    * In the event creation form, fill in:
        * **Subject:** The event name.
        * **Description:** Details about the event.
        * **Start Date & Time:** Specify when the event begins.
        * **End Date & Time:** Specify when the event ends.
    * **Recurring Events (Optional):**
        * Check the **"Recurring Event"** checkbox if the event should repeat.
        * Enter the recurring days (e.g., "MTWRFSU" where each letter represents a day of the week).
        * Specify the number of occurrences or the end recurring date.
    * Click **"Create"** to add the event.
* **Edit a Single Event**
    * In the day view panel, each event is listed with an **"Edit"** button.
    * Click the **"Edit"** button corresponding to the event you wish to modify.
    * Update the event information in the form provided.
    * Click **"Save"** to apply the changes.
* **Batch Edit Multiple Events**
    * In the day view panel, click the **"Edit Multiple Events"** button.
    * In the batch editing dialog, specify:
        * **Event Name:** The name of the events you want to edit.
        * **Date Range:** The start and end dates in which to search for the events.
        * **Property to Edit:** Choose the event property (name, description, start time, or end time).
        * **New Value:** Provide the new value to update all matching events.
    * Click **"Apply to All"** to commit the changes.

## CSV Import and Export
* **Import Events from CSV**
    * Click the **"Import from CSV"** button found in the CSV Import/Export panel.
    * A file chooser dialog will open; select the CSV file that contains event data.
    * The system will attempt to import the events into the currently active calendar.
* **Export Events to CSV**
    * Click the **"Export to CSV"** button in the CSV Import/Export panel.
    * Choose the destination and filename for the exported CSV file.
    * The calendar events will be written to a CSV file that can be used with Google Calendar or other applications.

## Additional Options
* **Switch to Command-Line Interface (CLI) Mode**
    * If preferred, click the **"Switch to CLI"** button to exit the GUI, close the GUI window and launch the command-line interface. This mode is ideal for advanced users who want to use textual commands.