package sg.edu.nus.comp.cs4218.impl.cmd;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import sg.edu.nus.comp.cs4218.Command;
import sg.edu.nus.comp.cs4218.EnvironmentHelper;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.CdException;
import sg.edu.nus.comp.cs4218.exception.CpException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.util.ApplicationRunner;
import sg.edu.nus.comp.cs4218.impl.util.ArgumentResolver;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_REDIR_OUTPUT;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;

class SequenceCommandIT {

    private static final String ECHO_APP = "echo";
    private static final String PASTE_APP = "paste";
    private static final String RM_APP = "rm";
    private static final String CP_APP = "cp";
    private static final String CD_APP = "cd";
    private static final String MV_APP = "mv";
    private static final String SORT_APP = "sort";
    private static final String WC_APP = "wc";
    private static final String SED_APP = "sed";
    private static final String CUT_APP = "cut";
    private static final String INVALID_APP = "lsa";
    private static final String FOLDER_NAME_1 = "folder1";
    private static final String FILE_NAME_1 = "CS4218A";
    private static final String FILE_NAME_2 = "A4218A";
    private static final String FILE_NAME_3 = "CS3203A";
    private static final String FILE_NAME_4 = "A.txt";
    private static final String FILE_NAME_5 = "B.txt";
    private static final String FILE_NAME_6 = "AB.txt";
    private static final String FILE_NAME_7 = "1.txt";
    private static final String FILE_CONTENT_1 = "Hello world";
    private static final String FILE_CONTENT_2 = "How are you";
    private static final String REGEX_EXPR_1 = "s/^/> /";
    private static final String C_FLAG = "-c";
    private static final String RELATIVE_UP = "..";

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
    void testEvaluateSequenceCommandWithValidCallCommandAndCallCommandFormatShouldOutputCorrectly() throws AbstractApplicationException, ShellException {
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
    void testEvaluateSequenceCommandWithValidCallCommandAndPipeCommandFormatShouldOutputCorrectly(@TempDir Path tempDir) throws AbstractApplicationException, ShellException, IOException {
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
     * Tests evaluate method when a ShellException occur in one part.
     * For example: lsa; echo Greetings
     * Where lsa is an invalid application.
     * Expected: Outputs ShellException message followed by a new line during the execution of first command, in this case, lsa which is an invalid application,
     * and continue the execution of the second command.
     * shell: lsa: Invalid app
     * Greetings
     */
    @Test
    void testEvaluateSequenceCommandWithCommandWhichThrowShellExceptionFollowedByValidCommandFormatShouldOutputShellExceptionAndContinueExecution() throws AbstractApplicationException, ShellException {
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
    void testEvaluateSequenceCommandWithCommandWhichThrowAbstractApplicationExceptionFollowedByValidCommandFormatShouldOutputAbstractionApplicationExceptionAndContinueExecution(@TempDir Path tempDir) throws AbstractApplicationException, ShellException {
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
    void testEvaluateSequenceCommandWithOutputRedirectionPasteCommandAndRemoveCommandShouldRemoveTheCreatedFile(@TempDir Path tempDir) throws IOException, AbstractApplicationException, ShellException {
        Path file1 = Paths.get(FILE_NAME_4);
        Path file2 = Paths.get(FILE_NAME_5);
        Path file3 = Paths.get(FILE_NAME_6);
        Files.deleteIfExists(file1); // delete A.txt used for testing if it exists before the test.
        Files.deleteIfExists(file2); // delete B.txt used for testing if it exists before the test.
        Files.deleteIfExists(file3); // delete AB.txt used for testing if it exists before the test.
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
        // clean up
        Files.deleteIfExists(file1);
        Files.deleteIfExists(file2);
        Files.deleteIfExists(file3);
    }

    /**
     * Tests evaluate method when involving streams such as output redirection.
     * This is to ensure streams are open and closed properly and can be used properly.
     * For example: echo streaming > A.txt; rm A.txt
     * Where A.txt does not exist initially.
     * Expected: Removes A.txt after A.txt is created from the execution of ls > A.txt
     */
    @Test
    void testEvaluateSequenceCommandWithOutputRedirectionCommandAndRemoveCommandShouldRemoveTheCreatedFile(@TempDir Path tempDir) throws IOException, AbstractApplicationException, ShellException {
        Path file1 = tempDir.resolve(FILE_NAME_4);
        assertFalse(Files.exists(file1)); // Check A.txt does not exist initially
        commands.add(new CallCommand(Arrays.asList(ECHO_APP, "streaming", Character.toString(CHAR_REDIR_OUTPUT), FILE_NAME_4), new ApplicationRunner(), new ArgumentResolver()));
        commands.add(new CallCommand(Arrays.asList(RM_APP, FILE_NAME_4), new ApplicationRunner(), new ArgumentResolver()));
        SequenceCommand sequenceCommand = new SequenceCommand(commands);
        sequenceCommand.evaluate(System.in, outputStream);
        assertFalse(Files.exists(file1)); // Check that AB.txt does not exist as it should be removed
    }

    /**
     * Tests evaluate method when involving cp command and rm command interaction
     * For example: cp B.txt AB.txt; rm B.txt
     * Where B.txt is an existing file. AB.txt is a non existing file.
     * Expected: Removes B.txt while AB.txt should be a copy of the deleted B.txt
     */
    @Test
    void testEvaluateSequenceCommandWithCpCommandAndRmCommandInteractionShouldRemoveTheCpSrcFile(@TempDir Path tempDir) throws Exception {
        Path srcFile = tempDir.resolve(FILE_NAME_5);
        Path destFile = tempDir.resolve(FILE_NAME_6);
        List<String> contents = Arrays.asList(FILE_CONTENT_1, FILE_CONTENT_2);
        Files.createFile(srcFile);
        Files.write(srcFile, contents);
        assertTrue(Files.exists(srcFile)); // check that B.txt exists
        assertFalse(Files.exists(destFile)); // check that AB.txt does not exist.
        assertEquals(contents, Files.readAllLines(srcFile));
        commands.add(new CallCommand(Arrays.asList(CP_APP, srcFile.toString(), destFile.toString()), new ApplicationRunner(), new ArgumentResolver()));
        commands.add(new CallCommand(Arrays.asList(RM_APP, srcFile.toString()), new ApplicationRunner(), new ArgumentResolver()));
        SequenceCommand sequenceCommand = new SequenceCommand(commands);
        sequenceCommand.evaluate(System.in, outputStream);
        assertFalse(Files.exists(srcFile)); // check that B.txt does not exist.
        assertTrue(Files.exists(destFile)); // check that AB.txt exists.
        assertEquals(contents, Files.readAllLines(destFile)); // check that file content is copied.
    }

    /**
     * Tests evaluate method when involving mv command rm command interaction
     * For example: mv B.txt AB.txt; rm AB.txt
     * Where B.txt is an existing file and AB.txt is a non existing file.
     * Expected: Renames B.txt to AB.txt and then remove AB.txt
     */
    @Test
    void testEvaluateSequenceCommandWithMvCommandAndRmCommandInteractionShouldRenameSrcFileToTargetFileThenRemoveTargetFile(@TempDir Path tempDir) throws Exception {
        Path srcFile = tempDir.resolve(FILE_NAME_5);
        Path targetFile = tempDir.resolve(FILE_NAME_6);
        Files.createFile(srcFile);
        assertTrue(Files.exists(srcFile)); // check that B.txt exists
        assertFalse(Files.exists(targetFile)); // check that AB.txt does not exist.
        commands.add(new CallCommand(Arrays.asList(MV_APP, srcFile.toString(), targetFile.toString()), new ApplicationRunner(), new ArgumentResolver()));
        commands.add(new CallCommand(Arrays.asList(RM_APP, targetFile.toString()), new ApplicationRunner(), new ArgumentResolver()));
        SequenceCommand sequenceCommand = new SequenceCommand(commands);
        sequenceCommand.evaluate(System.in, outputStream);
        assertFalse(Files.exists(srcFile)); // check that B.txt does not exists.
        assertFalse(Files.exists(targetFile)); // check that AB.txt doest not exist.
    }

    /**
     * Tests evaluate method when involving cd command and rm command interaction
     * For example: cd folder1; rm B.txt
     * Where folder1 is an existing directory and the folder1 directory contains an existing B.txt file.
     * Removes B.txt file in folder1 directory.
     */
    @Test
    void testEvaluateSeqeuenceCommandWithCdCommandAndRmCommandInteractionShouldRemoveFileInDirectory(@TempDir Path tempDir) throws Exception {
        Path folder = tempDir.resolve(FOLDER_NAME_1);
        Path file = folder.resolve(FILE_NAME_5);
        Files.createDirectory(folder);
        Files.createFile(file);
        assertTrue(Files.isDirectory(folder));
        assertTrue(Files.exists(file));
        commands.add(new CallCommand(Arrays.asList(CD_APP, folder.toString()), new ApplicationRunner(), new ArgumentResolver()));
        commands.add(new CallCommand(Arrays.asList(RM_APP, file.toString()), new ApplicationRunner(), new ArgumentResolver()));
        SequenceCommand sequenceCommand = new SequenceCommand(commands);
        sequenceCommand.evaluate(System.in, outputStream);
        assertFalse(Files.exists(file)); // check that B.txt is deleted.
    }

    /**
     * Tests evaluate method when involving sort command and rm command interaction
     * For example: sort AB.txt; rm AB.txt
     * Where AB.txt is an existing file containing 2 lines. The first line is How are you and the second line is Hello World.
     * It is mainly to test to ensure stream is closed for those commands that read files. As if streams are not closed, remove command
     * would not be able to remove the file.
     * Expected: Outputs correctly as shown below and remove AB.txt
     * Output:
     * Hello World
     * How are you
     */
    @Test
    void testEvaluateSequenceCommandWithSortCommandAndRmCommandInteractionShouldRemoveFile(@TempDir Path tempDir) throws Exception {
        Path file = tempDir.resolve(FILE_NAME_6);
        Files.createFile(file);
        assertTrue(Files.exists(file)); // check that AB.txt exists.
        List<String> contents = Arrays.asList(FILE_CONTENT_2, FILE_CONTENT_1);
        Files.write(file, contents);
        assertEquals(contents, Files.readAllLines(file));
        commands.add(new CallCommand(Arrays.asList(SORT_APP, file.toString()), new ApplicationRunner(), new ArgumentResolver()));
        commands.add(new CallCommand(Arrays.asList(RM_APP, file.toString()), new ApplicationRunner(), new ArgumentResolver()));
        SequenceCommand sequenceCommand = new SequenceCommand(commands);
        sequenceCommand.evaluate(System.in, outputStream);
        assertEquals(FILE_CONTENT_1 + STRING_NEWLINE + FILE_CONTENT_2 + STRING_NEWLINE, outputStream.toString());
        assertFalse(Files.exists(file)); // check that AB.txt is deleted.
    }

    /**
     * Tests evaluate method when involving wc command and rm command
     * For example: wc AB.txt; rm Ab.txt
     * Where AB.txt is an existing file.
     * It is mainly to test to ensure stream is closed for those commands that read files. As if streams are not closed, remove command
     * would not be able to remove the file.
     * Expected: Outputs correctly as shown below and remove AB.txt
     */
    @Test
    void testEvaluateSequenceCommandWithWcCommandAndRmCommandInteractionShouldRemoveFile(@TempDir Path tempDir) throws Exception {
        Path file = tempDir.resolve(FILE_NAME_6);
        Files.createFile(file);
        assertTrue(Files.exists(file)); // check that AB.txt exists.
        List<String> contents = Arrays.asList(FILE_CONTENT_1, FILE_CONTENT_2);
        Files.write(file, contents);
        assertEquals(contents, Files.readAllLines(file));
        String expected = "       2       5      " + file.toFile().length() + " " + file.toString() + STRING_NEWLINE;
        commands.add(new CallCommand(Arrays.asList(WC_APP, file.toString()), new ApplicationRunner(), new ArgumentResolver()));
        commands.add(new CallCommand(Arrays.asList(RM_APP, file.toString()), new ApplicationRunner(), new ArgumentResolver()));
        SequenceCommand sequenceCommand = new SequenceCommand(commands);
        sequenceCommand.evaluate(System.in, outputStream);
        assertEquals(expected, outputStream.toString());
        assertFalse(Files.exists(file)); // check that AB.txt is deleted.
    }

    /**
     * Tests evaluate method when involving sed command and rm command
     * For example: sed "s/^/> /" AB.txt; rm Ab.txt
     * Where AB.txt is an existing file.
     * It is mainly to test to ensure stream is closed for those commands that read files. As if streams are not closed, remove command
     * would not be able to remove the file.
     * Expected: Outputs correctly as shown below and remove AB.txt
     */
    @Test
    void testEvaluateSequenceCommandWithSedCommandAndRmCommandInteractionShouldRemoveFile(@TempDir Path tempDir) throws Exception {
        Path file = tempDir.resolve(FILE_NAME_6);
        Files.createFile(file);
        assertTrue(Files.exists(file)); // check that AB.txt exists.
        List<String> contents = Arrays.asList(FILE_CONTENT_1, FILE_CONTENT_2);
        Files.write(file, contents);
        assertEquals(contents, Files.readAllLines(file));
        commands.add(new CallCommand(Arrays.asList(SED_APP, REGEX_EXPR_1, file.toString()), new ApplicationRunner(), new ArgumentResolver()));
        commands.add(new CallCommand(Arrays.asList(RM_APP, file.toString()), new ApplicationRunner(), new ArgumentResolver()));
        SequenceCommand sequenceCommand = new SequenceCommand(commands);
        sequenceCommand.evaluate(System.in, outputStream);
        assertEquals("> " + FILE_CONTENT_1 + STRING_NEWLINE + "> " + FILE_CONTENT_2 + STRING_NEWLINE, outputStream.toString());
        assertFalse(Files.exists(file)); // check that AB.txt is deleted.
    }

    /**
     * Tests evaluate method when involving cut command and rm command
     * For example: sed "s/^/> /" AB.txt; rm AB.txt
     * Where AB.txt is an existing file.
     * It is mainly to test to ensure stream is closed for those commands that read files. As if streams are not closed, remove command
     * would not be able to remove the file.
     * Expected: Outputs correctly as shown below and remove AB.txt
     */
    @Test
    void testEvaluateSequenceCommandWithCutCommandAndRmCommandInteractionShouldRemoveFile(@TempDir Path tempDir) throws Exception {
        Path file = tempDir.resolve(FILE_NAME_6);
        Files.createFile(file);
        assertTrue(Files.exists(file)); // check that AB.txt exists.
        List<String> contents = Arrays.asList(FILE_CONTENT_1, FILE_CONTENT_2);
        Files.write(file, contents);
        assertEquals(contents, Files.readAllLines(file));
        commands.add(new CallCommand(Arrays.asList(CUT_APP, C_FLAG, "5-7", file.toString()), new ApplicationRunner(), new ArgumentResolver()));
        commands.add(new CallCommand(Arrays.asList(RM_APP, file.toString()), new ApplicationRunner(), new ArgumentResolver()));
        SequenceCommand sequenceCommand = new SequenceCommand(commands);
        sequenceCommand.evaluate(System.in, outputStream);
        assertEquals("o w" + STRING_NEWLINE + "are" + STRING_NEWLINE, outputStream.toString());
        assertFalse(Files.exists(file)); // check that AB.txt is deleted.
    }

    // Tests with at least 2 semicolon operators

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
    void testEvaluateSequenceCommandWithValidCallCommandAndSequenceCommandFormatShouldOutputCorrect() throws AbstractApplicationException, ShellException {
        commands.add(new CallCommand(Arrays.asList(ECHO_APP, "Good", "Afternoon"), new ApplicationRunner(), new ArgumentResolver()));
        commands.add(new CallCommand(Arrays.asList(ECHO_APP, "Hi"), new ApplicationRunner(), new ArgumentResolver()));
        commands.add(new CallCommand(Arrays.asList(ECHO_APP, "Sweet", "dreams"), new ApplicationRunner(), new ArgumentResolver()));
        SequenceCommand sequenceCommand = new SequenceCommand(commands);
        sequenceCommand.evaluate(System.in, outputStream);
        assertEquals("Good Afternoon" + System.lineSeparator() + "Hi" + System.lineSeparator() + "Sweet dreams" + System.lineSeparator(), outputStream.toString());
    }

    /**
     * Tests evaluate method with paste command cd command and rm command
     * For example paste A.txt B.txt > AB.txt; cd folder1; rm ../A.txt ../B.txt ../AB.txt
     * Where A.txt and B.txt exists and AB.txt does not exist initially and folder1 is an existing directory.
     * Expected: Removes all the three files.
     */
    @Test
    void testEvaluateSequenceCommandsWithPasteCommandAndCdCommandAndRmCommandShouldRemoveFiles(@TempDir Path tempDir) throws Exception {
        Path file1 = tempDir.resolve(FILE_NAME_4);
        Path file2 = tempDir.resolve(FILE_NAME_5);
        Path file3 = tempDir.resolve(FILE_NAME_6);
        Path folder = tempDir.resolve(FOLDER_NAME_1);
        Files.createDirectory(folder);
        Files.createFile(file1);
        Files.write(file1, Collections.singletonList(FILE_CONTENT_1));
        Files.createFile(file2);
        Files.write(file2, Collections.singletonList(FILE_CONTENT_2));
        assertTrue(Files.exists(file1)); // check that A.txt exists
        assertTrue(Files.exists(file2)); // check that B.txt exists
        assertFalse(Files.exists(file3)); // check that AB.txt does not exist
        assertTrue(Files.isDirectory(folder)); // check that folder1 exists
        commands.add(new CallCommand(Arrays.asList(PASTE_APP, file1.toString(), file2.toString(), Character.toString(CHAR_REDIR_OUTPUT), file3.toString()), new ApplicationRunner(), new ArgumentResolver()));
        commands.add(new CallCommand(Arrays.asList(CD_APP, folder.toString()), new ApplicationRunner(), new ArgumentResolver()));
        commands.add(new CallCommand(Arrays.asList(RM_APP, RELATIVE_UP + File.separator + FILE_NAME_4, RELATIVE_UP + File.separator + FILE_NAME_5, RELATIVE_UP + File.separator + FILE_NAME_6), new ApplicationRunner(), new ArgumentResolver()));
        SequenceCommand sequenceCommand = new SequenceCommand(commands);
        sequenceCommand.evaluate(System.in, outputStream);
        assertFalse(Files.exists(file1)); // check that A.txt is deleted
        assertFalse(Files.exists(file2)); // check that B.txt is deleted
        assertFalse(Files.exists(file3)); // check that AB.txt is deleted
        assertTrue(Files.isDirectory(folder)); // check that folder1 exists
    }

    /**
     * Tests evaluate method with paste command mv command and rm command
     * For example paste A.txt B.txt AB.txt; mv A.txt B.txt AB.txt folder1; rm A.txt B.txt folder1/AB.txt
     * Where A.txt and B.txt and AB.txt exist and folder1 is an existing directory.
     * Expected: Removes folder1/A.txt folder1/B.txt and folder1/AB.txt
     */
    @Test
    void testEvaluateSequenceCommandsWithPasteCommandAndMvCommandAndRmCommandShouldRemoveFile(@TempDir Path tempDir) throws Exception {
        Path file1 = tempDir.resolve(FILE_NAME_4);
        Path file2 = tempDir.resolve(FILE_NAME_5);
        Path file3 = tempDir.resolve(FILE_NAME_6);
        Path folder = tempDir.resolve(FOLDER_NAME_1);
        Files.createDirectory(folder);
        Files.createFile(file1);
        Files.write(file1, Collections.singletonList(FILE_CONTENT_1));
        Files.createFile(file2);
        Files.write(file2, Collections.singletonList(FILE_CONTENT_2));
        Files.createFile(file3);
        assertTrue(Files.exists(file1)); // check that A.txt exists
        assertTrue(Files.exists(file2)); // check that B.txt exists
        assertTrue(Files.exists(file3)); // check that AB.txt exists
        assertTrue(Files.isDirectory(folder)); // check that folder1 exists
        commands.add(new CallCommand(Arrays.asList(PASTE_APP, file1.toString(), file2.toString(), file3.toString()), new ApplicationRunner(), new ArgumentResolver()));
        commands.add(new CallCommand(Arrays.asList(MV_APP, file1.toString(), file2.toString(), file3.toString(), folder.toString()), new ApplicationRunner(), new ArgumentResolver()));
        commands.add(new CallCommand(Arrays.asList(RM_APP, folder.resolve(FILE_NAME_4).toString(), folder.resolve(FILE_NAME_5).toString(), folder.resolve(FILE_NAME_6).toString()), new ApplicationRunner(), new ArgumentResolver()));
        SequenceCommand sequenceCommand = new SequenceCommand(commands);
        sequenceCommand.evaluate(System.in, outputStream);
        assertFalse(Files.exists(file1)); // check that A.txt is moved
        assertFalse(Files.exists(file2)); // check that B.txt is moved
        assertFalse(Files.exists(file3)); // check that AB.txt is moved
        assertFalse(Files.exists(folder.resolve(FILE_NAME_4))); // check that folder1/A.txt is deleted
        assertFalse(Files.exists(folder.resolve(FILE_NAME_5))); // check that folder1/B.txt is deleted
        assertFalse(Files.exists(folder.resolve(FILE_NAME_6))); // check that folder1/AB.txt is deleted
        assertTrue(Files.isDirectory(folder)); // check that folder1 exists
    }

    /**
     * Tests evaluate method with paste command mv command and rm command
     * For example paste A.txt B.txt AB.txt; cp A.txt B.txt AB.txt folder1; rm folder1/A.txt folder1/B.txt folder1/AB.txt
     * Where A.txt and B.txt and AB.txt exist and folder1 is an existing directory.
     * Expected: Removes folder1/A.txt, folder1/B.txt and folder1/AB.txt
     */
    @Test
    void testEvaluateSequenceCommandsWithPasteCommandAndCpCommandAndRmCommandShouldRemoveFilesInFolder(@TempDir Path tempDir) throws Exception {
        Path file1 = tempDir.resolve(FILE_NAME_4);
        Path file2 = tempDir.resolve(FILE_NAME_5);
        Path file3 = tempDir.resolve(FILE_NAME_6);
        Path folder = tempDir.resolve(FOLDER_NAME_1);
        Files.createDirectory(folder);
        Files.createFile(file1);
        Files.write(file1, Collections.singletonList(FILE_CONTENT_1));
        Files.createFile(file2);
        Files.write(file2, Collections.singletonList(FILE_CONTENT_2));
        Files.createFile(file3);
        assertTrue(Files.exists(file1)); // check that A.txt exists
        assertTrue(Files.exists(file2)); // check that B.txt exists
        assertTrue(Files.exists(file3)); // check that AB.txt exists
        assertTrue(Files.isDirectory(folder)); // check that folder1 exists
        commands.add(new CallCommand(Arrays.asList(PASTE_APP, file1.toString(), file2.toString(), file3.toString()), new ApplicationRunner(), new ArgumentResolver()));
        commands.add(new CallCommand(Arrays.asList(CP_APP, file1.toString(), file2.toString(), file3.toString(), folder.toString()), new ApplicationRunner(), new ArgumentResolver()));
        commands.add(new CallCommand(Arrays.asList(RM_APP, folder.resolve(FILE_NAME_4).toString(), folder.resolve(FILE_NAME_5).toString(), folder.resolve(FILE_NAME_6).toString()), new ApplicationRunner(), new ArgumentResolver()));
        SequenceCommand sequenceCommand = new SequenceCommand(commands);
        sequenceCommand.evaluate(System.in, outputStream);
        assertTrue(Files.exists(file1)); // check that A.txt exists
        assertTrue(Files.exists(file2)); // check that B.txt exists
        assertTrue(Files.exists(file3)); // check that AB.txt exists
        assertFalse(Files.exists(folder.resolve(FILE_NAME_4))); // check that folder1/A.txt is deleted
        assertFalse(Files.exists(folder.resolve(FILE_NAME_5))); // check that folder1/B.txt is deleted
        assertFalse(Files.exists(folder.resolve(FILE_NAME_6))); // check that folder1/AB.txt is deleted
        assertTrue(Files.isDirectory(folder)); // check that folder1 exists
    }

    /**
     * Tests evaluate method with paste command mv command and rm command
     * For example cd A.txt; cp A.txt B.txt AB.txt 1.txt folder1; rm folder1/A.txt folder1/B.txt
     * Where A.txt and B.txt and AB.txt exist and 1.txt does not exist and folder1 is an existing directory.
     * Expected: Outputs cd ERR_IS_NOT_DIR exception followed by a new line and cp ERR_FILE_NOT_FOUND exception followed by a new line.
     * At the same time, A.txt, B.txt and AB.txt are copied to folder1 and then folder1/A.txt and folder1/B.txt folder1/AB.txt are removed.
     */
    @Test
    void testEvaluateSequenceCommandsWithInvalidCdCommandAndInvalidCpCommandAndValidRmCommandShouldOutputExceptionsAndRemoveFilesInFolder(@TempDir Path tempDir) throws Exception {
        Path file1 = tempDir.resolve(FILE_NAME_4);
        Path file2 = tempDir.resolve(FILE_NAME_5);
        Path file3 = tempDir.resolve(FILE_NAME_6);
        Path file4 = tempDir.resolve(FILE_NAME_7);
        Path folder = tempDir.resolve(FOLDER_NAME_1);
        Files.createDirectory(folder);
        Files.createFile(file1);
        Files.write(file1, Collections.singletonList(FILE_CONTENT_1));
        Files.createFile(file2);
        Files.write(file2, Collections.singletonList(FILE_CONTENT_2));
        Files.createFile(file3);
        assertTrue(Files.exists(file1)); // check that A.txt exists
        assertTrue(Files.exists(file2)); // check that B.txt exists
        assertTrue(Files.exists(file3)); // check that AB.txt exists
        assertFalse(Files.exists(file4)); // check that 1.txt does not exist
        assertTrue(Files.isDirectory(folder)); // check that folder1 exists
        commands.add(new CallCommand(Arrays.asList(CD_APP, file1.toString()), new ApplicationRunner(), new ArgumentResolver()));
        commands.add(new CallCommand(Arrays.asList(CP_APP, file1.toString(), file2.toString(), file3.toString(), file4.toString(), folder.toString()), new ApplicationRunner(), new ArgumentResolver()));
        commands.add(new CallCommand(Arrays.asList(RM_APP, folder.resolve(FILE_NAME_4).toString(), folder.resolve(FILE_NAME_5).toString(), folder.resolve(FILE_NAME_6).toString()), new ApplicationRunner(), new ArgumentResolver()));
        SequenceCommand sequenceCommand = new SequenceCommand(commands);
        sequenceCommand.evaluate(System.in, outputStream);
        assertTrue(Files.exists(file1)); // check that A.txt exists
        assertTrue(Files.exists(file2)); // check that B.txt exists
        assertTrue(Files.exists(file3)); // check that AB.txt exists
        assertFalse(Files.exists(file4)); // check that 1.txt does not exist
        assertFalse(Files.exists(folder.resolve(FILE_NAME_4))); // check that folder1/A.txt is deleted
        assertFalse(Files.exists(folder.resolve(FILE_NAME_5))); // check that folder1/B.txt is deleted
        assertFalse(Files.exists(folder.resolve(FILE_NAME_6))); // check that folder1/AB.txt is deleted
        assertTrue(Files.isDirectory(folder)); // check that folder1 exists
        assertEquals(new CdException(ERR_IS_NOT_DIR).getMessage() + STRING_NEWLINE + new CpException(ERR_FILE_NOT_FOUND).getMessage() + STRING_NEWLINE, outputStream.toString());
    }
}