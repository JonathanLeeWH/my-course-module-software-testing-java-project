package test.sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.impl.app.PasteApplication;
import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

public class PasteApplicationTest {
    private static final String testFileType = ".txt";
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

    @BeforeAll
    public static void setupBeforeTest() throws IOException {
        pasteApplication = new PasteApplication();
        try {
            emptyFile = File.createTempFile(TEST_EMPTY_FILE_NAME, testFileType);
            fileWithTwoLines = File.createTempFile(TEST_FILE_WITH_TWO_LINES_NAME, testFileType);
            fileWithOneLine = File.createTempFile(TEST_FILE_WITH_ONE_LINE_NAME, testFileType);
            OutputStream outputStreamZero = new FileOutputStream(emptyFile);
            OutputStream outputStreamOne = new FileOutputStream(fileWithTwoLines);
            OutputStream outputStreamTwo = new FileOutputStream(fileWithOneLine);
            outputStreamZero.write(TEXT_EMPTY_FILE.getBytes());
            outputStreamOne.write(TEXT_FILE_WITH_TWO_LINES.getBytes());
            outputStreamTwo.write(TEXT_FILE_WITH_ONE_LINE.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @AfterAll
    public static void deleteTempFilesUponExit() {
        emptyFile.deleteOnExit();
        fileWithTwoLines.deleteOnExit();
        fileWithOneLine.deleteOnExit();
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
        assertEquals(TEXT_FILE_WITH_TWO_LINES + "\n", pasteApplication.mergeFile(fileName));
    }

    @Test
    public void execute_printSingleLineWhenOneSingleLineFileIsGiven_success() throws Exception {
        String[] fileName = new String[1];
        fileName[0] = fileWithOneLine.toPath().toString();
        String actualOutput = pasteApplication.mergeFile(fileName);
        assertEquals(TEXT_FILE_WITH_ONE_LINE + "\n", actualOutput);
    }

    @Test
    public void execute_mergeMultipleFiles_success() throws Exception {
        String[] args = { fileWithTwoLines.toPath().toString(), fileWithOneLine.toPath().toString() };
        String expectedOutput = "First Line" + "\t" + "Only One Line" + "\n" + "Second Line" + "\t";
        String actualOutput = pasteApplication.mergeFile(args);
        assertEquals(expectedOutput, actualOutput);
    }


}
