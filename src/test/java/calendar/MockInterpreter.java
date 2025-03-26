package calendar;

import calendar.controller.CommandController;
import calendar.view.Interpreter;

public class MockInterpreter extends Interpreter {
  public String lastModeSelected = "";
  public String lastInputProcessed = "";
  public boolean exitCalled = false;

  @Override
  public void run(CommandController commandController) {
    // Empty implementation for testing
  }

  public void simulateModeSelection(String mode) {
    lastModeSelected = mode;
  }

  public void simulateInput(String input) {
    lastInputProcessed = input;
  }

  public void simulateExit() {
    exitCalled = true;
  }
}