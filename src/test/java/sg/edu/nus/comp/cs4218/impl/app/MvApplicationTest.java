package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import sg.edu.nus.comp.cs4218.EnvironmentHelper;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.MvException;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;

import static org.junit.jupiter.api.Assertions.*;
public class MvApplicationTest {

    private static final String TEST_FILE = "testFile";
    private static final String TEST_SAME_NAME = "testFile";
    private static final String TEST_DIFFERENT = "testFile2";
    private static final String TEST_DIFFERENT2 = "testFile3";
    private static final String TEST_FOLDER = "testFolder";
    private static MvApplication mvApplication;
    private static final String FOLDER_PATH = System.getProperty("user.dir");
    private List<File> allFiles = new ArrayList<File>();

    @BeforeEach
    void setupBeforeTest() throws IOException {
        mvApplication = new MvApplication();
    }


    @Test
    public void runWhenNoArgSpecifiedThrowsArgException() {

        AbstractApplicationException exception = assertThrows(MvException.class, () -> {
            mvApplication.run(null, null, null); // stdin and stdout is not used in MvApplication
        });

        assertEquals(new MvException(NO_ARG_EXCEPTION).getMessage(), exception.getMessage());
    }

    @Test
    public void runWhenStdOutisNullThrowException() {
        String[] constructArgs = new String [] {"-n"};
        AbstractApplicationException exception = assertThrows(MvException.class, () -> {
            mvApplication.run(constructArgs, System.in, null); // stdin and stdout is not used in MvApplication
        });

        assertEquals(new MvException(ERR_NO_OSTREAM).getMessage(), exception.getMessage());
    }

    @Test
    public void runWhenMissingArgSpecifiedThrowsArgException() {

        String[] constructArgs = new String [] {"-n"};
        AbstractApplicationException exception = assertThrows(MvException.class, () -> {
            mvApplication.run(constructArgs, System.in, System.out);
        });

        assertEquals(new MvException(MISSING_ARG_EXCEPTION).getMessage(), exception.getMessage());
    }

    @Test
    public void runWhenMissingDestinationArgSpecifiedThrowsArgException() {

        String[] constructArgs = new String [] {"-n",TEST_FILE};
        AbstractApplicationException exception = assertThrows(MvException.class, () -> {
            mvApplication.run(constructArgs, System.in, System.out);
        });

        assertEquals(new MvException(MISSING_ARG_EXCEPTION).getMessage(), exception.getMessage());
    }

    @Test
    public void runWhenMissingArgSpecifiedNoOverwriteThrowsArgException() {

        String[] constructArgs = new String [] {"-n"};
        AbstractApplicationException exception = assertThrows(MvException.class, () -> {
            mvApplication.run(constructArgs, System.in, System.out);
        });

        assertEquals(new MvException(MISSING_ARG_EXCEPTION).getMessage(), exception.getMessage());
    }

    @Test
    public void runWhenMissingDestinationArgSpecifiedNoOverwriteThrowsArgException() {

        String[] constructArgs = new String [] {"-n",TEST_FILE};
        AbstractApplicationException exception = assertThrows(MvException.class, () -> {
            mvApplication.run(constructArgs, System.in, System.out);
        });

        assertEquals(new MvException(MISSING_ARG_EXCEPTION).getMessage(), exception.getMessage());
    }


    @Test
    public void runWhenMvFileToDestFileWithOverwriteSuccess() throws Exception {

        String currentDir = EnvironmentHelper.currentDirectory.trim();
        String filePath1 = currentDir + File.separator + TEST_FILE;
        File file1 = new File(currentDir + File.separator + TEST_FILE);
        File file2 = new File(currentDir + File.separator + TEST_DIFFERENT);

        Files.createFile(Paths.get(filePath1));
        String[] constructArgs = new String [] {TEST_FILE,TEST_DIFFERENT};

        Path file1Path = file1.toPath();
        Path file2Path = file2.toPath();

        assertTrue(Files.exists(file1Path));
        assertFalse(Files.exists(file2Path));
        mvApplication.run(constructArgs, System.in, System.out);

        assertTrue(Files.exists(Paths.get(file2Path.toString())));
        assertTrue(!Files.exists(Paths.get(file1Path.toString())));

        Files.delete(file2Path);

    }

