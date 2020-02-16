package test.sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.exception.CutException;
import sg.edu.nus.comp.cs4218.impl.app.CutApplication;
import test.sg.edu.nus.comp.cs4218.impl.util.TestFileUtils;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;

class CutApplicationTest {
    private CutApplication cutApplication;
    private String[] defaultCutArgs;
    private InputStream ourTestStdin;
    private OutputStream ourTestStdout;
    private static final String TEST_STDIN_MSG_1 = "baz";
    private Path testFile1 = Paths.get(TestFileUtils.TESTDATA_DIR + "test1.txt");


    @BeforeEach
    public void setUp() {
        cutApplication = new CutApplication();
        defaultCutArgs = Arrays.asList("-c","8").toArray(new String[1]);
        ourTestStdin = new ByteArrayInputStream(TEST_STDIN_MSG_1.getBytes());
        ourTestStdout = new ByteArrayOutputStream();
    }

    @AfterEach
    public void tearDown() {
    }

    /**
     * Test cut application with run().
     */

    @Test
    void testRunNullArgs() {
       Throwable thrown = assertThrows(CutException.class, () -> cutApplication.run(null, ourTestStdin, ourTestStdout));
       assertEquals(thrown.getMessage(), CutApplication.COMMAND + ": " + ERR_NULL_ARGS);
    }

    @Test
    void testRunNullOutputStream() {
        Throwable thrown = assertThrows(CutException.class, () -> cutApplication.run(defaultCutArgs, ourTestStdin, null));
        assertEquals(thrown.getMessage(), CutApplication.COMMAND + ": "  + ERR_NO_OSTREAM);
    }

    @Test
    void testRunSuccess() throws CutException {
        //Use a sample test file.
        cutApplication.run(Arrays.asList("-c", "6", "README.md").toArray(new String[3]), ourTestStdin, ourTestStdout);
        assertEquals("2\na\nr\n", ourTestStdout.toString());
    }

    /**
     * Test cut application using cutFromFiles()
     */
    // Erronorous Test cases (13 cases)
    @Test
    void testCutFromFilesWithoutAnyPosFlags() {
        Throwable thrown = assertThrows(CutException.class, () -> cutApplication.cutFromFiles(
                false, false, false, 1, 5, testFile1.toFile().getPath()
        ));
        assertEquals(thrown.getMessage(), CutApplication.COMMAND + ": "  + ERR_MISSING_ARG);
    }

    @Test
    void testCutFromFilesUsingByteAndCharPos() {
        Throwable thrown = assertThrows(CutException.class, () -> cutApplication.cutFromFiles(
                true, true, false, 1, 5, testFile1.toFile().getPath()
        ));
        assertEquals(thrown.getMessage(), CutApplication.COMMAND + ": "  + ERR_TOO_MANY_ARGS);
    }

    @Test
    void testCutFromFilesUsingBytePosAnd2CommaSeparatedNumAndASingleFileWithStartNumLessThanZero() { }

    @Test
    void testCutFromFilesUsingCharPosAnd2CommaSeparatedNumAndASingleFileWithEndNumLessThanZero() { }

    @Test
    void testCutFromFilesUsingCharPosAnd2CommaSeparatedNumAndASingleFileWithStartNumIsNotAInteger() { }

    @Test
    void testCutFromFilesUsingBytePosAnd2CommaSeparatedNumAndASingleFileWithEndNumIsNotAInteger() { }

    @Test
    void testCutFromFilesUsingBytePosAndSingleNumAndASingleFileWithNumLessThanZero() { }

    @Test
    void testCutFromFilesUsingBytePosAndSingleNumAndASingleFileWithNumIsNotAInteger() { }

    @Test
    void testCutFromFilesUsingCharPosAndNumRangeAndASingleFileWithFileNotFoundInDir() { }

    @Test
    void testCutFromFilesUsingBytePosAndNumRangeAndMultipleFilesWithAtLeastOneFileNotFoundInDir() { }

    @Test
    void testCutFromFilesUsingBytePosAndNumRangeAndASingleFileWithInvalidFilename() { }

    @Test
    void testCutFromFilesUsingCharPosAndNumRangeAndMultipleFilesWithAtLeastOneInvalidFilename() { }

