package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.exception.SortException;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;

public class SortApplicationTest {
    private SortApplication sortApplication;
    private String[] defaultSortArgs;
    private InputStream ourTestStdin;
    private OutputStream ourTestStdout;
    private static final String TEST_STDIN_MSG_1 = "1 test 1 2";
    private final Path testFile1 = Paths.get(TestFileUtils.TESTDATA_DIR + "test1.txt");
    private final Path testFile2 = Paths.get(TestFileUtils.TESTDATA_DIR + "test2.txt");
    private final Path testFile3 = Paths.get(TestFileUtils.TESTDATA_DIR + "test3.csv");

    @BeforeEach
    void setUp() {
        sortApplication = new SortApplication();
        defaultSortArgs = Arrays.asList("-n").toArray(new String[1]);
        ourTestStdin = new ByteArrayInputStream(TEST_STDIN_MSG_1.getBytes());
        ourTestStdout = new ByteArrayOutputStream();
    }

    @AfterEach
    public void tearDown() throws IOException {
        ourTestStdin.close();
        ourTestStdout.close();
    }

    /**
     * Test cases with run().
     */
    // Error test cases
    @Test
    void testRunNullOutputStream() {
        Throwable thrown = assertThrows(SortException.class, () -> sortApplication.run(defaultSortArgs, ourTestStdin, null));
        assertEquals(thrown.getMessage(), SortApplication.COMMAND + ": " + ERR_NO_OSTREAM);
    }

    // Positive test cases
    @Test
    void testRunWithMultipleFiles() throws SortException {
        sortApplication.run(Arrays.asList("-rn", testFile3.toFile().getPath()).toArray(new String[2]), ourTestStdin, ourTestStdout);
        String expectedResult = "";
        assertEquals(expectedResult, ourTestStdout.toString());
    }

    @Test
    void testRunWithNoFiles() throws SortException {
        sortApplication.run(Collections.singletonList("-nr").toArray(new String[1]), ourTestStdin, ourTestStdout);
        String expectedResult = "";
        assertEquals(expectedResult, ourTestStdout.toString());
    }

    /**
     * Test cases with sortFromFiles().
     */
    // Error test cases
    @Test
    void testSortFromFilesWithFileNotFound() {
        Throwable thrown = assertThrows(SortException.class, () -> sortApplication.sortFromFiles(
                false, false, false,
                "no-file.txt"
        ));
        assertEquals(thrown.getMessage(), SortApplication.COMMAND + ": " + ERR_FILE_NOT_FOUND);
    }

    @Test
    void testSortFromFilesWithFilesNotFound() {
        Throwable thrown = assertThrows(SortException.class, () -> sortApplication.sortFromFiles(
                false, false, false,
                testFile3.toFile().toString(), "no-file.txt"
        ));
        assertEquals(thrown.getMessage(), SortApplication.COMMAND + ": " + ERR_FILE_NOT_FOUND);
    }

