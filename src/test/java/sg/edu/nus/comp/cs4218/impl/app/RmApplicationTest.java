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
import static org.mockito.Mockito.*;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FLAG_PREFIX;

class RmApplicationTest {

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

    /**
     * Tests remove method when input file/folder does not exist.
     * For example: rm 1.txt
     * Where 1.txt does not exist.
     * Expected: Throws RmException with ERR_FILE_NOT_FOUND
     */
    @Test
    void testRemoveWhenInputFileAbsentThrowsRmException(@TempDir Path tempDir) {
        Path file = tempDir.resolve(FILE_NAME_1);
        String[] fileNames = {FILE_NAME_1};

        assertFalse(Files.exists(file));

        EnvironmentHelper.currentDirectory = tempDir.toString();

        RmException exception = assertThrows(RmException.class, () -> {
            rmApplication.remove(false, false, fileNames);
        });

        assertEquals(new RmException(ERR_FILE_NOT_FOUND).getMessage(), exception.getMessage());
    }

    /**
     * Tests remove method without any flags for removing a file.
     * For example: rm 1.txt
     * Expected: Removes the file.
     */
    @Test
    void testRemoveWhenNoFlagsFileExistsShouldDeleteFile(@TempDir Path tempDir) throws Exception {
        Path file = tempDir.resolve(FILE_NAME_1);
        String[] fileNames = {FILE_NAME_1};

        Files.createFile(file);

        assertTrue(Files.exists(file));

        EnvironmentHelper.currentDirectory = tempDir.toString();

        // rm with no flags
        rmApplication.remove(false, false, fileNames);

        // Check that the file is deleted.
        assertFalse(Files.exists(file));
    }

    /**
     * Tests remove method without any flags for removing an empty folder.
     * For example: rm hello
     * Where hello directory exists and is an empty directory.
     * Expected: Throws RmException with ERR_IS_DIR
     */
    @Test
    void testRemoveWhenNoFlagsEmptyFolderExistsThrowsRmException(@TempDir Path tempDir) throws IOException {
        Path emptyFolder = tempDir.resolve(FOLDER_NAME_1);
        String[] fileNames = {FOLDER_NAME_1};

        Files.createDirectory(emptyFolder);

        assertTrue(Files.isDirectory(emptyFolder));

        EnvironmentHelper.currentDirectory = tempDir.toString();

        // rm with no flags
        RmException exception = assertThrows(RmException.class, () -> {
            rmApplication.remove(false, false, fileNames);
        });

       assertEquals(new RmException(ERR_IS_DIR).getMessage(), exception.getMessage());

       // Check that the empty folder is not deleted.
       assertTrue(Files.isDirectory(emptyFolder));
    }

    /**
     * Tests remove method without any flags for removing multiple files (no folders).
     * For example: rm 1.txt 2.txt
     * Expected: Removes the files.
     */
    @Test
    void testRemoveWhenNoFlagsFilesExistsShouldDeleteFiles(@TempDir Path tempDir) throws Exception {
        Path file1 = tempDir.resolve(FILE_NAME_1);
        Path file2 = tempDir.resolve(FILE_NAME_2);
        String[] fileNames = {FILE_NAME_1, FILE_NAME_2};

        Files.createFile(file1);
        Files.createFile(file2);

        assertTrue(Files.exists(file1));
        assertTrue(Files.exists(file2));

        EnvironmentHelper.currentDirectory = tempDir.toString();

        // rm with no flags
        rmApplication.remove(false, false, fileNames);

        // Check to ensure file1 and file2 are deleted.
        assertFalse(Files.exists(file1));
        assertFalse(Files.exists(file2));
    }

