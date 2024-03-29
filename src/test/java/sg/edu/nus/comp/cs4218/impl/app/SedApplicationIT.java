package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.exception.SedException;

import java.io.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;

public class SedApplicationIT {
    private static SedApplication sedApplication;
    private static final String TEST_FILE_TYPE = ".txt";
    private static final String EMPTY_FILE = "testFileZero";
    private static final String TWO_LINES_NAME = "testFileOne";
    private static final String ONE_LINE_NAME = "testFileTwo";
    private static final String TEXT_EMPTY_FILE = "";
    private static final String TWO_LINES = "lineTwo" + STRING_NEWLINE + " line twoLine aline";
    private static final String ONE_LINE = "lineTwo" + "\t" + " line twoLine aline";
    private static final String LINE_TEN_WORDS = "line line line line line line line line line line";
    private static final String LINE = "line";
    private static final String REPLACE = "replace";
    private static final String REPLACED_LINE = " replace twoLine aline";
    private static final String VALID_ARG = "s|line|replace|1";
    private static final String SED_EXCEPTION = "sed: ";
    private static final String REPLACE_TWO = "replaceTwo";
    private static final String REPLACEMENT = "replacement";
    private static File emptyFile, fileWithOneLine, fileWithTwoLines, fileWithTenSameWordsInOneLine;
    private static OutputStream outputStreamZero, outputStreamOne, outputStreamTwo, outputStreamThree;

