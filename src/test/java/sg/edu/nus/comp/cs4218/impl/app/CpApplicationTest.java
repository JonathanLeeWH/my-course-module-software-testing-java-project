package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.api.io.TempDir;
import sg.edu.nus.comp.cs4218.exception.CpException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;

class CpApplicationTest {

    private static final String SRC_FILE = "1.txt";
    private static final String DEST_FILE = "dest.txt";
    private static final String DEST_FOLDER = "dest";
    private static final String FOLDER_NAME_1 = "hello";
    private static final String FILE_NAME_1 = "2.txt";
    private static final String FILE_CONTENT_1 = "Hello world";
    private static final String FILE_CONTENT_2 = "How are you";

    private CpApplication cpApplication;

    @BeforeEach
    void setUp() {
        cpApplication = new CpApplication();
    }

    /**
     * Tests cpSrcFileToDestFile method when source file is absent.
     * For example: cp 1.txt dest.txt
     * Where 1.txt is a non existing file while dest.txt is the destination file.
     * Expected: Throws CpException with ERR_FILE_NOT_FOUND
     */
    @Test
    void testCpSrcFileToDestFileWhenSrcFileAbsentShouldThrowCpException(@TempDir Path tempDir) throws IOException {
        Path srcFile = tempDir.resolve(SRC_FILE);
        Path destFile = tempDir.resolve(DEST_FILE);

        assertFalse(Files.exists(srcFile)); // check source file does not exist

       CpException exception = assertThrows(CpException.class, () -> {
            cpApplication.cpSrcFileToDestFile(srcFile.toString(), destFile.toString());
        });

       assertEquals(new CpException(ERR_FILE_NOT_FOUND).getMessage(), exception.getMessage());
    }

    /**
     * Tests cpSrcFileToDestFile method when source file is present and dest file is absent.
     * For example: cp 1.txt dest.txt
     * Where 1.txt is an existing file while dest.txt is a non existing file.
     * Expected: Copies the contents of source file to destination file. Since the destination file is absent,
     * created.
     */
    @Test
    void testCpSrcFileToDestFileWhenSrcFilePresentDestFileAbsentShouldCopyContentFromSrcFileToDestFile(@TempDir Path tempDir) throws IOException, Exception {
        Path srcFile = tempDir.resolve(SRC_FILE);
        Path destFile = tempDir.resolve(DEST_FILE);
        List<String> fileContents = Arrays.asList(FILE_CONTENT_1, FILE_CONTENT_2);
        Files.createFile(srcFile);
        Files.write(srcFile, fileContents);

        assertTrue(Files.exists(srcFile)); // check source file exists
        assertFalse(Files.exists(destFile)); // check destination file does not exists

        cpApplication.cpSrcFileToDestFile(srcFile.toString(), destFile.toString());
        assertTrue(Files.exists(destFile)); // check that destination file is created.
        assertEquals(fileContents, Files.readAllLines(destFile));
    }

    /**
     * Tests cpSrcFileToDestFile method when source file is present and dest file is present.
     * For example: cp 1.txt dest.txt
     * Where 1.txt and dest.txt are existing files
     * Expected: Copies the contents of source file to destination file. Since the destination file is present,
     * the destination file is overwritten by contents of the source file similar to in unix.
     */
    @Test
    void testCpSrcFileToDestFileWhenSrcFilePresentDestFilePresentShouldCopyContentFromSrcFileToOverwriteDestFile(@TempDir Path tempDir) throws IOException, Exception {
        Path srcFile = tempDir.resolve(SRC_FILE);
        Path destFile = tempDir.resolve(DEST_FILE);
        List<String> fileContents = Arrays.asList(FILE_CONTENT_1, FILE_CONTENT_2);
        Files.createFile(srcFile);
        Files.write(srcFile, fileContents);

        assertTrue(Files.exists(srcFile)); // check source file exists
        Files.createFile(destFile);
        assertTrue(Files.exists(destFile)); // check destination file exists

        cpApplication.cpSrcFileToDestFile(srcFile.toString(), destFile.toString());
        assertEquals(fileContents, Files.readAllLines(destFile));
    }

    /**
     * Tests cpSrcFileToDestFile method when input source file and destination file is the same.
     * For example: cp 1.txt 1.txt
     * Where 1.txt is an existing file.
     * Expected: Throws CpException with ERR_SRC_DEST_SAME
     */
    @Test
    void testCpSrcFileToDestFileWhenSourceFileAndDestinationFileInputSameShouldThrowCpException(@TempDir Path tempDir) throws IOException {
        Path srcFile = tempDir.resolve(SRC_FILE);
        Files.createFile(srcFile);

        CpException exception = assertThrows(CpException.class, () -> {
           cpApplication.cpSrcFileToDestFile(srcFile.toString(), srcFile.toString());
        });
        assertEquals(new CpException(ERR_SRC_DEST_SAME).getMessage(), exception.getMessage());
    }

