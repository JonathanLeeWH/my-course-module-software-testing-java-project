package sg.edu.nus.comp.cs4218.impl.cmd;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import sg.edu.nus.comp.cs4218.EnvironmentHelper;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.GrepException;
import sg.edu.nus.comp.cs4218.exception.SedException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.util.ApplicationRunner;
import sg.edu.nus.comp.cs4218.impl.util.ArgumentResolver;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;

class PipeCommandIT {

    private static final String FILE_NAME_1 = "CS4218A.txt";
    private static final String FILE_NAME_2 = "A4218A.txt";
    private static final String FILE_NAME_3 = "CS3203A.txt";
    private static final String FILE_NAME_4 = "1.txt";
    private static final String FILE_NAME_5 = "2.txt";
    private static final String FOLDER_NAME_1 = "folder1";
    private static final String FILE_CONTENT_1 = "Hello world";
    private static final String FILE_CONTENT_2 = "How are you";
    private static final String FILE_CONTENT_3 = "1";
    private static final String FILE_CONTENT_4 = "2";
    private static final String FILE_CONTENT_5 = "3";
    private static final String FILE_CONTENT_6 = "A";
    private static final String FILE_CONTENT_7 = "B";
    private static final String FILE_CONTENT_8 = "AA";
    private static final String WC_APP = "wc";
    private static final String ECHO_APP = "echo";
    private static final String GREP_APP = "grep";
    private static final String CUT_APP = "cut";
    private static final String SORT_APP = "sort";
    private static final String FIND_APP = "find";
    private static final String LS_APP = "ls";
    private static final String PASTE_APP = "paste";
    private static final String SED_APP = "sed";
    private static final String RM_APP = "rm";
    private static final String INVALID_APP = "lsa";
    private static final String B_FLAG = "-b";
    private static final String C_FLAG = "-c";
    private static final String NAME_FLAG = "-name";
    private static final String RELATIVE_CURR = ".";
    private static final String REGEX_EXPR_1 = "s/^/> /";
    private static final String REGEX_EXPR_2 = "s/^/1/";

    private OutputStream outputStream;
    private List<CallCommand> callCommands;

    @BeforeEach
    void setUp() {
        EnvironmentHelper.currentDirectory = System.getProperty("user.dir"); // reset current directory
        outputStream = new ByteArrayOutputStream();
        callCommands = new LinkedList<>();
    }

    @AfterEach
    void tearDown() {
        EnvironmentHelper.currentDirectory = System.getProperty("user.dir"); // reset current directory
    }

    // One pipe test cases

    /**
     * Tests evaluate when a ShellException occurs in one part.
     * For example: lsa | echo How are you
     * Expected: Throws ShellException as lsa is an invalid application. The echo command is not executed as stated in teh assumption and project specification.
     */
    @Test
    void testEvaluatePipeCommandWithACommandThrowingAShellExceptionShouldThrowException() throws Exception {
        callCommands.add(new CallCommand(Collections.singletonList(INVALID_APP), new ApplicationRunner(), new ArgumentResolver()));
        callCommands.add(new CallCommand(Arrays.asList(ECHO_APP, "How", "are", "you"), new ApplicationRunner(), new ArgumentResolver()));
        PipeCommand pipeCommand = new PipeCommand(callCommands);
        ShellException exception = assertThrows(ShellException.class, () -> {
            pipeCommand.evaluate(System.in, outputStream);
        });
        assertEquals(new ShellException("lsa: " + ERR_INVALID_APP).getMessage(), exception.getMessage());
    }

