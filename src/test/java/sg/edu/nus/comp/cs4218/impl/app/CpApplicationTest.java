package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import sg.edu.nus.comp.cs4218.exception.CpException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
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
    void cpSrcFileToDestFileWhenSrcFileAbsentShouldThrowCpException(@TempDir Path tempDir) throws IOException {
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
    void cpSrcFileToDestFileWhenSrcFilePresentDestFileAbsentShouldCopyContentFromSrcFileToDestFile(@TempDir Path tempDir) throws IOException, Exception {
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
    void cpSrcFileToDestFileWhenSrcFilePresentDestFilePresentShouldCopyContentFromSrcFileToOverwriteDestFile(@TempDir Path tempDir) throws IOException, Exception {
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
    void cpSrcFileToDestFileWhenSourceFileAndDestinationFileInputSameShouldThrowCpException(@TempDir Path tempDir) throws IOException {
        Path srcFile = tempDir.resolve(SRC_FILE);;
        Files.createFile(srcFile);

        CpException exception = assertThrows(CpException.class, () -> {
           cpApplication.cpSrcFileToDestFile(srcFile.toString(), srcFile.toString());
        });
        assertEquals(new CpException(ERR_SRC_DEST_SAME).getMessage(), exception.getMessage());
    }

    /**
     * Tests cpFilesToFolder method when input files exists but the destination folder does not exist.
     * For example: cp 1.txt dest
     * Where 1.txt is an existing file. dest is a directory which does not exist.
     * Expected: Throws CpException with ERR_FILE_NOT_FOUND
     */
    @Test
    void cpFilesToFolderWhenDestFolderDoesNoExistShouldThrowCpException(@TempDir Path tempDir) throws IOException {
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
    void cpFilesToFolderWhenInputSourceContainsAtLeastOneNonExistingFileShouldThrowCpException(@TempDir Path tempDir) throws IOException {
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
    void cpFilesToFolderWhenInputSourceFilesExistsAndDestFolderExistsShouldCopyContentFromSourceFilesToDestFolder(@TempDir Path tempDir) throws Exception {
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
     * Expected: Copies the content of the 1.txt to be copied to he dest directory and overwrite the existing 1.txt file in the dest directory.
     */
    @Test
    void cpFilesToFolderWhenInputSourceFilesExistsAndDestFolderContainsFileWithSameFileNameAsFileToBeCopiedShouldOverwriteTheFileInDestWithInputFileContent(@TempDir Path tempDir) throws Exception {
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
     * Tests run method when input args is null.
     * Expected: Throws CpException with ERR_NULL_ARGS
     */
    @Test
    void runWhenInputArgsIsNullShouldThrowCpException() {
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
    void runWhenInputArgsIsEmptyShouldThrowCpException() {
        String[] argsList = {};
        CpException exception = assertThrows(CpException.class, () -> {
            cpApplication.run(argsList, mock(InputStream.class), mock(OutputStream.class));
        });
        assertEquals(new CpException(ERR_NO_ARGS).getMessage(), exception.getMessage());
    }

    /**
     * Tests run method when input args only has one element.
     * For example: cp 1.txt
     * Expected: Throws CpException with ERR_MISSING_ARG
     */
    @Test
    void runWhenInputArgsOnlyHasOneElementShouldThrowCpException(@TempDir Path tempDir) {
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
    void runWhenInputSourceContainsADirectoryShouldThrowCpException(@TempDir Path tempDir) throws IOException {
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
    void runWhenInputSourceContainsAtLeastOneNonExistingFileShouldThrowCpException(@TempDir Path tempDir) throws IOException {
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
    void runWhenInputSourceFilesExistsAndDestFolderExistsShouldCopyContentFromSourceFilesToDestFolder(@TempDir Path tempDir) throws Exception {
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
    void runWhenInputSourceFilesExistsAndDestFolderContainsFileWithSameFileNameAsFileToBeCopiedShouldOverwriteTheFileInDestWithInputFileContent(@TempDir Path tempDir) throws Exception {
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

    // TODO

    /**
     * Tests run method when source file is absent.
     * For example: cp 1.txt dest.txt
     * Where 1.txt is a non existing file while dest.txt is the destination file.
     * Expected: Throws CpException with ERR_FILE_NOT_FOUND
     */
    @Test
    void runWhenSrcFileAbsentShouldThrowCpException(@TempDir Path tempDir) throws IOException {
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
    void runWhenSrcFilePresentDestFileAbsentShouldCopyContentFromSrcFileToDestFile(@TempDir Path tempDir) throws IOException, Exception {
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
    void runWhenSrcFilePresentDestFilePresentShouldCopyContentFromSrcFileToOverwriteDestFile(@TempDir Path tempDir) throws IOException, Exception {
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
    void runWhenSourceFileAndDestinationFileInputSameShouldThrowCpException(@TempDir Path tempDir) throws IOException {
        Path srcFile = tempDir.resolve(SRC_FILE);;
        Files.createFile(srcFile);

        String[] argsList = {srcFile.toString(), srcFile.toString()};
        CpException exception = assertThrows(CpException.class, () -> {
            cpApplication.run(argsList, mock(InputStream.class), mock(OutputStream.class));
        });
        assertEquals(new CpException(ERR_SRC_DEST_SAME).getMessage(), exception.getMessage());
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