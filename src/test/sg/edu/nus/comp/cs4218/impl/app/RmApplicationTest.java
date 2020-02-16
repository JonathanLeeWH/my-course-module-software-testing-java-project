package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import sg.edu.nus.comp.cs4218.exception.RmException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;

class RmApplicationTest {

    private static final String FILE_NAME_1 = "1.txt";
    private static final String FILE_NAME_2 = "2.txt";
    private static final String FOLDER_NAME_1 = "hello";

    private RmApplication rmApplication;

    @BeforeEach
    void setUp() {
        rmApplication = new RmApplication();
    }

    /**
     * Tests remove method when input file/folder does not exist.
     * For example: rm 1.txt
     * Where 1.txt does not exist.
     * Expected: Throws RmException with ERR_FILE_NOT_FOUND
     */
    @Test
    void removeInputFileAbsentThrowsRmException(@TempDir Path tempDir) throws Exception {
        Path file = tempDir.resolve(FILE_NAME_1);
        String[] fileNames = {file.toString()};

        assertFalse(Files.exists(file));

        Exception exception = assertThrows(RmException.class, () -> {
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
    void removeNoFlagsFileExistsShouldDeleteFile(@TempDir Path tempDir) throws Exception {
        Path file = tempDir.resolve(FILE_NAME_1);
        String[] fileNames = {file.toString()};

        Files.createFile(file);

        assertTrue(Files.exists(file));

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
    void removeNoFlagsEmptyFolderExistsThrowsRmException(@TempDir Path tempDir) throws IOException {
        Path emptyFolder = tempDir.resolve(FOLDER_NAME_1);
        String[] fileNames = {emptyFolder.toString()};

        Files.createDirectory(emptyFolder);

        assertTrue(Files.isDirectory(emptyFolder));

        // rm with no flags
        Exception exception = assertThrows(RmException.class, () -> {
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
    void removeNoFlagsFilesExistsShouldDeleteFiles(@TempDir Path tempDir) throws Exception {
        Path file1 = tempDir.resolve(FILE_NAME_1);
        Path file2 = tempDir.resolve(FILE_NAME_2);
        String[] fileNames = {file1.toString(), file2.toString()};

        Files.createFile(file1);
        Files.createFile(file2);

        assertTrue(Files.exists(file1));
        assertTrue(Files.exists(file2));

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
    void removeNoFlagsFilesAndEmptyFolderExistsShouldThrowRmException(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve(FILE_NAME_1);
        Path emptyFolder  = tempDir.resolve(FOLDER_NAME_1);
        String[] fileNames = {file.toString(), emptyFolder.toString()};

        Files.createFile(file);
        Files.createDirectory(emptyFolder);

        assertTrue(Files.exists(file));
        assertTrue(Files.isDirectory(emptyFolder));

        // rm with no flags
        Exception exception = assertThrows(RmException.class, () -> {
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
    void removeNoFlagsFilesAndNonEmptyFolderExistsShouldThrowRmException(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve(FILE_NAME_1);
        Path fileInFolder = tempDir.resolve(FOLDER_NAME_1 + File.separator + FILE_NAME_2);
        Path nonEmptyFolder = fileInFolder.getParent();
        String[] fileNames = {file.toString(), nonEmptyFolder.toString()};

        Files.createFile(file);
        Files.createDirectories(fileInFolder);
        
        assertTrue(Files.exists(file));
        assertTrue(Files.isDirectory(nonEmptyFolder));
        assertTrue(Files.exists(fileInFolder));

        // rm with no flags
        Exception exception = assertThrows(RmException.class, () -> {
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
    void removeNoFlagsMultipleFileArgumentsIncludeNonExistingFileAndExistingFolderShouldDeleteExistingFileAndThrowLatestRmException(@TempDir Path tempDir) throws IOException {
        Path file1 = tempDir.resolve(FILE_NAME_1);
        Path file2 = tempDir.resolve(FILE_NAME_2);
        Path folder  = tempDir.resolve(FOLDER_NAME_1);
        String[] fileNames = {folder.toString(), file1.toString(), file2.toString()};

        Files.createFile(file1);
        Files.createDirectories(folder);
        assertTrue(Files.exists(file1));
        assertTrue(Files.isDirectory(folder));
        assertFalse(Files.exists(file2)); // file2 does not exist.

        // rm with no flags
        Exception exception = assertThrows(RmException.class, () -> {
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
    void removeDFlagEmptyFolderExistsShouldDeleteEmptyFolder(@TempDir Path tempDir) throws Exception {
        Path emptyFolder = tempDir.resolve(FOLDER_NAME_1);
        String[] fileNames = {emptyFolder.toString()};

        Files.createDirectory(emptyFolder);

        assertTrue(Files.isDirectory(emptyFolder));

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
    void removeDFlagNonEmptyFolderExistsThrowsRmException(@TempDir Path tempDir) throws Exception {
        Path fileInFolder = tempDir.resolve(FOLDER_NAME_1 + File.separator + FILE_NAME_2);
        Path nonEmptyFolder = fileInFolder.getParent();;
        String[] fileNames = {nonEmptyFolder.toString()};

        Files.createDirectories(fileInFolder);

        assertTrue(Files.isDirectory(nonEmptyFolder));
        assertTrue(Files.exists(fileInFolder));

        // rm with -d flag
        Exception exception = assertThrows(RmException.class, () -> {
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
    void removeRFlagNonEmptyFolderExistsShouldDeleteNonEmptyFolder(@TempDir Path tempDir) throws Exception {
        Path fileInFolder = tempDir.resolve(FOLDER_NAME_1 + File.separator + FILE_NAME_2);
        Path nonEmptyFolder = fileInFolder.getParent();;
        String[] fileNames = {nonEmptyFolder.toString()};

        Files.createDirectories(fileInFolder);

        assertTrue(Files.isDirectory(nonEmptyFolder));
        assertTrue(Files.exists(fileInFolder));

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
    void removeFileOnlyWhenFileAbsentShouldThrowRmException(@TempDir Path tempDir) {
        Path file = tempDir.resolve(FILE_NAME_1);

        assertFalse(Files.exists(file)); // check to ensure file does not exist initially.

        Exception exception = assertThrows(RmException.class, () -> {
            rmApplication.removeFileOnly(file.toFile());
        });

        assertEquals(new RmException(ERR_IO_EXCEPTION).getMessage(), exception.getMessage());
    }

    /**
     * Tests removeFileOnly method when input file exists.
     * Expected: Input file is deleted.
     */
    @Test
    void removeFileOnlyWhenFileExistsShouldDeleteFile(@TempDir Path tempDir) throws Exception {
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
    void removeFileAndEmptyFolderOnlyWhenFileAbsentShouldThrowRmException(@TempDir Path tempDir) {
        Path file = tempDir.resolve(FILE_NAME_1);

        assertFalse(Files.exists(file)); // check to ensure file does not exist initially.

        Exception exception = assertThrows(RmException.class, () -> {
            rmApplication.removeFileAndEmptyFolderOnly(file.toFile());
        });

        assertEquals(new RmException(ERR_IO_EXCEPTION).getMessage(), exception.getMessage());
    }

    /**
     * Tests removeFileAndEmptyFolderOnly method when input file exists.
     * Expected: Input file is deleted.
     */
    @Test
    void removeFileAndEmptyFolderOnlyWhenFileExistsShouldDeleteFile(@TempDir Path tempDir) throws Exception {
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
    void removeFileAndEmptyFolderOnlyWhenInputFolderIsAbsentShouldThrowRmException(@TempDir Path tempDir) {
        Path folder = tempDir.resolve(FOLDER_NAME_1);

        assertFalse(Files.isDirectory(folder)); // check to ensure folder does not exist initially.

        Exception exception = assertThrows(RmException.class, () -> {
            rmApplication.removeFileAndEmptyFolderOnly(folder.toFile());
        });

        assertEquals(new RmException(ERR_IO_EXCEPTION).getMessage(), exception.getMessage());
    }

    /**
     * Tests removeFileAndEmptyFolderOnly method when input empty folder exists.
     * Expected : Input empty folder is deleted.
     */
    @Test
    void removeFileAndEmptyFolderOnlyWhenEmptyFolderExistsShouldDeleteEmptyFolder(@TempDir Path tempDir) throws Exception {
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
    void removeFileAndEmptyFolderOnlyWhenInputNonEmptyFolderShouldThrowRmException(@TempDir Path tempDir) throws Exception {
        Path fileInFolder = tempDir.resolve(FOLDER_NAME_1 + File.separator + FILE_NAME_1);
        Path nonEmptyFolder = fileInFolder.getParent();

        Files.createDirectories(fileInFolder);

        assertTrue(Files.isDirectory(nonEmptyFolder));
        assertTrue(Files.exists(fileInFolder));

        Exception exception = assertThrows(RmException.class, () -> {
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
    void removeFilesAndFolderContentWhenInputFileAbsentShouldThrowRmException(@TempDir Path tempDir) {
        Path file = tempDir.resolve(FILE_NAME_1);

        assertFalse(Files.exists(file)); // check to ensure file does not exist initially.

        Exception exception = assertThrows(RmException.class, () -> {
            rmApplication.removeFilesAndFolderContent(file.toFile());
        });

        assertEquals(new RmException(ERR_IO_EXCEPTION).getMessage(), exception.getMessage());
    }

    /**
     * Tests removeFileAndFolderContent method when input file exists.
     * Expected: Input file is deleted.
     */
    @Test
    void removeFileAndFolderContentWhenInputFileExistsShouldDeleteFile(@TempDir Path tempDir) throws Exception {
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
    void removeFileAndFolderContentWhenInputFolderIsAbsentShouldThrowRmException(@TempDir Path tempDir) {
        Path folder = tempDir.resolve(FOLDER_NAME_1);

        assertFalse(Files.isDirectory(folder)); // check to ensure folder does not exist initially.

        Exception exception = assertThrows(RmException.class, () -> {
            rmApplication.removeFilesAndFolderContent(folder.toFile());
        });

        assertEquals(new RmException(ERR_IO_EXCEPTION).getMessage(), exception.getMessage());
    }

    /**
     * Tests removeFileAndFolderContent method when input empty folder exists.
     * Expected : Input empty folder is deleted.
     */
    @Test
    void removeFileAndFolderContentWhenEmptyFolderExistsShouldDeleteEmptyFolder(@TempDir Path tempDir) throws Exception {
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
    void removeFileAndFolderContentWhenInputNonEmptyFolderShouldDeleteNonEmptyFolder(@TempDir Path tempDir) throws Exception {
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

//
//    @Test
//    void removeFilesAndFolderContent() {
//    }
//
//    @Test
//    void run() {
//    }
}