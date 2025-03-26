package calendar;
import calendar.view.Interpreter;

import static org.junit.Assert.*;
import calendar.MockCommandController;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class IntrepreterTest {
  private Interpreter interpreter;
  private MockCommandController mockController;
  private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
  private final PrintStream originalOut = System.out;
  private final InputStream originalIn = System.in;

  @Before
  public void setUp() {
    interpreter = new Interpreter();
    mockController = new MockCommandController();
    System.setOut(new PrintStream(outContent));
  }

  @After
  public void tearDown() {
    System.setOut(originalOut);
    System.setIn(originalIn);
  }

  @Test
  public void testRun_ExitCommand() {
    provideInput("exit\n");
    interpreter.run(mockController);
    assertOutputContains("Exiting the application. Goodbye!");
    assertFalse(mockController.parseCommandCalled);
  }

  @Test
  public void testRun_InteractiveModeValidCommand() {
    provideInput("interactive\ncreate calendar Test\nmode\nexit\n");
    interpreter.run(mockController);
    assertOutputContains("Interactive mode");
    assertTrue(mockController.parseCommandCalled);
    assertEquals("create calendar Test", mockController.lastParsedCommand);
  }

  @Test
  public void testRun_DisplaysWelcomeMessage() {
    provideInput("exit\n");
    interpreter.run(mockController);

    String output = getOutput();
    assertTrue(output.contains("Welcome to the Calendar Application!"));
    assertTrue(output.contains("Select mode: 'interactive', 'headless', or 'exit'"));
    assertTrue(output.contains("> "));
  }

  @Test
  public void testRun_DisplaysModePrompt() {
    provideInput("interactive\nmode\nexit\n");
    interpreter.run(mockController);

    String output = getOutput();
    // Should show mode prompt twice:
    // 1. Initial prompt
    // 2. After returning from interactive mode
    assertTrue(output.split("Select mode:").length >= 2);
    assertTrue(output.split("> ").length >= 3); // Prompts + command input
  }

  @Test
  public void testRun_InteractiveModeInvalidCommand() {
    mockController.exceptionToThrow = new IllegalArgumentException("Invalid command");
    provideInput("interactive\ninvalid command\nmode\nexit\n");
    interpreter.run(mockController);
    assertOutputContains("Error: Invalid command");
  }

//  @Test
//  public void testRun_HeadlessModeValidFile() throws Exception {
//    Path tempFile = createTempFile("create event Meeting\n"+"print events\n"+"exit\n");
//    try {
//      provideInput("headless\n" + tempFile.toString()+"\n" );
//      interpreter.run(mockController);
//      assertOutputContains("Running in headless mode");
//      assertEquals(2, mockController.parseCommandCallCount);
//      assertEquals("print events", mockController.lastParsedCommand);
//    } finally {
//      Files.deleteIfExists(tempFile);
//    }
//  }

  @Test
  public void testRun_HeadlessModeFileNotFound() {
    provideInput("headless\nnonexistent.txt\nexit\n");
    interpreter.run(mockController);
    assertOutputContains("Could not find file");
    assertFalse(mockController.parseCommandCalled);
  }

//  @Test
//  public void testRun_HeadlessModeCommandError() throws Exception {
//    mockController.exceptionToThrow = new IllegalArgumentException("Test error");
//    Path tempFile = createTempFile("invalid command\nexit\n");
//    try {
//      provideInput("headless\n" + tempFile.toString() + "exit\n");
//      interpreter.run(mockController);
//      assertOutputContains("Error at line 1");
//      assertOutputContains("Reason: Test error");
//    } finally {
//      Files.deleteIfExists(tempFile);
//    }
//  }

  @Test
  public void testRun_InvalidMode() {
    provideInput("invalid\nexit\n");
    interpreter.run(mockController);
    assertOutputContains("Invalid mode");
    assertFalse(mockController.parseCommandCalled);
  }

  @Test
  public void testRunInteractiveMode_ReturnToMainMenu() {
    provideInput("interactive\nmode\nexit\n");
    interpreter.run(mockController);
    assertOutputContains("Returning to mode selection");
    assertFalse(mockController.parseCommandCalled);
  }

  private void provideInput(String data) {
    System.setIn(new ByteArrayInputStream(data.getBytes()));
  }

  private void assertOutputContains(String expected) {
    assertTrue("Output should contain: " + expected,
            outContent.toString().contains(expected));
  }

  private Path createTempFile(String content) throws Exception {
    Path tempFile = Files.createTempFile("test-commands", ".txt");
    Files.writeString(tempFile, content);
    return tempFile;
  }

  private String getOutput() {
    return outContent.toString();
  }
}