    /**
     * Tests cpSrcFileToDestFile method when the input destination file has read only permission.
     * For example: cp 1.txt dest.txt
     * Where 1.txt and dest.txt are existing files. dest.txt has read only permissions.
     * Expected: Throws CpException with ERR_NO_PERM
     */
    @Test
    void testCpSrcFileToDestFileWhenDestinationFileIsReadOnlyShouldThrowCpException(@TempDir Path tempDir) throws IOException {
        Path srcFile = tempDir.resolve(SRC_FILE);
        Path destFile = tempDir.resolve(DEST_FILE);
        List<String> fileContents = Arrays.asList(FILE_CONTENT_1, FILE_CONTENT_2);
        List<String> fileContents2 = Arrays.asList(FILE_CONTENT_1);
        Files.createFile(srcFile);
        Files.write(srcFile, fileContents);
        Files.createFile(destFile);
        Files.write(destFile, fileContents2);
        assertTrue(Files.exists(srcFile)); // check 1.txt exists
        assertTrue(Files.exists(destFile)); // check dest.txt exists
        if (System.getProperty("os.name").toLowerCase().contains("win")) {//NOPMD {
            Files.setAttribute(destFile, "dos:readonly", true);
        } else {
            destFile.toFile().setReadOnly();
        }
        CpException exception = assertThrows(CpException.class, () -> {
            cpApplication.cpSrcFileToDestFile(srcFile.toString(), destFile.toString());
        });
        assertEquals(new CpException(ERR_NO_PERM).getMessage(), exception.getMessage());
        assertTrue(Files.exists(destFile)); // check dest.txt exists.
        assertEquals(fileContents2, Files.readAllLines(destFile)); // check that dest.txt contents is not overwritten by 1.txt contents.
        if (System.getProperty("os.name").toLowerCase().contains("win")) {//NOPMD {
            Files.setAttribute(destFile, "dos:readonly", false);
        } else {
            destFile.toFile().setWritable(true); // reset permissions from read only.
        }
    }

    /**
     * Tests cpFilesToFolder method when input files exists but the destination folder does not exist.
     * For example: cp 1.txt dest
     * Where 1.txt is an existing file. dest is a directory which does not exist.
     * Expected: Throws CpException with ERR_FILE_NOT_FOUND
     */
    @Test
    void testCpFilesToFolderWhenDestFolderDoesNoExistShouldThrowCpException(@TempDir Path tempDir) throws IOException {
        Path srcFile = tempDir.resolve(SRC_FILE);
        Path destFolder = tempDir.resolve(DEST_FOLDER);
        Files.createFile(srcFile);
        assertTrue(Files.exists(srcFile)); // check input file exists.
        assertFalse(Files.isDirectory(destFolder)); // check destination folder does not exist.

        String[] fileNameList = {srcFile.toString()};
        CpException exception = assertThrows(CpException.class, () -> {
            cpApplication.cpFilesToFolder(destFolder.toString(), fileNameList);
        });
        assertEquals(new CpException(ERR_FILE_NOT_FOUND).getMessage(), exception.getMessage());
    }

    /**
     * Tests cpFilesToFolder method when input source file have at least one source file that does not exist.
     * For example: cp 1.txt 2.txt dest
     * Where 2.txt is a non existing file, 1.txt is an existing file and dest is an existing directory.
     * Expected: Throws CpException with ERR_FILE_NOT_FOUND and 1.txt is copied into dest folder.
     * This is similar to unix behaviour.
     */
    @Test
    void testCpFilesToFolderWhenInputSourceContainsAtLeastOneNonExistingFileShouldThrowCpException(@TempDir Path tempDir) throws IOException {
        Path file1 = tempDir.resolve(SRC_FILE);
        Path file2 = tempDir.resolve(FILE_NAME_1);
        Path destFolder = tempDir.resolve(DEST_FOLDER);
        Files.createDirectories(destFolder);
        Files.createFile(file1);
        assertTrue(Files.isDirectory(destFolder)); // checks that dest folder exists.
        assertTrue(Files.exists(file1)); // checks that file1 exists.
        assertFalse(Files.exists(file2)); // checks that file2 does not exist.

        String[] fileNameList = {file1.toString(), file2.toString()};
        CpException exception = assertThrows(CpException.class, () -> {
            cpApplication.cpFilesToFolder(destFolder.toString(), fileNameList);
        });
        assertEquals(new CpException(ERR_FILE_NOT_FOUND).getMessage(), exception.getMessage());
        Path copiedFile = tempDir.resolve(DEST_FOLDER + File.separator + SRC_FILE);
        assertTrue(Files.exists(copiedFile)); // checks that the existing file is copied into dest folder.
    }

    /**
     * Tests cpFilesToFolder method when input source files exists and the destination directory exists.
     * For example: cp 1.txt dest
     * Where 1.txt is an existing file and dest is an existing directory
     * Expected: 1.txt is copied into dest directory.
     */
    @Test
    void testCpFilesToFolderWhenInputSourceFilesExistsAndDestFolderExistsShouldCopyContentFromSourceFilesToDestFolder(@TempDir Path tempDir) throws Exception {
        Path srcFile = tempDir.resolve(SRC_FILE);
        Path destFolder = tempDir.resolve(DEST_FOLDER);
        Files.createFile(srcFile);
        Files.createDirectories(destFolder);
        assertTrue(Files.exists(srcFile));
        assertTrue(Files.exists(destFolder));

        String[] fileNamesList = {srcFile.toString()};
        cpApplication.cpFilesToFolder(destFolder.toString(), fileNamesList);
        Path copiedFile = tempDir.resolve(DEST_FOLDER + File.separator + SRC_FILE);
        assertTrue(Files.exists(copiedFile));
    }

