package sg.edu.nus.comp.cs4218.impl.cmd;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import sg.edu.nus.comp.cs4218.Command;
import sg.edu.nus.comp.cs4218.EnvironmentHelper;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.CdException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.util.ApplicationRunner;
import sg.edu.nus.comp.cs4218.impl.util.ArgumentResolver;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_FILE_NOT_FOUND;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_INVALID_APP;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_REDIR_OUTPUT;

class SequenceCommandIT {

    private static final String ECHO_APP = "echo";
    private static final String PASTE_APP = "paste";
    private static final String RM_APP = "rm";
    private static final String INVALID_APP = "lsa";
    private static final String FOLDER_NAME_1 = "folder1";
    private static final String FILE_NAME_1 = "CS4218A";
    private static final String FILE_NAME_2 = "A4218A";
    private static final String FILE_NAME_3 = "CS3203A";
    private static final String FILE_NAME_4 = "A.txt";
    private static final String FILE_NAME_5 = "B.txt";
    private static final String FILE_NAME_6 = "AB.txt";

    private OutputStream outputStream;
    private List<Command> commands;

    @BeforeEach
    void setUp() {
        EnvironmentHelper.currentDirectory = System.getProperty("user.dir");
        outputStream = new ByteArrayOutputStream();
        commands = new LinkedList<>();
    }

    @AfterEach
    void tearDown() {
        EnvironmentHelper.currentDirectory = System.getProperty("user.dir");
    }

    /**
     * Tests evaluate method for a valid <Command> ; <Command> format
     * In this case we will test two call commands
     * For example: echo hello world ; echo How are you
     * Expected: Outputs hello world with a new line followed by How are you terminated with a new line.
     * hello world
     * How are you
     */
    @Test
    void evaluateSequenceCommandWithValidCallCommandAndCallCommandFormatShouldOutputCorrectly() throws AbstractApplicationException, ShellException {
        commands.add(new CallCommand(Arrays.asList(ECHO_APP, "hello", "world"), new ApplicationRunner(), new ArgumentResolver()));
        commands.add(new CallCommand(Arrays.asList(ECHO_APP, "How", "are", "you"), new ApplicationRunner(), new ArgumentResolver()));
        SequenceCommand sequenceCommand = new SequenceCommand(commands);
        sequenceCommand.evaluate(System.in, outputStream);
        assertEquals("hello world" + System.lineSeparator() + "How are you" + System.lineSeparator(), outputStream.toString());
    }

    /**
     * Tests evaluate method for a valid <Command> ; <Command> format
     * In this case we will test one call commands and one pipe command
     * Based on Grammar command can be <call> | <seq> | <pipe>
     * For example: echo Goodnight ; ls | grep "CS4218"
     * Expected: Outputs
     * Goodnight
     * CS4218A
     */
    @Test
    void evaluateSequenceCommandWithValidCallCommandAndPipeCommandFormatShouldOutputCorrectly(@TempDir Path tempDir) throws AbstractApplicationException, ShellException, IOException {
        EnvironmentHelper.currentDirectory = tempDir.toString();
        Files.createFile(tempDir.resolve(FILE_NAME_1));
        Files.createFile(tempDir.resolve(FILE_NAME_2));
        Files.createFile(tempDir.resolve(FILE_NAME_3));
        LinkedList<CallCommand> callCommands = new LinkedList<>();
        callCommands.add(new CallCommand(Collections.singletonList("ls"), new ApplicationRunner(), new ArgumentResolver()));
        callCommands.add(new CallCommand(Arrays.asList("grep", "CS4218"), new ApplicationRunner(), new ArgumentResolver()));
        PipeCommand pipeCommand = new PipeCommand(callCommands);
        commands.add(new CallCommand(Arrays.asList(ECHO_APP, "Goodnight"), new ApplicationRunner(), new ArgumentResolver()));
        commands.add(new PipeCommand(callCommands));
        SequenceCommand sequenceCommand = new SequenceCommand(commands);
        sequenceCommand.evaluate(System.in, outputStream);
        assertEquals("Goodnight" + System.lineSeparator() + FILE_NAME_1 + System.lineSeparator(), outputStream.toString());
    }

    /**
     * Tests evaluate method for a valid <Command> ; <Command> format
     * In this case we will test one call commands and one seq command
     * Based on Grammar command can be <call> | <seq> | <pipe>
     * For example: echo Good Afternoon ; echo Hi ; echo Sweet dreams
     * Expected: Outputs
     * Good Afternoon
     * Hi
     * Sweet dreams
     */
    @Test
    void evaluateSequenceCommandWithValidCallCommandAndSequenceCommandFormatShouldOutputCorrect() throws AbstractApplicationException, ShellException {
        commands.add(new CallCommand(Arrays.asList(ECHO_APP, "Good", "Afternoon"), new ApplicationRunner(), new ArgumentResolver()));
        commands.add(new CallCommand(Arrays.asList(ECHO_APP, "Hi"), new ApplicationRunner(), new ArgumentResolver()));
        commands.add(new CallCommand(Arrays.asList(ECHO_APP, "Sweet", "dreams"), new ApplicationRunner(), new ArgumentResolver()));
        SequenceCommand sequenceCommand = new SequenceCommand(commands);
        sequenceCommand.evaluate(System.in, outputStream);
        assertEquals("Good Afternoon" + System.lineSeparator() + "Hi" + System.lineSeparator() + "Sweet dreams" + System.lineSeparator(), outputStream.toString());
    }

