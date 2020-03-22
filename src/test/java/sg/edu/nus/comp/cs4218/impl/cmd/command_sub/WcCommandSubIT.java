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

public class WcCommandSubIT {
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
    void testWcCommandAndEchoAsSubCommandShouldEvaluateSuccessfully() throws AbstractApplicationException, ShellException {
        List<String> args = Arrays.asList("wc", "`echo " + testFile1.toFile().getPath() + "`");
        CallCommand callCommand = new CallCommand(args, applicationRunner, argumentResolver);
        callCommand.evaluate(ourTestStdin, ourTestStdout);
        String expectedResult = String.format(" %7d %7d %7d", 6, 73, 553) + " " +
                testFile1.toFile().getPath() + System.lineSeparator();
        assertEquals(expectedResult, ourTestStdout.toString());
    }

    @Test
    void testWcCommandAndPasteAsSubCommandShouldEvaluateSuccessfully() throws AbstractApplicationException, ShellException {
        List<String> args = Arrays.asList("wc", "`paste " + testFile4.toFile().getPath() + " " + testFile4.toFile().getPath() + "`");
        CallCommand callCommand = new CallCommand(args, applicationRunner, argumentResolver);
        callCommand.evaluate(ourTestStdin, ourTestStdout);
        String expectedResult = String.format(" %7d %7d %7d", 5, 320, 2081) + " " +
                testFile2.toFile().getPath() + System.lineSeparator() +
                String.format(" %7d %7d %7d", 5, 320, 2081) + " " +
                testFile2.toFile().getPath() + System.lineSeparator() +
                String.format(" %7d %7d %7d", 10, 640, 4162) + " total" + System.lineSeparator();
        assertEquals(expectedResult, ourTestStdout.toString());
    }

    @Test
    void testWcCommandAndSedAsSubCommandShouldEvaluateSuccessfully() throws AbstractApplicationException, ShellException {
        List<String> args = Arrays.asList("wc", "-clw", "`sed \"s|" + testFile2.getFileName() +
                "|" + testFile2.toFile().getPath() + "|\" " + testFile5.toFile().getPath() + "`");
        CallCommand callCommand = new CallCommand(args, applicationRunner, argumentResolver);
        callCommand.evaluate(ourTestStdin, ourTestStdout);
        String expectedResult = String.format(" %7d %7d %7d", 5, 320, 2081) + " " +
                testFile2.toFile().getPath() + System.lineSeparator();
        assertEquals(expectedResult, ourTestStdout.toString());
    }

    @Test
    void testWcCommandAndGrepAsSubCommandShouldEvaluateSuccessfully() throws AbstractApplicationException, ShellException {
        List<String> args = Arrays.asList("wc", "-l", "`grep \"test\" " + testFile4.toFile().getPath() + "`");
        CallCommand callCommand = new CallCommand(args, applicationRunner, argumentResolver);
        callCommand.evaluate(ourTestStdin, ourTestStdout);
        String expectedResult = String.format(" %7d", 5) + " " +
                testFile2.toFile().getPath() + System.lineSeparator();
        assertEquals(expectedResult, ourTestStdout.toString());
    }
    
    @Test
    void testWcCommandAndCutAsSubCommandShouldEvaluateSuccessfully() throws AbstractApplicationException, ShellException {
        List<String> args = Arrays.asList("wc", "-clw", "`cut -c 1-123 " + testFile4.toFile().getPath() + "`");
        CallCommand callCommand = new CallCommand(args, applicationRunner, argumentResolver);
        callCommand.evaluate(ourTestStdin, ourTestStdout);
        String expectedResult = String.format(" %7d %7d %7d", 5, 320, 2081) + " " +
                testFile2.toFile().getPath() + System.lineSeparator();
        assertEquals(expectedResult, ourTestStdout.toString());
    }

    @Test
    void testWcCommandAndLsAsSubCommandShouldEvaluateSuccessfully() throws AbstractApplicationException, ShellException {
        List<String> args = Arrays.asList("wc", "-w", "`ls -d " + TestFileUtils.TESTDATA_DIR + "`" +
                testFile1.toFile().getName());
        CallCommand callCommand = new CallCommand(args, applicationRunner, argumentResolver);
        callCommand.evaluate(ourTestStdin, ourTestStdout);
        String expectedResult = String.format(" %7d", 73) + " " +
                testFile1.toFile().getPath() + System.lineSeparator();
        assertEquals(expectedResult, ourTestStdout.toString());
    }

    @Test
    void testWcCommandAndSortAsSubCommandShouldEvaluateSuccessfully() throws AbstractApplicationException, ShellException {
        List<String> args = Arrays.asList("wc", "-w", "`sort -f " + testFile4.toFile().getPath() + "`");
        CallCommand callCommand = new CallCommand(args, applicationRunner, argumentResolver);
        callCommand.evaluate(ourTestStdin, ourTestStdout);
        String expectedResult = String.format(" %7d", 320) + " " +
                testFile2.toFile().getPath() + System.lineSeparator();
        assertEquals(expectedResult, ourTestStdout.toString());
    }

    @Test
    void testWcCommandAndFindAsSubCommandShouldEvaluateSuccessfully() throws AbstractApplicationException, ShellException {
        List<String> args = Arrays.asList("wc", "-cw", "`find " + TestFileUtils.TESTDATA_DIR + " -name \"*test*.txt\"`");
        CallCommand callCommand = new CallCommand(args, applicationRunner, argumentResolver);
        callCommand.evaluate(ourTestStdin, ourTestStdout);
        String expectedResult = String.format(" %7d %7d", 73, 553) + " " + testFile1.toFile().getPath() + System.lineSeparator() +
                String.format(" %7d %7d", 320, 2081) + " " + testFile2.toFile().getPath() + System.lineSeparator() +
                String.format(" %7d %7d", 1, 55) + " " + testFile4.toFile().getPath() + System.lineSeparator() +
                String.format(" %7d %7d", 1, 9) + " " + testFile5.toFile().getPath() + System.lineSeparator() +
                String.format(" %7d %7d", 395, 2698) + " total" + System.lineSeparator();
        assertEquals(expectedResult, ourTestStdout.toString());
    }
}
