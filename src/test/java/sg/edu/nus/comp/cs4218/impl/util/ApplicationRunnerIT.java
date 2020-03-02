package sg.edu.nus.comp.cs4218.impl.util;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import sg.edu.nus.comp.cs4218.EnvironmentHelper;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ExitException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.FileIOHelper;
import sg.edu.nus.comp.cs4218.impl.cmd.CallCommand;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;

public class ApplicationRunnerIT {

    private static final String MOCK_ROOT_DIR = "ROOT";
    private static final String MOCK_FILE_NAME = "File1.txt";
    private static final String MOCK_FOLDER = "Folder1";
    private static final String MOCK_ROOT_FILE1 = MOCK_ROOT_DIR + File.separator + MOCK_FILE_NAME;
    private static final String MOCK_ROOT_FOLDER1 = MOCK_ROOT_DIR + File.separator + MOCK_FOLDER;
    private static final String LS_OUTPUT = MOCK_FILE_NAME + System.lineSeparator() + "" + MOCK_FOLDER;

    private static final String ECHO_COMMAND = "echo";

    private static final String OUTPUT_FILE_1 = "outputFile1.txt";
    private static final String OUTPUT_FILE_2 = "outputFile2.txt";

    private static final String FILE_1_CONTENT = "This is the content for file 1."
            + System.lineSeparator() + "There are some content here."
            + System.lineSeparator() + "Some numbers: 50 1 2."
            + System.lineSeparator() + "Some whitespace      ?><*&^%.";
    private static final String F1_CONTENT_SED = "helloThis is the content for file 1."
            + System.lineSeparator() + "helloThere are some content here."
            + System.lineSeparator() + "helloSome numbers: 50 1 2."
            + System.lineSeparator() + "helloSome whitespace    ><*&^%.?";
    private static final String F1_CONTENT_SORT = "Some numbers: 50 1 2." + System.lineSeparator() +
            "Some whitespace    ><*&^%.?" + System.lineSeparator() +
            "There are some content here." + System.lineSeparator() +
            "This is the content for file 1.";

    private static final String SPACE = " ";
    private static final String FILE_NOT_EXIST = "testFileNotExist.txt";
    private static final String FILENAME1 = "testFile1.txt";
    private static final String FILENAME2 = "testFile2.txt";
    private static final String FILENAME3 = "testFile3.txt";
    private static final String FOLDER1 = "testFolder1";

    private static final String EMPTY_STRING = "";
    public static final String ECHO_TEST = "Echo test.";

    private static ApplicationRunner appRunner;
    private static FileInputStream fileInputStream;
    private static FileOutputStream fileOutputStream;
    private static CallCommand callCommand;
    private static ArgumentResolver argumemtResovler = new ArgumentResolver();

    @SuppressWarnings("PMD.CloseResource")
    @BeforeAll
    static void setUp() throws IOException {
        appRunner = new ApplicationRunner();

        File rootDirectory = new File(MOCK_ROOT_DIR);
        rootDirectory.mkdir();
        File mockFile = new File(MOCK_ROOT_FILE1);
        mockFile.createNewFile();
        File mockDirectory = new File(MOCK_ROOT_FOLDER1);
        mockDirectory.mkdir();

        BufferedWriter writer1 = new BufferedWriter(new PrintWriter(FILENAME1));
        writer1.write(FILE_1_CONTENT);
        writer1.flush();
        writer1.close();

        File file = new File(FOLDER1);
        file.mkdir();
    }

    @AfterAll
    static void tearDown() {
        FileIOHelper.deleteFiles(FILENAME1, FILENAME2, FILENAME3, MOCK_ROOT_FILE1,
                MOCK_ROOT_FOLDER1, MOCK_ROOT_DIR, FOLDER1);
    }

    @AfterEach
    void tearDownAfterEach() {
        FileIOHelper.deleteFiles(MOCK_ROOT_DIR + File.separator + OUTPUT_FILE_1, OUTPUT_FILE_1,
                OUTPUT_FILE_2);
    }

    /**
     * Tests runApp method when input app is rm, execute RmApplication.
     * For example: rm 1.txt
     * Where 1.txt exists.
     * Expected: 1.txt is removed.
     */
    @Test
    void testRunAppWhenInputRmAppShouldExecuteRmApplication(@TempDir Path tempDir) throws AbstractApplicationException, ShellException, IOException {
        Path file1 = tempDir.resolve("1.txt");
        String[] argsList = {file1.toString()};
        Files.createFile(file1);
        assertTrue(Files.exists(file1)); // check 1.txt exists.
        appRunner.runApp("rm", argsList, mock(InputStream.class), mock(OutputStream.class));
        assertFalse(Files.exists(file1));
    }

