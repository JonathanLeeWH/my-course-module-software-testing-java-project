package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.MvException;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import sg.edu.nus.comp.cs4218.impl.app.MvApplication;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;

import static org.junit.jupiter.api.Assertions.*;
public class MvApplicationTest {

    private static final String TEST_FILE = "testFile";
    private static final String TEST_SAME_NAME = "testFile";
    private static final String TEST_DIFFERENT = "testFile2";
    private static final String TEXT_NO_IMPUT = "";
    private static final String TEST_FOLDER = "testFolder";
    private static MvApplication mvApplication;
    private static final String NO_ARG_EXCEPTION ="No input found, please specify file to be moved";
    private static final String FAILED_TO_MOVE = "Failed to move file";


    @BeforeEach
    void setupBeforeTest() throws IOException {
        mvApplication = new MvApplication();
    }


    @Test
    public void executeNoArgSpecifiedThrowsArgException() {

        AbstractApplicationException exception = assertThrows(MvException.class, () -> {
            mvApplication.run(null, null, null); // stdin and stdout is not used in MvApplication
        });

        assertEquals(new MvException(NO_ARG_EXCEPTION).getMessage(), exception.getMessage());
    }

    @Test
    public void executeMvFileToDestFileSuccess(@TempDir Path tempDir) throws Exception {
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
    public void executeMvFileToFolderSuccess(@TempDir Path tempDir) throws Exception {
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
    public void executeMvFileToFolderFolderNotFound(@TempDir Path tempDir) throws Exception {
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
    public void executeMvFileToDestFileFileNotFound(@TempDir Path tempDir) throws Exception {
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
    public void executeMvFileToDestFileDestinationNull(@TempDir Path tempDir) throws Exception {
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
    public void executeMvFileToDestFolderDestinationNull(@TempDir Path tempDir) throws Exception {
        Path file1 = tempDir.resolve(TEST_FILE);
        String fileSrcString = file1.toString();

        Files.createFile(file1);
        assertTrue(Files.exists(file1));
        AbstractApplicationException exception = assertThrows(MvException.class, () -> {
            mvApplication.mvFilesToFolder("",fileSrcString);
        });
        assertEquals(new MvException(FAILED_TO_MOVE).getMessage(), exception.getMessage());
    }

    @Test
    public void executeMvFileToDestFolderNotExist(@TempDir Path tempDir) throws Exception {
        Path file1 = tempDir.resolve(TEST_FILE);
        String fileSrcString = file1.toString();

        Files.createFile(file1);
        assertTrue(Files.exists(file1));
        AbstractApplicationException exception = assertThrows(MvException.class, () -> {
            mvApplication.mvFilesToFolder(TEST_FOLDER,fileSrcString);
        });
        assertEquals(new MvException(NO_DESTINATION_FOLDER).getMessage(), exception.getMessage());
    }

}
