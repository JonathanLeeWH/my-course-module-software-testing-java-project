package tdd.bf;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

import java.io.InputStream;
import java.io.OutputStream;
import java.security.Permission;

import org.junit.jupiter.api.*;

import sg.edu.nus.comp.cs4218.exception.ExitException;
import sg.edu.nus.comp.cs4218.impl.app.ExitApplication;

class ExitApplicationTest {

    /**
     * Our own ExitApplicationTest.java Tests which match our implementation. This version is same as our own ExitApplicationTest.java.
     */
    private ExitApplication exitApplication;

    @BeforeEach
    void setUp() {
        exitApplication = new ExitApplication();
    }

    /**
     * Tests run method.
     * Expected: Throws ExitException with exit code 0.
     */
    @Test
    void testRun() {
        Exception exception = assertThrows(ExitException.class, () -> {
            exitApplication.run(null, mock(InputStream.class), mock(OutputStream.class));
        });

        assertEquals(new ExitException("0").getMessage(), exception.getMessage());
    }

    /**
     * Tests terminate method
     * Expected: Throws ExitException with exit code 0.
     */
    @Test
    void testTerminateExecutionShouldThrowExitExceptionWithExitCode0() {
        Exception exception = assertThrows(ExitException.class, () -> {
            exitApplication.terminateExecution();
        });

        assertEquals(new ExitException("0").getMessage(), exception.getMessage());
    }

    /**
     * This tdd test suite is ignored/commented out as it differs in implementation compared to our own ExitApplication.java
     */
//    private ExitApplication exitApp;
//
//    protected static class TestExitException extends SecurityException {
//        public final int status;
//        public TestExitException(int status) {
//            super("Expected Exit");
//            this.status = status;
//        }
//    }
//    private static class NoExitSecurityManager extends SecurityManager {
//        @Override
//        public void checkPermission(Permission perm)
//        {
//            // allow anything.
//        }
//        @Override
//        public void checkPermission(Permission perm, Object context)
//        {
//            // allow anything.
//        }
//        @Override
//        public void checkExit(int status) {
//            super.checkExit(status);
//            throw new TestExitException(status);
//
//        }
//    }
//
//    @BeforeAll
//    static void setUp() {
//        System.out.println("Starting Exit App Test");
//        System.setSecurityManager(new NoExitSecurityManager());
//    }
//
//    @BeforeEach
//    public void initialisation() {
//        exitApp = new ExitApplication();
//    }
//
//    // This test case is commented out as it differs from our implementation as our implementation throws exit exception instead of directly call System.exit(0).
//    @Test
//    public void testExit(){
//        try {
//            exitApp.terminateExecution();
//        } catch (TestExitException e) {
//            assertEquals(0, e.status);
//        } catch (ExitException e) {
//            fail("Expected Exit");
//        }
//    }
//
//    @AfterEach
//    public void tearDown() {
//        System.setSecurityManager(null);
//    }
}