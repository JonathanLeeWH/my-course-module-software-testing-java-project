package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.exception.ExitException;

import java.io.InputStream;
import java.io.OutputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class ExitApplicationTest {

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
}