    /**
     * Tests remove method without any flags for removing multiple files (with empty folders).
     * For example: rm 1.txt hello
     * Where 1.txt exists and hello directory is an empty directory.
     * Expected: Removes 1.txt and throws RmException with ERR_IS_DIR as it attempts to remove hello directory.
     * The expected behaviour is similar to in unix.
     */
    @Test
    void testRemoveWhenNoFlagsFilesAndEmptyFolderExistsShouldThrowRmException(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve(FILE_NAME_1);
        Path emptyFolder  = tempDir.resolve(FOLDER_NAME_1);
        String[] fileNames = {FILE_NAME_1, FOLDER_NAME_1};

        Files.createFile(file);
        Files.createDirectory(emptyFolder);

        assertTrue(Files.exists(file));
        assertTrue(Files.isDirectory(emptyFolder));

        EnvironmentHelper.currentDirectory = tempDir.toString();

        // rm with no flags
        RmException exception = assertThrows(RmException.class, () -> {
            rmApplication.remove(false, false, fileNames);
        });

        assertEquals(new RmException(ERR_IS_DIR).getMessage(), exception.getMessage());

        // Check that the file is deleted but the folder still exist.
        assertFalse(Files.exists(file));
        assertTrue(Files.isDirectory(emptyFolder));
    }

    /**
     * Tests remove method without any flags for removing multiple files (with non empty folders).
     * For example: rm 1.txt hello
     * Where 1.txt exists and hello directory is a non empty directory.
     * Expected: Removes 1.txt and throws RmException with ERR_IS_DIR as it attempts to remove hello directory.
     * The expected behaviour is similar to in unix.
     */
    @Test
    void testRemoveWhenNoFlagsFilesAndNonEmptyFolderExistsShouldThrowRmException(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve(FILE_NAME_1);
        Path fileInFolder = tempDir.resolve(FOLDER_NAME_1 + File.separator + FILE_NAME_2);
        Path nonEmptyFolder = fileInFolder.getParent();
        String[] fileNames = {FILE_NAME_1, FOLDER_NAME_1 + File.separator + FILE_NAME_2};

        Files.createFile(file);
        Files.createDirectories(fileInFolder);
        
        assertTrue(Files.exists(file));
        assertTrue(Files.isDirectory(nonEmptyFolder));
        assertTrue(Files.exists(fileInFolder));

        EnvironmentHelper.currentDirectory = tempDir.toString();

        // rm with no flags
        RmException exception = assertThrows(RmException.class, () -> {
            rmApplication.remove(false, false, fileNames);
        });

        assertEquals(new RmException(ERR_IS_DIR).getMessage(), exception.getMessage());

        // Check that the file is deleted but the non empty folder still exist.
        assertFalse(Files.exists(file));
        assertTrue(Files.isDirectory(nonEmptyFolder));
        assertTrue(Files.exists(fileInFolder));
    }

    /**
     * Tests remove method without any flags for removing a non existing directory followed by an existing file followed by a non existing file.
     * For example: rm hello 1.txt 2.txt
     * Where 1.txt exists, hello is a directory that exist and 2.txt does not exist.
     * Expected: Removes 1.txt and throws latest RmException with ERR_FILE_NOT_FOUND as 2.txt does not exist and it is the latest exception.
     * The expected behaviour is similar to in unix except our shell only throw the latest exception as clarified with lecturer.
     */
    @Test
    void testRemoveWhenNoFlagsMultipleFileArgumentsIncludeNonExistingFileAndExistingFolderShouldDeleteExistingFileAndThrowLatestRmException(@TempDir Path tempDir) throws IOException {
        Path file1 = tempDir.resolve(FILE_NAME_1);
        Path file2 = tempDir.resolve(FILE_NAME_2);
        Path folder  = tempDir.resolve(FOLDER_NAME_1);
        String[] fileNames = {FOLDER_NAME_1, FILE_NAME_1, FILE_NAME_2};

        Files.createFile(file1);
        Files.createDirectories(folder);
        assertTrue(Files.exists(file1));
        assertTrue(Files.isDirectory(folder));
        assertFalse(Files.exists(file2)); // file2 does not exist.

        EnvironmentHelper.currentDirectory = tempDir.toString();

        // rm with no flags
        RmException exception = assertThrows(RmException.class, () -> {
            rmApplication.remove(false, false, fileNames);
        });

        assertEquals(new RmException(ERR_FILE_NOT_FOUND).getMessage(), exception.getMessage());

        // Check that file1 is deleted and folder still exist.
        assertFalse(Files.exists(file1));
        assertTrue(Files.isDirectory(folder));
    }

