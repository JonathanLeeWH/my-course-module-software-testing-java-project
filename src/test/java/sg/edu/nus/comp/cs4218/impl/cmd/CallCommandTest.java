package sg.edu.nus.comp.cs4218.impl.cmd;

import org.junit.jupiter.api.AfterEach;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.util.ApplicationRunner;
import sg.edu.nus.comp.cs4218.impl.util.ArgumentResolver;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_SYNTAX;

/**
 * In this unit test, it is important to note that call command encapsulates objects (e.g. application runner)
 * which are used by other classes as well. As such, they are treated as separate components rather than utilities.
 * Therefore, the contents of these objects are more extensively tested in the call command integration test.
 */
class CallCommandTest {
    private static ApplicationRunner mockAppRunner;
    private static ArgumentResolver mockArgResolver;
    private static CallCommand callCommand;
    private static OutputStream outputStream;
    private static final String SHELL_EXCEPTION = "shell: ";

    @BeforeEach
    void setUp() {
        mockAppRunner = mock(ApplicationRunner.class);
        mockArgResolver = mock(ArgumentResolver.class);
        outputStream = new ByteArrayOutputStream();
    }

    @AfterEach
    void teardown() throws IOException {
        outputStream.close();
    }

    /**
     * Test evaluate method in call command with null arg list.
     * Expected: Shell Exception with ERR_SYNTAX message.
     */
    @Test
    void runEvaluateMethodWithNullArgListShouldThrowShellException() {
        callCommand = new CallCommand(null, mockAppRunner, mockArgResolver);
        Exception thrown = assertThrows(ShellException.class, () -> {
            callCommand.evaluate(System.in, outputStream);
        });
        String expected = SHELL_EXCEPTION + ERR_SYNTAX;
        assertEquals(expected, thrown.getMessage());
    }

    /**
     * Test evaluate method with empty argList
     * Expected: Throw Shell Exception
     */
    @Test
    void runEvaluateMethodWithEmptyArgListShouldThrowShellException() {
        callCommand = new CallCommand(new ArrayList<>(), mockAppRunner, mockArgResolver);
        Exception thrown = assertThrows(ShellException.class, () -> {
            callCommand.evaluate(System.in, outputStream);
        });
        String expected = SHELL_EXCEPTION + ERR_SYNTAX;
        assertEquals(expected, thrown.getMessage());
    }


    /**
     * Test getter of argsList in Call Command.
     * Expected: No changes should be made to argsList.
     */
    @Test
    void testRunTrivialTestForArgListGetter() {
        List<String> argsList = new ArrayList<>(Arrays.asList("echo", "hello"));
        CallCommand callCommand = new CallCommand(argsList, mockAppRunner, mockArgResolver);
        assertEquals("[echo, hello]", callCommand.getArgsList().toString());
    }
}