    @Test
    public void mvSrcFileToDestFileFileSuccess(@TempDir Path tempDir) throws Exception {
        Path file1 = tempDir.resolve(TEST_FILE);
        Path file2 = tempDir.resolve(TEST_DIFFERENT);
        String fileSrcString = file1.toString();
        String fileDestString = file2.toString();

        Files.createFile(file1);
        assertTrue(Files.exists(file1));
        assertFalse(Files.exists(file2));
        mvApplication.mvSrcFileToDestFile(fileSrcString,fileDestString);

        assertTrue(Files.exists(Paths.get(fileDestString)));
        assertTrue(!Files.exists(Paths.get(fileSrcString)));
    }

    @Test
    public void runWhenMvFileToDestFileWithNoOverwriteSuccess() throws Exception {

        String currentDir = EnvironmentHelper.currentDirectory.trim();
        String filePath1 = currentDir + File.separator + TEST_FILE;
        File file1 = new File(currentDir + File.separator + TEST_FILE);
        File file2 = new File(currentDir + File.separator + TEST_DIFFERENT);

        Files.createFile(Paths.get(filePath1));
        String[] constructArgs = new String [] {"-n",TEST_FILE,TEST_DIFFERENT};

        Path file1Path = file1.toPath();
        Path file2Path = file2.toPath();

        assertTrue(Files.exists(file1Path));
        assertFalse(Files.exists(file2Path));
        mvApplication.run(constructArgs, System.in, System.out);

        assertTrue(Files.exists(Paths.get(file2Path.toString())));
        assertTrue(!Files.exists(Paths.get(file1Path.toString())));

        Files.delete(file2Path);

    }

    @Test
    public void runWhenMvFileToFolderWithOverwriteSuccess() throws Exception {

        String currentDir = EnvironmentHelper.currentDirectory.trim();
        String filePath1 = currentDir + File.separator + TEST_FILE;
        String filePath2 = currentDir + File.separator + TEST_FOLDER;
        File file1 = new File(currentDir + File.separator + TEST_FILE);
        File file2 = new File(currentDir + File.separator + TEST_FOLDER);

        Files.createFile(Paths.get(filePath1));
        Files.createDirectory(Paths.get(filePath2));
        String[] constructArgs = new String [] {TEST_FILE,TEST_FOLDER};

        Path file1Path = file1.toPath();
        Path file2Path = file2.toPath();

        assertTrue(Files.exists(file1Path));
        assertTrue(Files.exists(file2Path));
        mvApplication.run(constructArgs, System.in, System.out);

        assertTrue(Files.exists(Paths.get(file2Path.toString())));
        assertTrue(!Files.exists(Paths.get(file1Path.toString())));

        Path fileMoved =  Paths.get(filePath2 + File.separator + TEST_FILE);
        Files.delete(fileMoved);
        Files.delete(file2Path);

    }

    @Test
    public void runWhenMvFileToFolderWithNoOverwriteSuccess() throws Exception {

        String currentDir = EnvironmentHelper.currentDirectory.trim();
        String filePath1 = currentDir + File.separator + TEST_FILE;
        String filePath2 = currentDir + File.separator + TEST_FOLDER;
        File file1 = new File(currentDir + File.separator + TEST_FILE);
        File file2 = new File(currentDir + File.separator + TEST_FOLDER);

        Files.createFile(Paths.get(filePath1));
        Files.createDirectory(Paths.get(filePath2));
        String[] constructArgs = new String [] {"-n",TEST_FILE,TEST_FOLDER};

        Path file1Path = file1.toPath();
        Path file2Path = file2.toPath();

        assertTrue(Files.exists(file1Path));
        assertTrue(Files.exists(file2Path));
        mvApplication.run(constructArgs, System.in, System.out);

        assertTrue(Files.exists(Paths.get(file2Path.toString())));
        assertTrue(!Files.exists(Paths.get(file1Path.toString())));

        Path fileMoved =  Paths.get(filePath2 + File.separator + TEST_FILE);
        Files.delete(fileMoved);
        Files.delete(file2Path);

    }

