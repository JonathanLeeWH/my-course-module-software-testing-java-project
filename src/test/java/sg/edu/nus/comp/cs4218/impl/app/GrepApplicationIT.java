package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.GrepException;

import java.io.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;

public class GrepApplicationIT {
    private static final String FILE_ONE_NAME = "fileOne";
    private static final String FILE_TWO_NAME = "fileTwo";
    private static final String FILE_FORMAT = ".txt";
    private static final String FILE_ONE_TEXT = "line one" + System.lineSeparator() + "line two" + System.lineSeparator() + "line three";
    private static final String ONE_NAME_TEXT = FILE_ONE_NAME + FILE_FORMAT + ": line one" + System.lineSeparator()
            + FILE_ONE_NAME + FILE_FORMAT + ": line two" + System.lineSeparator() + FILE_ONE_NAME + FILE_FORMAT + ": line three";
    private static final String LINE_ONE = "line one";
    private static final String INVALID = "invalid";
    private static final String LOWER_LINE = "line";
    private static final String UPPER_LINE = "Line";
    private static File fileOne, fileTwo;
    private static GrepApplication grepApplication;
    private static OutputStream outputStream, stdoutOne, stdoutTwo;

    @SuppressWarnings("PMD.CloseResource")
    @BeforeEach
    void setup() throws IOException {
        grepApplication = new GrepApplication();
        fileOne = File.createTempFile(FILE_ONE_NAME, FILE_FORMAT);
        fileTwo = File.createTempFile(FILE_TWO_NAME, FILE_FORMAT);
        stdoutOne = new FileOutputStream(fileOne);
        stdoutTwo = new FileOutputStream(fileTwo);
        outputStream = new ByteArrayOutputStream();
        stdoutOne.write(FILE_ONE_TEXT.getBytes());
        stdoutTwo.write(LINE_ONE.getBytes());
        stdoutOne.close();
        stdoutTwo.close();
        outputStream = new ByteArrayOutputStream();
        grepApplication = new GrepApplication();
    }
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

    @Test
    void runValidGrepArgumentWithoutFlagsShouldPrintLinesThatMatches() throws AbstractApplicationException, IOException {
        String[] args = {LOWER_LINE, FILE_ONE_NAME + FILE_FORMAT};
        outputStream = new ByteArrayOutputStream();
        try(InputStream inputStream = new FileInputStream(FILE_ONE_NAME + FILE_FORMAT)) {
            grepApplication.run(args, inputStream, outputStream);
            assertEquals(FILE_ONE_TEXT + STRING_NEWLINE, outputStream.toString());
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
    void testRunNullStdoutGrepArgumentShouldThrowGrepException() {
        String[] args = {"-i", "-c", LOWER_LINE};
        try(InputStream inputStream = new FileInputStream(FILE_ONE_NAME + FILE_FORMAT)) {
            assertThrows(GrepException.class, () -> {
                grepApplication.run(args, inputStream, null);
            });
        } catch (IOException e) {
            e.getMessage();
        }
    }

    @Test
    void runNoFilesAndNoStdinGrepArgumentShouldThrowGrepException() {
        String[] args = {"-i", "-c", LOWER_LINE};
        assertThrows(GrepException.class, () -> {
            grepApplication.run(args, null, null);
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

    @Test
    void runInvalidDashWithDoubleIGrepArgumentShouldThrowGrepException() throws FileNotFoundException {
        String[] args = {"-ii", "-c", LOWER_LINE, FILE_ONE_NAME + FILE_FORMAT};
        InputStream inputStream = new FileInputStream(FILE_ONE_NAME + FILE_FORMAT);
        assertThrows(GrepException.class, () -> {
            grepApplication.run(args, inputStream, outputStream);
        });
    }

    @Test
    void runInvalidDashWithDoubleCCGrepArgumentShouldThrowGrepException() throws FileNotFoundException {
        String[] args = {"-i", "-cc", LOWER_LINE, FILE_ONE_NAME + FILE_FORMAT};
        InputStream inputStream = new FileInputStream(FILE_ONE_NAME + FILE_FORMAT);
        assertThrows(GrepException.class, () -> {
            grepApplication.run(args, inputStream, outputStream);
        });
    }

    @Test
    void testRunMethodWithInconsistentCasesInPatternWithDashIFlagShouldStillReturnLines() throws FileNotFoundException, AbstractApplicationException {
        String[] args = {"-i", "-c", "LinE", FILE_ONE_NAME + FILE_FORMAT};
        InputStream inputStream = new FileInputStream(FILE_ONE_NAME + FILE_FORMAT);
        grepApplication.run(args, inputStream, outputStream);
        assertEquals(3 + System.lineSeparator(), outputStream.toString());
    }

    @Test
    void testRunMethodWithInconsistentCasesInPatternWithDashCFlagShouldThrowException() throws FileNotFoundException, AbstractApplicationException {
        String[] args = {"LinE", FILE_ONE_NAME + FILE_FORMAT};
        InputStream inputStream = new FileInputStream(FILE_ONE_NAME + FILE_FORMAT);
        grepApplication.run(args, inputStream, outputStream);
        assertEquals("", outputStream.toString());
    }

    @Test
    void testRunMethodWithFlagsAfterTheFileNames() throws FileNotFoundException, AbstractApplicationException {
        String[] args = {"LinE", FILE_ONE_NAME + FILE_FORMAT, "-i", "-c"};
        InputStream inputStream = new FileInputStream(FILE_ONE_NAME + FILE_FORMAT);
        grepApplication.run(args, inputStream, outputStream);
        String expected = "-i: No such file or directory" + STRING_NEWLINE +
                "-c: No such file or directory" + STRING_NEWLINE;
        assertEquals(expected, outputStream.toString());
    }

    @Test
    void testRunMethodWithMultipleFileNames() throws FileNotFoundException, AbstractApplicationException {
        String[] args = { "line", FILE_ONE_NAME + FILE_FORMAT, FILE_ONE_NAME + FILE_FORMAT};
        InputStream inputStream = new FileInputStream(FILE_ONE_NAME + FILE_FORMAT);
        grepApplication.run(args, inputStream, outputStream);
        String expected = ONE_NAME_TEXT + STRING_NEWLINE + ONE_NAME_TEXT + STRING_NEWLINE;
        assertEquals(expected, outputStream.toString());
    }
}
