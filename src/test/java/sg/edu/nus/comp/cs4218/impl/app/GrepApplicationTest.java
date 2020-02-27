package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.*;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.GrepException;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

class GrepApplicationTest {

    private static GrepApplication grepApplication;
    private static OutputStream outputStream;

    @BeforeAll
    static void setup() {
        outputStream = new ByteArrayOutputStream();
        grepApplication = new GrepApplication();
    }

    @AfterAll
    static void teardown() throws IOException {
        outputStream.close();
    }
    // Test grepFromFiles Method

    /**
     * Test null files names in grepFromFiles Method.
     * Expected: Throw NullPointer Exception
     */
    @Test
    void runNullFileNamesShouldThrowNullPointerException() {
        String validPattern = "line";
        assertThrows(NullPointerException.class, () -> {
            grepApplication.grepFromFiles(validPattern, true, true, (String) null);
        });
    }

    /**
     * Test null pattern string in grepFromFiles Method.
     * Expected: Throw GrepException
     */
    @Test
    void runNullPatternShouldThrowGrepException() {
        String fileName = "fileOne.txt";
        assertThrows(GrepException.class, () -> {
            grepApplication.grepFromFiles(null, true, true, fileName);
        });
    }

    /**
     * Test grepFromFiles Method with valid inputs.
     * Expected: Number of lines that matches the pattern.
     */
    @Test
    void runIsCaseInsensitiveTrueWithValidPatternShouldReturnNumberOfLinesGrepped() throws Exception {
        String fileName = "fileOne.txt";
        String validPattern = "line";
        assertEquals("3" + System.lineSeparator(), grepApplication.grepFromFiles(validPattern, true, true, fileName));
    }

    /**
     * Test grepFromFiles Method with invalid pattern.
     * Expected: 0.
     */
    @Test
    void runIsCaseInsensitiveTrueWithInvalidPatternShouldReturnNumberOfLinesGrepped() throws Exception {
        String fileName = "fileOne.txt";
        String invalidPattern = "invalid";
        assertEquals("0" + System.lineSeparator(), grepApplication.grepFromFiles(invalidPattern, true, true, fileName));
    }

    /**
     * Test grepFromFiles Method with invalid pattern with case sensitivity.
     * Expected: 0.
     */
    @Test
    void runIsCaseInsensitiveFalseWithInvalidPatternShouldReturnNumberOfLinesGrepped() throws Exception {
        String fileName = "fileOne.txt";
        String validPattern = "Line";
        assertEquals("0" + System.lineSeparator(), grepApplication.grepFromFiles(validPattern, false, true, fileName));
    }


    /**
     * Test grepFromFiles method with valid pattern with case sensitivity.
     * Expected: 3.
     */
    @Test
    void runSingleFileNameShouldReturnNumberOfLinesGrepped() throws Exception {
        String[] fileNames = {"fileOne.txt"};
        String validPattern = "line";
        assertEquals(3 + System.lineSeparator(), grepApplication.grepFromFiles(validPattern, true, true, fileNames));
    }

    /**
     * Test grepFromFiles method with multiple file names.
     * Expected: number of lines grepped in each file.
     */
    @Test
    void runMultipleFileNamesShouldReturnNumberOfLinesGrepped() throws Exception {
        String[] fileNames = {"fileOne.txt", "fileOne.txt"};
        String validPattern = "line";
        String expectedOutput = "fileOne.txt: 3" + System.lineSeparator() + "fileOne.txt: 3";
        assertEquals(expectedOutput + System.lineSeparator(), grepApplication.grepFromFiles(validPattern, true, true, fileNames));
    }

    /**
     * Test grepFromFiles method with isCountLines false, with valid pattern.
     * Expected: return a single line.
     */
    @Test
    void runIsCountLinesFalseWithValidPatternShouldReturnSingleLineMatched() throws Exception {
        String[] fileNames = {"fileOne.txt"};
        String validPattern = "one";
        String expectedOutput = "line one" + System.lineSeparator();
        assertEquals(expectedOutput, grepApplication.grepFromFiles(validPattern, true, false, fileNames));
    }

