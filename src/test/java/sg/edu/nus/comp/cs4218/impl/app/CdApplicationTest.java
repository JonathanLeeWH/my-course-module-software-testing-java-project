package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import sg.edu.nus.comp.cs4218.EnvironmentHelper;
import sg.edu.nus.comp.cs4218.exception.CdException;
import sg.edu.nus.comp.cs4218.impl.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;

class CdApplicationTest {

    private static final String EMPTY_STRING = "";
    private static final String FILE_NAME = "1.txt";
    private static final String FOLDER_NAME = "hello";

    CdApplication cdApplication;

    @BeforeEach
    void setUp() {
        EnvironmentHelper.currentDirectory = System.getProperty("user.dir");
        cdApplication = new CdApplication();
    }

    @AfterEach
    void tearDown() {
        // Reset EnvironmentHelper.currentDirectory to default.
        EnvironmentHelper.currentDirectory = System.getProperty("user.dir");
    }

    /**
     * Tests run method when input args is null.
     * Expected: Throws CdException with ERR_NULL_ARGS
     */
    @Test
    void testRunWhenInputArgsIsNullShouldThrowCdException() {
        CdException exception = assertThrows(CdException.class, () -> {
            cdApplication.run(null, mock(InputStream.class), mock(OutputStream.class));
        });
        assertEquals(new CdException(ERR_NULL_ARGS).getMessage(), exception.getMessage());
    }

    /**
     * Tests run method when input args is empty.
     * Expected: Throws CdException with ERR_MISSING_ARG
     */
    @Test
    void testRunWhenInputArgsIsEmptyShouldThrowCdException() {
        String[] argsList = {};
        CdException exception = assertThrows(CdException.class, () -> {
            cdApplication.run(argsList, mock(InputStream.class), mock(OutputStream.class));
        });
        assertEquals(new CdException(ERR_MISSING_ARG).getMessage(), exception.getMessage());
    }

    /**
     * Tests run method when input args has more than one argument.
     * Expected: Throws CdException with ERR_TOO_MANY_ARGS
     */
    @Test
    void testRunWhenInputArgsMoreThanOneShouldThrowCdException() {
        String[] argsList = {FOLDER_NAME, FOLDER_NAME};
        CdException exception = assertThrows(CdException.class, () -> {
            cdApplication.run(argsList, mock(InputStream.class), mock(OutputStream.class));
        });
        assertEquals(new CdException(ERR_TOO_MANY_ARGS).getMessage(), exception.getMessage());
    }

    /**
     * Tests run method when input arg path is an empty Path.
     * Expected: Throws CdException with ERR_NO_ARGS
     */
    @Test
    void testRunWhenInputArgPathIsEmptyPathOneShouldThrowCdException() {
        String[] argsList = {EMPTY_STRING};
        CdException exception = assertThrows(CdException.class, () -> {
            cdApplication.run(argsList, mock(InputStream.class), mock(OutputStream.class));
        });
        assertEquals(new CdException(ERR_NO_ARGS).getMessage(), exception.getMessage());
    }

    /**
     * Tests run method when input arg path is a file and not a directory.
     * Expected: Throws CdException with ERR_IS_NOT_DIR.
     */
    @Test
    void testRunWhenInputArgPathIsAFileShouldThrowCdException(@TempDir Path tempDir) throws IOException {
        String[] argsList = {FILE_NAME};
        Path file = tempDir.resolve(FILE_NAME);

        EnvironmentHelper.currentDirectory = tempDir.toString();
        assertTrue(Files.isDirectory(tempDir));
        assertTrue(tempDir.isAbsolute());

        Files.createFile(file);
        assertTrue(Files.exists(file));

        Exception exception = assertThrows(CdException.class, () -> {
            cdApplication.run(argsList, mock(InputStream.class), mock(OutputStream.class));
        });
        assertEquals(new CdException(ERR_IS_NOT_DIR).getMessage(), exception.getMessage());
    }

    /**
     * Tests run method when input arg path does not exist.
     * Expected: Throws CdException with ERR_FILE_NOT_FOUND.
     */
    @Test
    void testRunWhenInputArgPathDoesNotExistShouldThrowCdException(@TempDir Path tempDir) throws IOException {
        String[] argsList = {FOLDER_NAME};
        Path path = tempDir.resolve(FOLDER_NAME);

        EnvironmentHelper.currentDirectory = tempDir.toString();
        assertTrue(Files.isDirectory(tempDir));
        assertTrue(tempDir.isAbsolute());

        // Check that the path does not exist.
        assertFalse(Files.exists(path));

        Exception exception = assertThrows(CdException.class, () -> {
            cdApplication.run(argsList, mock(InputStream.class), mock(OutputStream.class));
        });
        assertEquals(new CdException(ERR_FILE_NOT_FOUND).getMessage(), exception.getMessage());
    }

    /**
     * Tests run method when input arg path exists and is a directory and not an absolute path (relative path).
     * For example: cd ..
     * Expected: Sets EnvironmentHelper.currentDirectory to the full absolute path (converted from the non absolute/relative path.
     * In this case, the current directory is changed to the parent folder path of the present working directory.
     */
    @Test
    void testRunWhenInputArgPathExistsIsADirectoryAndNonAbsolutePathShouldChangeToTheAbsolutePathVersionOfInput() throws CdException {
        String[] argsList = {".."};
        String parentAbsPath = Paths.get(EnvironmentHelper.currentDirectory).getParent().toString();

        assertFalse(new File(argsList[0]).toPath().isAbsolute());

        cdApplication.run(argsList, mock(InputStream.class), mock(OutputStream.class));
        String newPath = EnvironmentHelper.currentDirectory;

        assertEquals(parentAbsPath, newPath);
    }