    /**
     * Tests cpFilesToFolder method when destination folder contains a file with the same file name as the file to be copied.
     * For example: cp 1.txt dest
     * Where 1.txt is an existing file to be copied and dest is an existing folder containing another 1.txt file.
     * Expected: Copies the content of the 1.txt to be copied to the dest directory and overwrite the existing 1.txt file in the dest directory.
     */
    @Test
    void testCpFilesToFolderWhenInputSourceFilesExistsAndDestFolderContainsFileWithSameFileNameAsFileToBeCopiedShouldOverwriteTheFileInDestWithInputFileContent(@TempDir Path tempDir) throws Exception {
        Path srcFile = tempDir.resolve(SRC_FILE);
        Path destFolderFile = tempDir.resolve(DEST_FOLDER + File.separator + SRC_FILE);
        List<String> fileContents = Arrays.asList(FILE_CONTENT_1, FILE_CONTENT_2);
        Files.createFile(srcFile);
        Files.write(srcFile, fileContents);
        Files.createDirectories(destFolderFile.getParent());
        Files.createFile(destFolderFile);
        assertTrue(Files.exists(srcFile)); // checks src file exists
        assertTrue(Files.exists(destFolderFile)); // checks file exists in dest folder.

        String[] fileNamesList = {srcFile.toString()};
        cpApplication.cpFilesToFolder(destFolderFile.getParent().toString(), fileNamesList);
        assertEquals(fileContents, Files.readAllLines(destFolderFile));
    }

    /**
     * Tests cpFilesToFolder method when there are multiple CpException thrown, only the latest exception will be thrown.
     * For example: cp hello 1.txt 2.txt dest
     * Where hello and dest are existing directories while 1.txt is a non existing file and 2.txt is an existing file. The dest directory does not contain 1.txt.
     * Expected: Throws latest CpException in this case CpException with ERR_FILE_NOT_FOUND is thrown. At the same time, copies 2.txt into dest folder.
     */
    @Test
    void testCpFilesToFolderWhenInputSourceFilesThrowsMultipleCpExceptionAndDestFolderExistsShouldThrowLatestCpExceptionAndCopiesValidSourceFileIntoDestFolder(@TempDir Path tempDir) throws IOException {
        Path folder = tempDir.resolve(FOLDER_NAME_1);
        Path file1 = tempDir.resolve(SRC_FILE);
        Path file2 = tempDir.resolve(FILE_NAME_1);
        Path destFolder = tempDir.resolve(DEST_FOLDER);
        List<String> fileContents = Arrays.asList(FILE_CONTENT_1, FILE_CONTENT_2);
        Files.createDirectories(folder);
        Files.createFile(file2);
        Files.write(file2, fileContents);
        Files.createDirectories(destFolder);
        assertTrue(Files.isDirectory(folder)); // hello folder exists
        assertFalse(Files.exists(file1)); // 1.txt does not exist
        assertTrue(Files.exists(file2)); // 2.txt exists.
        assertTrue(Files.isDirectory(destFolder)); // dest folder exists

        String[] fileNameList = {folder.toString(), file1.toString(), file2.toString()};
        CpException exception = assertThrows(CpException.class, () -> {
            cpApplication.cpFilesToFolder(destFolder.toString(), fileNameList);
        });
        assertEquals(new CpException(ERR_FILE_NOT_FOUND).getMessage(), exception.getMessage());
        Path copiedFile = destFolder.resolve(FILE_NAME_1);
        assertTrue(Files.exists(copiedFile));
        assertEquals(fileContents, Files.readAllLines(copiedFile));
    }

    /**
     * Tests cpFilesToFolder method when the input destination file to be overwritten has read only permission.
     * For example: cp 1.txt dest
     * Where 1.txt is an existing file to be copied and dest is an existing folder containing another 1.txt file.
     * Expected: Throws CpException with ERR_NO_PERM
     */
    @Test
    void testCpFilesToFolderWhenDestinationFileToBeOverwrittenIsReadOnlyShouldThrowCpException(@TempDir Path tempDir) throws IOException {
        Path srcFile = tempDir.resolve(SRC_FILE);
        Path destFolder = tempDir.resolve(DEST_FOLDER);
        Path destFile = destFolder.resolve(SRC_FILE);
        List<String> fileContents = Arrays.asList(FILE_CONTENT_1, FILE_CONTENT_2);
        List<String> fileContents2 = Arrays.asList(FILE_CONTENT_1);
        Files.createFile(srcFile);
        Files.write(srcFile, fileContents);
        Files.createDirectory(destFolder);
        Files.createFile(destFile);
        Files.write(destFile, fileContents2);
        assertTrue(Files.exists(srcFile)); // check 1.txt exists
        assertTrue(Files.isDirectory(destFolder)); // check dest directory exists
        assertTrue(Files.exists(destFile)); // check 1.txt in dest directory exists
        String[] fileNameList = {srcFile.toString()};
        if (System.getProperty("os.name").toLowerCase().contains("win")) {//NOPMD {
            Files.setAttribute(destFile, "dos:readonly", true);
        } else {
            destFile.toFile().setReadOnly();
        }
        CpException exception = assertThrows(CpException.class, () -> {
            cpApplication.cpFilesToFolder(destFolder.toString(), fileNameList);
        });
        assertEquals(new CpException(ERR_NO_PERM).getMessage(), exception.getMessage());
        assertTrue(Files.exists(destFile)); // check dest.txt exists.
        assertEquals(fileContents2, Files.readAllLines(destFile)); // check that dest.txt contents is not overwritten by 1.txt contents.
        if (System.getProperty("os.name").toLowerCase().contains("win")) {//NOPMD {
            Files.setAttribute(destFile, "dos:readonly", false);
        } else {
            destFile.toFile().setWritable(true); // reset permissions from read only.
        }
    }

