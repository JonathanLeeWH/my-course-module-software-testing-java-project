package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.*;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.GrepException;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

class GrepApplicationTest {
    private static final String FILE_ONE_NAME = "fileOne";
    private static final String FILE_TWO_NAME = "fileTwo";
    private static final String FILE_FORMAT = ".txt";
    private static final String FILE_ONE_TEXT = "line one" + System.lineSeparator() + "line two" + System.lineSeparator() + "line three";
    private static final String LINE_ONE = "line one";
    private static final String INVALID = "invalid";
    private static final String LOWER_LINE = "line";
    private static final String UPPER_LINE = "Line";
    private static File fileOne;
    private static File fileTwo;
    private static GrepApplication grepApplication;
    private static OutputStream outputStream;

    @BeforeEach
    void setup() throws IOException {
        grepApplication = new GrepApplication();
        fileOne = File.createTempFile(FILE_ONE_NAME, FILE_FORMAT);
        fileTwo = File.createTempFile(FILE_TWO_NAME, FILE_FORMAT);
        OutputStream stdoutOne = new FileOutputStream(fileOne);
        OutputStream stdoutTwo = new FileOutputStream(fileTwo);
        outputStream = new ByteArrayOutputStream();
        stdoutOne.write(FILE_ONE_TEXT.getBytes());
        stdoutTwo.write(LINE_ONE.getBytes());
        stdoutOne.close();
        stdoutTwo.close();
        outputStream = new ByteArrayOutputStream();
        grepApplication = new GrepApplication();
    }

    @AfterEach
    void teardown() throws IOException {
        outputStream.close();
        fileOne.deleteOnExit();
        fileTwo.deleteOnExit();
    }

    // Test grepFromFiles Method

    /**
     * Test null files names in grepFromFiles Method.
     * Expected: Throw NullPointer Exception
     */
    @Test
    void runNullFileNamesShouldThrowNullPointerException() {
        assertThrows(NullPointerException.class, () -> {
            grepApplication.grepFromFiles(LOWER_LINE, true, true, (String) null);
        });
    }

    /**
     * Test null pattern string in grepFromFiles Method.
     * Expected: Throw GrepException
     */
    @Test
    void runNullPatternShouldThrowGrepException() {
        assertThrows(GrepException.class, () -> {
            grepApplication.grepFromFiles(null, true, true, FILE_ONE_NAME + FILE_FORMAT);
        });
    }

    /**
     * Test grepFromFiles Method with valid inputs.
     * Expected: Number of lines that matches the pattern.
     */
    @Test
    void runIsCaseInsensitiveTrueWithValidPatternShouldReturnNumberOfLinesGrepped() throws Exception {
        assertEquals("3" + System.lineSeparator(), grepApplication.grepFromFiles(LOWER_LINE, true, true, FILE_ONE_NAME + FILE_FORMAT));
    }

    /**
     * Test grepFromFiles Method with invalid pattern with case sensitivity.
     * Expected: 0.
     */
    @Test
    void runIsCaseInsensitiveFalseWithValidPatternShouldReturnNumberOfLinesGrepped() throws Exception {
        assertEquals("3" + System.lineSeparator(), grepApplication.grepFromFiles(LOWER_LINE, false, true, FILE_ONE_NAME + FILE_FORMAT));
    }

    /**
     * Test grepFromFiles Method with invalid pattern.
     * Expected: 0.
     */
    @Test
    void runIsCaseInsensitiveTrueWithInvalidPatternShouldReturnNumberOfLinesGrepped() throws Exception {
        assertEquals("0" + System.lineSeparator(), grepApplication.grepFromFiles(INVALID, true, true, FILE_ONE_NAME + FILE_FORMAT));
    }

    /**
     * Test grepFromFiles Method with invalid pattern with case sensitivity.
     * Expected: 0.
     */
    @Test
    void runIsCaseInsensitiveFalseWithInvalidPatternShouldReturnNumberOfLinesGrepped() throws Exception {
        assertEquals("0" + System.lineSeparator(), grepApplication.grepFromFiles(UPPER_LINE, false, true, FILE_ONE_NAME + FILE_FORMAT));
    }


