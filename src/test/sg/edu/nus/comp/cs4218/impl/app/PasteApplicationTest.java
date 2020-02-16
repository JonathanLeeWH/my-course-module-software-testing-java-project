package test.sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.impl.app.PasteApplication;
import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

public class PasteApplicationTest {
    private static final String TEST_FILE_TYPE = ".txt";
    private static final String TEST_EMPTY_FILE_NAME = "testFileZero";
    private static final String TEST_FILE_WITH_TWO_LINES_NAME = "testFileOne";
    private static final String TEST_FILE_WITH_ONE_LINE_NAME = "testFileTwo";
    private static final String TEXT_EMPTY_FILE = "";
    private static final String TEXT_FILE_WITH_TWO_LINES = "First Line" + "\n" + "Second Line";
    private static final String TEXT_FILE_WITH_ONE_LINE = "Only One Line";
    private static File emptyFile;
    private static File fileWithTwoLines;
    private static File fileWithOneLine;
    private static PasteApplication pasteApplication;
    private static OutputStream outputStreamZero, outputStreamOne, outputStreamTwo;

    @BeforeAll
    public static void setupBeforeTest() throws IOException {
        pasteApplication = new PasteApplication();
        try {
            emptyFile = File.createTempFile(TEST_EMPTY_FILE_NAME, TEST_FILE_TYPE);
            fileWithTwoLines = File.createTempFile(TEST_FILE_WITH_TWO_LINES_NAME, TEST_FILE_TYPE);
            fileWithOneLine = File.createTempFile(TEST_FILE_WITH_ONE_LINE_NAME, TEST_FILE_TYPE);
            outputStreamZero = new FileOutputStream(emptyFile);
            outputStreamOne = new FileOutputStream(fileWithTwoLines);
            outputStreamTwo = new FileOutputStream(fileWithOneLine);
            outputStreamZero.write(TEXT_EMPTY_FILE.getBytes());
            outputStreamOne.write(TEXT_FILE_WITH_TWO_LINES.getBytes());
            outputStreamTwo.write(TEXT_FILE_WITH_ONE_LINE.getBytes());
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
    public void execute_invalidFile_throwsFileNotFoundException() {
        String[] args = { "invalidTest" };
        Exception thrown = assertThrows(FileNotFoundException.class, () -> {
            pasteApplication.mergeFile(args);
        });
        assertTrue(thrown.getMessage().contains("invalidTest (The system cannot find the file specified)"));
    }

    @Test
    public void execute_emptyFileName_throwFileNotFoundException() {
        String[] args = { fileWithOneLine.toPath().toString(), ""};
        Exception thrown = assertThrows(FileNotFoundException.class, () -> {
            pasteApplication.mergeFile(args);
        });
        assertEquals("", thrown.getMessage());
    }

    @Test
    public void execute_printNothingWhenOneEmptyFileIsGiven_success() throws Exception {
        String[] fileNames = new String[1];
        fileNames[0] = emptyFile.toPath().toString();
        assertEquals("", pasteApplication.mergeFile(fileNames));
    }

    @Test
    public void execute_printTwoLinesWhenATwoLinesFileIsGiven_success() throws Exception {
        String[] fileName = new String[1];
        fileName[0] = fileWithTwoLines.toPath().toString();
        assertEquals(TEXT_FILE_WITH_TWO_LINES, pasteApplication.mergeFile(fileName));
    }

    @Test
    public void execute_printSingleLineWhenOneSingleLineFileIsGiven_success() throws Exception {
        String[] fileName = new String[1];
        fileName[0] = fileWithOneLine.toPath().toString();
        String actualOutput = pasteApplication.mergeFile(fileName);
        assertEquals(TEXT_FILE_WITH_ONE_LINE, actualOutput);
    }

    @Test
    public void execute_mergeMultipleFiles_success() throws Exception {
        String[] args = { fileWithTwoLines.toPath().toString(), fileWithOneLine.toPath().toString() };
        String expectedOutput = "First Line" + "\t" + "Only One Line" + "\n" + "Second Line";
        String actualOutput = pasteApplication.mergeFile(args);
        assertEquals(expectedOutput, actualOutput);
    }

    @Test
    public void execute_printSingleLineWhenStdinSingleLine_success() throws Exception {
        InputStream inputStream = new FileInputStream(fileWithOneLine);
        assertEquals(TEXT_FILE_WITH_ONE_LINE, pasteApplication.mergeStdin(inputStream));
    }

    @Test
    public void execute_printStdinMultipleLines_success() throws Exception {
        InputStream inputStream = new FileInputStream(fileWithTwoLines);
        assertEquals(TEXT_FILE_WITH_TWO_LINES, pasteApplication.mergeStdin(inputStream));
    }

    @Test
    public void execute_mergeStdinAndSingleFile_success() throws Exception {
        InputStream inputStream = new FileInputStream(fileWithTwoLines);
        String[] fileNames = { fileWithTwoLines.toPath().toString() };
        String expectedOutput = "First Line" + "\t" + "First Line" + "\n" + "Second Line" + "\t" + "Second Line";
        assertEquals(expectedOutput, pasteApplication.mergeFileAndStdin(inputStream, fileNames));
    }
}
