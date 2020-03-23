package sg.edu.nus.comp.cs4218.impl.cmd.command_sub;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.CutException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.app.CutApplication;
import sg.edu.nus.comp.cs4218.impl.parser.ArgsParser;
import sg.edu.nus.comp.cs4218.impl.util.ArgumentResolver;
import sg.edu.nus.comp.cs4218.impl.util.TestFileUtils;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SuppressWarnings({"PMD.AvoidDuplicateLiterals"})
public class CommandSubIT {
    private ArgumentResolver argumentResolver;
    private InputStream ourTestStdin;
    private OutputStream ourTestStdout;
    private static final String TEST_STDIN_MSG_1 = "drüberspringen";
    private final Path testFile1 = Paths.get(TestFileUtils.TESTDATA_DIR + "test1.txt");
    private final Path testFile2 = Paths.get(TestFileUtils.TESTDATA_DIR + "test2.txt");
    private final Path testFile3 = Paths.get(TestFileUtils.TESTDATA_DIR + "test3.csv");

    @BeforeEach
    public void setUp() {
        argumentResolver = new ArgumentResolver();
        ourTestStdin = new ByteArrayInputStream(TEST_STDIN_MSG_1.getBytes());
        ourTestStdout = new ByteArrayOutputStream();
    }

    @AfterEach
    public void tearDown() throws IOException {
        ourTestStdin.close();
        ourTestStdout.close();
    }

    // Error test cases
    @Test
    void testWcCommandAndCutAsSubCommandWithErrorExceptionThrownShouldThrowCutException() {
        List<String> args = Arrays.asList("wc", "`cut -x 300 "+testFile1.toFile().getPath()+"`");
        Throwable thrown = assertThrows(CutException.class, () -> argumentResolver.parseArguments(args));
        assertEquals(thrown.getMessage(), CutApplication.COMMAND + ": " + ArgsParser.ILLEGAL_FLAG_MSG + "x");
    }

    // Positive test cases
    @Test
    void testEchoCommandAndCutAsSubCommandWithBlankOutputShouldParseArgumentsSuccessfully() throws AbstractApplicationException, ShellException {
        List<String> args = Arrays.asList("echo", "`cut -c 200 "+testFile1.toFile().getPath()+"`");
        List<String> expectedResult = Arrays.asList("echo");
        assertEquals(expectedResult, argumentResolver.parseArguments(args));
    }

    @Test
    void testLsCommandAndEchoAsSubCommandWithOneLineOutputShouldParseArgumentsSuccessfully() throws AbstractApplicationException, ShellException {
        List<String> args = Arrays.asList("ls", "-d", "`echo "+testFile1.toFile().getPath()+"`", "-R");
        List<String> expectedResult = Arrays.asList("ls", "-d", testFile1.toFile().getPath(), "-R");
        assertEquals(expectedResult, argumentResolver.parseArguments(args));
    }

    @Test
    void testWcCommandAndEchoAsSubCommandWithMultipleLineOutputShouldParseArgumentsSuccessfully() throws AbstractApplicationException, ShellException {
        List<String> args = Arrays.asList("wc", "`echo " + testFile1.toFile().getPath()  + " " + testFile2.toFile().getPath() + "`");
        List<String> expectedResult = Arrays.asList("wc", testFile1.toFile().getPath(), testFile2.toFile().getPath());
        assertEquals(expectedResult, argumentResolver.parseArguments(args));
    }

    @Test
    void testEchoCommandAndEchoAsSubCommandWithASingleQuoteShouldParseArgumentsSuccessfully() throws AbstractApplicationException, ShellException {
        List<String> args = Arrays.asList("echo", "`echo \'single quote is not interpreted as special character\'`");
        List<String> expectedResult = Arrays.asList("echo", "single", "quote", "is", "not" ,"interpreted", "as", "special" ,"character");
        assertEquals(expectedResult, argumentResolver.parseArguments(args));
    }

    @Test
    void testEchoCommandAndEchoAsSubCommandWithADoubleQuoteShouldParseArgumentsSuccessfully() throws AbstractApplicationException, ShellException {
        List<String> args = Arrays.asList("echo", "`echo \"single quote is not interpreted as special character\"`");
        List<String> expectedResult = Arrays.asList("echo", "single", "quote", "is", "not" ,"interpreted", "as", "special" ,"character");
        assertEquals(expectedResult, argumentResolver.parseArguments(args));
    }

    @Test
    void testEchoCommandAndEchoAsSubCommandWithABackQuoteInsideASingleQuoteShouldParseArgumentsSuccessfully() throws AbstractApplicationException, ShellException {
        List<String> args = Arrays.asList("echo", "\'`echo single quote is not interpreted as special character`\'");
        List<String> expectedResult = Arrays.asList("echo", "`echo single quote is not interpreted as special character`");
        assertEquals(expectedResult, argumentResolver.parseArguments(args));
    }

    @Test
    void testEchoCommandAndEchoAsSubCommandWithABackQuoteInsideADoubleQuoteShouldParseArgumentsSuccessfully() throws AbstractApplicationException, ShellException {
        List<String> args = Arrays.asList("echo", "\"`echo single quote is not interpreted as special character`\"");
        List<String> expectedResult = Arrays.asList("echo", "single quote is not interpreted as special character");
        assertEquals(expectedResult, argumentResolver.parseArguments(args));
    }

    @Test
    void testEchoCommandAndEchoAsSubCommandWithNumerousNonBackQuotesShouldParseArgumentsSuccessfully() throws AbstractApplicationException, ShellException {
        List<String> args = Arrays.asList("echo", "`echo `echo test``");
        List<String> expectedResult = Arrays.asList("echo", "echo test");
        assertEquals(expectedResult, argumentResolver.parseArguments(args));
    }

    @Test
    void testWcCommandAndEchoAndCutAsSubCommandUsingOnePipeOperatorShouldParseArgumentsSuccessfully() throws AbstractApplicationException, ShellException {
        List<String> args = Arrays.asList("wc", "-c", "`echo \"quote is not interpreted as special character\" | cut -c 5-15`");
        List<String> expectedResult = Arrays.asList("wc", "-c", "e", "is", "not" ,"in");
        assertEquals(expectedResult, argumentResolver.parseArguments(args));
    }

    @Test
    void testWcCommandAndLsAndGrepAndCutAsSubCommandUsingMultiplePipeOperatorShouldParseArgumentsSuccessfully() throws AbstractApplicationException, ShellException {
        List<String> args = Arrays.asList("wc", "-c", "`ls | grep s* | cut -c 1-3`");
        List<String> expectedResult = Arrays.asList("wc", "-c", "src");
        assertEquals(expectedResult, argumentResolver.parseArguments(args));
    }

    @Test
    void testEchoCommandAndEchoCommandsAsSubCommandWithMultipleSubCommandShouldParseArgumentsSuccessfully() throws AbstractApplicationException, ShellException {
        List<String> args = Arrays.asList("echo", "`echo abc`123`echo abc`123`echo abc`123");
        List<String> expectedResult = Arrays.asList("echo", "abc123", "abc123", "abc123");
        assertEquals(expectedResult, argumentResolver.parseArguments(args));
    }
}
