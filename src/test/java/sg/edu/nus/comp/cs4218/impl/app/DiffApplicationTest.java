package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.*;
import sg.edu.nus.comp.cs4218.EnvironmentHelper;
import sg.edu.nus.comp.cs4218.exception.DiffException;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_INVALID_ARGS;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_SPACE;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;

class diffAppTest {
    private static DiffApplication diffApp;
    private static final String DIFF_EXCEPTION = "diff: ";
    private static final String IDENTICAL = " are identical";
    private static final String FILES = "Files ";
    private static final String DIFFFOLDER = "DiffTestFolder/";
    private static final String ORIGINAL_DIR = EnvironmentHelper.currentDirectory;
    private static final Path DIRECTORY = Paths.get("src", "test", "java", "sg", "edu", "nus", "comp", "cs4218", "testdata", "DiffTestFolder");
    private static final String ABSOLUTE_PATH = DIRECTORY.toFile().getAbsolutePath();
    private static OutputStream stdout;

    private static final String DIFF1_FILE = ABSOLUTE_PATH + "/diff1.txt";
    private static final String DIFF1_FILENAME = "diff1.txt";
    private static final String DIFF1_IDENTICAL_FILE = ABSOLUTE_PATH + "/diff1-identical.txt"; // NOPMD
    private static final String DIFF1_IDENTICAL_FILENAME = "diff1-identical.txt"; // NOPMD
    private static final String DIFF1_BLANK_LINES_FILE = ABSOLUTE_PATH + "/diff1-blank-lines.txt"; // NOPMD
    private static final String DIFF1_BLANK_LINES_FILENAME = "diff1-blank-lines.txt"; // NOPMD
    private static final String DIFF2_FILE = ABSOLUTE_PATH + "/diff2.txt";
    private static final String DIFF2_FILENAME = "diff2.txt";

    private static final String DIFFDIR1 = ABSOLUTE_PATH + "/diffDir1";
    private static final String DIFFDIR1NAME = "diffDir1";
    private static final String DIFFDIR1_IDENTICAL = ABSOLUTE_PATH + "/diffDir1-identical"; // NOPMD
    private static final String DIFFDIR1NAME_IDENTICAL = "diffDir1-identical"; // NOPMD
    private static final String DIFFDIR2 = ABSOLUTE_PATH + "/diffDir2";
    private static final String DIFFDIR2NAME = "diffDir2";

    private static final String DIFFBIN1 = ABSOLUTE_PATH + "/diff1Bin.bin";
    private static final String DIFFBIN1NAME = "diff1Bin.bin";
    private static final String DIFFBIN2 = ABSOLUTE_PATH + "/diff2Bin.bin";
    private static final String DIFFBIN2NAME = "diff2Bin.bin";
    private static final String DIFFBIN3 = ABSOLUTE_PATH + "/diff3Bin.bin";
    private static final String DIFFBIN3NAME = "diff3Bin.bin";

    @BeforeEach
    void setUp() throws Exception {
        diffApp = new DiffApplication();
        stdout = new ByteArrayOutputStream();
    }

    @AfterEach
    void tearDown() throws IOException {
        stdout.flush();
        EnvironmentHelper.currentDirectory = ORIGINAL_DIR;
    }

    @AfterAll
    static void reset() {
        EnvironmentHelper.currentDirectory = ORIGINAL_DIR;
    }

    // Test diffTwoFiles Method

    /**
     * Test diffTwoFiles method when both files are null.
     * Expected: Throw Diff Exception.
     */
    @Test
    void testDiffTwoFilesMethodWithAllNullFileNamesShouldThrowException() {
        assertThrows(DiffException.class, () -> {
            diffApp.diffTwoFiles(null, null, true, true, true);
        });
    }

    /**
     * Test diffTwoFiles method when fileName B null.
     * Expected: Throw Diff Exception.
     */
    @Test
    void testDiffTwoFilesMethodWithFileNameBNullShouldThrowException() {
        assertThrows(DiffException.class, () -> {
            diffApp.diffTwoFiles(DIFF1_FILENAME, null, true, true, true);
        });
    }

    /**
     * Test diffTwoFiles method when fileName A null.
     * Expected: Throw Diff Exception.
     */
    @Test
    void runDiffTwoFilesMethodWithFileNameANullShouldThrowException() {
        assertThrows(DiffException.class, () -> {
            diffApp.diffTwoFiles(null, DIFF2_FILENAME, true, true, true);
        });
    }

    /**
     * Test diffTwoFiles method when fileNameB is empty.
     * Expected: Throw Diff Exception.
     */
    @Test
    void testDiffTwoFilesWithFileNameBEmptyShouldThrowException() {
        assertThrows(DiffException.class, () -> {
            diffApp.diffTwoFiles(DIFF1_FILENAME, "", true, true, true);
        });
    }

    /**
     * Test diffTwoFiles method when fileNameA is empty.
     * Expected: Throw Diff Exception.
     */
    @Test
    void testDiffTwoFilesWithFileNameAEmptyShouldThrowException() {
        assertThrows(DiffException.class, () -> {
            diffApp.diffTwoFiles("", DIFF2_FILENAME, true, true, true);
        });
    }

