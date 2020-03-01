package sg.edu.nus.comp.cs4218.impl.cmd;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import sg.edu.nus.comp.cs4218.EnvironmentHelper;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.CdException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.util.ApplicationRunner;
import sg.edu.nus.comp.cs4218.impl.util.ArgumentResolver;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_FILE_NOT_FOUND;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_INVALID_APP;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;

class PipeCommandIT {

    private static final String FILE_NAME_1 = "CS4218A";
    private static final String FILE_NAME_2 = "A4218A";
    private static final String FILE_NAME_3 = "CS3203A";
    private static final String FOLDER_NAME_1 = "folder1";

    private OutputStream outputStream;
    private List<CallCommand> callCommands;

    @BeforeEach
    void setUp() {
        EnvironmentHelper.currentDirectory = System.getProperty("user.dir");
        outputStream = new ByteArrayOutputStream();
        callCommands = new LinkedList<>();
    }

    @AfterEach
    void tearDown() {
        EnvironmentHelper.currentDirectory = System.getProperty("user.dir");
    }

    /**
     * Tests evaluate method for a valid  <Call> | <Call> format
     * For example: echo hello world | grep "world"
     * Expected: Outputs hello world terminated with a new line character.
     */
    @Test
    void testEvaluatePipeCommandWithValidCallCommandAndCallCommandFormatShouldOutputCorrectly() throws AbstractApplicationException, ShellException {
        callCommands.add(new CallCommand(Arrays.asList("echo", "hello", "world"), new ApplicationRunner(), new ArgumentResolver()));
        callCommands.add(new CallCommand(Arrays.asList("grep", "world"), new ApplicationRunner(), new ArgumentResolver()));
        PipeCommand pipeCommand = new PipeCommand(callCommands);
        pipeCommand.evaluate(System.in, outputStream);
        assertEquals("hello world" + System.lineSeparator(), outputStream.toString());
    }

    /**
     * Tests evaluate method for a valid  <Pipe> | <Call> format
     * For example: ls | grep "4218" | grep "CS4218"
     * Assuming, ls would return a list of files with names, CS4218A, A4218A, CS3203A.
     * Expected: Outputs CS4218A terminated with a new line character.
     */
    @Test
    void testEvaluatePipeCommandWithValidPipeCommandAndCallCommandsFormatShouldOutputCorrectly(@TempDir Path tempDir) throws Exception {
        EnvironmentHelper.currentDirectory = tempDir.toString();
        Files.createFile(tempDir.resolve(FILE_NAME_1));
        Files.createFile(tempDir.resolve(FILE_NAME_2));
        Files.createFile(tempDir.resolve(FILE_NAME_3));
        callCommands.add(new CallCommand(Collections.singletonList("ls"), new ApplicationRunner(), new ArgumentResolver()));
        callCommands.add(new CallCommand(Arrays.asList("grep", "4218"), new ApplicationRunner(), new ArgumentResolver()));
        callCommands.add(new CallCommand(Arrays.asList("grep", "CS4218"), new ApplicationRunner(), new ArgumentResolver()));
        PipeCommand pipeCommand = new PipeCommand(callCommands);
        pipeCommand.evaluate(System.in, outputStream);
        assertEquals("CS4218A" + System.lineSeparator(), outputStream.toString());
    }

    /**
     * Tests evaluate when a ShellException occurs in one part.
     * For example: lsa | echo How are you
     * Expected: Throws ShellException as lsa is an invalid application. The echo command is not executed as stated in teh assumption and project specification.
     */
    @Test
    void testEvaluatePipeCommandWithACommandThrowingAShellExceptionShouldThrowException() throws Exception {
        callCommands.add(new CallCommand(Collections.singletonList("lsa"), new ApplicationRunner(), new ArgumentResolver()));
        callCommands.add(new CallCommand(Arrays.asList("echo", "How", "are", "you"), new ApplicationRunner(), new ArgumentResolver()));
        PipeCommand pipeCommand = new PipeCommand(callCommands);
        ShellException exception = assertThrows(ShellException.class, () -> {
            pipeCommand.evaluate(System.in, outputStream);
        });
        assertEquals(new ShellException("lsa: " + ERR_INVALID_APP).getMessage(), exception.getMessage());
    }

    /**
     * Tests evaluate method when an AbstractApplicationException occurs in one part.
     * For example: cd folder1 | echo Welcome
     * Where folder1 is a directory which does not exists
     * Expected: Throws CdException, a subclass of AbstractApplicationException, ERR_FILE_NOT_FOUND
     */
    @Test
    void testEvaluatePipeCommandWithACommandThrowingASubClassOfApplicationCdExceptionShouldThrowCdException(@TempDir Path tempDir) {
        Path folder = tempDir.resolve(FOLDER_NAME_1);
        EnvironmentHelper.currentDirectory = tempDir.toString();
        assertFalse(Files.isDirectory(folder)); // check that the folder does not exist.
        callCommands.add(new CallCommand(Arrays.asList("cd", FOLDER_NAME_1), new ApplicationRunner(), new ArgumentResolver()));
        callCommands.add(new CallCommand(Arrays.asList("echo", "Welcome"), new ApplicationRunner(), new ArgumentResolver()));
        PipeCommand pipeCommand = new PipeCommand(callCommands);
        AbstractApplicationException exception = assertThrows(AbstractApplicationException.class, () -> {
            pipeCommand.evaluate(System.in, outputStream);
        });
        assertEquals(new CdException(ERR_FILE_NOT_FOUND).getMessage(), exception.getMessage());
    }
}