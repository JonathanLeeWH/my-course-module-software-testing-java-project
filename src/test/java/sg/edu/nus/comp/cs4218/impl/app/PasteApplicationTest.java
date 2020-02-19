package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

public class PasteApplicationTest {
    private static final String TEST_FILE_TYPE = ".txt";
    private static final String TEST_EMPTY_FILE_NAME = "testFileZero";
    private static final String TEST_FILE_WITH_TWO_LINES_NAME = "testFileOne";
    private static final String TEST_FILE_WITH_ONE_LINE_NAME = "testFileTwo";
    private static final String TEXT_EMPTY_FILE = "";
    private static final String TEXT_FILE_WITH_TWO_LINES = "First Line" + System.lineSeparator() + "Second Line";
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
            outputStreamZero.write(TEXT_EMPTY_FILE.getBytes()   );
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

    /**
     * Test mergeFile method when filename is invalid.
     *  Expected: Throws FileNotFound Exception
     */
    @Test
    public void executeInvalidFileThrowsFileNotFoundException() {
        String[] args = { "invalidTest" };
        assertThrows(FileNotFoundException.class, () -> {
            pasteApplication.mergeFile(args);
        });
    }

    /**
     * Test mergeFile method when filename is empty.
     *  Expected: Throws FileNotFound Exception
     */
    @Test
    public void executeEmptyFileNameThrowFileNotFoundException() {
        String[] args = { fileWithOneLine.toPath().toString(), ""};
        assertThrows(FileNotFoundException.class, () -> {
            pasteApplication.mergeFile(args);
        });
    }

    /**
     * Test mergeFile method when filename is empty.
     *  Expected: Throws FileNotFound Exception
     */
    @Test
    public void executePrintNothingWhenOneEmptyFileIsGivenSuccess() throws Exception {
        String[] fileNames = new String[1];
        fileNames[0] = emptyFile.toPath().toString();
        assertEquals("", pasteApplication.mergeFile(fileNames));
    }

    /**
     * Test mergeFile method when filename is the name of a file with two lines.
     *  Expected: Returns a string of the file contents and terminates with a newline.
     */
    @Test
    public void executePrintTwoLinesWhenATwoLinesFileIsGivenSuccess() throws Exception {
        String[] fileName = new String[1];
        fileName[0] = fileWithTwoLines.toPath().toString();
        assertEquals(TEXT_FILE_WITH_TWO_LINES, pasteApplication.mergeFile(fileName));
    }

    /**
     * Test mergeFile method when filename is the name of a file with one line.
     *  Expected: Returns a string of the file contents and terminates with a newline.
     */
    @Test
    public void executePrintSingleLineWhenOneSingleLineFileIsGivenSuccess() throws Exception {
        String[] fileName = new String[1];
        fileName[0] = fileWithOneLine.toPath().toString();
        String actualOutput = pasteApplication.mergeFile(fileName);
        assertEquals(TEXT_FILE_WITH_ONE_LINE, actualOutput);
    }

    /**
     * Test mergeFile method when two filenames are given.
     *  Expected: Returns a string with the two file contents merged (tab-concatenated).
     */
    @Test
    public void executeMergeMultipleFilesSuccess() throws Exception {
        String[] args = { fileWithTwoLines.toPath().toString(), fileWithOneLine.toPath().toString() };
        String expectedOutput = "First Line" + "\t" + "Only One Line" + System.lineSeparator() + "Second Line";
        String actualOutput = pasteApplication.mergeFile(args);
        assertEquals(expectedOutput, actualOutput);
    }

    /**
     * Test mergeStdin method when no filenames are given and Stdin contains only one file that has a single line.
     *  Expected: Returns a string of the file contents and terminates with a newline.
     */
    @Test
    public void executePrintSingleLineWhenStdinSingleLineSuccess() throws Exception {
        InputStream inputStream = new FileInputStream(fileWithOneLine);
        assertEquals(TEXT_FILE_WITH_ONE_LINE, pasteApplication.mergeStdin(inputStream));
    }

    /**
     * Test mergeStdin method when no filenames are given and Stdin contains only one file that has multiple lines.
     *  Expected: Returns a string of the file contents and terminates with a newline.
     */
    @Test
    public void executePrintStdinMultipleLinesSuccess() throws Exception {
        InputStream inputStream = new FileInputStream(fileWithTwoLines);
        assertEquals(TEXT_FILE_WITH_TWO_LINES, pasteApplication.mergeStdin(inputStream));
    }

    /**
     * Test mergeFileAndStdin method when one filename is given and Stdin contains only one file.
     *  Expected: Returns a string with the two file contents merged (tab-concatenated).
     */
    @Test
    public void executeMergeStdinAndSingleFileSuccess() throws Exception {
        InputStream inputStream = new FileInputStream(fileWithTwoLines);
        String[] fileNames = { fileWithTwoLines.toPath().toString() };
        String expectedOutput = "First Line" + "\t" + "First Line" + "\n" + "Second Line" + "\t" + "Second Line";
        assertEquals(expectedOutput, pasteApplication.mergeFileAndStdin(inputStream, fileNames));
    }
}