    // Positive test cases
    @Test
    void testSortFromFilesWithNoFlagArgsAndASingleFile() throws Exception {
        String actualResult = sortApplication.sortFromFiles(
                false, false, false,
                testFile1.toFile().toString()
        );
        String expectedResult = "";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testSortFromFilesWithNoFlagArgsAndAllValidSimilarFiles() throws Exception {
        String actualResult = sortApplication.sortFromFiles(
                false, false, false,
                testFile3.toFile().toString(), testFile3.toFile().toString()
        );
        String expectedResult = "";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testSortFromFilesWithNoFlagArgsAndAllValidDistinctFiles() throws Exception {
        String actualResult = sortApplication.sortFromFiles(
                false, false, false,
                testFile2.toFile().toString(), testFile1.toFile().toString()
        );
        String expectedResult = "";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testSortFromFilesWithFirstWordNumArgOnlyAndASingleFile() throws Exception {
        String actualResult = sortApplication.sortFromFiles(
                true, false, false,
                testFile3.toFile().toString()
        );
        String expectedResult = "";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testSortFromFilesWithFirstWordNumArgOnlyAndAllValidSimilarFiles() throws Exception {
        String actualResult = sortApplication.sortFromFiles(
                true, false, false,
                testFile1.toFile().toString(), testFile1.toFile().toString()
        );
        String expectedResult = "";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testSortFromFilesWithFirstWordNumArgOnlyAndAllValidDistinctFiles() throws Exception {
        String actualResult = sortApplication.sortFromFiles(
                true, false, false,
                testFile3.toFile().toString(), testFile1.toFile().toString()
        );
        String expectedResult = "";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testSortFromFilesWithReverseOrderArgOnlyAndASingleFile() throws Exception {
        String actualResult = sortApplication.sortFromFiles(
                false, true, false,
                testFile3.toFile().toString()
        );
        String expectedResult = "";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testSortFromFilesWithReverseOrderArgOnlyAndAllValidSimilarFiles() throws Exception {
        String actualResult = sortApplication.sortFromFiles(
                false, true, false,
                testFile2.toFile().toString(), testFile2.toFile().toString()
        );
        String expectedResult = "";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testSortFromFilesWithReverseOrderArgOnlyAndAllValidDistinctFiles() throws Exception {
        String actualResult = sortApplication.sortFromFiles(
                false, true, false,
                testFile3.toFile().toString(), testFile1.toFile().toString()
        );
        String expectedResult = "";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testSortFromFilesWithCaseIndependentArgOnlyAndASingleFile() throws Exception {
        String actualResult = sortApplication.sortFromFiles(
                false, false, true,
                testFile2.toFile().toString()
        );
        String expectedResult = "";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testSortFromFilesWithCaseIndependentArgOnlyAndAllValidSimilarFiles() throws Exception {
        String actualResult = sortApplication.sortFromFiles(
                false, false, true,
                testFile3.toFile().toString(), testFile3.toFile().toString()
        );
        String expectedResult = "";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testSortFromFilesWithCaseIndependentArgOnlyAndAllValidDistinctFiles() throws Exception {
        String actualResult = sortApplication.sortFromFiles(
                false, false, true,
                testFile1.toFile().toString(), testFile2.toFile().toString()
        );
        String expectedResult = "";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testSortFromFilesWithFirstWordNumAndReverseOrderArgAndASingleFile() throws Exception {
        String actualResult = sortApplication.sortFromFiles(
                true, true, false,
                testFile2.toFile().toString()
        );
        String expectedResult = "";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testSortFromFilesWithFirstWordNumAndReverseOrderArgAndAllValidSimilarFiles() throws Exception {
        String actualResult = sortApplication.sortFromFiles(
                true, true, false,
                testFile1.toFile().toString(), testFile1.toFile().toString()
        );
        String expectedResult = "";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testSortFromFilesWithFirstWordNumAndReverseOrderArgAndAllValidDistinctFiles() throws Exception {
        String actualResult = sortApplication.sortFromFiles(
                true, true, false,
                testFile2.toFile().toString(), testFile3.toFile().toString()
        );
        String expectedResult = "";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testSortFromFilesWithFirstWordNumAndCaseIndependentArgAndASingleFile() throws Exception {
        String actualResult = sortApplication.sortFromFiles(
                true, false, true,
                testFile3.toFile().toString()
        );
        String expectedResult = "";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testSortFromFilesWithFirstWordNumAndCaseIndependentArgAndAllValidSimilarFiles() throws Exception {
        String actualResult = sortApplication.sortFromFiles(
                true, false, true,
                testFile2.toFile().toString(), testFile2.toFile().toString()
        );
        String expectedResult = "";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testSortFromFilesWithFirstWordNumAndCaseIndependentArgAndAllValidDistinctFiles() throws Exception {
        String actualResult = sortApplication.sortFromFiles(
                true, false, true,
                testFile2.toFile().toString(), testFile3.toFile().toString()
        );
        String expectedResult = "";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testSortFromFilesWithReverseOrderAndCaseIndependentArgAndASingleFile() throws Exception {
        String actualResult = sortApplication.sortFromFiles(
                false, true, true,
                testFile2.toFile().toString()
        );
        String expectedResult = "";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testSortFromFilesWithReverseOrderAndCaseIndependentArgAndAllValidSimilarFiles() throws Exception {
        String actualResult = sortApplication.sortFromFiles(
                false, true, true,
                testFile2.toFile().toString(), testFile2.toFile().toString()
        );
        String expectedResult = "";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testSortFromFilesWithReverseOrderAndCaseIndependentArgAndAllValidDistinctFiles() throws Exception {
        String actualResult = sortApplication.sortFromFiles(
                false, true, true,
                testFile1.toFile().toString(), testFile3.toFile().toString()
        );
        String expectedResult = "";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testSortFromFilesWithFirstWordNumAndReverseOrderAndCaseIndependentArgAndASingleFile() throws Exception {
        String actualResult = sortApplication.sortFromFiles(
                true, true, true,
                testFile1.toFile().toString()
        );
        String expectedResult = "";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testSortFromFilesWithFirstWordNumAndReverseOrderAndCaseIndependentArgAndAllValidSimilarFiles() throws Exception {
        String actualResult = sortApplication.sortFromFiles(
                true, true, true,
                testFile2.toFile().toString(), testFile2.toFile().toString()
        );
        String expectedResult = "";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testSortFromFilesWithFirstWordNumAndReverseOrderAndCaseIndependentArgAndAllValidDistinctFiles() throws Exception {
        String actualResult = sortApplication.sortFromFiles(
                true, true, true,
                testFile1.toFile().toString(), testFile2.toFile().toString(), testFile3.toFile().toString()
        );
        String expectedResult = "";
        assertEquals(expectedResult, actualResult);
    }

    /**
     * Test cases with sortFromStdin().
     */
    // Error test cases
    @Test
    void testSortFromStdinWithNullInputStream() {
        Throwable thrown = assertThrows(SortException.class, () -> sortApplication.sortFromStdin(
                false, false, false, null
        ));
        assertEquals(thrown.getMessage(), SortApplication.COMMAND + ": " + ERR_NULL_STREAMS);
    }

    // Single Test cases
    @Test
    void testSortFromStdinWithEmptyInputStream() throws Exception {
        String actualResult = sortApplication.sortFromStdin(
                false, false, false,
                new ByteArrayInputStream(new byte[0])
        );
        String expectedResult = "";
        assertEquals(expectedResult, actualResult);
    }

    // Positive test cases
    @Test
    void testSortFromStdinWithNoFlagArgsAndValidStandardInput() throws Exception {
        String actualResult = sortApplication.sortFromStdin(
                false, false, false,
                ourTestStdin
        );
        String expectedResult = "";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testSortFromStdinWithFirstWordNumArgOnlyAndValidStandardInput() throws Exception {
        String actualResult = sortApplication.sortFromStdin(
                true, false, false,
                ourTestStdin
        );
        String expectedResult = "";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testSortFromStdinWithReverseOrderArgOnlyAndValidStandardInput() throws Exception {
        String actualResult = sortApplication.sortFromStdin(
                false, true, false,
                ourTestStdin
        );
        String expectedResult = "";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testSortFromStdinWithCaseIndependentArgOnlyAndValidStandardInput() throws Exception {
        String actualResult = sortApplication.sortFromStdin(
                false, false, true,
                ourTestStdin
        );
        String expectedResult = "";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testSortFromStdinWithFirstWordNumAndReverseOrderArgAndValidStandardInput() throws Exception {
        String actualResult = sortApplication.sortFromStdin(
                true, true, false,
                ourTestStdin
        );
        String expectedResult = "";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testSortFromStdinWithFirstWordNumAndCaseIndependentArgAndValidStandardInput() throws Exception {
        String actualResult = sortApplication.sortFromStdin(
                true, false, true,
                ourTestStdin
        );
        String expectedResult = "";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testSortFromStdinWithReverseOrderAndCaseIndependentArgAndValidStandardInput() throws Exception {
        String actualResult = sortApplication.sortFromStdin(
                false, true, true,
                ourTestStdin
        );
        String expectedResult = "";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testSortFromStdinWithFirstWordNumAndReverseOrderAndCaseIndependentArgAndValidStandardInput() throws Exception {
        String actualResult = sortApplication.sortFromStdin(
                true, true, true,
                ourTestStdin
        );
        String expectedResult = "";
        assertEquals(expectedResult, actualResult);
    }
}
