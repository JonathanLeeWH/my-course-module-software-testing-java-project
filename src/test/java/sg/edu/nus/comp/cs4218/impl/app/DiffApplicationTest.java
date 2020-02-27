package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import sg.edu.nus.comp.cs4218.exception.DiffException;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

public class DiffApplicationTest {

    private static final String FILE_ONE_TEXT = "Same line" + System.lineSeparator() + "Different line";
    private static final String FILE_TWO_TEXT = "Same line" + System.lineSeparator() + "Same line";
    private static final String FILE_ONE_NAME = "fileOne";
    private static final String FILE_TWO_NAME = "fileTwo";
    private static final String FILE_FORMAT = ".txt";
    private static final String NO_DIFF_OUTPUT = FILE_ONE_NAME + " and " + FILE_TWO_NAME + " have no difference";
    private static DiffApplication diffApplication;
    private static File fileOne;
    private static File fileTwo;
    private static InputStream stdinOne, stdinTwo;
    private static OutputStream stdoutOne, stdoutTwo;
    private static boolean isShowSame, isNoBlank, isSimple;

    @BeforeAll
    static void setUp() throws Exception {
        diffApplication = new DiffApplication();
        fileOne = File.createTempFile(FILE_ONE_NAME, FILE_FORMAT);
        fileTwo = File.createTempFile(FILE_TWO_NAME, FILE_FORMAT);
        stdinOne = new FileInputStream(fileOne);
        stdinTwo = new FileInputStream(fileTwo);
        stdoutOne = new FileOutputStream(fileOne);
        stdoutTwo = new FileOutputStream(fileTwo);
        stdoutOne.write(FILE_ONE_TEXT.getBytes());
        stdoutTwo.write(FILE_TWO_TEXT.getBytes());
    }

    @AfterAll
    static void tearDown() throws Exception {
        fileOne.deleteOnExit();
        fileTwo.deleteOnExit();
    }

    @Test
    void runOnlyOneArgShouldThrowDiffException() throws Exception {
        String[] args = {FILE_ONE_NAME};
        assertThrows(DiffException.class, () -> {
            diffApplication.run(args, stdinOne, stdoutOne);
        });
    }

    @Test
    void runNullStdinShouldThrowDiffException() throws Exception {
        String[] args = {FILE_ONE_NAME, "-"};
        assertThrows(DiffException.class, () -> {
            diffApplication.run(args, null, stdoutOne);
        });
    }

    @Test
    void runNullArgsShouldThrowDiffException() throws Exception {
        InputStream inputStream = new FileInputStream("fileOne.txt");
        assertThrows(DiffException.class, () -> {
            diffApplication.run(null, inputStream, stdoutOne);
        });
    }

    @Test
    void runTwoSameFilesShouldOutputNoDiffMessage() throws Exception {
        InputStream inputStream = new FileInputStream("fileOne.txt");
        String[] args = {"-s", FILE_ONE_NAME, FILE_ONE_NAME};
        diffApplication.run(args, inputStream, stdoutOne);
        assertEquals(NO_DIFF_OUTPUT, stdoutOne.toString());
    }

    @Test
    void runCaseInsensitiveCorrectNoDiffMessage() throws Exception {
        String[] args = {"-s", FILE_ONE_NAME, FILE_ONE_NAME};
        diffApplication.run(args, null, stdoutOne);
        assertEquals(NO_DIFF_OUTPUT, stdoutOne.toString());
    }

    // Test diffTwoFiles Method

    @Test
    void runNullFilesNamesShouldThrowException() {
        assertThrows(DiffException.class, () -> {
            diffApplication.diffTwoFiles(null, null, true, true, true);
        });
    }

    @Test
    void runOneNullFileShouldThrowException() {
        String fileNameA = "fileA.txt";
        assertThrows(DiffException.class, () -> {
            diffApplication.diffTwoFiles(fileNameA, null, true, true, true);
        });
    }

    @Test
    void runOneEmptyFileShouldThrowException() {
        String fileNameA = "fileA.txt";
        assertThrows(DiffException.class, () -> {
            diffApplication.diffTwoFiles(fileNameA, "", true, true, true);
        });
    }

    // Test diffTwoDir Method

    @Test
    void runNullDirectoryShouldThrowException() {
        assertThrows(DiffException.class, () -> {
            diffApplication.diffTwoDir(null, null, true, true, true);
        });
    }

    @Test
    void runOneNullDirectoryShouldThrowException() {
        String folderA = "folderA";
        assertThrows(DiffException.class, () -> {
            diffApplication.diffTwoDir(folderA, null, true, true, true);
        });
    }

    @Test
    void runOneEmptyDirectoryShouldThrowException() {
        String folderA = "folderA";
        assertThrows(DiffException.class, () -> {
            diffApplication.diffTwoDir(folderA, "", true, true, true);
        });
    }

    // Test diffFileAndStdin
    @Test
    void runNullStdinInDiffFileAndStdinMethodShouldThrowException() throws FileNotFoundException {
        String fileNameA = "fileA.txt";
        InputStream inputStream = new FileInputStream("fileOne.txt");
        assertThrows(DiffException.class, () -> {
            diffApplication.diffFileAndStdin(null, inputStream, true, true, true);
        });
    }

    @Test
    void runNullFileInDiffFileAndStdinShouldThrowException() throws FileNotFoundException {
        InputStream inputStream = new FileInputStream("fileOne.txt");
        assertThrows(DiffException.class, () -> {
            diffApplication.diffFileAndStdin(null, inputStream, true, true, true);
        });
    }
}