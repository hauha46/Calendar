# Calendar Application

## Overview
This Calendar application provides an I/O interface that enables users to create and manage their personal scheduling system. The application supports both interactive and headless operation modes, allowing for manual input or batch processing of commands.

## Operation Modes

### Interactive Mode
In this mode, users can manually input commands to build and manage their calendar. Users can return to mode selection by entering the `mode` command, or exit the application with the `exit` command.

### Headless Mode
This mode allows users to provide a text file containing a sequence of commands for batch processing. The application processes commands line by line and requires an `exit` command at the end of the file to ensure proper termination. Upon completion, the application returns to mode selection.

### Exit
This option terminates the application.

## Supported Commands

### Event Creation

#### Single Events
```
create event [--autoDecline] <eventName> from <dateString>T<timeString> to <dateString>T<timeString>
```
Creates a single event in the calendar. The `dateString` should be in "YYYY-MM-DD" format, and the `timeString` in "HH:MM" format.

#### All-Day Single Events
```
create event [--autoDecline] <eventName> on <dateString>T<timeString>
```
Creates a single all-day event.

#### Recurring Events
```
create event [--autoDecline] <eventName> from <dateString>T<timeString> to <dateString>T<timeString> repeats <weekdays> for <N> times
```
Creates a recurring event that repeats N times on specified weekdays.

```
create event [--autoDecline] <eventName> from <dateString>T<timeString> to <dateString>T<timeString> repeats <weekdays> until <dateString>T<timeString>
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

**Note**: For all event creation commands, the `--autoDecline` option is optional. When enabled, event creation will be rejected if a scheduling conflict exists.

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
Modifies the specified property of all events with the given name starting at the specified date/time.

```
edit events <property> <eventName> <NewPropertyValue>
```
Modifies the specified property of all events with the given name.

### Viewing Events

```
print events on <dateString>
```
Displays a bulleted list of all events scheduled for the specified date, including start/end times and location (if available).

```
print events from <dateString>T<timeString> to <dateString>T<timeString>
```
Displays a bulleted list of all events within the specified time interval, including start/end times and location (if available).

### Calendar Export

```
export cal <fileName>.csv
```
Exports the calendar to a CSV file compatible with Google Calendar import functionality.

### Status Check

```
show status on <dateString>T<timeString>
```
Displays "busy" if events are scheduled at the specified date and time, otherwise displays "available".

## Execution Instructions

1. Run the Interpreter main class
2. Select an operation mode:
    - Interactive: For manual command input
    - Headless: For processing commands from a text file
    - Exit: To terminate the application

For headless mode, the command file should be placed in the project root directory or specified with an absolute path.

## Work distributions
- Hau Ha: Handle event manager logic and interactive logic.
- Anand Pinnamaneni: Handle the interface-abstract design with unit tests.