    /**
     * Test diffTwoFiles method when fileNameA and fileNameB are empty.
     * Expected: Throw Diff Exception.
     */
    @Test
    void testDiffTwoFilesWithFileNameAAndBAreEmptyShouldThrowException() {
        assertThrows(DiffException.class, () -> {
            diffApp.diffTwoFiles("", "", true, true, true);
        });
    }

    @Test
    void testDiffTwoFilesMethodWithSameFilesAndDashSShouldReturnIdenticalMessage() throws DiffException {
        String actualOutput = diffApp.diffTwoFiles(DIFF1_FILE, DIFF1_FILE, true, false, false);
        String expected = "Files " + DIFFFOLDER + DIFF1_FILENAME + CHAR_SPACE + DIFFFOLDER + DIFF1_FILENAME + " are identical";
        assertEquals(expected, actualOutput);
    }

    @Test
    void testDiffTwoFilesWithDirectories() {
        assertThrows(DiffException.class, () -> {
            diffApp.diffTwoFiles(DIFFDIR1, DIFFDIR2, true, true, true);
        });
    }

    @Test
    void testDiffTwoFilesMethodWithDifferentFilesAndDashQShouldReturnDiffMessage() throws DiffException {
        String actualOutput = diffApp.diffTwoFiles(DIFF1_FILE, DIFF2_FILE, false, false, true);
        String expected = "Files " + DIFF1_FILENAME + CHAR_SPACE + DIFF2_FILENAME + " differ";
        assertEquals(expected, actualOutput);
    }

    /**
     * Test diffTwoFiles method when fileNameA and fileNameB are the same file and isShowSame is false.
     * Expected: Print nothing.
     */
    @Test
    void testDiffTwoFilesWhenBothFilesAAndBAreTheSameFilesAndIsShowSameIsFalse() throws Exception {
        assertEquals("", diffApp.diffTwoFiles(DIFF1_FILE, DIFF1_IDENTICAL_FILE, false, false, false));
    }

    /**
     * Test diffTwoFiles method when fileNameA and fileNameB are the different files but contents are the same
     * and isShowSame is true.
     * Expected: Print same output.
     */
    @Test
    void testDiffTwoFilesWhenBothFilesAAndBAreDifferentFilesWithDifferentContentShouldPrintDifference() throws Exception {
        String expected = "< test A" + STRING_NEWLINE +
                "< test B" + STRING_NEWLINE +
                "< test C" + STRING_NEWLINE +
                "> test D" + STRING_NEWLINE +
                "> test E" + STRING_NEWLINE +
                "> test F";
        assertEquals(expected, diffApp.diffTwoFiles(DIFF1_FILE, DIFF2_FILE, false, false, false));
    }

    @Test
    void testDiffTwoFilesWhenBothFilesAreDifferentFilesWithSameContentWithoutBlanksAndIsNoBlankTrue() throws Exception {
        assertEquals("", diffApp.diffTwoFiles(DIFF1_FILE, DIFF1_BLANK_LINES_FILE, false, true, false));
    }


    // Test diffTwoDir Method

    /**
     * Test diffTwoDir method when folderA and folderB are null.
     * Expected: Throw Diff Exception.
     */
    @Test
    void testDiffTwoDirMethodWhenDirectoryAAndDirectoryBAreNullShouldThrowException() {
        assertThrows(DiffException.class, () -> {
            diffApp.diffTwoDir(null, null, true, true, true);
        });
    }

    /**
     * Test diffTwoDir method when folderB are null.
     * Expected: Throw Diff Exception.
     */
    @Test
    void testRunDiffTwoDirMethodWhenDirectoryBIsNullShouldThrowException() {
        assertThrows(DiffException.class, () -> {
            diffApp.diffTwoDir(DIFFDIR1, null, true, true, true);
        });
    }

    /**
     * Test diffTwoDir method when folderA are null.
     * Expected: Throw Diff Exception.
     */
    @Test
    void testDiffTwoDirMethodWhenDirectoryAIsNullShouldThrowException() {
        assertThrows(DiffException.class, () -> {
            diffApp.diffTwoDir(null, DIFFDIR2, true, true, true);
        });
    }

    /**
     * Test diffTwoDir method when folderB are empty.
     * Expected: Throw Diff Exception.
     */
    @Test
    void testDiffTwoDirMethodWhenFolderBIsEmptyShouldThrowException() {
        assertThrows(DiffException.class, () -> {
            diffApp.diffTwoDir(DIFFDIR1, "", true, true, true);
        });
    }

    /**
     * Test diffTwoDir method when folderA are empty.
     * Expected: Throw Diff Exception.
     */
    @Test
    void testDiffTwoDirMethodWhenFolderAIsEmptyShouldThrowException() {
        assertThrows(DiffException.class, () -> {
            diffApp.diffTwoDir("", DIFFDIR2, true, true, true);
        });
    }

