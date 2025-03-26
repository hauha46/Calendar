package calendar;

import calendar.view.Interpreter;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for the Intrepreter, checking its functions.
 */
public class IntrepreterTest {
  private Interpreter interpreter;
  private MockCommandController mockController;
  private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
  private final PrintStream originalOut = System.out;
  private final InputStream originalIn = System.in;

  /**
   * Setting up the required base inputs used by the tests.
   */
  @Before
  public void setUp() {
    interpreter = new Interpreter();
    mockController = new MockCommandController();
    System.setOut(new PrintStream(outContent));
  }

  /**
   * Resetting to the original Streams , so that they don't affect other tests.
   */
  @After
  public void tearDown() {
    System.setOut(originalOut);
    System.setIn(originalIn);
  }

  /**
   * Checking if the display is correct.
   */
  @Test
  public void testRunDisplaysWelcomeMessage() {
    provideInput("exit\n");
    interpreter.run(mockController);

    String output = getOutput();
    assertTrue(output.contains("Welcome to the Calendar Application!"));
    assertTrue(output.contains("Select mode: 'interactive', 'headless', or 'exit'"));
    assertTrue(output.contains("> "));
  }

  /**
   * display the selection prompt after returning from a selected mode.
   */
  @Test
  public void testRunDisplaysModePrompt() {
    provideInput("interactive\nmode\nexit\n");
    interpreter.run(mockController);

    String output = getOutput();
    assertTrue(output.split("Select mode:").length >= 2);
    assertTrue(output.split("> ").length >= 3);
  }

  /**
   * Test for terminating the program.
   */
  @Test
  public void testRunExitCommand() {
    provideInput("exit\n");
    interpreter.run(mockController);
    assertOutputContains("Exiting the application. Goodbye!");
    assertFalse(mockController.parseCommandCalled);
  }

  /**
   * Test for Interactivemode valid with command and then terminate.
   */
  @Test
  public void testRunInteractiveModeValidCommand() {
    provideInput("interactive\ncreate calendar Test\nmode\nexit\n");
    interpreter.run(mockController);
    assertOutputContains("Interactive mode");
    assertOutputContains("> ");
    assertTrue(mockController.parseCommandCalled);
    assertEquals("create calendar Test", mockController.lastParsedCommand);
  }


  /**
   * Test for checking wrong input command in InteractiveMode.
   */
  @Test
  public void testRunInteractiveModeInvalidCommand() {
    mockController.exceptionToThrow = new IllegalArgumentException("Invalid command");
    provideInput("interactive\ninvalid command\nmode\nexit\n");
    interpreter.run(mockController);
    assertOutputContains("Error: Invalid command");
  }

  /**
   * Tests headless mode execution with a valid command file.
   *
   * @throws Exception if temporary file creation fails
   */
  @Test
  public void testRunHeadlessModeValidFile() throws Exception {
    Path tempFile = createTempFile("create event Meeting\nprint events\nexit\n");
    try {
      provideInput("headless\n" + tempFile.toString() + "\nexit\n");
      interpreter.run(mockController);
      assertOutputContains("Enter the path to the command file:");
      assertOutputContains("> ");
      assertOutputContains("Running in headless mode");
      assertTrue(mockController.parseCommandCallCount >= 2);
      assertOutputContains("Command: " + "print events");
      assertTrue("print events".equals(mockController.lastParsedCommand) ||
              "create event Meeting".equals(mockController.lastParsedCommand));
      assertOutputContains("Exit command found. Terminating headless mode.");
    } finally {
      Files.deleteIfExists(tempFile);
    }
  }

  /**
   * Tests handling for headlessmode no file present.
   */
  @Test
  public void testRunHeadlessModeFileNotFound() {
    provideInput("headless\nnonexistent.txt\nexit\n");
    interpreter.run(mockController);
    assertOutputContains("Could not find file");
    assertFalse(mockController.parseCommandCalled);
  }

  /**
   * Test for HeadlessMode Invalid command.
   */
  @Test(expected = AssertionError.class)
  public void testRunHeadlessModeCommandError() throws Exception {
    mockController.exceptionToThrow = new IllegalArgumentException("Test error");
    Path tempFile = createTempFile("invalid command\nexit\n");
    try {

      provideInput("headless\n" + tempFile.toString() + "\nexit\n");
      interpreter.run(mockController);

      // Verify error handling
      assertOutputContains("Error at line 1");
      assertOutputContains("Reason: Test error");
      // Verify we exited headless mode
      assertOutputContains("Terminating headless mode");
    } finally {
      Files.deleteIfExists(tempFile);
    }
  }

  /**
   * Tests handling of invalid mode selection input.
   */
  @Test
  public void testRunInvalidMode() {
    provideInput("invalid\nexit\n");
    interpreter.run(mockController);
    assertOutputContains("Invalid mode");
    assertFalse(mockController.parseCommandCalled);
  }

  /**
   * Test for returning to mode selection after selecting a mode.
   */
  @Test
  public void testRunInteractiveModeReturnToMainMenu() {
    provideInput("interactive\nmode\nexit\n");
    interpreter.run(mockController);
    assertOutputContains("Returning to mode selection");
    assertFalse(mockController.parseCommandCalled);
  }

  /**
   * Provides test input by using ByteArray to store input.
   *
   * @param data the input string to simulate
   */
  private void provideInput(String data) {
    System.setIn(new ByteArrayInputStream(data.getBytes()));
  }

  /**
   * Special output assertion to check system.out.
   *
   * @param expected
   */
  private void assertOutputContains(String expected) {
    assertTrue("Output should contain: " + expected,
            outContent.toString().contains(expected));
  }

  /**
   * This creates a temporary file and returns its path.
   *
   * @param content
   * @return the file path.
   * @throws Exception
   */
  private Path createTempFile(String content) throws Exception {
    Path tempFile = Files.createTempFile("test-commands", ".txt");
    Files.writeString(tempFile, content);
    return tempFile;
  }

  /**
   * Gets the captured output content.
   *
   * @return the output as a string
   */
  private String getOutput() {
    return outContent.toString();
  }
}