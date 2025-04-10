package calendar;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.fail;


/**
 * Test cases for the Main entry point of the Calendar Application.
 */
public class MainTest {

  private SecurityManager originalSecurityManager;
  private InputStream originalIn;

  /**
   * Exception class to check output.
   */
  public static class ExitException extends SecurityException {
    public final int status;
    public ExitException(int status) {
      super("System.exit(" + status + ") called");
      this.status = status;
    }
  }

  /**
   * Throws the exception on exit.
   */
  public static class NoExitSecurityManager extends SecurityManager {
    @Override
    public void checkExit(int status) {
      super.checkExit(status);
      throw new ExitException(status);
    }
    @Override
    public void checkPermission(java.security.Permission perm) {
      // Allow all.
    }
    @Override
    public void checkPermission(java.security.Permission perm, Object context) {
      // Allow all.
    }
  }

  /**
   * Setup before the start of each test.
   */
  @Before
  public void setUp() {
    originalSecurityManager = System.getSecurityManager();
    originalIn = System.in;
  }

  /**
   * Resets the Security manager after each test.
   */
  @After
  public void tearDown() {
    System.setSecurityManager(originalSecurityManager);
    System.setIn(originalIn);
  }

//  /**
//   * Test that when no arguments are provided, GUI mode is launched.
//   * This test polls for up to 5 seconds until a top-level frame with the title
//   * "Calendar Application" is visible.
//   */
//  @Test
//  public void testNoArgsLaunchGUIMode() throws Exception {
//    Main.main(new String[0]);
//    boolean found = false;
//    long startTime = System.currentTimeMillis();
//    while (System.currentTimeMillis() - startTime < 5000) {
//      Frame[] frames = Frame.getFrames();
//      for (Frame frame : frames) {
//        if (frame instanceof JFrame && "Calendar Application".equals(((JFrame) frame).getTitle())
//                && frame.isShowing()) {
//          found = true;
//          SwingUtilities.invokeLater(() -> frame.dispose());
//          break;
//        }
//      }
//      if (found) {
//        break;
//      }
//      Thread.sleep(100);
//    }
//    assertTrue("GUI mode should launch a window with title 'Calendar Application'", found);
//  }

  /**
   * Test that interactive mode is launched when given "--mode interactive".
   * This test simulates user input ("exit\n") so that the interpreter will exit.
   */
  @Test
  public void testInteractiveMode() throws Exception {
    String simulatedInput = "exit\n";
    ByteArrayInputStream in = new ByteArrayInputStream(simulatedInput.getBytes());
    System.setIn(in);
    Thread thread = new Thread(() -> Main.main(new String[]{"--mode", "interactive"}));
    thread.start();
    thread.join(3000);
    if (thread.isAlive()) {
      thread.interrupt();
    }
  }

  /**
   * Test that headless mode works when provided a valid script file.
   * This creates a temporary file with "exit" as the only command.
   */
  @Test
  public void testHeadlessModeValid() throws Exception {
    File tempScript = File.createTempFile("testScript", ".txt");
    tempScript.deleteOnExit();
    java.nio.file.Files.write(tempScript.toPath(), "exit\n".getBytes());
    String[] args = {"--mode", "headless", tempScript.getAbsolutePath()};
    Main.main(args);

  }

  /**
   * Test that headless mode calls System.exit(1) when the script file is not found.
   */
  @Test(expected = ExitException.class)
  public void testHeadlessModeInvalidScript() {
    System.setSecurityManager(new NoExitSecurityManager());
    String[] args = {"--mode", "headless", "nonexistent_script.txt"};
    Main.main(args);
    fail("System.exit should have been called for missing script file in headless mode");
  }

  /**
   * Test that invalid command-line arguments cause the program to call System.exit(1).
   */
  @Test(expected = ExitException.class)
  public void testInvalidArgs() {
    System.setSecurityManager(new NoExitSecurityManager());
    String[] args = {"invalid"};
    Main.main(args);
    fail("System.exit should have been called for invalid command-line arguments");
  }
}
