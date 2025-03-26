package calendar.controller;

import calendar.controller.CommandController;
import calendar.manager.CalendarManager;
import calendar.view.Interpreter;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
  public static void main(String[] args) {
    //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
    // to see how IntelliJ IDEA suggests fixing it.
    Interpreter view = new Interpreter();
    CalendarManager model = new CalendarManager();
    CommandController controller = new CommandController(model,view);
    controller.start();
  }
}