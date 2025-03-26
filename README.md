# Calendar Application

## Overview
This Calendar application provides an I/O interface that enables users to create and manage their personal scheduling system. The application supports both interactive and headless operation modes, allowing for manual input or batch processing of commands. Key features include support for multiple calendars, timezone management, and conflict resolution.

## Operation Modes

### Interactive Mode
In this mode, users can manually input commands to build and manage their calendar. Users can return to mode selection by entering the `mode` command, or exit the application with the `exit` command.

### Headless Mode
This mode allows users to provide a text file containing a sequence of commands for batch processing. The application processes commands line by line and requires an `exit` command at the end of the file to ensure proper termination. Upon completion, the application returns to mode selection.

### Exit
This option terminates the application.

## Supported Commands

### Calendar Management

#### Creating a Calendar
```
create calendar --name <calName> --timezone <area/location>
```
Creates a new calendar with a unique name and timezone. The timezone format follows the IANA Time Zone Database format (e.g., "America/New_York", "Europe/Paris").

#### Editing a Calendar
```
edit calendar --name <name-of-calendar> --property <property-name> <new-property-value>
```
Modifies an existing property (name or timezone) of the calendar.

#### Setting Active Calendar
```
use calendar --name <name-of-calendar>
```
Sets the specified calendar as the active calendar. This command must be executed before using event-related commands.

### Event Creation

#### Single Events
```
create event <eventName> from <dateString>T<timeString> to <dateString>T<timeString>
```
Creates a single event in the active calendar. The `dateString` should be in "YYYY-MM-DD" format, and the `timeString` in "HH:MM" format.

#### All-Day Single Events
```
create event <eventName> on <dateString>T<timeString>
```
Creates a single all-day event in the active calendar.

#### Recurring Events
```
create event <eventName> from <dateString>T<timeString> to <dateString>T<timeString> repeats <weekdays> for <N> times
```
Creates a recurring event that repeats N times on specified weekdays.

```
create event <eventName> from <dateString>T<timeString> to <dateString>T<timeString> repeats <weekdays> until <dateString>T<timeString>
```
Creates a recurring event that continues until a specific date (inclusive).

#### All-Day Recurring Events
```
create event <eventName> on <dateString> repeats <weekdays> for <N> times
```
Creates a recurring all-day event that repeats N times on specified weekdays.

```
create event <eventName> on <dateString> repeats <weekdays> until <dateString>
```
Creates a recurring all-day event until a specific date (inclusive).

**Note**: By default, the application will decline event creation if a scheduling conflict exists.

**Weekday Codes**:
- M: Monday
- T: Tuesday
- W: Wednesday
- R: Thursday
- F: Friday
- S: Saturday
- U: Sunday

### Event Editing

#### Editing a Single Event
```
edit event <property> <eventName> from <dateString>T<timeString> to <dateString>T<timeString> with <NewPropertyValue>
```
Modifies the specified property of a single event.

#### Editing Multiple Events
```
edit events <property> <eventName> from <dateString>T<timeString> with <NewPropertyValue>
```
Modifies the specified property of all events with the given name starting at or after the specified date/time.

```
edit events <property> <eventName> <NewPropertyValue>
```
Modifies the specified property of all events with the given name.

**Note**: Editing an event that would create a conflict with another existing event is not allowed.

### Copying Events

#### Copy a Single Event
```
copy event <eventName> on <dateStringTtimeString> --target <calendarName> to <dateStringTtimeString>
```
Copies a specific event from the active calendar to the target calendar, scheduled to start at the specified date/time.

#### Copy Events for a Day
```
copy events on <dateString> --target <calendarName> to <dateString>
```
Copies all events scheduled on the specified day from the active calendar to the target calendar. The times remain the same but are converted to the timezone of the target calendar.

#### Copy Events in a Date Range
```
copy events between <dateString> and <dateString> --target <calendarName> to <dateString>
```
Copies all events scheduled in the specified date interval from the active calendar to the target calendar. The target date corresponds to the start of the interval.

### Viewing Events

```
print events on <dateString>
```
Displays a bulleted list of all events scheduled for the specified date in the active calendar, including start/end times and location (if available).

```
print events from <dateString>T<timeString> to <dateString>T<timeString>
```
Displays a bulleted list of all events within the specified time interval in the active calendar, including start/end times and location (if available).

### Calendar Export

```
export cal <fileName>.csv
```
Exports the active calendar to a CSV file compatible with Google Calendar import functionality.

### Status Check

```
show status on <dateString>T<timeString>
```
Displays "busy" if events are scheduled at the specified date and time in the active calendar, otherwise displays "available".

## Execution Instructions

### Without JAR
1. Run the Interpreter main class
2. Select an operation mode:
    - Interactive: For manual command input
    - Headless: For processing commands from a text file
    - Exit: To terminate the application

For headless mode, the command file should be placed in the project root directory or specified with an absolute path.

### With JAR
Run the following command in order to run the JAR file. Please make sure to create an artifact at first.
```
 java -jar NameOfJARFile.jar 
```

## New Design Changes
- Separate and organize the programs into different modules, align with the MVC design: view, controller, manager and model.
- View: Interpreter class still has the responsibility of managing the I/O operations.
- Controller: Command Controller is still handling the parsing for each command and map it to the correct function call.
- Manager:
  - Calendar Manager: A new manager for calendar operations to include the logic for each command.
  - Event Manager: A new class separate from the old Calendar class to solely manage the event operations, including every logic for event commands.
  - Adding interfaces for each manager class for better documentations.
- Model: All models for Events still remain the same with minor name changes. Adding interface and class implementation for Calendar to support new model. 
- Utils: New Package for utility functions, dedicated for each domain:
  - Date Time Utils: Manage utilities functions for converting date time objects or changing time zone.
  - Event Utils: Utilities functions for events domain, specifically the conflict management.
  - Export Utils: Utilities function for exporting, including function to convert the name to suitable type for CSV export.
- Add more unit tests to cover edge case.

## Work distributions
- Hau Ha: Handle calendar and event manager logic and interactive logic.
- Anand Pinnamaneni: Handle the interface-abstract design with unit tests.