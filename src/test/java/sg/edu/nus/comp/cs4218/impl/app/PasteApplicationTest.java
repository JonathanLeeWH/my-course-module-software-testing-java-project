package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.*;

import sg.edu.nus.comp.cs4218.exception.PasteException;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_INVALID_FILE;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.FILE_NOT_FOUND;

class PasteApplicationTest {
    private static final String FILE_TYPE = ".txt";
    private static final String EMPTY_FILE = "testFileZero";
    private static final String ONE_LINE_FILE = "singleLineFile";
    private static final String TWO_LINES_FILE = "twoLinesFile";
    private static final String EMPTY = "";
    private static final String TWO_LINES = "Line One" + System.lineSeparator() + "Line Two";
    private static final String ONE_LINE = "Line One";
    private static final String NEW_LINE = "\n";
    private static File emptyFile;
    private static File twoLinesFile;
    private static File oneLineFile;
    private static PasteApplication pasteApplication;
    private static OutputStream osZero, osTwo, osOne, osPrint;
    private static final String FIRST_LINE = "Line One";
    private static final String SECOND_LINE = "Line Two";
    private static final String PASTE_EXCEPTION = "paste: ";

    @BeforeEach
    void setupBeforeTest() {
        pasteApplication = new PasteApplication();
        try {
            emptyFile = File.createTempFile(EMPTY_FILE, FILE_TYPE);
            twoLinesFile = File.createTempFile(TWO_LINES_FILE, FILE_TYPE);
            oneLineFile = File.createTempFile(ONE_LINE_FILE, FILE_TYPE);
            osZero = new FileOutputStream(emptyFile);
            osTwo = new FileOutputStream(twoLinesFile);
            osOne = new FileOutputStream(oneLineFile);
            osZero.write(EMPTY.getBytes()   );
            osTwo.write(TWO_LINES.getBytes());
            osOne.write(ONE_LINE.getBytes());
            osPrint = new ByteArrayOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @AfterEach
    void tearDownAfterTesting() {
        emptyFile.deleteOnExit();
        twoLinesFile.deleteOnExit();
        oneLineFile.deleteOnExit();
        try {
            osZero.close();
            osTwo.close();
            osOne.close();
            osPrint.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Test mergeFile method when filename is invalid.
     *  Expected: Throws FileNotFound Exception
     */
    @Test
    void runInvalidFileShouldThrowPasteException() {
        String invalidFile = "invalidTest";
        String[] args = { invalidFile };
        Exception thrown = assertThrows(PasteException.class, () -> {
            pasteApplication.mergeFile(args);
        });
        String expected = PASTE_EXCEPTION + FILE_NOT_FOUND;
        assertEquals(expected, thrown.getMessage());
    }

    /**
     *  Test run when dash is not in the first argument.
     *  Expected: Throws FileNotFound Exception
     */
    @Test
    void runDashInSecondArgumentShouldThrowPasteException() {
        String[] args = {"ok.txt", "-"};
        try (InputStream inputStream = new FileInputStream(twoLinesFile)) {
            assertThrows(PasteException.class, () -> {
                pasteApplication.run(args, inputStream, osTwo);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Test mergeFile method when file content is empty.
     *  Expected: Print Nothing.
     */
    @Test
    void runEmptyFileContentShouldPrintNothing() throws Exception {
        String[] fileNames = new String[1];
        fileNames[0] = emptyFile.toPath().toString();
        assertEquals("", pasteApplication.mergeFile(fileNames));
    }

    /**
     * Test mergeFile method when filename is the name of a file with two lines.
     *  Expected: Returns a string of the file contents and terminates with a newline.
     */
    @Test
    void runTwoLinesFileShouldPrintTwoLines() throws Exception {
        String[] fileName = new String[1];
        fileName[0] = twoLinesFile.toPath().toString();
        assertEquals(TWO_LINES, pasteApplication.mergeFile(fileName));
    }

    /**
     * Test mergeFile method when filename is the name of a file with one line.
     *  Expected: Returns a string of the file contents and terminates with a newline.
     */
    @Test
    void runOneLineFileShouldPrintOneLines() throws Exception {
        String[] fileName = new String[1];
        fileName[0] = oneLineFile.toPath().toString();
        assertEquals(ONE_LINE, pasteApplication.mergeFile(fileName));
    }

    /**
     * Test mergeFile method when two filenames are given.
     *  Expected: Returns a string with the two file contents merged (tab-concatenated).
     */
    @Test
    void runMergeMultipleFilesShouldMergeAllFilesAndPrintMergedContents() throws Exception {
        String tab = "\t";
        String[] args = { twoLinesFile.toPath().toString(), oneLineFile.toPath().toString() };
        String expectedOutput = FIRST_LINE + tab + FIRST_LINE + System.lineSeparator()
                + SECOND_LINE;
        String actualOutput = pasteApplication.mergeFile(args);
        assertEquals(expectedOutput, actualOutput);
    }

    /**
     * Test mergeFile method when three filenames (including one empty file) are given.
     *  Expected: Returns a string with the three file contents merged (tab-concatenated).
     */
    @Test
    void runMergeMultipleFilesThatIncludesOneEmptyFileShouldMergeAllFilesAndPrintMergedContents() throws Exception {
        String tab = "\t";
        String[] args = { twoLinesFile.toPath().toString(), oneLineFile.toPath().toString(), emptyFile.toPath().toString() };
        String expectedOutput = FIRST_LINE + tab + FIRST_LINE + System.lineSeparator()
                + SECOND_LINE;
        String actualOutput = pasteApplication.mergeFile(args);
        assertEquals(expectedOutput, actualOutput);
    }

    /**
     * Test mergeStdin method with null stdin.
     *  Expected: PasteException
     */
    @Test
    void runNullStdinShouldThrowPasteException() {
        assertThrows(PasteException.class, () -> {
            pasteApplication.mergeStdin(null);
        });
    }

    /**
     * Test mergeStdin method when no filenames are given and Stdin contains only one file that has a single line.
     *  Expected: Returns a string of the file contents and terminates with a newline.
     */
    @Test
    void runStdinSingleLineShouldPrintSingleLine() throws Exception {
        try(InputStream inputStream = new FileInputStream(oneLineFile.toPath().toString())) {
            assertEquals(ONE_LINE, pasteApplication.mergeStdin(inputStream));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Test mergeStdin method when no filenames are given and Stdin contains only one file that has multiple lines.
     *  Expected: Returns a string of the file contents and terminates with a newline.
     */
    @Test
    void runStdinMultipleLinesOnMergeStdinMethodShouldPrintMultipleLines() throws Exception {
        try(InputStream inputStream = new FileInputStream(twoLinesFile.toPath().toString())) {
            assertEquals(TWO_LINES, pasteApplication.mergeStdin(inputStream));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Test mergeFileAndStdin method when one filename is given and Stdin contains only one file.
     *  Expected: Returns a string with the two file contents merged (tab-concatenated).
     */
    @Test
    void runMergeStdinAndSingleFileShouldMergeAllFilesAndPrintMergedContents() throws Exception {
        String tab = "\t";
        try (InputStream inputStream = new FileInputStream(twoLinesFile.toPath().toString())) {
            String[] fileNames = { twoLinesFile.toPath().toString() };
            String expectedOutput = FIRST_LINE + tab + FIRST_LINE + System.lineSeparator()
                    + SECOND_LINE + tab + SECOND_LINE;
            assertEquals(expectedOutput, pasteApplication.mergeFileAndStdin(inputStream, fileNames));
        }
    }

    /**
     * Test mergeStdin method when no filenames are given and Stdin contains only one file that has a single line.
     *  Expected: Returns a string of the file contents and terminates with a newline.
     */
    @Test
    void runStdinMultiLineWithDashShouldPrintMergedContents() throws Exception {
        String input = oneLineFile.toPath().toString() + " " + oneLineFile.toPath().toString();
        try(InputStream inputStream = new ByteArrayInputStream(input.getBytes())) {
            String[] args = {"-"};
            pasteApplication.run(args, inputStream, osOne);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Test mergeStdin method when no filenames are given and Stdin contains only one file that has a single line.
     *  Expected: Returns a string of the file contents and terminates with a newline.
     */
    @Test
    void runStdinSingleLineWithDashShouldPrintSingleLine() throws Exception {
        try(InputStream inputStream = new ByteArrayInputStream(oneLineFile.toPath().toString().getBytes())) {
            String[] args = {"-"};
            pasteApplication.run(args, inputStream, osOne);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Test run method with null outputStream.
     *  Expected: PasteException.
     */
    @Test
    void runNullOutputStreamShouldThrowPasteException() {
        try(InputStream inputStream = new ByteArrayInputStream(oneLineFile.toPath().toString().getBytes())) {
            String[] args = {"-"};
            assertThrows(PasteException.class, () -> {
                pasteApplication.run(args, inputStream, null);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *  Test run method with null outputStream.
     *  Expected: String of merged contents from the input files.
     */
    @Test
    void runBothStdinAndFileNamesInRunShouldReturnMergedContents() throws Exception {
        try (InputStream inputStream = new FileInputStream(oneLineFile.toPath().toString())) {
            String[] args = {"-", oneLineFile.toPath().toString()};
            pasteApplication.run(args, inputStream, osPrint);
            assertEquals(FIRST_LINE + "\t" + FIRST_LINE + System.lineSeparator(),
                osPrint.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *  Test run method with null outputStream.
     *  Expected: String of merged contents from the input files.
     */
    @Test
    void runNullStdinAndNullStdoutInRunShouldThrowPasteException() {
        String[] args = {"-", oneLineFile.toPath().toString()};
        assertThrows(PasteException.class, () -> {
            pasteApplication.run(args, null, null);
        });
    }


    /**
     *  Test run method with null outputStream.
     *  Expected: String of merged contents from the input files.
     */
    @Test
    void runDoubleDashShouldThrowPasteException() {
        try(InputStream inputStream = new ByteArrayInputStream(oneLineFile.toPath().toString().getBytes())) {
            String[] args = {"-", "-"};
            assertThrows(PasteException.class, () -> {
                pasteApplication.run(args, inputStream, osPrint);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *  Test run method with null outputStream.
     *  Expected: String of merged contents from the input files.
     */
    @Test
    void runNullStdinWithoutArgsShouldThrowPasteException() {
        try(InputStream inputStream = new ByteArrayInputStream(oneLineFile.toPath().toString().getBytes())) {
            String[] args = new String[0];
            assertThrows(PasteException.class, () -> {
                pasteApplication.run(args, null, osPrint);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
