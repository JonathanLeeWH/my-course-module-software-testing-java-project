package sg.edu.nus.comp.cs4218.impl.cmd;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;
import sg.edu.nus.comp.cs4218.EnvironmentHelper;
import sg.edu.nus.comp.cs4218.impl.FileIOHelper;
import sg.edu.nus.comp.cs4218.impl.StringsArgListHelper;
import sg.edu.nus.comp.cs4218.impl.util.ApplicationRunner;
import sg.edu.nus.comp.cs4218.impl.util.ArgumentResolver;

import java.io.*;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class QuotingIT {

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
    private static final String FILE_2_CONTENT = "This is the content for file 1."
            + System.lineSeparator() + "There are 'some content here."
            + System.lineSeparator() + "Some numbers: 50 1 2."
            + System.lineSeparator() + "Some whitespace    ><*&^%.?";

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
        outputStream = new ByteArrayOutputStream();
    }

    /**
     * Tests evaluate method quoting and echo interaction
     * For example: echo "hi
     * Expected: Outputs correctly
     */
    @Test
    void testEvaluateEchoCommandWithNoQuotesInteractionShouldOutputCorrectly(@TempDir Path tempDir) throws Exception {

        List<String> argList = StringsArgListHelper.concantenateStringsToList(ECHO_APP , "hi");
        callCommand = new CallCommand(argList , new ApplicationRunner(), new ArgumentResolver());
        callCommand.evaluate(System.in, outputStream);
        assertEquals("hi" + System.lineSeparator(), outputStream.toString());
    }

    /**
     * Tests evaluate method quoting and echo interaction
     * For example: echo 'hi
     * Expected: Outputs correctly
     */
    @Test
    void testEvaluateEchoCommandWithSingleQuoteInteractionShouldOutputCorrectly(@TempDir Path tempDir) throws Exception {

        List<String> argList = StringsArgListHelper.concantenateStringsToList(ECHO_APP , "\'hi");
        callCommand = new CallCommand(argList , new ApplicationRunner(), new ArgumentResolver());
        callCommand.evaluate(System.in, outputStream);
        assertEquals("hi" + System.lineSeparator(), outputStream.toString());
    }

    /**
     * Tests evaluate method quoting and echo interaction
     * For example: echo "hello world"
     * Expected: Outputs correctly
     */
    @Test
    void testEvaluateEchoCommandWithDoubleQuoteInteractionShouldOutputCorrectly(@TempDir Path tempDir) throws Exception {

        List<String> argList = StringsArgListHelper.concantenateStringsToList(ECHO_APP , "\"hello world\"");
        callCommand = new CallCommand(argList , new ApplicationRunner(), new ArgumentResolver());
        callCommand.evaluate(System.in, outputStream);
        assertEquals("hello world" + System.lineSeparator(), outputStream.toString());
    }

    /**
     * Tests evaluate method quoting and echo interaction
     * For example: echo '"hello world yes"
     * Expected: Output Correctly
     */
    @Test
    void testEvaluateEchoCommandWithNoQuoteInteractionShouldOutputCorrectly(@TempDir Path tempDir) throws Exception {

        List<String> argList = StringsArgListHelper.concantenateStringsToList(ECHO_APP , "hello world yes");
        callCommand = new CallCommand(argList , new ApplicationRunner(), new ArgumentResolver());
        callCommand.evaluate(System.in, outputStream);
        assertEquals("hello world yes" + System.lineSeparator(), outputStream.toString());
    }

    /**
     * Tests evaluate method quoting and echo interaction
     * For example: echo '"This is space `echo " "`
     * Expected: Echo correctly
     */
    @Test
    void testEvaluateEchoCommandWithMultipleSingleDoubleQuoteInteractionShouldOutputCorrectly(@TempDir Path tempDir) throws Exception {

        List<String> argList = StringsArgListHelper.concantenateStringsToList(ECHO_APP , "'\"This is space `echo \" \"`");
        callCommand = new CallCommand(argList , new ApplicationRunner(), new ArgumentResolver());
        callCommand.evaluate(System.in, outputStream);
        assertEquals("\"This is space `echo \" \"`" + System.lineSeparator(), outputStream.toString());
    }

    /**
     * Tests evaluate method quoting and commandSub interaction
     * For example: echo `echo "‘quote is not interpreted as special character’"`
     * Expected: Echo correctly
     */
    @Test
    void testEvaluateEchoCommandWithCommandSubQuoteInteractionShouldOutputCorrectly(@TempDir Path tempDir) throws Exception {

        List<String> argList = StringsArgListHelper.concantenateStringsToList(ECHO_APP , "`echo \"‘quote is not interpreted as special character’\"`");
        callCommand = new CallCommand(argList , new ApplicationRunner(), new ArgumentResolver());
        callCommand.evaluate(System.in, outputStream);
        assertEquals("‘quote is not interpreted as special character’" + System.lineSeparator(), outputStream.toString());
    }

    /**
     * Tests evaluate method quoting and grep interaction
     * For example: grep "'some content" TestFile2.txt
     * Expected: Echo correctly
     */
    @Test
    void testEvaluateGrepCommandWithCommandSubQuoteInteractionShouldOutputCorrectly() throws Exception {
        BufferedWriter writer1 = new BufferedWriter(new PrintWriter(FILENAME2));
        writer1.write(FILE_2_CONTENT);
        writer1.flush();
        writer1.close();

        List<String> argList = StringsArgListHelper.concantenateStringsToList(GREP_APP , "\"'some content\"", FILENAME2);
        callCommand = new CallCommand(argList , new ApplicationRunner(), new ArgumentResolver());
        callCommand.evaluate(System.in, outputStream);
        assertEquals("There are 'some content here." + System.lineSeparator(), outputStream.toString());
    }
}
