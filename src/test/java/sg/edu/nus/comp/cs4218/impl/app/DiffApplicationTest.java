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
     * Run Null Stdout in run method with valid filenames
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

    @Test
    void testRunWhenOnlyStdinIsNullShouldThrowDiffException() {
        String[] args = {FILE_ONE_NAME + FILE_FORMAT, FILE_TWO_NAME + FILE_FORMAT};
        assertThrows(DiffException.class, () -> {
            diffApplication.run(args, null, stdoutOne);
        });
    }

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

    @Test
    void runTwoSameFilesShouldOutputNoDiffMessage() throws Exception {
        InputStream inputStream = new FileInputStream("fileOne.txt");
        String[] args = {"-s", FILE_ONE_NAME, FILE_ONE_NAME};
        diffApplication.run(args, inputStream, stdoutOne);
        assertEquals(SAME_OUTPUT, stdoutOne.toString());
    }

    // Test diffTwoFiles Method

    @Test
    void runNullFilesNamesShouldThrowException() {
        assertThrows(DiffException.class, () -> {
            diffApplication.diffTwoFiles(null, null, true, true, true);
        });
    }

    @Test
    void runOneNullFileShouldThrowException() {
        String fileNameA = "fileA.txt";
        assertThrows(DiffException.class, () -> {
            diffApplication.diffTwoFiles(fileNameA, null, true, true, true);
        });
    }

    @Test
    void runOneEmptyFileShouldThrowException() {
        String fileNameA = "fileA.txt";
        assertThrows(DiffException.class, () -> {
            diffApplication.diffTwoFiles(fileNameA, "", true, true, true);
        });
    }

    // Test diffTwoDir Method

    @Test
    void runNullDirectoryShouldThrowException() {
        assertThrows(DiffException.class, () -> {
            diffApplication.diffTwoDir(null, null, true, true, true);
        });
    }

    @Test
    void runOneNullDirectoryShouldThrowException() {
        String folderA = "folderA";
        assertThrows(DiffException.class, () -> {
            diffApplication.diffTwoDir(folderA, null, true, true, true);
        });
    }

    @Test
    void runOneEmptyDirectoryShouldThrowException() {
        String folderA = "folderA";
        assertThrows(DiffException.class, () -> {
            diffApplication.diffTwoDir(folderA, "", true, true, true);
        });
    }

    // Test diffFileAndStdin
    @Test
    void runNullFileNameAndValidStdinInDiffFileAndStdinMethodShouldThrowException() throws FileNotFoundException {
        String fileNameA = "fileA.txt";
        InputStream inputStream = new FileInputStream("fileOne.txt");
        assertThrows(DiffException.class, () -> {
            diffApplication.diffFileAndStdin(null, inputStream, true, true, true);
        });
    }

    @Test
    void runNullStdinAndValidFileNameInDiffFileAndStdinShouldThrowException() {
        assertThrows(DiffException.class, () -> {
            diffApplication.diffFileAndStdin(FILE_ONE_NAME + FILE_FORMAT, null, true, true, true);
        });
    }
}