package calendar;

import calendar.controller.CommandController;
import calendar.manager.CalendarManager;
import calendar.view.Interpreter;

public class Main {
  public static void main(String[] args) {
    Interpreter view = new Interpreter();
    CalendarManager model = new CalendarManager();
    CommandController controller = new CommandController(model, view);
    controller.start();
  }
}