    /**
     * Tests evaluate method when a ShellException occur in one part.
     * For example: lsa; echo Greetings
     * Where lsa is an invalid application.
     * Expected: Outputs ShellException message followed by a new line during the execution of first command, in this case, lsa which is an invalid application,
     * and continue the execution of the second command.
     * shell: lsa: Invalid app
     * Greetings
     */
    @Test
    void evaluateSequenceCommandWithCommandWhichThrowShellExceptionFollowedByValidCommandFormatShouldOutputShellExceptionAndContinueExecution() throws AbstractApplicationException, ShellException {
        commands.add(new CallCommand(Collections.singletonList(INVALID_APP), new ApplicationRunner(), new ArgumentResolver()));
        commands.add(new CallCommand(Arrays.asList(ECHO_APP, "Greetings"), new ApplicationRunner(), new ArgumentResolver()));
        SequenceCommand sequenceCommand = new SequenceCommand(commands);
        sequenceCommand.evaluate(System.in, outputStream);
        assertEquals(new ShellException(INVALID_APP + ": " + ERR_INVALID_APP).getMessage() + System.lineSeparator() + "Greetings" + System.lineSeparator(), outputStream.toString());
    }

    /**
     * Tests evaluate method when a subclass of AbstractApplicationException occur in the one part.
     * For example: cd folder1; echo Good morning
     * Where folder1 is a directory which does not exists
     * Expected: Outputs CdException, a subclass of AbstractApplicationException, ERR_FILE_NOT_FOUND message followed by a new line during the execution of first command, in this case, lsa which is an invalid application,
     * and continue the execution of the second command.
     * cd: No such file or directory
     * Good morning
     */
    @Test
    void evaluateSequenceCommandWithCommandWhichThrowAbstractApplicationExceptionFollowedByValidCommandFormatShouldOutputAbstractionApplicationExceptionAndContinueExecution(@TempDir Path tempDir) throws AbstractApplicationException, ShellException {
        Path folder = tempDir.resolve(FOLDER_NAME_1);
        EnvironmentHelper.currentDirectory = tempDir.toString();
        assertFalse(Files.isDirectory(folder)); // check that the folder does not exist.
        commands.add(new CallCommand(Arrays.asList("cd", FOLDER_NAME_1), new ApplicationRunner(), new ArgumentResolver()));
        commands.add(new CallCommand(Arrays.asList(ECHO_APP, "Good morning"), new ApplicationRunner(), new ArgumentResolver()));
        SequenceCommand sequenceCommand = new SequenceCommand(commands);
        sequenceCommand.evaluate(System.in, outputStream);
        assertEquals(new CdException(ERR_FILE_NOT_FOUND).getMessage() + System.lineSeparator() + "Good morning" + System.lineSeparator(), outputStream.toString());
    }

    /**
     * Tests evaluate method when involving streams such as output redirection.
     * This is to ensure streams are open and closed properly and can be used properly.
     * For example: paste A.txt B.txt > AB.txt; rm AB.txt
     * Where A.txt and B.txt exist. AB.txt does not exist initially.
     * Expected: Removes AB.txt after AB.txt is created from the execution of paste A.txt B.txt > AB.txt
     */
    @Test
    void evaluateSequenceCommandWithOutputRedirectionCommandAndRemoveCommandShouldRemoveTheCreatedFile(@TempDir Path tempDir) throws IOException, AbstractApplicationException, ShellException {
        Path file1 = tempDir.resolve(FILE_NAME_4);
        Path file2 = tempDir.resolve(FILE_NAME_5);
        Path file3 = tempDir.resolve(FILE_NAME_6);
        EnvironmentHelper.currentDirectory = tempDir.toString();
        Files.createFile(file1);
        Files.createFile(file2);
        assertTrue(Files.exists(file1)); // Check A.txt exists
        assertTrue(Files.exists(file2)); // Check B.txt exists
        assertFalse(Files.exists(file3)); // Check AB.txt does not exist initially
        commands.add(new CallCommand(Arrays.asList(PASTE_APP, FILE_NAME_4, FILE_NAME_5, Character.toString(CHAR_REDIR_OUTPUT), FILE_NAME_6), new ApplicationRunner(), new ArgumentResolver()));
        commands.add(new CallCommand(Arrays.asList(RM_APP, FILE_NAME_6), new ApplicationRunner(), new ArgumentResolver()));
        SequenceCommand sequenceCommand = new SequenceCommand(commands);
        sequenceCommand.evaluate(System.in, outputStream);
        assertFalse(Files.exists(file3)); // Check that AB.txt does not exist as it should be removed
    }
}