    /**
     * Test grepFromFiles method with isCountLines false, with valid pattern.
     * Expected: return multiple lines.
     */
    @Test
    void runIsCountLinesFalseWithValidPatternShouldReturnMultipleLineMatched() throws Exception {
        String[] fileNames = {"fileOne.txt"};
        String validPattern = "line";
        String expectedOutput = "line one" + System.lineSeparator() + "line two"
                + System.lineSeparator() + "line three" + System.lineSeparator();
        assertEquals(expectedOutput, grepApplication.grepFromFiles(validPattern, true, false, fileNames));
    }

    /**
     * Test grepFromFiles method with isCountLines false, with invalid pattern.
     * Expected: return empty string.
     */
    @Test
    void runIsCountLinesFalseWithInvalidPatternShouldReturnEmptyString() throws Exception {
        String[] fileNames = {"fileOne.txt"};
        String invalidPattern = "invalid";
        String expectedOutput = "";
        assertEquals(expectedOutput, grepApplication.grepFromFiles(invalidPattern, true, false, fileNames));
    }

    /**
     * Test grepFromFiles method with isCountLines false, with invalid pattern due to case sensitivity.
     * Expected: return empty string.
     */
    @Test
    void runIsCountLinesFalseWithInValidPatternDueToCaseSensitivityShouldReturnEmptyString() throws Exception {
        String[] fileNames = {"fileOne.txt"};
        String validPattern = "Line";
        String expectedOutput = "";
        assertEquals(expectedOutput, grepApplication.grepFromFiles(validPattern, false, false, fileNames));
    }

    // Test grepFromStdin Method

    /**
     * Test grepFromStdin method with null standard input.
     * Expected: throws GrepException.
     */
    @Test
    void runNullInputStream() {
        String validPattern = "line";
        assertThrows(GrepException.class, () -> {
            grepApplication.grepFromStdin(validPattern, true, true, null);
        });
    }

    /**
     * Test grepFromStdin method with valid input.
     * Expected: return number of lines matched.
     */
    @Test
    void runInputStreamWithValidPatternShouldReturnNumberOfLinesGrepped() throws Exception {
        String validPattern = "line";
        InputStream inputStream = new FileInputStream("fileOne.txt");
        assertEquals(3 + System.lineSeparator(),
                grepApplication.grepFromStdin(validPattern, true, true, inputStream));
    }

    /**
     * Test grepFromStdin method with valid input.
     * Expected: return number of lines matched.
     */
    @Test
    void runInputStreamWithInvalidPatternShouldReturnZeroNumberOfLinesGrepped() throws Exception {
        String invalidPattern = "invalid";
        InputStream inputStream = new FileInputStream("fileOne.txt");
        assertEquals(0 + System.lineSeparator(),
                grepApplication.grepFromStdin(invalidPattern, true, true, inputStream));
    }

    /**
     * Test grepFromStdin method with invalid input due to case sensitivity.
     * Expected: 0.
     */
    @Test
    void runInputStreamWithIsCaseInsensitiveFalseShouldReturnZeroNumberOfLinesGrepped() throws Exception {
        String invalidPattern = "Line";
        InputStream inputStream = new FileInputStream("fileOne.txt");
        assertEquals(0 + System.lineSeparator(),
                grepApplication.grepFromStdin(invalidPattern, false, true, inputStream));
    }

    /**
     * Test grepFromStdin method with valid pattern and isCountLines is false.
     * Expected: return lines that matched the pattern.
     */
    @Test
    void runInputStreamWithValidPatternAndIsCountLinesFalseShouldReturnLinesGrepped() throws Exception {
        String validPattern = "one";
        InputStream inputStream = new FileInputStream("fileOne.txt");
        assertEquals("line one" + System.lineSeparator(),
                grepApplication.grepFromStdin(validPattern, true, false, inputStream));
    }

