package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import sg.edu.nus.comp.cs4218.exception.RmException;
import sg.edu.nus.comp.cs4218.impl.app.RmApplication;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

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

//    @AfterEach
//    void tearDown() {
//    }
//
//    @Test
//    void remove() {
//    }

    /**
     * Tests for removing a file without any flags.
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
     * Tests for removing an empty folder without any flags.
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
     * Tests for removing multiple files (no folders) without any flags.
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
     * Tests for removing multiple files (with empty folders) without any flags.
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
     * Tests for removing multiple files (with non empty folders) without any flags.
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
     * Tests for removing a non existing directory followed by an existing file followed by a non existing file.
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

        assertFalse(Files.exists(file));
    }

//    @Test
//    void removeFileAndEmptyFolderOnlyWhenNonEmptyFolderIsPresent() {
//
//    }
//
//    @Test
//    void removeFilesAndFolderContent() {
//    }
//
//    @Test
//    void run() {
//    }
}