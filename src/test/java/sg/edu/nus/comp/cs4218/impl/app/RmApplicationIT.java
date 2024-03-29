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
    private static final String RD_FLAG = "-rd";
    private static final String DR_FLAG = "-dr";
    private static final String RELATIVE_UP = "..";

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
     * Tests run method with -r or -rd  or -r -d flag for an existing non empty directory.
     * We will only test as long as -r exists by MC/DC to reduce number of test cases as both cases exhibit same behaviour.
     * For example: rm -r hello or rm -r -d hello or rm -rd hello
     * Where hello is a non empty directory that exists.
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

    /**
     * Tests run method with -r flag fpr a non existing file followed by an existing non empty directory.
     * For example: rm -r 1.txt hello
     * Where hello is a non empty directory that exists and 1.txt is a non existing text file.
     * Expected: Throws latest RmException with ERR_FILE_NOT_FOUND as it attempts to remove a non existing 1.txt file. At the same time, it removes hello directory.
     */
    @Test
    void testRunWhenRFlagNonExistingFileAndExistingNonEmptyDirectoryShouldThrowRmExceptionAndRemoveExistingNonEmptyDirectory(@TempDir Path tempDir) throws RmException, IOException {
        Path file = tempDir.resolve(FILE_NAME_1);
        Path fileInFolder = tempDir.resolve(FOLDER_NAME_1 + File.separator + FILE_NAME_2);
        Path nonEmptyFolder = fileInFolder.getParent();;
        String[] argsList = {R_FLAG, FILE_NAME_1, FOLDER_NAME_1};

        Files.createDirectories(fileInFolder);

        assertFalse(Files.exists(file));
        assertTrue(Files.isDirectory(nonEmptyFolder));
        assertTrue(Files.exists(fileInFolder));

        EnvironmentHelper.currentDirectory = tempDir.toString();

        // rm with -r flag
        RmException exception = assertThrows(RmException.class, () -> {
            rmApplication.run(argsList, mock(InputStream.class), mock(OutputStream.class));
        });

        assertEquals(new RmException(ERR_FILE_NOT_FOUND).getMessage(), exception.getMessage());

        // Check that the non empty folder is deleted.
        assertFalse(Files.isDirectory(nonEmptyFolder));
    }

    /**
     * Tests run method with -r flag for an existing file followed by an existing non empty directory.
     * For example rm -r 1.txt hello
     * Where hello is a non empty directory that exists and 1.txt is an existing file.
     * Expected: Removes 1.txt and hello directory
     */
    @Test
    void testRunWhenRFlagExistingFileAndExistingNonEmptyDirectoryShouldRemoveBothFilesAndFolder(@TempDir Path tempDir) throws RmException, IOException {
        Path file = tempDir.resolve(FILE_NAME_1);
        Path fileInFolder = tempDir.resolve(FOLDER_NAME_1 + File.separator + FILE_NAME_2);
        Path nonEmptyFolder = fileInFolder.getParent();;
        String[] argsList = {R_FLAG, FILE_NAME_1, FOLDER_NAME_1};

        Files.createFile(file);
        Files.createDirectories(fileInFolder);

        assertTrue(Files.exists(file));
        assertTrue(Files.isDirectory(nonEmptyFolder));
        assertTrue(Files.exists(fileInFolder));

        EnvironmentHelper.currentDirectory = tempDir.toString();

        // rm with -r flag
        rmApplication.run(argsList, mock(InputStream.class), mock(OutputStream.class));

        // Check that the non empty folder and file are deleted.
        assertFalse(Files.exists(file));
        assertFalse(Files.isDirectory(nonEmptyFolder));
    }

    /**
     * Tests run method with -r flag for an existing empty directory
     * For example: rm -r hello
     * Where hello is an existing empty directory.
     * Expected: Removes empty hello directory.
     */
    @Test
    void testRunWhenRFlagExistingEmptyDirectoryShouldRemoveEmptyDirectory(@TempDir Path tempDir) throws RmException, IOException {
        Path emptyFolder = tempDir.resolve(FOLDER_NAME_1);
        String[] argsList = {R_FLAG, FOLDER_NAME_1};

        Files.createDirectory(emptyFolder);
        assertTrue(Files.exists(emptyFolder));

        EnvironmentHelper.currentDirectory = tempDir.toString();

        // rm with -r flag
        rmApplication.run(argsList, mock(InputStream.class), mock(OutputStream.class));

        // Check that the empty folder is deleted.
        assertFalse(Files.exists(emptyFolder));
    }

    /**
     * Tests run method with -r flag for an existing file.
     * For example: rm -r 1.txt
     * Where 1.txt is an existing file.
     * Expected: Removes empty hello directory.
     */
    @Test
    void testRunWhenRFlagExistingFileShouldRemoveFile(@TempDir Path tempDir) throws RmException, IOException {
        Path file = tempDir.resolve(FILE_NAME_1);
        String[] argsList = {R_FLAG, FILE_NAME_1};

        Files.createDirectory(file);
        assertTrue(Files.exists(file));

        EnvironmentHelper.currentDirectory = tempDir.toString();

        // rm with -r flag
        rmApplication.run(argsList, mock(InputStream.class), mock(OutputStream.class));

        // Check that the empty folder is deleted.
        assertFalse(Files.exists(file));
    }

    // More comprehensive test cases to cover those reduced by mc/dc

    /**
     * Tests run method with -rd flag for an existing non empty directory.
     * For example: rm -rd hello
     * Where hello is a non empty directory that exist.
     * Expected: Removes hello directory and its contents
     */
    @Test
    void testRunWhenRDFlagNonEmptyFolderExistsShouldDeleteNonEmptyFolder(@TempDir Path tempDir) throws Exception {
        Path fileInFolder = tempDir.resolve(FOLDER_NAME_1 + File.separator + FILE_NAME_2);
        Path nonEmptyFolder = fileInFolder.getParent();;
        String[] argsList = {RD_FLAG, FOLDER_NAME_1};

        Files.createDirectories(fileInFolder);

        assertTrue(Files.isDirectory(nonEmptyFolder));
        assertTrue(Files.exists(fileInFolder));

        EnvironmentHelper.currentDirectory = tempDir.toString();

        // rm with -rd flag
        rmApplication.run(argsList, mock(InputStream.class), mock(OutputStream.class));

        // Check that the non empty folder is deleted.
        assertFalse(Files.isDirectory(nonEmptyFolder));
    }

    /**
     * Tests run method with -rd for a non existing file followed by an existing non empty directory.
     * For example: rm -rd 1.txt hello
     * Where hello is a non empty directory that exists and 1.txt is a non existing text file.
     * Expected: Throws latest RmException with ERR_FILE_NOT_FOUND as it attempts to remove a non existing 1.txt file. At the same time, it removes hello directory.
     */
    @Test
    void testRunWhenRDFlagNonExistingFileAndExistingNonEmptyDirectoryShouldThrowRmExceptionAndRemoveExistingNonEmptyDirectory(@TempDir Path tempDir) throws RmException, IOException {
        Path file = tempDir.resolve(FILE_NAME_1);
        Path fileInFolder = tempDir.resolve(FOLDER_NAME_1 + File.separator + FILE_NAME_2);
        Path nonEmptyFolder = fileInFolder.getParent();;
        String[] argsList = {RD_FLAG, FILE_NAME_1, FOLDER_NAME_1};

        Files.createDirectories(fileInFolder);

        assertFalse(Files.exists(file));
        assertTrue(Files.isDirectory(nonEmptyFolder));
        assertTrue(Files.exists(fileInFolder));

        EnvironmentHelper.currentDirectory = tempDir.toString();

        // rm with -rd flag
        RmException exception = assertThrows(RmException.class, () -> {
            rmApplication.run(argsList, mock(InputStream.class), mock(OutputStream.class));
        });

        assertEquals(new RmException(ERR_FILE_NOT_FOUND).getMessage(), exception.getMessage());

        // Check that the non empty folder is deleted.
        assertFalse(Files.isDirectory(nonEmptyFolder));
    }

    /**
     * Tests run method with -rd flag for an existing file followed by an existing directory.
     * For example rm -rd 1.txt hello
     * Where hello is a non empty directory that exists and 1.txt is an existing file.
     * Expected: Removes 1.txt and hello directory
     */
    @Test
    void testRunWhenRDFlagExistingFileAndExistingNonEmptyDirectoryShouldRemoveBothFilesAndFolder(@TempDir Path tempDir) throws RmException, IOException {
        Path file = tempDir.resolve(FILE_NAME_1);
        Path fileInFolder = tempDir.resolve(FOLDER_NAME_1 + File.separator + FILE_NAME_2);
        Path nonEmptyFolder = fileInFolder.getParent();;
        String[] argsList = {RD_FLAG, FILE_NAME_1, FOLDER_NAME_1};

        Files.createFile(file);
        Files.createDirectories(fileInFolder);

        assertTrue(Files.exists(file));
        assertTrue(Files.isDirectory(nonEmptyFolder));
        assertTrue(Files.exists(fileInFolder));

        EnvironmentHelper.currentDirectory = tempDir.toString();

        // rm with -rd flag
        rmApplication.run(argsList, mock(InputStream.class), mock(OutputStream.class));

        // Check that the non empty folder and file are deleted.
        assertFalse(Files.exists(file));
        assertFalse(Files.isDirectory(nonEmptyFolder));
    }

    /**
     * Tests run method with -rd flag for an existing empty directory
     * For example: rm -rd hello
     * Where hello is an existing empty directory.
     * Expected: Removes empty hello directory.
     */
    @Test
    void testRunWhenRDFlagExistingEmptyDirectoryShouldRemoveEmptyDirectory(@TempDir Path tempDir) throws RmException, IOException {
        Path emptyFolder = tempDir.resolve(FOLDER_NAME_1);
        String[] argsList = {RD_FLAG, FOLDER_NAME_1};

        Files.createDirectory(emptyFolder);
        assertTrue(Files.exists(emptyFolder));

        EnvironmentHelper.currentDirectory = tempDir.toString();

        // rm with -rd flag
        rmApplication.run(argsList, mock(InputStream.class), mock(OutputStream.class));

        // Check that the empty folder is deleted.
        assertFalse(Files.exists(emptyFolder));
    }

    /**
     * Tests run method with with -rd flag for an existing file.
     * For example: rm -rd 1.txt
     * Where 1.txt is an existing file.
     * Expected: Removes empty hello directory.
     */
    @Test
    void testRunWhenRDFlagExistingFileShouldRemoveFile(@TempDir Path tempDir) throws RmException, IOException {
        Path file = tempDir.resolve(FILE_NAME_1);
        String[] argsList = {RD_FLAG, FILE_NAME_1};

        Files.createDirectory(file);
        assertTrue(Files.exists(file));

        EnvironmentHelper.currentDirectory = tempDir.toString();

        // rm with -rd flag
        rmApplication.run(argsList, mock(InputStream.class), mock(OutputStream.class));

        // Check that the empty folder is deleted.
        assertFalse(Files.exists(file));
    }

    /**
     * Tests run method with -dr flag for an existing non empty directory.
     * For example: rm -dr hello
     * Where hello is a non empty directory that exist.
     * Expected: Removes hello directory and its contents
     */
    @Test
    void testRunWhenDRFlagNonEmptyFolderExistsShouldDeleteNonEmptyFolder(@TempDir Path tempDir) throws Exception {
        Path fileInFolder = tempDir.resolve(FOLDER_NAME_1 + File.separator + FILE_NAME_2);
        Path nonEmptyFolder = fileInFolder.getParent();;
        String[] argsList = {DR_FLAG, FOLDER_NAME_1};

        Files.createDirectories(fileInFolder);

        assertTrue(Files.isDirectory(nonEmptyFolder));
        assertTrue(Files.exists(fileInFolder));

        EnvironmentHelper.currentDirectory = tempDir.toString();

        // rm with -dr flag
        rmApplication.run(argsList, mock(InputStream.class), mock(OutputStream.class));

        // Check that the non empty folder is deleted.
        assertFalse(Files.isDirectory(nonEmptyFolder));
    }

    /**
     * Tests run method with -dr for a non existing file followed by an existing non empty directory.
     * For example: rm -dr 1.txt hello
     * Where hello is a non empty directory that exists and 1.txt is a non existing text file.
     * Expected: Throws latest RmException with ERR_FILE_NOT_FOUND as it attempts to remove a non existing 1.txt file. At the same time, it removes hello directory.
     */
    @Test
    void testRunWhenDRFlagNonExistingFileAndExistingNonEmptyDirectoryShouldThrowRmExceptionAndRemoveExistingNonEmptyDirectory(@TempDir Path tempDir) throws RmException, IOException {
        Path file = tempDir.resolve(FILE_NAME_1);
        Path fileInFolder = tempDir.resolve(FOLDER_NAME_1 + File.separator + FILE_NAME_2);
        Path nonEmptyFolder = fileInFolder.getParent();;
        String[] argsList = {DR_FLAG, FILE_NAME_1, FOLDER_NAME_1};

        Files.createDirectories(fileInFolder);

        assertFalse(Files.exists(file));
        assertTrue(Files.isDirectory(nonEmptyFolder));
        assertTrue(Files.exists(fileInFolder));

        EnvironmentHelper.currentDirectory = tempDir.toString();

        // rm with -dr flag
        RmException exception = assertThrows(RmException.class, () -> {
            rmApplication.run(argsList, mock(InputStream.class), mock(OutputStream.class));
        });

        assertEquals(new RmException(ERR_FILE_NOT_FOUND).getMessage(), exception.getMessage());

        // Check that the non empty folder is deleted.
        assertFalse(Files.isDirectory(nonEmptyFolder));
    }

    /**
     * Tests run method with -dr flag for an existing file followed by an existing directory.
     * For example rm -dr 1.txt hello
     * Where hello is a non empty directory that exists and 1.txt is an existing file.
     * Expected: Removes 1.txt and hello directory
     */
    @Test
    void testRunWhenDRFlagExistingFileAndExistingNonEmptyDirectoryShouldRemoveBothFilesAndFolder(@TempDir Path tempDir) throws RmException, IOException {
        Path file = tempDir.resolve(FILE_NAME_1);
        Path fileInFolder = tempDir.resolve(FOLDER_NAME_1 + File.separator + FILE_NAME_2);
        Path nonEmptyFolder = fileInFolder.getParent();;
        String[] argsList = {DR_FLAG, FILE_NAME_1, FOLDER_NAME_1};

        Files.createFile(file);
        Files.createDirectories(fileInFolder);

        assertTrue(Files.exists(file));
        assertTrue(Files.isDirectory(nonEmptyFolder));
        assertTrue(Files.exists(fileInFolder));

        EnvironmentHelper.currentDirectory = tempDir.toString();

        // rm with -dr flag
        rmApplication.run(argsList, mock(InputStream.class), mock(OutputStream.class));

        // Check that the non empty folder and file are deleted.
        assertFalse(Files.exists(file));
        assertFalse(Files.isDirectory(nonEmptyFolder));
    }

    /**
     * Tests run method with -dr flag for an existing empty directory
     * For example: rm -dr hello
     * Where hello is an existing empty directory.
     * Expected: Removes empty hello directory.
     */
    @Test
    void testRunWhenDRFlagExistingEmptyDirectoryShouldRemoveEmptyDirectory(@TempDir Path tempDir) throws RmException, IOException {
        Path emptyFolder = tempDir.resolve(FOLDER_NAME_1);
        String[] argsList = {RD_FLAG, FOLDER_NAME_1};

        Files.createDirectory(emptyFolder);
        assertTrue(Files.exists(emptyFolder));

        EnvironmentHelper.currentDirectory = tempDir.toString();

        // rm with -dr flag
        rmApplication.run(argsList, mock(InputStream.class), mock(OutputStream.class));

        // Check that the empty folder is deleted.
        assertFalse(Files.exists(emptyFolder));
    }

    /**
     * Tests run method with with -dr flag for an existing file.
     * For example: rm -dr 1.txt
     * Where 1.txt is an existing file.
     * Expected: Removes empty hello directory.
     */
    @Test
    void testRunWhenDRFlagExistingFileShouldRemoveFile(@TempDir Path tempDir) throws RmException, IOException {
        Path file = tempDir.resolve(FILE_NAME_1);
        String[] argsList = {RD_FLAG, FILE_NAME_1};

        Files.createDirectory(file);
        assertTrue(Files.exists(file));

        EnvironmentHelper.currentDirectory = tempDir.toString();

        // rm with -dr flag
        rmApplication.run(argsList, mock(InputStream.class), mock(OutputStream.class));

        // Check that the empty folder is deleted.
        assertFalse(Files.exists(file));
    }

    /**
     * Tests run method with -r -d flag for an existing non empty directory.
     * For example: rm -r -d hello
     * Where hello is a non empty directory that exist.
     * Expected: Removes hello directory and its contents
     */
    @Test
    void testRunWhenRFlagDFlagNonEmptyFolderExistsShouldDeleteNonEmptyFolder(@TempDir Path tempDir) throws Exception {
        Path fileInFolder = tempDir.resolve(FOLDER_NAME_1 + File.separator + FILE_NAME_2);
        Path nonEmptyFolder = fileInFolder.getParent();;
        String[] argsList = {R_FLAG, D_FLAG, FOLDER_NAME_1};

        Files.createDirectories(fileInFolder);

        assertTrue(Files.isDirectory(nonEmptyFolder));
        assertTrue(Files.exists(fileInFolder));

        EnvironmentHelper.currentDirectory = tempDir.toString();

        // rm with -r -d flag
        rmApplication.run(argsList, mock(InputStream.class), mock(OutputStream.class));

        // Check that the non empty folder is deleted.
        assertFalse(Files.isDirectory(nonEmptyFolder));
    }

    /**
     * Tests run method with -r -d for a non existing file followed by an existing non empty directory.
     * For example: rm -r -d 1.txt hello
     * Where hello is a non empty directory that exists and 1.txt is a non existing text file.
     * Expected: Throws latest RmException with ERR_FILE_NOT_FOUND as it attempts to remove a non existing 1.txt file. At the same time, it removes hello directory.
     */
    @Test
    void testRunWhenRFlagDFlagNonExistingFileAndExistingNonEmptyDirectoryShouldThrowRmExceptionAndRemoveExistingNonEmptyDirectory(@TempDir Path tempDir) throws RmException, IOException {
        Path file = tempDir.resolve(FILE_NAME_1);
        Path fileInFolder = tempDir.resolve(FOLDER_NAME_1 + File.separator + FILE_NAME_2);
        Path nonEmptyFolder = fileInFolder.getParent();;
        String[] argsList = {R_FLAG, D_FLAG, FILE_NAME_1, FOLDER_NAME_1};

        Files.createDirectories(fileInFolder);

        assertFalse(Files.exists(file));
        assertTrue(Files.isDirectory(nonEmptyFolder));
        assertTrue(Files.exists(fileInFolder));

        EnvironmentHelper.currentDirectory = tempDir.toString();

        // rm with -r -d flag
        RmException exception = assertThrows(RmException.class, () -> {
            rmApplication.run(argsList, mock(InputStream.class), mock(OutputStream.class));
        });

        assertEquals(new RmException(ERR_FILE_NOT_FOUND).getMessage(), exception.getMessage());

        // Check that the non empty folder is deleted.
        assertFalse(Files.isDirectory(nonEmptyFolder));
    }

    /**
     * Tests run method with -r -d flag for an existing file followed by an existing directory.
     * For example rm -r -d 1.txt hello
     * Where hello is a non empty directory that exists and 1.txt is an existing file.
     * Expected: Removes 1.txt and hello directory
     */
    @Test
    void testRunWhenRFlagDFlagExistingFileAndExistingNonEmptyDirectoryShouldRemoveBothFilesAndFolder(@TempDir Path tempDir) throws RmException, IOException {
        Path file = tempDir.resolve(FILE_NAME_1);
        Path fileInFolder = tempDir.resolve(FOLDER_NAME_1 + File.separator + FILE_NAME_2);
        Path nonEmptyFolder = fileInFolder.getParent();;
        String[] argsList = {R_FLAG, D_FLAG, FILE_NAME_1, FOLDER_NAME_1};

        Files.createFile(file);
        Files.createDirectories(fileInFolder);

        assertTrue(Files.exists(file));
        assertTrue(Files.isDirectory(nonEmptyFolder));
        assertTrue(Files.exists(fileInFolder));

        EnvironmentHelper.currentDirectory = tempDir.toString();

        // rm with -r -d flag
        rmApplication.run(argsList, mock(InputStream.class), mock(OutputStream.class));

        // Check that the non empty folder and file are deleted.
        assertFalse(Files.exists(file));
        assertFalse(Files.isDirectory(nonEmptyFolder));
    }

    /**
     * Tests run method with -r -d flag for an existing empty directory
     * For example: rm -r -d hello
     * Where hello is an existing empty directory.
     * Expected: Removes empty hello directory.
     */
    @Test
    void testRunWhenRFlagDFlagExistingEmptyDirectoryShouldRemoveEmptyDirectory(@TempDir Path tempDir) throws RmException, IOException {
        Path emptyFolder = tempDir.resolve(FOLDER_NAME_1);
        String[] argsList = {R_FLAG, D_FLAG, FOLDER_NAME_1};

        Files.createDirectory(emptyFolder);
        assertTrue(Files.exists(emptyFolder));

        EnvironmentHelper.currentDirectory = tempDir.toString();

        // rm with -r -d flag
        rmApplication.run(argsList, mock(InputStream.class), mock(OutputStream.class));

        // Check that the empty folder is deleted.
        assertFalse(Files.exists(emptyFolder));
    }

    /**
     * Tests run method with with -r -d flag for an existing file.
     * For example: rm -r -d 1.txt
     * Where 1.txt is an existing file.
     * Expected: Removes empty hello directory.
     */
    @Test
    void testRunWhenRFlagDFlagExistingFileShouldRemoveFile(@TempDir Path tempDir) throws RmException, IOException {
        Path file = tempDir.resolve(FILE_NAME_1);
        String[] argsList = {R_FLAG, D_FLAG, FILE_NAME_1};

        Files.createDirectory(file);
        assertTrue(Files.exists(file));

        EnvironmentHelper.currentDirectory = tempDir.toString();

        // rm with -r -d flag
        rmApplication.run(argsList, mock(InputStream.class), mock(OutputStream.class));

        // Check that the empty folder is deleted.
        assertFalse(Files.exists(file));
    }

    /**
     * Tests run method with -d -r flag for an existing non empty directory.
     * For example: rm -d -r hello
     * Where hello is a non empty directory that exist.
     * Expected: Removes hello directory and its contents
     */
    @Test
    void testRunWhenDFlagRFlagNonEmptyFolderExistsShouldDeleteNonEmptyFolder(@TempDir Path tempDir) throws Exception {
        Path fileInFolder = tempDir.resolve(FOLDER_NAME_1 + File.separator + FILE_NAME_2);
        Path nonEmptyFolder = fileInFolder.getParent();;
        String[] argsList = {D_FLAG, R_FLAG, FOLDER_NAME_1};

        Files.createDirectories(fileInFolder);

        assertTrue(Files.isDirectory(nonEmptyFolder));
        assertTrue(Files.exists(fileInFolder));

        EnvironmentHelper.currentDirectory = tempDir.toString();

        // rm with -d -r flag
        rmApplication.run(argsList, mock(InputStream.class), mock(OutputStream.class));

        // Check that the non empty folder is deleted.
        assertFalse(Files.isDirectory(nonEmptyFolder));
    }

    /**
     * Tests run method with -d -r for a non existing file followed by an existing non empty directory.
     * For example: rm -d -r 1.txt hello
     * Where hello is a non empty directory that exists and 1.txt is a non existing text file.
     * Expected: Throws latest RmException with ERR_FILE_NOT_FOUND as it attempts to remove a non existing 1.txt file. At the same time, it removes hello directory.
     */
    @Test
    void testRunWhenDFlagRFlagNonExistingFileAndExistingNonEmptyDirectoryShouldThrowRmExceptionAndRemoveExistingNonEmptyDirectory(@TempDir Path tempDir) throws RmException, IOException {
        Path file = tempDir.resolve(FILE_NAME_1);
        Path fileInFolder = tempDir.resolve(FOLDER_NAME_1 + File.separator + FILE_NAME_2);
        Path nonEmptyFolder = fileInFolder.getParent();;
        String[] argsList = {D_FLAG, R_FLAG, FILE_NAME_1, FOLDER_NAME_1};

        Files.createDirectories(fileInFolder);

        assertFalse(Files.exists(file));
        assertTrue(Files.isDirectory(nonEmptyFolder));
        assertTrue(Files.exists(fileInFolder));

        EnvironmentHelper.currentDirectory = tempDir.toString();

        // rm with -d -r flag
        RmException exception = assertThrows(RmException.class, () -> {
            rmApplication.run(argsList, mock(InputStream.class), mock(OutputStream.class));
        });

        assertEquals(new RmException(ERR_FILE_NOT_FOUND).getMessage(), exception.getMessage());

        // Check that the non empty folder is deleted.
        assertFalse(Files.isDirectory(nonEmptyFolder));
    }

    /**
     * Tests run method with -d -r flag for an existing file followed by an existing directory.
     * For example rm -d -r 1.txt hello
     * Where hello is a non empty directory that exists and 1.txt is an existing file.
     * Expected: Removes 1.txt and hello directory
     */
    @Test
    void testRunWhenDFlagRFlagExistingFileAndExistingNonEmptyDirectoryShouldRemoveBothFilesAndFolder(@TempDir Path tempDir) throws RmException, IOException {
        Path file = tempDir.resolve(FILE_NAME_1);
        Path fileInFolder = tempDir.resolve(FOLDER_NAME_1 + File.separator + FILE_NAME_2);
        Path nonEmptyFolder = fileInFolder.getParent();;
        String[] argsList = {D_FLAG, R_FLAG, FILE_NAME_1, FOLDER_NAME_1};

        Files.createFile(file);
        Files.createDirectories(fileInFolder);

        assertTrue(Files.exists(file));
        assertTrue(Files.isDirectory(nonEmptyFolder));
        assertTrue(Files.exists(fileInFolder));

        EnvironmentHelper.currentDirectory = tempDir.toString();

        // rm with -d -r flag
        rmApplication.run(argsList, mock(InputStream.class), mock(OutputStream.class));

        // Check that the non empty folder and file are deleted.
        assertFalse(Files.exists(file));
        assertFalse(Files.isDirectory(nonEmptyFolder));
    }

    /**
     * Tests run method with -d -r flag for an existing empty directory
     * For example: rm -d -r hello
     * Where hello is an existing empty directory.
     * Expected: Removes empty hello directory.
     */
    @Test
    void testRunWhenDFlagRFlagExistingEmptyDirectoryShouldRemoveEmptyDirectory(@TempDir Path tempDir) throws RmException, IOException {
        Path emptyFolder = tempDir.resolve(FOLDER_NAME_1);
        String[] argsList = {D_FLAG, R_FLAG, FOLDER_NAME_1};

        Files.createDirectory(emptyFolder);
        assertTrue(Files.exists(emptyFolder));

        EnvironmentHelper.currentDirectory = tempDir.toString();

        // rm with -d -r flag
        rmApplication.run(argsList, mock(InputStream.class), mock(OutputStream.class));

        // Check that the empty folder is deleted.
        assertFalse(Files.exists(emptyFolder));
    }

    /**
     * Tests run method with -d -r flag for an existing file.
     * For example: rm -d -r 1.txt
     * Where 1.txt is an existing file.
     * Expected: Removes empty hello directory.
     */
    @Test
    void testRunWhenDFlagRFlagExistingFileShouldRemoveFile(@TempDir Path tempDir) throws RmException, IOException {
        Path file = tempDir.resolve(FILE_NAME_1);
        String[] argsList = {D_FLAG, R_FLAG, FILE_NAME_1};

        Files.createDirectory(file);
        assertTrue(Files.exists(file));

        EnvironmentHelper.currentDirectory = tempDir.toString();

        // rm with -d -r flag
        rmApplication.run(argsList, mock(InputStream.class), mock(OutputStream.class));

        // Check that the empty folder is deleted.
        assertFalse(Files.exists(file));
    }

    /**
     * Tests run method when attempting to remove sub path of the current path in this case, the parent of the current directory.
     * For example: rm ..
     * Expected: Throws RmException ERR_IS_SUB_PATH
     */
    @Test
    public void testRunWhenInputSubPathOfCurrentPathShouldThrowRmException() {
        String[] args = {RELATIVE_UP};
        RmException exception = assertThrows(RmException.class, () -> {
            rmApplication.run(args, mock(InputStream.class), mock(OutputStream.class));
        });
        assertEquals(new RmException(ERR_IS_SUB_PATH).getMessage(), exception.getMessage());
    }

    /**
     * Tests run method when attempting to remove the current path in this case, the current directory.
     * For example: rm .
     * Expected: Throws RmException ERR_IS_SUB_PATH
     */
    @Test
    public void testRunWhenInputCurrentPathShouldThrowRmException() {
        String[] args = {"."};
        RmException exception = assertThrows(RmException.class, () -> {
            rmApplication.run(args, mock(InputStream.class), mock(OutputStream.class));
        });
        assertEquals(new RmException(ERR_IS_CURR_DIR).getMessage(), exception.getMessage());
    }

    /**
     * Tests run method when attempting to remove files in sub path for the current path.
     * For example: rm ../1.txt
     * Where 1.txt is a file that exists in the parent directory of the current directory.
     * Expected: Removes 1.txt file.
     */
    @Test
    public void testRunWhenNoFlagInputExistingFilePathWhichInSubPathOfCurrentPathShouldRemoveFile(@TempDir Path tempDir) throws IOException, RmException {
        Path currentDirectory = tempDir.resolve(FOLDER_NAME_1);
        Path fileInParent = tempDir.resolve(FILE_NAME_1);
        String[] argsList = {RELATIVE_UP + File.separator + FILE_NAME_1};

        Files.createFile(fileInParent);
        Files.createDirectories(currentDirectory);
        assertTrue(Files.exists(fileInParent)); // check 1.txt file in parent directory of current directory exists.
        assertTrue(Files.isDirectory(currentDirectory)); // check current directory exists.

        EnvironmentHelper.currentDirectory = currentDirectory.toString();

        rmApplication.run(argsList, mock(InputStream.class), mock(OutputStream.class));

        assertFalse(Files.exists(fileInParent)); // check 1.txt file in parent directory of current directory is deleted.
    }

    /**
     * Tests run method when attempting to remove an empty folder in sub path but not sub path for the current path.
     * For example: rm -d ../hello2
     * Where hello2 is an empty folder that exists in the parent directory of the current directory.
     * For example: The current directory is cd C:\Users\<COMPUTER_USER_NAME>\Documents\cs4218-project-ay1920-s2-2020-TEAMNUMBER and the input directory path to remove is C:\Users\<COMPUTER_USER_NAME>\Documents\hello2
     * Expected: Removes hello2 directory.
     */
    @Test
    public void testRunWhenDFlagInputExistingEmptyFolderPathWhichIsNotSubPathButIsInSubPathOfCurrentPathShouldRemoveEmptyFolder(@TempDir Path tempDir) throws IOException, RmException {
        Path currentDirectory = tempDir.resolve(FOLDER_NAME_1);
        Path folderInParent = tempDir.resolve(FOLDER_NAME_2);
        String[] argsList = {D_FLAG, RELATIVE_UP + File.separator + FOLDER_NAME_2};

        Files.createDirectories(folderInParent);
        Files.createDirectories(currentDirectory);
        assertTrue(Files.isDirectory(folderInParent)); // check hello2 folder in parent directory of current directory exists.
        String[] list = folderInParent.toFile().list();
        assertNotNull(list);
        assertEquals(0, list.length); // check that hello folder is empty.
        assertTrue(Files.isDirectory(currentDirectory)); // check current directory exists.

        EnvironmentHelper.currentDirectory = currentDirectory.toString();

        rmApplication.run(argsList, mock(InputStream.class), mock(OutputStream.class));

        assertFalse(Files.isDirectory(folderInParent)); // check hello2 in parent directory of current directory is deleted.
    }

    /**
     * Tests run method when attempting to remove a non empty folder in sub path but not sub path for the current path.
     * For example: rm -rd ../hello2
     * Where hello2 is a non empty folder that exists in the parent directory of the current directory.
     * For example: The current directory is cd C:\Users\<COMPUTER_USER_NAME>\Documents\cs4218-project-ay1920-s2-2020-TEAMNUMBER and the input directory path to remove is C:\Users\<COMPUTER_USER_NAME>\Documents\hello2
     * Expected: Removes hello2 directory.
     */
    @Test
    public void testRunWhenRDFlagInputExistingNonEmptyFolderPathWhichIsNotSubPathButIsInSubPathOfCurrentPathShouldRemoveNonEmptyFolder(@TempDir Path tempDir) throws IOException, RmException {
        Path currentDirectory = tempDir.resolve(FOLDER_NAME_1);
        Path folderInParent = tempDir.resolve(FOLDER_NAME_2);
        Path fileInFolder = folderInParent.resolve(FILE_NAME_1);
        String[] argsList = {RD_FLAG, RELATIVE_UP + File.separator + FOLDER_NAME_2};

        Files.createDirectories(folderInParent);
        Files.createFile(fileInFolder); // create 1.txt in hello2 folder.
        Files.createDirectories(currentDirectory);
        assertTrue(Files.isDirectory(folderInParent)); // check hello2 folder in parent directory of current directory exists.
        assertTrue(Files.exists(fileInFolder)); // check 1.txt exists in hello2 folder.
        String[] list = folderInParent.toFile().list();
        assertNotNull(list);
        assertNotEquals(0, list.length); // check that hello folder is not empty.
        assertTrue(Files.isDirectory(currentDirectory)); // check current directory exists.

        EnvironmentHelper.currentDirectory = currentDirectory.toString();

        rmApplication.run(argsList, mock(InputStream.class), mock(OutputStream.class));

        assertFalse(Files.isDirectory(folderInParent)); // check hello2 in parent directory of current directory is deleted.
    }
}