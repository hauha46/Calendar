package calendar;

//contaiing only commands present inside the calanderManager as OckClass is preset.
import calendar.controller.CommandController;
/**
 * This class mock for the CommandController , used for isolating classes.
 */
public class MockCommandController extends CommandController {
  public String lastParsedCommand;
  public boolean parseCommandCalled = false;
  public RuntimeException exceptionToThrow = null;
  public int parseCommandCallCount = 0;

  /**
   * The public constructor extending the super class CommandController and filling null.
   */
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