package sg.edu.nus.comp.cs4218.impl;

import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import sg.edu.nus.comp.cs4218.EnvironmentHelper;
import sg.edu.nus.comp.cs4218.Shell;
import sg.edu.nus.comp.cs4218.impl.cmd.CallCommand;
import sg.edu.nus.comp.cs4218.impl.util.ApplicationRunner;
import sg.edu.nus.comp.cs4218.impl.util.ArgumentResolver;
import sg.edu.nus.comp.cs4218.impl.util.TestFileUtils;

import static org.junit.jupiter.api.Assertions.*;

import java.io.*;

public class ShellSystemTest {
    private static final String FILE_NAME_1 = "CS4218A.txt";
    private static final String FILE_NAME_2 = "A4218A.txt";
    private static final String FILE_NAME_3 = "CS3203A.txt";

    private static final String WC_APP = "wc";
    private static final String ECHO_APP = "echo";
    private static final String GREP_APP = "grep";
    private static final String CUT_APP = "cut";
    private static final String SORT_APP = "sort";
    private static final String FIND_APP = "find";
    private static final String LS_APP = "ls";
    private static final String PASTE_APP = "paste";

    private static final String MOCK_ROOT_DIR = "ROOT";
    private static final String MOCK_FILE_NAME = "File1.txt";
    private static final String MOCK_FOLDER = "Folder1";
    private static final String MOCK_ROOT_FILE1 = MOCK_ROOT_DIR + File.separator + MOCK_FILE_NAME;
    private static final String MOCK_ROOT_FOLDER1 = MOCK_ROOT_DIR + File.separator + MOCK_FOLDER;
    private static final String LS_OUTPUT = MOCK_FILE_NAME + System.lineSeparator() + "" + MOCK_FOLDER;

    private static final String OUTPUT_FILE_1 = "outputFile1.txt";
    private static final String OUTPUT_FILE_2 = "outputFile2.txt";

    private static final String FILE_1_CONTENT = "This is the content for file 1."
            + System.lineSeparator() + "There are some content here."
            + System.lineSeparator() + "Some numbers: 50 1 2."
            + System.lineSeparator() + "Some whitespace    ><*&^%.?";
    private static final String F1_CONTENT_SED = "helloThis is the content for file 1."
            + System.lineSeparator() + "helloThere are some content here."
            + System.lineSeparator() + "helloSome numbers: 50 1 2."
            + System.lineSeparator() + "helloSome whitespace    ><*&^%.?";
    private static final String F1_CONTENT_SORT = "Some numbers: 50 1 2." + System.lineSeparator() +
            "Some whitespace    ><*&^%.?" + System.lineSeparator() +
            "There are some content here." + System.lineSeparator() +
            "This is the content for file 1.";
    private static final String F1_CONTENT_CUT = "i"
            + System.lineSeparator() + "e" +
            System.lineSeparator() + "m" +
            System.lineSeparator() + "m" + System.lineSeparator();

    private static final String INPUT_REDIR_CHAR = "<";
    private static final String OUTPUT_REDIR_CHAR = ">";
    private static final String FILE_NOT_EXIST = "testFileNotExist.txt";
    private static final String FILENAME1 = "TestFile1.txt";
    private static final String FILENAME2 = "TestFile2.txt";
    private static final String FILENAME3 = "TestFile3.txt";
    private static final String FOLDER1 = "TestFolder1";
    private static final String SPACE = " ";

    private static final String EMPTY_STRING = "";

    private static ApplicationRunner appRunner;
    private static InputStream inputStream;
    private static ByteArrayOutputStream outputStream;
    private static CallCommand callCommand;
    private static ArgumentResolver argumentResolver = new ArgumentResolver();
    private static String currDir;
    private static Shell shell;

    @BeforeAll
    static void setUp() throws IOException {

        appRunner = new ApplicationRunner();
        inputStream = Mockito.mock(InputStream.class);

        currDir = EnvironmentHelper.currentDirectory;
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
    static void tearDown() throws IOException {
        inputStream.close();
        outputStream.close();
        FileIOHelper.deleteTestFiles(FILENAME1, FILENAME2, FILENAME3, MOCK_ROOT_FILE1,
                MOCK_ROOT_FOLDER1, MOCK_ROOT_DIR, FOLDER1);
    }

    @AfterEach
    void tearDownAfterEach() throws IOException {
        inputStream.close();
        outputStream.close();
        FileIOHelper.deleteTestFiles(MOCK_ROOT_DIR + File.separator + OUTPUT_FILE_1, OUTPUT_FILE_1,
                OUTPUT_FILE_2,FILENAME2, FILENAME3);
        EnvironmentHelper.currentDirectory = currDir;
    }

    @BeforeEach
    void setUpBeforeEach() {
        shell = new ShellImpl();
        outputStream = new ByteArrayOutputStream();
    }

    @Test
    void testParseAndEvaluateUsingComplexLsAndEchoCommandWithSemiColonShouldRunSuccessfully() throws Exception {
        String input = "ls ROOT;echo hello";

        shell.parseAndEvaluate(input,outputStream);
        assertEquals(MOCK_FILE_NAME + System.lineSeparator()
                + MOCK_FOLDER + System.lineSeparator() +"hello" + System.lineSeparator(), outputStream.toString());
    }

    @Test
    void testParseAndEvaluateUsingComplexVariousCommandsWithSemiColonAndPipeAndBackQuoteShouldRunSuccessfully() throws Exception {
        String input = "cd " + TestFileUtils.TESTDATA_DIR + "; paste test1.txt | grep \"CS4218\" | cut -c 1-7";
        shell.parseAndEvaluate(input,outputStream);
        String expectedOutput = "CS4218:" + System.lineSeparator();
        assertEquals(expectedOutput, outputStream.toString());
    }

    @Test
    void testParseAndEvaluateUsingComplexVariousCommandsWithIORedirOperatorsAndGlobbingAndBackQuoteShouldRunSuccessfully() throws Exception {
        String input = "paste " + TestFileUtils.TESTDATA_DIR + "test1.txt `paste < " + TestFileUtils.TESTDATA_DIR +
                "test*Ab*` > " + TestFileUtils.TESTDATA_DIR + "testOut.txt";
        shell.parseAndEvaluate(input,outputStream);
        String expectedOutput = "Operation Done." + System.lineSeparator();
        assertEquals(expectedOutput, outputStream.toString());
    }

    @Test
    void testParseAndEvaluateUsingComplexVariousCommandsWithIORedirOperatorsAndPipeOperatorAndGlobbingAndSemiColonAndSingleQuoteAndBackQuoteShouldRunSuccessfully() throws Exception {
        String input = "echo < " + TestFileUtils.TESTDATA_DIR + "test1.txt > " + TestFileUtils.TESTDATA_DIR +
                "testOutput.txt | grep \"CS4218\" | rm " + TestFileUtils.TESTDATA_DIR +
                "testOutput.txt; echo `echo \'Operation Done.\'`";
        shell.parseAndEvaluate(input,outputStream);
        String expectedOutput = "Operation Done." + System.lineSeparator();
        assertEquals(expectedOutput, outputStream.toString());
    }
}
