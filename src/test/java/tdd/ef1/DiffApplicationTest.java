package tdd.ef1;

import org.junit.jupiter.api.*;
import sg.edu.nus.comp.cs4218.EnvironmentHelper;
import sg.edu.nus.comp.cs4218.exception.DiffException;
import sg.edu.nus.comp.cs4218.impl.app.DiffApplication;
import sg.edu.nus.comp.cs4218.impl.util.StringUtils;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_SPACE;

/**
 * Tests for diff command.
 *
 * Negative test cases:
 * - Invalid file
 * - Invalid directory
 * - Directory without files
 *
 * Positive test cases:
 * - No flag used + Files/stdin with same content
 * - "-s" flag used + Files/stdin with same content
 * - "-B" flag used + Files/stdin with same content
 * - "-sB" flags used + Files with same content
 * - No flag used + Directories containing Files with same content
 * - "-s" flag used + Directories containing Files with same content
 *
 * - No flag used + Files/stdin with different content
 * - "-q" flag used + Files/stdin with different content
 * - "-Bq" flags used + Files/stdin with different content
 * - "sBq" flags used + Files/stdin with different content
 * - No flag used + Directories containing Files with different content
 * - "-q" flag used + Directories containing Files with different content
 */
public class DiffApplicationTest { // NOPMD
    private static DiffApplication diffApp;
    private static final String ORIGINAL_DIR = EnvironmentHelper.currentDirectory;
    private static final Path DIRECTORY = Paths.get("src", "test", "java", "tdd", "util", "dummyTestFolder",
            "DiffTestFolder");
    private static final String ABSOLUTE_PATH = DIRECTORY.toFile().getAbsolutePath();
    private static OutputStream stdout;

    private static final String IDENTICAL = " are identical";
    private static final String FILES = "Files ";
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

