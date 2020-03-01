package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.*;
import sg.edu.nus.comp.cs4218.exception.DiffException;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

class DiffApplicationTest {
    private static final String SAME_LINE = "Same line";
    private static final String FILE_ONE_TEXT = SAME_LINE + System.lineSeparator() + "Different line";
    private static final String FILE_TWO_TEXT = SAME_LINE + System.lineSeparator() + "Different line";
    private static final String FILE_THREE_TEXT = SAME_LINE + System.lineSeparator() + SAME_LINE;
    private static final String FILE_ONE_NAME = "fileOne";
    private static final String FILE_TWO_NAME = "fileTwo";
    private static final String FILE_THREE_NAME = "fileThree";
    private static final String FILE_FORMAT = ".txt";
    private static final String SAME_OUTPUT = "Files are identical";
    private static final String DIFF_OUTPUT = "The two files are different";
    private static final String DIFF_LINES = "<line2" + System.lineSeparator() + ">line2";
    private static final String DIFF_EXCEPTION = "diff: ";
    private static DiffApplication diffApplication;
    private static File fileOne;
    private static File fileTwo;
    private static File fileThree;
    private static InputStream stdinOne;
    private static OutputStream stdoutOne, stdoutTwo, stdoutThree, osPrint;
    private static boolean isShowSame, isNoBlank, isSimple;

    @BeforeEach
    void setUp() throws Exception {
        diffApplication = new DiffApplication();
        fileOne = File.createTempFile(FILE_ONE_NAME, FILE_FORMAT);
        fileTwo = File.createTempFile(FILE_TWO_NAME, FILE_FORMAT);
        fileThree = File.createTempFile(FILE_THREE_NAME, FILE_FORMAT);
        stdinOne = new FileInputStream(fileOne);
        stdoutOne = new FileOutputStream(fileTwo);
        stdoutTwo = new FileOutputStream(fileOne);
        stdoutThree = new FileOutputStream(fileThree);
        osPrint = new ByteArrayOutputStream();
        stdoutOne.write(FILE_ONE_TEXT.getBytes());
        stdoutTwo.write(FILE_TWO_TEXT.getBytes());
        stdoutThree.write(FILE_THREE_TEXT.getBytes());
    }

    @AfterEach
    void tearDown() throws IOException {
        fileOne.deleteOnExit();
        fileTwo.deleteOnExit();
        fileThree.deleteOnExit();
        stdinOne.close();
        stdoutOne.close();
        stdoutTwo.close();
        stdoutThree.close();
    }

    // Test diffTwoFiles Method

    /**
     * Test diffTwoFiles method when both files are null.
     * Expected: Throw Diff Exception.
     */
    @Test
    void runDiffTwoFilesMethodWithAllNullFileNamesShouldThrowException() {
        assertThrows(DiffException.class, () -> {
            diffApplication.diffTwoFiles(null, null, true, true, true);
        });
    }

    /**
     * Test diffTwoFiles method when fileName B null.
     * Expected: Throw Diff Exception.
     */
    @Test
    void runDiffTwoFilesMethodWithFileNameBNullShouldThrowException() {
        String fileNameA = fileOne.getPath();
        assertThrows(DiffException.class, () -> {
            diffApplication.diffTwoFiles(fileNameA, null, true, true, true);
        });
    }

    /**
     * Test diffTwoFiles method when fileName A null.
     * Expected: Throw Diff Exception.
     */
    @Test
    void runDiffTwoFilesMethodWithFileNameANullShouldThrowException() {
        String fileNameB = fileTwo.getPath();
        assertThrows(DiffException.class, () -> {
            diffApplication.diffTwoFiles(null, fileNameB, true, true, true);
        });
    }

    /**
     * Test diffTwoFiles method when fileNameB is empty.
     * Expected: Throw Diff Exception.
     */
    @Test
    void testRunDiffTwoFilesWithFileNameBEmptyShouldThrowException() {
        String fileNameA = fileOne.getPath();
        assertThrows(DiffException.class, () -> {
            diffApplication.diffTwoFiles(fileNameA, "", true, true, true);
        });
    }

    /**
     * Test diffTwoFiles method when fileNameA is empty.
     * Expected: Throw Diff Exception.
     */
    @Test
    void testRunDiffTwoFilesWithFileNameAEmptyShouldThrowException() {
        String fileNameB = fileTwo.getPath();
        assertThrows(DiffException.class, () -> {
            diffApplication.diffTwoFiles("", fileNameB, true, true, true);
        });
    }

    /**
     * Test diffTwoFiles method when fileNameA and fileNameB are empty.
     * Expected: Throw Diff Exception.
     */
    @Test
    void testRunDiffTwoFilesWithFileNameAAndBAreEmptyShouldThrowException() {
        assertThrows(DiffException.class, () -> {
            diffApplication.diffTwoFiles("", "", true, true, true);
        });
    }

