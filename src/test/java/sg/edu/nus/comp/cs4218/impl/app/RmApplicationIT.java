package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import sg.edu.nus.comp.cs4218.EnvironmentHelper;
import sg.edu.nus.comp.cs4218.exception.InvalidArgsException;
import sg.edu.nus.comp.cs4218.exception.RmException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FLAG_PREFIX;

class RmApplicationIT {

    private static final String FILE_NAME_1 = "1.txt";
    private static final String FILE_NAME_2 = "2.txt";
    private static final String FOLDER_NAME_1 = "hello";
    private static final String ILLEGAL_FLAG = "z";
    public static final String ILLEGAL_FLAG_MSG = "illegal option -- ";

    private RmApplication rmApplication;

    @BeforeEach
    void setUp() {
        // Reset EnvironmentHelper current directory to default.
        EnvironmentHelper.currentDirectory = System.getProperty("user.dir");
        rmApplication = new RmApplication();
    }

    @AfterEach
    void tearDown() {
        // Reset EnvironmentHelper current directory to default.
        EnvironmentHelper.currentDirectory = System.getProperty("user.dir");
    }

    /**
     * Tests run method when input array of arguments is null.
     * Expected: Throws RmException with ERR_NULL_ARGS
     */
    @Test
    void testRunWhenInputArgsIsNullThrowsRmException() {
        RmException exception = assertThrows(RmException.class, () -> {
            rmApplication.run(null, mock(InputStream.class), mock(OutputStream.class));
        });

        assertEquals(new RmException(ERR_NULL_ARGS).getMessage(), exception.getMessage());
    }

    /**
     * Tests run method when input array of arguments is empty.
     * Expected: Throws RmException with ERR_MISSING_ARG
     */
    @Test
    void testRunWhenInputArgsIsEmptyThrowsRmException() {
        RmException exception = assertThrows(RmException.class, () -> {
            rmApplication.run(new String[] {}, mock(InputStream.class), mock(OutputStream.class));
        });

        assertEquals(new RmException(ERR_MISSING_ARG).getMessage(), exception.getMessage());
    }

    /**
     * Tests run method when input array of arguments contains illegal flag
     * Expected: Throws RmException with InvalidArgsException message.
     */
    @Test
    void testRunWhenInputArgsContainsIllegalFlagShouldThrowRmException() {
        String[] argsList = {CHAR_FLAG_PREFIX + ILLEGAL_FLAG};
        RmException exception = assertThrows(RmException.class, () -> {
            rmApplication.run(argsList, mock(InputStream.class), mock(OutputStream.class));
        });
        assertEquals(new RmException(new InvalidArgsException(ILLEGAL_FLAG_MSG + ILLEGAL_FLAG).getMessage()).getMessage(), exception.getMessage());
    }

    /**
     * Tests run method when Exception is catch from remove method executed in run method.
     * For example: rm 1.txt 2.txt
     * Where 1.txt and 2.txt does not exist
     * Expected: Throws RmException.
     */
    @Test
    void testRunWhenRemoveThrowsExceptionShouldThrowRmException() {
        String[] argsList = {FILE_NAME_1, FILE_NAME_2};
        assertThrows(Exception.class, () -> {
            rmApplication.run(argsList, mock(InputStream.class), mock(OutputStream.class));
        });
    }

    /**
     * Tests run method execute successfully.
     */
    @Test
    void testRunWhenSuccessfulExecution(@TempDir Path tempDir) throws Exception {
        String[] argsList = {FILE_NAME_1};
        Path file = tempDir.resolve(FILE_NAME_1);

        Files.createFile(file);
        assertTrue(Files.exists(file)); // check if file exist.

        EnvironmentHelper.currentDirectory = tempDir.toString();

        rmApplication.run(argsList, mock(InputStream.class), mock(OutputStream.class));

        assertFalse(Files.exists(file)); // file is deleted.
    }
}