    @Test
    void testCutFromFilesUsingBytePosAndSingleNumAndNullFilename() {
        Throwable thrown = assertThrows(CutException.class, () -> cutApplication.cutFromFiles(false, false, false, 1, 2, (String[]) null));
        assertEquals(thrown.getMessage(), CutApplication.COMMAND + ": "  + ERR_GENERAL);
    }

    // Single Test cases (1 case)
    @Test
    void testCutFromFilesUsingBytePosAndSingleNumAndEmptyFilenameString() { }

    // Positive test cases (50 cases)
    @Test
    void testCutFromFilesUsingBytePosAnd2CommaSeparatedNumAndValidSingleFileWithStartNumLowerThanEndNum() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                false, true, false, 6, 13, testFile1.toFile().getPath()
        );
        String expectedResult = "8w\n" + "šü\n" + "eo\n" + "cf\n" + "rp\n" + "al";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingBytePosAnd2CommaSeparatedNumUnsortedAndValidSingleFileWithStartNumHigherThanEndNum() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                false, true, false, 13, 6, testFile1.toFile().getPath()
        );
        String expectedResult = "8w\n" + "šü\n" + "eo\n" + "cf\n" + "rp\n" + "al";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingBytePosAndNumRangeAndValidSingleFileWithStartNumLowerThanEndNum() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                false, true, true, 6, 12, testFile1.toFile().getPath()
        );
        String expectedResult = "8: Soft\n" + "š mödü\n" + "egressi\n" + "cause o\n" + "rmance \n" + "al skil";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingBytePosAndNumRangeAndValidSingleFileWithStartNumHigherThanEndNum() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                false, true, true, 12, 5, testFile1.toFile().getPath()
        );
        String expectedResult = "\n" + "\n" + "\n" + "\n" + "\n" + "";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingBytePosAndSingleNumAndValidSingleFile() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                false, true, false, 5, 5, testFile1.toFile().getPath()
        );
        String expectedResult = "1\n" + "š\n" + "r\n" + "-\n" + "o\n" + "i";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingCharPosAnd2CommaSeparatedNumAndValidSingleFileWithStartNumLowerThanEndNum() { }

    @Test
    void testCutFromFilesUsingCharPosAnd2CommaSeparatedNumUnsortedAndValidSingleFileWithStartNumHigherThanEndNum() { }

    @Test
    void testCutFromFilesUsingCharPosAndNumRangeAndValidSingleFileWithStartNumLowerThanEndNum() { }

    @Test
    void testCutFromFilesUsingCharPosAndNumRangeAndValidSingleFileWithStartNumHigherThanEndNum() { }

    @Test
    void testCutFromFilesUsingCharPosAndSingleNumAndValidSingleFile() { }

    @Test
    void testCutFromFilesUsingBytePosAnd2CommaSeparatedNumAndValidDistinctFilesWithStartNumLowerThanEndNum() { }

    @Test
    void testCutFromFilesUsingBytePosAnd2CommaSeparatedNumUnsortedAndValidDistinctFilesWithStartNumHigherThanEndNum() { }

    @Test
    void testCutFromFilesUsingBytePosAndNumRangeAndValidDistinctFilesWithStartNumLowerThanEndNum() { }

    @Test
    void testCutFromFilesUsingBytePosAndNumRangeAndValidDistinctFilesWithStartNumHigherThanEndNum() { }

    @Test
    void testCutFromFilesUsingBytePosAndSingleNumAndValidDistinctFiles() { }

    @Test
    void testCutFromFilesUsingCharPosAnd2CommaSeparatedNumAndValidDistinctFilesWithStartNumLowerThanEndNum() { }

    @Test
    void testCutFromFilesUsingCharPosAnd2CommaSeparatedNumUnsortedAndValidDistinctFilesWithStartNumHigherThanEndNum() { }

    @Test
    void testCutFromFilesUsingCharPosAndNumRangeAndValidDistinctFilesWithStartNumLowerThanEndNum() { }

    @Test
    void testCutFromFilesUsingCharPosAndNumRangeAndValidDistinctFilesWithStartNumHigherThanEndNum() { }

    @Test
    void testCutFromFilesUsingCharPosAndSingleNumAndValidDistinctFiles() { }

    @Test
    void testCutFromFilesUsingBytePosAnd2CommaSeparatedNumAndFileIsDashWithStartNumLowerThanEndNum() { }

    @Test
    void testCutFromFilesUsingBytePosAnd2CommaSeparatedNumUnsortedAndFileIsDashWithStartNumHigherThanEndNum() { }

    @Test
    void testCutFromFilesUsingBytePosAndNumRangeAndFileIsDashWithStartNumLowerThanEndNum() { }

    @Test
    void testCutFromFilesUsingBytePosAndNumRangeAndFileIsDashWithStartNumHigherThanEndNum() { }

    @Test
    void testCutFromFilesUsingBytePosAndSingleNumAndFileIsDash() { }

    @Test
    void testCutFromFilesUsingCharPosAnd2CommaSeparatedNumAndFileIsDashWithStartNumLowerThanEndNum() { }

    @Test
    void testCutFromFilesUsingCharPosAnd2CommaSeparatedNumUnsortedAndFileIsDashWithStartNumHigherThanEndNum() { }

    @Test
    void testCutFromFilesUsingCharPosAndNumRangeAndFileIsDashWithStartNumLowerThanEndNum() { }

    @Test
    void testCutFromFilesUsingCharPosAndNumRangeAndFileIsDashWithStartNumHigherThanEndNum() { }

    @Test
    void testCutFromFilesUsingCharPosAndSingleNumAndFileIsDash() { }

    @Test
    void testCutFromFilesUsingBytePosAnd2CommaSeparatedNumAndValidSimilarFilesWithStartNumLowerThanEndNum() { }

    @Test
    void testCutFromFilesUsingBytePosAnd2CommaSeparatedNumUnsortedAndValidSimilarFilesWithStartNumHigherThanEndNum() { }

    @Test
    void testCutFromFilesUsingBytePosAndNumRangeAndValidSimilarFilesWithStartNumLowerThanEndNum() { }

    @Test
    void testCutFromFilesUsingBytePosAndNumRangeAndValidSimilarFilesWithStartNumHigherThanEndNum() { }

    @Test
    void testCutFromFilesUsingBytePosAndSingleNumAndValidSimilarFiles() { }

    @Test
    void testCutFromFilesUsingCharPosAnd2CommaSeparatedNumAndValidSimilarFilesWithStartNumLowerThanEndNum() { }

    @Test
    void testCutFromFilesUsingCharPosAnd2CommaSeparatedNumUnsortedAndValidSimilarFilesWithStartNumHigherThanEndNum() { }

    @Test
    void testCutFromFilesUsingCharPosAndNumRangeAndValidSimilarFilesWithStartNumLowerThanEndNum() { }

    @Test
    void testCutFromFilesUsingCharPosAndNumRangeAndValidSimilarFilesWithStartNumHigherThanEndNum() { }

    @Test
    void testCutFromFilesUsingCharPosAndSingleNumAndValidSimilarFiles() { }

    @Test
    void testCutFromFilesUsingBytePosAnd2CommaSeparatedNumAndHavingDashBetweenMultipleFilesWithStartNumLowerThanEndNum() { }

    @Test
    void testCutFromFilesUsingBytePosAnd2CommaSeparatedNumUnsortedAndHavingDashBetweenMultipleFilesWithStartNumHigherThanEndNum() { }

    @Test
    void testCutFromFilesUsingBytePosAndNumRangeAndHavingDashBetweenMultipleFilesWithStartNumLowerThanEndNum() { }

    @Test
    void testCutFromFilesUsingBytePosAndNumRangeAndHavingDashBetweenMultipleFilesWithStartNumHigherThanEndNum() { }

    @Test
    void testCutFromFilesUsingBytePosAndSingleNumAndHavingDashBetweenMultipleFiles() { }

    @Test
    void testCutFromFilesUsingCharPosAnd2CommaSeparatedNumAndHavingDashBetweenMultipleFilesWithStartNumLowerThanEndNum() { }

    @Test
    void testCutFromFilesUsingCharPosAnd2CommaSeparatedNumUnsortedAndHavingDashBetweenMultipleFilesWithStartNumHigherThanEndNum() { }

    @Test
    void testCutFromFilesUsingCharPosAndNumRangeAndHavingDashBetweenMultipleFilesWithStartNumLowerThanEndNum() { }

    @Test
    void testCutFromFilesUsingCharPosAndNumRangeAndHavingDashBetweenMultipleFilesWithStartNumHigherThanEndNum() { }

    @Test
    void testCutFromFilesUsingCharPosAndSingleNumAndHavingDashBetweenMultipleFiles() { }


    /**
     * Test cut application using cutFromStdin()
     */
    // Erronorous Test cases (9 cases)
    @Test
    void testCutFromStdinWithoutAnyPosFlags() {
        Throwable thrown = assertThrows(CutException.class, () -> cutApplication.cutFromStdin(
                false, false, false, 1, 5, ourTestStdin
        ));
        assertEquals(thrown.getMessage(), CutApplication.COMMAND + ": "  + ERR_MISSING_ARG);
    }

    @Test
    void testCutFromStdinUsingByteAndCharPos() {
        Throwable thrown = assertThrows(CutException.class, () -> cutApplication.cutFromStdin(
                true, true, false, 1, 5, ourTestStdin
        ));
        assertEquals(thrown.getMessage(), CutApplication.COMMAND + ": "  + ERR_TOO_MANY_ARGS);
    }

    @Test
    void testCutFromStdinUsingBytePosAnd2CommaSeparatedNumAndValidInputStreamWithStartNumLessThanZero() { }

    @Test
    void testCutFromStdinUsingCharPosAnd2CommaSeparatedNumAndValidInputStreamWithEndNumLessThanZero() { }

    @Test
    void testCutFromStdinUsingCharPosAnd2CommaSeparatedNumAndValidInputStreamWithStartNumIsNotAInteger() { }

    @Test
    void testCutFromStdinUsingBytePosAnd2CommaSeparatedNumAndValidInputStreamWithEndNumIsNotAInteger() { }

    @Test
    void testCutFromStdinUsingBytePosAndSingleNumAndValidInputStreamWithNumLessThanZero() { }

    @Test
    void testCutFromStdinUsingBytePosAndSingleNumAndValidInputStreamWithNumIsNotAInteger() { }

    @Test
    void testCutFromStdinUsingBytePosAndSingleNumAndNullInputStream() {
        Throwable thrown = assertThrows(CutException.class, () -> cutApplication.cutFromStdin(false, false, false, 1, 2, null));
        assertEquals(thrown.getMessage(), CutApplication.COMMAND + ": "  + ERR_NULL_STREAMS);
    }

    // Single Test cases (1 case)
    @Test
    void testCutFromStdinUsingBytePosAndSingleNumAndValidInputStreamWithAnEmptyInputStream() { }

    // Positive Test cases (10 cases)
    @Test
    void testCutFromStdinUsingBytePosAnd2CommaSeparatedNumAndValidInputStreamWithStartNumLowerThanEndNum() { }

    @Test
    void testCutFromStdinUsingBytePosAnd2CommaSeparatedNumUnsortedAndValidInputStreamWithStartNumHigherThanEndNum() { }

    @Test
    void testCutFromStdinUsingBytePosAndNumRangeAndValidInputStreamWithStartNumLowerThanEndNum() { }

    @Test
    void testCutFromStdinUsingBytePosAndNumRangeAndValidInputStreamWithStartNumHigherThanEndNum() { }

    @Test
    void testCutFromStdinUsingBytePosAndSingleNumAndValidInputStream() { }

    @Test
    void testCutFromStdinUsingCharPosAnd2CommaSeparatedNumAndValidInputStreamWithStartNumLowerThanEndNum() { }

    @Test
    void testCutFromStdinUsingCharPosAnd2CommaSeparatedNumUnsortedAndValidInputStreamWithStartNumHigherThanEndNum() { }

    @Test
    void testCutFromStdinUsingCharPosAndNumRangeAndValidInputStreamWithStartNumLowerThanEndNum() { }

    @Test
    void testCutFromStdinUsingCharPosAndNumRangeAndValidInputStreamWithStartNumHigherThanEndNum() { }

    @Test
    void testCutFromStdinUsingCharPosAndSingleNumAndValidInputStream() { }
}