    /**
     * Test diffTwoFiles method when fileNameA and fileNameB are the same file and isShowSame is true.
     * Expected: Print same output.
     */
    @Test
    void testRunDiffTwoFilesWhenBothFilesAAndBAreTheSameFilesAndIsShowSameIsTrue() throws Exception {
        assertEquals(SAME_OUTPUT, diffApplication.diffTwoFiles(fileTwo.getPath(), fileTwo.getPath(), true, true, true));
    }

    /**
     * Test diffTwoFiles method when fileNameA and fileNameB are the same file and isShowSame is false.
     * Expected: Print nothing.
     */
    @Test
    void testRunDiffTwoFilesWhenBothFilesAAndBAreTheSameFilesAndIsShowSameIsFalse() throws Exception {
        assertEquals("", diffApplication.diffTwoFiles(fileTwo.getPath(), fileTwo.getPath(), false, true, true));
    }


    /**
     * Test diffTwoFiles method when fileNameA and fileNameB are the different files but contents are the same
     * and isShowSame is true.
     * Expected: Print same output.
     */
    @Test
    void testRunDiffTwoFilesWhenBothFilesAAndBAreDifferentFilesWithSameContentAndIsShowSameIsTrue() throws Exception {
        assertEquals(SAME_OUTPUT, diffApplication.diffTwoFiles(fileTwo.getPath(), fileTwo.getPath(), false, true, true));
    }

    /**
     * Test diffTwoFiles method when fileNameA and fileNameB are the different files but contents are the same
     * and isShowSame is false.
     * Expected: Print same output.
     */
    @Test
    void testRunDiffTwoFilesWhenBothFilesAAndBAreDifferentFilesWithSameContentAndIsShowSameIsFalse() throws Exception {
        assertEquals("", diffApplication.diffTwoFiles(fileTwo.getPath(), fileTwo.getPath(), false, true, true));
    }

    /**
     * Test diffTwoFiles method when fileNameA and fileNameB are the different files with different contents in the second line.
     * and isSimple is true.
     * Expected: Print the lines that differ.
     */
    @Test
    void testRunDiffTwoFilesWhenBothFilesAAndBAreDifferentFilesWithSameContentAndIsSimpleTrue() throws Exception {
        assertEquals(DIFF_OUTPUT, diffApplication.diffTwoFiles(fileTwo.getPath(), fileTwo.getPath(), true, true, true));
    }

    /**
     * Test diffTwoFiles method when fileNameA and fileNameB are the different files with different contents in the second line.
     * and isSimple is false.
     * Expected: Print the lines that differ.
     */
    @Test
    void testRunDiffTwoFilesWhenBothFilesAAndBAreDifferentFilesWithSameContentAndIsSimpleFalse() throws Exception {
        assertEquals(DIFF_LINES, diffApplication.diffTwoFiles(fileTwo.getPath(), fileTwo.getPath(), true, true, false));
    }

    // Test diffTwoDir Method
    /**
     * Test diffTwoDir method when folderA and folderB are null.
     * Expected: Throw Diff Exception.
     */
    @Test
    void testRunDiffTwoDirMethodWhenDirectoryAAndDirectoryBAreNullShouldThrowException() {
        assertThrows(DiffException.class, () -> {
            diffApplication.diffTwoDir(null, null, true, true, true);
        });
    }

    /**
     * Test diffTwoDir method when folderB are null.
     * Expected: Throw Diff Exception.
     */
    @Test
    void testRunDiffTwoDirMethodWhenDirectoryBIsNullShouldThrowException() {
        String folderA = "/folderA";
        assertThrows(DiffException.class, () -> {
            diffApplication.diffTwoDir(folderA, null, true, true, true);
        });
    }

    /**
     * Test diffTwoDir method when folderA are null.
     * Expected: Throw Diff Exception.
     */
    @Test
    void testRunDiffTwoDirMethodWhenDirectoryAIsNullShouldThrowException() {
        String folderB = "/folderB";
        assertThrows(DiffException.class, () -> {
            diffApplication.diffTwoDir(null, folderB, true, true, true);
        });
    }

    /**
     * Test diffTwoDir method when folderB are empty.
     * Expected: Throw Diff Exception.
     */
    @Test
    void testRunDiffTwoDirMethodWhenFolderBIsEmptyShouldThrowException() {
        String folderA = "/folderA";
        assertThrows(DiffException.class, () -> {
            diffApplication.diffTwoDir(folderA, "", true, true, true);
        });
    }

