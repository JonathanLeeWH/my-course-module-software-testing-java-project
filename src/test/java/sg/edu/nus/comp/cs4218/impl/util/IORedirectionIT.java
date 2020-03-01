package sg.edu.nus.comp.cs4218.impl.util;

import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import sg.edu.nus.comp.cs4218.EnvironmentHelper;
import sg.edu.nus.comp.cs4218.exception.ExitException;
import sg.edu.nus.comp.cs4218.exception.MvException;
import sg.edu.nus.comp.cs4218.exception.RmException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.FileIOHelper;
import sg.edu.nus.comp.cs4218.impl.StringsArgListHelper;
import sg.edu.nus.comp.cs4218.impl.cmd.CallCommand;

import java.io.*;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("PMD.CloseResource")
public class IORedirectionIT {

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
            + System.lineSeparator() + "helloSome whitespace      ?><*&^%.";
    private static final String F1_CONTENT_SORT = "Some numbers: 50 1 2." + System.lineSeparator() +
            "Some whitespace      ?><*&^%." + System.lineSeparator() +
            "There are some content here." + System.lineSeparator() +
            "This is the content for file 1.";
    private static final String F1_CONTENT_CUT = "i"
            + System.lineSeparator() + "e" +
            System.lineSeparator() + "m" +
            System.lineSeparator() + "m";

    private static final String INPUT_REDIR_CHAR = "<";
    private static final String OUTPUT_REDIR_CHAR = ">";
    private static final String SPACE = " ";
    private static final String FILE_NOT_EXIST = "testFileNotExist.txt";
    private static final String FILENAME1 = "testFile1.txt";
    private static final String FILENAME2 = "testFile2.txt";
    private static final String FILENAME3 = "testFile3.txt";
    private static final String FOLDER1 = "testFolder1";

    private static final String EMPTY_STRING = "";

    private static ApplicationRunner appRunner;
    private static InputStream mockInputStream;
    private static ByteArrayOutputStream mockBos;
    private static CallCommand callCommand;
    private static ArgumentResolver argumemtResovler = new ArgumentResolver();

