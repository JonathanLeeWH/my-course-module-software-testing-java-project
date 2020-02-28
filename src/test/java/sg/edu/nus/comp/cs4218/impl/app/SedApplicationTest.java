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
    private static final String REPLACED_LINE = " replace twoLine aline";
    private static final String VALID_ARG = "s|line|replace|1";
    private static final String SED_EXCEPTION = "sed: ";
    private static final String REPLACE_TWO = "replaceTwo";
    private static final String REPLACEMENT = "replacement";
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
        String expectedOutput = REPLACE_TWO + "\t line twoLine aline" + STRING_NEWLINE;
        int replacementIndex = 1;
        try(InputStream stdin = new ByteArrayInputStream(fileWithOneLine.toPath().toString().getBytes())) {
        assertEquals(expectedOutput,
                    sedApplication.replaceSubstringInStdin(LINE, REPLACE, replacementIndex, stdin));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Test replaceSubstringInStdin method with valid two-lined file line in inputStream.
     * Expected: Print out the file contents with the content replaced accordingly based on replacement index.
     */
    @Test
    void runStdinUsingMultiLineFileShouldReplaceWithReplacementTextInTheReplacementIndexForAllLines() throws Exception {
        String expectedOutput = REPLACE_TWO + STRING_NEWLINE + REPLACED_LINE + STRING_NEWLINE;
        InputStream stdinTwo = new ByteArrayInputStream(fileWithTwoLines.toPath().toString().getBytes());
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
        String expectedOutput = REPLACE_TWO+ STRING_NEWLINE + REPLACED_LINE + STRING_NEWLINE;
        int replacementIndex = 1;
        assertEquals(expectedOutput, sedApplication.replaceSubstringInFile
                (LINE, REPLACE, replacementIndex, fileWithTwoLines.toPath().toString()));
    }


    /**
     * Test null regex in replaceSubstringInFile method.
     * Expected: Exception thrown, with ERR_NULL_ARGS message.
     */
    @Test
    void runEmptyRegexShouldThrowException() {
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
    void runInvalidReplacementIndexShouldThrowException() throws FileNotFoundException {
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
    void runOtherSeparatingCharacterShouldWorkTheSameWay() throws SedException, FileNotFoundException {
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
     * Test replaceSubstringInFile method with Directory instead of filename.
     * Expected: Exception thrown, with ERR_IS_DIR message.
     */
    @Test
    void runDirectoryInsteadOfFileNameInReplaceSubstringInFileMethodShouldThrowException() {
        Exception thrown = assertThrows(Exception.class, () -> {
            sedApplication.replaceSubstringInFile
                    ("", REPLACEMENT, 1, EnvironmentHelper.currentDirectory);
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
                    ("valid", REPLACEMENT, 1, "invalidFile");
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
                    ("valid", REPLACEMENT, 1, null);
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

    /**
     * Test run method with null stdout.
     * Expected: Throw exception with ERR_NULL_STREAMS message.
     */
    @Test
    void runNullStdoutInRunMethodShouldThrowSedException() {
        String[] args = {VALID_ARG};
        InputStream stdinTwo = new ByteArrayInputStream(TWO_LINES.getBytes());
        SedException thrown = assertThrows(SedException.class, () -> {
            sedApplication.run
                    (args, stdinTwo, null);
        });
        assertEquals(SED_EXCEPTION + ERR_NULL_STREAMS, thrown.getMessage());
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
        assertEquals(SED_EXCEPTION + ERR_INVALID_REP_RULE, thrown.getMessage());
    }

    /**
     * Test run method with negative replacement index
     * Expected: Exception thrown, with ERR_INVALID_REP_X message.
     */
    @Test
    void runInvalidReplacementIndexInRunMethodShouldThrowSedException() throws SedException {
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
    void runZeroReplacementIndexInRunMethodShouldThrowSedException() throws IOException {
        String[] args = {"s|line|replace|0"};

        InputStream stdinTwo = new ByteArrayInputStream(TWO_LINES.getBytes());
        SedException thrown = assertThrows(SedException.class, () -> {
            sedApplication.run
                    (args, stdinTwo, outputStreamOne);
        });
        assertEquals(SED_EXCEPTION + ERR_INVALID_REP_X, thrown.getMessage());
    }

    /**
     * Test replaceSubstringInStdin with valid replacement index of 2.
     * Expected: Print file contents with replacements made based on the replacement index.
     */
    @Test
    void runReplacementIndexTwoInReplaceSubstringInStdinShouldPrintFileContentsWithReplacements() throws Exception {
        try (InputStream stdinTwo = new FileInputStream(fileWithOneLine.toPath().toString())) {
            assertEquals(ONE_LINE + STRING_NEWLINE,
                    sedApplication.replaceSubstringInStdin("line", "replace", 2, stdinTwo));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Test replaceSubstringInStdin with valid replacement index of 2.
     * Expected: Print file contents with replacements made based on the replacement index.
     */
    @Test
    void runReplacementIndexMaxIntInReplaceSubstringInStdinShouldPrintFileContentsWithoutReplacements() throws Exception {
        try (InputStream stdinTwo = new FileInputStream(fileWithOneLine.toPath().toString())) {
            assertEquals(ONE_LINE + STRING_NEWLINE,
                    sedApplication.replaceSubstringInStdin("line",
                            "replace", Integer.MAX_VALUE, stdinTwo));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Test replaceSubstringInStdin method with null stdin.
     * Expected: Exception thrown, with ERR_NULL_STREAMS message.
     */
    @Test
    void runNullStdinInReplaceSubstringInStdinShouldThrowException() {
        Exception thrown = assertThrows(Exception.class, () -> {
            sedApplication.replaceSubstringInStdin
                    ("valid", REPLACEMENT, 1, null);
        });
        assertEquals(ERR_NULL_STREAMS, thrown.getMessage());
    }

    /**
     * Test run method with valid filename.
     * Expected: Print file contents with replaced contents based on the replacement index.
     */
    @Test
    void runValidFileNamesInRunShouldPrintFileContentsWithReplacements() throws IOException, SedException {
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
    void runValidFileNameInRunMethodWithTwoLinesShouldPrintFileContentsWithReplacements() throws IOException, SedException {
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
}