    /**
     * Test grepFromStdin method with isCountLines false, with invalid pattern.
     * Expected: return empty string.
     */
    @Test
    void runInputStreamWithInvalidPatternAndIsCountLinesFalseShouldReturnEmptyString() throws Exception {
        String invalidPattern = "invalid";
        InputStream inputStream = new FileInputStream("fileOne.txt");
        assertEquals("",
                grepApplication.grepFromStdin(invalidPattern, true, false, inputStream));
    }

    /**
     * Test grepFromStdin method with isCountLines false, with invalid pattern due to case sensitivity.
     * Expected: return empty string.
     */
    @Test
    void runInputStreamWithInvalidPatternDueToCaseSensitivityAndIsCountLinesFalseShouldReturnEmptyString() throws Exception {
        String invalidPattern = "Line";
        InputStream inputStream = new FileInputStream("fileOne.txt");
        assertEquals("",
                grepApplication.grepFromStdin(invalidPattern, false, false, inputStream));
    }

    // Test getGrepArgument Method.

    @Test
    void runValidGrepArgument() throws AbstractApplicationException, FileNotFoundException {
        String[] args = {"-i", "-c", "line", "fileOne.txt"};
        outputStream = new ByteArrayOutputStream();
        InputStream inputStream = new FileInputStream("fileOne.txt");
        grepApplication.run(args, inputStream, outputStream);
        assertEquals("3" + System.lineSeparator(), outputStream.toString());
    }

    @Test
    void runInvalidDashIGrepArgument() throws AbstractApplicationException, FileNotFoundException {
        String[] args = {"-i-i", "-c", "line", "fileOne.txt"};
        InputStream inputStream = new FileInputStream("fileOne.txt");
        assertThrows(GrepException.class, () -> {
            grepApplication.run(args, inputStream, outputStream);
        });
    }

    @Test
    void runInvalidDashCGrepArgument() throws AbstractApplicationException, FileNotFoundException {
        String[] args = {"-i", "-c-c", "line", "fileOne.txt"};
        InputStream inputStream = new FileInputStream("fileOne.txt");
        assertThrows(GrepException.class, () -> {
            grepApplication.run(args, inputStream, outputStream);
        });
    }

    @Test
    void runNoFilesAndNoStdinGrepArgument() throws AbstractApplicationException, FileNotFoundException {
        String[] args = {"-i", "-c", "line"};
        assertThrows(GrepException.class, () -> {
            grepApplication.run(args, null, outputStream);
        });
    }

    @Test
    void runNoDashIInGrepArgument() throws AbstractApplicationException, FileNotFoundException {
        String[] args = {"-c", "line", "fileOne.txt"};
        InputStream inputStream = new FileInputStream("fileOne.txt");
        grepApplication.run(args, inputStream, outputStream);
        assertEquals(3 + System.lineSeparator(), outputStream.toString());

    }

    @Test
    void runEmptyArgsGrepArgument() throws AbstractApplicationException, FileNotFoundException {
        String[] args = {""};
        assertThrows(GrepException.class, () -> {
            grepApplication.run(args, null, outputStream);
        });
    }

    @Test
    void runNullArgsGrepArgument() throws AbstractApplicationException, FileNotFoundException {
        assertThrows(GrepException.class, () -> {
            grepApplication.run(null, null, outputStream);
        });
    }

    @Test
    void runInvalidDashWithDoubleIGrepArgument() throws FileNotFoundException {
        String[] args = {"-ii", "-c", "line", "fileOne.txt"};
        InputStream inputStream = new FileInputStream("fileOne.txt");
        assertThrows(GrepException.class, () -> {
            grepApplication.run(args, inputStream, outputStream);
        });
    }

    @Test
    void runInvalidDashWithDoubleCCGrepArgument() throws AbstractApplicationException, FileNotFoundException {
        String[] args = {"-i", "-cc", "line", "fileOne.txt"};
        InputStream inputStream = new FileInputStream("fileOne.txt");
        assertThrows(GrepException.class, () -> {
            grepApplication.run(args, inputStream, outputStream);
        });
    }

}