    /**
     * Tests cpFilesToFolder method when the input destination folder has a file which has same file name as one of the input source file with read only permission
     * and one of the input source file does not exist.
     * For example: cp 1.txt 2.txt dest
     * Where 1.txt is an existing file to be copied and dest is an existing folder containing another 1.txt file which only has read only permission. 2.txt is a non existing file.
     * In this case, it will throw the latest CpException which is ERR_FILE_NOT_FOUND
     * Expected: Throws latest CpException with ERR_FILE_NOT_FOUND
     */
    @Test
    void testCpFilesToFolderWhenOneOfTheFileInDestinationFolderToBeOverwrittenIsReadOnlyAndAnotherInputSourceFileDoesNotExistShouldThrowCpException(@TempDir Path tempDir) throws IOException {
        Path srcFile = tempDir.resolve(SRC_FILE);
        Path srcFile2 = tempDir.resolve(FILE_NAME_1);
        Path destFolder = tempDir.resolve(DEST_FOLDER);
        Path destFile = destFolder.resolve(SRC_FILE);
        List<String> fileContents = Arrays.asList(FILE_CONTENT_1, FILE_CONTENT_2);
        List<String> fileContents2 = Arrays.asList(FILE_CONTENT_1);
        Files.createFile(srcFile);
        Files.write(srcFile, fileContents);
        Files.createDirectory(destFolder);
        Files.createFile(destFile);
        Files.write(destFile, fileContents2);
        assertTrue(Files.exists(srcFile)); // check 1.txt exists
        assertFalse(Files.exists(srcFile2)); // check 2.txt does not exist
        assertTrue(Files.isDirectory(destFolder)); // check dest directory exists
        assertTrue(Files.exists(destFile)); // check 1.txt in dest directory exists
        String[] fileNameList = {srcFile.toString(), srcFile2.toString()};
        if (System.getProperty("os.name").toLowerCase().contains("win")) {//NOPMD {
            Files.setAttribute(destFile, "dos:readonly", true);
        } else {
            destFile.toFile().setReadOnly();
        }
        CpException exception = assertThrows(CpException.class, () -> {
            cpApplication.cpFilesToFolder(destFolder.toString(), fileNameList);
        });
        assertEquals(new CpException(ERR_FILE_NOT_FOUND).getMessage(), exception.getMessage());
        assertTrue(Files.exists(destFile)); // check dest.txt exists.
        assertEquals(fileContents2, Files.readAllLines(destFile)); // check that dest.txt contents is not overwritten by 1.txt contents.
        if (System.getProperty("os.name").toLowerCase().contains("win")) {//NOPMD {
            Files.setAttribute(destFile, "dos:readonly", false);
        } else {
            destFile.toFile().setWritable(true); // reset permissions from read only.
        }
    }

    /**
     * Tests cpFilesToFolder method when the input destination folder has no execute permission.
     * Note: As stated earlier, Windows have issue with permission in Java so it might not work on Windows.
     * This test case is disabled on Windows platform.
     * For example: cp 1.txt dest
     * Where 1.txt is an existing file to be copied and dest is an existing folder with no execute permission.
     * In this case, it will throw CpException: ERR_NO_PERM (similar to in unix shell where file cannot be copied to folder with no execute permission)
     * Expected: Throws CpException with ERR_NO_PERM
     */
    @Test
    @DisabledOnOs(OS.WINDOWS)
    void testCpFilesToFolderWhenDestinationDirectoryHasNoExecutablePermissionShouldThrowCpException(@TempDir Path tempDir) throws IOException {
        Path srcFile = tempDir.resolve(SRC_FILE);
        Path srcFile2 = tempDir.resolve(FILE_NAME_1);
        Path destFolder = tempDir.resolve(DEST_FOLDER);
        Path destFile = destFolder.resolve(SRC_FILE);
        List<String> fileContents = Arrays.asList(FILE_CONTENT_1, FILE_CONTENT_2);
        List<String> fileContents2 = Arrays.asList(FILE_CONTENT_1);
        Files.createFile(srcFile);
        Files.write(srcFile, fileContents);
        Files.createDirectory(destFolder);
        Files.createFile(destFile);
        Files.write(destFile, fileContents2);
        assertTrue(Files.exists(srcFile)); // check 1.txt exists
        assertFalse(Files.exists(srcFile2)); // check 2.txt does not exist
        assertTrue(Files.isDirectory(destFolder)); // check dest directory exists
        assertTrue(Files.exists(destFile)); // check 1.txt in dest directory exists
        String[] fileNameList = {srcFile.toString(), srcFile2.toString()};
        destFolder.toFile().setExecutable(false); // set the dest directory to have no executable permission.
        assertFalse(destFolder.toFile().canExecute()); // check that the dest directory have no executable permission.
        CpException exception = assertThrows(CpException.class, () -> {
            cpApplication.cpFilesToFolder(destFolder.toString(), fileNameList);
        });
        assertEquals(new CpException(ERR_NO_PERM).getMessage(), exception.getMessage());
        destFile.toFile().setExecutable(true); // reset permissions from no execute permission
    }

    /**
     * Tests run method when input args is null.
     * Expected: Throws CpException with ERR_NULL_ARGS
     */
    @Test
    void testRunWhenInputArgsIsNullShouldThrowCpException() {
        CpException exception = assertThrows(CpException.class, () -> {
            cpApplication.run(null, mock(InputStream.class), mock(OutputStream.class));
        });
        assertEquals(new CpException(ERR_NULL_ARGS).getMessage(), exception.getMessage());
    }

