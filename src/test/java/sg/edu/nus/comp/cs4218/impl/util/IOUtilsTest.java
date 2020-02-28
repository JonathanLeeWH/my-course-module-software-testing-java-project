package sg.edu.nus.comp.cs4218.impl.util;

import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;
import sg.edu.nus.comp.cs4218.EnvironmentHelper;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.FileIOHelper;


public class IOUtilsTest {

    private static InputStream mockInputStream;
    private static OutputStream mockOutputStream;

    private static final String NULL_ARGS_EXC = "shell: " + ERR_NULL_ARGS;
    private static final String FILE_NOT_FOUND = "shell: " +  ERR_FILE_NOT_FOUND;

    private static final String FOLDER_NAME = "testFolder";
    private static final String INPUT_FILE = "input.txt";
    private static final String OUTPUT_FILE = "output.txt";
    private static final String CURR_DIR = EnvironmentHelper.currentDirectory;

    private static List<String> createdFiles;

    @BeforeAll
    static void setUp() throws IOException {
        String folderPath = CURR_DIR + File.separator + FOLDER_NAME;
        String inputFilePath = folderPath + File.separator + INPUT_FILE;
        String outputFilePath = OUTPUT_FILE;

        createdFiles = new ArrayList<>();
        createdFiles.add(inputFilePath);
        createdFiles.add(outputFilePath);
        createdFiles.add(folderPath);

        FileIOHelper.createFileFolder(folderPath, true);
        FileIOHelper.createFileFolder(inputFilePath, false);
        FileIOHelper.createFileFolder(outputFilePath, false);
    }

    @BeforeEach
    void setUpEach() {
        mockInputStream = Mockito.mock(InputStream.class);
        mockOutputStream = Mockito.mock(OutputStream.class);
    }

    @AfterEach
    void tearDownEach() throws IOException {
        if (mockInputStream != null) {
            mockInputStream.close();
            mockInputStream = null;
        }
        if (mockOutputStream != null) {
            mockOutputStream.close();
            mockOutputStream = null;
        }
    }

    @AfterAll
    static void tearDown() {
        for (String filePath : createdFiles) {
            FileIOHelper.deleteFiles(filePath);
        }
        createdFiles.clear();
    }

    @Test
    void testOpenInputStreamEmptyFilenameThrowFileNotFoundException() {
        String filename = "";
        Exception actualException = assertThrows(ShellException.class, () -> IOUtils.openInputStream(filename));
        assertEquals(FILE_NOT_FOUND,
                actualException.getMessage());
    }
    
    @Test
    void testOpenInputStreamNullFilenameThrowNullArgsException() {
        String filename = null;
        Exception actualException = assertThrows(ShellException.class, () -> IOUtils.openInputStream(filename));
        assertEquals(NULL_ARGS_EXC, actualException.getMessage());
    }

    @Test
    void testOpenInputStreamValidFilenameSuccess() throws ShellException {
        String filename = CURR_DIR + File.separator + FOLDER_NAME + File.separator + INPUT_FILE;

        mockInputStream = IOUtils.openInputStream(filename);
        assertNotNull(mockInputStream);
    }

    @Test
    void testOpenOutputStreamEmptyFilenameThrowFileNotFoundException() {
        String filename = "";
        Exception actualException = assertThrows(ShellException.class, () -> IOUtils.openOutputStream(filename));
        assertEquals(FILE_NOT_FOUND, actualException.getMessage());
    }

    @Test
    void testOpenOutputStreamNullFilenameThrowNullArgException() {
        String filename = null;

        Exception actualException = assertThrows(ShellException.class, () -> IOUtils.openOutputStream(filename));
        assertEquals(NULL_ARGS_EXC, actualException.getMessage());
    }

    @Test
    void testOpenOutputStreamValidFilenameSuccess() throws ShellException {
        String filename = CURR_DIR + File.separator + OUTPUT_FILE;

        mockOutputStream = IOUtils.openOutputStream(filename);
        assertNotNull(mockOutputStream);
    }

    @Test
    void testResolveFilePathInCurrentDirectorySuccess() {
        String filePathToResolve = FOLDER_NAME + File.separator + INPUT_FILE;
        File file = new File(CURR_DIR + File.separator + FOLDER_NAME + File.separator + INPUT_FILE);
        String expectedPath = file.getPath();
        String actualPath = IOUtils.resolveFilePath(filePathToResolve).toString();

        assertEquals(expectedPath, actualPath);
    }

    @Test
    void testResolveFilePathNotInCurrentDirectorySuccesss() {
        String filePath = "invalidFolder" + File.separator + "test.txt";
        String expectedPath = File.separator + filePath;
        String actualPath = IOUtils.resolveFilePath(File.separator + filePath).toString();

        assertEquals(expectedPath, actualPath);
    }

}
