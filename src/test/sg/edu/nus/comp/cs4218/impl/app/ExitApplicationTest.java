package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.exception.ExitException;

import static org.junit.jupiter.api.Assertions.*;

class ExitApplicationTest {

    private ExitApplication exitApplication;

    @BeforeEach
    void setUp() {
        exitApplication = new ExitApplication();
    }

    /**
     * Tests run method. Parameters not tested since they are not used.
     * In this test case, the parameters are set to null for simplicity.
     * Expected: Throws ExitException with exit code 0.
     */
    @Test
    void run() {
        Exception exception = assertThrows(ExitException.class, () -> {
            exitApplication.run(null, null, null); // Since parameters are not used they are set to null for simplicity.
        });

        assertEquals(new ExitException("0").getMessage(), exception.getMessage());
    }

    /**
     * Tests terminate method
     * Expected: Throws ExitException with exit code 0.
     */
    @Test
    void terminateExecutionShouldThrowExitExceptionWithExitCode0() {
        Exception exception = assertThrows(ExitException.class, () -> {
            exitApplication.terminateExecution();
        });

        assertEquals(new ExitException("0").getMessage(), exception.getMessage());
    }
}