    /**
     * Tests run method when input args is empty.
     * Expected: Throws CpException with ERR_NO_ARGS
     */
    @Test
    void testRunWhenInputArgsIsEmptyShouldThrowCpException() {
        String[] argsList = {};
        CpException exception = assertThrows(CpException.class, () -> {
            cpApplication.run(argsList, mock(InputStream.class), mock(OutputStream.class));
        });
        assertEquals(new CpException(ERR_NO_ARGS).getMessage(), exception.getMessage());
    }

    /**
     * Tests run method when input stream is null although based on skeleton code input stream is not used.
     * This is for defensive programming.
     * Expected: Throws CpException with ERR_NULL_STREAMS
     */
    @Test
    void testRunWhenInputStreamIsNullShouldThrowCpException(@TempDir Path tempDir) {
        Path file = tempDir.resolve(SRC_FILE);
        String[] argsList = {file.toString()};
        CpException exception = assertThrows(CpException.class, () -> {
            cpApplication.run(argsList, null, mock(OutputStream.class));
        });
        assertEquals(new CpException(ERR_NULL_STREAMS).getMessage(), exception.getMessage());
    }

    /**
     * Tests run method when output stream is null although based on skeleton code input stream is not used.
     * This is for defensive programming.
     * Expected: Throws CpException with ERR_NULL_STREAMS
     */
    @Test
    void testRunWhenOutputStreamIsNullShouldThrowCpException(@TempDir Path tempDir) {
        Path file = tempDir.resolve(SRC_FILE);
        String[] argsList = {file.toString()};
        CpException exception = assertThrows(CpException.class, () -> {
            cpApplication.run(argsList, mock(InputStream.class), null);
        });
        assertEquals(new CpException(ERR_NULL_STREAMS).getMessage(), exception.getMessage());
    }

    /**
     * Tests run method when input args only has one element.
     * For example: cp 1.txt
     * Expected: Throws CpException with ERR_MISSING_ARG
     */
    @Test
    void testRunWhenInputArgsOnlyHasOneElementShouldThrowCpException(@TempDir Path tempDir) {
        Path file = tempDir.resolve(SRC_FILE);
        String[] argsList = {file.toString()};
        CpException exception = assertThrows(CpException.class, () -> {
            cpApplication.run(argsList, mock(InputStream.class), mock(OutputStream.class));
        });
        assertEquals(new CpException(ERR_MISSING_ARG).getMessage(), exception.getMessage());
    }

    /**
     * Tests run method when input source files contain a existing directory.
     * For example: cp hello 2.txt
     * Where hello is an existing directory
     * Expected: Throws CpException with ERR_IS_DIR
     */
    @Test
    void testRunWhenInputSourceContainsADirectoryShouldThrowCpException(@TempDir Path tempDir) throws IOException {
        Path folder = tempDir.resolve(FOLDER_NAME_1);
        Path file = tempDir.resolve(FILE_NAME_1);
        Files.createDirectories(folder);
        assertTrue(Files.isDirectory(folder));

        String[] argsList = {folder.toString(), file.toString()};
        CpException exception = assertThrows(CpException.class, () -> {
           cpApplication.run(argsList, mock(InputStream.class), mock(OutputStream.class));
        });
        assertEquals(new CpException(ERR_IS_DIR).getMessage(), exception.getMessage());
    }

    /**
     * Tests run method when input source file have at least one source file that does not exist.
     * For example: cp 1.txt 2.txt dest
     * Where 2.txt is a non existing file, 1.txt is an existing file and dest is an existing directory.
     * Expected: Throws CpException with ERR_FILE_NOT_FOUND and 1.txt is copied into dest folder.
     * This is similar to unix behaviour.
     */
    @Test
    void testRunWhenInputSourceContainsAtLeastOneNonExistingFileShouldThrowCpException(@TempDir Path tempDir) throws IOException {
        Path file1 = tempDir.resolve(SRC_FILE);
        Path file2 = tempDir.resolve(FILE_NAME_1);
        Path destFolder = tempDir.resolve(DEST_FOLDER);
        Files.createDirectories(destFolder);
        Files.createFile(file1);
        assertTrue(Files.isDirectory(destFolder)); // checks that dest folder exists.
        assertTrue(Files.exists(file1)); // checks that file1 exists.
        assertFalse(Files.exists(file2)); // checks that file2 does not exist.

        String[] argsList = {file1.toString(), file2.toString(), destFolder.toString()};
        CpException exception = assertThrows(CpException.class, () -> {
            cpApplication.run(argsList, mock(InputStream.class), mock(OutputStream.class));
        });
        assertEquals(new CpException(ERR_FILE_NOT_FOUND).getMessage(), exception.getMessage());
        Path copiedFile = tempDir.resolve(DEST_FOLDER + File.separator + SRC_FILE);
        assertTrue(Files.exists(copiedFile)); // checks that the existing file is copied into dest folder.
    }

