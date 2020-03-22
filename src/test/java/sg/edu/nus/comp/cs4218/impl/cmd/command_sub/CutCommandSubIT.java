package sg.edu.nus.comp.cs4218.impl.cmd.command_sub;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.cmd.CallCommand;
import sg.edu.nus.comp.cs4218.impl.util.ApplicationRunner;
import sg.edu.nus.comp.cs4218.impl.util.ArgumentResolver;
import sg.edu.nus.comp.cs4218.impl.util.TestFileUtils;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class CutCommandSubIT {
    private ApplicationRunner applicationRunner;
    private ArgumentResolver argumentResolver;
    private InputStream ourTestStdin;
    private OutputStream ourTestStdout;
    private static final String TEST_STDIN_MSG_1 = "drüberspringen";
    private final Path testFile1 = Paths.get(TestFileUtils.TESTDATA_DIR + "test1.txt");
    private final Path testFile2 = Paths.get(TestFileUtils.TESTDATA_DIR + "test2.txt");
    private final Path testFile3 = Paths.get(TestFileUtils.TESTDATA_DIR + "test3.csv");
    private final Path testFile4 = Paths.get(TestFileUtils.TESTDATA_DIR + "testFileWithTestDataAbsoluteFilePath.txt");
    private final Path testFile5 = Paths.get(TestFileUtils.TESTDATA_DIR + "testFileWithTestDataFileName.txt");

    @BeforeEach
    public void setUp() {
        applicationRunner = new ApplicationRunner();
        argumentResolver = new ArgumentResolver();
        ourTestStdin = new ByteArrayInputStream(TEST_STDIN_MSG_1.getBytes());
        ourTestStdout = new ByteArrayOutputStream();
    }

    @AfterEach
    public void tearDown() throws IOException {
        ourTestStdin.close();
        ourTestStdout.close();
    }

    // Positive test cases
    @Test
    void testCutCommandAndEchoAsSubCommandShouldEvaluateSuccessfully() throws AbstractApplicationException, ShellException {
        List<String> args = Arrays.asList("cut", "-c", "2", "`echo " + testFile3.toFile().getPath() + "`");
        CallCommand callCommand = new CallCommand(args, applicationRunner, argumentResolver);
        callCommand.evaluate(ourTestStdin, ourTestStdout);
        String expectedResult = "." + System.lineSeparator() + "," + System.lineSeparator() +
                "1" + System.lineSeparator() + "1" + System.lineSeparator() +
                "2" + System.lineSeparator() + "5" + System.lineSeparator() +
                "0" + System.lineSeparator();
        assertEquals(expectedResult, ourTestStdout.toString());
    }

    @Test
    void testCutCommandAndPasteAsSubCommandShouldEvaluateSuccessfully() throws AbstractApplicationException, ShellException {
        List<String> args = Arrays.asList("cut", "-c", "2", "`paste " + testFile4.toFile().getPath() + "`");
        CallCommand callCommand = new CallCommand(args, applicationRunner, argumentResolver);
        callCommand.evaluate(ourTestStdin, ourTestStdout);
        String expectedResult = "o" + System.lineSeparator() + System.lineSeparator() + "u" + System.lineSeparator() +
                System.lineSeparator() + "u" + System.lineSeparator();
        assertEquals(expectedResult, ourTestStdout.toString());
    }

    @Test
    void testCutCommandAndSedAsSubCommandShouldEvaluateSuccessfully() throws AbstractApplicationException, ShellException {
        List<String> args = Arrays.asList("cut", "-b", "4", "`sed \"s|" + testFile2.getFileName() +
                "|" + testFile2.toFile().getPath() + "|\" " + testFile5.toFile().getPath() + "`");
        CallCommand callCommand = new CallCommand(args, applicationRunner, argumentResolver);
        callCommand.evaluate(ourTestStdin, ourTestStdout);
        String expectedResult = "e" + System.lineSeparator() + System.lineSeparator() +
                "s" + System.lineSeparator() + System.lineSeparator() + "p" + System.lineSeparator();
        assertEquals(expectedResult, ourTestStdout.toString());
    }

    @Test
    void testCutCommandAndDiffAsSubCommandShouldEvaluateSuccessfully() throws AbstractApplicationException, ShellException {
        fail();
    }

    @Test
    void testCutCommandAndGrepAsSubCommandShouldEvaluateSuccessfully() throws AbstractApplicationException, ShellException {
        List<String> args = Arrays.asList("cut", "-b", "4-13", "`grep \"cs4218\" " + testFile4.toFile().getPath() + "`");
        CallCommand callCommand = new CallCommand(args, applicationRunner, argumentResolver);
        callCommand.evaluate(ourTestStdin, ourTestStdout);
        String expectedResult = "em ipsum d" + System.lineSeparator() + System.lineSeparator() +
                "smod quis " + System.lineSeparator() + System.lineSeparator() +
                "pis massa " + System.lineSeparator();
        assertEquals(expectedResult, ourTestStdout.toString());
    }

    @Test
    void testCutCommandAndWcAsSubCommandShouldEvaluateSuccessfully() throws AbstractApplicationException, ShellException {
        fail();
    }

    @Test
    void testCutCommandAndCutAsSubCommandShouldEvaluateSuccessfully() throws AbstractApplicationException, ShellException {
        List<String> args = Arrays.asList("cut", "-b", "4", "`cut -c 1-55 " + testFile4.toFile().getPath() + "`");
        CallCommand callCommand = new CallCommand(args, applicationRunner, argumentResolver);
        callCommand.evaluate(ourTestStdin, ourTestStdout);
        String expectedResult = "e" + System.lineSeparator() + System.lineSeparator() +
                "s" + System.lineSeparator() + System.lineSeparator() + "p" + System.lineSeparator();
        assertEquals(expectedResult, ourTestStdout.toString());
    }

    @Test
    void testCutCommandAndLsAsSubCommandShouldEvaluateSuccessfully() throws AbstractApplicationException, ShellException {
        List<String> args = Arrays.asList("cut", "-b", "4", "`ls -d " + TestFileUtils.TESTDATA_DIR + "`*Name*");
        CallCommand callCommand = new CallCommand(args, applicationRunner, argumentResolver);
        callCommand.evaluate(ourTestStdin, ourTestStdout);
        String expectedResult = "t" + System.lineSeparator();
        assertEquals(expectedResult, ourTestStdout.toString());
    }

    @Test
    void testCutCommandAndSortAsSubCommandShouldEvaluateSuccessfully() throws AbstractApplicationException, ShellException {
        List<String> args = Arrays.asList("cut", "-b", "4", "`sort " + testFile4.toFile().getPath() + "`");
        CallCommand callCommand = new CallCommand(args, applicationRunner, argumentResolver);
        callCommand.evaluate(ourTestStdin, ourTestStdout);
        String expectedResult = "e" + System.lineSeparator() + System.lineSeparator() +
                "s" + System.lineSeparator() + System.lineSeparator() + "p" + System.lineSeparator();
        assertEquals(expectedResult, ourTestStdout.toString());
    }

    @Test
    void testCutCommandAndFindAsSubCommandShouldEvaluateSuccessfully() throws AbstractApplicationException, ShellException {
        List<String> args = Arrays.asList("cut", "-b", "4", "`find " + TestFileUtils.TESTDATA_DIR +
                " -name \"test*.csv\"`");
        CallCommand callCommand = new CallCommand(args, applicationRunner, argumentResolver);
        callCommand.evaluate(ourTestStdin, ourTestStdout);
        String expectedResult = "," + System.lineSeparator() + "3" + System.lineSeparator() +
                " " + System.lineSeparator() + " " + System.lineSeparator() +
                " " + System.lineSeparator() + "," + System.lineSeparator() + "," + System.lineSeparator();
        assertEquals(expectedResult, ourTestStdout.toString());
    }
}
