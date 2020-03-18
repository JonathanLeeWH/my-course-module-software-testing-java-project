package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.app.WcInterface;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.impl.util.TestFileUtils;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;

@SuppressWarnings({"PMD.AvoidDuplicateLiterals"})
public class WcApplicationTest {
    private WcApplication wcApplication;
    private InputStream ourTestStdin;
    private OutputStream ourTestStdout;
    private static final String TEST_STDIN_MSG_1 = "11"+  System.lineSeparator() +
            "1 test 1 2" +  System.lineSeparator() + "5" + System.lineSeparator() + "+" + System.lineSeparator();
    private final Path testFile1 = Paths.get(TestFileUtils.TESTDATA_DIR + "test1.txt");
    private final Path testFile2 = Paths.get(TestFileUtils.TESTDATA_DIR + "test2.txt");
    private final Path testFile3 = Paths.get(TestFileUtils.TESTDATA_DIR + "test3.csv");

    @BeforeEach
    void setUp() {
        wcApplication = new WcApplication();
        ourTestStdin = new ByteArrayInputStream(TEST_STDIN_MSG_1.getBytes());
        ourTestStdout = new ByteArrayOutputStream();
    }

    @AfterEach
    public void tearDown() throws IOException {
        ourTestStdin.close();
        ourTestStdout.close();
    }

    /**
     * Test cases with countFromFiles().
     */
    // Error test cases
    @Test
    void testCountFromFilesWithNullFileNameShouldThrowException() {
        Throwable thrown = assertThrows(Exception.class, () -> wcApplication.countFromFiles(
                false, false, false,
                ((String[]) null)
        ));
        assertEquals(thrown.getMessage(), ERR_GENERAL);
    }

    @Test
    void testCountFromFilesUsingASingleFileWithFileHasNoReadAccessShouldThrowException() {
        WcApplicationStubWithFileHasNoReadAccess wcApplicationStub = new WcApplicationStubWithFileHasNoReadAccess();
        Throwable thrown = assertThrows(Exception.class, () -> wcApplicationStub.countFromFiles(
                true, false, false,
                testFile1.toFile().getPath()
        ));

        assertEquals(thrown.getMessage(), ERR_NO_PERM);
    }