    /**
     * Tests evaluate method when an AbstractApplicationException occurs in one part.
     * For example: echo Welcome | grep
     * Where folder1 is a directory which does not exists
     * Expected: Throws GrepException, a subclass of AbstractApplicationException, ERR_SYNTAX
     */
    @Test
    void testEvaluatePipeCommandWithACommandThrowingASubClassOfAbstractApplicationExceptionShouldThrowGrepException(@TempDir Path tempDir) {
        Path folder = tempDir.resolve(FOLDER_NAME_1);
        EnvironmentHelper.currentDirectory = tempDir.toString();
        assertFalse(Files.isDirectory(folder)); // check that the folder does not exist.
        callCommands.add(new CallCommand(Arrays.asList(ECHO_APP, "Welcome"), new ApplicationRunner(), new ArgumentResolver()));
        callCommands.add(new CallCommand(Arrays.asList(GREP_APP), new ApplicationRunner(), new ArgumentResolver()));
        PipeCommand pipeCommand = new PipeCommand(callCommands);
        AbstractApplicationException exception = assertThrows(AbstractApplicationException.class, () -> {
            pipeCommand.evaluate(System.in, outputStream);
        });
        assertEquals(new GrepException(ERR_EMPTY_REGEX).getMessage(), exception.getMessage());
    }

    /**
     * Tests evaluate method ls command and rm command interaction.
     * This is to test interactions with commands that do not involve input streams and/or output streams.
     * For example: ls | rm 1.txt 2.txt
     * Where 1.txt and 2.txt are existing files..
     * Expected: Removes 1.txt and 2.txt
     */
    @Test
    void testEvaluatePipeCommandWithLsCommandAndRmCommandInteractionShouldRemoveFiles(@TempDir Path tempDir) throws AbstractApplicationException, ShellException, IOException {
        Path file1 = tempDir.resolve(FILE_NAME_4);
        Path file2 = tempDir.resolve(FILE_NAME_5);
        Files.createFile(file1);
        Files.createFile(file2);
        assertTrue(Files.exists(file1)); // checks 1.txt exists
        assertTrue(Files.exists(file2)); // checks 2.txt exists

        EnvironmentHelper.currentDirectory = tempDir.toString();
        callCommands.add(new CallCommand(Collections.singletonList(LS_APP), new ApplicationRunner(), new ArgumentResolver()));
        callCommands.add(new CallCommand(Arrays.asList(RM_APP, file1.toString(), file2.toString()), new ApplicationRunner(), new ArgumentResolver()));
        PipeCommand pipeCommand = new PipeCommand(callCommands);
        pipeCommand.evaluate(System.in, outputStream);
        assertFalse(Files.exists(file1)); // checks 1.txt is deleted.
        assertFalse(Files.exists(file2)); // checks 2.txt is deleted.
    }

    /**
     * Tests evaluate method for a valid  <Call> | <Call> format
     * For example: echo hello world | grep "world"
     * Expected: Outputs hello world terminated with a new line character.
     */
    @Test
    void testEvaluatePipeCommandWithValidCallCommandAndCallCommandFormatShouldOutputCorrectly() throws AbstractApplicationException, ShellException {
        callCommands.add(new CallCommand(Arrays.asList(ECHO_APP, "hello", "world"), new ApplicationRunner(), new ArgumentResolver()));
        callCommands.add(new CallCommand(Arrays.asList(GREP_APP, "world"), new ApplicationRunner(), new ArgumentResolver()));
        PipeCommand pipeCommand = new PipeCommand(callCommands);
        pipeCommand.evaluate(System.in, outputStream);
        assertEquals("hello world" + System.lineSeparator(), outputStream.toString());
    }

    /**
     * Tests evaluate method wc and grep interaction
     * For example: wc CS4218A A4218A | grep A4218
     * Expected: Outputs correctly
     */
    @Test
    void testEvaluatePipeCommandWithWcCommandAndGrepCommandInteractionShouldOutputCorrectly(@TempDir Path tempDir) throws AbstractApplicationException, ShellException, IOException {
        callCommands.add(new CallCommand(Arrays.asList(WC_APP, FILE_NAME_1, FILE_NAME_2), new ApplicationRunner(), new ArgumentResolver()));
        callCommands.add(new CallCommand(Arrays.asList(GREP_APP, FILE_NAME_2), new ApplicationRunner(), new ArgumentResolver()));
        PipeCommand pipeCommand = new PipeCommand(callCommands);
        EnvironmentHelper.currentDirectory = tempDir.toString();
        Path file1 = tempDir.resolve(FILE_NAME_1);
        Path file2 = tempDir.resolve(FILE_NAME_2);
        Files.createFile(file1);
        Files.createFile(file2);
        Files.write(file1, Collections.singletonList(FILE_CONTENT_1));
        Files.write(file2, Arrays.asList(FILE_CONTENT_1, FILE_CONTENT_2));
        pipeCommand.evaluate(System.in, outputStream);
        assertEquals("       2       5      " + file2.toFile().length() + " A4218A.txt" + STRING_NEWLINE, outputStream.toString());
    }

