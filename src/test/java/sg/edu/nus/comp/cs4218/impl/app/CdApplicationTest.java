package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import sg.edu.nus.comp.cs4218.exception.CdException;
import sg.edu.nus.comp.cs4218.impl.util.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

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
        cdApplication = new CdApplication();
    }

    /**
     * Tests for changeToDirectory method when input path is empty.
     * Expected: Throws CdException with ERR_NO_ARGS
     */
    @Test
    void changeToDirectoryWhenInputPathIsEmptyShouldThrowsCdException() {
        Exception exception = assertThrows(CdException.class, () -> {
            cdApplication.changeToDirectory(EMPTY_STRING);
        });
        assertEquals(new CdException(ERR_NO_ARGS).getMessage(), exception.getMessage());
    }

    /**
     * Tests for changeToDirectory method when input path is a file and not a directory.
     * Expected: Throws CdException with ERR_IS_NOT_DIR.
     */
    @Test
    void changeToDirectoryWhenInputPathIsAFileShouldThrowsCdException(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve(FILE_NAME);

        Files.createFile(file);
        assertTrue(Files.exists(file));

        Exception exception = assertThrows(CdException.class, () -> {
            cdApplication.changeToDirectory(file.toString());
        });
        assertEquals(new CdException(ERR_IS_NOT_DIR).getMessage(), exception.getMessage());
    }

    /**
     * Tests for changeToDirectory method when input path does not exist.
     * Expected: Throws CdException with ERR_FILE_NOT_FOUND.
     */
    @Test
    void changeToDirectoryWhenInputPathDoesNotExistShouldThrowsCdException(@TempDir Path tempDir) throws IOException {
        Path path = tempDir.resolve(FOLDER_NAME);

        // Check that the path does not exist.
        assertFalse(Files.exists(path));

        Exception exception = assertThrows(CdException.class, () -> {
            cdApplication.changeToDirectory(path.toString());
        });
        assertEquals(new CdException(ERR_FILE_NOT_FOUND).getMessage(), exception.getMessage());
    }

//    /**
//     * Tests for changeToDirectory method when input path file or directory does not exist.
//     * Expected: Throws CdExpection with ERR_FILE_NOT_FOUND
//     */
//    @Test
//    void changeToDirectoryWhenInputPathIsNot
}