    // Single test cases
    @Test
    void testCountFromFilesUsingASingleFileWithFileNotFoundInDirShouldRunSuccessfullyAndShowErrorMessage() throws Exception {
        String actualResult = wcApplication.countFromFiles(
                false, false, false,
                "no-file.txt"
        );
        String expectedResult = "wc: " + ERR_FILE_NOT_FOUND;
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCountFromFilesUsingMultipleFilesWithAtLeastOneFileNotFoundInDirShouldRunSuccessfullyAndShowErrorMessage() throws Exception {
        String actualResult = wcApplication.countFromFiles(
                false, false, false,
                testFile3.toFile().toString(), "no-file.txt"
        );
        String expectedResult = String.format(" %7d %7d %7d", 7, 14, 60) + " " + testFile3.toFile().getPath() + System.lineSeparator()
                + "wc: " + ERR_FILE_NOT_FOUND + System.lineSeparator()
                + String.format(" %7d %7d %7d", 7, 14, 60) + " total";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCountFromFilesUsingASingleFileWithFilenameIsADirShouldRunSuccessfullyAndShowErrorMessage() throws Exception {
        String actualResult = wcApplication.countFromFiles(
                true, false, false,
                TestFileUtils.TESTDATA_DIR
        );
        String expectedResult = "wc: " + ERR_IS_DIR;
        assertEquals(expectedResult, actualResult);
    }

    // Positive test cases
    @Test
    void testCountFromFilesWithNoFlagArgsAndASingleFileShouldRunSuccessfully() throws Exception {
        String actualResult = wcApplication.countFromFiles(
                false, false, false,
                testFile1.toFile().toString()
        );
        String expectedResult = String.format(" %7d %7d %7d", 6, 73, 553) + " " + testFile1.toFile().getPath();
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCountFromFilesWithNoFlagArgsAndAllValidSimilarFilesShouldRunSuccessfully() throws Exception {
        String actualResult = wcApplication.countFromFiles(
                false, false, false,
                testFile3.toFile().toString(), testFile3.toFile().toString()
        );
        String expectedResult = String.format(" %7d %7d %7d", 7, 14, 60) + " " + testFile3.toFile().getPath() + System.lineSeparator()
                + String.format(" %7d %7d %7d", 7, 14, 60) + " " + testFile3.toFile().getPath() + System.lineSeparator()
                + String.format(" %7d %7d %7d", 14, 28, 120) + " total";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCountFromFilesWithNoFlagArgsAndAllValidDistinctFilesShouldRunSuccessfully() throws Exception {
        String actualResult = wcApplication.countFromFiles(
                false, false, false,
                testFile2.toFile().toString(), testFile1.toFile().toString()
        );
        String expectedResult = String.format(" %7d %7d %7d", 5, 320, 2081) + " " + testFile2.toFile().getPath() + System.lineSeparator()
                + String.format(" %7d %7d %7d", 6, 73, 553) + " " + testFile1.toFile().getPath() + System.lineSeparator()
                + String.format(" %7d %7d %7d", 11, 393, 2634) + " total";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCountFromFilesWithIsBytesArgOnlyAndASingleFileShouldRunSuccessfully() throws Exception {
        String actualResult = wcApplication.countFromFiles(
                true, false, false,
                testFile3.toFile().toString()
        );
        String expectedResult = String.format(" %7d", 60) + " " + testFile3.toFile().getPath();
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCountFromFilesWithIsBytesArgOnlyAndAllValidSimilarFilesShouldRunSuccessfully() throws Exception {
        String actualResult = wcApplication.countFromFiles(
                true, false, false,
                testFile1.toFile().toString(), testFile1.toFile().toString()
        );
        String expectedResult = String.format(" %7d", 553) + " " + testFile1.toFile().getPath() + System.lineSeparator()
                + String.format(" %7d", 553) + " " + testFile1.toFile().getPath() + System.lineSeparator()
                + String.format(" %7d", 1106) + " total";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCountFromFilesWithIsBytesArgOnlyAndAllValidDistinctFilesShouldRunSuccessfully() throws Exception {
        String actualResult = wcApplication.countFromFiles(
                true, false, false,
                testFile3.toFile().toString(), testFile1.toFile().toString()
        );
        String expectedResult = String.format(" %7d", 60) + " " + testFile3.toFile().getPath() + System.lineSeparator()
                + String.format(" %7d", 553) + " " + testFile1.toFile().getPath() + System.lineSeparator()
                + String.format(" %7d", 613) + " total";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCountFromFilesWithIsLinesArgOnlyAndASingleFileShouldRunSuccessfully() throws Exception {
        String actualResult = wcApplication.countFromFiles(
                false, true, false,
                testFile3.toFile().toString()
        );
        String expectedResult = String.format(" %7d", 7) + " " + testFile3.toFile().getPath();
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCountFromFilesWithIsLinesArgOnlyAndAllValidSimilarFilesShouldRunSuccessfully() throws Exception {
        String actualResult = wcApplication.countFromFiles(
                false, true, false,
                testFile2.toFile().toString(), testFile2.toFile().toString()
        );
        String expectedResult = String.format(" %7d", 5) + " " + testFile2.toFile().getPath() + System.lineSeparator()
                + String.format(" %7d", 5) + " " + testFile2.toFile().getPath() + System.lineSeparator()
                + String.format(" %7d", 10) + " total";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCountFromFilesWithIsLinesArgOnlyAndAllValidDistinctFilesShouldRunSuccessfully() throws Exception {
        String actualResult = wcApplication.countFromFiles(
                false, true, false,
                testFile3.toFile().toString(), testFile1.toFile().toString()
        );
        String expectedResult = String.format(" %7d", 7) + " " + testFile3.toFile().getPath() + System.lineSeparator()
                + String.format(" %7d", 6) + " " + testFile1.toFile().getPath() + System.lineSeparator()
                + String.format(" %7d", 13) + " total";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCountFromFilesWithIsWordsArgOnlyAndASingleFileShouldRunSuccessfully() throws Exception {
        String actualResult = wcApplication.countFromFiles(
                false, false, true,
                testFile2.toFile().toString()
        );
        String expectedResult = String.format(" %7d", 320) + " " + testFile2.toFile().getPath();
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCountFromFilesWithIsWordsArgOnlyAndAllValidSimilarFilesShouldRunSuccessfully() throws Exception {
        String actualResult = wcApplication.countFromFiles(
                false, false, true,
                testFile3.toFile().toString(), testFile3.toFile().toString()
        );
        String expectedResult = String.format(" %7d", 14) + " " + testFile3.toFile().getPath() + System.lineSeparator()
                + String.format(" %7d", 14) + " " + testFile3.toFile().getPath() + System.lineSeparator()
                + String.format(" %7d", 28) + " total";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCountFromFilesWithIsWordsArgOnlyAndAllValidDistinctFilesShouldRunSuccessfully() throws Exception {
        String actualResult = wcApplication.countFromFiles(
                false, false, true,
                testFile1.toFile().toString(), testFile2.toFile().toString()
        );
        String expectedResult = String.format(" %7d", 73) + " " + testFile1.toFile().getPath() + System.lineSeparator()
                + String.format(" %7d", 320) + " " + testFile2.toFile().getPath() + System.lineSeparator()
                + String.format(" %7d", 393) + " total";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCountFromFilesWithIsBytesAndIsLinesArgAndASingleFileShouldRunSuccessfully() throws Exception {
        String actualResult = wcApplication.countFromFiles(
                true, true, false,
                testFile2.toFile().toString()
        );
        String expectedResult = String.format(" %7d %7d", 5, 2081) + " " + testFile2.toFile().getPath();
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCountFromFilesWithIsBytesAndIsLinesArgAndAllValidSimilarFilesShouldRunSuccessfully() throws Exception {
        String actualResult = wcApplication.countFromFiles(
                true, true, false,
                testFile1.toFile().toString(), testFile1.toFile().toString()
        );
        String expectedResult = String.format(" %7d %7d", 6, 553) + " " + testFile1.toFile().getPath() + System.lineSeparator()
                + String.format(" %7d %7d", 6, 553) + " " + testFile1.toFile().getPath() + System.lineSeparator()
                + String.format(" %7d %7d", 12, 1106) + " total";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCountFromFilesWithIsBytesAndIsLinesArgAndAllValidDistinctFilesShouldRunSuccessfully() throws Exception {
        String actualResult = wcApplication.countFromFiles(
                true, true, false,
                testFile2.toFile().toString(), testFile3.toFile().toString()
        );
        String expectedResult = String.format(" %7d %7d", 5, 2081) + " " + testFile2.toFile().getPath() + System.lineSeparator()
                + String.format(" %7d %7d", 7, 60) + " " + testFile3.toFile().getPath() + System.lineSeparator()
                + String.format(" %7d %7d", 12, 2141) + " total";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCountFromFilesWithIsBytesAndIsWordsArgAndASingleFileShouldRunSuccessfully() throws Exception {
        String actualResult = wcApplication.countFromFiles(
                true, false, true,
                testFile3.toFile().toString()
        );
        String expectedResult = String.format(" %7d %7d", 14, 60) + " " + testFile3.toFile().getPath();
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCountFromFilesWithIsBytesAndIsWordsArgAndAllValidSimilarFilesShouldRunSuccessfully() throws Exception {
        String actualResult = wcApplication.countFromFiles(
                true, false, true,
                testFile2.toFile().toString(), testFile2.toFile().toString()
        );
        String expectedResult = String.format(" %7d %7d", 320, 2081) + " " + testFile2.toFile().getPath() + System.lineSeparator()
                + String.format(" %7d %7d", 320, 2081) + " " + testFile2.toFile().getPath() + System.lineSeparator()
                + String.format(" %7d %7d", 640, 4162) + " total";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCountFromFilesWithIsBytesAndIsWordsArgAndAllValidDistinctFilesShouldRunSuccessfully() throws Exception {
        String actualResult = wcApplication.countFromFiles(
                true, false, true,
                testFile2.toFile().toString(), testFile3.toFile().toString()
        );
        String expectedResult = String.format(" %7d %7d", 320, 2081) + " " + testFile2.toFile().getPath() + System.lineSeparator()
                + String.format(" %7d %7d", 14, 60) + " " + testFile3.toFile().getPath() + System.lineSeparator()
                + String.format(" %7d %7d", 334, 2141) + " total";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCountFromFilesWithIsLinesAndIsWordsArgAndASingleFileShouldRunSuccessfully() throws Exception {
        String actualResult = wcApplication.countFromFiles(
                false, true, true,
                testFile2.toFile().toString()
        );
        String expectedResult = String.format(" %7d %7d", 5, 320) + " " + testFile2.toFile().getPath();
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCountFromFilesWithIsLinesAndIsWordsArgAndAllValidSimilarFilesShouldRunSuccessfully() throws Exception {
        String actualResult = wcApplication.countFromFiles(
                false, true, true,
                testFile2.toFile().toString(), testFile2.toFile().toString()
        );
        String expectedResult = String.format(" %7d %7d", 5, 320) + " " + testFile2.toFile().getPath() + System.lineSeparator()
                + String.format(" %7d %7d", 5, 320) + " " + testFile2.toFile().getPath() + System.lineSeparator()
                + String.format(" %7d %7d", 10, 640) + " total";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCountFromFilesWithIsLinesAndIsWordsArgAndAllValidDistinctFilesShouldRunSuccessfully() throws Exception {
        String actualResult = wcApplication.countFromFiles(
                false, true, true,
                testFile1.toFile().toString(), testFile3.toFile().toString()
        );
        String expectedResult = String.format(" %7d %7d", 6, 73) + " " + testFile1.toFile().getPath() + System.lineSeparator()
                + String.format(" %7d %7d", 7, 14) + " " + testFile3.toFile().getPath() + System.lineSeparator()
                + String.format(" %7d %7d", 13, 87) + " total";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCountFromFilesWithIsBytesAndIsLinesAndIsWordsArgAndASingleFileShouldRunSuccessfully() throws Exception {
        String actualResult = wcApplication.countFromFiles(
                true, true, true,
                testFile1.toFile().toString()
        );
        String expectedResult = String.format(" %7d %7d %7d", 6, 73, 553) + " " + testFile1.toFile().getPath();
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCountFromFilesWithIsBytesAndIsLinesAndIsWordsArgAndAllValidSimilarFilesShouldRunSuccessfully() throws Exception {
        String actualResult = wcApplication.countFromFiles(
                true, true, true,
                testFile2.toFile().toString(), testFile2.toFile().toString()
        );
        String expectedResult = String.format(" %7d %7d %7d", 5, 320, 2081) + " " + testFile2.toFile().getPath() + System.lineSeparator()
                + String.format(" %7d %7d %7d", 5, 320, 2081) + " " + testFile2.toFile().getPath() + System.lineSeparator()
                + String.format(" %7d %7d %7d", 10, 640, 4162) + " total";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCountFromFilesWithIsBytesAndIsLinesAndIsWordsArgAndAllValidDistinctFilesShouldRunSuccessfully() throws Exception {
        String actualResult = wcApplication.countFromFiles(
                true, true, true,
                testFile1.toFile().toString(), testFile2.toFile().toString(), testFile3.toFile().toString()
        );
        String expectedResult = String.format(" %7d %7d %7d", 6, 73, 553) + " " + testFile1.toFile().getPath() + System.lineSeparator()
                + String.format(" %7d %7d %7d", 5, 320, 2081) + " " + testFile2.toFile().getPath() + System.lineSeparator()
                + String.format(" %7d %7d %7d", 7, 14, 60) + " " + testFile3.toFile().getPath() + System.lineSeparator()
                + String.format(" %7d %7d %7d", 18, 407, 2694) + " total";
        assertEquals(expectedResult, actualResult);
    }

    /**
     * Test cases with countFromStdin().
     */
    // Error test cases
    @Test
    void testCountFromStdinWithNullInputStreamShouldThrowException() {
        Throwable thrown = assertThrows(Exception.class, () -> wcApplication.countFromStdin(
                false, false, false, null
        ));
        assertEquals(thrown.getMessage(), ERR_NULL_STREAMS);
    }

    // Single test cases
    @Test
    void testCountFromStdinWithEmptyInputStreamShouldRunSuccessfully() throws Exception {
        String actualResult = wcApplication.countFromStdin(
                false, false, false,
                new ByteArrayInputStream(new byte[0])
        );
        String expectedResult = String.format(" %7d %7d %7d", 0, 0, 0);
        assertEquals(expectedResult, actualResult);
    }

    // Positive test cases
    @Test
    void testCountFromStdinWithNoFlagArgsAndValidStandardInputShouldRunSuccessfully() throws Exception {
        String actualResult = wcApplication.countFromStdin(
                false, false, false,
                ourTestStdin
        );
        String expectedResult = String.format(" %7d %7d %7d", 4, 7, 22);
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCountFromStdinWithIsBytesArgOnlyAndValidStandardInputShouldRunSuccessfully() throws Exception {
        String actualResult = wcApplication.countFromStdin(
                true, false, false,
                ourTestStdin
        );
        String expectedResult = String.format(" %7d", 22);
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCountFromStdinWithIsLinesArgOnlyAndValidStandardInputShouldRunSuccessfully() throws Exception {
        String actualResult = wcApplication.countFromStdin(
                false, true, false,
                ourTestStdin
        );
        String expectedResult = String.format(" %7d", 4);
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCountFromStdinWithIsWordsArgOnlyAndValidStandardInputShouldRunSuccessfully() throws Exception {
        String actualResult = wcApplication.countFromStdin(
                false, false, true,
                ourTestStdin
        );
        String expectedResult = String.format(" %7d", 7);
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCountFromStdinWithIsBytesAndIsLinesArgAndValidStandardInputShouldRunSuccessfully() throws Exception {
        String actualResult = wcApplication.countFromStdin(
                true, true, false,
                ourTestStdin
        );
        String expectedResult = String.format(" %7d %7d", 4, 22);
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCountFromStdinWithIsBytesAndIsWordsArgAndValidStandardInputShouldRunSuccessfully() throws Exception {
        String actualResult = wcApplication.countFromStdin(
                true, false, true,
                ourTestStdin
        );
        String expectedResult = String.format(" %7d %7d", 7, 22);
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCountFromStdinWithIsLinesAndIsWordsArgAndValidStandardInputShouldRunSuccessfully() throws Exception {
        String actualResult = wcApplication.countFromStdin(
                false, true, true,
                ourTestStdin
        );
        String expectedResult = String.format(" %7d %7d", 4, 7);
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCountFromStdinWithIsBytesAndIsLinesAndIsWordsArgAndValidStandardInputShouldRunSuccessfully() throws Exception {
        String actualResult = wcApplication.countFromStdin(
                true, true, true,
                ourTestStdin
        );
        String expectedResult = String.format(" %7d %7d %7d", 4, 7, 22);
        assertEquals(expectedResult, actualResult);
    }

    /**
     * Test cases with getCountReport().
     */
    // Error test cases
    @Test
    void testGetCountReportWithNullInputStreamShouldThrowException() {
        Throwable thrown = assertThrows(Exception.class, () -> wcApplication.getCountReport(null));
        assertEquals(thrown.getMessage(), ERR_NULL_STREAMS);
    }

    // Positive test cases


    private class WcApplicationStub implements WcInterface {
        @Override
        public String countFromFiles(Boolean isBytes, Boolean isLines, Boolean isWords, String... fileName) throws Exception {
            throw new AssertionError("This method should not be implemented");
        }

        @Override
        public String countFromStdin(Boolean isBytes, Boolean isLines, Boolean isWords, InputStream stdin) {
            throw new AssertionError("This method should not be implemented");
        }

        @Override
        public void run(String[] args, InputStream stdin, OutputStream stdout) {
            throw new AssertionError("This method should not be implemented");
        }
    }

    private class WcApplicationStubWithFileHasNoReadAccess extends WcApplicationStub {
        @Override
        public String countFromFiles(Boolean isBytes, Boolean isLines, Boolean isWords, String... fileName) throws Exception {
            throw new Exception(ERR_NO_PERM);
        }
    }
}
