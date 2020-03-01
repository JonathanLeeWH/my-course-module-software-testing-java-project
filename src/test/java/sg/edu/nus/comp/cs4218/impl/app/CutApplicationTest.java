package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.*;
import sg.edu.nus.comp.cs4218.app.CutInterface;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.CutException;
import sg.edu.nus.comp.cs4218.impl.util.TestFileUtils;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static sg.edu.nus.comp.cs4218.impl.parser.ArgsParser.ILLEGAL_FLAG_MSG;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;

@SuppressWarnings({"PMD.AvoidDuplicateLiterals"})
class CutApplicationTest {
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
    void testRunWithNullArgsShouldThrowCutException() {
       Throwable thrown = assertThrows(CutException.class, () -> cutApplication.run(null, ourTestStdin, ourTestStdout));
       assertEquals(thrown.getMessage(), CutApplication.COMMAND + ": " + ERR_NULL_ARGS);
    }

    @Test
    void testRunWithNullOutputStreamShouldThrowCutException() {
        Throwable thrown = assertThrows(CutException.class, () -> cutApplication.run(defaultCutArgs, ourTestStdin, null));
        assertEquals(thrown.getMessage(), CutApplication.COMMAND + ": " + ERR_NO_OSTREAM);
    }

    @Test
    void testRunWithInvalidFlagArgumentShouldThrowCutException() {
        Throwable thrown = assertThrows(CutException.class, () -> cutApplication.run(
                Arrays.asList("-x", "6", testFile1.toFile().getPath()).toArray(new String[3]), ourTestStdin, ourTestStdout
        ));
        assertEquals(thrown.getMessage(), CutApplication.COMMAND + ": "  + ILLEGAL_FLAG_MSG + "x");
    }

    @Test
    void testRunUsingWithoutByteAndCharPosShouldThrowCutException() {
        Throwable thrown = assertThrows(CutException.class, () -> cutApplication.run(
                Arrays.asList("6", testFile1.toFile().getPath()).toArray(new String[2]), ourTestStdin, ourTestStdout
        ));
        assertEquals(thrown.getMessage(), CutApplication.COMMAND + ": " + ERR_MISSING_ARG);
    }

    @Test
    void testRunWithByteAndCharPosShouldThrowCutException() {
        Throwable thrown = assertThrows(CutException.class, () -> cutApplication.run(
                Arrays.asList("-b","-c", "6", testFile1.toFile().getPath()).toArray(new String[3]), ourTestStdin, ourTestStdout
        ));
        assertEquals(thrown.getMessage(), CutApplication.COMMAND + ": " + ERR_TOO_MANY_ARGS);
    }

    // Positive test cases
    @Test
    void testRunUsingStdinShouldRunSuccessfully() throws CutException {
        List<String> args = Arrays.asList("-b", "1");
        cutApplication.run(args.toArray(new String[0]), ourTestStdin, ourTestStdout);
        String expectedResult = "d" + System.lineSeparator();
        assertEquals(expectedResult, ourTestStdout.toString());
    }

    @Test
    void testRunUsingFilesShouldRunSuccessfully() throws CutException {
        List<String> args = Arrays.asList("-c", "6", testFile1.toFile().getPath());
        cutApplication.run(args.toArray(new String[0]), ourTestStdin, ourTestStdout);
        String expectedResult = "8" + System.lineSeparator() + "m" + System.lineSeparator() +
                "e" + System.lineSeparator() + "c" + System.lineSeparator() +
                "r" + System.lineSeparator() + "a" + System.lineSeparator();
        assertEquals(expectedResult, ourTestStdout.toString());
    }

    @Test
    void testRunUsingBytePosAndSingleNumAndFileIsDashShouldRunSuccessfully() throws Exception {
        List<String> args = Arrays.asList("-b", "15", "-");
        cutApplication.run(args.toArray(new String[0]), ourTestStdin, ourTestStdout);
        String expectedResult = "n" + System.lineSeparator();
        assertEquals(expectedResult, ourTestStdout.toString());
    }