    /**
     * Tests remove method with -d flag (isEmptyFolder is set to true) for removing a existing empty directory.
     * For example: rm -d hello
     * Where hello is an empty directory that exist.
     * Expected: Removes hello directory.
     */
    @Test
    void testRemoveWhenDFlagEmptyFolderExistsShouldDeleteEmptyFolder(@TempDir Path tempDir) throws Exception {
        Path emptyFolder = tempDir.resolve(FOLDER_NAME_1);
        String[] fileNames = {FOLDER_NAME_1};

        Files.createDirectory(emptyFolder);

        assertTrue(Files.isDirectory(emptyFolder));

        EnvironmentHelper.currentDirectory = tempDir.toString();

        // rm with -d flag
        rmApplication.remove(true, false, fileNames);

        // Check that the empty folder is deleted.
        assertFalse(Files.isDirectory(emptyFolder));
    }

    /**
     * Tests remove method with -d flag (isEmptyFolder is set to true) for removing an existing non empty directory.
     * For example: rm -d hello
     * Where hello is a non empty directory that exist.
     * Expected: Throws RmException with ERR_NON_EMPTY_DIR as it attempts to remove hello directory
     */
    @Test
    void testRemoveWhenDFlagNonEmptyFolderExistsThrowsRmException(@TempDir Path tempDir) throws Exception {
        Path fileInFolder = tempDir.resolve(FOLDER_NAME_1 + File.separator + FILE_NAME_2);
        Path nonEmptyFolder = fileInFolder.getParent();;
        String[] fileNames = {FOLDER_NAME_1};

        Files.createDirectories(fileInFolder);

        assertTrue(Files.isDirectory(nonEmptyFolder));
        assertTrue(Files.exists(fileInFolder));

        EnvironmentHelper.currentDirectory = tempDir.toString();

        // rm with -d flag
        RmException exception = assertThrows(RmException.class, () -> {
            rmApplication.remove(true, false, fileNames);
        });

        assertEquals(new RmException(ERR_NON_EMPTY_DIR).getMessage(), exception.getMessage());

        // Check that the non empty folder still exist.
        assertTrue(Files.isDirectory(nonEmptyFolder));
    }

    /**
     * Tests remove method with -r or -rd  or -r -d flag
     * We will only test as long as -r exists by MC/DC to reduce number of test cases as both cases exhibit same behaviour.
     * For example: rm -r hello or rm -r -d hello or rm -rd hello
     * Where hello is a non empty directory that exist.
     * Expected: Removes hello directory and its contents
     */
    @Test
    void testRemoveWhenRFlagNonEmptyFolderExistsShouldDeleteNonEmptyFolder(@TempDir Path tempDir) throws Exception {
        Path fileInFolder = tempDir.resolve(FOLDER_NAME_1 + File.separator + FILE_NAME_2);
        Path nonEmptyFolder = fileInFolder.getParent();;
        String[] fileNames = {FOLDER_NAME_1};

        Files.createDirectories(fileInFolder);

        assertTrue(Files.isDirectory(nonEmptyFolder));
        assertTrue(Files.exists(fileInFolder));

        EnvironmentHelper.currentDirectory = tempDir.toString();

        // rm with -r flag
        rmApplication.remove(false, true, fileNames);

        // Check that the non empty folder is deleted.
        assertFalse(Files.isDirectory(nonEmptyFolder));
    }

    /**
     * Tests removeFileOnly method when input file is absent.
     * Expected: Throws RmException with ERR_IO_EXCEPTION
     */
    @Test
    void testRemoveFileOnlyWhenFileAbsentShouldThrowRmException(@TempDir Path tempDir) {
        Path file = tempDir.resolve(FILE_NAME_1);

        assertFalse(Files.exists(file)); // check to ensure file does not exist initially.

        RmException exception = assertThrows(RmException.class, () -> {
            rmApplication.removeFileOnly(file.toFile());
        });

        assertEquals(new RmException(ERR_IO_EXCEPTION).getMessage(), exception.getMessage());
    }