    /**
     * Tests run method when input source files exists and the destination directory exists.
     * For example: cp 1.txt dest
     * Where 1.txt is an existing file and dest is an existing directory
     * Expected: 1.txt is copied into dest directory.
     */
    @Test
    void testRunWhenInputSourceFilesExistsAndDestFolderExistsShouldCopyContentFromSourceFilesToDestFolder(@TempDir Path tempDir) throws Exception {
        Path srcFile = tempDir.resolve(SRC_FILE);
        Path destFolder = tempDir.resolve(DEST_FOLDER);
        Files.createFile(srcFile);
        Files.createDirectories(destFolder);
        assertTrue(Files.exists(srcFile));
        assertTrue(Files.exists(destFolder));

        String[] argsList = {srcFile.toString(), destFolder.toString()};
        cpApplication.run(argsList, mock(InputStream.class), mock(OutputStream.class));
        Path copiedFile = tempDir.resolve(DEST_FOLDER + File.separator + SRC_FILE);
        assertTrue(Files.exists(copiedFile));
    }

    /**
     * Tests run method when destination folder contains a file with the same file name as the file to be copied.
     * For example: cp 1.txt dest
     * Where 1.txt is an existing file to be copied and dest is an existing folder containing another 1.txt file.
     * Expected: Copies the content of the 1.txt to be copied to he dest directory and overwrite the existing 1.txt file in the dest directory.
     */
    @Test
    void testRunWhenInputSourceFilesExistsAndDestFolderContainsFileWithSameFileNameAsFileToBeCopiedShouldOverwriteTheFileInDestWithInputFileContent(@TempDir Path tempDir) throws Exception {
        Path srcFile = tempDir.resolve(SRC_FILE);
        Path destFolderFile = tempDir.resolve(DEST_FOLDER + File.separator + SRC_FILE);
        List<String> fileContents = Arrays.asList(FILE_CONTENT_1, FILE_CONTENT_2);
        Files.createFile(srcFile);
        Files.write(srcFile, fileContents);
        Files.createDirectories(destFolderFile.getParent());
        Files.createFile(destFolderFile);
        assertTrue(Files.exists(srcFile)); // checks src file exists
        assertTrue(Files.exists(destFolderFile)); // checks file exists in dest folder.

        String[] argsList = {srcFile.toString(), destFolderFile.getParent().toString()};
        cpApplication.run(argsList, mock(InputStream.class), mock(OutputStream.class));
        assertEquals(fileContents, Files.readAllLines(destFolderFile));
    }

    /**
     * Tests run method when source file is absent.
     * For example: cp 1.txt dest.txt
     * Where 1.txt is a non existing file while dest.txt is the destination file.
     * Expected: Throws CpException with ERR_FILE_NOT_FOUND
     */
    @Test
    void testRunWhenSrcFileAbsentShouldThrowCpException(@TempDir Path tempDir) throws IOException {
        Path srcFile = tempDir.resolve(SRC_FILE);
        Path destFile = tempDir.resolve(DEST_FILE);

        assertFalse(Files.exists(srcFile)); // check source file does not exist

        String[] argsList = {srcFile.toString(), destFile.toString()};
        CpException exception = assertThrows(CpException.class, () -> {
            cpApplication.run(argsList, mock(InputStream.class), mock(OutputStream.class));
        });

        assertEquals(new CpException(ERR_FILE_NOT_FOUND).getMessage(), exception.getMessage());
    }

    /**
     * Tests run method when source file is present and dest file is absent.
     * For example: cp 1.txt dest.txt
     * Where 1.txt is an existing file while dest.txt is a non existing file.
     * Expected: Copies the contents of source file to destination file. Since the destination file is absent,
     * created.
     */
    @Test
    void testRunWhenSrcFilePresentDestFileAbsentShouldCopyContentFromSrcFileToDestFile(@TempDir Path tempDir) throws IOException, Exception {
        Path srcFile = tempDir.resolve(SRC_FILE);
        Path destFile = tempDir.resolve(DEST_FILE);
        List<String> fileContents = Arrays.asList(FILE_CONTENT_1, FILE_CONTENT_2);
        Files.createFile(srcFile);
        Files.write(srcFile, fileContents);

        assertTrue(Files.exists(srcFile)); // check source file exists
        assertFalse(Files.exists(destFile)); // check destination file does not exists

        String[] argsList = {srcFile.toString(), destFile.toString()};
        cpApplication.run(argsList, mock(InputStream.class), mock(OutputStream.class));
        assertTrue(Files.exists(destFile)); // check that destination file is created.
        assertEquals(fileContents, Files.readAllLines(destFile));
    }

    /**
     * Tests run method when source file is present and dest file is present.
     * For example: cp 1.txt dest.txt
     * Where 1.txt and dest.txt are existing files
     * Expected: Copies the contents of source file to destination file. Since the destination file is present,
     * the destination file is overwritten by contents of the source file similar to in unix.
     */
    @Test
    void testRunWhenSrcFilePresentDestFilePresentShouldCopyContentFromSrcFileToOverwriteDestFile(@TempDir Path tempDir) throws IOException, Exception {
        Path srcFile = tempDir.resolve(SRC_FILE);
        Path destFile = tempDir.resolve(DEST_FILE);
        List<String> fileContents = Arrays.asList(FILE_CONTENT_1, FILE_CONTENT_2);
        Files.createFile(srcFile);
        Files.write(srcFile, fileContents);

        assertTrue(Files.exists(srcFile)); // check source file exists
        Files.createFile(destFile);
        assertTrue(Files.exists(destFile)); // check destination file exists

        String[] argsList = {srcFile.toString(), destFile.toString()};
        cpApplication.run(argsList, mock(InputStream.class), mock(OutputStream.class));
        assertEquals(fileContents, Files.readAllLines(destFile));
    }