    /**
     * Tests evaluate method echo and cut interaction
     * For example: echo "How are you" | cut -b 5
     * Expected: Outputs correctly
     */
    @Test
    void testEvaluatePipeCommandWithEchoCommandAndCutCommandInteractionShouldOutputCorrectly() throws AbstractApplicationException, ShellException {
        callCommands.add(new CallCommand(Arrays.asList(ECHO_APP, FILE_CONTENT_2), new ApplicationRunner(), new ArgumentResolver()));
        callCommands.add(new CallCommand(Arrays.asList(CUT_APP, B_FLAG, "5"), new ApplicationRunner(), new ArgumentResolver()));
        PipeCommand pipeCommand = new PipeCommand(callCommands);
        pipeCommand.evaluate(System.in, outputStream);
        assertEquals("a" + STRING_NEWLINE, outputStream.toString());
    }

    /**
     * Tests evaluate method cut and sort interaction
     * For example: cut -c 5-7 | sort
     * Expected: Outputs correctly
     */
    @Test
    void testEvaluatePipeCommandWithCutCommandAndSortCommandInteractionShouldOutputCorrectly(@TempDir Path tempDir) throws AbstractApplicationException, ShellException, IOException {
        callCommands.add(new CallCommand(Arrays.asList(CUT_APP, C_FLAG, "5-7", FILE_NAME_1), new ApplicationRunner(), new ArgumentResolver()));
        callCommands.add(new CallCommand(Collections.singletonList(SORT_APP), new ApplicationRunner(), new ArgumentResolver()));
        PipeCommand pipeCommand = new PipeCommand(callCommands);
        EnvironmentHelper.currentDirectory = tempDir.toString();
        Path file1 = tempDir.resolve(FILE_NAME_1);
        Files.createFile(file1);
        Files.write(file1, Arrays.asList(FILE_CONTENT_1, FILE_CONTENT_2));
        pipeCommand.evaluate(System.in, outputStream);
        assertEquals("are" + STRING_NEWLINE + "o w" + STRING_NEWLINE, outputStream.toString());
    }

    /**
     * Tests evaluate method find and grep interaction
     * For example: find . | grep "4A"
     * Expected: Outputs correctly
     */
    @Test
    void testEvaluatePipeCommandWithFindCommandAndGrepCommandInteractionShouldOutputCorrectly(@TempDir Path tempDir) throws AbstractApplicationException, ShellException, IOException {
        callCommands.add(new CallCommand(Arrays.asList(FIND_APP, RELATIVE_CURR, NAME_FLAG, "\"*.txt\""), new ApplicationRunner(), new ArgumentResolver()));
        callCommands.add(new CallCommand(Arrays.asList(GREP_APP, "3A"), new ApplicationRunner(), new ArgumentResolver()));
        PipeCommand pipeCommand = new PipeCommand(callCommands);
        EnvironmentHelper.currentDirectory = tempDir.toString();
        Path file1 = tempDir.resolve(FILE_NAME_1);
        Path file2 = tempDir.resolve(FILE_NAME_2);
        Path file3 = tempDir.resolve(FILE_NAME_3);
        Files.createFile(file1);
        Files.createFile(file2);
        Files.createFile(file3);
        pipeCommand.evaluate(System.in, outputStream);
        assertEquals(RELATIVE_CURR + File.separator + FILE_NAME_3 + STRING_NEWLINE, outputStream.toString());
    }