    @Test
    public void runWhenMvFileToDestFileSWithNoOverwriteException(@TempDir Path tempDir) throws Exception {
        Path file1 = tempDir.resolve(TEST_FILE);
        Path file2 = tempDir.resolve(TEST_DIFFERENT);
        String fileSrcString = file1.toString();
        String fileDestString = file2.toString();

        String[] constructArgs = new String [] {"-n",fileSrcString,fileDestString};;


        Files.createFile(file1);
        Files.createFile(file2);
        assertTrue(Files.exists(file1));
        assertTrue(Files.exists(file2));
        AbstractApplicationException exception = assertThrows(MvException.class, () -> {
            mvApplication.run(constructArgs, System.in, System.out);
        });

        assertEquals(new MvException(NO_OVERWRITE).getMessage(), exception.getMessage());
    }

    @Test
    public void runWhenMvFileToDestFolderWithNoOverwriteException(@TempDir Path tempDir) throws Exception {
        Path file1 = tempDir.resolve(TEST_FILE);
        Path file2 = tempDir.resolve(TEST_FOLDER);
        Path file3 = tempDir.resolve(TEST_FOLDER + File.separator + TEST_FILE);
        String fileSrcString = file1.toString();
        String fileDestString = file2.toString();

        String[] constructArgs = new String [] {"-n",fileSrcString,fileDestString};


        Files.createFile(file1);
        Files.createDirectory(file2);
        Files.createFile(file3);
        assertTrue(Files.exists(file1));
        assertTrue(Files.exists(file2));
        assertTrue(Files.exists(file3));
        AbstractApplicationException exception = assertThrows(MvException.class, () -> {
            mvApplication.run(constructArgs, System.in, System.out);
        });

        assertEquals(new MvException(NO_OVERWRITE).getMessage(), exception.getMessage());
    }

    @Test
    public void mvFileToFolderSuccess(@TempDir Path tempDir) throws Exception {
        Path file1 = tempDir.resolve(TEST_FILE);
        Path file2 = tempDir.resolve(TEST_FOLDER);
        String fileSrcString = file1.toString();
        String destFolderString = file2.toString();

        Files.createFile(file1);
        Files.createDirectory(file2);
        assertTrue(Files.exists(file1));
        assertTrue(Files.exists(file2));

        mvApplication.mvFilesToFolder(destFolderString,fileSrcString);
        String newFileInFolder = destFolderString + File.separator + TEST_FILE;
        assertTrue(Files.exists(Paths.get(newFileInFolder)));
        assertTrue(!Files.exists(Paths.get(TEST_FILE)));
    }

    @Test
    public void mvFileToFolderFolderNotFoundThrowNoDestinationException(@TempDir Path tempDir) throws Exception {
        Path file1 = tempDir.resolve(TEST_FILE);
        Path file2 = tempDir.resolve(TEST_FOLDER);
        String fileSrcString = file1.toString();
        String destFolderString = file2.toString();

        Files.createFile(file1);
        assertTrue(Files.exists(file1));
        assertFalse(Files.exists(file2));

        AbstractApplicationException exception = assertThrows(MvException.class, () -> {
            mvApplication.mvFilesToFolder(destFolderString,fileSrcString);
        });

        assertEquals(new MvException(NO_DESTINATION_FOLDER).getMessage(), exception.getMessage());
    }

    @Test
    public void mvSrcFileToDestFileFileThrowNotFoundException(@TempDir Path tempDir) throws Exception {
        Path file1 = tempDir.resolve(TEST_FILE);
        Path file2 = tempDir.resolve(TEST_DIFFERENT);
        String fileSrcString = file1.toString();
        String destFileString = file2.toString();

        assertFalse(Files.exists(file1));
        assertFalse(Files.exists(file2));

        AbstractApplicationException exception = assertThrows(MvException.class, () -> {
            mvApplication.mvSrcFileToDestFile(fileSrcString,destFileString);
        });

        assertEquals(new MvException(NO_FILE).getMessage(), exception.getMessage());
    }

    @Test
    public void mvSrcFileToDestFileThrowDestinationNullException(@TempDir Path tempDir) throws Exception {
        Path file1 = tempDir.resolve(TEST_FILE);
        String fileSrcString = file1.toString();

        Files.createFile(file1);
        assertTrue(Files.exists(file1));
        AbstractApplicationException exception = assertThrows(MvException.class, () -> {
            mvApplication.mvSrcFileToDestFile(fileSrcString,"");
        });
        assertEquals(new MvException(NO_DESTINATION).getMessage(), exception.getMessage());
    }

