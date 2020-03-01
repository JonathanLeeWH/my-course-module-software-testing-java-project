package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.exception.CutException;
import sg.edu.nus.comp.cs4218.impl.util.TestFileUtils;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static sg.edu.nus.comp.cs4218.impl.parser.ArgsParser.ILLEGAL_FLAG_MSG;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_TOO_MANY_ARGS;

public class CutApplicationIT {
    private CutApplication cutApplication;
    private String[] defaultCutArgs;
    private InputStream ourTestStdin;
    private OutputStream ourTestStdout;
    private static final String TEST_STDIN_MSG_1 = "drüberspringen";
    private final Path testFile1 = Paths.get(TestFileUtils.TESTDATA_DIR + "test1.txt");
    private final Path testFile3 = Paths.get(TestFileUtils.TESTDATA_DIR + "test3.csv");

    @BeforeEach
    public void setUp() {
        cutApplication = new CutApplication();
        defaultCutArgs = Arrays.asList("-c","8").toArray(new String[1]);
        ourTestStdin = new ByteArrayInputStream(TEST_STDIN_MSG_1.getBytes());
        ourTestStdout = new ByteArrayOutputStream();
    }

    @AfterEach
    public void tearDown() throws IOException {
        ourTestStdin.close();
        ourTestStdout.close();
    }

    /**
     * Test cut application with run().
     */

    // Error Test cases
    @Test
    void testCutApplicationAndCutArgsParserWithNullArgsShouldThrowCutException() {
        Throwable thrown = assertThrows(CutException.class, () -> cutApplication.run(null, ourTestStdin, ourTestStdout));
        assertEquals(thrown.getMessage(), CutApplication.COMMAND + ": " + ERR_NULL_ARGS);
    }

    @Test
    void testCutApplicationAndCutArgsParserWithNullOutputStreamShouldThrowCutException() {
        Throwable thrown = assertThrows(CutException.class, () -> cutApplication.run(defaultCutArgs, ourTestStdin, null));
        assertEquals(thrown.getMessage(), CutApplication.COMMAND + ": " + ERR_NO_OSTREAM);
    }

    @Test
    void testCutApplicationAndCutArgsParserWithInvalidFlagArgumentShouldThrowCutException() {
        Throwable thrown = assertThrows(CutException.class, () -> cutApplication.run(
                Arrays.asList("-x", "6", testFile1.toFile().getPath()).toArray(new String[3]), ourTestStdin, ourTestStdout
        ));
        assertEquals(thrown.getMessage(), CutApplication.COMMAND + ": "  + ILLEGAL_FLAG_MSG + "x");
    }

    @Test
    void testCutApplicationAndCutArgsParserUsingWithoutByteAndCharPosShouldThrowCutException() {
        Throwable thrown = assertThrows(CutException.class, () -> cutApplication.run(
                Arrays.asList("6", testFile1.toFile().getPath()).toArray(new String[2]), ourTestStdin, ourTestStdout
        ));
        assertEquals(thrown.getMessage(), CutApplication.COMMAND + ": " + ERR_MISSING_ARG);
    }

    @Test
    void testCutApplicationAndCutArgsParserWithByteAndCharPosShouldThrowCutException() {
        Throwable thrown = assertThrows(CutException.class, () -> cutApplication.run(
                Arrays.asList("-b","-c", "6", testFile1.toFile().getPath()).toArray(new String[3]), ourTestStdin, ourTestStdout
        ));
        assertEquals(thrown.getMessage(), CutApplication.COMMAND + ": " + ERR_TOO_MANY_ARGS);
    }

    // Positive test cases
    @Test
    void testCutApplicationAndCutArgsParserUsingStdinShouldRunSuccessfully() throws CutException {
        List<String> args = Arrays.asList("-b", "1");
        cutApplication.run(args.toArray(new String[0]), ourTestStdin, ourTestStdout);
        String expectedResult = "d" + System.lineSeparator();
        assertEquals(expectedResult, ourTestStdout.toString());
    }

    @Test
    void testCutApplicationAndCutArgsParserUsingFilesShouldRunSuccessfully() throws CutException {
        List<String> args = Arrays.asList("-c", "6", testFile1.toFile().getPath());
        cutApplication.run(args.toArray(new String[0]), ourTestStdin, ourTestStdout);
        String expectedResult = "8" + System.lineSeparator() + "m" + System.lineSeparator() +
                "e" + System.lineSeparator() + "c" + System.lineSeparator() +
                "r" + System.lineSeparator() + "a" + System.lineSeparator();
        assertEquals(expectedResult, ourTestStdout.toString());
    }

    @Test
    void testCutApplicationAndCutArgsParserUsingBytePosAndSingleNumAndFileIsDashShouldRunSuccessfully() throws Exception {
        List<String> args = Arrays.asList("-b", "15", "-");
        cutApplication.run(args.toArray(new String[0]), ourTestStdin, ourTestStdout);
        String expectedResult = "n" + System.lineSeparator();
        assertEquals(expectedResult, ourTestStdout.toString());
    }

    @Test
    void testCutApplicationAndCutArgsParserUsingBytePosAndNumRangeAndHavingDashBetweenMultipleFilesWithStartNumLowerThanEndNumShouldRunSuccessfully() throws Exception {
        List<String> args = Arrays.asList("-b", "10-19", testFile3.toFile().getPath(),
                testFile1.toFile().getPath(), testFile3.toFile().getPath(), "-", "-");
        cutApplication.run(args.toArray(new String[0]), ourTestStdin, ourTestStdout);
        String expectedResult = System.lineSeparator() + System.lineSeparator() + System.lineSeparator() +
                System.lineSeparator() + System.lineSeparator() + System.lineSeparator() + System.lineSeparator() +
                "oftware Te" + System.lineSeparator() + "ödülè cö" + System.lineSeparator() +
                "ssion test" + System.lineSeparator() + "e of error" + System.lineSeparator() + "ce predict" + System.lineSeparator() +
                "kills on t" + System.lineSeparator() + System.lineSeparator() + System.lineSeparator() + System.lineSeparator() +
                System.lineSeparator() + System.lineSeparator() + System.lineSeparator() + System.lineSeparator() + "ringen" + System.lineSeparator();
        assertEquals(expectedResult, ourTestStdout.toString());
    }
}
