package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.*;
import sg.edu.nus.comp.cs4218.exception.DiffException;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_NULL_STREAMS;

class DiffApplicationTest {

    private static final String FILE_ONE_TEXT = "Same line" + System.lineSeparator() + "Different line";
    private static final String FILE_TWO_TEXT = "Same line" + System.lineSeparator() + "Different line";
    private static final String FILE_THREE_TEXT = "Same line" + System.lineSeparator() + "Same line";
    private static final String FILE_ONE_NAME = "fileOne";
    private static final String FILE_TWO_NAME = "fileTwo";
    private static final String FILE_THREE_NAME = "fileThree";
    private static final String FILE_FORMAT = ".txt";
    private static final String SAME_OUTPUT = "Files are identical";
    private static final String DIFF_OUTPUT = "The two files are different";
    private static final String DIFF_EXCEPTION = "diff: ";
    private static DiffApplication diffApplication;
    private static File fileOne;
    private static File fileTwo;
    private static File fileThree;
    private static InputStream stdinOne;
    private static OutputStream stdoutOne, stdoutTwo, stdoutThree;
    private static boolean isShowSame, isNoBlank, isSimple;

    @BeforeEach
    void setUp() throws Exception {
        diffApplication = new DiffApplication();
        fileOne = File.createTempFile(FILE_ONE_NAME, FILE_FORMAT);
        fileTwo = File.createTempFile(FILE_TWO_NAME, FILE_FORMAT);
        fileThree = File.createTempFile(FILE_THREE_NAME, FILE_FORMAT);
        stdinOne = new FileInputStream(fileOne);
        stdoutOne = new ByteArrayOutputStream();
        stdoutTwo = new ByteArrayOutputStream();
        stdoutThree = new ByteArrayOutputStream();
    }

    @AfterEach
    void tearDown() {
        fileOne.deleteOnExit();
        fileTwo.deleteOnExit();
        fileThree.deleteOnExit();
    }

    /**
     * Run Null Stdout and null Stdin in run method with valid filenames in args.
     * Exception: Throw DiffException
     */
    @Test
    void testRunWhenBothStdinAndStdoutAreNullShouldThrowDiffException() {
        String[] args = {FILE_ONE_NAME + FILE_FORMAT, FILE_TWO_NAME + FILE_FORMAT};
        Exception thrown = assertThrows(DiffException.class, () -> {
            diffApplication.run(args, null, null);
        });
        String expected = DIFF_EXCEPTION + ERR_NULL_STREAMS;
        assertEquals(expected, thrown.getMessage());
    }

    /**
     * Run Null Stdin in run method with valid filenames in args.
     * Exception: Throw DiffException
     */
    @Test
    void testRunWhenOnlyStdinIsNullShouldThrowDiffException() {
        String[] args = {FILE_ONE_NAME + FILE_FORMAT, FILE_TWO_NAME + FILE_FORMAT};
        assertThrows(DiffException.class, () -> {
            diffApplication.run(args, null, stdoutOne);
        });
    }

    /**
     * Run Null Stdout in run method with valid filenames in args.
     * Exception: Throw DiffException
     */
    @Test
    void testRunWhenOnlyStdoutIsNullShouldThrowDiffException() {
        InputStream inputStream = new ByteArrayInputStream(fileOne.toPath().toString().getBytes());
        String[] args = {FILE_ONE_NAME + FILE_FORMAT, FILE_TWO_NAME + FILE_FORMAT};
        assertThrows(DiffException.class, () -> {
            diffApplication.run(args, inputStream, null);
        });
    }

    @Test
    void runNullArgsShouldThrowDiffException() throws Exception {
        InputStream inputStream = new ByteArrayInputStream(fileOne.toPath().toString().getBytes());
        assertThrows(DiffException.class, () -> {
            diffApplication.run(null, inputStream, stdoutOne);
        });
    }

    /**
     * Test run method with args that has dash that comes after filename.
     * The underlying assumption is that stdin should come before any filenames.
     * Exception: Throw DiffException
     */
    @Test
    void testRunStdinInSecondArgumentShouldThrowDiffException() {
        String[] args = {FILE_ONE_NAME + FILE_FORMAT, "-"};
        InputStream inputStream = new ByteArrayInputStream(fileOne.toPath().toString().getBytes());
        assertThrows(DiffException.class, () -> {
            diffApplication.run(args, inputStream, stdoutOne);
        });
    }

    /**
     * Test run method with only one filename supplied in arg.
     * Expected: Throw DiffException
     */
    @Test
    void testRunOnlyOneArgShouldThrowDiffException() {
        String[] args = {FILE_ONE_NAME + FILE_FORMAT};
        InputStream inputStream = new ByteArrayInputStream(fileOne.getPath().getBytes());
        assertThrows(DiffException.class, () -> {
            diffApplication.run(args, inputStream, stdoutOne);
        });
    }

    /**
     * Test run method with only one filename supplied in arg. -s is also supplied in arg.
     * Expected: Throw DiffException
     */
    @Test
    void testRunOnlyOneArgInStdinWithDashSShouldThrowDiffException() {
        String[] args = {"-s", FILE_ONE_NAME + FILE_FORMAT};
        InputStream inputStream = new ByteArrayInputStream(fileOne.getPath().getBytes());
        assertThrows(DiffException.class, () -> {
            diffApplication.run(args, inputStream, stdoutOne);
        });
    }