    /**
     * Tests run method when input source file and destination file is the same.
     * For example: cp 1.txt 1.txt
     * Where 1.txt is an existing file.
     * Expected: Throws CpException with ERR_SRC_DEST_SAME
     */
    @Test
    void testRunWhenSourceFileAndDestinationFileInputSameShouldThrowCpException(@TempDir Path tempDir) throws IOException {
        Path srcFile = tempDir.resolve(SRC_FILE);
        Files.createFile(srcFile);

        String[] argsList = {srcFile.toString(), srcFile.toString()};
        CpException exception = assertThrows(CpException.class, () -> {
            cpApplication.run(argsList, mock(InputStream.class), mock(OutputStream.class));
        });
        assertEquals(new CpException(ERR_SRC_DEST_SAME).getMessage(), exception.getMessage());
    }

    /**
     * Tests run method when there are multiple CpException thrown, only the latest exception will be thrown.
     * For example: cp hello 1.txt 2.txt dest
     * Where hello and dest are existing directories while 1.txt is a non existing file and 2.txt is an existing file. The dest directory does not contain 1.txt.
     * Expected: Throws latest CpException in this case CpException with ERR_FILE_NOT_FOUND is thrown. At the same time, copies 2.txt into dest folder.
     */
    @Test
    void testRunWhenInputSourceFilesThrowsMultipleCpExceptionAndDestFolderExistsShouldThrowLatestCpExceptionAndCopiesValidSourceFileIntoDestFolder(@TempDir Path tempDir) throws IOException {
        Path folder = tempDir.resolve(FOLDER_NAME_1);
        Path file1 = tempDir.resolve(SRC_FILE);
        Path file2 = tempDir.resolve(FILE_NAME_1);
        Path destFolder = tempDir.resolve(DEST_FOLDER);
        List<String> fileContents = Arrays.asList(FILE_CONTENT_1, FILE_CONTENT_2);
        Files.createDirectories(folder);
        Files.createFile(file2);
        Files.write(file2, fileContents);
        Files.createDirectories(destFolder);
        assertTrue(Files.isDirectory(folder)); // hello folder exists
        assertFalse(Files.exists(file1)); // 1.txt does not exist
        assertTrue(Files.exists(file2)); // 2.txt exists.
        assertTrue(Files.isDirectory(destFolder)); // dest folder exists

        String[] argsList = {folder.toString(), file1.toString(), file2.toString(), destFolder.toString()};
        CpException exception = assertThrows(CpException.class, () -> {
            cpApplication.run(argsList, mock(InputStream.class), mock(OutputStream.class));
        });
        assertEquals(new CpException(ERR_FILE_NOT_FOUND).getMessage(), exception.getMessage());
        Path copiedFile = destFolder.resolve(FILE_NAME_1);
        assertTrue(Files.exists(copiedFile));
        assertEquals(fileContents, Files.readAllLines(copiedFile));
    }

    /**
     * Tests run method when the input destination file to be overwritten has read only permission.
     * For example: cp 1.txt dest
     * Where 1.txt is an existing file to be copied and dest is an existing folder containing another 1.txt file.
     * Expected: Throws CpException with ERR_NO_PERM
     */
    @Test
    void testRunWhenDestinationFileToBeOverwrittenIsReadOnlyShouldThrowCpException(@TempDir Path tempDir) throws IOException {
        Path srcFile = tempDir.resolve(SRC_FILE);
        Path destFolder = tempDir.resolve(DEST_FOLDER);
        Path destFile = destFolder.resolve(SRC_FILE);
        List<String> fileContents = Arrays.asList(FILE_CONTENT_1, FILE_CONTENT_2);
        List<String> fileContents2 = Arrays.asList(FILE_CONTENT_1);
        Files.createFile(srcFile);
        Files.write(srcFile, fileContents);
        Files.createDirectory(destFolder);
        Files.createFile(destFile);
        Files.write(destFile, fileContents2);
        assertTrue(Files.exists(srcFile)); // check 1.txt exists
        assertTrue(Files.isDirectory(destFolder)); // check dest directory exists
        assertTrue(Files.exists(destFile)); // check 1.txt in dest directory exists
        String[] argsList = {srcFile.toString(), destFile.toString()};
        if (System.getProperty("os.name").toLowerCase().contains("win")) {//NOPMD {
            Files.setAttribute(destFile, "dos:readonly", true);
        } else {
            destFile.toFile().setReadOnly();
        }
        CpException exception = assertThrows(CpException.class, () -> {
            cpApplication.run(argsList, mock(InputStream.class), mock(OutputStream.class));
        });
        assertEquals(new CpException(ERR_NO_PERM).getMessage(), exception.getMessage());
        assertTrue(Files.exists(destFile)); // check dest.txt exists.
        assertEquals(fileContents2, Files.readAllLines(destFile)); // check that dest.txt contents is not overwritten by 1.txt contents.
        if (System.getProperty("os.name").toLowerCase().contains("win")) {//NOPMD {
            Files.setAttribute(destFile, "dos:readonly", false);
        } else {
            destFile.toFile().setWritable(true); // reset permissions from read only.
        }
    }

