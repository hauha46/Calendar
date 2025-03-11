package calendar;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParseCommand {

  private CommandOperator operator;
  ParseCommand()
  {
    this.operator=new CommandOperator();
  }

  /**
   * breaks down the input given into words and call the appropriate methods according to the first
   * word.
   * @param input
   */
  public void inputparser(String input)
  {
    List<String> commandWords = Arrays.asList(input.split(" "));
    if (commandWords.isEmpty()) {
      System.out.println("Empty command.");
      return;
    }
    String commandType = commandWords.get(0).toLowerCase();
    if (commandType.equals("create")) {
      parseCreateCommand(input);
    } else if (commandType.equals("edit")) {
      parseEditCommand(input);
    } else if (commandType.equals("print")) {
      parsePrintCommand(input);
    } else if (commandType.equals("show")) {
      parseShowCommand(input);
    } else {
      System.out.println("Unknown command: " + commandType);
    }
  }
  //create event --autoDecline <eventName> from <dateStringTtimeString> to <dateStringTtimeString>
//create event --autoDecline <eventName> from <dateStringTtimeString> to <dateStringTtimeString> repeats <weekdays> for <N> times
//create event --autoDecline <eventName> from <dateStringTtimeString> to <dateStringTtimeString> repeats <weekdays> until <dateStringTtimeString>
  String createFromPattern = "^create\\s+event\\s+(--autoDecline\\s+)?(\\S+)\\s+from\\s+(\\d{4}-\\d{2}-\\d{2}\\s+\\d{2}:\\d{2})\\s+to\\s+(\\S+)(?:\\s+repeats\\s+(\\d{4}-\\d{2}-\\d{2}\\s+\\d{2}:\\d{2})\\s+(for|until)\\s+(\\S+))?$";
  //create event --autoDecline <eventName> on <dateStringTtimeString>
//create event <eventName> on <dateString> repeats <weekdays> for <N> times
//create event <eventName> on <dateString> repeats <weekdays> until <dateString>
  String createOnPattern = "^create\\s+event\\s+(--autoDecline\\s+)?(\\S+)\\s+on\\s+(\\d{4}-\\d{2}-\\d{2}\\s+\\d{2}:\\d{2})(?:\\s+repeats\\s+(\\d{4}-\\d{2}-\\d{2}\\s+\\d{2}:\\d{2})\\s+(for|until)\\s+(\\S+))?$";
  //edit event <property> <eventName> from <dateStringTtimeString> to <dateStringTtimeString> with <NewPropertyValue>
  String editEventPattern1 = "^edit\\s+event\\s+(\\S+)\\s+(\\S+)\\s+from\\s+(\\d{4}-\\d{2}-\\d{2}\\s+\\d{2}:\\d{2})\\s+to\\s+(\\d{4}-\\d{2}-\\d{2}\\s+\\d{2}:\\d{2})\\s+with\\s+(.+)$";
  //edit events <property> <eventName> from <dateStringTtimeString> with <NewPropertyValue>
  String editEventsPattern2 = "^edit\\s+events\\s+(\\S+)\\s+(\\S+)\\s+from\\s+(\\d{4}-\\d{2}-\\d{2}\\s+\\d{2}:\\d{2})\\s+with\\s+(.+)$";
  //edit events <property> <eventName> <NewPropertyValue>
  String editEventsPattern3 = "^edit\\s+events\\s+(\\S+)\\s+(\\S+)\\s+(.+)$";
  //print events on <dateString>
  String printOnPattern = "^print\\s+events\\s+on\\s+(\\d{4}-\\d{2}-\\d{2}\\s+\\d{2}:\\d{2})$";
  //print events from <dateStringTtimeString> to <dateStringTtimeString>
  String printFromPattern = "^print\\s+events\\s+from\\s+(\\d{4}-\\d{2}-\\d{2}\\s+\\d{2}:\\d{2})\\s+to\\s+(\\d{4}-\\d{2}-\\d{2}\\s+\\d{2}:\\d{2})$";
  //show status on <dateStringTtimeString>
  String showStatusPattern = "^show\\s+status\\s+on\\s+(\\d{4}-\\d{2}-\\d{2}\\s+\\d{2}:\\d{2})$";

//show status on <dateStringTtimeString>

  /**
   * One parses the show command.
   * @param commandWords
   */
  private void parseShowCommand(String commandWords) {

    Pattern pattern;
    Matcher matcher;
    pattern = Pattern.compile(showStatusPattern);
    matcher = pattern.matcher(commandWords);

    if(matcher.matches())
    {
      String startTime = matcher.group(1);
      operator.isBusy(startTime);
    }

  }

  /**
   * only parses the print command.
   * @param commandWords
   */
  //print events on <dateString>
//print events from <dateStringTtimeString> to <dateStringTtimeString>
  private void parsePrintCommand(String commandWords) {
    Pattern pattern;
    Matcher matcher;
       pattern = Pattern.compile(printOnPattern);
       matcher = pattern.matcher(commandWords);

      if(matcher.matches()) {
        String date = matcher.group(1);
        String startTime =date+"00:00";
        String endTime =date+"23:59";
        operator.printEvents(startTime,endTime);

      }
     pattern = Pattern.compile(printFromPattern);
     matcher = pattern.matcher(commandWords);
    if(matcher.matches()) {
      String startTime =matcher.group(1);
      String endTime =matcher.group(2);
      operator.printEvents(startTime,endTime);

    }
  }

  /**
   * only parses the edit command.
   * @param commandWords
   */
  //edit event <property> <eventName> from <dateStringTtimeString> to <dateStringTtimeString> with <NewPropertyValue>
//edit events <property> <eventName> from <dateStringTtimeString> with <NewPropertyValue>
//edit events <property> <eventName> <NewPropertyValue>
  private void parseEditCommand(String commandWords) {
    Pattern pattern;
    Matcher matcher;

    pattern= Pattern.compile(editEventPattern1);
    matcher=pattern.matcher(commandWords);
    if (matcher.matches()) {
      String property = matcher.group(1);
      String eventName = matcher.group(2);
      String startTime = matcher.group(3);
      String endTime = matcher.group(4);
      String newValue = matcher.group(5);
      operator.editEventSingle(eventName,startTime,endTime,property,newValue);
    }

    pattern= Pattern.compile(editEventsPattern2);
    matcher=pattern.matcher(commandWords);
    if(matcher.matches()) {
      String property = matcher.group(1);
      String eventName = matcher.group(2);
      String startTime = matcher.group(3);
      String newValue = matcher.group(4);
      operator.editEventRecurring(eventName,startTime,property,newValue);

    }

    pattern= Pattern.compile(editEventsPattern3);
    matcher=pattern.matcher(commandWords);
    if(matcher.matches()) {
      String property = matcher.group(1);
      String eventName = matcher.group(2);
      String newValue = matcher.group(3);
      operator.editEventRecurring(eventName,"",property,newValue);

    }

  }
  //create event --autoDecline <eventName> from <dateStringTtimeString> to <dateStringTtimeString>
//create event --autoDecline <eventName> from <dateStringTtimeString> to <dateStringTtimeString> repeats <weekdays> for <N> times
//create event --autoDecline <eventName> from <dateStringTtimeString> to <dateStringTtimeString> repeats <weekdays> until <dateStringTtimeString>
//create event --autoDecline <eventName> on <dateStringTtimeString>
//create event <eventName> on <dateString> repeats <weekdays> for <N> times
//create event <eventName> on <dateString> repeats <weekdays> until <dateString>

  /**
   * only parses the create command.
   * @param commandWords
   */
  private void parseCreateCommand(String commandWords) {
    Pattern pattern;
    Matcher matcher;

    pattern= Pattern.compile(createOnPattern);
    matcher=pattern.matcher(commandWords);

    if(matcher.matches())
    {
      boolean autoDecline = (matcher.group(1) != null);
      String eventName = matcher.group(2);
      String date = matcher.group(3);
      String recurringDays = matcher.group(4);
      String forOrUntil = matcher.group(5);
      String value = matcher.group(6);

      if(autoDecline)
      {
        operator.setAutoDecline(true);
      }

      if(recurringDays != null&&forOrUntil!=null&&value!=null)
      {
        if(forOrUntil.equals("until"))
        {
          String startTime = date + "00:00";
          String endTime = date + "23:59";
          operator.createEventRecurringAllDay(eventName,startTime,value,recurringDays,null);
        }
        else if(forOrUntil.equals("for"))
        {
          String startTime = date + "00:00";
          String endTime = date + "23:59";
          operator.createEventRecurringAllDay(eventName,startTime,null,recurringDays,value);

        }
      }
      else{
        String startTime = date + "00:00";
        String endTime = date + "23:59";

        operator.createEventSingle(eventName,startTime,endTime);
      }

    }
    pattern= Pattern.compile(createFromPattern);
    matcher=pattern.matcher(commandWords);
    if(matcher.matches())
    {
      boolean autoDecline = (matcher.group(1) != null);
      String eventName = matcher.group(2);
      String startTime = matcher.group(3);
      String endTime = matcher.group(4);
      String recurringDays = matcher.group(5);
      String forOrUntil = matcher.group(6);
      String value = matcher.group(7);

      if(autoDecline)
      {
        operator.setAutoDecline(true);
      }

      if(recurringDays != null&& forOrUntil != null && value != null)
      {
        if(forOrUntil.equals("for"))
        {
          operator.createEventRecurring(eventName,startTime,endTime,null,recurringDays,value);
        }
        else if(forOrUntil.equals("until"))
        {
          operator.createEventRecurring(eventName,startTime,endTime,value,recurringDays,null);

        }
      }
      else{
        operator.createEventSingle(eventName,startTime,endTime);
      }


    }



  }







}
