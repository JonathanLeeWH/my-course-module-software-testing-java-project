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

public class GrepCommandSubIT {
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
    void testGrepCommandAndRmAsSubCommandShouldEvaluateSuccessfully() throws AbstractApplicationException, ShellException {
        fail();
    }

    @Test
    void testGrepCommandAndEchoAsSubCommandShouldEvaluateSuccessfully() throws AbstractApplicationException, ShellException {
        List<String> args = Arrays.asList("grep", "`echo assignments`", testFile1.toFile().getPath());
        CallCommand callCommand = new CallCommand(args, applicationRunner, argumentResolver);
        callCommand.evaluate(ourTestStdin, ourTestStdout);
        String expectedResult = "crucial skills on testing and debugging through hands-on assignments." + System.lineSeparator();
        assertEquals(expectedResult, ourTestStdout.toString());
    }

    @Test
    void testGrepCommandAndSedAsSubCommandShouldEvaluateSuccessfully() throws AbstractApplicationException, ShellException {
        fail();
    }

    @Test
    void testGrepCommandAndDiffAsSubCommandShouldEvaluateSuccessfully() throws AbstractApplicationException, ShellException {
        fail();
    }

    @Test
    void testGrepCommandAndGrepAsSubCommandShouldEvaluateSuccessfully() throws AbstractApplicationException, ShellException {
        List<String> args = Arrays.asList("grep", "\"`grep 'CS4218' " + testFile1.toFile().getPath() + "`\"", testFile1.toFile().getPath());
        CallCommand callCommand = new CallCommand(args, applicationRunner, argumentResolver);
        callCommand.evaluate(ourTestStdin, ourTestStdout);
        String expectedResult = "CS4218: Software Testing" + System.lineSeparator();
        assertEquals(expectedResult, ourTestStdout.toString());
    }

    @Test
    void testGrepCommandAndWcAsSubCommandShouldEvaluateSuccessfully() throws AbstractApplicationException, ShellException {
        fail();
    }

    @Test
    void testGrepCommandAndCdAsSubCommandShouldEvaluateSuccessfully() throws AbstractApplicationException, ShellException {
        fail();
    }

    @Test
    void testGrepCommandAndCpAsSubCommandShouldEvaluateSuccessfully() throws AbstractApplicationException, ShellException {
        fail();
    }

    @Test
    void testGrepCommandAndCutAsSubCommandShouldEvaluateSuccessfully() throws AbstractApplicationException, ShellException {
        List<String> args = Arrays.asList("grep", "\"`cut -c 1 " + testFile3.toFile().getPath() + "`\"", testFile3.toFile().getPath());
        CallCommand callCommand = new CallCommand(args, applicationRunner, argumentResolver);
        callCommand.evaluate(ourTestStdin, ourTestStdout);
        String expectedResult = "1.0, 5.0" + System.lineSeparator() + "2, 3" + System.lineSeparator() +
                "51, 15" + System.lineSeparator() + "21, 4" + System.lineSeparator() +
                "22, 41" + System.lineSeparator() + "551, 1200" + System.lineSeparator() +
                "001, 010" + System.lineSeparator();
        assertEquals(expectedResult, ourTestStdout.toString());
    }

    @Test
    void testGrepCommandAndLsAsSubCommandShouldEvaluateSuccessfully() throws AbstractApplicationException, ShellException {
        fail();
    }

    @Test
    void testGrepCommandAndSortAsSubCommandShouldEvaluateSuccessfully() throws AbstractApplicationException, ShellException {
        fail();
    }

    @Test
    void testGrepCommandAndFindAsSubCommandShouldEvaluateSuccessfully() throws AbstractApplicationException, ShellException {
        fail();
    }

    @Test
    void testGrepCommandAndMvAsSubCommandShouldEvaluateSuccessfully() throws AbstractApplicationException, ShellException {
        fail();
    }
}
