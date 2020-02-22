package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.exception.SedException;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;

public class SedApplicationTest {
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
    public static void setupBeforeTest() {
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
    public static void tearDownAfterTesting() {
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

    @Test
    public void runStdinUsingSingleLineFileShouldReplaceWithReplacementTextInTheReplacementIndex() throws Exception {
        String expectedOutput = "replaceTwo\t line twoLine aline" + STRING_NEWLINE;
        String pattern = LINE;
        String replacement = REPLACE;
        int replacementIndex = 1;
        InputStream stdinOne = new ByteArrayInputStream(ONE_LINE.getBytes());
        assertEquals(expectedOutput,
                sedApplication.replaceSubstringInStdin(pattern, replacement, replacementIndex, stdinOne));
    }

    @Test
    public void runStdinUsingMultiLineFileShouldReplaceWithReplacementTextInTheReplacementIndexForAllLines() throws Exception {
        String expectedOutput = "replaceTwo"+ STRING_NEWLINE + " replace twoLine aline" + STRING_NEWLINE;
        String pattern = LINE;
        String replacement = REPLACE;
        InputStream stdinTwo = new ByteArrayInputStream(TWO_LINES.getBytes());
        int replacementIndex = 1;
        assertEquals(expectedOutput, sedApplication.replaceSubstringInStdin
                (pattern, replacement, replacementIndex, stdinTwo));
    }

    @Test
    public void runFileNameUsingSingleLineFileShouldReplaceWithReplacementTextInTheReplacementIndex() throws Exception {
        String expectedOutput = "replaceTwo\t line twoLine aline" + STRING_NEWLINE;
        String pattern = LINE;
        String replacement = REPLACE;
        int replacementIndex = 1;
        assertEquals(expectedOutput, sedApplication.replaceSubstringInFile
                (pattern, replacement, replacementIndex, fileWithOneLine.toPath().toString()));
    }

    @Test
    public void runFileNameUsingMultiLineFileShouldReplaceWithReplacementTextInTheReplacementIndexForAllLines() throws Exception {
        String expectedOutput = "replaceTwo"+ STRING_NEWLINE + " replace twoLine aline" + STRING_NEWLINE;
        String pattern = LINE;
        String replacement = REPLACE;
        int replacementIndex = 1;
        assertEquals(expectedOutput, sedApplication.replaceSubstringInFile
                (pattern, replacement, replacementIndex, fileWithTwoLines.toPath().toString()));
    }

    @Test
    public void runEmptyRegexShouldThrowException() {
        String pattern = "";
        String replacement = " ";
        int replacementIndex = 1;
        Exception thrown = assertThrows(Exception.class, () -> {
            sedApplication.replaceSubstringInFile(
                    pattern, replacement, replacementIndex, fileWithTwoLines.toPath().toString());
        });
        assertEquals(thrown.getMessage(),ERR_EMPTY_REGEX);
    }

    @Test
    public void runNullRegexShouldThrowException() {
        String pattern = null;
        String replacement = " ";
        int replacementIndex = 1;
        Exception thrown = assertThrows(Exception.class, () -> {
            sedApplication.replaceSubstringInFile(
                    pattern, replacement, replacementIndex, fileWithTwoLines.toPath().toString());
        });
        assertEquals(thrown.getMessage(),ERR_NULL_ARGS);
    }

    @Test
    public void runInvalidReplacementIndexShouldThrowException() throws Exception {
        String pattern = LINE;
        String replacement = REPLACE;
        int replacementIndex = -1;
        Exception thrown = assertThrows(Exception.class, () -> {
            sedApplication.replaceSubstringInFile(
                    pattern, replacement, replacementIndex, fileWithTwoLines.toPath().toString());
        });
        assertEquals(thrown.getMessage(), ERR_INVALID_REP_X);
    }

    @Test
    public void runOtherSeparatingCharacterShouldWorkTheSameWay() throws SedException {
        String[] args = {"s|line|replace|1"};
        InputStream stdinTwo = new ByteArrayInputStream(TWO_LINES.getBytes());
        sedApplication.run(args, stdinTwo, outputStreamOne);
    }
}