    /**
     * Tests evaluate method ls and grep interaction
     * For example: ls | grep "A4"
     * Expected: Outputs correctly
     */
    @Test
    void testEvaluatePipeCommandWithLsCommandAndGrepCommandInteractionShouldOutputCorrectly(@TempDir Path tempDir) throws Exception {
        EnvironmentHelper.currentDirectory = tempDir.toString();
        Files.createFile(tempDir.resolve(FILE_NAME_1));
        Files.createFile(tempDir.resolve(FILE_NAME_2));
        Files.createFile(tempDir.resolve(FILE_NAME_3));
        callCommands.add(new CallCommand(Collections.singletonList(LS_APP), new ApplicationRunner(), new ArgumentResolver()));
        callCommands.add(new CallCommand(Arrays.asList(GREP_APP, "A4"), new ApplicationRunner(), new ArgumentResolver()));
        PipeCommand pipeCommand = new PipeCommand(callCommands);
        pipeCommand.evaluate(System.in, outputStream);
        assertEquals("A4218A.txt" + System.lineSeparator(), outputStream.toString());
    }

    /**
     * Tests evaluate method paste and sed interaction
     * For example: paste 1.txt 2.txt | sed "s/^/> /"
     * Where 1.txt and 2.txt exists.
     * Expected: Outputs correctly
     */
    @Test
    void testEvaluatePipeCommandWithPasteCommandAndSedCommandInteractionShouldOutputCorrectly(@TempDir Path tempDir) throws Exception {
        Path file1 = tempDir.resolve(FILE_NAME_4);
        Path file2 = tempDir.resolve(FILE_NAME_5);
        Files.createFile(file1);
        Files.createFile(file2);
        assertTrue(Files.exists(file1)); // check that 1.txt exists
        assertTrue(Files.exists(file2)); // check that 2.txt exists.
        Files.write(file1, Arrays.asList(FILE_CONTENT_3, FILE_CONTENT_4, FILE_CONTENT_5));
        Files.write(file2, Arrays.asList(FILE_CONTENT_6, FILE_CONTENT_7, FILE_CONTENT_8));
        callCommands.add(new CallCommand(Arrays.asList(PASTE_APP, file1.toString(), file2.toString()), new ApplicationRunner(), new ArgumentResolver()));
        callCommands.add(new CallCommand(Arrays.asList(SED_APP, REGEX_EXPR_1), new ApplicationRunner(), new ArgumentResolver()));
        PipeCommand pipeCommand = new PipeCommand(callCommands);
        pipeCommand.evaluate(System.in, outputStream);
        assertEquals("> 1\tA" + System.lineSeparator() + "> 2\tB" + System.lineSeparator() + "> 3\tAA" + System.lineSeparator(), outputStream.toString());
    }

    /**
     * Tests evaluate method paste and grep interaction
     * For example: paste 1.txt 2.txt | grep "A"
     * Where 1.txt and 2.txt exists.
     * Expected: Outputs correctly
     */
    @Test
    void testEvaluatePipeCommandWithPasteCommandAndGrepCommandInteractionShouldOutputCorrectly(@TempDir Path tempDir) throws Exception {
        Path file1 = tempDir.resolve(FILE_NAME_4);
        Path file2 = tempDir.resolve(FILE_NAME_5);
        Files.createFile(file1);
        Files.createFile(file2);
        assertTrue(Files.exists(file1)); // check that 1.txt exists
        assertTrue(Files.exists(file2)); // check that 2.txt exists.
        Files.write(file1, Arrays.asList(FILE_CONTENT_3, FILE_CONTENT_4, FILE_CONTENT_5));
        Files.write(file2, Arrays.asList(FILE_CONTENT_6, FILE_CONTENT_7, FILE_CONTENT_8));
        callCommands.add(new CallCommand(Arrays.asList(PASTE_APP, file1.toString(), file2.toString()), new ApplicationRunner(), new ArgumentResolver()));
        callCommands.add(new CallCommand(Arrays.asList(GREP_APP, "A"), new ApplicationRunner(), new ArgumentResolver()));
        PipeCommand pipeCommand = new PipeCommand(callCommands);
        pipeCommand.evaluate(System.in, outputStream);
        assertEquals("1\tA" + System.lineSeparator() + "3\tAA" + System.lineSeparator(), outputStream.toString());
    }