    @Test
    void testRunUsingBytePosAndNumRangeAndHavingDashBetweenMultipleFilesWithStartNumLowerThanEndNumShouldRunSuccessfully() throws Exception {
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

    /**
     * Test cut application using cutFromFiles()
     */
    // Error Test cases
    @Test
    void testCutFromFilesUsingBytePosAnd2CommaSeparatedNumAndASingleFileWithStartNumLessThanZeroShouldThrowCutException() {
        Throwable thrown = assertThrows(Exception.class, () -> cutApplication.cutFromFiles(
                false, true, false, -2, 5, testFile2.toFile().getPath()
        ));
        assertEquals(thrown.getMessage(), ERR_LESS_THAN_ZERO);
    }

    @Test
    void testCutFromFilesUsingCharPosAnd2CommaSeparatedNumAndASingleFileWithEndNumLessThanZeroShouldThrowCutException() {
        Throwable thrown = assertThrows(Exception.class, () -> cutApplication.cutFromFiles(
                true, false, false, 12, 0, testFile1.toFile().getPath()
        ));
        assertEquals(thrown.getMessage(), ERR_LESS_THAN_ZERO);
    }

    @Test
    void testCutFromFilesUsingBytePosAndSingleNumAndASingleFileWithNumLessThanZeroShouldThrowCutException() {
        Throwable thrown = assertThrows(Exception.class, () -> cutApplication.cutFromFiles(
                false, true, false, (int) -12.44, 15, testFile1.toFile().getPath()
        ));
        assertEquals(thrown.getMessage(), ERR_LESS_THAN_ZERO);
    }

    @Test
    void testCutFromFilesUsingCharPosAndNumRangeAndASingleFileWithFileNotFoundInDirShouldThrowCutException() {
        Throwable thrown = assertThrows(Exception.class, () -> cutApplication.cutFromFiles(
                true, false, false, (int) 12.44, 15, "noFile.txt"
        ));
        assertEquals(thrown.getMessage(), ERR_FILE_NOT_FOUND);
    }

    @Test
    void testCutFromFilesUsingBytePosAndNumRangeAndMultipleFilesWithAtLeastOneFileNotFoundInDirShouldThrowCutException() {
        Throwable thrown = assertThrows(Exception.class, () -> cutApplication.cutFromFiles(
                false, true, false, (int) 12.44, 15, testFile1.toFile().getPath(),
                "no-File.txt"
        ));
        assertEquals(thrown.getMessage(), ERR_FILE_NOT_FOUND);
    }

    @Test
    void testCutFromFilesUsingBytePosAndSingleNumAndNullFilenameShouldThrowCutException() {
        Throwable thrown = assertThrows(Exception.class, () -> cutApplication.cutFromFiles(
                false, true, false, 1, 2, (String[]) null)
        );
        assertEquals(thrown.getMessage(), ERR_GENERAL);
    }

    @Test
    void testCutFromFilesUsingCharPosAndSingleNumAndFileNameWhereFileHasNoReadAccessShouldThrowCutException() {
        CutApplicationStubWithFileHasNoReadAccess cutApplicationStub = new CutApplicationStubWithFileHasNoReadAccess();
        Throwable thrown = assertThrows(Exception.class, () -> cutApplicationStub.cutFromFiles(
                true, false, false, 1, 1, testFile1.toFile().getPath()
        ));
        assertEquals(thrown.getMessage(), ERR_NO_PERM);
    }

    @Test
    void testCutFromFilesUsingCharPosAndSingleNumAndFileNameWhereFilenameIsADirShouldThrowCutException() {
        Throwable thrown = assertThrows(Exception.class, () -> cutApplication.cutFromFiles(
                true, false, false, 1, 1, TestFileUtils.TESTDATA_DIR
        ));
        assertEquals(thrown.getMessage(), ERR_IS_DIR);
    }

    // Positive test cases
    @Test
    void testCutFromFilesUsingBytePosAnd2CommaSeparatedNumAndValidSingleFileWithStartNumLowerThanEndNumShouldRunSuccessfully() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                false, true, false, 6, 13, testFile1.toFile().getPath()
        );
        String expectedResult = "8w" + System.lineSeparator() + "šü" + System.lineSeparator() +
                "eo" + System.lineSeparator() + "cf" + System.lineSeparator() + "rp" + System.lineSeparator() + "al";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingBytePosAnd2CommaSeparatedNumAndValidSingleFileWithStartNumHigherThanEndNumShouldRunSuccessfully() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                false, true, false, 13, 6, testFile1.toFile().getPath()
        );
        String expectedResult = "8w" + System.lineSeparator() + "šü" + System.lineSeparator() +
                "eo" + System.lineSeparator() + "cf" + System.lineSeparator() +
                "rp" + System.lineSeparator() + "al";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingBytePosAnd2CommaSeparatedNumAndValidSingleFileWithStartNumHigherThanAnyNumLinesInFileShouldRunSuccessfully() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                false, true, false, Integer.MAX_VALUE, 2 , testFile1.toFile().getPath()
        );
        String expectedResult = "S" + System.lineSeparator() + "h" + System.lineSeparator() + "n" +
                System.lineSeparator() + "o" + System.lineSeparator() + "e" + System.lineSeparator() + "r";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingBytePosAnd2CommaSeparatedNumAndValidSingleFileWithEndNumHigherThanAnyNumLinesInFileShouldRunSuccessfully() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                false, true, false, 2, Integer.MAX_VALUE, testFile1.toFile().getPath()
        );
        String expectedResult = "S" + System.lineSeparator() + "h" + System.lineSeparator() + "n" +
                System.lineSeparator() + "o" + System.lineSeparator() + "e" + System.lineSeparator() + "r";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingBytePosAndNumRangeAndValidSingleFileWithStartNumLowerThanEndNumShouldRunSuccessfully() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                false, true, true, 6, 12, testFile1.toFile().getPath()
        );
        String expectedResult = "8: Soft" + System.lineSeparator() + "š mödü" + System.lineSeparator() +
                "egressi" + System.lineSeparator() + "cause o" + System.lineSeparator() +
                "rmance " + System.lineSeparator() + "al skil";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingBytePosAndNumRangeAndValidSingleFileWithStartNumHigherThanEndNumShouldRunSuccessfully() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                false, true, true, 12, 5, testFile1.toFile().getPath()
        );
        String expectedResult = System.lineSeparator() + System.lineSeparator() + System.lineSeparator() +
                System.lineSeparator() + System.lineSeparator();
        assertEquals(expectedResult, actualResult);
    }


    @Test
    void testCutFromFilesUsingBytePosAndNumRangeAndValidSingleFileWithStartNumHigherThanAnyNumLinesInFileShouldRunSuccessfully() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                false, true, true, Integer.MAX_VALUE, 715, testFile2.toFile().getPath()
        );
        String expectedResult = System.lineSeparator() + System.lineSeparator() + System.lineSeparator() +
                System.lineSeparator();
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingBytePosAndNumRangeAndValidSingleFileWithEndNumHigherThanAnyNumLinesInFileShouldRunSuccessfully() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                false, true, true, 600, Integer.MAX_VALUE, testFile2.toFile().getPath()
        );
        String expectedResult = "ique. Feugiat pretium nibh ipsum consequat nisl." + System.lineSeparator() + System.lineSeparator() +
                "orta non. Maecenas sed enim ut sem viverra aliquet eget sit amet. In egestas erat imperdiet sed euismod nisi porta lorem. Amet volutpat consequat mauris nunc congue. Sodales ut etiam sit amet." +
                System.lineSeparator() + System.lineSeparator() +
                "rttitor eget dolor morbi non arcu.";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingBytePosAndSingleNumAndValidSingleFileShouldRunSuccessfully() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                false, true, false, 5, 5, testFile1.toFile().getPath()
        );
        String expectedResult = "1" + System.lineSeparator() + "š" + System.lineSeparator() + "r" + System.lineSeparator() +
                "-" + System.lineSeparator() + "o" + System.lineSeparator() + "i";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingBytePosAndSingleNumAndValidSingleFileWithIndexHigherThanAnyNumLinesInFileShouldRunSuccessfully() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                false, true, false, Integer.MAX_VALUE, Integer.MAX_VALUE, testFile1.toFile().getPath()
        );
        String expectedResult = System.lineSeparator() + System.lineSeparator() + System.lineSeparator() +
                System.lineSeparator() + System.lineSeparator();
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingCharPosAnd2CommaSeparatedNumAndValidSingleFileWithStartNumLowerThanEndNumShouldRunSuccessfully() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                true, false, false, 2, 29, testFile1.toFile().getPath()
        );
        String expectedResult = "S" + System.lineSeparator() + "hp" + System.lineSeparator() + "no" + System.lineSeparator() +
                "oi" + System.lineSeparator() + "eo" + System.lineSeparator() + "rd";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingCharPosAnd2CommaSeparatedNumAndValidSingleFileWithStartNumHigherThanEndNumShouldRunSuccessfully() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                true, false, false, 56, 18, testFile1.toFile().getPath()
        );
        String expectedResult = "T" + System.lineSeparator() + "se" + System.lineSeparator() + "si" +
                System.lineSeparator() + "o " + System.lineSeparator() + "co" + System.lineSeparator() + " n";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingCharPosAnd2CommaSeparatedNumAndValidSingleFileWithStartNumHigherThanAnyNumLinesInFileShouldRunSuccessfully() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                true, false, false, Integer.MAX_VALUE, 76, testFile2.toFile().getPath()
        );
        String expectedResult = "p" + System.lineSeparator() + System.lineSeparator() + "e" + System.lineSeparator() +
                System.lineSeparator() + "u";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingCharPosAnd2CommaSeparatedNumAndValidSingleFileWithEndNumHigherThanAnyNumLinesInFileShouldRunSuccessfully() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                true, false, false, 81, Integer.MAX_VALUE, testFile2.toFile().getPath()
        );
        String expectedResult = "n" + System.lineSeparator() + System.lineSeparator() + "t" +
                System.lineSeparator() + System.lineSeparator() + " ";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingCharPosAndNumRangeAndValidSingleFileWithStartNumLowerThanEndNumShouldRunSuccessfully() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                true, false, true, 3, 63, testFile1.toFile().getPath()
        );
        String expectedResult = "4218: Software Testing" + System.lineSeparator() +
                "ìš mödülè cövèrs thè concepts and prãctīće of software testin" + System.lineSeparator() +
                "d regression testing. Various testing coverage criteria will " + System.lineSeparator() +
                "ot-cause of errors in failing test cases will also be investi" + System.lineSeparator() +
                "rformance prediction, performance clustering and performance " + System.lineSeparator() +
                "ucial skills on testing and debugging through hands-on assign";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingCharPosAndNumRangeAndValidSingleFileWithStartNumHigherThanEndNumShouldRunSuccessfully() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                true, false, true, 18, 17, testFile1.toFile().getPath()
        );
        String expectedResult = System.lineSeparator() + System.lineSeparator() + System.lineSeparator() +
                System.lineSeparator() + System.lineSeparator();
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingCharPosAndNumRangeAndValidSingleFileWithStartNumHigherThanAnyNumLinesInFileShouldRunSuccessfully() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                true, false, true, Integer.MAX_VALUE, 17, testFile3.toFile().getPath()
        );
        String expectedResult = System.lineSeparator() + System.lineSeparator() + System.lineSeparator() +
                System.lineSeparator() + System.lineSeparator() + System.lineSeparator();
        assertEquals(expectedResult, actualResult);
    }


    @Test
    void testCutFromFilesUsingCharPosAndNumRangeAndValidSingleFileWithEndNumHigherThanAnyNumLinesInFileShouldRunSuccessfully() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                true, false, true, 18, Integer.MAX_VALUE, testFile3.toFile().getPath()
        );
        String expectedResult = System.lineSeparator() + System.lineSeparator() + System.lineSeparator() +
                System.lineSeparator() + System.lineSeparator() + System.lineSeparator();
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingCharPosAndSingleNumAndValidSingleFileShouldRunSuccessfully() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                true, false, false, 103, 103, testFile1.toFile().getPath()
        );
        String expectedResult = System.lineSeparator() + "e" + System.lineSeparator() + "d" + System.lineSeparator() +
                "f" + System.lineSeparator() + "l" + System.lineSeparator();
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingCharPosAndSingleNumAndValidSingleFileWithIndexHigherThanAnyNumLinesInFileShouldRunSuccessfully() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                true, false, false, Integer.MAX_VALUE, Integer.MAX_VALUE, testFile1.toFile().getPath()
        );
        String expectedResult = System.lineSeparator() + System.lineSeparator() + System.lineSeparator() +
                System.lineSeparator() + System.lineSeparator();
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingBytePosAnd2CommaSeparatedNumAndValidDistinctFilesWithStartNumLowerThanEndNumShouldRunSuccessfully() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                false, true, false, 10, 12,
                testFile1.toFile().getPath(), testFile2.toFile().getPath()
        );
        String expectedResult = "ot" + System.lineSeparator() + "öü" + System.lineSeparator() + "si" + System.lineSeparator() +
                "eo" + System.lineSeparator() + "c " + System.lineSeparator() + "kl" + System.lineSeparator() + "u " +
                System.lineSeparator() + System.lineSeparator() + "us" + System.lineSeparator() + System.lineSeparator() + "sa";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingBytePosAnd2CommaSeparatedNumAndValidDistinctFilesWithStartNumHigherThanEndNumShouldRunSuccessfully() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                false, true, false, 115, 1,
                testFile1.toFile().getPath(), testFile2.toFile().getPath(), testFile3.toFile().getPath()
        );
        String expectedResult = "C" + System.lineSeparator() + "Te" + System.lineSeparator() + "a" + System.lineSeparator() +
                "r" + System.lineSeparator() + "p" + System.lineSeparator() + "c" + System.lineSeparator() +
                "La" + System.lineSeparator() + System.lineSeparator() + "Et" + System.lineSeparator() + System.lineSeparator() +
                "Tt" + System.lineSeparator() + "1" + System.lineSeparator() + "2" + System.lineSeparator() +
                "5" + System.lineSeparator() + "2" + System.lineSeparator() + "2" + System.lineSeparator() +
                "5" + System.lineSeparator() + "0";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingBytePosAnd2CommaSeparatedNumAndValidDistinctFilesWithStartNumHigherThanAnyNumLinesInFilesShouldRunSuccessfully() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                false, true, false, Integer.MAX_VALUE, 8,
                testFile1.toFile().getPath(), testFile2.toFile().getPath(), testFile3.toFile().getPath()
        );
        String expectedResult = " " + System.lineSeparator() + "m" + System.lineSeparator() + "r" + System.lineSeparator() +
                "u" + System.lineSeparator() + "a" + System.lineSeparator() + " " + System.lineSeparator() + "p" +
                System.lineSeparator() + System.lineSeparator() + " " + System.lineSeparator() + System.lineSeparator() + "m" +
                System.lineSeparator() + "0" + System.lineSeparator() + System.lineSeparator() + System.lineSeparator() +
                System.lineSeparator() + System.lineSeparator() + "0" + System.lineSeparator() + "0";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingBytePosAnd2CommaSeparatedNumAndValidDistinctFilesWithEndNumHigherThanAnyNumLinesInFilesShouldRunSuccessfully() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                false, true, false, 8, Integer.MAX_VALUE,
                testFile1.toFile().getPath(), testFile2.toFile().getPath(), testFile3.toFile().getPath()
        );
        String expectedResult = " " + System.lineSeparator() + "m" + System.lineSeparator() + "r" + System.lineSeparator() +
                "u" + System.lineSeparator() + "a" + System.lineSeparator() + " " + System.lineSeparator() +
                "p" + System.lineSeparator() + System.lineSeparator() + " " + System.lineSeparator() + System.lineSeparator() +
                "m" + System.lineSeparator() + "0" + System.lineSeparator() + System.lineSeparator() + System.lineSeparator() +
                System.lineSeparator() + System.lineSeparator() + "0" + System.lineSeparator() + "0";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingBytePosAndNumRangeAndValidDistinctFilesWithStartNumLowerThanEndNumShouldRunSuccessfully() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                false, true, true, 1, 11,
                testFile1.toFile().getPath(), testFile2.toFile().getPath(), testFile3.toFile().getPath()
        );
        String expectedResult = "CS4218: Sof" + System.lineSeparator() + "Thìš möd" + System.lineSeparator() + "and regress" +
                System.lineSeparator() + "root-cause " + System.lineSeparator() + "performance" + System.lineSeparator() +
                "crucial ski" + System.lineSeparator() + "Lorem ipsum" + System.lineSeparator() + System.lineSeparator() +
                "Euismod qui" + System.lineSeparator() + System.lineSeparator() + "Turpis mass" + System.lineSeparator() +
                "1.0, 5.0" + System.lineSeparator() + "2, 3" + System.lineSeparator() + "51, 15" + System.lineSeparator() +
                "21, 4" + System.lineSeparator() + "22, 41" + System.lineSeparator() + "551, 1200" + System.lineSeparator() + "001, 010";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingBytePosAndNumRangeAndValidDistinctFilesWithStartNumHigherThanEndNumShouldRunSuccessfully() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                false, true, true, 9, 1,
                testFile1.toFile().getPath(), testFile3.toFile().getPath()
        );
        String expectedResult = System.lineSeparator() + System.lineSeparator() + System.lineSeparator() + System.lineSeparator() +
                System.lineSeparator() + System.lineSeparator() + System.lineSeparator() + System.lineSeparator() +
                System.lineSeparator() + System.lineSeparator() + System.lineSeparator() + System.lineSeparator();
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingBytePosAndNumRangeAndValidDistinctFilesWithStartNumHigherThanAnyNumLinesInFilesShouldRunSuccessfully() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                false, true, true, Integer.MAX_VALUE, 1,
                testFile2.toFile().getPath(), testFile3.toFile().getPath()
        );
        String expectedResult = System.lineSeparator() + System.lineSeparator() + System.lineSeparator() + System.lineSeparator() +
                System.lineSeparator() + System.lineSeparator() + System.lineSeparator() + System.lineSeparator() +
                System.lineSeparator() + System.lineSeparator() + System.lineSeparator();
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingBytePosAndNumRangeAndValidDistinctFilesWithEndNumHigherThanAnyNumLinesInFilesShouldRunSuccessfully() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                false, true, true, 9, Integer.MAX_VALUE,
                testFile1.toFile().getPath(), testFile2.toFile().getPath()
        );
        String expectedResult = "Software Testing" + System.lineSeparator() +
                "ödülè cövèrs thè concepts and prãctīće of software testing including unït testing, integration testing," + System.lineSeparator() +
                "ession testing. Various testing coverage criteria will be discussed. Debugging methods for finding the" + System.lineSeparator() +
                "se of errors in failing test cases will also be investigated. The use öf testing and analysis for" + System.lineSeparator() +
                "nce prediction, performance clustering and performance debugging will be studied. Students will acquire" + System.lineSeparator() +
                "skills on testing and debugging through hands-on assignments." + System.lineSeparator() +
                "sum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Aliquam malesuada bibendum arcu vitae. Nam libero justo laoreet sit amet cursus sit amet. Egestas tellus rutrum tellus pellentesque eu. Proin nibh nisl condimentum id venenatis a condimentum. Magna etiam tempor orci eu lobortis. Vel facilisis volutpat est velit egestas dui. Sed viverra ipsum nunc aliquet bibendum enim facilisis gravida. Id aliquet risus feugiat in ante. Tincidunt augue interdum velit euismod in pellentesque. Vitae sapien pellentesque habitant morbi tristique. Feugiat pretium nibh ipsum consequat nisl." +
                System.lineSeparator() + System.lineSeparator() +
                "quis viverra nibh cras pulvinar mattis nunc. Nam libero justo laoreet sit amet cursus sit amet dictum. Auctor augue mauris augue neque gravida in fermentum et. Nunc eget lorem dolor sed viverra ipsum nunc aliquet. Mauris nunc congue nisi vitae. Sed adipiscing diam donec adipiscing. Luctus venenatis lectus magna fringilla. Quis auctor elit sed vulputate mi sit. Elit at imperdiet dui accumsan sit amet nulla facilisi. Semper viverra nam libero justo laoreet sit amet cursus sit. Sit amet commodo nulla facilisi nullam vehicula ipsum a arcu. Volutpat sed cras ornare arcu dui. Leo vel orci porta non. Maecenas sed enim ut sem viverra aliquet eget sit amet. In egestas erat imperdiet sed euismod nisi porta lorem. Amet volutpat consequat mauris nunc congue. Sodales ut etiam sit amet." +
                System.lineSeparator() + System.lineSeparator() +
                "assa tincidunt dui ut ornare lectus sit. Phasellus egestas tellus rutrum tellus pellentesque eu tincidunt tortor aliquam. Est velit egestas dui id ornare arcu odio ut sem. Facilisi nullam vehicula ipsum a. Et netus et malesuada fames ac turpis egestas. Euismod lacinia at quis risus sed vulputate odio. Placerat orci nulla pellentesque dignissim enim sit. Metus aliquam eleifend mi in nulla posuere. Amet venenatis urna cursus eget. Elit sed vulputate mi sit. Lorem ipsum dolor sit amet consectetur adipiscing elit duis. Curabitur gravida arcu ac tortor dignissim. A pellentesque sit amet porttitor eget dolor morbi non arcu.";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingBytePosAndSingleNumAndValidDistinctFilesShouldRunSuccessfully() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                false, true, false, 9, 9,
                testFile1.toFile().getPath(), testFile3.toFile().getPath()
        );
        String expectedResult = "S" + System.lineSeparator() + "ö" + System.lineSeparator() + "e" +
                System.lineSeparator() + "s" + System.lineSeparator() + "n" + System.lineSeparator() +
                "s" + System.lineSeparator() + System.lineSeparator() + System.lineSeparator() +
                System.lineSeparator() + System.lineSeparator() + System.lineSeparator() + "0" + System.lineSeparator();
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingBytePosAndSingleNumAndValidDistinctFilesWithIndexHigherThanAnyNumLinesInFilesShouldRunSuccessfully() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                false, true, false, Integer.MAX_VALUE, Integer.MAX_VALUE,
                testFile1.toFile().getPath(), testFile3.toFile().getPath()
        );
        String expectedResult = System.lineSeparator() + System.lineSeparator() + System.lineSeparator() +
                System.lineSeparator() + System.lineSeparator() + System.lineSeparator() + System.lineSeparator() +
                System.lineSeparator() + System.lineSeparator() + System.lineSeparator() +
                System.lineSeparator() + System.lineSeparator();
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingCharPosAnd2CommaSeparatedNumAndValidDistinctFilesWithStartNumLowerThanEndNumShouldRunSuccessfully() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                true, false, false, 1, 274,
                testFile2.toFile().getPath(), testFile3.toFile().getPath()
        );
        String expectedResult = "Ls" + System.lineSeparator() + System.lineSeparator() + "Ed" + System.lineSeparator() +
                System.lineSeparator() + "Tn" + System.lineSeparator() + "1" + System.lineSeparator() +
                "2" + System.lineSeparator() + "5" + System.lineSeparator() + "2" + System.lineSeparator() +
                "2" + System.lineSeparator() + "5" + System.lineSeparator() + "0";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingCharPosAnd2CommaSeparatedNumAndValidDistinctFilesWithStartNumHigherThanEndNumShouldRunSuccessfully() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                true, false, false, 274, 1,
                testFile2.toFile().getPath(), testFile3.toFile().getPath()
        );
        String expectedResult = "Ls" + System.lineSeparator() + System.lineSeparator() + "Ed" + System.lineSeparator() +
                System.lineSeparator() + "Tn" + System.lineSeparator() + "1" + System.lineSeparator() + "2" + System.lineSeparator() +
                "5" + System.lineSeparator() + "2" + System.lineSeparator() + "2" + System.lineSeparator() + "5" + System.lineSeparator()
                + "0";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingCharPosAnd2CommaSeparatedNumAndValidDistinctFilesWithStartNumHigherThanAnyNumLinesInFilesShouldRunSuccessfully() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                true, false, false, Integer.MAX_VALUE, 1,
                testFile2.toFile().getPath(), testFile3.toFile().getPath()
        );
        String expectedResult = "L" + System.lineSeparator() + System.lineSeparator() + "E" + System.lineSeparator() +
                System.lineSeparator() + "T" + System.lineSeparator() + "1" + System.lineSeparator() + "2" +
                System.lineSeparator() + "5" + System.lineSeparator() + "2" + System.lineSeparator() + "2" +
                System.lineSeparator() + "5" + System.lineSeparator() + "0";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingCharPosAnd2CommaSeparatedNumAndValidDistinctFilesWithEndNumHigherThanAnyNumLinesInFilesShouldRunSuccessfully() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                true, false, false, 274, Integer.MAX_VALUE,
                testFile1.toFile().getPath(), testFile2.toFile().getPath()
        );
        String expectedResult = System.lineSeparator() + System.lineSeparator() + System.lineSeparator() + System.lineSeparator() +
                System.lineSeparator() + System.lineSeparator() + "s" + System.lineSeparator() + System.lineSeparator() +
                "d" + System.lineSeparator() + System.lineSeparator() + "n";
        assertEquals(expectedResult, actualResult);
    }


    @Test
    void testCutFromFilesUsingCharPosAndNumRangeAndValidDistinctFilesWithStartNumLowerThanEndNumShouldRunSuccessfully() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                true, false, true, 1, 200,
                testFile2.toFile().getPath(), testFile1.toFile().getPath()
        );
        String expectedResult = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Aliquam malesuada bibendum arcu vitae. Nam libero justo laoreet sit amet cur" +
                System.lineSeparator() + System.lineSeparator() +
                "Euismod quis viverra nibh cras pulvinar mattis nunc. Nam libero justo laoreet sit amet cursus sit amet dictum. Auctor augue mauris augue neque gravida in fermentum et. Nunc eget lorem dolor sed viverr" +
                System.lineSeparator() + System.lineSeparator() +
                "Turpis massa tincidunt dui ut ornare lectus sit. Phasellus egestas tellus rutrum tellus pellentesque eu tincidunt tortor aliquam. Est velit egestas dui id ornare arcu odio ut sem. Facilisi nullam vehi" + System.lineSeparator() +
                "CS4218: Software Testing" + System.lineSeparator() +
                "Thìš mödülè cövèrs thè concepts and prãctīće of software testing including unït testing, integration testing," + System.lineSeparator() +
                "and regression testing. Various testing coverage criteria will be discussed. Debugging methods for finding the" + System.lineSeparator() +
                "root-cause of errors in failing test cases will also be investigated. The use öf testing and analysis for" + System.lineSeparator() +
                "performance prediction, performance clustering and performance debugging will be studied. Students will acquire" + System.lineSeparator() +
                "crucial skills on testing and debugging through hands-on assignments.";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingCharPosAndNumRangeAndValidDistinctFilesWithStartNumHigherThanEndNumShouldRunSuccesfully() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                true, false, true, 200, 1,
                testFile2.toFile().getPath(), testFile1.toFile().getPath()
        );
        String expectedResult = System.lineSeparator() + System.lineSeparator() + System.lineSeparator() + System.lineSeparator() +
                System.lineSeparator() + System.lineSeparator() + System.lineSeparator() + System.lineSeparator() +
                System.lineSeparator() + System.lineSeparator();
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingCharPosAndNumRangeAndValidDistinctFilesWithStartNumHigherThanAnyNumLinesInFilesShouldRunSuccessfully() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                true, false, true, Integer.MAX_VALUE, 1,
                testFile2.toFile().getPath(), testFile1.toFile().getPath()
        );
        String expectedResult = System.lineSeparator() + System.lineSeparator() + System.lineSeparator() + System.lineSeparator() +
                System.lineSeparator() + System.lineSeparator() + System.lineSeparator() + System.lineSeparator() +
                System.lineSeparator() + System.lineSeparator();
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingCharPosAndNumRangeAndValidDistinctFilesWithEndNumHigherThanAnyNumLinesInFilesShouldRunSuccessfully() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                true, false, true, 200, Integer.MAX_VALUE,
                testFile2.toFile().getPath(), testFile1.toFile().getPath()
        );
        String expectedResult = "rsus sit amet. Egestas tellus rutrum tellus pellentesque eu. Proin nibh nisl condimentum id venenatis a condimentum. Magna etiam tempor orci eu lobortis. Vel facilisis volutpat est velit egestas dui. Sed viverra ipsum nunc aliquet bibendum enim facilisis gravida. Id aliquet risus feugiat in ante. Tincidunt augue interdum velit euismod in pellentesque. Vitae sapien pellentesque habitant morbi tristique. Feugiat pretium nibh ipsum consequat nisl." + System.lineSeparator() +
                System.lineSeparator() +
                "ra ipsum nunc aliquet. Mauris nunc congue nisi vitae. Sed adipiscing diam donec adipiscing. Luctus venenatis lectus magna fringilla. Quis auctor elit sed vulputate mi sit. Elit at imperdiet dui accumsan sit amet nulla facilisi. Semper viverra nam libero justo laoreet sit amet cursus sit. Sit amet commodo nulla facilisi nullam vehicula ipsum a arcu. Volutpat sed cras ornare arcu dui. Leo vel orci porta non. Maecenas sed enim ut sem viverra aliquet eget sit amet. In egestas erat imperdiet sed euismod nisi porta lorem. Amet volutpat consequat mauris nunc congue. Sodales ut etiam sit amet." + System.lineSeparator() +
                System.lineSeparator() +
                "icula ipsum a. Et netus et malesuada fames ac turpis egestas. Euismod lacinia at quis risus sed vulputate odio. Placerat orci nulla pellentesque dignissim enim sit. Metus aliquam eleifend mi in nulla posuere. Amet venenatis urna cursus eget. Elit sed vulputate mi sit. Lorem ipsum dolor sit amet consectetur adipiscing elit duis. Curabitur gravida arcu ac tortor dignissim. A pellentesque sit amet porttitor eget dolor morbi non arcu." + System.lineSeparator() +
                System.lineSeparator() + System.lineSeparator() + System.lineSeparator() + System.lineSeparator() + System.lineSeparator();
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingCharPosAndSingleNumAndValidDistinctFilesShouldRunSuccessfully() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                true, false, false, 250, 250,
                testFile2.toFile().getPath(), testFile1.toFile().getPath()
        );
        String expectedResult = "t" + System.lineSeparator() + System.lineSeparator() + "a" + System.lineSeparator() +
                System.lineSeparator() + "i" + System.lineSeparator() + System.lineSeparator() + System.lineSeparator() +
                System.lineSeparator() + System.lineSeparator() + System.lineSeparator();
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingCharPosAndSingleNumAndValidDistinctFilesWithIndexHigherThanAnyNumLinesInFilesShouldRunSuccessfully() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                true, false, false, Integer.MAX_VALUE, Integer.MAX_VALUE,
                testFile2.toFile().getPath(), testFile3.toFile().getPath()
        );
        String expectedResult = System.lineSeparator() + System.lineSeparator() + System.lineSeparator() + System.lineSeparator() +
                System.lineSeparator() + System.lineSeparator() + System.lineSeparator() + System.lineSeparator() +
                System.lineSeparator() + System.lineSeparator() + System.lineSeparator();
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingBytePosAnd2CommaSeparatedNumAndValidSimilarFilesWithStartNumLowerThanEndNumShouldRunSuccessfully() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                false, true, false, 32, 48,
                testFile2.toFile().getPath(), testFile2.toFile().getPath()
        );
        String expectedResult = "si" + System.lineSeparator() + System.lineSeparator() + "pn" + System.lineSeparator() +
                System.lineSeparator() + "r." + System.lineSeparator() + "si" + System.lineSeparator() + System.lineSeparator()
                + "pn" + System.lineSeparator() + System.lineSeparator() + "r.";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingBytePosAnd2CommaSeparatedNumAndValidSimilarFilesWithStartNumHigherThanEndNumShouldRunSuccessfully() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                false, true, false, 120, 8,
                testFile2.toFile().getPath(), testFile2.toFile().getPath()
        );
        String expectedResult = "pq" + System.lineSeparator() + System.lineSeparator() + " u" + System.lineSeparator() +
                System.lineSeparator() + "mr" + System.lineSeparator() + "pq" + System.lineSeparator() + System.lineSeparator() +
                " u" + System.lineSeparator() + System.lineSeparator() + "mr";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingBytePosAnd2CommaSeparatedNumAndValidSimilarFilesWithStartNumHigherThanAnyNumLinesInFilesShouldRunSuccessfully() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                false, true, false, Integer.MAX_VALUE, 144,
                testFile2.toFile().getPath(), testFile2.toFile().getPath()
        );
        String expectedResult = "i" + System.lineSeparator() + System.lineSeparator() + "g" + System.lineSeparator() + System.lineSeparator()
                + "s" + System.lineSeparator() + "i" + System.lineSeparator() + System.lineSeparator() + "g" +
                System.lineSeparator() + System.lineSeparator() + "s";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingBytePosAnd2CommaSeparatedNumAndValidSimilarFilesWithEndNumHigherThanAnyNumLinesInFilesShouldRunSuccessfully() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                false, true, false, 120, Integer.MAX_VALUE,
                testFile2.toFile().getPath(), testFile2.toFile().getPath()
        );
        String expectedResult = "q" + System.lineSeparator() + System.lineSeparator() + "u" + System.lineSeparator() +
                System.lineSeparator() + "r" + System.lineSeparator() + "q" + System.lineSeparator() +
                System.lineSeparator() + "u" + System.lineSeparator() + System.lineSeparator() + "r";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingBytePosAndNumRangeAndValidSimilarFilesWithStartNumLowerThanEndNumShouldRunSuccessfully() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                false, true, true, 2, 18,
                testFile1.toFile().getPath(), testFile1.toFile().getPath(), testFile1.toFile().getPath()
        );
        String expectedResult = "S4218: Software T" + System.lineSeparator() + "hìš mödülè c" + System.lineSeparator() +
                "nd regression tes" + System.lineSeparator() + "oot-cause of erro" + System.lineSeparator() +
                "erformance predic" + System.lineSeparator() + "rucial skills on " + System.lineSeparator() +
                "S4218: Software T" + System.lineSeparator() + "hìš mödülè c" + System.lineSeparator() + "nd regression tes" +
                System.lineSeparator() + "oot-cause of erro" + System.lineSeparator() + "erformance predic" + System.lineSeparator() +
                "rucial skills on " + System.lineSeparator() + "S4218: Software T" + System.lineSeparator() + "hìš mödülè c" +
                System.lineSeparator() + "nd regression tes" + System.lineSeparator() + "oot-cause of erro" + System.lineSeparator() +
                "erformance predic" + System.lineSeparator() + "rucial skills on ";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingBytePosAndNumRangeAndValidSimilarFilesWithStartNumHigherThanEndNumShouldRunSuccessfully() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                false, true, true, 10, 8,
                testFile1.toFile().getPath(), testFile1.toFile().getPath(), testFile1.toFile().getPath()
        );
        String expectedResult = System.lineSeparator() + System.lineSeparator() + System.lineSeparator() + System.lineSeparator() +
                System.lineSeparator() + System.lineSeparator() + System.lineSeparator() + System.lineSeparator() + System.lineSeparator()
                + System.lineSeparator() + System.lineSeparator()+ System.lineSeparator() + System.lineSeparator() + System.lineSeparator()
                + System.lineSeparator() + System.lineSeparator() + System.lineSeparator();
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingBytePosAndNumRangeAndValidSimilarFilesWithStartNumHigherThanAnyNumLinesInFilesShouldRunSuccessfully() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                false, true, true, Integer.MAX_VALUE, 8,
                testFile1.toFile().getPath(), testFile1.toFile().getPath(), testFile1.toFile().getPath()
        );
        String expectedResult = System.lineSeparator() + System.lineSeparator() + System.lineSeparator() + System.lineSeparator() +
                System.lineSeparator() + System.lineSeparator() + System.lineSeparator() + System.lineSeparator() + System.lineSeparator() +
                System.lineSeparator() + System.lineSeparator() + System.lineSeparator() + System.lineSeparator() + System.lineSeparator() +
                System.lineSeparator() + System.lineSeparator() + System.lineSeparator();
        assertEquals(expectedResult, actualResult);
    }


    @Test
    void testCutFromFilesUsingBytePosAndNumRangeAndValidSimilarFilesWithEndNumHigherThanAnyNumLinesInFilesShouldRunSuccessfully() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                false, true, true, 90, Integer.MAX_VALUE,
                testFile1.toFile().getPath(), testFile1.toFile().getPath(), testFile1.toFile().getPath()
        );
        String expectedResult = System.lineSeparator() + "ït testing, integration testing," + System.lineSeparator() +
                "thods for finding the" + System.lineSeparator() + " and analysis for" + System.lineSeparator() +
                " Students will acquire" + System.lineSeparator() + System.lineSeparator() + System.lineSeparator() +
                "ït testing, integration testing," + System.lineSeparator() + "thods for finding the" + System.lineSeparator() +
                " and analysis for" + System.lineSeparator() + " Students will acquire" + System.lineSeparator() +
                System.lineSeparator() +  System.lineSeparator() + "ït testing, integration testing," + System.lineSeparator() +
                "thods for finding the" + System.lineSeparator() + " and analysis for" +
                System.lineSeparator() + " Students will acquire" + System.lineSeparator();
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingBytePosAndSingleNumAndValidSimilarFilesShouldRunSuccessfully() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                false, true, false, 3, 3,
                testFile3.toFile().getPath(), testFile3.toFile().getPath(), testFile3.toFile().getPath(),
                testFile3.toFile().getPath(), testFile3.toFile().getPath()
        );
        String expectedResult = "0" + System.lineSeparator() + " " + System.lineSeparator() + "," + System.lineSeparator()
                + "," + System.lineSeparator() + "," + System.lineSeparator() + "1" + System.lineSeparator() + "1" +
                System.lineSeparator() + "0" + System.lineSeparator() + " " + System.lineSeparator() + "," + System.lineSeparator() +
                "," + System.lineSeparator() + "," + System.lineSeparator() + "1" + System.lineSeparator() + "1" + System.lineSeparator() +
                "0" + System.lineSeparator() + " " + System.lineSeparator() + "," + System.lineSeparator() + "," + System.lineSeparator() +
                "," + System.lineSeparator() + "1" + System.lineSeparator() + "1" + System.lineSeparator() + "0" + System.lineSeparator() +
                " " + System.lineSeparator() + "," + System.lineSeparator() + "," + System.lineSeparator() + "," + System.lineSeparator() +
                "1" + System.lineSeparator() + "1" + System.lineSeparator() + "0" + System.lineSeparator() + " " + System.lineSeparator() +
                "," + System.lineSeparator() + "," + System.lineSeparator() + "," + System.lineSeparator() + "1" + System.lineSeparator() + "1";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingBytePosAndSingleNumAndValidSimilarFilesWithIndexHigherThanAnyNumLinesInFilesShouldRunSuccessfully() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                false, true, false, Integer.MAX_VALUE, Integer.MAX_VALUE,
                testFile3.toFile().getPath(), testFile3.toFile().getPath()
        );
        String expectedResult = System.lineSeparator() + System.lineSeparator() + System.lineSeparator() + System.lineSeparator() +
                System.lineSeparator() + System.lineSeparator() + System.lineSeparator() + System.lineSeparator() +
                System.lineSeparator() + System.lineSeparator() + System.lineSeparator() + System.lineSeparator() +
                System.lineSeparator();
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingCharPosAnd2CommaSeparatedNumAndValidSimilarFilesWithStartNumLowerThanEndNumShouldRunSuccessfully() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                true, false, false, 1, 4,
                testFile3.toFile().getPath(), testFile3.toFile().getPath(), testFile3.toFile().getPath(),
                testFile3.toFile().getPath()
        );
        String expectedResult = "1," + System.lineSeparator() + "23" + System.lineSeparator() + "5 " + System.lineSeparator() + "2 " +
                System.lineSeparator() + "2 " + System.lineSeparator() + "5," + System.lineSeparator() + "0," + System.lineSeparator() +
                "1," + System.lineSeparator() + "23" + System.lineSeparator() + "5 " + System.lineSeparator() + "2 " + System.lineSeparator() +
                "2 " + System.lineSeparator() + "5," + System.lineSeparator() + "0," + System.lineSeparator() + "1," + System.lineSeparator() +
                "23" + System.lineSeparator() + "5 " + System.lineSeparator() + "2 " + System.lineSeparator() + "2 " + System.lineSeparator() +
                "5," + System.lineSeparator() + "0," + System.lineSeparator() + "1," + System.lineSeparator() + "23" + System.lineSeparator() +
                "5 " + System.lineSeparator() + "2 " + System.lineSeparator() + "2 " + System.lineSeparator() + "5," + System.lineSeparator() +
                "0,";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingCharPosAnd2CommaSeparatedNumAndValidSimilarFilesWithStartNumHigherThanEndNumShouldRunSuccessfully() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                true, false, false, 6, 3,
                testFile1.toFile().getPath(), testFile1.toFile().getPath(), testFile1.toFile().getPath(),
                testFile1.toFile().getPath(), testFile1.toFile().getPath(), testFile1.toFile().getPath()
        );
        String expectedResult = "48" + System.lineSeparator() + "ìm" + System.lineSeparator() + "de" + System.lineSeparator() +
                "oc" + System.lineSeparator() + "rr" + System.lineSeparator() + "ua" + System.lineSeparator() +
                "48" + System.lineSeparator() + "ìm" + System.lineSeparator() + "de" + System.lineSeparator() + "oc" + System.lineSeparator() +
                "rr" + System.lineSeparator() + "ua" + System.lineSeparator() + "48" + System.lineSeparator() + "ìm" + System.lineSeparator() +
                "de" + System.lineSeparator() + "oc" + System.lineSeparator() + "rr" + System.lineSeparator() + "ua" + System.lineSeparator() +
                "48" + System.lineSeparator() + "ìm" + System.lineSeparator() + "de" + System.lineSeparator() + "oc" + System.lineSeparator() +
                "rr" + System.lineSeparator() + "ua" + System.lineSeparator() + "48" + System.lineSeparator() + "ìm" + System.lineSeparator() +
                "de" + System.lineSeparator() + "oc" + System.lineSeparator() + "rr" + System.lineSeparator() + "ua" + System.lineSeparator() +
                "48" + System.lineSeparator() + "ìm" + System.lineSeparator() + "de" + System.lineSeparator() + "oc" + System.lineSeparator() +
                "rr" + System.lineSeparator() + "ua";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingCharPosAnd2CommaSeparatedNumAndValidSimilarFilesWithStartNumHigherThanAnyNumLinesInFilesShouldRunSuccessfully() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                true, false, false, Integer.MAX_VALUE, 3,
                testFile1.toFile().getPath(), testFile1.toFile().getPath(), testFile1.toFile().getPath()
        );
        String expectedResult = "4" + System.lineSeparator() + "ì" + System.lineSeparator() + "d" + System.lineSeparator() +
                "o" + System.lineSeparator() + "r" + System.lineSeparator() + "u" + System.lineSeparator() + "4" + System.lineSeparator() +
                "ì" + System.lineSeparator() + "d" + System.lineSeparator() + "o" + System.lineSeparator() + "r" + System.lineSeparator() +
                "u" + System.lineSeparator() + "4" + System.lineSeparator() + "ì" + System.lineSeparator() + "d" + System.lineSeparator() +
                "o" + System.lineSeparator() + "r" + System.lineSeparator() + "u";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingCharPosAnd2CommaSeparatedNumAndValidSimilarFilesWithEndNumHigherThanAnyNumLinesInFilesShouldRunSuccessfully() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                true, false, false, 6, Integer.MAX_VALUE,
                testFile1.toFile().getPath(), testFile1.toFile().getPath(), testFile1.toFile().getPath()
        );
        String expectedResult = "8" + System.lineSeparator() + "m" + System.lineSeparator() + "e" + System.lineSeparator() +
                "c" + System.lineSeparator() + "r" + System.lineSeparator() + "a" + System.lineSeparator() + "8" + System.lineSeparator() +
                "m" + System.lineSeparator() + "e" + System.lineSeparator() + "c" + System.lineSeparator() + "r" + System.lineSeparator() +
                "a" + System.lineSeparator() + "8" + System.lineSeparator() + "m" + System.lineSeparator() + "e" + System.lineSeparator() +
                "c" + System.lineSeparator() + "r" + System.lineSeparator() + "a";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingCharPosAndNumRangeAndValidSimilarFilesWithStartNumLowerThanEndNumShouldRunSuccessfully() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                true, false, true, 6, 17,
                testFile1.toFile().getPath(), testFile1.toFile().getPath(), testFile1.toFile().getPath(),
                testFile1.toFile().getPath(), testFile1.toFile().getPath(), testFile1.toFile().getPath(),
                testFile1.toFile().getPath(), testFile1.toFile().getPath(), testFile1.toFile().getPath(),
                testFile1.toFile().getPath()
        );
        String expectedResult = "8: Software " + System.lineSeparator() + "mödülè cövèr" + System.lineSeparator() + "egression te" + System.lineSeparator() +
                "cause of err" + System.lineSeparator() + "rmance predi" + System.lineSeparator() + "al skills on" + System.lineSeparator() +
                "8: Software " + System.lineSeparator() + "mödülè cövèr" + System.lineSeparator() + "egression te" + System.lineSeparator() +
                "cause of err" + System.lineSeparator() + "rmance predi" + System.lineSeparator() + "al skills on" + System.lineSeparator() +
                "8: Software " + System.lineSeparator() + "mödülè cövèr" + System.lineSeparator() + "egression te" + System.lineSeparator() +
                "cause of err" + System.lineSeparator() + "rmance predi" + System.lineSeparator() + "al skills on" + System.lineSeparator() +
                "8: Software " + System.lineSeparator() + "mödülè cövèr" + System.lineSeparator() + "egression te" + System.lineSeparator() +
                "cause of err" + System.lineSeparator() + "rmance predi" + System.lineSeparator() + "al skills on" + System.lineSeparator() +
                "8: Software " + System.lineSeparator() + "mödülè cövèr" + System.lineSeparator() + "egression te" + System.lineSeparator() +
                "cause of err" + System.lineSeparator() + "rmance predi" + System.lineSeparator() + "al skills on" + System.lineSeparator() +
                "8: Software " + System.lineSeparator() + "mödülè cövèr" + System.lineSeparator() + "egression te" + System.lineSeparator() +
                "cause of err" + System.lineSeparator() + "rmance predi" + System.lineSeparator() + "al skills on" + System.lineSeparator() +
                "8: Software " + System.lineSeparator() + "mödülè cövèr" + System.lineSeparator() + "egression te" + System.lineSeparator() +
                "cause of err" + System.lineSeparator() + "rmance predi" + System.lineSeparator() + "al skills on" + System.lineSeparator() +
                "8: Software " + System.lineSeparator() + "mödülè cövèr" + System.lineSeparator() + "egression te" + System.lineSeparator() +
                "cause of err" + System.lineSeparator() + "rmance predi" + System.lineSeparator() + "al skills on" + System.lineSeparator() +
                "8: Software " + System.lineSeparator() + "mödülè cövèr" + System.lineSeparator() + "egression te" + System.lineSeparator() +
                "cause of err" + System.lineSeparator() + "rmance predi" + System.lineSeparator() + "al skills on" + System.lineSeparator() +
                "8: Software " + System.lineSeparator() + "mödülè cövèr" + System.lineSeparator() + "egression te" + System.lineSeparator() +
                "cause of err" + System.lineSeparator() + "rmance predi" + System.lineSeparator() + "al skills on";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingCharPosAndNumRangeAndValidSimilarFilesWithStartNumHigherThanEndNumShouldRunSuccessfully() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                true, false, true, 16, 4,
                testFile1.toFile().getPath(), testFile1.toFile().getPath(), testFile1.toFile().getPath(),
                testFile1.toFile().getPath(), testFile1.toFile().getPath(), testFile1.toFile().getPath(),
                testFile1.toFile().getPath(), testFile1.toFile().getPath(), testFile1.toFile().getPath(),
                testFile1.toFile().getPath()
        );
        String expectedResult = System.lineSeparator() + System.lineSeparator() + System.lineSeparator() + System.lineSeparator() +
                System.lineSeparator() + System.lineSeparator() + System.lineSeparator() + System.lineSeparator() + System.lineSeparator() +
                System.lineSeparator() + System.lineSeparator() + System.lineSeparator() + System.lineSeparator() + System.lineSeparator() +
                System.lineSeparator() + System.lineSeparator() + System.lineSeparator() + System.lineSeparator() + System.lineSeparator() +
                System.lineSeparator() + System.lineSeparator() + System.lineSeparator() + System.lineSeparator() + System.lineSeparator() +
                System.lineSeparator() + System.lineSeparator() + System.lineSeparator() + System.lineSeparator() + System.lineSeparator() +
                System.lineSeparator() + System.lineSeparator() + System.lineSeparator() + System.lineSeparator() + System.lineSeparator() +
                System.lineSeparator() + System.lineSeparator() + System.lineSeparator() + System.lineSeparator() + System.lineSeparator() +
                System.lineSeparator() + System.lineSeparator() + System.lineSeparator() + System.lineSeparator() + System.lineSeparator() +
                System.lineSeparator() + System.lineSeparator() + System.lineSeparator() + System.lineSeparator() + System.lineSeparator() +
                System.lineSeparator() + System.lineSeparator() + System.lineSeparator() + System.lineSeparator() + System.lineSeparator() +
                System.lineSeparator() + System.lineSeparator() + System.lineSeparator() + System.lineSeparator() + System.lineSeparator();
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingCharPosAndNumRangeAndValidSimilarFilesWithStartNumHigherThanAnyNumLinesInFilesShouldRunSuccessfully() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                true, false, true, Integer.MAX_VALUE, 4,
                testFile1.toFile().getPath(), testFile1.toFile().getPath(), testFile1.toFile().getPath(),
                testFile1.toFile().getPath(), testFile1.toFile().getPath(), testFile1.toFile().getPath(),
                testFile1.toFile().getPath(), testFile1.toFile().getPath(), testFile1.toFile().getPath(),
                testFile1.toFile().getPath(), testFile1.toFile().getPath(), testFile1.toFile().getPath()
        );
        String expectedResult = System.lineSeparator() + System.lineSeparator() + System.lineSeparator() + System.lineSeparator() +
                System.lineSeparator() + System.lineSeparator() + System.lineSeparator() + System.lineSeparator() + System.lineSeparator() +
                System.lineSeparator() + System.lineSeparator() + System.lineSeparator() + System.lineSeparator() + System.lineSeparator() +
                System.lineSeparator() + System.lineSeparator() + System.lineSeparator() + System.lineSeparator() + System.lineSeparator() +
                System.lineSeparator() + System.lineSeparator() + System.lineSeparator() + System.lineSeparator() + System.lineSeparator() +
                System.lineSeparator() + System.lineSeparator() + System.lineSeparator() + System.lineSeparator() + System.lineSeparator() +
                System.lineSeparator() + System.lineSeparator() + System.lineSeparator() + System.lineSeparator() + System.lineSeparator() +
                System.lineSeparator() + System.lineSeparator() + System.lineSeparator() + System.lineSeparator() + System.lineSeparator() +
                System.lineSeparator() + System.lineSeparator() + System.lineSeparator() + System.lineSeparator() + System.lineSeparator() +
                System.lineSeparator() + System.lineSeparator() + System.lineSeparator() + System.lineSeparator() + System.lineSeparator() +
                System.lineSeparator() + System.lineSeparator() + System.lineSeparator() + System.lineSeparator() + System.lineSeparator() +
                System.lineSeparator() + System.lineSeparator() + System.lineSeparator() + System.lineSeparator() + System.lineSeparator() +
                System.lineSeparator() + System.lineSeparator() + System.lineSeparator() + System.lineSeparator() + System.lineSeparator() +
                System.lineSeparator() + System.lineSeparator() + System.lineSeparator() + System.lineSeparator() + System.lineSeparator() +
                System.lineSeparator() + System.lineSeparator();
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingCharPosAndNumRangeAndValidSimilarFilesWithEndNumHigherThanAnyNumLinesInFilesShouldRunSuccessfully() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                true, false, true, 55, Integer.MAX_VALUE,
                testFile1.toFile().getPath(), testFile1.toFile().getPath(), testFile1.toFile().getPath(),
                testFile1.toFile().getPath(), testFile1.toFile().getPath(), testFile1.toFile().getPath(),
                testFile1.toFile().getPath(), testFile1.toFile().getPath(), testFile1.toFile().getPath()
        );
        String expectedResult = System.lineSeparator() + "re testing including unït testing, integration testing," + System.lineSeparator() +
                "ria will be discussed. Debugging methods for finding the" + System.lineSeparator() + "e investigated. The use öf testing and analysis for" + System.lineSeparator() +
                "formance debugging will be studied. Students will acquire" + System.lineSeparator() + "on assignments." + System.lineSeparator() + System.lineSeparator() +
                "re testing including unït testing, integration testing," + System.lineSeparator() + "ria will be discussed. Debugging methods for finding the" + System.lineSeparator() +
                "e investigated. The use öf testing and analysis for" + System.lineSeparator() + "formance debugging will be studied. Students will acquire" + System.lineSeparator() +
                "on assignments." + System.lineSeparator() + System.lineSeparator() + "re testing including unït testing, integration testing," + System.lineSeparator() +
                "ria will be discussed. Debugging methods for finding the" + System.lineSeparator() + "e investigated. The use öf testing and analysis for" + System.lineSeparator() +
                "formance debugging will be studied. Students will acquire" + System.lineSeparator() + "on assignments." + System.lineSeparator() + System.lineSeparator() +
                "re testing including unït testing, integration testing," + System.lineSeparator() + "ria will be discussed. Debugging methods for finding the" + System.lineSeparator() +
                "e investigated. The use öf testing and analysis for" + System.lineSeparator() + "formance debugging will be studied. Students will acquire" + System.lineSeparator() +
                "on assignments." + System.lineSeparator() + System.lineSeparator() + "re testing including unït testing, integration testing," + System.lineSeparator() +
                "ria will be discussed. Debugging methods for finding the" + System.lineSeparator() + "e investigated. The use öf testing and analysis for" + System.lineSeparator() +
                "formance debugging will be studied. Students will acquire" + System.lineSeparator() + "on assignments." + System.lineSeparator() + System.lineSeparator() +
                "re testing including unït testing, integration testing," + System.lineSeparator() + "ria will be discussed. Debugging methods for finding the" + System.lineSeparator() +
                "e investigated. The use öf testing and analysis for" + System.lineSeparator() + "formance debugging will be studied. Students will acquire" + System.lineSeparator() +
                "on assignments." + System.lineSeparator() + System.lineSeparator() + "re testing including unït testing, integration testing," + System.lineSeparator() +
                "ria will be discussed. Debugging methods for finding the" + System.lineSeparator() + "e investigated. The use öf testing and analysis for" + System.lineSeparator() +
                "formance debugging will be studied. Students will acquire" + System.lineSeparator() + "on assignments." + System.lineSeparator() + System.lineSeparator() +
                "re testing including unït testing, integration testing," + System.lineSeparator() + "ria will be discussed. Debugging methods for finding the" + System.lineSeparator() +
                "e investigated. The use öf testing and analysis for" + System.lineSeparator() + "formance debugging will be studied. Students will acquire" + System.lineSeparator() +
                "on assignments." + System.lineSeparator() + System.lineSeparator() + "re testing including unït testing, integration testing," + System.lineSeparator() +
                "ria will be discussed. Debugging methods for finding the" + System.lineSeparator() + "e investigated. The use öf testing and analysis for" + System.lineSeparator() +
                "formance debugging will be studied. Students will acquire" + System.lineSeparator() + "on assignments.";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingCharPosAndSingleNumAndValidSimilarFilesShouldRunSuccessfully() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                true, false, false, 8, 8,
                testFile2.toFile().getPath(), testFile2.toFile().getPath(), testFile2.toFile().getPath(),
                testFile2.toFile().getPath()
        );
        String expectedResult = "p" + System.lineSeparator() + System.lineSeparator() + " " + System.lineSeparator() + System.lineSeparator() +
                "m" + System.lineSeparator() + "p" + System.lineSeparator() + System.lineSeparator() + " " + System.lineSeparator() +
                System.lineSeparator() + "m" + System.lineSeparator() + "p" + System.lineSeparator() + System.lineSeparator() + " " + System.lineSeparator() +
                System.lineSeparator() + "m" + System.lineSeparator() + "p" + System.lineSeparator() + System.lineSeparator() + " " +
                System.lineSeparator() + System.lineSeparator() + "m";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingCharPosAndSingleNumAndValidSimilarFilesWithIndexHigherThanAnyNumLinesInFilesShouldRunSuccessfully() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                true, false, false, Integer.MAX_VALUE, Integer.MAX_VALUE,
                testFile2.toFile().getPath(), testFile2.toFile().getPath(), testFile2.toFile().getPath()
        );
        String expectedResult = System.lineSeparator() + System.lineSeparator() + System.lineSeparator() + System.lineSeparator() +
                System.lineSeparator() + System.lineSeparator() + System.lineSeparator() + System.lineSeparator() + System.lineSeparator() +
                System.lineSeparator() + System.lineSeparator() + System.lineSeparator() + System.lineSeparator() + System.lineSeparator();
        assertEquals(expectedResult, actualResult);
    }

    /**
     * Test cut application using cutFromStdin()
     */
    // Error Test cases
    @Test
    void testCutFromStdinUsingBytePosAnd2CommaSeparatedNumAndValidInputStreamWithStartNumLessThanZeroShouldThrowCutException() {
        Throwable thrown = assertThrows(Exception.class, () -> cutApplication.cutFromStdin(
                false, true, false, -1, 15, ourTestStdin
        ));
        assertEquals(thrown.getMessage(), ERR_LESS_THAN_ZERO);
    }

    @Test
    void testCutFromStdinUsingCharPosAnd2CommaSeparatedNumAndValidInputStreamWithEndNumLessThanZeroShouldThrowCutException() {
        Throwable thrown = assertThrows(Exception.class, () -> cutApplication.cutFromStdin(
                true, false, false, 15, 0, ourTestStdin
        ));
        assertEquals(thrown.getMessage(), ERR_LESS_THAN_ZERO);
    }

    @Test
    void testCutFromStdinUsingBytePosAndSingleNumAndValidInputStreamWithNumLessThanZeroShouldThrowCutException() {
        Throwable thrown = assertThrows(Exception.class, () -> cutApplication.cutFromStdin(
                false, true, false, (int) -12.65, (int) -5.5, ourTestStdin
        ));
        assertEquals(thrown.getMessage(), ERR_LESS_THAN_ZERO);
    }

    @Test
    void testCutFromStdinUsingBytePosAndSingleNumAndNullInputStreamShouldThrowCutException() {
        Throwable thrown = assertThrows(Exception.class, () -> cutApplication.cutFromStdin(false, false, false, 1, 2, null));
        assertEquals(thrown.getMessage(), ERR_NULL_STREAMS);
    }

    // Single Test cases
    @Test
    void testCutFromStdinUsingBytePosAndSingleNumAndValidInputStreamWithAnEmptyInputStreamShouldRunSuccessfully() throws Exception {
        String actualResult = cutApplication.cutFromStdin(
                false, true, false, 4, 4,
                new ByteArrayInputStream(new byte[0])
        );
        String expectedResult = "";
        assertEquals(expectedResult, actualResult);
    }

    // Positive Test cases
    @Test
    void testCutFromStdinUsingBytePosAnd2CommaSeparatedNumAndValidInputStreamWithStartNumLowerThanEndNumShouldRunSuccessfully() throws Exception {
        String actualResult = cutApplication.cutFromStdin(
                false, true, false, 1, 5,
                ourTestStdin
        );
        String expectedResult = "db";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromStdinUsingBytePosAnd2CommaSeparatedNumAndValidInputStreamWithStartNumHigherThanEndNumShouldRunSuccessfully() throws Exception {
        String actualResult = cutApplication.cutFromStdin(
                false, true, false, 2, 1,
                ourTestStdin
        );
        String expectedResult = "dr";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromStdinUsingBytePosAnd2CommaSeparatedNumAndValidInputStreamWithStartNumHigherThanAnyNumLinesInInputStreamShouldRunSuccessfully() throws Exception {
        String actualResult = cutApplication.cutFromStdin(
                false, true, false, Integer.MAX_VALUE, 3,
                ourTestStdin
        );
        String expectedResult = "ü";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromStdinUsingBytePosAnd2CommaSeparatedNumAndValidInputStreamWithEndNumHigherThanAnyNumLinesInInputStreamShouldRunSuccessfully() throws Exception {
        String actualResult = cutApplication.cutFromStdin(
                false, true, false, 4, Integer.MAX_VALUE,
                ourTestStdin
        );
        String expectedResult = "ü";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromStdinUsingBytePosAndNumRangeAndValidInputStreamWithStartNumLowerThanEndNumShouldRunSuccessfully() throws Exception {
        String actualResult = cutApplication.cutFromStdin(
                false, true, true, 2, 13,
                ourTestStdin
        );
        String expectedResult = "rüberspring";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromStdinUsingBytePosAndNumRangeAndValidInputStreamWithStartNumHigherThanEndNumShouldRunSuccessfully() throws Exception {
        String actualResult = cutApplication.cutFromStdin(
                false, true, true, 11, 7,
                ourTestStdin
        );
        String expectedResult = "";
        assertEquals(expectedResult, actualResult);
    }


    @Test
    void testCutFromStdinUsingBytePosAndNumRangeAndValidInputStreamWithStartNumHigherThanAnyNumLinesInInputStreamShouldRunSuccessfully() throws Exception {
        String actualResult = cutApplication.cutFromStdin(
                false, true, true, Integer.MAX_VALUE, 13,
                ourTestStdin
        );
        String expectedResult = "";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromStdinUsingBytePosAndNumRangeAndValidInputStreamWithEndNumHigherThanAnyNumLinesInInputStreamShouldRunSuccessfully() throws Exception {
        String actualResult = cutApplication.cutFromStdin(
                false, true, true, 15, Integer.MAX_VALUE,
                ourTestStdin
        );
        String expectedResult = "n";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromStdinUsingBytePosAndSingleNumAndValidInputStreamShouldRunSuccessfully() throws Exception {
        String actualResult = cutApplication.cutFromStdin(
                false, true, false, 5, 5,
                ourTestStdin
        );
        String expectedResult = "b";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromStdinUsingBytePosAndSingleNumAndValidInputStreamWithIndexHigherThanAnyNumLinesInInputStreamShouldRunSuccessfully() throws Exception {
        String actualResult = cutApplication.cutFromStdin(
                false, true, false, Integer.MAX_VALUE, Integer.MAX_VALUE,
                ourTestStdin
        );
        String expectedResult = "";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromStdinUsingCharPosAnd2CommaSeparatedNumAndValidInputStreamWithStartNumLowerThanEndNumShouldRunSuccessfully() throws Exception {
        String actualResult = cutApplication.cutFromStdin(
                true, false, false, 14, 15,
                ourTestStdin
        );
        String expectedResult = "n";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromStdinUsingCharPosAnd2CommaSeparatedNumAndValidInputStreamWithStartNumHigherThanEndNumShouldRunSuccessfully() throws Exception {
        String actualResult = cutApplication.cutFromStdin(
                true, false, false, 14, 1,
                ourTestStdin
        );
        String expectedResult = "dn";
        assertEquals(expectedResult, actualResult);
    }


    @Test
    void testCutFromStdinUsingCharPosAnd2CommaSeparatedNumAndValidInputStreamWithStartNumHigherThanAnyNumLinesInInputStreamShouldRunSuccessfully() throws Exception {
        String actualResult = cutApplication.cutFromStdin(
                true, false, false, Integer.MAX_VALUE, 1,
                ourTestStdin
        );
        String expectedResult = "d";
        assertEquals(expectedResult, actualResult);
    }


    @Test
    void testCutFromStdinUsingCharPosAnd2CommaSeparatedNumAndValidInputStreamWithEndNumHigherThanAnyNumLinesInInputStreamShouldRunSuccessfully() throws Exception {
        String actualResult = cutApplication.cutFromStdin(
                true, false, false, 14, Integer.MAX_VALUE,
                ourTestStdin
        );
        String expectedResult = "n";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromStdinUsingCharPosAndNumRangeAndValidInputStreamWithStartNumLowerThanEndNumShouldRunSuccessfully() throws Exception {
        String actualResult = cutApplication.cutFromStdin(
                true, false, true, 4, 14,
                ourTestStdin
        );
        String expectedResult = "berspringen";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromStdinUsingCharPosAndNumRangeAndValidInputStreamWithStartNumHigherThanEndNumShouldRunSuccessfully() throws Exception {
        String actualResult = cutApplication.cutFromStdin(
                true, false, true, 13, 9,
                ourTestStdin
        );
        String expectedResult = "";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromStdinUsingCharPosAndNumRangeAndValidInputStreamWithStartNumHigherThanAnyNumLinesInInputStreamShouldRunSuccessfully() throws Exception {
        String actualResult = cutApplication.cutFromStdin(
                true, false, true, Integer.MAX_VALUE, 2,
                ourTestStdin
        );
        String expectedResult = "";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromStdinUsingCharPosAndNumRangeAndValidInputStreamWithEndNumHigherThanAnyNumLinesInInputStreamShouldRunSuccessfully() throws Exception {
        String actualResult = cutApplication.cutFromStdin(
                true, false, true, 2, Integer.MAX_VALUE,
                ourTestStdin
        );
        String expectedResult = "rüberspringen";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromStdinUsingCharPosAndSingleNumAndValidInputStreamShouldRunSuccessfully() throws Exception {
        String actualResult = cutApplication.cutFromStdin(
                true, false, false, 4, 4,
                ourTestStdin
        );
        String expectedResult = "b";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromStdinUsingCharPosAndSingleNumAndValidInputStreamWithIndexHigherThanAnyNumLinesInInputStreamShouldRunSuccessfully() throws Exception {
        String actualResult = cutApplication.cutFromStdin(
                true, false, false, Integer.MAX_VALUE, Integer.MAX_VALUE,
                ourTestStdin
        );
        String expectedResult = "";
        assertEquals(expectedResult, actualResult);
    }

    private class CutApplicationStub implements CutInterface {
        @Override
        public String cutFromFiles(Boolean isCharPo, Boolean isBytePo, Boolean isRange, int startIdx, int endIdx, String... fileName) throws Exception {
            throw new AssertionError("This method should not be implemented");
        }

        @Override
        public String cutFromStdin(Boolean isCharPo, Boolean isBytePo, Boolean isRange, int startIdx, int endIdx, InputStream stdin) {
            throw new AssertionError("This method should not be implemented");
        }

        @Override
        public void run(String[] args, InputStream stdin, OutputStream stdout) {
            throw new AssertionError("This method should not be implemented");
        }
    }

    private class CutApplicationStubWithFileHasNoReadAccess extends CutApplicationStub {
        @Override
        public String cutFromFiles(Boolean isCharPo, Boolean isBytePo, Boolean isRange, int startIdx, int endIdx, String... fileName) throws Exception {
            throw new Exception(ERR_NO_PERM);
        }
    }
}