    @BeforeEach
    void setUp() {
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

    @Test
    public void testFailsWithInvalidFile() {
        Exception expectedException = assertThrows(DiffException.class, () -> diffApp.diffTwoFiles("invalidFile.txt", "invalidFile.txt", false, false, false));
        assertTrue(expectedException.getMessage().contains(ERR_FILE_NOT_FOUND));
    }

    @Test
    public void testFailsWithInvalidDir() {
        Exception expectedException = assertThrows(DiffException.class, () -> diffApp.diffTwoFiles("invalidDir", "invalidDir", false, false, false));
        assertTrue(expectedException.getMessage().contains(ERR_FILE_NOT_FOUND));
    }

    @Test
    public void testFailsWithDirWithoutFiles() {
        Exception expectedException = assertThrows(DiffException.class, () -> diffApp.diffTwoDir("dummyDir", "dummyDir", false ,false, false));
        assertTrue(expectedException.getMessage().contains(ERR_IS_NOT_DIR));
    }

    @Test
    public void testDiffFilesWithSameContent() {
        try {
            diffApp.diffTwoFiles(DIFF1_FILE, DIFF1_IDENTICAL_FILE, false, false, false);
            assertTrue(stdout.toString().contains("")); // No message represents a successful diff
        } catch (DiffException e) {
            fail("should not fail: " + e.getMessage()); // NOPMD
        }
    }

    @Test
    public void testDiffFileAndStdinWithSameContent() throws DiffException {
        try {
            InputStream inputStream = new FileInputStream(new File(DIFF1_FILE)); //NOPMD
            diffApp.diffFileAndStdin(DIFF1_FILE, inputStream, false, false, false);
            assertTrue(stdout.toString().contains("")); // No message represents a successful diff
        } catch (IOException e) {
            fail("should not fail: " + e.getMessage());
        }
    }

    @Test
    public void testDiffFilesWithSameContentUsingFlagS() {
        try {
            String actualOutput = diffApp.diffTwoFiles(DIFF1_FILE, DIFF1_IDENTICAL_FILE, true, false, false);
            String expected = FILES + "DiffTestFolder" + File.separator + DIFF1_FILENAME + CHAR_SPACE + "DiffTestFolder" + File.separator + DIFF1_IDENTICAL_FILENAME + " are identical"; // NOPMD
            assertEquals(expected, actualOutput);
        } catch (DiffException e) {
            fail("should not fail: " + e.getMessage());
        }
    }

    @Test
    public void testDiffFileAndStdinWithSameContentUsingFlagS() throws DiffException {
        try {
            InputStream inputStream = new FileInputStream(new File(DIFF1_FILE)); //NOPMD
            String actualOutput = diffApp.diffFileAndStdin(DIFF1_FILE, inputStream, true, false, false);
            String expected = FILES + DIFF1_FILENAME + " - are identical";
            assertEquals(expected, actualOutput);
        } catch (IOException e) {
            fail("should not fail: " + e.getMessage());
        }
    }

    @Test
    public void testDiffFilesWithSameContentUsingFlagB() {
        try {
            diffApp.diffTwoFiles(DIFF1_FILE, DIFF1_BLANK_LINES_FILE, false, true, false);
            assertTrue(stdout.toString().contains("")); // No message represents a successful diff
        } catch (DiffException e) {
            fail("should not fail: " + e.getMessage());
        }
    }

    @Test
    public void testDiffFileAndStdinWithSameContentUsingFlagB() throws DiffException {
        try {
            InputStream inputStream = new FileInputStream(new File(DIFF1_BLANK_LINES_FILE)); //NOPMD
            diffApp.diffFileAndStdin(DIFF1_FILE, inputStream, false, true, false);
            assertTrue(stdout.toString().contains("")); // No message represents a successful diff
        } catch (IOException e) {
            fail("should not fail: " + e.getMessage());
        }
    }

    @Test
    public void testDiffFilesWithSameContentUsingFlagSB() {
        try {
            String actualOutput = diffApp.diffTwoFiles(DIFF1_FILE, DIFF1_BLANK_LINES_FILE, true, true, false);
            String expected = FILES + "DiffTestFolder" + File.separator + DIFF1_FILENAME + " " + "DiffTestFolder" + File.separator + DIFF1_BLANK_LINES_FILENAME + " are identical";
            assertEquals(expected, actualOutput);
        } catch (DiffException e) {
            fail("should not fail: " + e.getMessage());
        }
    }

    @Test
    public void testDiffDirContainFilesWithSameContent() {
        try {
            String actualOutput = diffApp.diffTwoDir(DIFFDIR1, DIFFDIR1_IDENTICAL, false, false, false);
            assertTrue(actualOutput.contains("")); // No message represents a successful diff
        } catch (DiffException e) {
            fail("should not fail: " + e.getMessage());
        }
    }

    @Test
    public void testDiffDirContainFilesWithSameContentUsingFlagS() {
        try {
            String actualOutput = diffApp.diffTwoDir(DIFFDIR1, DIFFDIR1_IDENTICAL, true, false, false);
            String expectedOne = FILES + DIFFDIR1NAME + File.separator +  DIFF1_FILENAME + CHAR_SPACE + DIFFDIR1NAME_IDENTICAL + File.separator + DIFF1_FILENAME + IDENTICAL;
            String expectedTwo = FILES + DIFFDIR1NAME + File.separator + DIFF1_IDENTICAL_FILENAME + CHAR_SPACE + DIFFDIR1NAME_IDENTICAL + File.separator + DIFF1_IDENTICAL_FILENAME + IDENTICAL;
            assertTrue(actualOutput.contains(expectedOne));
            assertTrue(actualOutput.contains(expectedTwo));
        } catch (DiffException e) {
            fail("should not fail: " + e.getMessage());
        }
    }

    @Test
    public void testDiffFilesWithDifferentContent() {
        try {
            String actualOutput = diffApp.diffTwoFiles(DIFF1_FILE, DIFF2_FILE, false, false, false);
            String expected = "< test A" + StringUtils.STRING_NEWLINE +
                    "< test B" + StringUtils.STRING_NEWLINE +
                    "< test C" + StringUtils.STRING_NEWLINE +
                    "> test D" + StringUtils.STRING_NEWLINE +
                    "> test E" + StringUtils.STRING_NEWLINE +
                    "> test F";
            assertTrue(actualOutput.contains(expected));
        } catch (DiffException e) {
            fail("should not fail: " + e.getMessage());
        }
    }

    @Test
    public void testDiffFileAndStdinWithDifferentContent() throws DiffException {
        try {
            InputStream inputStream = new FileInputStream(new File(DIFF2_FILE)); //NOPMD
            String actualOutput = diffApp.diffFileAndStdin(DIFF1_FILE, inputStream, false, false, false);
            String expected = "< test A" + StringUtils.STRING_NEWLINE +
                    "< test B" + StringUtils.STRING_NEWLINE +
                    "< test C" + StringUtils.STRING_NEWLINE +
                    "> test D" + StringUtils.STRING_NEWLINE +
                    "> test E" + StringUtils.STRING_NEWLINE +
                    "> test F";
            assertTrue(actualOutput.contains(expected));
        } catch (IOException e) {
            fail("should not fail: " + e.getMessage());
        }
    }

    @Test
    public void testDiffFilesWithDifferentContentUsingFlagQ() {
        try {
            String actualOutput = diffApp.diffTwoFiles(DIFF1_FILE, DIFF2_FILE, false, false, true);
            String expected = FILES + DIFF1_FILENAME + CHAR_SPACE + DIFF2_FILENAME + " differ"; // NOPMD
            assertEquals(expected, actualOutput);
        } catch (DiffException e) {
            fail("should not fail: " + e.getMessage());
        }
    }

    @Test
    public void testDiffFileAndStdinWithDifferentContentUsingFlagQ() throws DiffException {
        try {
            InputStream inputStream = new FileInputStream(new File(DIFF2_FILE)); //NOPMD
            String actualOutput = diffApp.diffFileAndStdin(DIFF1_FILE, inputStream, false, false, true);
            String expected = FILES + DIFF1_FILENAME + CHAR_SPACE + "-" + " differ";
            assertTrue(actualOutput.contains(expected));
        } catch (IOException e) {
            fail("should not fail: " + e.getMessage());
        }
    }

    @Test
    public void testDiffFilesWithDifferentContentUsingFlagBQ() {
        try {
            String actualOutput = diffApp.diffTwoFiles(DIFF2_FILE, DIFF1_BLANK_LINES_FILE, false, true, true);
            String expected = FILES + DIFF2_FILENAME + " " + DIFF1_BLANK_LINES_FILENAME + " differ";
            assertEquals(expected, actualOutput);
        } catch (DiffException e) {
            fail("should not fail: " + e.getMessage());
        }
    }

    @Test
    public void testDiffFileAndStdinWithDifferentContentUsingFlagBQ() throws DiffException {
        try {
            InputStream inputStream = new FileInputStream(new File(DIFF1_BLANK_LINES_FILE)); //NOPMD
            String actualOutput = diffApp.diffFileAndStdin(DIFF2_FILE, inputStream, false, true, true);
            String expected = FILES + DIFF2_FILENAME + CHAR_SPACE + "-" + " differ";
            assertTrue(actualOutput.contains(expected));
        } catch (IOException e) {
            fail("should not fail: " + e.getMessage());
        }
    }

    @Test
    public void testDiffFilesWithDifferentContentUsingFlagSBQ() {
        try {
            String actualOutput = diffApp.diffTwoFiles(DIFF2_FILE, DIFF1_BLANK_LINES_FILE, true, true, true);
            String expected =  FILES + DIFF2_FILENAME + " " + DIFF1_BLANK_LINES_FILENAME + " differ";
            assertTrue(actualOutput.contains(expected));
        } catch (DiffException e) {
            fail("should not fail: " + e.getMessage());
        }
    }

    @Test
    public void testDiffFileAndStdinWithDifferentContentUsingFlagSBQ() throws DiffException {
        try {
            InputStream inputStream = new FileInputStream(new File(DIFF1_BLANK_LINES_FILE)); //NOPMD
            String actualOutput = diffApp.diffFileAndStdin(DIFF2_FILE, inputStream, true, true, true);
            String expected = FILES + DIFF2_FILENAME + " " + "-" + " differ";
            assertTrue(actualOutput.contains(expected));
        } catch (IOException e) {
            fail("should not fail: " + e.getMessage());
        }
    }

    @Test
    public void testDiffDirContainFilesWithDifferentContent() {
        try {
            String actualOutput = diffApp.diffTwoDir(DIFFDIR1, DIFFDIR2, false, false, false);
            String expected = "Only in diffDir1: diff1-identical.txt" + StringUtils.STRING_NEWLINE +
                    "Only in diffDir1: diff1.txt" + StringUtils.STRING_NEWLINE +
                    "Only in diffDir2: diff2.txt";
            assertTrue(actualOutput.contains(expected));
        } catch (DiffException e) {
            fail("should not fail: " + e.getMessage());
        }
    }

    @Test
    public void testDiffDirContainFilesWithDifferentContentUsingFlagQ() {
        try {
            String actualOutput = diffApp.diffTwoDir(DIFFDIR1, DIFFDIR2, false, false, true);
            String expected = "Only in diffDir1: diff1-identical.txt" + StringUtils.STRING_NEWLINE +
                    "Only in diffDir1: diff1.txt" + StringUtils.STRING_NEWLINE +
                    "Only in diffDir2: diff2.txt";
            assertTrue(actualOutput.contains(expected));
        } catch (DiffException e) {
            fail("should not fail: " + e.getMessage());
        }
    }
}