    @BeforeAll
    static void setUp() throws IOException {
        appRunner = new ApplicationRunner();
        mockInputStream = Mockito.mock(InputStream.class);

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

    @BeforeEach
    void setUpBeforeEach() {
        mockBos = new ByteArrayOutputStream();
    }

    /*******************************************************************
     Tests for input redirection only
     ******************************************************************/

    @Test
    void inputRedirectionArgumentNotGivenTest() {
        List<String> argsList = StringsArgListHelper.concantenateStringsToList("ls", INPUT_REDIR_CHAR);
        callCommand = new CallCommand(argsList, appRunner, argumemtResovler);
        assertThrows(NoSuchElementException.class, () -> callCommand.evaluate(mockInputStream, mockBos));
    }

    @Test
    void fileNotExistForInputRedirectionTest() {
        List<String> argsList = StringsArgListHelper.concantenateStringsToList("ls", INPUT_REDIR_CHAR,
                FILE_NOT_EXIST);
        callCommand = new CallCommand(argsList, appRunner, argumemtResovler);
        assertThrows(ShellException.class, () -> callCommand.evaluate(mockInputStream, mockBos));
    }

    @Test
    void folderForInputRedirectionTest() {
        List<String> argsList = StringsArgListHelper.concantenateStringsToList("ls", INPUT_REDIR_CHAR,
                FOLDER1);
        callCommand = new CallCommand(argsList, appRunner, argumemtResovler);
        assertThrows(ShellException.class, () -> callCommand.evaluate(mockInputStream, mockBos));
    }

    @Test
    void commandNotExistForInputRedirectionTest() {
        List<String> argsList = StringsArgListHelper.concantenateStringsToList("notExist", INPUT_REDIR_CHAR,
                FILENAME1);
        callCommand = new CallCommand(argsList, appRunner, argumemtResovler);
        assertThrows(ShellException.class, () -> callCommand.evaluate(mockInputStream, mockBos));
    }
//
//    @Test
//    void inputRedirectionSedApplicationIntegrationTest() throws Exception {
//        List<String> argsList = StringsArgListHelper.concantenateStringsToList("sed", "s/^/hello/",
//                INPUT_REDIR_CHAR, FILENAME1);
//        callCommand = new CallCommand(argsList, appRunner, argumemtResovler);
//        callCommand.evaluate(mockInputStream, mockBos);
//
//        String expectedOutput = F1_CONTENT_SED + System.lineSeparator();
//        assertEquals(expectedOutput, mockBos.toString());
//    }

    @Test
    void inputRedirectionSortApplicationIntegrationTest() throws Exception {
        List<String> argsList = StringsArgListHelper.concantenateStringsToList("sort", INPUT_REDIR_CHAR, FILENAME1);
        callCommand = new CallCommand(argsList, appRunner, argumemtResovler);
        callCommand.evaluate(mockInputStream, mockBos);

        String expectedOutput = F1_CONTENT_SORT + System.lineSeparator();
        assertEquals(expectedOutput, mockBos.toString());
    }


    @Test
    void inputRedirectionEchoApplicationIntegrationTest() throws Exception {
        // Ignores the input redirection and just output newline according to Terminal
        List<String> argsList = StringsArgListHelper.concantenateStringsToList(ECHO_COMMAND, INPUT_REDIR_CHAR, FILENAME1);
        callCommand = new CallCommand(argsList, appRunner, argumemtResovler);
        callCommand.evaluate(mockInputStream, mockBos);

        String expectedOutput = System.lineSeparator();
        assertEquals(expectedOutput, mockBos.toString());
    }

    @Test
    void inputRedirectionExitApplicationIntegrationTest() {
        // Ignores the input redirection and just exit
        List<String> argsList = StringsArgListHelper.concantenateStringsToList("exit", INPUT_REDIR_CHAR, FILENAME1);
        callCommand = new CallCommand(argsList, appRunner, argumemtResovler);

        Exception actualException = assertThrows(ExitException.class, () -> callCommand.evaluate(mockInputStream, mockBos));
    }

    @Test
    void inputRedirectionRmApplicationIntegrationTest() throws Exception{
        // Ignores the input redirection and just exit cause Rm dont take in
        List<String> argsList = StringsArgListHelper.concantenateStringsToList("rm", INPUT_REDIR_CHAR, FILENAME2);
        File file = new File(FILENAME2);
        file.createNewFile();
        callCommand = new CallCommand(argsList, appRunner, argumemtResovler);

        Exception actualException = assertThrows(RmException.class, () -> callCommand.evaluate(mockInputStream, mockBos));
    }

    @Test
    void inputRedirectionMvApplicationIntegrationTest() throws Exception{
        // Ignores the input redirection and just exit
        List<String> argsList = StringsArgListHelper.concantenateStringsToList("mv", INPUT_REDIR_CHAR, FILENAME2);
        File file = new File(FILENAME2);
        file.createNewFile();
        callCommand = new CallCommand(argsList, appRunner, argumemtResovler);

        Exception actualException = assertThrows(MvException.class, () -> callCommand.evaluate(mockInputStream, mockBos));
    }

    @Test
    void inputRedirectionLsApplicationIntegrationTest() throws Exception {
        // Ignores the input redirection and just output the files and folder
        String currentDirectory = EnvironmentHelper.currentDirectory;
        EnvironmentHelper.currentDirectory = EnvironmentHelper.currentDirectory + File.separator + MOCK_ROOT_DIR;


        List<String> argsList = StringsArgListHelper.concantenateStringsToList("ls", INPUT_REDIR_CHAR, MOCK_FILE_NAME);
        callCommand = new CallCommand(argsList, appRunner, argumemtResovler);
        callCommand.evaluate(mockInputStream, mockBos);

        String expectedOutput = LS_OUTPUT + System.lineSeparator();
        assertEquals(expectedOutput, mockBos.toString());

        EnvironmentHelper.currentDirectory = System.getProperty("user.dir");
    }

    @Test
    void inputRedirectionFindApplicationIntegrationTest() throws Exception {
        List<String> argsList = StringsArgListHelper.concantenateStringsToList("find", MOCK_ROOT_DIR,
                "-name", "\"" + MOCK_FILE_NAME + "\"", INPUT_REDIR_CHAR, FILENAME1);
        callCommand = new CallCommand(argsList, appRunner, argumemtResovler);
        callCommand.evaluate(mockInputStream, mockBos);

        String expectedOutput = MOCK_ROOT_FILE1 + System.lineSeparator();
        assertEquals(expectedOutput, mockBos.toString());
    }

//    @Test
//    void inputRedirectionPasteApplicationIntegrationTest() throws Exception {
//        List<String> argsList = StringsArgListHelper.concantenateStringsToList("paste",
//                INPUT_REDIR_CHAR,  FILENAME1);
//        callCommand = new CallCommand(argsList, appRunner, argumemtResovler);
//        callCommand.evaluate(mockInputStream, mockBos);
//
//        String expectedOutput = FILE_1_CONTENT;
//        assertEquals(expectedOutput, mockBos.toString());
//    }

    @Test
    void inputRedirectionCutApplicationIntegrationTest() throws Exception {
        List<String> argsList = StringsArgListHelper.concantenateStringsToList("cut", "-c","3" ,INPUT_REDIR_CHAR, FILENAME1);
        callCommand = new CallCommand(argsList, appRunner, argumemtResovler);
        callCommand.evaluate(mockInputStream, mockBos);

        String expectedOutput = F1_CONTENT_CUT + System.lineSeparator();
        assertEquals(expectedOutput, mockBos.toString());
    }


    /*******************************************************************
     Tests for output redirection only
     ******************************************************************/

    @Test
    void outputRedirectionArgumentNotGivenTest() {
        List<String> argsList = StringsArgListHelper.concantenateStringsToList("ls", OUTPUT_REDIR_CHAR);
        callCommand = new CallCommand(argsList, appRunner, argumemtResovler);
        assertThrows(NoSuchElementException.class, () -> callCommand.evaluate(mockInputStream, mockBos));
    }

    @Test
    void outputRedirectionMultipleArgumentTest() throws Exception {
        String contentToEcho = "hello world";
        List<String> argsList = StringsArgListHelper.concantenateStringsToList(ECHO_COMMAND, contentToEcho, OUTPUT_REDIR_CHAR,
                OUTPUT_FILE_2, OUTPUT_FILE_1);
        callCommand = new CallCommand(argsList, appRunner, argumemtResovler);
        callCommand.evaluate(mockInputStream, mockBos);

        String outputFromFile2 = FileIOHelper.extractAndConcatenate(OUTPUT_FILE_2);
        String expectedOutput = contentToEcho + SPACE + OUTPUT_FILE_1;
        assertEquals(expectedOutput, outputFromFile2);

        // Check that OUTPUT_FILE_1 does not even exist
        File file = new File(OUTPUT_FILE_1);
//        assertFalse(file.exists());
    }

    @Test
    void outputRedirectionPasteApplicationIntegrationTest() throws Exception {
        List<String> argsList = StringsArgListHelper.concantenateStringsToList("paste",
                FILENAME1, OUTPUT_REDIR_CHAR, FILENAME2);
        callCommand = new CallCommand(argsList, appRunner, argumemtResovler);
        callCommand.evaluate(mockInputStream, mockBos);

        // Check that the correct output is written to FILENAME2
        String outputFromFile1 = FileIOHelper.extractAndConcatenate(FILENAME2);
        String expectedOutput = FILE_1_CONTENT;

        assertEquals(expectedOutput, outputFromFile1);
    }

    @Test
    void outputRedirectionSedApplicationIntegrationTest() throws Exception {
        List<String> argsList = StringsArgListHelper.concantenateStringsToList("sed", "s/^/hello/",
                FILENAME1, OUTPUT_REDIR_CHAR, OUTPUT_FILE_1);
        callCommand = new CallCommand(argsList, appRunner, argumemtResovler);
        callCommand.evaluate(mockInputStream, mockBos);

        // Check that the correct output is written to OUTPUT_FILE_1
        String outputFromFile1 = FileIOHelper.extractAndConcatenate(OUTPUT_FILE_1);
        String expectedOutput = F1_CONTENT_SED;
        assertEquals(expectedOutput, outputFromFile1);

        assertEquals(EMPTY_STRING, mockBos.toString());
    }

    @Test
    void outputRedirectionSortApplicationIntegrationTest() throws Exception {
        List<String> argsList = StringsArgListHelper.concantenateStringsToList("sort",
                FILENAME1, OUTPUT_REDIR_CHAR, OUTPUT_FILE_1);
        callCommand = new CallCommand(argsList, appRunner, argumemtResovler);
        callCommand.evaluate(mockInputStream, mockBos);

        // Check that the correct output is written to OUTPUT_FILE_1
        String outputFromFile1 = FileIOHelper.extractAndConcatenate(OUTPUT_FILE_1);
        String expectedOutput = F1_CONTENT_SORT;
        assertEquals(expectedOutput, outputFromFile1);

        assertEquals(EMPTY_STRING, mockBos.toString());
    }

    @Test
    void outputRedirectionEchoApplicationIntegrationTest() throws Exception {
        String echoText = "This is text to be echoed";
        List<String> argsList = StringsArgListHelper.concantenateStringsToList(ECHO_COMMAND,
                echoText, OUTPUT_REDIR_CHAR, OUTPUT_FILE_1);
        callCommand = new CallCommand(argsList, appRunner, argumemtResovler);
        callCommand.evaluate(mockInputStream, mockBos);

        // Check that the correct output is written to OUTPUT_FILE_1
        String outputFromFile1 = FileIOHelper.extractAndConcatenate(OUTPUT_FILE_1);
        String expectedOutput = echoText;
        assertEquals(expectedOutput, outputFromFile1);

        assertEquals(EMPTY_STRING, mockBos.toString());
    }

    /**
     * LS would not have standard output, the output will be redirected to file, hence
     * we check if the output written in file is correct.
     */
    @Test
    void outputRedirectionLsApplicationIntegrationTest() throws Exception {
        String currentDirectory = EnvironmentHelper.currentDirectory;
        EnvironmentHelper.currentDirectory = EnvironmentHelper.currentDirectory + File.separator + MOCK_ROOT_DIR;

        List<String> argsList = StringsArgListHelper.concantenateStringsToList("ls", OUTPUT_REDIR_CHAR, OUTPUT_FILE_1);
        callCommand = new CallCommand(argsList, appRunner, argumemtResovler);
        callCommand.evaluate(mockInputStream, mockBos);

        // Check that the correct output is written to OUTPUT_FILE_1. It should exist and be empty.
        // OUTPUT_FILE_1 will now be at a different location, so will have to locate it.
        String outputFromFile1 = FileIOHelper.extractAndConcatenate(MOCK_ROOT_DIR + File.separator + OUTPUT_FILE_1);
        // Will have OUTPUT_FILE_1 inside as well
        String expectedOutput = LS_OUTPUT + System.lineSeparator() + OUTPUT_FILE_1;
        assertEquals(expectedOutput, outputFromFile1);

        assertEquals(EMPTY_STRING, mockBos.toString());

        EnvironmentHelper.currentDirectory = currentDirectory;
    }

    @Test
    void outputRedirectionFindApplicationIntegrationTest() throws Exception {
        List<String> argsList = StringsArgListHelper.concantenateStringsToList("find", MOCK_ROOT_DIR,
                "-name", MOCK_FILE_NAME, OUTPUT_REDIR_CHAR, OUTPUT_FILE_1);
        callCommand = new CallCommand(argsList, appRunner, argumemtResovler);
        callCommand.evaluate(mockInputStream, mockBos);

        // Check that the correct output is written to OUTPUT_FILE_1.
        String outputFromFile1 = FileIOHelper.extractAndConcatenate(OUTPUT_FILE_1);
        String expectedOutput = MOCK_ROOT_FILE1;
        assertEquals(expectedOutput, outputFromFile1);

        // Check that nothing is written to stdout
        assertEquals(EMPTY_STRING, mockBos.toString());
    }

    @Test
    void outputRedirectionRmApplicationIntegrationTest() {
        // Ignores the input redirection and just exit
        List<String> argsList = StringsArgListHelper.concantenateStringsToList("rm", OUTPUT_REDIR_CHAR, FILENAME2);
        callCommand = new CallCommand(argsList, appRunner, argumemtResovler);

        Exception actualException = assertThrows(RmException.class, () -> callCommand.evaluate(mockInputStream, mockBos));
    }

    @Test
    void outputRedirectionMvApplicationIntegrationTest() {
        // Ignores the input redirection and just exit
        List<String> argsList = StringsArgListHelper.concantenateStringsToList("mv", OUTPUT_REDIR_CHAR, FILENAME2);
        callCommand = new CallCommand(argsList, appRunner, argumemtResovler);

        Exception actualException = assertThrows(MvException.class, () -> callCommand.evaluate(mockInputStream, mockBos));
    }

    @Test
    void outputRedirectionCutApplicationIntegrationTest() throws Exception {
        List<String> argsList = StringsArgListHelper.concantenateStringsToList("cut", "-c","3" , FILENAME1
        ,OUTPUT_REDIR_CHAR, OUTPUT_FILE_1);
        callCommand = new CallCommand(argsList, appRunner, argumemtResovler);
        callCommand.evaluate(mockInputStream, mockBos);

        String expectedOutput = F1_CONTENT_CUT;

        // Check that the correct output is written to OUTPUT_FILE_1.
        String outputFromFile1 = FileIOHelper.extractAndConcatenate(OUTPUT_FILE_1);
        assertEquals(expectedOutput, outputFromFile1);
    }

    /*******************************************************************
     Tests for both input and output redirection
     ******************************************************************/

    @Test
    void inputAndOutputRedirectionInputArgumentNotGivenTest() {
        List<String> argsList = StringsArgListHelper.concantenateStringsToList("ls", INPUT_REDIR_CHAR,
                OUTPUT_REDIR_CHAR, OUTPUT_FILE_1);
        callCommand = new CallCommand(argsList, appRunner, argumemtResovler);
        assertThrows(ShellException.class, () -> callCommand.evaluate(mockInputStream, mockBos));
    }

    @Test
    void inputAndOutputRedirectionOutputArgumentNotGivenTest() {
        List<String> argsList = StringsArgListHelper.concantenateStringsToList("ls", INPUT_REDIR_CHAR,
                FILENAME1, OUTPUT_REDIR_CHAR);
        callCommand = new CallCommand(argsList, appRunner, argumemtResovler);
        assertThrows(NoSuchElementException.class, () -> callCommand.evaluate(mockInputStream, mockBos));
    }

    @Test
    void inputAndOutputRedirectionInputAndOutputArgumentNotGivenTest() {
        List<String> argsList = StringsArgListHelper.concantenateStringsToList("ls", INPUT_REDIR_CHAR,
                OUTPUT_REDIR_CHAR);
        callCommand = new CallCommand(argsList, appRunner, argumemtResovler);
        assertThrows(ShellException.class, () -> callCommand.evaluate(mockInputStream, mockBos));
    }

//    @Test
//    void inputAndOutputRedirectionSedApplicationIntegrationTest() throws Exception {
//        List<String> argsList = StringsArgListHelper.concantenateStringsToList("sed", "s/^/hello/",
//                INPUT_REDIR_CHAR, FILENAME1, OUTPUT_REDIR_CHAR, OUTPUT_FILE_1);
//        callCommand = new CallCommand(argsList, appRunner, argumemtResovler);
//        callCommand.evaluate(mockInputStream, mockBos);
//
//        // Check that the correct output is written to OUTPUT_FILE_1
//        String outputFromFile1 = FileIOHelper.extractAndConcatenate(OUTPUT_FILE_1);
//        String expectedOutput = F1_CONTENT_SED;
//        assertEquals(expectedOutput, outputFromFile1);
//
//        // Check that nothing is written to stdout
//        assertEquals(EMPTY_STRING, mockBos.toString());
//    }

    @Test
    void inputAndOutputRedirectionSortApplicationIntegrationTest() throws Exception {
        List<String> argsList = StringsArgListHelper.concantenateStringsToList("sort",
                INPUT_REDIR_CHAR, FILENAME1, OUTPUT_REDIR_CHAR, OUTPUT_FILE_1);
        callCommand = new CallCommand(argsList, appRunner, argumemtResovler);
        callCommand.evaluate(mockInputStream, mockBos);

        // Check that the correct output is written to OUTPUT_FILE_1
        String outputFromFile1 = FileIOHelper.extractAndConcatenate(OUTPUT_FILE_1);
        String expectedOutput = F1_CONTENT_SORT;
        assertEquals(expectedOutput, outputFromFile1);

        // Check that nothing is written to stdout
        assertEquals(EMPTY_STRING, mockBos.toString());
    }

    @Test
    void inputAndOutputRedirectionEchoApplicationIntegrationTest() throws Exception {
        List<String> argsList = StringsArgListHelper.concantenateStringsToList(ECHO_COMMAND,
                INPUT_REDIR_CHAR, FILENAME1, OUTPUT_REDIR_CHAR, OUTPUT_FILE_1);
        callCommand = new CallCommand(argsList, appRunner, argumemtResovler);
        callCommand.evaluate(mockInputStream, mockBos);

        // Check that the correct output is written to OUTPUT_FILE_1
        String outputFromFile1 = FileIOHelper.extractAndConcatenate(OUTPUT_FILE_1);

        // echo does not accept input redirection so just write empty string to stdout
        String expectedOutput = EMPTY_STRING;
        assertEquals(expectedOutput, outputFromFile1);

        // Check that nothing is written to stdout
        assertEquals(EMPTY_STRING, mockBos.toString());
    }

    @Test
    void inputAndOutputRedirectionLsApplicationIntegrationTest() throws Exception {
        // Ignores the input redirection and just output the files and folder
        String currentDirectory = EnvironmentHelper.currentDirectory;
        EnvironmentHelper.currentDirectory = EnvironmentHelper.currentDirectory + File.separator + MOCK_ROOT_DIR;

        List<String> argsList = StringsArgListHelper.concantenateStringsToList("ls",
                INPUT_REDIR_CHAR, MOCK_FILE_NAME, OUTPUT_REDIR_CHAR, OUTPUT_FILE_1);
        callCommand = new CallCommand(argsList, appRunner, argumemtResovler);
        callCommand.evaluate(mockInputStream, mockBos);

        // Check that the correct output is written to OUTPUT_FILE_1.
        // OUTPUT_FILE_1 will now be at a different location, so will have to locate it.
        String outputFromFile1 = FileIOHelper.extractAndConcatenate(MOCK_ROOT_DIR + File.separator + OUTPUT_FILE_1);

        // Will have OUTPUT_FILE_1 inside as well
        String expectedOutput = LS_OUTPUT + System.lineSeparator() + OUTPUT_FILE_1;
        assertEquals(expectedOutput, outputFromFile1);

        // Check that nothing is written to stdout
        assertEquals(EMPTY_STRING, mockBos.toString());

        EnvironmentHelper.currentDirectory = currentDirectory;
    }

    @Test
    void inputAndOutputRedirectionFindApplicationIntegrationTest() throws Exception {
        List<String> argsList = StringsArgListHelper.concantenateStringsToList("find", MOCK_ROOT_DIR,
                "-name", MOCK_FILE_NAME, INPUT_REDIR_CHAR, FILENAME1, OUTPUT_REDIR_CHAR, OUTPUT_FILE_1);
        callCommand = new CallCommand(argsList, appRunner, argumemtResovler);
        callCommand.evaluate(mockInputStream, mockBos);

        // Check that the correct output is written to OUTPUT_FILE_1.
        String outputFromFile1 = FileIOHelper.extractAndConcatenate(OUTPUT_FILE_1);

        String expectedOutput = MOCK_ROOT_FILE1;
        assertEquals(expectedOutput, outputFromFile1);

        // Check that nothing is written to stdout
        assertEquals(EMPTY_STRING, mockBos.toString());
    }

    @Test
    void inputAndOutputRedirectionRmApplicationIntegrationTest() throws Exception{
        // Ignores the input redirection and just exit cause Rm dont take in
        List<String> argsList = StringsArgListHelper.concantenateStringsToList("rm", INPUT_REDIR_CHAR, FILENAME2, OUTPUT_REDIR_CHAR, FILENAME3);
        File file = new File(FILENAME2);
        file.createNewFile();
        File file2 = new File(FILENAME3);
        file2.createNewFile();
        callCommand = new CallCommand(argsList, appRunner, argumemtResovler);

        Exception actualException = assertThrows(RmException.class, () -> callCommand.evaluate(mockInputStream, mockBos));
    }

    @Test
    void inputAndOutputRedirectionMvApplicationIntegrationTest() throws Exception{
        // Ignores the input redirection and just exit
        List<String> argsList = StringsArgListHelper.concantenateStringsToList("mv", INPUT_REDIR_CHAR, FILENAME2, OUTPUT_REDIR_CHAR, FILENAME3);
        File file = new File(FILENAME2);
        file.createNewFile();
        File file2 = new File(FILENAME3);
        file2.createNewFile();
        callCommand = new CallCommand(argsList, appRunner, argumemtResovler);

        Exception actualException = assertThrows(MvException.class, () -> callCommand.evaluate(mockInputStream, mockBos));
    }

//    @Test
//    void intputAndoutputRedirectionPasteApplicationIntegrationTest() throws Exception {
//        List<String> argsList = StringsArgListHelper.concantenateStringsToList("paste", INPUT_REDIR_CHAR,
//                FILENAME1, OUTPUT_REDIR_CHAR, FILENAME2);
//        callCommand = new CallCommand(argsList, appRunner, argumemtResovler);
//        callCommand.evaluate(mockInputStream, mockBos);
//
//        // Check that the correct output is written to FILENAME2
//        String outputFromFile1 = FileIOHelper.extractAndConcatenate(FILENAME2);
//        String expectedOutput = FILE_1_CONTENT;
//
//        assertEquals(expectedOutput, outputFromFile1);
//    }

    @Test
    void intputAndOutputRedirectionCutApplicationIntegrationTest() throws Exception {
        List<String> argsList = StringsArgListHelper.concantenateStringsToList("cut", "-c","3" ,INPUT_REDIR_CHAR, FILENAME1
                ,OUTPUT_REDIR_CHAR, OUTPUT_FILE_1);
        callCommand = new CallCommand(argsList, appRunner, argumemtResovler);
        callCommand.evaluate(mockInputStream, mockBos);

        String expectedOutput = F1_CONTENT_CUT;

        // Check that the correct output is written to OUTPUT_FILE_1.
        String outputFromFile1 = FileIOHelper.extractAndConcatenate(OUTPUT_FILE_1);
        assertEquals(expectedOutput, outputFromFile1);
    }
}