    /**
     * Tests runApp method when input app is exit, execute ExitApplication.
     * For example: exit
     * Expected: Throws ExitException with exit code 0.
     */
    @Test
    void testRunAppWhenInputExitAppShouldExecuteExitApplication() {
        ExitException exception = assertThrows(ExitException.class, () -> {
            appRunner.runApp("exit", null, mock(InputStream.class), mock(OutputStream.class));
        });
        assertEquals(new ExitException("0").getMessage(), exception.getMessage());
    }

    /**
     * Tests runApp method when input app is echo, execute EchoApplication.
     * For example: echo hello world
     * Expected: Outputstream should contain hello world.
     */
    @Test
    void testRunAppWhenInputEchoAppShouldExecuteEchoApplication() throws AbstractApplicationException, ShellException {
        String[] argsList = {"hello", "world"};
        OutputStream outputStream = new ByteArrayOutputStream();
        appRunner.runApp("echo", argsList, mock(InputStream.class), outputStream);
        assertEquals("hello world" + STRING_NEWLINE, outputStream.toString());
    }

    /**
     * Tests runApp method when input app is cd, execute CdApplication.
     * For example: cd ..
     * Expected: Sets EnvironmentHelper.currentDirectory to the full absolute path (converted from the non absolute/relative path.
     * In this case, the current directory is changed to the parent folder path of the present working directory.
     */
    @Test
    void testRunAppWhenInputCdAppShouldExecuteCdApplication() throws AbstractApplicationException, ShellException {
        EnvironmentHelper.currentDirectory = System.getProperty("user.dir");
        String[] argsList = {".."};
        String parentAbsPath = Paths.get(EnvironmentHelper.currentDirectory).getParent().toString();

        assertFalse(new File(argsList[0]).toPath().isAbsolute());

        appRunner.runApp("cd", argsList, mock(InputStream.class), mock(OutputStream.class));
        String newPath = EnvironmentHelper.currentDirectory;

        assertEquals(parentAbsPath, newPath);
        EnvironmentHelper.currentDirectory = System.getProperty("user.dir"); // reset environment directory to default
    }

    /**
     * Tests runApp method when input app is ls, execute lsApplication.
     * For example: ls
     * Expected: List files in current directory set by the test folders
     */
    @Test
    void runAppWhenInputLsAppShouldExecuteLsApplication() throws Exception {
        String[] args = {""};
        String currentDirectory = EnvironmentHelper.currentDirectory;
        EnvironmentHelper.currentDirectory = EnvironmentHelper.currentDirectory + File.separator + MOCK_ROOT_DIR;

        fileOutputStream = new FileOutputStream(OUTPUT_FILE_1);
        appRunner.runApp("ls", args, null, fileOutputStream);

        String expectedOutput = LS_OUTPUT;
        String actualOutput = FileIOHelper.extractAndConcatenate(OUTPUT_FILE_1);
        assertEquals(expectedOutput, actualOutput);
        EnvironmentHelper.currentDirectory = System.getProperty("user.dir");
    }

    /**
     * Tests runApp method when input app is ls, execute lsApplication.
     * For example: Find
     * Expected: Find file that exist in current directory set by the test folders
     */
    @Test
    void runAppWhenInputFindAppShouldExecuteFindApplication() throws Exception {
        String[] args = {".",
                "-name", FILENAME1 };

        fileOutputStream = new FileOutputStream(OUTPUT_FILE_1);
        appRunner.runApp("find", args, null, fileOutputStream);

        String expectedOutput = "." + File.separator +FILENAME1;
        String actualOutput = FileIOHelper.extractAndConcatenate(OUTPUT_FILE_1);
        assertEquals(expectedOutput, actualOutput);
    }

    /**
     * Tests runApp method when input app is ls, execute lsApplication.
     * For example: Mv
     * Expected: Rename file that exist in current directory set by the test folders from File1 to file 2
     */
    @Test
    void runAppWhenInputMvAppShouldExecuteMvApplication() throws Exception {
        String[] args = { FILENAME1 , FILENAME2 };
        File file1 = new File(FILENAME1);
        File file2 = new File(FILENAME2);

        fileOutputStream = new FileOutputStream(OUTPUT_FILE_1);

        Path file1Path = file1.toPath();
        Path file2Path = file2.toPath();
        assertTrue(Files.exists(file1Path));
//      assertFalse(Files.exists(file2Path));

        appRunner.runApp("mv", args, null, fileOutputStream);

        assertTrue(!Files.exists(file1Path));
        assertTrue(Files.exists(file2Path));

        //rename
        String[] args2 = { FILENAME2 , FILENAME1 };
        appRunner.runApp("mv", args2, null, fileOutputStream);
    }
}
