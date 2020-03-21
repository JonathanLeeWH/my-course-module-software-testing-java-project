package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.exception.DiffException;

import java.io.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_NULL_STREAMS;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_SPACE;

public class DiffApplicationIT {
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
    private static final String DIFF_LINES = "< line2" + System.lineSeparator() + "> line2";
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
        stdoutOne = new FileOutputStream(fileOne);
        stdoutTwo = new FileOutputStream(fileTwo);
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

    /**
     * Run Null Stdout and null Stdin in run method with valid filenames in args.
     * Exception: Throw DiffException
     */
    @Test
    void testRunWhenBothStdinAndStdoutAreNullShouldThrowDiffException() {
        String[] args = {fileOne.toPath().toString(), fileTwo.toPath().toString()};
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
        String[] args = {fileOne.toPath().toString(), fileTwo.toPath().toString()};
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
        String[] args = {fileOne.toPath().toString(), fileTwo.toPath().toString()};
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
     * Test run method with only one filename supplied in arg.
     * Expected: Throw DiffException
     */
    @Test
    void testRunOnlyOneArgShouldThrowDiffException() {
        String[] args = {fileOne.toPath().toString()};
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
        String[] args = {"-s", fileOne.toPath().toString()};
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
        String[] args = {"-B", fileOne.toPath().toString()};
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
        String[] args = {"-q", fileOne.toPath().toString()};
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
        String[] args = {"-s", fileOne.toPath().toString(), fileOne.toPath().toString()};
        try {
            diffApplication.run(args, stdinOne, osPrint);
            String expected = "Files " + fileOne.getName() + CHAR_SPACE + fileOne.getName() + " are identical" + System.lineSeparator();
            assertEquals(expected, osPrint.toString());
        } catch (DiffException e) {
            e.getMessage();
        }
    }

    /**
     * Test run method when two different files that have the same content are provided when -s is given.
     * Expected: Print message that says both files are identical.
     */
    @Test
    void testRunTwoFilesWithIdenticalContentShouldReturnSameOutputMessageWhenDashSIsProvided() throws Exception {
        String[] args = {"-s", fileOne.toPath().toString(), fileTwo.toPath().toString()};
        diffApplication.run(args, stdinOne, osPrint);
        String expected = "Files " + fileOne.getName() + CHAR_SPACE + fileTwo.getName() + " are identical";
        assertEquals(expected + System.lineSeparator(), osPrint.toString());
    }

    /**
     * Test run method when two identical files are provided when -q is given.
     * Expected: Print nothing
     */
    @Test
    void testRunTwoIdenticalFilesShouldReturnNothingWhenDashQIsGiven() throws Exception {
        String[] args = {"-q", fileOne.toPath().toString(), fileOne.toPath().toString()};
        try {
            diffApplication.run(args, stdinOne, osPrint);
            assertEquals(System.lineSeparator(), osPrint.toString());
        } catch (DiffException e) {
            e.getMessage();
        }

    }

    /**
     * Test run method when two different files that have the same content are provided when -q is given.
     * Expected: Print nothing
     */
    @Test
    void testRunTwoDifferentFilesWithSameContentShouldReturnNothingWhenDashQIsGiven() throws Exception {
        String[] args = {"-q", FILE_ONE_NAME, FILE_TWO_NAME};
        try {
            assertEquals("", diffApplication.diffTwoFiles(fileOne.toPath().toString(), fileTwo.toPath().toString(), false,
                    true, true));
        } catch (DiffException e) {
            e.getMessage();
        }
    }

    /**
     * Test run method when two different files that have the different content are provided when -q is given.
     * Expected: Print message that states that the files are different.
     */
    @Test
    void testRunDiffTwoFilesMethodWithTwoFilesThatHaveDifferentContentsShouldReturnDiffMessage() throws Exception {
        String expected = "Files " + fileOne.getName() + CHAR_SPACE + fileThree.getName() + " differ";
        try {
            assertEquals(expected, diffApplication.diffTwoFiles(fileOne.toPath().toString(), fileThree.toPath().toString(), true,
                    true, true));
        } catch (DiffException e) {
            e.getMessage();
        }

    }

    /**
     * Test run method when two identical files are provided when no flags are given.
     * Expected: Print nothing.
     */
    @Test
    void runTwoSameFilesWithoutAnyFlagsShouldPrintNothing() throws Exception {
        String[] args = {fileOne.toPath().toString(), fileOne.toPath().toString()};
        try {
            diffApplication.run(args, stdinOne, osPrint);
            assertEquals(System.lineSeparator(), osPrint.toString());
        } catch(DiffException e) {
            e.getMessage();
        }
    }

}