    /**
     * Tests evaluate method sed and grep interaction
     * For example: sed "s/^/> /" 1.txt | grep "> A"
     * Where 1.txt exists.
     * Expected: Outputs correctly
     */
    @Test
    void testEvaluatePipeCommandWithSedCommandAndGrepCommandInteractionShouldOutputCorrectly(@TempDir Path tempDir) throws Exception {
        Path file1 = tempDir.resolve(FILE_NAME_4);
        Files.createFile(file1);
        assertTrue(Files.exists(file1)); // check that 1.txt exists
        Files.write(file1, Arrays.asList(FILE_CONTENT_6, FILE_CONTENT_7, FILE_CONTENT_8));
        callCommands.add(new CallCommand(Arrays.asList(SED_APP, REGEX_EXPR_1, file1.toString()), new ApplicationRunner(), new ArgumentResolver()));
        callCommands.add(new CallCommand(Arrays.asList(GREP_APP, "> A"), new ApplicationRunner(), new ArgumentResolver()));
        PipeCommand pipeCommand = new PipeCommand(callCommands);
        pipeCommand.evaluate(System.in, outputStream);
        assertEquals("> A" + System.lineSeparator() + "> AA" + System.lineSeparator(), outputStream.toString());
    }

    /**
     * Tests evaluate method sed and cut interaction
     * For example: sed "s/^/> /" 1.txt | cut -c 1-2
     * Where 1.txt exists.
     * Expected: Outputs correctly
     */
    @Test
    void testEvaluatePipeCommandWithSedCommandAndCutCommandInteractionShouldOutputCorrectly(@TempDir Path tempDir) throws Exception {
        Path file1 = tempDir.resolve(FILE_NAME_4);
        Files.createFile(file1);
        assertTrue(Files.exists(file1)); // check that 1.txt exists
        Files.write(file1, Arrays.asList(FILE_CONTENT_6, FILE_CONTENT_7, FILE_CONTENT_8));
        callCommands.add(new CallCommand(Arrays.asList(SED_APP, REGEX_EXPR_1, file1.toString()), new ApplicationRunner(), new ArgumentResolver()));
        callCommands.add(new CallCommand(Arrays.asList(CUT_APP, C_FLAG, "1-2"), new ApplicationRunner(), new ArgumentResolver()));
        PipeCommand pipeCommand = new PipeCommand(callCommands);
        pipeCommand.evaluate(System.in, outputStream);
        assertEquals("> " + System.lineSeparator() + "> " + System.lineSeparator() + "> " + System.lineSeparator(), outputStream.toString());
    }

    // At least two pipes test case

    /**
     * Tests evaluate method ls command and sort command and rm command interaction.
     * This is to test interactions with commands that do not involve input streams and/or output streams.
     * For example: ls | sort | rm 1.txt 2.txt
     * Where 1.txt and 2.txt are existing files..
     * Expected: Removes 1.txt and 2.txt
     */
    @Test
    void testEvaluatePipeCommandWithLsCommandAndSortCommandAndRmCommandInteractionShouldRemoveFiles(@TempDir Path tempDir) throws AbstractApplicationException, ShellException, IOException {
        Path file1 = tempDir.resolve(FILE_NAME_4);
        Path file2 = tempDir.resolve(FILE_NAME_5);
        Files.createFile(file1);
        Files.createFile(file2);
        assertTrue(Files.exists(file1)); // checks 1.txt exists
        assertTrue(Files.exists(file2)); // checks 2.txt exists

        EnvironmentHelper.currentDirectory = tempDir.toString();
        callCommands.add(new CallCommand(Collections.singletonList(LS_APP), new ApplicationRunner(), new ArgumentResolver()));
        callCommands.add(new CallCommand(Collections.singletonList(SORT_APP), new ApplicationRunner(), new ArgumentResolver()));
        callCommands.add(new CallCommand(Arrays.asList(RM_APP, file1.toString(), file2.toString()), new ApplicationRunner(), new ArgumentResolver()));
        PipeCommand pipeCommand = new PipeCommand(callCommands);
        pipeCommand.evaluate(System.in, outputStream);
        assertFalse(Files.exists(file1)); // checks 1.txt is deleted.
        assertFalse(Files.exists(file2)); // checks 2.txt is deleted.
    }