    /**
     * Test grepFromFiles method with valid pattern with case sensitivity.
     * Expected: 3.
     */
    @Test
    void runSingleFileNameShouldReturnNumberOfLinesGrepped() throws Exception {
        String[] fileNames = {FILE_ONE_NAME + FILE_FORMAT};
        assertEquals(3 + System.lineSeparator(), grepApplication.grepFromFiles(LOWER_LINE, true, true, fileNames));
    }

    /**
     * Test grepFromFiles method with multiple file names.
     * Expected: number of lines grepped in each file.
     */
    @Test
    void runMultipleFileNamesShouldReturnNumberOfLinesGrepped() throws Exception {
        String[] fileNames = {FILE_ONE_NAME + FILE_FORMAT, FILE_ONE_NAME + FILE_FORMAT};
        String expectedOutput = FILE_ONE_NAME + FILE_FORMAT + ": 3" + System.lineSeparator() + FILE_ONE_NAME + FILE_FORMAT + ": 3";
        assertEquals(expectedOutput + System.lineSeparator(), grepApplication.grepFromFiles(LOWER_LINE, true, true, fileNames));
    }

    /**
     * Test grepFromFiles method with isCountLines false, with valid pattern.
     * Expected: return a single line.
     */
    @Test
    void runIsCountLinesFalseWithValidPatternShouldReturnSingleLineMatched() throws Exception {
        String[] fileNames = {FILE_ONE_NAME + FILE_FORMAT};
        String validPattern = "one";
        String expectedOutput = LINE_ONE + System.lineSeparator();
        assertEquals(expectedOutput, grepApplication.grepFromFiles(validPattern, true, false, fileNames));
    }

    /**
     * Test grepFromFiles method with isCountLines false, with valid pattern.
     * Expected: return multiple lines.
     */
    @Test
    void runIsCountLinesFalseWithValidPatternShouldReturnMultipleLineMatched() throws Exception {
        String[] fileNames = {FILE_ONE_NAME + FILE_FORMAT};
        String expectedOutput = LINE_ONE + System.lineSeparator() + "line two"
                + System.lineSeparator() + "line three" + System.lineSeparator();
        assertEquals(expectedOutput, grepApplication.grepFromFiles(LOWER_LINE, true, false, fileNames));
    }

    /**
     * Test grepFromFiles method with isCountLines false, with invalid pattern.
     * Expected: return empty string.
     */
    @Test
    void runIsCountLinesFalseWithInvalidPatternShouldReturnEmptyString() throws Exception {
        String[] fileNames = {FILE_ONE_NAME + FILE_FORMAT};
        String expectedOutput = "";
        assertEquals(expectedOutput, grepApplication.grepFromFiles(INVALID, true, false, fileNames));
    }

    /**
     * Test grepFromFiles method with isCountLines false, with invalid pattern due to case sensitivity.
     * Expected: return empty string.
     */
    @Test
    void runIsCountLinesFalseWithInValidPatternDueToCaseSensitivityShouldReturnEmptyString() throws Exception {
        String[] fileNames = {FILE_ONE_NAME + FILE_FORMAT};
        String expectedOutput = "";
        assertEquals(expectedOutput, grepApplication.grepFromFiles(UPPER_LINE, false, false, fileNames));
    }

    // Test grepFromStdin Method

    /**
     * Test grepFromStdin method with null standard input.
     * Expected: throws GrepException.
     */
    @Test
    void runNullInputStream() {
        assertThrows(GrepException.class, () -> {
            grepApplication.grepFromStdin(LOWER_LINE, true, true, null);
        });
    }

