package tdd.bf;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

import java.io.InputStream;
import java.io.OutputStream;
import java.security.Permission;

import org.junit.jupiter.api.*;

import sg.edu.nus.comp.cs4218.exception.ExitException;
import sg.edu.nus.comp.cs4218.impl.app.ExitApplication;

/**
 * Note: Added class level wide disable tdd's ExitApplicationTest.java due to difference in implementation. Please use our version of ExitApplicationTest.java instead.
 * Please use our version of ExitApplicationTest.java instead of tdd's version should be used to test ExitApplication.java better coverage.
 */
@Disabled("The tdd's ExitApplicationTest.java is disabled and instead use our version of ExitApplication.java due to difference in implementation")
class ExitApplicationTest {

    /**
     * This tdd test suite is disabled/commented out as it differs in implementation compared to our version of ExitApplication.java
     */
    private ExitApplication exitApp;

    protected static class TestExitException extends SecurityException {
        public final int status;
        public TestExitException(int status) {
            super("Expected Exit");
            this.status = status;
        }
    }
    private static class NoExitSecurityManager extends SecurityManager {
        @Override
        public void checkPermission(Permission perm)
        {
            // allow anything.
        }
        @Override
        public void checkPermission(Permission perm, Object context)
        {
            // allow anything.
        }
        @Override
        public void checkExit(int status) {
            super.checkExit(status);
            throw new TestExitException(status);

        }
    }

    @BeforeAll
    static void setUp() {
        System.out.println("Starting Exit App Test");
        System.setSecurityManager(new NoExitSecurityManager());
    }

    @BeforeEach
    public void initialisation() {
        exitApp = new ExitApplication();
    }

    // This test case is commented out as it differs from our implementation as our implementation throws exit exception instead of directly call System.exit(0).
    @Test
    public void testExit(){
        try {
            exitApp.terminateExecution();
        } catch (TestExitException e) {
            assertEquals(0, e.status);
        } catch (ExitException e) {
            fail("Expected Exit");
        }
    }

    @AfterEach
    public void tearDown() {
        System.setSecurityManager(null);
    }
}