    /**
     * Tests evaluate method with at least 2 pipes when there is an exception in one part
     * For example: paste 1.txt 2.txt | sed "s" | sort
     * Where 1.txt and 2.txt exists.
     * In this case, sed has the wrong arguments and should throw sed exception with ERR_INVALID_REP_RULE
     * Expected: Throws sed exception with ERR_INVALID_REP_RULE
     */
    @Test
    void testEvaluatePipeCommandWithValidPasteCommandAndInvalidSedCommandAndValidSortCommandInteractionShouldThrowSedException(@TempDir Path tempDir) throws Exception {
        Path file1 = tempDir.resolve(FILE_NAME_4);
        Path file2 = tempDir.resolve(FILE_NAME_5);
        Files.createFile(file1);
        Files.createFile(file2);
        assertTrue(Files.exists(file1)); // check that 1.txt exists
        assertTrue(Files.exists(file2)); // check that 2.txt exists.
        Files.write(file1, Arrays.asList(FILE_CONTENT_4, FILE_CONTENT_3, FILE_CONTENT_5));
        Files.write(file2, Arrays.asList(FILE_CONTENT_6, FILE_CONTENT_7, FILE_CONTENT_8));
        callCommands.add(new CallCommand(Arrays.asList(PASTE_APP, file1.toString(), file2.toString()), new ApplicationRunner(), new ArgumentResolver()));
        callCommands.add(new CallCommand(Arrays.asList(SED_APP, "s"), new ApplicationRunner(), new ArgumentResolver()));
        callCommands.add(new CallCommand(Arrays.asList(SORT_APP), new ApplicationRunner(), new ArgumentResolver()));
        PipeCommand pipeCommand = new PipeCommand(callCommands);
        SedException exception = assertThrows(SedException.class, () -> {
            pipeCommand.evaluate(System.in, outputStream);
        });
        assertEquals(new SedException(ERR_INVALID_REP_RULE).getMessage(), exception.getMessage());
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
        callCommands.add(new CallCommand(Collections.singletonList(LS_APP), new ApplicationRunner(), new ArgumentResolver()));
        callCommands.add(new CallCommand(Arrays.asList(GREP_APP, "4218"), new ApplicationRunner(), new ArgumentResolver()));
        callCommands.add(new CallCommand(Arrays.asList(GREP_APP, "CS4218"), new ApplicationRunner(), new ArgumentResolver()));
        PipeCommand pipeCommand = new PipeCommand(callCommands);
        pipeCommand.evaluate(System.in, outputStream);
        assertEquals("CS4218A.txt" + System.lineSeparator(), outputStream.toString());
    }

    /**
     * Tests evaluate method paste command and sed command and grep interaction
     * For example: paste 1.txt 2.txt | sed "s/^/> /" | grep "A"
     * Where 1.txt and 2.txt exists.
     * Expected: Outputs correctly
     */
    @Test
    void testEvaluatePipeCommandWithPasteCommandAndSedCommandAndGrepCommandInteractionShouldOutputCorrectly(@TempDir Path tempDir) throws Exception {
        Path file1 = tempDir.resolve(FILE_NAME_4);
        Path file2 = tempDir.resolve(FILE_NAME_5);
        Files.createFile(file1);
        Files.createFile(file2);
        assertTrue(Files.exists(file1)); // check that 1.txt exists
        assertTrue(Files.exists(file2)); // check that 2.txt exists.
        Files.write(file1, Arrays.asList(FILE_CONTENT_3, FILE_CONTENT_4, FILE_CONTENT_5));
        Files.write(file2, Arrays.asList(FILE_CONTENT_6, FILE_CONTENT_7, FILE_CONTENT_8));
        callCommands.add(new CallCommand(Arrays.asList(PASTE_APP, file1.toString(), file2.toString()), new ApplicationRunner(), new ArgumentResolver()));
        callCommands.add(new CallCommand(Arrays.asList(SED_APP, REGEX_EXPR_1), new ApplicationRunner(), new ArgumentResolver()));
        callCommands.add(new CallCommand(Arrays.asList(GREP_APP, "A"), new ApplicationRunner(), new ArgumentResolver()));
        PipeCommand pipeCommand = new PipeCommand(callCommands);
        pipeCommand.evaluate(System.in, outputStream);
        assertEquals("> 1\tA" + System.lineSeparator() + "> 3\tAA" + System.lineSeparator(), outputStream.toString());
    }