    /**
     * Test grepFromStdin method with valid input.
     * Expected: return number of lines matched.
     */
    @Test
    void runInputStreamWithValidPatternShouldReturnNumberOfLinesGrepped() throws Exception {
        try(InputStream inputStream = new FileInputStream(FILE_ONE_NAME + FILE_FORMAT)) {
            assertEquals(3 + System.lineSeparator(),
                    grepApplication.grepFromStdin(LOWER_LINE, true, true, inputStream));
        } catch (IOException e) {
            e.getMessage();
        }
    }

    /**
     * Test grepFromStdin method with valid input.
     * Expected: return number of lines matched.
     */
    @Test
    void runInputStreamWithInvalidPatternShouldReturnZeroNumberOfLinesGrepped() throws Exception {
        try(InputStream inputStream = new FileInputStream(FILE_ONE_NAME + FILE_FORMAT)) {
            assertEquals(0 + System.lineSeparator(),
                    grepApplication.grepFromStdin(INVALID, true, true, inputStream));
        } catch (IOException e) {
            e.getMessage();
        }
    }

    /**
     * Test grepFromStdin method with invalid input due to case sensitivity.
     * Expected: 0.
     */
    @Test
    void runInputStreamWithIsCaseInsensitiveFalseShouldReturnZeroNumberOfLinesGrepped() throws Exception {
        try(InputStream inputStream = new FileInputStream(FILE_ONE_NAME + FILE_FORMAT)) {
            assertEquals(0 + System.lineSeparator(),
                grepApplication.grepFromStdin(UPPER_LINE, false, true, inputStream));
        } catch (IOException e) {
            e.getMessage();
        }
    }

    /**
     * Test grepFromStdin method with valid pattern and isCountLines is false.
     * Expected: return lines that matched the pattern.
     */
    @Test
    void runInputStreamWithValidPatternAndIsCountLinesFalseShouldReturnLinesGrepped() throws Exception {
        String validPattern = "one";
        try(InputStream inputStream = new FileInputStream(FILE_ONE_NAME + FILE_FORMAT)) {
           assertEquals(LINE_ONE + System.lineSeparator(),
                grepApplication.grepFromStdin(validPattern, true, false, inputStream));
        } catch (IOException e) {
            e.getMessage();
        }
    }

    /**
     * Test grepFromStdin method with isCountLines false, with invalid pattern.
     * Expected: return empty string.
     */
    @Test
    void runInputStreamWithInvalidPatternAndIsCountLinesFalseShouldReturnEmptyString() throws Exception {
        try(InputStream inputStream = new FileInputStream(FILE_ONE_NAME + FILE_FORMAT)) {
            assertEquals("",
                grepApplication.grepFromStdin(INVALID, true, false, inputStream));
        } catch (IOException e) {
            e.getMessage();
        }
    }

    /**
     * Test grepFromStdin method with isCountLines false, with invalid pattern due to case sensitivity.
     * Expected: return empty string.
     */
    @Test
    void runInputStreamWithInvalidPatternDueToCaseSensitivityAndIsCountLinesFalseShouldReturnEmptyString() throws Exception {
        try(InputStream inputStream = new FileInputStream(FILE_ONE_NAME + FILE_FORMAT)) {
            assertEquals("",
                grepApplication.grepFromStdin(UPPER_LINE, false, false, inputStream));
        } catch (IOException e) {
            e.getMessage();
        }
    }

    // Test getGrepArgument Method.
    /**
     * Test run method with valid grep argument with -i and -c flags.
     * Expected: Print number of lines grepped.
     */
    @Test
    void runValidGrepArgumentShouldPrintNumberOfLinesGrepped() throws AbstractApplicationException, IOException {
        String[] args = {"-i", "-c", LOWER_LINE, FILE_ONE_NAME + FILE_FORMAT};
        outputStream = new ByteArrayOutputStream();
        try(InputStream inputStream = new FileInputStream(FILE_ONE_NAME + FILE_FORMAT)) {
            grepApplication.run(args, inputStream, outputStream);
            assertEquals("3" + System.lineSeparator(), outputStream.toString());
        } catch (IOException e) {
            e.getMessage();
        }
    }