    @Test
    public void mvFileToFolderThrowsDestinationNullException(@TempDir Path tempDir) throws Exception {
        Path file1 = tempDir.resolve(TEST_FILE);
        String fileSrcString = file1.toString();

        Files.createFile(file1);
        assertTrue(Files.exists(file1));
        mvApplication.mvFilesToFolder("",fileSrcString);


        File fileInCurrFolder = new File(file1.getFileName().toString());
        assertTrue(fileInCurrFolder.exists());
        fileInCurrFolder.delete();
    }

    @Test
    public void mvFileToFolderThrowsFileNullException(@TempDir Path tempDir) throws Exception {
        Path file1 = tempDir.resolve(TEST_FILE);
        String fileSrcString = file1.toString();

        Files.createFile(file1);
        assertTrue(Files.exists(file1));
        AbstractApplicationException exception = assertThrows(MvException.class, () -> {
            mvApplication.mvSrcFileToDestFile(fileSrcString,"");
        });
        assertEquals(new MvException(NO_DESTINATION).getMessage(), exception.getMessage());
    }

    @Test
    public void mvFileToFolderThrowDestinationNotExistException(@TempDir Path tempDir) throws Exception {
        Path file1 = tempDir.resolve(TEST_FILE);
        String fileSrcString = file1.toString();

        Files.createFile(file1);
        assertTrue(Files.exists(file1));
        AbstractApplicationException exception = assertThrows(MvException.class, () -> {
            mvApplication.mvFilesToFolder(TEST_FOLDER,fileSrcString);
        });
        assertEquals(new MvException(NO_DESTINATION_FOLDER).getMessage(), exception.getMessage());
    }

    // Test mv app move two files into a non existent folder
    @Test
    void runMoveFilesIntoNonExistentDirectoryOverrideThrowNoDestinationException(@TempDir Path tempDir) throws Exception  {
        // mv tempFileA.txt tempFileB.txt invalidDirectory
        Path file1 = tempDir.resolve(TEST_FILE);
        Path file2 = tempDir.resolve(TEST_DIFFERENT);
        String[] args = {file1.toString(), file2.toString() , "nonExistentFolder"};

        Files.createFile(file1);
        assertTrue(Files.exists(file1));
        Files.createFile(file2);
        assertTrue(Files.exists(file2));
        AbstractApplicationException exception = assertThrows(MvException.class, () -> {
            mvApplication.run(args,System.in,System.out);
        });

        assertEquals(new MvException(DESTINATION_FOLDER_NOT).getMessage(), exception.getMessage());

    }

    // Test mv app move same file into same folder
    @Test
    void runMoveSameFilesIntoNonExistentDirectoryOverrideThrowSameSourceDestException(@TempDir Path tempDir) throws Exception  {
        // mv tempFileA.txt tempFileB.txt tempFileA.txt
        Path file1 = tempDir.resolve(TEST_FILE);
        Path folder2 = tempDir.resolve(TEST_FOLDER);
        String[] args = { folder2.toString() ,file1.toString(), folder2.toString()};

        Files.createFile(file1);
        assertTrue(Files.exists(file1));
        Files.createDirectory(folder2);
        assertTrue(Files.exists(folder2));
        AbstractApplicationException exception = assertThrows(MvException.class, () -> {
            mvApplication.run(args,System.in,System.out);
        });

        assertEquals(new MvException(SRC_DEST_SAME).getMessage(), exception.getMessage());

    }

    // Test mv app to move two files into a file that is not a folder
    @Test
    void runMoveFilesIntoInvalidDirectoryOverrideThrowNoDestFolderException(@TempDir Path tempDir) throws Exception  {
        // mv tempFileA.txt tempFileB.txt tempFileA.txt
        Path file1 = tempDir.resolve(TEST_FILE);
        Path file2 = tempDir.resolve(TEST_DIFFERENT);
        Path folder2 = tempDir.resolve(TEST_FOLDER);
        String[] args = { file1.toString(), file2.toString(), folder2.toString()};

        Files.createFile(file1);
        assertTrue(Files.exists(file1));
        Files.createFile(file2);
        assertTrue(Files.exists(file2));
        assertFalse(Files.exists(folder2));
        AbstractApplicationException exception = assertThrows(MvException.class, () -> {
            mvApplication.run(args,System.in,System.out);
        });

        assertEquals(new MvException(DESTINATION_FOLDER_NOT).getMessage(), exception.getMessage());
    }


}