    /**
     * Tests removeFileOnly method when input file exists.
     * Expected: Input file is deleted.
     */
    @Test
    void testRemoveFileOnlyWhenFileExistsShouldDeleteFile(@TempDir Path tempDir) throws Exception {
        Path file = tempDir.resolve(FILE_NAME_1);

        Files.createFile(file);

        assertTrue(Files.exists(file));

        rmApplication.removeFileOnly(file.toFile());

        // Check that file is deleted.
        assertFalse(Files.exists(file));
    }

    /**
     * Tests removeFileAndEmptyFolderOnly method when input file is absent.
     * Expected: Throws RmException with ERR_IO_EXCEPTION
     */
    @Test
    void testRemoveFileAndEmptyFolderOnlyWhenFileAbsentShouldThrowRmException(@TempDir Path tempDir) {
        Path file = tempDir.resolve(FILE_NAME_1);

        assertFalse(Files.exists(file)); // check to ensure file does not exist initially.

        RmException exception = assertThrows(RmException.class, () -> {
            rmApplication.removeFileAndEmptyFolderOnly(file.toFile());
        });

        assertEquals(new RmException(ERR_IO_EXCEPTION).getMessage(), exception.getMessage());
    }

    /**
     * Tests removeFileAndEmptyFolderOnly method when input file exists.
     * Expected: Input file is deleted.
     */
    @Test
    void testRemoveFileAndEmptyFolderOnlyWhenFileExistsShouldDeleteFile(@TempDir Path tempDir) throws Exception {
        Path file = tempDir.resolve(FILE_NAME_1);

        Files.createFile(file);

        assertTrue(Files.exists(file));

        rmApplication.removeFileAndEmptyFolderOnly(file.toFile());

        // Check that file is deleted.
        assertFalse(Files.exists(file));
    }

    /**
     * Tests removeFileAndEmptyFolderOnly method when input folder is absent.
     * Expected: Throws RmException with ERR_IO_EXCEPTION
     */
    @Test
    void testRemoveFileAndEmptyFolderOnlyWhenInputFolderIsAbsentShouldThrowRmException(@TempDir Path tempDir) {
        Path folder = tempDir.resolve(FOLDER_NAME_1);

        assertFalse(Files.isDirectory(folder)); // check to ensure folder does not exist initially.

        RmException exception = assertThrows(RmException.class, () -> {
            rmApplication.removeFileAndEmptyFolderOnly(folder.toFile());
        });

        assertEquals(new RmException(ERR_IO_EXCEPTION).getMessage(), exception.getMessage());
    }

    /**
     * Tests removeFileAndEmptyFolderOnly method when input empty folder exists.
     * Expected : Input empty folder is deleted.
     */
    @Test
    void testRemoveFileAndEmptyFolderOnlyWhenEmptyFolderExistsShouldDeleteEmptyFolder(@TempDir Path tempDir) throws Exception {
        Path emptyFolder = tempDir.resolve(FOLDER_NAME_1);

        Files.createDirectories(emptyFolder);

        assertTrue(Files.isDirectory(emptyFolder));

        rmApplication.removeFileAndEmptyFolderOnly(emptyFolder.toFile());

        // Check that empty folder is deleted.
        assertFalse(Files.isDirectory(emptyFolder));
    }

    /**
     * Tests removeFileAndEmptyFolderOnly method when input non empty folder exists.
     * Expected: Throws RmException with ERR_NON_EMPTY_DIR
     */
    @Test
    void testRemoveFileAndEmptyFolderOnlyWhenInputNonEmptyFolderShouldThrowRmException(@TempDir Path tempDir) throws Exception {
        Path fileInFolder = tempDir.resolve(FOLDER_NAME_1 + File.separator + FILE_NAME_1);
        Path nonEmptyFolder = fileInFolder.getParent();

        Files.createDirectories(fileInFolder);

        assertTrue(Files.isDirectory(nonEmptyFolder));
        assertTrue(Files.exists(fileInFolder));

        RmException exception = assertThrows(RmException.class, () -> {
            rmApplication.removeFileAndEmptyFolderOnly(nonEmptyFolder.toFile());
        });

        assertEquals(new RmException(ERR_NON_EMPTY_DIR).getMessage(), exception.getMessage());

        // Check that the non empty folder still exist.
        assertTrue(Files.isDirectory(nonEmptyFolder));
        assertTrue(Files.exists(fileInFolder));
    }