    /**
     * Tests run method when input arg path exists and is a directory and an absolute path
     * Assuming EnvironmentHelper.currentDirectory is initially at C:\Users\<COMPUTER_USER_NAME>\Documents\<PROJECT_DIRECTORY_NAME>
     * For example: cd C:\Users\<COMPUTER_USER_NAME>
     * Expected: Sets EnvironmentHelper.currentDirectory to the full absolute path (C:\Users\<COMPUTER_USER_NAME>)
     */
    @Test
    void testRunWhenInputArgPathExistsIsADirectoryAndAbsolutePathShouldChangeToTheAbsolutePath(@TempDir Path tempDir) throws IOException, CdException {
        Path folder = tempDir.resolve(FOLDER_NAME);
        String[] argsList = {folder.normalize().toAbsolutePath().toString()};

        EnvironmentHelper.currentDirectory = tempDir.toString();
        assertTrue(Files.isDirectory(tempDir));
        assertTrue(tempDir.isAbsolute());

        Files.createDirectories(folder);
        Files.isDirectory(folder);
        assertTrue(folder.isAbsolute());

        cdApplication.run(argsList, mock(InputStream.class), mock(OutputStream.class));
        String newPath = EnvironmentHelper.currentDirectory;
        assertEquals(folder.normalize().toAbsolutePath().toString(), newPath);
    }

    /**
     * Tests changeToDirectory method when input path is empty.
     * Expected: Throws CdException with ERR_NO_ARGS
     */
    @Test
    void testChangeToDirectoryWhenInputPathIsEmptyShouldThrowCdException() {
        Exception exception = assertThrows(CdException.class, () -> {
            cdApplication.changeToDirectory(EMPTY_STRING);
        });
        assertEquals(new CdException(ERR_NO_ARGS).getMessage(), exception.getMessage());
    }

    /**
     * Tests changeToDirectory method when input path is a file and not a directory.
     * Expected: Throws CdException with ERR_IS_NOT_DIR.
     */
    @Test
    void testChangeToDirectoryWhenInputPathIsAFileShouldThrowCdException(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve(FILE_NAME);

        Files.createFile(file);
        assertTrue(Files.exists(file));

        Exception exception = assertThrows(CdException.class, () -> {
            cdApplication.changeToDirectory(file.toString());
        });
        assertEquals(new CdException(ERR_IS_NOT_DIR).getMessage(), exception.getMessage());
    }

    /**
     * Tests changeToDirectory method when input path does not exist.
     * Expected: Throws CdException with ERR_FILE_NOT_FOUND.
     */
    @Test
    void testChangeToDirectoryWhenInputPathDoesNotExistShouldThrowCdException(@TempDir Path tempDir) throws IOException {
        Path path = tempDir.resolve(FOLDER_NAME);

        // Check that the path does not exist.
        assertFalse(Files.exists(path));

        Exception exception = assertThrows(CdException.class, () -> {
            cdApplication.changeToDirectory(path.toString());
        });
        assertEquals(new CdException(ERR_FILE_NOT_FOUND).getMessage(), exception.getMessage());
    }

    /**
     * Tests changeToDirectory method when input path exists and is a directory and not an absolute path (relative path).
     * For example: cd ..
     * Expected: Sets EnvironmentHelper.currentDirectory to the full absolute path (converted from the non absolute/relative path.
     * In this case, the current directory is changed to the parent folder path of the present working directory.
     */
    @Test
    void testChangeToDirectoryWhenInputPathExistsIsADirectoryAndNonAbsolutePathShouldChangeToTheAbsolutePathVersionOfInput() throws CdException {
        String path = "..";
        String parentAbsPath = Paths.get(EnvironmentHelper.currentDirectory).getParent().toString();

        assertFalse(new File(path).toPath().isAbsolute());

        cdApplication.changeToDirectory(path);
        String newPath = EnvironmentHelper.currentDirectory;

        assertEquals(parentAbsPath, newPath);
    }

    /**
     * Tests changeToDirectory method when input path exists and is a directory and an absolute path
     * Assuming EnvironmentHelper.currentDirectory is initially at C:\Users\<COMPUTER_USER_NAME>\Documents\<PROJECT_DIRECTORY_NAME>
     * For example: cd C:\Users\<COMPUTER_USER_NAME>
     * Expected: sets EnvironmentHelper.currentDirectory to the full absolute path (C:\Users\<COMPUTER_USER_NAME>)
     */
    @Test
    void testChangeToDirectoryWhenInputPathExistsIsADirectoryAndAbsolutePathShouldChangeToTheAbsolutePath(@TempDir Path tempDir) throws IOException, CdException {
        Path folder = tempDir.resolve(FOLDER_NAME);

        Files.createDirectories(folder);
        Files.isDirectory(folder);
        assertTrue(folder.isAbsolute());

        cdApplication.changeToDirectory(folder.normalize().toString());
        String newPath = EnvironmentHelper.currentDirectory;
        assertEquals(folder.normalize().toAbsolutePath().toString(), newPath);
    }
}