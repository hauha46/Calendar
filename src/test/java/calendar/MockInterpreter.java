package calendar;

import calendar.controller.CommandController;
import calendar.view.Interpreter;

/**
 * This class mock for the Interpreter , used for isolating classes.
 */
public class MockInterpreter extends Interpreter {
  public String lastModeSelected = "";
  public String lastInputProcessed = "";
  public boolean exitCalled = false;

  @Override
  public void run(CommandController commandController) {
    // Empty implementation for testing
  }

}