    /**
     * Test run method with valid grep argument with invalid -i-i flag.
     * Expected: throw Grep Exception
     */
    @Test
    void runInvalidDashIGrepArgumentShouldThrowGrepException() throws IOException {
        String[] args = {"-i-i", "-c", LOWER_LINE, FILE_ONE_NAME + FILE_FORMAT};
        try(InputStream inputStream = new FileInputStream(FILE_ONE_NAME + FILE_FORMAT)) {
            assertThrows(GrepException.class, () -> {
                grepApplication.run(args, inputStream, outputStream);
            });
        } catch (IOException e) {
            e.getMessage();
        }
    }

    /**
     * Test run method with valid grep argument with invalid -i-i flag.
     * Expected: throw Grep Exception
     */
    @Test
    void runInvalidDashCGrepArgument() throws IOException {
        String[] args = {"-i", "-c-c", LOWER_LINE, FILE_ONE_NAME + FILE_FORMAT};
        try(InputStream inputStream = new FileInputStream(FILE_ONE_NAME + FILE_FORMAT)) {
            assertThrows(GrepException.class, () -> {
                grepApplication.run(args, inputStream, outputStream);
            });
        } catch (IOException e) {
            e.getMessage();
        }
    }

    /**
     * Test run method with no filenames and null stdin.
     * Expected: throw Grep Exception
     */
    @Test
    void runNoFilesAndNoStdinGrepArgumentShouldThrowGrepException() {
        String[] args = {"-i", "-c", LOWER_LINE};
        assertThrows(GrepException.class, () -> {
            grepApplication.run(args, null, outputStream);
        });
    }

    /**
     * Test run method with no -i flag.
     * Expected: print number of lines grepped
     */
    @Test
    void runNoDashIInGrepArgumentShouldPrintNumberOfLinesGrepped() throws AbstractApplicationException, IOException {
        String[] args = {"-c", LOWER_LINE, FILE_ONE_NAME + FILE_FORMAT};
        try(InputStream inputStream = new FileInputStream(FILE_ONE_NAME + FILE_FORMAT)) {
            grepApplication.run(args, inputStream, outputStream);
            assertEquals(3 + System.lineSeparator(), outputStream.toString());
        } catch (IOException e) {
            e.getMessage();
        }
    }

    /**
     * Test run method with empty args.
     * Expected: throw Grep Exception
     */
    @Test
    void runEmptyArgsGrepArgumentShouldThrowGrepException() throws AbstractApplicationException, FileNotFoundException {
        String[] args = {""};
        assertThrows(GrepException.class, () -> {
            grepApplication.run(args, null, outputStream);
        });
    }

    /**
     * Test run method with null args.
     * Expected: throw Grep Exception
     */
    @Test
    void runNullArgsGrepArgumentShouldThrowGrepException() throws AbstractApplicationException, FileNotFoundException {
        assertThrows(GrepException.class, () -> {
            grepApplication.run(null, null, outputStream);
        });
    }

    // The following are bugs from the skeleton code. Reserved for debugging in milestone 2.
    /*
    @Test
    void runInvalidDashWithDoubleIGrepArgumentShouldThrowGrepException() {
        String[] args = {"-ii", "-c", LOWER_LINE, FILE_ONE_NAME + FILE_FORMAT};
        InputStream inputStream = new FileInputStream(FILE_ONE_NAME + FILE_FORMAT);
        assertThrows(GrepException.class, () -> {
            grepApplication.run(args, inputStream, outputStream);
        });
    }

    @Test
    void runInvalidDashWithDoubleCCGrepArgumentShouldThrowGrepException() {
        String[] args = {"-i", "-cc", LOWER_LINE, FILE_ONE_NAME + FILE_FORMAT};
        InputStream inputStream = new FileInputStream(FILE_ONE_NAME + FILE_FORMAT);
        assertThrows(GrepException.class, () -> {
            grepApplication.run(args, inputStream, outputStream);
        });
    }*/
}
