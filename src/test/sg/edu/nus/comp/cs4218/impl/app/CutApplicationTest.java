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
    private Path testFile2 = Paths.get(TestFileUtils.TESTDATA_DIR + "test2.txt");
    private Path testFile3 = Paths.get(TestFileUtils.TESTDATA_DIR + "test3.csv");

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
    void testCutFromFilesUsingBytePosAnd2CommaSeparatedNumAndValidSingleFileWithStartNumHigherThanEndNum() throws Exception {
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
    void testCutFromFilesUsingCharPosAnd2CommaSeparatedNumAndValidSingleFileWithStartNumLowerThanEndNum() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                true, false, false, 2, 29, testFile1.toFile().getPath()
        );
        String expectedResult = "S\n" + "hp\n" + "no\n" + "oi\n" + "eo\n" + "rd";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingCharPosAnd2CommaSeparatedNumAndValidSingleFileWithStartNumHigherThanEndNum() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                true, false, false, 56, 18, testFile1.toFile().getPath()
        );
        String expectedResult = "T \n" + "se\n" + "si\n" + "o \n" + "co\n" + " n";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingCharPosAndNumRangeAndValidSingleFileWithStartNumLowerThanEndNum() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                true, false, true, 3, 63, testFile1.toFile().getPath()
        );
        String expectedResult = "4218: Software Testing\n" +
                "ìš mödülè cövèrs thè concepts and prãctīće of software testin\n" +
                "d regression testing. Various testing coverage criteria will \n" +
                "ot-cause of errors in failing test cases will also be investi\n" +
                "rformance prediction, performance clustering and performance \n" +
                "ucial skills on testing and debugging through hands-on assign\n";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingCharPosAndNumRangeAndValidSingleFileWithStartNumHigherThanEndNum() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                true, false, true, 18, 17, testFile1.toFile().getPath()
        );
        String expectedResult = "\n" + "\n" + "\n" + "\n" + "\n" + "\n";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingCharPosAndSingleNumAndValidSingleFile() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                true, false, false, 103, 103, testFile1.toFile().getPath()
        );
        String expectedResult = "\n" + "e\n" + "d\n" + "f\n" + "l\n" + "\n";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingBytePosAnd2CommaSeparatedNumAndValidDistinctFilesWithStartNumLowerThanEndNum() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                false, true, false, 10, 12,
                testFile1.toFile().getPath(), testFile2.toFile().getPath()
        );
        String expectedResult = "ot\n" + "öü\n" + "si\n" + "eo\n" + "c \n" + "kl\n" + "u \n" +
                "\n" + "us\n" + "\n" + "sa";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingBytePosAnd2CommaSeparatedNumAndValidDistinctFilesWithStartNumHigherThanEndNum() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                false, true, false, 115, 1,
                testFile1.toFile().getPath(), testFile2.toFile().getPath(), testFile3.toFile().getPath()
        );
        String expectedResult = "";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingBytePosAndNumRangeAndValidDistinctFilesWithStartNumLowerThanEndNum() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                false, true, true, 1, 11,
                testFile1.toFile().getPath(), testFile2.toFile().getPath(), testFile3.toFile().getPath()
        );
        String expectedResult = "CS4218: Sof\n" + "Thìš möd\n" + "and regress\n" + "root-cause \n" + "performance\n" + "crucial ski\n" +
                "Lorem ipsum\n" + "\n" + "Euismod qui\n" + "\n" + "Turpis mass\n" +
                "1.0, 5.0\n" + "2, 3\n" + "51, 15\n" + "21, 4\n" + "22, 41\n" + "551, 1200\n" + "001, 010";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingBytePosAndNumRangeAndValidDistinctFilesWithStartNumHigherThanEndNum() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                false, true, true, 9, 1,
                testFile1.toFile().getPath(), testFile3.toFile().getPath()
        );
        String expectedResult = "\n" + "\n" + "\n" + "\n" + "\n" +
                "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingBytePosAndSingleNumAndValidDistinctFiles() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                false, true, true, 9, 9,
                testFile1.toFile().getPath(), testFile3.toFile().getPath()
        );
        String expectedResult = "\n" + "e\n" + "c\n" +
                " \n" + "i\n" + "\n" + "\n" +
                "\n" + "\n" + "\n" + "\n" + "\n" + "";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingCharPosAnd2CommaSeparatedNumAndValidDistinctFilesWithStartNumLowerThanEndNum() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                true, false, false, 1, 274,
                testFile2.toFile().getPath(), testFile3.toFile().getPath()
        );
        String expectedResult = "Ls\n" + "\n" + "Ed\n" + "\n" + "Tn\n" +
                "1\n" + "2\n" + "5\n" + "2\n" + "2\n" + "5\n" + "0";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingCharPosAnd2CommaSeparatedNumAndValidDistinctFilesWithStartNumHigherThanEndNum() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                true, false, false, 274, 1,
                testFile2.toFile().getPath(), testFile3.toFile().getPath()
        );
        String expectedResult = "Ls\n" + "\n" + "Ed\n" + "\n" + "Tn\n" +
                "1\n" + "2\n" + "5\n" + "2\n" + "2\n" + "5\n" + "0";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingCharPosAndNumRangeAndValidDistinctFilesWithStartNumLowerThanEndNum() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                true, false, true, 1, 200,
                testFile2.toFile().getPath(), testFile1.toFile().getPath()
        );
        String expectedResult = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Aliquam malesuada bibendum arcu vitae. Nam libero justo laoreet sit amet cur\n" +
                "\n" +
                "Euismod quis viverra nibh cras pulvinar mattis nunc. Nam libero justo laoreet sit amet cursus sit amet dictum. Auctor augue mauris augue neque gravida in fermentum et. Nunc eget lorem dolor sed viverr\n" +
                "\n" +
                "Turpis massa tincidunt dui ut ornare lectus sit. Phasellus egestas tellus rutrum tellus pellentesque eu tincidunt tortor aliquam. Est velit egestas dui id ornare arcu odio ut sem. Facilisi nullam vehi\n" +
                "CS4218: Software Testing\n" +
                "Thìš mödülè cövèrs thè concepts and prãctīće of software testing including unït testing, integration testing,\n" +
                "and regression testing. Various testing coverage criteria will be discussed. Debugging methods for finding the\n" +
                "root-cause of errors in failing test cases will also be investigated. The use öf testing and analysis for\n" +
                "performance prediction, performance clustering and performance debugging will be studied. Students will acquire\n" +
                "crucial skills on testing and debugging through hands-on assignments.";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingCharPosAndNumRangeAndValidDistinctFilesWithStartNumHigherThanEndNum() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                true, false, true, 200, 1,
                testFile2.toFile().getPath(), testFile1.toFile().getPath()
        );
        String expectedResult = "\n" + "\n" + "\n" + "\n" + "\n" +
                "\n" + "\n" + "\n" + "\n" + "\n" + "";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingCharPosAndSingleNumAndValidDistinctFiles() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                true, false, true, 250, 250,
                testFile2.toFile().getPath(), testFile1.toFile().getPath()
        );
        String expectedResult = "t\n" + "\n" + "a\n" + "\n" + "i\n" + "\n" +
                "\n" + "\n" + "\n" + "\n" + "";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingBytePosAnd2CommaSeparatedNumAndFileIsDashWithStartNumLowerThanEndNum() { }

    @Test
    void testCutFromFilesUsingBytePosAnd2CommaSeparatedNumAndFileIsDashWithStartNumHigherThanEndNum() { }

    @Test
    void testCutFromFilesUsingBytePosAndNumRangeAndFileIsDashWithStartNumLowerThanEndNum() { }

    @Test
    void testCutFromFilesUsingBytePosAndNumRangeAndFileIsDashWithStartNumHigherThanEndNum() { }

    @Test
    void testCutFromFilesUsingBytePosAndSingleNumAndFileIsDash() { }

    @Test
    void testCutFromFilesUsingCharPosAnd2CommaSeparatedNumAndFileIsDashWithStartNumLowerThanEndNum() { }

    @Test
    void testCutFromFilesUsingCharPosAnd2CommaSeparatedNumAndFileIsDashWithStartNumHigherThanEndNum() { }

    @Test
    void testCutFromFilesUsingCharPosAndNumRangeAndFileIsDashWithStartNumLowerThanEndNum() { }

    @Test
    void testCutFromFilesUsingCharPosAndNumRangeAndFileIsDashWithStartNumHigherThanEndNum() { }

    @Test
    void testCutFromFilesUsingCharPosAndSingleNumAndFileIsDash() { }

    @Test
    void testCutFromFilesUsingBytePosAnd2CommaSeparatedNumAndValidSimilarFilesWithStartNumLowerThanEndNum() { }

    @Test
    void testCutFromFilesUsingBytePosAnd2CommaSeparatedNumAndValidSimilarFilesWithStartNumHigherThanEndNum() { }

    @Test
    void testCutFromFilesUsingBytePosAndNumRangeAndValidSimilarFilesWithStartNumLowerThanEndNum() { }

    @Test
    void testCutFromFilesUsingBytePosAndNumRangeAndValidSimilarFilesWithStartNumHigherThanEndNum() { }

    @Test
    void testCutFromFilesUsingBytePosAndSingleNumAndValidSimilarFiles() { }

    @Test
    void testCutFromFilesUsingCharPosAnd2CommaSeparatedNumAndValidSimilarFilesWithStartNumLowerThanEndNum() { }

    @Test
    void testCutFromFilesUsingCharPosAnd2CommaSeparatedNumAndValidSimilarFilesWithStartNumHigherThanEndNum() { }

    @Test
    void testCutFromFilesUsingCharPosAndNumRangeAndValidSimilarFilesWithStartNumLowerThanEndNum() { }

    @Test
    void testCutFromFilesUsingCharPosAndNumRangeAndValidSimilarFilesWithStartNumHigherThanEndNum() { }

    @Test
    void testCutFromFilesUsingCharPosAndSingleNumAndValidSimilarFiles() { }

    @Test
    void testCutFromFilesUsingBytePosAnd2CommaSeparatedNumAndHavingDashBetweenMultipleFilesWithStartNumLowerThanEndNum() { }

    @Test
    void testCutFromFilesUsingBytePosAnd2CommaSeparatedNumAndHavingDashBetweenMultipleFilesWithStartNumHigherThanEndNum() { }

    @Test
    void testCutFromFilesUsingBytePosAndNumRangeAndHavingDashBetweenMultipleFilesWithStartNumLowerThanEndNum() { }

    @Test
    void testCutFromFilesUsingBytePosAndNumRangeAndHavingDashBetweenMultipleFilesWithStartNumHigherThanEndNum() { }

    @Test
    void testCutFromFilesUsingBytePosAndSingleNumAndHavingDashBetweenMultipleFiles() { }

    @Test
    void testCutFromFilesUsingCharPosAnd2CommaSeparatedNumAndHavingDashBetweenMultipleFilesWithStartNumLowerThanEndNum() { }

    @Test
    void testCutFromFilesUsingCharPosAnd2CommaSeparatedNumAndHavingDashBetweenMultipleFilesWithStartNumHigherThanEndNum() { }

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
        Throwable thrown =
                assertThrows(CutException.class, () -> cutApplication.cutFromStdin(
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
    void testCutFromStdinUsingBytePosAnd2CommaSeparatedNumAndValidInputStreamWithStartNumHigherThanEndNum() { }

    @Test
    void testCutFromStdinUsingBytePosAndNumRangeAndValidInputStreamWithStartNumLowerThanEndNum() { }

    @Test
    void testCutFromStdinUsingBytePosAndNumRangeAndValidInputStreamWithStartNumHigherThanEndNum() { }

    @Test
    void testCutFromStdinUsingBytePosAndSingleNumAndValidInputStream() { }

    @Test
    void testCutFromStdinUsingCharPosAnd2CommaSeparatedNumAndValidInputStreamWithStartNumLowerThanEndNum() { }

    @Test
    void testCutFromStdinUsingCharPosAnd2CommaSeparatedNumAndValidInputStreamWithStartNumHigherThanEndNum() { }

    @Test
    void testCutFromStdinUsingCharPosAndNumRangeAndValidInputStreamWithStartNumLowerThanEndNum() { }

    @Test
    void testCutFromStdinUsingCharPosAndNumRangeAndValidInputStreamWithStartNumHigherThanEndNum() { }

    @Test
    void testCutFromStdinUsingCharPosAndSingleNumAndValidInputStream() { }
}