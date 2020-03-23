package sg.edu.nus.comp.cs4218.impl.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.exception.ShellException;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static sg.edu.nus.comp.cs4218.impl.util.CommandBuilder.parseCommand;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_SYNTAX;

@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public class CommandBuilderTest {
    private ApplicationRunner appRunner;
    private final Path testFile1 = Paths.get(TestFileUtils.TESTDATA_DIR + "test1.txt");

    @BeforeEach
    void setup() {
        appRunner = mock(ApplicationRunner.class);
    }

    @Test
    void testParseCommandWithMisplacedQuotestShouldThrowShellException() {
        Throwable thrown = assertThrows(ShellException.class,
                () -> parseCommand("echo \"Test\'", appRunner));
        assertEquals(thrown.getMessage(), "shell: " + ERR_SYNTAX);
    }

    @Test
    void testParseCommandWithEmptyCommandStringShouldThrowShellException() {
        Throwable thrown = assertThrows(ShellException.class,
                () -> parseCommand("", appRunner));
        assertEquals(thrown.getMessage(), "shell: " + ERR_SYNTAX);
    }

    @Test
    void testParseCommandWithPipeOperatorAtTheStartShouldThrowShellException() {
        Throwable thrown = assertThrows(ShellException.class,
                () -> parseCommand("| echo \"Hello\"", appRunner));
        assertEquals(thrown.getMessage(), "shell: " + ERR_SYNTAX);
    }

    @Test
    void testParseCommandWithSemiColonOperatorAtTheStartShouldThrowShellException() {
        Throwable thrown = assertThrows(ShellException.class,
                () -> parseCommand("; echo \"Hello\"", appRunner));
        assertEquals(thrown.getMessage(), "shell: " + ERR_SYNTAX);
    }

    @Test
    void testParseCommandWithMismatchedQuotesShouldThrowShellException() {
        Throwable thrown = assertThrows(ShellException.class,
                () -> parseCommand("; echo \"Hello'", appRunner));
        assertEquals(thrown.getMessage(), "shell: " + ERR_SYNTAX);
    }
}
