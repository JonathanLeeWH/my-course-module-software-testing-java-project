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
    private final Path testFile2 = Paths.get(TestFileUtils.TESTDATA_DIR + "test2.txt");
    private final Path testFile3 = Paths.get(TestFileUtils.TESTDATA_DIR + "test3.csv");

    @BeforeEach
    public void setUp() {
        cutApplication = new CutApplication();
        defaultCutArgs = Arrays.asList("-c", "8").toArray(new String[1]);
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
        assertEquals(thrown.getMessage(), CutApplication.COMMAND + ": " + ILLEGAL_FLAG_MSG + "x");
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
                Arrays.asList("-b", "-c", "6", testFile1.toFile().getPath()).toArray(new String[3]), ourTestStdin, ourTestStdout
        ));
        assertEquals(thrown.getMessage(), CutApplication.COMMAND + ": " + ERR_TOO_MANY_ARGS);
    }
    // Positive test cases
    @Test
    void testCutApplicationAndCutArgsParserUsingBytePosAndRangeNumWithFileNotSpecifiedShouldRunSuccessfully() throws CutException {
        List<String> args = Arrays.asList("-b", "3-5");
        cutApplication.run(args.toArray(new String[2]), ourTestStdin, ourTestStdout);
        String expectedResult = "üb" + System.lineSeparator();
        assertEquals(expectedResult, ourTestStdout.toString());
    }

    @Test
    void testCutApplicationAndCutArgsParserUsingCharPosAndRangeNumWithFileNotSpecifiedShouldRunSuccessfully() throws CutException {
        List<String> args = Arrays.asList("-c", "3-5");
        cutApplication.run(args.toArray(new String[2]), ourTestStdin, ourTestStdout);
        String expectedResult = "übe" + System.lineSeparator();
        assertEquals(expectedResult, ourTestStdout.toString());
    }

    @Test
    void testCutApplicationAndCutArgsParserUsingBytePosAndSingleNumWithFileNotSpecifiedShouldRunSuccessfully() throws CutException {
        List<String> args = Arrays.asList("-b", "1");
        cutApplication.run(args.toArray(new String[2]), ourTestStdin, ourTestStdout);
        String expectedResult = "d" + System.lineSeparator();
        assertEquals(expectedResult, ourTestStdout.toString());
    }

    @Test
    void testCutApplicationAndCutArgsParserUsingCharPosAndSingleNumWithFileNotSpecifiedShouldRunSuccessfully() throws CutException {
        List<String> args = Arrays.asList("-c", "5");
        cutApplication.run(args.toArray(new String[2]), ourTestStdin, ourTestStdout);
        String expectedResult = "e" + System.lineSeparator();
        assertEquals(expectedResult, ourTestStdout.toString());
    }

    @Test
    void testCutApplicationAndCutArgsParserUsingBytePosAndCommaSeparatedNumWithFileNotSpecifiedShouldRunSuccessfully() throws CutException {
        List<String> args = Arrays.asList("-b", "3,4");
        cutApplication.run(args.toArray(new String[2]), ourTestStdin, ourTestStdout);
        String expectedResult = "ü" + System.lineSeparator();
        assertEquals(expectedResult, ourTestStdout.toString());
    }

    @Test
    void testCutApplicationAndCutArgsParserUsingCharPosAndCommaSeparatedNumWithFileNotSpecifiedShouldRunSuccessfully() throws CutException {
        List<String> args = Arrays.asList("-c", "15,12");
        cutApplication.run(args.toArray(new String[2]), ourTestStdin, ourTestStdout);
        String expectedResult = "g" + System.lineSeparator();
        assertEquals(expectedResult, ourTestStdout.toString());
    }

    @Test
    void testCutApplicationAndCutArgsParserUsingBytePosAndRangeNumAndFileIsDashShouldRunSuccessfully() throws Exception {
        List<String> args = Arrays.asList("-b", "3-3", "-", "-");
        cutApplication.run(args.toArray(new String[3]), ourTestStdin, ourTestStdout);
        String expectedResult = "ü" + System.lineSeparator();
        assertEquals(expectedResult, ourTestStdout.toString());
    }

    @Test
    void testCutApplicationAndCutArgsParserUsingCharPosAndRangeNumAndFileIsDashShouldRunSuccessfully() throws Exception {
        List<String> args = Arrays.asList("-c", "14-14", "-");
        cutApplication.run(args.toArray(new String[3]), ourTestStdin, ourTestStdout);
        String expectedResult = "n" + System.lineSeparator();
        assertEquals(expectedResult, ourTestStdout.toString());
    }

    @Test
    void testCutApplicationAndCutArgsParserUsingBytePosAndSingleNumAndFileIsDashShouldRunSuccessfully() throws Exception {
        List<String> args = Arrays.asList("-b", "15", "-");
        cutApplication.run(args.toArray(new String[3]), ourTestStdin, ourTestStdout);
        String expectedResult = "n" + System.lineSeparator();
        assertEquals(expectedResult, ourTestStdout.toString());
    }

    @Test
    void testCutApplicationAndCutArgsParserUsingCharPosAndSingleNumAndFileIsDashShouldRunSuccessfully() throws Exception {
        List<String> args = Arrays.asList("-c", "6", "-", "-");
        cutApplication.run(args.toArray(new String[3]), ourTestStdin, ourTestStdout);
        String expectedResult = "r" + System.lineSeparator();
        assertEquals(expectedResult, ourTestStdout.toString());
    }

    @Test
    void testCutApplicationAndCutArgsParserUsingBytePosAndCommaSeparatedNumAndFileIsDashShouldRunSuccessfully() throws Exception {
        List<String> args = Arrays.asList("-b", "15,1", "-");
        cutApplication.run(args.toArray(new String[3]), ourTestStdin, ourTestStdout);
        String expectedResult = "dn" + System.lineSeparator();
        assertEquals(expectedResult, ourTestStdout.toString());
    }

    @Test
    void testCutApplicationAndCutArgsParserUsingCharPosAndCommaSeparatedNumAndFileIsDashShouldRunSuccessfully() throws Exception {
        List<String> args = Arrays.asList("-c", "2,16", "-");
        cutApplication.run(args.toArray(new String[3]), ourTestStdin, ourTestStdout);
        String expectedResult = "r" + System.lineSeparator();
        assertEquals(expectedResult, ourTestStdout.toString());
    }

    @Test
    void testCutApplicationAndCutArgsParserUsingBytePosAndRangeNumWithMultipleFilesShouldRunSuccessfully() throws Exception {
        List<String> args = Arrays.asList("-b", "10-19", testFile3.toFile().getPath(),
                testFile1.toFile().getPath(), testFile3.toFile().getPath(), "-", "-");
        cutApplication.run(args.toArray(new String[7]), ourTestStdin, ourTestStdout);
        String expectedResult = System.lineSeparator() + System.lineSeparator() + System.lineSeparator() +
                System.lineSeparator() + System.lineSeparator() + System.lineSeparator() + System.lineSeparator() +
                "oftware Te" + System.lineSeparator() + "ödülè cö" + System.lineSeparator() +
                "ssion test" + System.lineSeparator() + "e of error" + System.lineSeparator() + "ce predict" + System.lineSeparator() +
                "kills on t" + System.lineSeparator() + System.lineSeparator() + System.lineSeparator() + System.lineSeparator() +
                System.lineSeparator() + System.lineSeparator() + System.lineSeparator() + System.lineSeparator() + "ringen" + System.lineSeparator();
        assertEquals(expectedResult, ourTestStdout.toString());
    }

    @Test
    void testCutApplicationAndCutArgsParserUsingCutPosAndRangeNumWithMultipleFilesShouldRunSuccessfully() throws Exception {
        List<String> args = Arrays.asList("-c", "1-150", testFile3.toFile().getPath(),
                testFile1.toFile().getPath());
        cutApplication.run(args.toArray(new String[4]), ourTestStdin, ourTestStdout);
        String expectedResult = "1.0, 5.0" + System.lineSeparator() + "2, 3" + System.lineSeparator() + "51, 15" + System.lineSeparator() +
                "21, 4" + System.lineSeparator() + "22, 41" + System.lineSeparator() + "551, 1200" + System.lineSeparator() + "001, 010" + System.lineSeparator() +
                "CS4218: Software Testing" + System.lineSeparator() + "Thìš mödülè cövèrs thè concepts and prãctīće of software testing including unït testing, integration testing," + System.lineSeparator() +
                "and regression testing. Various testing coverage criteria will be discussed. Debugging methods for finding the" + System.lineSeparator() +
                "root-cause of errors in failing test cases will also be investigated. The use öf testing and analysis for" + System.lineSeparator() +
                "performance prediction, performance clustering and performance debugging will be studied. Students will acquire" + System.lineSeparator() +
                "crucial skills on testing and debugging through hands-on assignments." + System.lineSeparator();
        assertEquals(expectedResult, ourTestStdout.toString());
    }

    @Test
    void testCutApplicationAndCutArgsParserUsingBytePosAndSingleNumWithSingleFileShouldRunSuccessfully() throws CutException {
        List<String> args = Arrays.asList("-b", "2", testFile2.toFile().getPath());
        cutApplication.run(args.toArray(new String[3]), ourTestStdin, ourTestStdout);
        String expectedResult = "o" + System.lineSeparator() + System.lineSeparator() + "u" + System.lineSeparator() +
                System.lineSeparator() + "u" + System.lineSeparator();
        assertEquals(expectedResult, ourTestStdout.toString());
    }

    @Test
    void testCutApplicationAndCutArgsParserUsingCharPosAndSingleNumWithSingleFileShouldRunSuccessfully() throws CutException {
        List<String> args = Arrays.asList("-c", "6", testFile1.toFile().getPath());
        cutApplication.run(args.toArray(new String[3]), ourTestStdin, ourTestStdout);
        String expectedResult = "8" + System.lineSeparator() + "m" + System.lineSeparator() +
                "e" + System.lineSeparator() + "c" + System.lineSeparator() +
                "r" + System.lineSeparator() + "a" + System.lineSeparator();
        assertEquals(expectedResult, ourTestStdout.toString());
    }

    @Test
    void testCutApplicationAndCutArgsParserUsingBytePosAndCommaSeparatedNumWithMultipleFilesShouldRunSuccessfully() throws CutException {
        List<String> args = Arrays.asList("-b", "5,5", testFile2.toFile().getPath(), testFile1.toFile().getPath());
        cutApplication.run(args.toArray(new String[3]), ourTestStdin, ourTestStdout);
        String expectedResult = "m" + System.lineSeparator() + System.lineSeparator() + "m" + System.lineSeparator() + System.lineSeparator() +
                "i" + System.lineSeparator() + "1" + System.lineSeparator() + "š" + System.lineSeparator() +
                "r" + System.lineSeparator() + "-" + System.lineSeparator() + "o" + System.lineSeparator() + "i" + System.lineSeparator();
        assertEquals(expectedResult, ourTestStdout.toString());
    }

    @Test
    void testCutApplicationAndCutArgsParserUsingCharPosAndCommaSeparatedNumWithMultipleFilesShouldRunSuccessfully() throws CutException {
        List<String> args = Arrays.asList("-c", "6,14", testFile1.toFile().getPath(), testFile2.toFile().getPath());
        cutApplication.run(args.toArray(new String[3]), ourTestStdin, ourTestStdout);
        String expectedResult = "8a" + System.lineSeparator() + "mö" + System.lineSeparator() + "en" + System.lineSeparator() + "c " + System.lineSeparator() +
                "rr" + System.lineSeparator() + "as" + System.lineSeparator() + " o" + System.lineSeparator() +
                System.lineSeparator() + "ov" + System.lineSeparator() + System.lineSeparator() + "st" + System.lineSeparator();
        assertEquals(expectedResult, ourTestStdout.toString());
    }

    @Test
    void testCutApplicationAndCutArgsParserUsingBytePosAndRangeNumAndInputStreamWithEmptyValuesShouldRunSuccessfully() throws Exception {
        InputStream emptyInputStream = new ByteArrayInputStream(new byte[0]);
        List<String> args = Arrays.asList("-b", "4-4");
        cutApplication.run(args.toArray(new String[2]), emptyInputStream, ourTestStdout);
        String expectedResult = "";
        assertEquals(expectedResult, ourTestStdout.toString());
    }

    @Test
    void testCutApplicationAndCutArgsParserUsingCutPosAndRangeNumAndInputStreamWithEmptyValuesShouldRunSuccessfully() throws Exception {
        InputStream emptyInputStream = new ByteArrayInputStream(new byte[0]);
        List<String> args = Arrays.asList("-c", "1-2");
        cutApplication.run(args.toArray(new String[2]), emptyInputStream, ourTestStdout);
        String expectedResult = "";
        assertEquals(expectedResult, ourTestStdout.toString());
    }

    @Test
    void testCutApplicationAndCutArgsParserUsingBytePosAndSingleNumAndInputStreamWithEmptyValuesShouldRunSuccessfully() throws Exception {
        InputStream emptyInputStream = new ByteArrayInputStream(new byte[0]);
        List<String> args = Arrays.asList("-b", "7");
        cutApplication.run(args.toArray(new String[2]), emptyInputStream, ourTestStdout);
        String expectedResult = "";
        assertEquals(expectedResult, ourTestStdout.toString());
    }

    @Test
    void testCutApplicationAndCutArgsParserUsingCutPosAndSingleNumAndInputStreamWithEmptyValuesShouldRunSuccessfully() throws Exception {
        InputStream emptyInputStream = new ByteArrayInputStream(new byte[0]);
        List<String> args = Arrays.asList("-c", "12");
        cutApplication.run(args.toArray(new String[2]), emptyInputStream, ourTestStdout);
        String expectedResult = "";
        assertEquals(expectedResult, ourTestStdout.toString());
    }


    @Test
    void testCutApplicationAndCutArgsParserUsingBytePosAndCommaSeparatedNumAndInputStreamWithEmptyValuesShouldRunSuccessfully() throws Exception {
        InputStream emptyInputStream = new ByteArrayInputStream(new byte[0]);
        List<String> args = Arrays.asList("-b", "1,6");
        cutApplication.run(args.toArray(new String[2]), emptyInputStream, ourTestStdout);
        String expectedResult = "";
        assertEquals(expectedResult, ourTestStdout.toString());
    }

    @Test
    void testCutApplicationAndCutArgsParserUsingCutPosAndCommaSeparatedNumAndInputStreamWithEmptyValuesShouldRunSuccessfully() throws Exception {
        InputStream emptyInputStream = new ByteArrayInputStream(new byte[0]);
        List<String> args = Arrays.asList("-c", "12,14");
        cutApplication.run(args.toArray(new String[2]), emptyInputStream, ourTestStdout);
        String expectedResult = "";
        assertEquals(expectedResult, ourTestStdout.toString());
    }
}