    /**
     * Tests removeFilesAndFolderContent method when input file is absent.
     * Expected: Throws RmException with ERR_IO_EXCEPTION
     */
    @Test
    void testRemoveFilesAndFolderContentWhenInputFileAbsentShouldThrowRmException(@TempDir Path tempDir) {
        Path file = tempDir.resolve(FILE_NAME_1);

        assertFalse(Files.exists(file)); // check to ensure file does not exist initially.

        RmException exception = assertThrows(RmException.class, () -> {
            rmApplication.removeFilesAndFolderContent(file.toFile());
        });

        assertEquals(new RmException(ERR_IO_EXCEPTION).getMessage(), exception.getMessage());
    }

    /**
     * Tests removeFileAndFolderContent method when input file exists.
     * Expected: Input file is deleted.
     */
    @Test
    void testRemoveFileAndFolderContentWhenInputFileExistsShouldDeleteFile(@TempDir Path tempDir) throws Exception {
        Path file = tempDir.resolve(FILE_NAME_1);

        Files.createFile(file);

        assertTrue(Files.exists(file));

        rmApplication.removeFilesAndFolderContent(file.toFile());

        // Check that file is deleted.
        assertFalse(Files.exists(file));
    }

    /**
     * Tests removeFileAndFolderContent method when input folder is absent.
     * Expected: Throws RmException with ERR_IO_EXCEPTION
     */
    @Test
    void testRemoveFileAndFolderContentWhenInputFolderIsAbsentShouldThrowRmException(@TempDir Path tempDir) {
        Path folder = tempDir.resolve(FOLDER_NAME_1);

        assertFalse(Files.isDirectory(folder)); // check to ensure folder does not exist initially.

        RmException exception = assertThrows(RmException.class, () -> {
            rmApplication.removeFilesAndFolderContent(folder.toFile());
        });

        assertEquals(new RmException(ERR_IO_EXCEPTION).getMessage(), exception.getMessage());
    }

    /**
     * Tests removeFileAndFolderContent method when input empty folder exists.
     * Expected: Input empty folder is deleted.
     */
    @Test
    void testRemoveFileAndFolderContentWhenEmptyFolderExistsShouldDeleteEmptyFolder(@TempDir Path tempDir) throws Exception {
        Path emptyFolder = tempDir.resolve(FOLDER_NAME_1);

        Files.createDirectories(emptyFolder);

        assertTrue(Files.isDirectory(emptyFolder));

        rmApplication.removeFilesAndFolderContent(emptyFolder.toFile());

        // Check that empty folder is deleted.
        assertFalse(Files.isDirectory(emptyFolder));
    }

    /**
     * Tests removeFileAndFolderContent method when input non empty folder exists.
     * Expected: Input non empty folder is deleted.
     */
    @Test
    void testRemoveFileAndFolderContentWhenInputNonEmptyFolderShouldDeleteNonEmptyFolder(@TempDir Path tempDir) throws Exception {
        Path fileInFolder = tempDir.resolve(FOLDER_NAME_1 + File.separator + FILE_NAME_1);
        Path nonEmptyFolder = fileInFolder.getParent();

        Files.createDirectories(fileInFolder);

        assertTrue(Files.isDirectory(nonEmptyFolder));
        assertTrue(Files.exists(fileInFolder));

        rmApplication.removeFilesAndFolderContent(nonEmptyFolder.toFile());

        // Check that the non empty folder is deleted.
        assertFalse(Files.isDirectory(nonEmptyFolder));
        assertFalse(Files.exists(fileInFolder));
    }
}