    /**
     * Tests run method when the input destination file has read only permission and one of the input source file does not exist.
     * For example: cp 1.txt 2.txt dest
     * Where 1.txt is an existing file to be copied and dest is an existing folder containing another 1.txt file. 2.txt is a non existing file.
     * In this case, it will throw the latest CpException which is ERR_FILE_NOT_FOUND
     * Expected: Throws latest CpException with ERR_FILE_NOT_FOUND
     */
    @Test
    void testRunWhenOneOfTheDestinationFileToBeOverwrittenIsReadOnlyAndAnotherInputSourceFileDoesNotExistShouldThrowCpException(@TempDir Path tempDir) throws IOException {
        Path srcFile = tempDir.resolve(SRC_FILE);
        Path srcFile2 = tempDir.resolve(FILE_NAME_1);
        Path destFolder = tempDir.resolve(DEST_FOLDER);
        Path destFile = destFolder.resolve(SRC_FILE);
        List<String> fileContents = Arrays.asList(FILE_CONTENT_1, FILE_CONTENT_2);
        List<String> fileContents2 = Arrays.asList(FILE_CONTENT_1);
        Files.createFile(srcFile);
        Files.write(srcFile, fileContents);
        Files.createDirectory(destFolder);
        Files.createFile(destFile);
        Files.write(destFile, fileContents2);
        assertTrue(Files.exists(srcFile)); // check 1.txt exists
        assertFalse(Files.exists(srcFile2)); // check 2.txt does not exist
        assertTrue(Files.isDirectory(destFolder)); // check dest directory exists
        assertTrue(Files.exists(destFile)); // check 1.txt in dest directory exists
        String[] argsList = {srcFile.toString(), srcFile2.toString(), destFolder.toString()};
        if (System.getProperty("os.name").toLowerCase().contains("win")) {//NOPMD {
            Files.setAttribute(destFile, "dos:readonly", true);
        } else {
            destFile.toFile().setReadOnly();
        }
        CpException exception = assertThrows(CpException.class, () -> {
            cpApplication.run(argsList, mock(InputStream.class), mock(OutputStream.class));
        });
        assertEquals(new CpException(ERR_FILE_NOT_FOUND).getMessage(), exception.getMessage());
        assertTrue(Files.exists(destFile)); // check dest.txt exists.
        assertEquals(fileContents2, Files.readAllLines(destFile)); // check that dest.txt contents is not overwritten by 1.txt contents.
        if (System.getProperty("os.name").toLowerCase().contains("win")) {//NOPMD {
            Files.setAttribute(destFile, "dos:readonly", false);
        } else {
            destFile.toFile().setWritable(true); // reset permissions from read only.
        }
    }

    /**
     * Tests run method when the input destination folder has no execute permission.
     * Note: As stated earlier, Windows have issue with permission in Java so it might not work on Windows.
     * This test case is disabled on Windows platform.
     * For example: cp 1.txt dest
     * Where 1.txt is an existing file to be copied and dest is an existing folder with no execute permission.
     * In this case, it will throw CpException: ERR_NO_PERM (similar to in unix shell where file cannot be copied to folder with no execute permission)
     * Expected: Throws CpException with ERR_NO_PERM
     */
    @Test
    @DisabledOnOs(OS.WINDOWS)
    void testRunWhenDestinationDirectoryHasNoExecutablePermissionShouldThrowCpException(@TempDir Path tempDir) throws IOException {
        Path srcFile = tempDir.resolve(SRC_FILE);
        Path srcFile2 = tempDir.resolve(FILE_NAME_1);
        Path destFolder = tempDir.resolve(DEST_FOLDER);
        Path destFile = destFolder.resolve(SRC_FILE);
        List<String> fileContents = Arrays.asList(FILE_CONTENT_1, FILE_CONTENT_2);
        List<String> fileContents2 = Arrays.asList(FILE_CONTENT_1);
        Files.createFile(srcFile);
        Files.write(srcFile, fileContents);
        Files.createDirectory(destFolder);
        Files.createFile(destFile);
        Files.write(destFile, fileContents2);
        assertTrue(Files.exists(srcFile)); // check 1.txt exists
        assertFalse(Files.exists(srcFile2)); // check 2.txt does not exist
        assertTrue(Files.isDirectory(destFolder)); // check dest directory exists
        assertTrue(Files.exists(destFile)); // check 1.txt in dest directory exists
        String[] argsList = {srcFile.toString(), srcFile2.toString(), destFolder.toString()};
        destFolder.toFile().setExecutable(false); // set the dest directory to have no executable permission.
        assertFalse(destFolder.toFile().canExecute()); // check that the dest directory have no executable permission.
        CpException exception = assertThrows(CpException.class, () -> {
            cpApplication.run(argsList, mock(InputStream.class), mock(OutputStream.class));
        });
        assertEquals(new CpException(ERR_NO_PERM).getMessage(), exception.getMessage());
        destFile.toFile().setExecutable(true); // reset permissions from no execute permission
    }

//    private static boolean isFilesEqual(Path first, Path second) throws IOException {
//        if (Files.size(first) != Files.size(second)) {
//            return false;
//        }
//
//        try (BufferedReader reader1 = Files.newBufferedReader(first); BufferedReader reader2 = Files.newBufferedReader(second)) {
//            String currentLine1;
//            String currentLine2;
//            // Read the first file content line by line and compare with the second file line by line.
//            while((currentLine1 = reader1.readLine()) != null) {
//                // If second file is shorter than first file, return false.
//                if ((currentLine2 = reader2.readLine()) == null) {
//                    return false;
//                }
//                if (!currentLine1.equals(currentLine2)) {
//                    return false;
//                }
//            }
//            // At the end of the while loop currentLine1 should be null so we check if currentLine2 is null.
//            currentLine2 = reader2.readLine();
//            return currentLine2 == null;
//        }
//    }
}