    /**
     * Tests evaluate method sed command and cut command and sort command interaction
     * For example: sed "s/^/1/" 1.txt | cut -c 1-2 | sort
     * Where 1.txt exists.
     * Expected: Outputs correctly
     */
    @Test
    void testEvaluatePipeCommandWithSedCommandAndCutCommandAndSortCommandInteractionShouldOutputCorrectly(@TempDir Path tempDir) throws Exception {
        Path file1 = tempDir.resolve(FILE_NAME_4);
        Files.createFile(file1);
        assertTrue(Files.exists(file1)); // check that 1.txt exists
        Files.write(file1, Arrays.asList(FILE_CONTENT_6, FILE_CONTENT_7, FILE_CONTENT_8));
        callCommands.add(new CallCommand(Arrays.asList(SED_APP, REGEX_EXPR_2, file1.toString()), new ApplicationRunner(), new ArgumentResolver()));
        callCommands.add(new CallCommand(Arrays.asList(CUT_APP, C_FLAG, "1-2"), new ApplicationRunner(), new ArgumentResolver()));
        callCommands.add(new CallCommand(Arrays.asList(SORT_APP), new ApplicationRunner(), new ArgumentResolver()));
        PipeCommand pipeCommand = new PipeCommand(callCommands);
        pipeCommand.evaluate(System.in, outputStream);
        assertEquals("1A" + System.lineSeparator() + "1A" + System.lineSeparator() + "1B" + System.lineSeparator(), outputStream.toString());
    }

    /**
     * Tests evaluate method wc command and sed command and sort command interaction
     * For example: wc A4218A CS4218A | sed "s/^/1/" | sort
     * Expected: Outputs correctly
     */
    @Test
    void testEvaluatePipeCommandWithWcCommandAndSedCommandAndSortCommandInteractionShouldOutputCorrectly(@TempDir Path tempDir) throws AbstractApplicationException, ShellException, IOException {
        callCommands.add(new CallCommand(Arrays.asList(WC_APP, FILE_NAME_2, FILE_NAME_1), new ApplicationRunner(), new ArgumentResolver()));
        callCommands.add(new CallCommand(Arrays.asList(SED_APP, REGEX_EXPR_2), new ApplicationRunner(), new ArgumentResolver()));
        callCommands.add(new CallCommand(Arrays.asList(SORT_APP), new ApplicationRunner(), new ArgumentResolver()));
        PipeCommand pipeCommand = new PipeCommand(callCommands);
        EnvironmentHelper.currentDirectory = tempDir.toString();
        Path file1 = tempDir.resolve(FILE_NAME_1);
        Path file2 = tempDir.resolve(FILE_NAME_2);
        Files.createFile(file1);
        Files.createFile(file2);
        Files.write(file1, Collections.singletonList(FILE_CONTENT_1));
        Files.write(file2, Arrays.asList(FILE_CONTENT_1, FILE_CONTENT_2));
        pipeCommand.evaluate(System.in, outputStream);
        long expectedTotalBytes = file1.toFile().length() + file2.toFile().length();
        assertEquals("1       1       2      " + file1.toFile().length() + " CS4218A.txt" + STRING_NEWLINE + "1       2       5      " + file2.toFile().length() + " A4218A.txt" + STRING_NEWLINE + "1       3       7      " + expectedTotalBytes +" total" + STRING_NEWLINE, outputStream.toString());
    }
}