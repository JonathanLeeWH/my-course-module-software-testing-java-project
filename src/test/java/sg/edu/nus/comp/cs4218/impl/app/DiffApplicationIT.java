package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.EnvironmentHelper;
import sg.edu.nus.comp.cs4218.exception.DiffException;
import sg.edu.nus.comp.cs4218.impl.util.StringUtils;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_SPACE;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;

public class DiffApplicationIT {
    private static DiffApplication diffApp;
    private static final String DIFF_EXCEPTION = "diff: ";
    private static final String IDENTICAL = " are identical";
    private static final String FILES = "Files ";
    private static final String DIFFFOLDER = "DiffTestFolder/";
    private static final String ORIGINAL_DIR = EnvironmentHelper.currentDirectory;
    private static final Path DIRECTORY = Paths.get("src", "test", "java", "sg", "edu", "nus","comp", "cs4218", "testdata", "DiffTestFolder");
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
    
    /**
     * Run Null Stdout and null Stdin in run method with valid filenames in args.
     * Exception: Throw DiffException
     */
    @Test
    void testRunWhenBothStdinAndStdoutAreNullShouldThrowDiffException() {
        String[] args = {DIFF1_FILE, DIFF1_IDENTICAL_FILE};
        Exception thrown = assertThrows(DiffException.class, () -> {
            diffApp.run(args, null, null);
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
        String[] args = {DIFF1_FILE, DIFF1_IDENTICAL_FILE};
        assertThrows(DiffException.class, () -> {
            diffApp.run(args, null, stdout);
        });
    }

    /**
     * Run Null Stdout in run method with valid filenames in args.
     * Exception: Throw DiffException
     */
    @Test
    void testRunWhenOnlyStdoutIsNullShouldThrowDiffException() {
        try {
            InputStream inputStream = new FileInputStream(new File(DIFF1_FILE)); //NOPMD
            String[] args = {DIFF1_FILE, DIFF1_IDENTICAL_FILE};
            assertThrows(DiffException.class, () -> {
                diffApp.run(args, inputStream, null);
            });
        } catch (Exception e) {
            fail("should not fail: " + e.getMessage());
        }
    }

    @Test
    void runNullArgsShouldThrowDiffException() throws Exception {
        try {
            InputStream inputStream = new FileInputStream(new File(DIFF1_FILE)); //NOPMD
            assertThrows(DiffException.class, () -> {
                diffApp.run(null, inputStream, stdout);
            });
        } catch (Exception e) {
                fail("should not fail: " + e.getMessage());
        }
    }

    /**
     * Test run method with only one filename supplied in arg.
     * Expected: Throw DiffException
     */
    @Test
    void testRunOnlyOneArgShouldThrowDiffException() {
        try {
            String[] args = {DIFF1_FILE};
            InputStream inputStream = new FileInputStream(new File(DIFF1_FILE)); //NOPMD
            assertThrows(DiffException.class, () -> {
                diffApp.run(args, inputStream, stdout);
            });
        } catch (Exception e) {
                fail("should not fail: " + e.getMessage());
            }
    }

    /**
     * Test run method with only one filename supplied in arg. -s is also supplied in arg.
     * Expected: Throw DiffException
     */
    @Test
    void testRunOnlyOneArgInStdinWithDashSShouldThrowDiffException() {
        try {
            String[] args = {"-s", DIFF1_FILE};
            InputStream inputStream = new FileInputStream(new File(DIFF1_FILE)); //NOPMD
            assertThrows(DiffException.class, () -> {
                diffApp.run(args, inputStream, stdout);
            });
        } catch (Exception e) {
            fail("should not fail: " + e.getMessage());
        }
    }

    /**
     * Test run method with only one filename supplied in arg. -B is also supplied in arg.
     * Expected: Throw DiffException
     */
    @Test
    void testRunOnlyOneArgWithDashBShouldThrowDiffException() {
        try {
            String[] args = {"-B", DIFF1_FILE};
            InputStream inputStream = new FileInputStream(new File(DIFF1_FILE)); //NOPMD
            assertThrows(DiffException.class, () -> {
                diffApp.run(args, inputStream, stdout);
            });
        } catch (Exception e) {
            fail("should not fail: " + e.getMessage());
        }
    }


    /**
     * Test run method with only one filename supplied in arg. -q is also supplied in arg.
     * Expected: Throw DiffException
     */
    @Test
    void testRunOnlyOneArgWithDashQShouldThrowDiffException() {
        try {
            String[] args = {"-q", DIFF1_FILE};
            InputStream inputStream = new FileInputStream(new File(DIFF1_FILE)); //NOPMD
            assertThrows(DiffException.class, () -> {
                diffApp.run(args, inputStream, stdout);
            });
        } catch (Exception e) {
            fail("should not fail: " + e.getMessage());
        }
    }

    /**
     * Test run method when two identical files are provided when -s is given.
     * Expected: Print message that says both files are identical.
     */
    @Test
    void testRunTwoSameFilesShouldReturnSameOutputMessageWhenDashSIsProvided() throws Exception {
        String[] args = {"-s", DIFF1_FILE, DIFF1_FILE};
        try {
            InputStream inputStream = new FileInputStream(new File(DIFF1_FILE)); //NOPMD
            diffApp.run(args, inputStream, stdout);
            String expected = "Files " + DIFFFOLDER + DIFF1_FILENAME + CHAR_SPACE + DIFFFOLDER + DIFF1_FILENAME
                    + " are identical" + STRING_NEWLINE;
            assertEquals(expected, stdout.toString());
        } catch (DiffException e) {
            fail("should not fail: " + e.getMessage());
        }
    }

    /**
     * Test run method when two different files that have the same content are provided when -s is given.
     * Expected: Print message that says both files are identical.
     */
    @Test
    void testRunTwoFilesWithIdenticalContentShouldReturnSameOutputMessageWhenDashSIsProvided() throws Exception {
        String[] args = {"-s", DIFF1_FILE, DIFF1_IDENTICAL_FILE};
        try {
            InputStream inputStream = new FileInputStream(new File(DIFF1_FILE)); //NOPMD
            diffApp.run(args, inputStream, stdout);
            String expected = "Files " + DIFFFOLDER + DIFF1_FILENAME + CHAR_SPACE + DIFFFOLDER + DIFF1_IDENTICAL_FILENAME
                    + " are identical" + STRING_NEWLINE;
            assertEquals(expected, stdout.toString());
        } catch (DiffException e) {
            fail("should not fail: " + e.getMessage());
        }
    }

    /**
     * Test run method when two identical files are provided when -q is given.
     * Expected: Print nothing
     */
    @Test
    void testRunTwoIdenticalFilesShouldReturnNothingWhenDashQIsGiven() throws Exception {
        String[] args = {"-q", DIFF1_FILE, DIFF1_FILE};
        try {
            InputStream inputStream = new FileInputStream(new File(DIFF1_FILE)); //NOPMD
            diffApp.run(args, inputStream, stdout);
            assertEquals("", stdout.toString());
        } catch (DiffException e) {
            fail("should not fail: " + e.getMessage());;
        }

    }

    /**
     * Test run method when two different files that have the same content are provided when -q is given.
     * Expected: Print nothing
     */
    @Test
    void testRunTwoDifferentFilesWithSameContentShouldReturnNothingWhenDashQIsGiven() throws Exception {
        String[] args = {"-q", DIFF1_FILENAME, DIFF1_IDENTICAL_FILENAME};
        try {
            assertEquals("", diffApp.diffTwoFiles(DIFF1_FILE, DIFF1_IDENTICAL_FILE, false,
                    true, true));
        } catch (DiffException e) {
            fail("should not fail: " + e.getMessage());
        }
    }

    /**
     * Test run method when two different files that have the different content are provided when -q is given.
     * Expected: Print message that states that the files are different.
     */
    @Test
    void testRunDiffTwoFilesMethodWithTwoFilesThatHaveDifferentContentsShouldReturnDiffMessage() throws Exception {
        String expected = "Files " + DIFF1_FILENAME + CHAR_SPACE + DIFF2_FILENAME + " differ";
        try {
            assertEquals(expected, diffApp.diffTwoFiles(DIFF1_FILE, DIFF2_FILE, true,
                    true, true));
        } catch (DiffException e) {
            fail("should not fail: " + e.getMessage());
        }

    }

    /**
     * Test run method when two identical files are provided when no flags are given.
     * Expected: Print nothing.
     */
    @Test
    void runTwoSameFilesWithoutAnyFlagsShouldPrintNothing() throws Exception {
        String[] args = {DIFF1_FILE, DIFF1_FILE};
        try {
            InputStream inputStream = new FileInputStream(new File(DIFF1_FILE)); //NOPMD
            diffApp.run(args, inputStream, stdout);
            assertEquals("", stdout.toString());
        } catch(DiffException e) {
            fail("should not fail: " + e.getMessage());
        }
    }

    @Test
    void testRunWithValidStdinInSecondArgAndValidFilenameShouldReturnString() throws Exception {
        String[] args = {DIFF1_FILE, "-"};
        try {
            InputStream inputStream = new FileInputStream(new File(DIFF1_FILE)); //NOPMD
            diffApp.run(args, inputStream, stdout);
            assertEquals("", stdout.toString());
        } catch(DiffException e) {
            fail("should not fail: " + e.getMessage());
        }
    }

    @Test
    void testRunWithValidStdinInFirstArgAndValidFilenameShouldReturnString() throws Exception {
        String[] args = {"-", DIFF1_FILE};
        try {
            InputStream inputStream = new FileInputStream(new File(DIFF1_FILE)); //NOPMD
            diffApp.run(args, inputStream, stdout);
            assertEquals("", stdout.toString());
        } catch(DiffException e) {
            fail("should not fail: " + e.getMessage());
        }
    }

    @Test
    void testRunWithValidStdinInFirstArgAndValidFilenameThatHasTheDifferentFileNamesShouldReturnString() throws Exception {
        String[] args = {"-", DIFF1_FILE};
        try {
            InputStream inputStream = new FileInputStream(new File(DIFF2_FILE)); //NOPMD
            diffApp.run(args, inputStream, stdout);
            String expected = "< test A" + STRING_NEWLINE +
                    "< test B" + STRING_NEWLINE +
                    "< test C" + STRING_NEWLINE +
                    "> test D" + STRING_NEWLINE +
                    "> test E" + STRING_NEWLINE +
                    "> test F" + STRING_NEWLINE ;
            assertEquals(expected, stdout.toString());

        } catch(DiffException e) {
            fail("should not fail: " + e.getMessage());
        }
    }

    @Test
    void testRunWithValidStdinInFirstArgAndInvalidFilenameShouldThrowDiffException() throws Exception {
        String[] args = {"-", "invalidFile"};
        try {
            InputStream inputStream = new FileInputStream(new File(DIFF1_FILE)); //NOPMD
            Exception thrown = assertThrows(DiffException.class, () -> {
                    diffApp.run(args, inputStream, stdout);
            });
            assertEquals("diff: " + ERR_INVALID_ARGS, thrown.getMessage());
        } catch(Exception e) {
            fail("should not fail: " + e.getMessage());
        }
    }

    @Test
    void testRunWithOneInvalidFileAndOneValidFile() {
        try {
            String[] args = {"invalid file", DIFF1_IDENTICAL_FILE};
            InputStream inputStream = new FileInputStream(new File(DIFF1_FILE)); //NOPMD
            Exception thrown = assertThrows(DiffException.class, () -> {
                diffApp.run(args, inputStream, stdout);
            });
            String expected =  DIFF_EXCEPTION + ERR_INVALID_ARGS;
            assertEquals(expected, thrown.getMessage());
        } catch (Exception e) {
            fail("should not fail: " + e.getMessage());
        }
    }

    @Test
    void testRunWithDiffDirContainFilesWithDifferentContent() {
        try {
            String[] args = {DIFFDIR1, DIFFDIR2};
            InputStream inputStream = new FileInputStream(new File(DIFF1_FILE)); //NOPMD
            diffApp.run(args, inputStream, stdout);
            String expected = "Only in diffDir1: diff1-identical.txt" + StringUtils.STRING_NEWLINE +
                    "Only in diffDir2: diff2.txt";
            assertTrue(stdout.toString().contains(expected));
        } catch (Exception e) {
            fail("should not fail: " + e.getMessage());
        }
    }

    @Test
    void testRunDiffDirContainFilesWithDifferentContentUsingFlagQ() {
        try {
            String[] args = {"-q", DIFFDIR1, DIFFDIR2};
            InputStream inputStream = new FileInputStream(new File(DIFF1_FILE)); //NOPMD
            diffApp.run(args, inputStream, stdout);
            String expected = "Only in diffDir1: diff1-identical.txt" + StringUtils.STRING_NEWLINE +
                    "Only in diffDir2: diff2.txt";
            assertTrue(stdout.toString().contains(expected));
        } catch (Exception e) {
            fail("should not fail: " + e.getMessage());
        }
    }

    @Test
    void testRunDiffDirContainFilesWithDifferentContentUsingFlagS() {
        try {
            String[] args = {"-s", DIFFDIR1, DIFFDIR2};
            InputStream inputStream = new FileInputStream(new File(DIFF1_FILE)); //NOPMD
            diffApp.run(args, inputStream, stdout);
            String expected = "Only in diffDir1: diff1-identical.txt" + STRING_NEWLINE +
                    "Only in diffDir2: diff2.txt";
            String expectedTwo = FILES + DIFFDIR1NAME + "/" +  DIFF1_FILENAME + CHAR_SPACE + DIFFDIR2NAME + "/" + DIFF1_FILENAME + IDENTICAL;;
            assertTrue(stdout.toString().contains(expected));
            assertTrue(stdout.toString().contains(expectedTwo));
        } catch (Exception e) {
            fail("should not fail: " + e.getMessage());
        }
    }

    @Test
    void testRunWithOneInvalidIdenticalDirectoriesShouldReturnString() {
        String[] args = {DIFFDIR1, "invalidDir"};
        try {
            InputStream inputStream = new FileInputStream(new File(DIFF1_FILE)); //NOPMD
            Exception thrown = assertThrows(DiffException.class, () -> {
                diffApp.run(args, inputStream, stdout);
            });
            assertEquals("diff: " + ERR_INVALID_ARGS, thrown.getMessage());
        } catch(Exception e) {
            fail("should not fail: " + e.getMessage());
        }
    }

    @Test
    void testRunWithValidIdenticalDirectoriesWithFlagSShouldReturnSameMessage() {
        String[] args = {"-s", DIFFDIR1, DIFFDIR1_IDENTICAL};
        try {
            InputStream inputStream = new FileInputStream(new File(DIFF1_FILE)); //NOPMD
            diffApp.run(args, inputStream, stdout);
            String expected = FILES + DIFFDIR1NAME + "/" +  DIFF1_FILENAME + CHAR_SPACE + DIFFDIR1NAME_IDENTICAL + "/" + DIFF1_FILENAME + IDENTICAL;
            String expectedTwo = FILES + DIFFDIR1NAME + "/" + DIFF1_IDENTICAL_FILENAME + CHAR_SPACE + DIFFDIR1NAME_IDENTICAL + "/" + DIFF1_IDENTICAL_FILENAME + IDENTICAL;
            assertTrue(stdout.toString().contains(expected));
            assertTrue(stdout.toString().contains(expectedTwo));
        } catch(Exception e) {
            fail("should not fail: " + e.getMessage());
        }
    }
    
    @Test
    void testRunWithBinaryFilesWithDifference() {
        String[] args = {DIFFBIN1, DIFFBIN2};
        try {
            InputStream inputStream = new FileInputStream(new File(DIFF1_FILE)); //NOPMD
            diffApp.run(args, inputStream, stdout);
            String expected = "Binary" + " files " + DIFFFOLDER +  DIFFBIN1NAME + CHAR_SPACE + DIFFFOLDER + DIFFBIN2NAME + " differ";
            assertTrue(stdout.toString().contains(expected));
        } catch(Exception e) {
            fail("should not fail: " + e.getMessage());
        }
    }

    @Test
    void testRunWithBinaryFilesWithNoDifference() {
        String[] args = {DIFFBIN1, DIFFBIN3};
        try {
            InputStream inputStream = new FileInputStream(new File(DIFF1_FILE)); //NOPMD
            diffApp.run(args, inputStream, stdout);
            assertTrue(stdout.toString().contains(STRING_NEWLINE));
        } catch(Exception e) {
            fail("should not fail: " + e.getMessage());
        }
    }
}