    /**
     * Test diffTwoDir method when folderA are empty.
     * Expected: Throw Diff Exception.
     */
    @Test
    void testRunDiffTwoDirMethodWhenFolderAIsEmptyShouldThrowException() {
        String folderB = "/folderB";
        assertThrows(DiffException.class, () -> {
            diffApplication.diffTwoDir("", folderB, true, true, true);
        });
    }

    // Test diffFileAndStdin
    /**
     * Test diffFileAndStdin method when fileName is null.
     * Expected: Throw Diff Exception.
     */
    @Test
    void testRunNullFileNameAndValidStdinInDiffFileAndStdinMethodShouldThrowException() throws FileNotFoundException {
        InputStream inputStream = new ByteArrayInputStream(fileOne.toPath().toString().getBytes());
        assertThrows(DiffException.class, () -> {
            diffApplication.diffFileAndStdin(null, inputStream, true, true, true);
        });
    }

    /**
     * Test diffFileAndStdin method when stdin is null.
     * Expected: Throw Diff Exception.
     */
    @Test
    void testRunNullStdinAndValidFileNameInDiffFileAndStdinShouldThrowException() {
        assertThrows(DiffException.class, () -> {
            diffApplication.diffFileAndStdin(fileOne.toPath().toString(), null, true, true, true);
        });
    }

    /**
     * Test diffFileAndStdin method when stdin is null and filename is null.
     * Expected: Throw Diff Exception.
     */
    @Test
    void testRunDiffFileAndStdinMethodWhenNullFileNameAndNullStdinShouldThrowException() {
        assertThrows(DiffException.class, () -> {
            diffApplication.diffFileAndStdin(null, null, true, true, true);
        });
    }

    /**
     * Test diffFileAndStdin method when stdin is null and filename is null.
     * Expected: Throw Diff Exception.
     */
    @Test
    void testRunDiffFileAndStdinMethodWhenFileNameAndStdinHaveIdenticalFilesWithIsShowSameTrueShouldPrintSameOutputMessage() {
        InputStream inputStream = new ByteArrayInputStream(fileOne.toPath().toString().getBytes());
        assertThrows(DiffException.class, () -> {
            diffApplication.diffFileAndStdin(fileOne.toPath().toString(), inputStream, true, true, true);
        });
    }

    /**
     * Test diffFileAndStdin method when stdin is null and filename is null.
     * Expected: Throw Diff Exception.
     */
    @Test
    void testRunDiffFileAndStdinMethodWhenFileNameAndStdinHaveDifferentFilesWithSameContentAndIsShowSameTrueShouldPrintSameOutputMessage() throws Exception {
        InputStream inputStream = new ByteArrayInputStream(fileTwo.toPath().toString().getBytes());
        assertEquals(SAME_OUTPUT, diffApplication.diffFileAndStdin(fileOne.toPath().toString(), inputStream, true, true, true));
    }

    /**
     * Test diffFileAndStdin method when files provided by fileName and stdin are different in content.
     * Boolean isSimple is false for this case.
     * Expected: Print the line numbers of both files that are different.
     */
    @Test
    void testRunDiffFileAndStdinMethodWhenFileNameAndStdinHaveDifferentFilesWithDifferentContentAndIsSimpleFalseShouldPrintDifference() throws Exception {
        InputStream inputStream = new ByteArrayInputStream(fileThree.toPath().toString().getBytes());
        assertEquals(DIFF_LINES, diffApplication.diffFileAndStdin(fileOne.toPath().toString(), inputStream, true, true, false));
    }

    /**
     * Test diffFileAndStdin method when files provided by fileName and stdin are different in content.
     * Boolean isSimple is true for this case.
     * Expected: Print the line numbers of both files that are different.
     */
    @Test
    void testRunDiffFileAndStdinMethodWhenFileNameAndStdinHaveDifferentFilesWithDifferentContentAndIsSimpleTrueShouldPrintDifference() throws Exception {
        InputStream inputStream = new ByteArrayInputStream(fileThree.toPath().toString().getBytes());
        assertEquals(DIFF_OUTPUT, diffApplication.diffFileAndStdin(fileOne.toPath().toString(), inputStream, true, true, false));

    }

    /**
     * Test diffFileAndStdin method when stdin is null and filename is null.
     * Expected: Throw Diff Exception.
     */
    @Test
    void testRunDiffFileAndStdinMethodWhenFileNameAndStdinHaveDifferentFilesWithDifferentContentAndIsShowSameTrueShouldPrintDifference() {
        InputStream inputStream = new ByteArrayInputStream(fileTwo.toPath().toString().getBytes());
        assertThrows(DiffException.class, () -> {
            diffApplication.diffFileAndStdin(fileOne.toPath().toString(), inputStream, true, true, true);
        });
    }
}