    /**
     * Test run method with only one filename supplied in arg. -B is also supplied in arg.
     * Expected: Throw DiffException
     */
    @Test
    void testRunOnlyOneArgWithDashBShouldThrowDiffException() {
        String[] args = {"-B", FILE_ONE_NAME + FILE_FORMAT};
        InputStream inputStream = new ByteArrayInputStream(fileOne.getPath().getBytes());
        assertThrows(DiffException.class, () -> {
            diffApplication.run(args, inputStream, stdoutOne);
        });
    }


    /**
     * Test run method with only one filename supplied in arg. -q is also supplied in arg.
     * Expected: Throw DiffException
     */
    @Test
    void testRunOnlyOneArgWithDashQShouldThrowDiffException() {
        String[] args = {"-q", FILE_ONE_NAME + FILE_FORMAT};
        InputStream inputStream = new ByteArrayInputStream(fileOne.getPath().getBytes());
        assertThrows(DiffException.class, () -> {
            diffApplication.run(args, inputStream, stdoutOne);
        });
    }

    /**
     * Test run method when two identical files are provided when -s is given.
     * Expected: Print message that says both files are identical.
     */
    @Test
    void testRunTwoSameFilesShouldReturnSameOutputMessageWhenDashSIsProvided() throws Exception {
        String[] args = {"-s", FILE_ONE_NAME + FILE_FORMAT, FILE_ONE_NAME + FILE_FORMAT};
        diffApplication.run(args, stdinOne, stdoutOne);
        assertEquals(SAME_OUTPUT, stdoutOne.toString());
    }

    /**
     * Test run method when two different files that have the same content are provided when -s is given.
     * Expected: Print message that says both files are identical.
     */
    @Test
    void testRunTwoFilesWithIdenticalContentShouldReturnSameOutputMessageWhenDashSIsProvided() throws Exception {
        String[] args = {"-s", FILE_ONE_NAME + FILE_FORMAT, FILE_TWO_NAME + FILE_FORMAT};
        diffApplication.run(args, stdinOne, stdoutOne);
        assertEquals(SAME_OUTPUT, stdoutOne.toString());
    }

    /**
     * Test run method when two identical files are provided when -q is given.
     * Expected: Print nothing
     */
    @Test
    void testRunTwoIdenticalFilesShouldReturnNothingWhenDashQIsGiven() throws Exception {
        String[] args = {"-q", FILE_ONE_NAME + FILE_FORMAT, FILE_ONE_NAME + FILE_FORMAT};
        diffApplication.run(args, stdinOne, stdoutOne);
        assertEquals("", stdinOne.toString());
    }

    /**
     * Test run method when two different files that have the same content are provided when -q is given.
     * Expected: Print nothing
     */
    @Test
    void testRunTwoDifferentFilesWithSameContentShouldReturnNothingWhenDashQIsGiven() throws Exception {
        String[] args = {"-q", FILE_ONE_NAME, FILE_TWO_NAME};
        diffApplication.run(args, stdinOne, stdoutOne);
        assertEquals("", diffApplication.diffTwoFiles(FILE_ONE_NAME, FILE_TWO_NAME, false,
                true, true));
    }

    /**
     * Test run method when two different files that have the different content are provided when -q is given.
     * Expected: Print message that states that the files are different.
     */
    @Test
    void testRunDiffTwoFilesMethodWithTwoFilesThatHaveDifferentContentsShouldReturnDiffMessage() throws Exception {
        assertEquals(DIFF_OUTPUT, diffApplication.diffTwoFiles(FILE_ONE_NAME, FILE_THREE_NAME, true,
                true, true));
    }

    /**
     * Test run method when two identical files are provided when no flags are given.
     * Expected: Print nothing.
     */
    @Test
    void runTwoSameFilesWithoutAnyFlagsShouldPrintNothing() throws Exception {
        InputStream inputStream = new FileInputStream("fileOne.txt");
        String[] args = {FILE_ONE_NAME + FILE_FORMAT, FILE_ONE_NAME + FILE_FORMAT};
        diffApplication.run(args, inputStream, stdoutOne);
        assertEquals("", stdoutOne.toString());
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
        String fileNameA = "fileA.txt";
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
        String fileNameB = "fileB.txt";
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
        String fileNameA = "fileA.txt";
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
        String fileNameB = "fileB.txt";
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
        String folderA = "folderA";
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
        String folderB = "folderB";
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
        String folderA = "folderA";
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
        String folderB = "folderB";
        assertThrows(DiffException.class, () -> {
            diffApplication.diffTwoDir("", folderB, true, true, true);
        });
    }

    // Test diffFileAndStdin
    @Test
    void testRunNullFileNameAndValidStdinInDiffFileAndStdinMethodShouldThrowException() throws FileNotFoundException {
        String fileNameA = "fileA.txt";
        InputStream inputStream = new FileInputStream("fileOne.txt");
        assertThrows(DiffException.class, () -> {
            diffApplication.diffFileAndStdin(null, inputStream, true, true, true);
        });
    }

    @Test
    void testRunNullStdinAndValidFileNameInDiffFileAndStdinShouldThrowException() {
        assertThrows(DiffException.class, () -> {
            diffApplication.diffFileAndStdin(FILE_ONE_NAME + FILE_FORMAT, null, true, true, true);
        });
    }

    @Test
    void testRunNullFileNameAndNullStdinInDiffFileAndStdinMethodShouldThrowException() throws FileNotFoundException {
        assertThrows(DiffException.class, () -> {
            diffApplication.diffFileAndStdin(null, null, true, true, true);
        });
    }
}