    @Test
    void testDiffTwoDirMethodWithTwoDirectoriesThatAreDifferentWithIsShowSame() throws DiffException {
        String expected = "Only in diffDir1: diff1-identical.txt" + STRING_NEWLINE +
                "Only in diffDir2: diff2.txt";
        String expectedTwo = FILES + DIFFDIR1NAME + "/" +  DIFF1_FILENAME + CHAR_SPACE + DIFFDIR2NAME + "/" + DIFF1_FILENAME + IDENTICAL;;
        String actualOutput = diffApp.diffTwoDir(DIFFDIR1, DIFFDIR2, true, true, true);
        assertTrue(actualOutput.contains(expected));
        assertTrue(actualOutput.contains(expectedTwo));
    }

    // Test diffFileAndStdin

    /**
     * Test diffFileAndStdin method when fileName is null.
     * Expected: Throw Diff Exception.
     */
    @Test
    void testNullFileNameAndValidStdinInDiffFileAndStdinMethodShouldThrowException() throws FileNotFoundException {
        try {
            InputStream inputStream = new FileInputStream(new File(DIFF1_FILE)); //NOPMD
            assertThrows(DiffException.class, () -> {
                diffApp.diffFileAndStdin(null, inputStream, true, true, true);
            });
        } catch (Exception e) {
            fail("should not fail: " + e.getMessage());
        }
    }

    /**
     * Test diffFileAndStdin method when stdin is null.
     * Expected: Throw Diff Exception.
     */
    @Test
    void testNullStdinAndValidFileNameInDiffFileAndStdinShouldThrowException() {
        assertThrows(DiffException.class, () -> {
            diffApp.diffFileAndStdin(DIFF1_FILE, null, true, true, true);
        });
    }

    /**
     * Test diffFileAndStdin method when stdin is null and filename is null.
     * Expected: Throw Diff Exception.
     */
    @Test
    void testDiffFileAndStdinMethodWhenNullFileNameAndNullStdinShouldThrowException() {
        assertThrows(DiffException.class, () -> {
            diffApp.diffFileAndStdin(null, null, true, true, true);
        });
    }

    @Test
    void testDiffFileAndStdinMethodWhenFileNameAndStdinHaveIdenticalFilesWithIsShowSameTrueShouldPrintSameOutputMessage() {
        try {
            InputStream inputStream = new FileInputStream(new File(DIFF1_FILE)); //NOPMD
            String expected = "Files " + DIFF1_FILENAME + CHAR_SPACE + "-" + " are identical";
            assertEquals(expected, diffApp.diffFileAndStdin(DIFF1_FILE, inputStream, true, true, true));

        } catch (Exception e) {
            fail("should not fail: " + e.getMessage());
        }
    }

    @Test
    void testRunDiffFileAndStdinMethodWhenFileNameAndStdinHaveDifferentFilesWithSameContentAndIsShowSameTrueShouldPrintSameOutputMessage() throws Exception {
        try {
            InputStream inputStream = new FileInputStream(new File(DIFF1_IDENTICAL_FILE)); //NOPMD
            String expected = "Files " + DIFF1_FILENAME + CHAR_SPACE + "-" + " are identical";
            assertEquals(expected, diffApp.diffFileAndStdin(DIFF1_FILE, inputStream, true, true, true));
        } catch (Exception e) {
            fail("should not fail: " + e.getMessage());
        }
    }

    /**
     * Test diffFileAndStdin method when files provided by fileName and stdin are different in content.
     * Boolean isSimple is false for this case.
     * Expected: Print the line numbers of both files that are different.
     */
    @Test
    void testDiffFileAndStdinMethodWhenFileNameAndStdinHaveDifferentFilesWithDifferentContentAndIsSimpleFalseShouldPrintDifference() throws Exception {
        try {
            InputStream inputStream = new FileInputStream(new File(DIFF2_FILE)); //NOPMD
            String expected = "< test A" + STRING_NEWLINE +
                    "< test B" + STRING_NEWLINE +
                    "< test C" + STRING_NEWLINE +
                    "> test D" + STRING_NEWLINE +
                    "> test E" + STRING_NEWLINE +
                    "> test F";
            assertEquals(expected, diffApp.diffFileAndStdin(DIFF1_FILE, inputStream, false, false, false));
        } catch (Exception e) {
            fail("should not fail: " + e.getMessage());
        }
    }

    /**
     * Test diffFileAndStdin method when stdin is null and filename is null.
     * Expected: Throw Diff Exception.
     */
    @Test
    void testDiffFileAndStdinMethodWhenFileNameAndStdinHaveDifferentFilesWithDifferentContentAndIsShowSameTrueShouldPrintDifference() {
        try {
            InputStream inputStream = new FileInputStream(new File(DIFF2_FILE)); //NOPMD
            String expected = "< test A" + STRING_NEWLINE +
                    "< test B" + STRING_NEWLINE +
                    "< test C" + STRING_NEWLINE +
                    "> test D" + STRING_NEWLINE +
                    "> test E" + STRING_NEWLINE +
                    "> test F";
            assertEquals(expected, diffApp.diffFileAndStdin(DIFF1_FILE, inputStream, true, false, false));
        } catch (Exception e) {
            fail("should not fail: " + e.getMessage());
        }
    }
}