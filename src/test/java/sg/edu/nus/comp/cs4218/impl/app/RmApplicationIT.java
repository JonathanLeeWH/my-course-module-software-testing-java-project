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
    private static final String FOLDER_NAME_2 = "hello2";
    private static final String ILLEGAL_FLAG = "z";
    public static final String ILLEGAL_FLAG_MSG = "illegal option -- ";
    private static final String R_FLAG = "-r";
    private static final String D_FLAG = "-d";

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
     * Tests run method when Exception is thrown from remove method executed in run method.
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
     * Tests run method execute successfully when all inputs are valid.
     */
    @Test
    void testRunWhenAllInputsAreValidShouldSuccessfulExecution(@TempDir Path tempDir) throws Exception {
        String[] argsList = {FILE_NAME_1};
        Path file = tempDir.resolve(FILE_NAME_1);

        Files.createFile(file);
        assertTrue(Files.exists(file)); // check if file exist.

        EnvironmentHelper.currentDirectory = tempDir.toString();

        rmApplication.run(argsList, mock(InputStream.class), mock(OutputStream.class));

        assertFalse(Files.exists(file)); // file is deleted.
    }

    /**
     * Tests run method when no flags and input file does not exist.
     * For example: rm 1.txt
     * Where 1.txt does not exist.
     * Expected: Throws RmException with ERR_FILE_NOT_FOUND
     */
    @Test
    void testRunMethodWhenNoFlagsAndInputFileDoesNotExistShouldThrowRmException(@TempDir Path tempDir) {
        Path file = tempDir.resolve(FILE_NAME_1);
        String[] argsList = {FILE_NAME_1};
        assertFalse(Files.exists(file)); // check that the file does not exist.

        EnvironmentHelper.currentDirectory = tempDir.toString();

        RmException exception = assertThrows(RmException.class, () -> {
            rmApplication.run(argsList, mock(InputStream.class), mock(OutputStream.class));
        });

        assertEquals(new RmException(ERR_FILE_NOT_FOUND).getMessage(), exception.getMessage());
    }


    //TODO

    /**
     * Tests run method without any flags for removing a file.
     * For example: rm 1.txt
     * Where 1.txt exists.
     * Expected: Removes the file.
     */
    @Test
    void testRunWhenNoFlagsFileExistsShouldDeleteFile(@TempDir Path tempDir) throws Exception {
        Path file = tempDir.resolve(FILE_NAME_1);
        String[] argsList = {FILE_NAME_1};

        Files.createFile(file);

        assertTrue(Files.exists(file));

        EnvironmentHelper.currentDirectory = tempDir.toString();

        // rm with no flags
        rmApplication.run(argsList, mock(InputStream.class), mock(OutputStream.class));

        // Check that the file is deleted.
        assertFalse(Files.exists(file));
    }

    /**
     * Tests run method without any flags for removing an empty folder.
     * For example: rm hello
     * Where hello directory exists and is an empty directory.
     * Expected: Throws RmException with ERR_IS_DIR
     */
    @Test
    void testRunWhenNoFlagsEmptyFolderExistsThrowsRmException(@TempDir Path tempDir) throws IOException {
        Path emptyFolder = tempDir.resolve(FOLDER_NAME_1);
        String[] argsList = {FOLDER_NAME_1};

        Files.createDirectory(emptyFolder);

        assertTrue(Files.isDirectory(emptyFolder));

        EnvironmentHelper.currentDirectory = tempDir.toString();

        // rm with no flags
        RmException exception = assertThrows(RmException.class, () -> {
            rmApplication.run(argsList, mock(InputStream.class), mock(OutputStream.class));
        });

        assertEquals(new RmException(ERR_IS_DIR).getMessage(), exception.getMessage());

        // Check that the empty folder is not deleted.
        assertTrue(Files.isDirectory(emptyFolder));
    }

    /**
     * Tests run method without any flags for removing multiple files (no folders).
     * For example: rm 1.txt 2.txt
     * Expected: Removes the files.
     */
    @Test
    void testRunWhenNoFlagsFilesExistsShouldDeleteFiles(@TempDir Path tempDir) throws Exception {
        Path file1 = tempDir.resolve(FILE_NAME_1);
        Path file2 = tempDir.resolve(FILE_NAME_2);
        String[] argsList = {FILE_NAME_1, FILE_NAME_2};

        Files.createFile(file1);
        Files.createFile(file2);

        assertTrue(Files.exists(file1));
        assertTrue(Files.exists(file2));

        EnvironmentHelper.currentDirectory = tempDir.toString();

        // rm with no flags
        rmApplication.run(argsList, mock(InputStream.class), mock(OutputStream.class));

        // Check to ensure file1 and file2 are deleted.
        assertFalse(Files.exists(file1));
        assertFalse(Files.exists(file2));
    }

    /**
     * Tests run method without any flags for removing multiple files (with empty folders).
     * For example: rm 1.txt hello
     * Where 1.txt exists and hello directory is an empty directory.
     * Expected: Removes 1.txt and throws RmException with ERR_IS_DIR as it attempts to remove hello directory.
     * The expected behaviour is similar to in unix.
     */
    @Test
    void testRunWhenNoFlagsFilesAndEmptyFolderExistsShouldThrowRmException(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve(FILE_NAME_1);
        Path emptyFolder  = tempDir.resolve(FOLDER_NAME_1);
        String[] argsList = {FILE_NAME_1, FOLDER_NAME_1};

        Files.createFile(file);
        Files.createDirectory(emptyFolder);

        assertTrue(Files.exists(file));
        assertTrue(Files.isDirectory(emptyFolder));

        EnvironmentHelper.currentDirectory = tempDir.toString();

        // rm with no flags
        RmException exception = assertThrows(RmException.class, () -> {
            rmApplication.run(argsList, mock(InputStream.class), mock(OutputStream.class));
        });

        assertEquals(new RmException(ERR_IS_DIR).getMessage(), exception.getMessage());

        // Check that the file is deleted but the folder still exist.
        assertFalse(Files.exists(file));
        assertTrue(Files.isDirectory(emptyFolder));
    }

    /**
     * Tests run method without any flags for removing multiple files (with non empty folders).
     * For example: rm 1.txt hello
     * Where 1.txt exists and hello directory is a non empty directory.
     * Expected: Removes 1.txt and throws RmException with ERR_IS_DIR as it attempts to remove hello directory.
     * The expected behaviour is similar to in unix.
     */
    @Test
    void testRunWhenNoFlagsFilesAndNonEmptyFolderExistsShouldThrowRmException(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve(FILE_NAME_1);
        Path fileInFolder = tempDir.resolve(FOLDER_NAME_1 + File.separator + FILE_NAME_2);
        Path nonEmptyFolder = fileInFolder.getParent();
        String[] argsList = {FILE_NAME_1, FOLDER_NAME_1 + File.separator + FILE_NAME_2};

        Files.createFile(file);
        Files.createDirectories(fileInFolder);

        assertTrue(Files.exists(file));
        assertTrue(Files.isDirectory(nonEmptyFolder));
        assertTrue(Files.exists(fileInFolder));

        EnvironmentHelper.currentDirectory = tempDir.toString();

        // rm with no flags
        RmException exception = assertThrows(RmException.class, () -> {
            rmApplication.run(argsList, mock(InputStream.class), mock(OutputStream.class));
        });

        assertEquals(new RmException(ERR_IS_DIR).getMessage(), exception.getMessage());

        // Check that the file is deleted but the non empty folder still exist.
        assertFalse(Files.exists(file));
        assertTrue(Files.isDirectory(nonEmptyFolder));
        assertTrue(Files.exists(fileInFolder));
    }

    /**
     * Tests run method without any flags for removing a non existing directory followed by an existing file followed by a non existing file.
     * For example: rm hello 1.txt 2.txt
     * Where 1.txt exists, hello is a directory that exist and 2.txt does not exist.
     * Expected: Removes 1.txt and throws latest RmException with ERR_FILE_NOT_FOUND as 2.txt does not exist and it is the latest exception.
     * The expected behaviour is similar to in unix except our shell only throw the latest exception as clarified with lecturer.
     */
    @Test
    void testRunWhenNoFlagsMultipleFileArgumentsIncludeNonExistingFileAndExistingFolderShouldDeleteExistingFileAndThrowLatestRmException(@TempDir Path tempDir) throws IOException {
        Path file1 = tempDir.resolve(FILE_NAME_1);
        Path file2 = tempDir.resolve(FILE_NAME_2);
        Path folder  = tempDir.resolve(FOLDER_NAME_1);
        String[] argsList = {FOLDER_NAME_1, FILE_NAME_1, FILE_NAME_2};

        Files.createFile(file1);
        Files.createDirectories(folder);
        assertTrue(Files.exists(file1));
        assertTrue(Files.isDirectory(folder));
        assertFalse(Files.exists(file2)); // file2 does not exist.

        EnvironmentHelper.currentDirectory = tempDir.toString();

        // rm with no flags
        RmException exception = assertThrows(RmException.class, () -> {
            rmApplication.run(argsList, mock(InputStream.class), mock(OutputStream.class));
        });

        assertEquals(new RmException(ERR_FILE_NOT_FOUND).getMessage(), exception.getMessage());

        // Check that file1 is deleted and folder still exist.
        assertFalse(Files.exists(file1));
        assertTrue(Files.isDirectory(folder));
    }

    /**
     * Tests run method with -d flag (isEmptyFolder is set to true) for removing an existing empty directory.
     * For example: rm -d hello
     * Where hello is an empty directory that exist.
     * Expected: Removes hello directory.
     */
    @Test
    void testRunWhenDFlagEmptyFolderExistsShouldDeleteEmptyFolder(@TempDir Path tempDir) throws Exception {
        Path emptyFolder = tempDir.resolve(FOLDER_NAME_1);
        String[] argsList = {D_FLAG, FOLDER_NAME_1};

        Files.createDirectory(emptyFolder);

        assertTrue(Files.isDirectory(emptyFolder));

        EnvironmentHelper.currentDirectory = tempDir.toString();

        // rm with -d flag
        rmApplication.run(argsList, mock(InputStream.class), mock(OutputStream.class));

        // Check that the empty folder is deleted.
        assertFalse(Files.isDirectory(emptyFolder));
    }

    /**
     * Tests run method with -d flag (isEmptyFolder is set to true) for removing multiple existing empty directories.
     * For example: rm -d hello hello2
     * Where hello and hello2 are empty directories that exist.
     * Expected: Removes hello and hello2 directory
     */
    @Test
    void testRunWhenDFlagMultipleEmptyFoldersExistsShouldDeleteInputEmptyFolders(@TempDir Path tempDir) throws Exception {
        Path emptyFolder = tempDir.resolve(FOLDER_NAME_1);
        Path emptyFolder2= tempDir.resolve(FOLDER_NAME_2);
        String[] argsList = {D_FLAG, FOLDER_NAME_1, FOLDER_NAME_2};

        Files.createDirectory(emptyFolder);
        Files.createDirectory(emptyFolder2);

        assertTrue(Files.isDirectory(emptyFolder));
        assertTrue(Files.isDirectory(emptyFolder2));

        EnvironmentHelper.currentDirectory = tempDir.toString();

        // rm with -d flag
        rmApplication.run(argsList, mock(InputStream.class), mock(OutputStream.class));

        // Check that the input empty folders are deleted.
        assertFalse(Files.isDirectory(emptyFolder));
        assertFalse(Files.isDirectory(emptyFolder2));
    }

    /**
     * Tests run method with -d flag (isEmptyFolder is set to true) for removing an existing empty directory followed by an existing file.
     * For example: rm -d hello 1.txt
     * Where hello is an empty directory that exist and 1.txt is a file that exist.
     * Expected: Removes hello directory and 1.txt.
     */
    @Test
    void testRunWhenDFlagEmptyFolderAndFileExistShouldDeleteEmptyFolder(@TempDir Path tempDir) throws Exception {
        Path emptyFolder = tempDir.resolve(FOLDER_NAME_1);
        Path file = tempDir.resolve(FILE_NAME_1);
        String[] argsList = {D_FLAG, FOLDER_NAME_1, FILE_NAME_1};

        Files.createDirectory(emptyFolder);
        Files.createFile(file);

        assertTrue(Files.exists(file));
        assertTrue(Files.isDirectory(emptyFolder));

        EnvironmentHelper.currentDirectory = tempDir.toString();

        // rm with -d flag
        rmApplication.run(argsList, mock(InputStream.class), mock(OutputStream.class));

        // Check that the empty folder and file is deleted.
        assertFalse(Files.isDirectory(emptyFolder));
        assertFalse(Files.exists(file));
    }



    /**
     * Tests run method with -d flag (isEmptyFolder is set to true) for removing an existing non empty directory.
     * For example: rm -d hello
     * Where hello is a non empty directory that exist.
     * Expected: Throws RmException with ERR_NON_EMPTY_DIR as it attempts to remove hello directory
     */
    @Test
    void testRunWhenDFlagNonEmptyFolderExistsThrowsRmException(@TempDir Path tempDir) throws Exception {
        Path fileInFolder = tempDir.resolve(FOLDER_NAME_1 + File.separator + FILE_NAME_2);
        Path nonEmptyFolder = fileInFolder.getParent();;
        String[] argsList = {D_FLAG, FOLDER_NAME_1};

        Files.createDirectories(fileInFolder);

        assertTrue(Files.isDirectory(nonEmptyFolder));
        assertTrue(Files.exists(fileInFolder));

        EnvironmentHelper.currentDirectory = tempDir.toString();

        // rm with -d flag
        RmException exception = assertThrows(RmException.class, () -> {
            rmApplication.run(argsList, mock(InputStream.class), mock(OutputStream.class));
        });

        assertEquals(new RmException(ERR_NON_EMPTY_DIR).getMessage(), exception.getMessage());

        // Check that the non empty folder still exist.
        assertTrue(Files.isDirectory(nonEmptyFolder));
    }

    /**
     * Tests run method with -d flag (isEmptyFolder is set to true) for removing an existing non empty directory followed by an existing file.
     * For example: rm -d hello 1.txt
     * Where hello is a non empty directory that exist and 1.txt exists.
     * Expected: Throws latest RmException with ERR_NON_EMPTY_DIR as it attempts to remove hello directory. At the same time, it removes 1.txt similar to in unix.
     */
    @Test
    void testRunWhenDFlagNonEmptyFolderAndFileExistsThrowsRmExceptionAndRemoveFile(@TempDir Path tempDir) throws Exception {
        Path fileInFolder = tempDir.resolve(FOLDER_NAME_1 + File.separator + FILE_NAME_2);
        Path nonEmptyFolder = fileInFolder.getParent();;
        Path file = tempDir.resolve(FILE_NAME_1);
        String[] argsList = {D_FLAG, FOLDER_NAME_1, FILE_NAME_1};

        Files.createFile(file);
        Files.createDirectories(fileInFolder);

        assertTrue(Files.exists(file));
        assertTrue(Files.isDirectory(nonEmptyFolder));
        assertTrue(Files.exists(fileInFolder));

        EnvironmentHelper.currentDirectory = tempDir.toString();

        // rm with -d flag
        RmException exception = assertThrows(RmException.class, () -> {
            rmApplication.run(argsList, mock(InputStream.class), mock(OutputStream.class));
        });

        assertEquals(new RmException(ERR_NON_EMPTY_DIR).getMessage(), exception.getMessage());

        // Check that the non empty folder still exist but 1.txt is deleted.
        assertTrue(Files.isDirectory(nonEmptyFolder));
        assertFalse(Files.exists(file));
    }

    /**
     * Tests run method with -d flag (isEmptyFolder is set to true) for removing an existing file followed by an existing non empty directory.
     * For example: rm -d 1.txt hello
     * Where hello is a non empty directory that exist and 1.txt exists.
     * Expected: Throws latest RmException with ERR_NON_EMPTY_DIR as it attempts to remove hello directory. At the same time, it removes 1.txt similar to in unix.
     */
    @Test
    void testRunWhenDFlagFileAndNonEmptyFolderExistsThrowsRmExceptionAndRemoveFile(@TempDir Path tempDir) throws Exception {
        Path fileInFolder = tempDir.resolve(FOLDER_NAME_1 + File.separator + FILE_NAME_2);
        Path nonEmptyFolder = fileInFolder.getParent();;
        Path file = tempDir.resolve(FILE_NAME_1);
        String[] argsList = {D_FLAG, FILE_NAME_1, FOLDER_NAME_1};

        Files.createFile(file);
        Files.createDirectories(fileInFolder);

        assertTrue(Files.exists(file));
        assertTrue(Files.isDirectory(nonEmptyFolder));
        assertTrue(Files.exists(fileInFolder));

        EnvironmentHelper.currentDirectory = tempDir.toString();

        // rm with -d flag
        RmException exception = assertThrows(RmException.class, () -> {
            rmApplication.run(argsList, mock(InputStream.class), mock(OutputStream.class));
        });

        assertEquals(new RmException(ERR_NON_EMPTY_DIR).getMessage(), exception.getMessage());

        // Check that the non empty folder still exist but 1.txt is deleted.
        assertTrue(Files.isDirectory(nonEmptyFolder));
        assertFalse(Files.exists(file));
    }

    /**
     * Tests run method with -r or -rd  or -r -d flag
     * We will only test as long as -r exists by MC/DC to reduce number of test cases as both cases exhibit same behaviour.
     * For example: rm -r hello or rm -r -d hello or rm -rd hello
     * Where hello is a non empty directory that exist.
     * Expected: Removes hello directory and its contents
     */
    @Test
    void testRunWhenRFlagNonEmptyFolderExistsShouldDeleteNonEmptyFolder(@TempDir Path tempDir) throws Exception {
        Path fileInFolder = tempDir.resolve(FOLDER_NAME_1 + File.separator + FILE_NAME_2);
        Path nonEmptyFolder = fileInFolder.getParent();;
        String[] argsList = {R_FLAG, FOLDER_NAME_1};

        Files.createDirectories(fileInFolder);

        assertTrue(Files.isDirectory(nonEmptyFolder));
        assertTrue(Files.exists(fileInFolder));

        EnvironmentHelper.currentDirectory = tempDir.toString();

        // rm with -r flag
        rmApplication.run(argsList, mock(InputStream.class), mock(OutputStream.class));

        // Check that the non empty folder is deleted.
        assertFalse(Files.isDirectory(nonEmptyFolder));
    }
}