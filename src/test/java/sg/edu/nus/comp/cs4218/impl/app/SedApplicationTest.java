package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.EnvironmentHelper;
import sg.edu.nus.comp.cs4218.exception.SedException;

import javax.print.DocFlavor;
import java.io.*;

import static org.junit.jupiter.api.Assertions.*;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;

class SedApplicationTest {
    private static SedApplication sedApplication;
    private static final String TEST_FILE_TYPE = ".txt";
    private static final String EMPTY_FILE = "testFileZero";
    private static final String TWO_LINES_NAME = "testFileOne";
    private static final String ONE_LINE_NAME = "testFileTwo";
    private static final String TEXT_EMPTY_FILE = "";
    private static final String TWO_LINES = "lineTwo" + STRING_NEWLINE + " line twoLine aline";
    private static final String ONE_LINE = "lineTwo" + "\t" + " line twoLine aline";
    private static final String LINE = "line";
    private static final String REPLACE = "replace";
    private static File emptyFile;
    private static File fileWithTwoLines;
    private static File fileWithOneLine;
    private static OutputStream outputStreamZero, outputStreamOne, outputStreamTwo;

    @BeforeAll
    static void setupBeforeTest() {
        sedApplication = new SedApplication();
        try {
            emptyFile = File.createTempFile(EMPTY_FILE, TEST_FILE_TYPE);
            fileWithTwoLines = File.createTempFile(TWO_LINES_NAME, TEST_FILE_TYPE);
            fileWithOneLine = File.createTempFile(ONE_LINE_NAME, TEST_FILE_TYPE);
            outputStreamZero = new FileOutputStream(emptyFile);
            outputStreamOne = new FileOutputStream(fileWithTwoLines);
            outputStreamTwo = new FileOutputStream(fileWithOneLine);
            outputStreamZero.write(TEXT_EMPTY_FILE.getBytes());
            outputStreamOne.write(TWO_LINES.getBytes());
            outputStreamTwo.write(ONE_LINE.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @AfterAll
    static void tearDownAfterTesting() {
        emptyFile.deleteOnExit();
        fileWithTwoLines.deleteOnExit();
        fileWithOneLine.deleteOnExit();
        try {
            outputStreamZero.close();
            outputStreamOne.close();
            outputStreamTwo.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Test replaceSubstringInStdin method with valid single file line in inputStream.
     * Expected: Print out the file contents with the content replaced accordingly based on replacement index.
     */
    @Test
    void runStdinUsingSingleLineFileShouldReplaceWithReplacementTextInTheReplacementIndex() throws Exception {
        String expectedOutput = "replaceTwo\t line twoLine aline" + STRING_NEWLINE;
        int replacementIndex = 1;
        InputStream stdinOne = new FileInputStream(fileWithOneLine.toPath().toString());
        assertEquals(expectedOutput,
                sedApplication.replaceSubstringInStdin(LINE, REPLACE, replacementIndex, stdinOne));
    }

    /**
     * Test replaceSubstringInStdin method with valid two-lined file line in inputStream.
     * Expected: Print out the file contents with the content replaced accordingly based on replacement index.
     */
    @Test
    void runStdinUsingMultiLineFileShouldReplaceWithReplacementTextInTheReplacementIndexForAllLines() throws Exception {
        String expectedOutput = "replaceTwo"+ STRING_NEWLINE + " replace twoLine aline" + STRING_NEWLINE;
        InputStream stdinTwo = new ByteArrayInputStream(TWO_LINES.getBytes());
        int replacementIndex = 1;
        assertEquals(expectedOutput, sedApplication.replaceSubstringInStdin
                (LINE, REPLACE, replacementIndex, stdinTwo));
    }

    /**
     * Test replaceSubstringInFile method with valid filename (file with single line).
     * Expected: Print out the file contents with the content replaced accordingly based on replacement index.
     */
    @Test
    void runFileNameUsingSingleLineFileShouldReplaceWithReplacementTextInTheReplacementIndex() throws Exception {
        String expectedOutput = "replaceTwo\t line twoLine aline" + STRING_NEWLINE;
        int replacementIndex = 1;
        assertEquals(expectedOutput, sedApplication.replaceSubstringInFile
                (LINE, REPLACE, replacementIndex, fileWithOneLine.toPath().toString()));
    }

    /**
     * Test replaceSubstringInFile method with valid filename (file with two lines).
     * Expected: Print out the file contents with the content replaced accordingly based on replacement index.
     */
    @Test
    void runFileNameUsingMultiLineFileShouldReplaceWithReplacementTextInTheReplacementIndexForAllLines() throws Exception {
        String expectedOutput = "replaceTwo"+ STRING_NEWLINE + " replace twoLine aline" + STRING_NEWLINE;
        int replacementIndex = 1;
        assertEquals(expectedOutput, sedApplication.replaceSubstringInFile
                (LINE, REPLACE, replacementIndex, fileWithTwoLines.toPath().toString()));
    }

    /**
     * Test Empty Regex in replaceSubstringInFile method.
     * Expected: Throw Exception with ERR_EMPTY_REGEX message.
     */
    @Test
    void runEmptyRegexInReplaceSubstringInFileMethodShouldThrowException() {
        String pattern = "";
        String replacement = " ";
        int replacementIndex = 1;
        Exception thrown = assertThrows(Exception.class, () -> {
            sedApplication.replaceSubstringInFile(
                    pattern, replacement, replacementIndex, fileWithTwoLines.toPath().toString());
        });
        assertEquals(thrown.getMessage(),ERR_EMPTY_REGEX);
    }

    /**
     * Test null regex in replaceSubstringInFile method.
     * Expected: Exception thrown, with ERR_NULL_ARGS message.
     */
    @Test
    void runNullRegexShouldThrowException() {
        String pattern = null;
        String replacement = " ";
        int replacementIndex = 1;
        Exception thrown = assertThrows(Exception.class, () -> {
            sedApplication.replaceSubstringInFile(
                    pattern, replacement, replacementIndex, fileWithTwoLines.toPath().toString());
        });
        assertEquals(thrown.getMessage(),ERR_NULL_ARGS);
    }

    /**
     * Test replaceSubstringInFile method with invalid replacement index.
     * Expected: Exception thrown, ERR_INVALID_REP_X message.
     */
    @Test
    void runInvalidReplacementIndexShouldThrowException() {
        int replacementIndex = -1;
        Exception thrown = assertThrows(Exception.class, () -> {
            sedApplication.replaceSubstringInFile(
                    LINE, REPLACE, replacementIndex, fileWithTwoLines.toPath().toString());
        });
        assertEquals(ERR_INVALID_REP_X, thrown.getMessage());
    }

    /**
     * Test run method with other separating character
     * Expected: Print out the file contents with the content replaced accordingly based on replacement index.
     */
    @Test
    void runOtherSeparatingCharacterShouldWorkTheSameWay() throws SedException {
        String[] args = {"s|line|replace|1"};
        OutputStream osPrint = new ByteArrayOutputStream();
        InputStream stdinTwo = new ByteArrayInputStream(TWO_LINES.getBytes());
        sedApplication.run(args, stdinTwo, osPrint);
        String expected = "replaceTwo" + STRING_NEWLINE + " replace twoLine aline" + STRING_NEWLINE;
        assertEquals(expected, osPrint.toString());
    }

    /**
     * Test replaceSubstringInFile method with Directory instead of filename.
     * Expected: Exception thrown, with ERR_IS_DIR message.
     */
    @Test
    void runDirectoryInsteadOfFileNameInReplaceSubstringInFileMethodShouldThrowException() {
        Exception thrown = assertThrows(Exception.class, () -> {
            sedApplication.replaceSubstringInFile
                    ("", "replacement", 1, EnvironmentHelper.currentDirectory);
        });
        assertEquals(ERR_IS_DIR, thrown.getMessage());
    }

    /**
     * Test replaceSubstringInFileMethod with invalid filename
     * Expected: Throw exception, with ERR_FILE_NOT_FOUND message.
     */
    @Test
    void runInvalidFileNameInReplaceSubstringInFileMethodShouldThrowException() {
        Exception thrown = assertThrows(Exception.class, () -> {
            sedApplication.replaceSubstringInFile
                    ("valid", "replacement", 1, "invalidFile");
        });
        assertEquals(ERR_FILE_NOT_FOUND, thrown.getMessage());
    }

    /**
     * Test null filename in replaceSubstringInFile method.
     * Expected: Throw exception, with ERR_NULL_ARGS message.
     */
    @Test
    void runNullFileNameInReplaceSubstringInFileMethodShouldThrowException() {
        Exception thrown = assertThrows(Exception.class, () -> {
            sedApplication.replaceSubstringInFile
                    ("valid", "replacement", 1, null);
        });
        assertEquals(ERR_NULL_ARGS, thrown.getMessage());
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
        assertEquals("sed: " + ERR_NULL_ARGS, thrown.getMessage());
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
        assertEquals("sed: " + ERR_NO_ARGS, thrown.getMessage());
    }

    /**
     * Test run method with null stdout.
     * Expected: Throw exception with ERR_NULL_STREAMS message.
     */
    @Test
    void runNullStdoutInRunMethodShouldThrowSedException() {
        String[] args = {"s|line|replace|1"};
        InputStream stdinTwo = new ByteArrayInputStream(TWO_LINES.getBytes());
        SedException thrown = assertThrows(SedException.class, () -> {
            sedApplication.run
                    (args, stdinTwo, null);
        });
        assertEquals("sed: " + ERR_NULL_STREAMS, thrown.getMessage());
    }

    /**
     * Test run method with non-'s' invalid first character.
     * Expected: Throw exception with ERR_INVALID_REP_RULE message.
     */
    @Test
    void runInvalidFirstArgumentInRunMethodShouldThrowSedException() {
        String[] args = {"invalid|line|replace|1"};
        InputStream stdinTwo = new ByteArrayInputStream(TWO_LINES.getBytes());
        SedException thrown = assertThrows(SedException.class, () -> {
            sedApplication.run
                    (args, stdinTwo, outputStreamOne);
        });
        assertEquals("sed: " + ERR_INVALID_REP_RULE, thrown.getMessage());
    }

    /**
     * Test run method with negative replacement index
     * Expected: Exception thrown, with ERR_INVALID_REP_X message.
     */
    @Test
    void runInvalidReplacementIndexInRunMethodShouldThrowSedException() throws SedException {
        String[] args = {"s|line|replace|-1"};
        InputStream stdinTwo = new ByteArrayInputStream(TWO_LINES.getBytes());
        SedException thrown = assertThrows(SedException.class, () -> {
            sedApplication.run
                    (args, stdinTwo, outputStreamOne);
        });
        assertEquals("sed: " + ERR_INVALID_REP_X, thrown.getMessage());
    }

    /**
     * Test run method with zero replacement index
     * Expected: Exception thrown, with ERR_INVALID_REP_X message.
     */
    @Test
    void runZeroReplacementIndexInRunMethodShouldThrowSedException() {
        String[] args = {"s|line|replace|0"};
        InputStream stdinTwo = new ByteArrayInputStream(TWO_LINES.getBytes());
        SedException thrown = assertThrows(SedException.class, () -> {
            sedApplication.run
                    (args, stdinTwo, outputStreamOne);
        });
        assertEquals("sed: " + ERR_INVALID_REP_X, thrown.getMessage());
    }

    /**
     * Test replaceSubstringInStdin with valid replacement index of 2.
     * Expected: Print file contents with replacements made based on the replacement index.
     */
    @Test
    void runReplacementIndexTwoInReplaceSubstringInStdinShouldPrintFileContentsWithReplacements() throws Exception {
        InputStream stdinTwo = new FileInputStream(fileWithOneLine.toPath().toString());
        assertEquals(ONE_LINE + STRING_NEWLINE,
                sedApplication.replaceSubstringInStdin("line", "replace", 2, stdinTwo));
    }

    /**
     * Test replaceSubstringInStdin with valid replacement index of 2.
     * Expected: Print file contents with replacements made based on the replacement index.
     */
    @Test
    void runReplacementIndexMaxIntInReplaceSubstringInStdinShouldPrintFileContentsWithoutReplacements() throws Exception {
        InputStream stdinTwo = new FileInputStream(fileWithOneLine.toPath().toString());
        assertEquals(ONE_LINE + STRING_NEWLINE,
                sedApplication.replaceSubstringInStdin("line",
                        "replace", Integer.MAX_VALUE, stdinTwo));
    }

    /**
     * Test replaceSubstringInStdin method with null stdin.
     * Expected: Exception thrown, with ERR_NULL_STREAMS message.
     */
    @Test
    void runNullStdinInReplaceSubstringInStdinShouldThrowException() {
        Exception thrown = assertThrows(Exception.class, () -> {
            sedApplication.replaceSubstringInStdin
                    ("valid", "replacement", 1, null);
        });
        assertEquals(ERR_NULL_STREAMS, thrown.getMessage());
    }

    /**
     * Test run method with valid filename.
     * Expected: Print file contents with replaced contents based on the replacement index.
     */
    @Test
    void runValidFileNamesInRunShouldPrintFileContentsWithReplacements() throws FileNotFoundException, SedException {
        String[] args = {"s|line|replace|1", fileWithOneLine.toPath().toString()};
        InputStream stdinTwo = new FileInputStream(fileWithOneLine.toPath().toString());
        OutputStream osPrint = new ByteArrayOutputStream();
        sedApplication.run(args, stdinTwo, osPrint);
        assertEquals("replaceTwo\t line twoLine aline" + STRING_NEWLINE, osPrint.toString());
    }

    /**
     * Test valid filename in run method with a file with two lines/
     * Expected: print both lines with replacements based on replacement index.
     */
    @Test
    void runValidFileNameInRunMethodWithTwoLinesShouldPrintFileContentsWithReplacements() throws FileNotFoundException, SedException {
        String[] args = {"s|line|replace|1", fileWithTwoLines.toPath().toString()};
        InputStream stdinTwo = new FileInputStream(fileWithTwoLines.toPath().toString());
        OutputStream osPrint = new ByteArrayOutputStream();
        sedApplication.run(args, stdinTwo, osPrint);
        String expected = "replaceTwo" + STRING_NEWLINE + " replace twoLine aline" + STRING_NEWLINE;
        assertEquals(expected, osPrint.toString());
    }
}
