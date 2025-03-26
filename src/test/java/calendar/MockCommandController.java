package calendar;

import calendar.controller.CommandController;

public class MockCommandController extends CommandController {
  public String lastParsedCommand;
  public boolean parseCommandCalled = false;
  public RuntimeException exceptionToThrow = null;
  public int parseCommandCallCount = 0;

  public MockCommandController() {
    super(null, null);
  }

  @Override
  public void parseCommand(String input) {
    parseCommandCalled = true;
    lastParsedCommand = input;
    parseCommandCallCount++;

    if (exceptionToThrow != null) {
      throw exceptionToThrow;
    }
  }
}