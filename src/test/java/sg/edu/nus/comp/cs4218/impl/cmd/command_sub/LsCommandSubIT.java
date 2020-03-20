package sg.edu.nus.comp.cs4218.impl.cmd.command_sub;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.EnvironmentHelper;
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

public class LsCommandSubIT {
    private ApplicationRunner applicationRunner;
    private ArgumentResolver argumentResolver;
    private InputStream ourTestStdin;
    private OutputStream ourTestStdout;
    private static final String TEST_STDIN_MSG_1 = "drüberspringen";
    private final Path testFile1 = Paths.get(TestFileUtils.TESTDATA_DIR + "test1.txt");
    private final Path testFile2 = Paths.get(TestFileUtils.TESTDATA_DIR + "test2.txt");
    private final Path testFile3 = Paths.get(TestFileUtils.TESTDATA_DIR + "test3.csv");

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
    void testLsCommandAndRmAsSubCommandShouldEvaluateSuccessfully() throws AbstractApplicationException, ShellException {
        fail();
    }

    @Test
    void testLsCommandAndEchoAsSubCommandShouldEvaluateSuccessfully() throws AbstractApplicationException, ShellException {
        List<String> args = Arrays.asList("ls", "`echo " + TestFileUtils.TESTDATA_DIR + "`");
        CallCommand callCommand = new CallCommand(args, applicationRunner, argumentResolver);
        callCommand.evaluate(ourTestStdin, ourTestStdout);
        String expectedResult = "test1.txt" + System.lineSeparator() + "test2.txt" + System.lineSeparator() +
                "test3.csv" + System.lineSeparator() + "test4.txt" + System.lineSeparator();
        assertEquals(expectedResult, ourTestStdout.toString());
    }

    @Test
    void testLsCommandAndSedAsSubCommandShouldEvaluateSuccessfully() throws AbstractApplicationException, ShellException {
        fail();
    }

    @Test
    void testLsCommandAndDiffAsSubCommandShouldEvaluateSuccessfully() throws AbstractApplicationException, ShellException {
        fail();
    }

    @Test
    void testLsCommandAndGrepAsSubCommandShouldEvaluateSuccessfully() throws AbstractApplicationException, ShellException {
        fail();
    }

    @Test
    void testLsCommandAndWcAsSubCommandShouldEvaluateSuccessfully() throws AbstractApplicationException, ShellException {
        fail();
    }

    @Test
    void testLsCommandAndCdAsSubCommandShouldEvaluateSuccessfully() throws AbstractApplicationException, ShellException {
        fail();
    }

    @Test
    void testLsCommandAndCpAsSubCommandShouldEvaluateSuccessfully() throws AbstractApplicationException, ShellException {
        fail();
    }

    @Test
    void testLsCommandAndCutAsSubCommandShouldEvaluateSuccessfully() throws AbstractApplicationException, ShellException {
        fail();
    }

    @Test
    void testLsCommandAndLsAsSubCommandShouldEvaluateSuccessfully() throws AbstractApplicationException, ShellException {
        List<String> args = Arrays.asList("ls", "`ls " + TestFileUtils.TESTDATA_DIR + "`");
        CallCommand callCommand = new CallCommand(args, applicationRunner, argumentResolver);
        callCommand.evaluate(ourTestStdin, ourTestStdout);
        String expectedResult = "" + System.lineSeparator();
        assertEquals(expectedResult, ourTestStdout.toString());
    }

    @Test
    void testLsCommandAndSortAsSubCommandShouldEvaluateSuccessfully() throws AbstractApplicationException, ShellException {
        fail();
    }

    @Test
    void testLsCommandAndFindAsSubCommandShouldEvaluateSuccessfully() throws AbstractApplicationException, ShellException {
        fail();
    }

    @Test
    void testLsCommandAndMvAsSubCommandShouldEvaluateSuccessfully() throws AbstractApplicationException, ShellException {
        fail();
    }
}