    @BeforeAll
    static void setupBeforeTest() {
        sedApplication = new SedApplication();
        try {
            emptyFile = File.createTempFile(EMPTY_FILE, TEST_FILE_TYPE);
            fileWithTwoLines = File.createTempFile(TWO_LINES_NAME, TEST_FILE_TYPE);
            fileWithOneLine = File.createTempFile(ONE_LINE_NAME, TEST_FILE_TYPE);
            fileWithTenSameWordsInOneLine =  File.createTempFile(ONE_LINE_NAME, TEST_FILE_TYPE);
            outputStreamZero = new FileOutputStream(emptyFile);
            outputStreamOne = new FileOutputStream(fileWithTwoLines);
            outputStreamTwo = new FileOutputStream(fileWithOneLine);
            outputStreamThree = new FileOutputStream(fileWithTenSameWordsInOneLine);
            outputStreamZero.write(TEXT_EMPTY_FILE.getBytes());
            outputStreamOne.write(TWO_LINES.getBytes());
            outputStreamTwo.write(ONE_LINE.getBytes());
            outputStreamThree.write(LINE_TEN_WORDS.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @AfterAll
    static void tearDownAfterTesting() {
        emptyFile.deleteOnExit();
        fileWithTwoLines.deleteOnExit();
        fileWithOneLine.deleteOnExit();
        fileWithTenSameWordsInOneLine.deleteOnExit();
        try {
            outputStreamZero.close();
            outputStreamOne.close();
            outputStreamTwo.close();
            outputStreamThree.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Test run method with valid filename.
     * Expected: Print file contents with replaced contents based on the replacement index.
     */
    @Test
    void testRunValidFileNamesInRunShouldPrintFileContentsWithReplacements() throws IOException, SedException {
        String[] args = {VALID_ARG, fileWithOneLine.toPath().toString()};
        try (InputStream stdinTwo = new FileInputStream(fileWithOneLine.toPath().toString())) {
            OutputStream osPrint = new ByteArrayOutputStream();
            sedApplication.run(args, stdinTwo, osPrint);
            assertEquals("replaceTwo\t line twoLine aline" + STRING_NEWLINE, osPrint.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Test valid filename in run method with a file with two lines/
     * Expected: print both lines with replacements based on replacement index.
     */
    @Test
    void testRunValidFileNameInRunMethodWithTwoLinesShouldPrintFileContentsWithReplacements() throws IOException, SedException {
        String[] args = {VALID_ARG, fileWithTwoLines.toPath().toString()};
        try(InputStream stdinTwo = new FileInputStream(fileWithTwoLines.toPath().toString())) {
            OutputStream osPrint = new ByteArrayOutputStream();
            sedApplication.run(args, stdinTwo, osPrint);
            String expected = REPLACE_TWO + STRING_NEWLINE + REPLACED_LINE + STRING_NEWLINE;
            assertEquals(expected, osPrint.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Test run method with non-'s' invalid first character.
     * Expected: Throw exception with ERR_INVALID_REP_RULE message.
     */
    @Test
    void testRunInvalidFirstArgumentInRunMethodShouldThrowSedException() {
        String[] args = {"invalid|line|replace|1"};
        InputStream stdinTwo = new ByteArrayInputStream(TWO_LINES.getBytes());
        SedException thrown = assertThrows(SedException.class, () -> {
            sedApplication.run
                    (args, stdinTwo, outputStreamOne);
        });
        assertEquals(SED_EXCEPTION + ERR_INVALID_REP_RULE, thrown.getMessage());
    }

    /**
     * Test run method with negative replacement index
     * Expected: Exception thrown, with ERR_INVALID_REP_X message.
     */
    @Test
    void testRunInvalidReplacementIndexInRunMethodShouldThrowSedException() throws SedException {
        String[] args = {"s|line|replace|-1"};
        try {
            InputStream stdinTwo = new ByteArrayInputStream(TWO_LINES.getBytes());
            SedException thrown = assertThrows(SedException.class, () -> {
                sedApplication.run
                        (args, stdinTwo, outputStreamOne);
            });
            assertEquals(SED_EXCEPTION + ERR_INVALID_REP_X, thrown.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Test run method with zero replacement index
     * Expected: Exception thrown, with ERR_INVALID_REP_X message.
     */
    @Test
    void testRunZeroReplacementIndexInRunMethodShouldThrowSedException() throws IOException {
        String[] args = {"s|line|replace|0"};

        InputStream stdinTwo = new ByteArrayInputStream(TWO_LINES.getBytes());
        SedException thrown = assertThrows(SedException.class, () -> {
            sedApplication.run
                    (args, stdinTwo, outputStreamOne);
        });
        assertEquals(SED_EXCEPTION + ERR_INVALID_REP_X, thrown.getMessage());
    }

    /**
     * Test run method with null stdout.
     * Expected: Throw exception with ERR_NULL_STREAMS message.
     */
    @Test
    void testRunNullStdoutInRunMethodShouldThrowSedException() {
        String[] args = {VALID_ARG};
        InputStream stdinTwo = new ByteArrayInputStream(TWO_LINES.getBytes());
        SedException thrown = assertThrows(SedException.class, () -> {
            sedApplication.run
                    (args, stdinTwo, null);
        });
        assertEquals(SED_EXCEPTION + ERR_NULL_STREAMS, thrown.getMessage());
    }
    /**
     * Test null regex in replaceSubstringInFile method.
     * Expected: Exception thrown, with ERR_NULL_ARGS message.
     */
    @Test
    void testRunEmptyRegexShouldThrowException() {
        String commandInput = "s///1";
        String[] args = {commandInput, fileWithTwoLines.toPath().toString()};
        try (InputStream stdin = new FileInputStream(fileWithTwoLines.toPath().toString());) {
            OutputStream osPrint = new ByteArrayOutputStream();
            Exception thrown = assertThrows(Exception.class, () -> {
                sedApplication.run(
                        args, stdin, osPrint);
            });
            assertEquals(SED_EXCEPTION + ERR_EMPTY_REGEX, thrown.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Test replaceSubstringInFile method with invalid replacement index.
     * Expected: Exception thrown, ERR_INVALID_REP_X message.
     */
    @Test
    void testRunInvalidReplacementIndexShouldThrowException() throws FileNotFoundException {
        String[] args = {"s/line/replace/-1", fileWithTwoLines.toPath().toString()};
        try (InputStream stdin = new FileInputStream(fileWithTwoLines.toPath().toString())) {
            OutputStream osPrint = new ByteArrayOutputStream();
            Exception thrown = assertThrows(Exception.class, () -> {
                sedApplication.run(
                        args, stdin, osPrint);
            });
            assertEquals(SED_EXCEPTION + ERR_INVALID_REP_X, thrown.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Test run method with other separating character
     * Expected: Print out the file contents with the content replaced accordingly based on replacement index.
     */
    @Test
    void testRunOtherSeparatingCharacterShouldWorkTheSameWay() throws SedException, FileNotFoundException {
        String[] args = {VALID_ARG, fileWithTwoLines.toPath().toString()};
        OutputStream osPrint = new ByteArrayOutputStream();
        try (InputStream stdin = new FileInputStream(fileWithTwoLines.toPath().toString())) {
            sedApplication.run(args, stdin, osPrint);
            String expected = REPLACE_TWO + STRING_NEWLINE + REPLACED_LINE + STRING_NEWLINE;
            assertEquals(expected, osPrint.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Test run method with null arguments.
     * Expected: Exception thrown with ERR_NULL_ARGS message.
     */
    @Test
    void runNullArgsInRunMethodShouldThrowException() {
        InputStream stdinTwo = new ByteArrayInputStream(TWO_LINES.getBytes());
        SedException thrown = assertThrows(SedException.class, () -> {
            sedApplication.run
                    (null, stdinTwo, outputStreamTwo);
        });
        assertEquals(SED_EXCEPTION + ERR_NULL_ARGS, thrown.getMessage());
    }

    /**
     * Test run method with empty arguments.
     * Expected: Throw exception with
     */
    @Test
    void runEmptyArgsInRunMethodShouldThrowException() {
        String[] args = new String[0];
        InputStream stdinTwo = new ByteArrayInputStream(TWO_LINES.getBytes());
        SedException thrown = assertThrows(SedException.class, () -> {
            sedApplication.run
                    (args, stdinTwo, outputStreamTwo);
        });
        assertEquals(SED_EXCEPTION + ERR_NO_ARGS, thrown.getMessage());
    }

}
