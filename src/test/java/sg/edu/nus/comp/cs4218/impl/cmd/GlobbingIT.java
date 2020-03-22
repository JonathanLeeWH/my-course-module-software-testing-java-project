package sg.edu.nus.comp.cs4218.impl.cmd;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;
import sg.edu.nus.comp.cs4218.EnvironmentHelper;
import sg.edu.nus.comp.cs4218.exception.*;
import sg.edu.nus.comp.cs4218.impl.util.ErrorConstants;
import sg.edu.nus.comp.cs4218.impl.FileIOHelper;
import sg.edu.nus.comp.cs4218.impl.StringsArgListHelper;
import sg.edu.nus.comp.cs4218.impl.util.ApplicationRunner;
import sg.edu.nus.comp.cs4218.impl.util.ArgumentResolver;

import java.io.ByteArrayOutputStream;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class GlobbingIT {

    private static final String FILE_NAME_1 = "CS4218A.txt";
    private static final String FILE_NAME_2 = "A4218A.txt";
    private static final String FILE_NAME_3 = "CS3203A.txt";
    private static final String FILE_NAME_4 = "1.txt";
    private static final String FILE_NAME_5 = "2.txt";
    private static final String FOLDER_NAME_1 = "folder1";
    private static final String FILE_CONTENT_1 = "Hello world";
    private static final String FILE_CONTENT_2 = "How are you";
    private static final String FILE_CONTENT_3 = "1";
    private static final String FILE_CONTENT_4 = "2";
    private static final String FILE_CONTENT_5 = "3";
    private static final String FILE_CONTENT_6 = "A";
    private static final String FILE_CONTENT_7 = "B";
    private static final String FILE_CONTENT_8 = "AA";
    private static final String WC_APP = "wc";
    private static final String ECHO_APP = "echo";
    private static final String GREP_APP = "grep";
    private static final String CUT_APP = "cut";
    private static final String SORT_APP = "sort";
    private static final String FIND_APP = "find";
    private static final String LS_APP = "ls";
    private static final String PASTE_APP = "paste";
    private static final String SED_APP = "sed";
    private static final String INVALID_APP = "lsa";
    private static final String B_FLAG = "-b";
    private static final String C_FLAG = "-c";
    private static final String NAME_FLAG = "-name";
    private static final String RELATIVE_CURR = ".";
    private static final String REGEX_EXPR_1 = "s/^/> /";
    private static final String REGEX_EXPR_2 = "s/^/1/";

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
     * Tests evaluate method ls and glob interaction
     * For example: ls *
     * Expected: Outputs correctly in lexographical order
     */
    @Test
    void testEvaluateLsCommandWithGlobInteractionShouldOutputCorrectly(@TempDir Path tempDir) throws Exception {
        EnvironmentHelper.currentDirectory = tempDir.toString();
        Files.createFile(tempDir.resolve(FILE_NAME_1));
        Files.createFile(tempDir.resolve(FILE_NAME_2));
        Files.createFile(tempDir.resolve(FILE_NAME_3));
        List<String> argList = StringsArgListHelper.concantenateStringsToList(LS_APP , "*");
        callCommand = new CallCommand(argList , new ApplicationRunner(), new ArgumentResolver());
        callCommand.evaluate(System.in, outputStream);
        assertEquals(FILE_NAME_2 + System.lineSeparator() + FILE_NAME_3 + System.lineSeparator() + FILE_NAME_1 + System.lineSeparator(), outputStream.toString());
    }

    /**
     * Tests evaluate method ls and glob interaction
     * For example: ls Test*
     * Expected: Outputs correctly in lexographical order
     */
    @Test
    void testEvaluateLsCommandWithGlobInteractionCurrFolderShouldOutputCorrectly() throws Exception {
        List<String> argList = StringsArgListHelper.concantenateStringsToList(LS_APP , "Test*");
        callCommand = new CallCommand(argList , new ApplicationRunner(), new ArgumentResolver());
        callCommand.evaluate(System.in, outputStream);
        assertEquals(FILENAME1  + System.lineSeparator() + System.lineSeparator() + FOLDER1+ ":" + System.lineSeparator(), outputStream.toString());
    }

    /**
     * Tests evaluate method ls and glob interaction
     * For example: ls TestFolder2*
     * Expected: Throws No such file or folder Error
     */
    @Test
    void testEvaluateLsCommandWithGlobInteractionNoFilesGLobShouldOutputCorrectly() throws Exception {
        List<String> argList = StringsArgListHelper.concantenateStringsToList(LS_APP , "TestFolder2*");
        callCommand = new CallCommand(argList , new ApplicationRunner(), new ArgumentResolver());

        assertThrows(LsException.class, () -> callCommand.evaluate(System.in, outputStream));
    }

    /**
     * Tests evaluate method find and glob interaction
     * For example: find A -name A*
     * Expected: Finds all file with A in the "A" folder
     */
    @Test
    void testEvaluateFindCommandWithGlobInteractionFilesExistGlobShouldOutputCorrectly() throws Exception {
        Path DIRECTORY = Paths.get(currDir,"src","test","java", "tdd",
                "util", "dummyTestFolder", "FindTestFolder", "sampleFiles" , "globbingTest");

        EnvironmentHelper.currentDirectory = DIRECTORY.toString();

        List<String> argList = StringsArgListHelper.concantenateStringsToList(FIND_APP ,"A", "-name","\"A*\"");
        callCommand = new CallCommand(argList , new ApplicationRunner(), new ArgumentResolver());
        callCommand.evaluate(inputStream,outputStream);
        assertEquals(   "A" + File.separator + "A1.txt"  + System.lineSeparator()
                + "A" + File.separator + "A2.txt" + System.lineSeparator()
                + "A" + File.separator + "A3.json" + System.lineSeparator(), outputStream.toString());
        EnvironmentHelper.currentDirectory = currDir;
    }

    /**
     * Tests evaluate method find and glob interaction
     * For example: find A -name B*
     * Expected: Returns Empty string as B not found in "A" folder
     */
    @Test
    void testEvaluateFindCommandWithGlobInteractionNoFilesGlobShouldOutputCorrectly() throws Exception {
        Path DIRECTORY = Paths.get(currDir,"src","test","java", "tdd",
                "util", "dummyTestFolder", "FindTestFolder", "sampleFiles" , "globbingTest");

        EnvironmentHelper.currentDirectory = DIRECTORY.toString();

        List<String> argList = StringsArgListHelper.concantenateStringsToList(FIND_APP ,"A", "-name","\"B*\"");
        callCommand = new CallCommand(argList , new ApplicationRunner(), new ArgumentResolver());
        callCommand.evaluate(inputStream,outputStream);
        assertEquals(   "" , outputStream.toString());
        EnvironmentHelper.currentDirectory = currDir;
    }

    /**
     * Tests evaluate method sort and glob interaction
     * For example: sort TestFile*
     * Expected: sort both contents of TextFile1 and TextFile2
     */
    @Test
    void testEvaluateSortCommandWithGlobInteractionShouldOutputCorrectly() throws Exception {

        BufferedWriter writer1 = new BufferedWriter(new PrintWriter(FILENAME2));
        writer1.write(FILE_1_CONTENT);
        writer1.flush();
        writer1.close();

        List<String> argList = StringsArgListHelper.concantenateStringsToList(SORT_APP ,"TestFile*");
        callCommand = new CallCommand(argList , new ApplicationRunner(), new ArgumentResolver());
        callCommand.evaluate(inputStream,outputStream);
        assertEquals(   "Some numbers: 50 1 2." + System.lineSeparator()
                + "Some numbers: 50 1 2." + System.lineSeparator()
                + "Some whitespace    ><*&^%.?" + System.lineSeparator()
                + "Some whitespace    ><*&^%.?" + System.lineSeparator()
                + "There are some content here." + System.lineSeparator()
                + "There are some content here." + System.lineSeparator()
                + "This is the content for file 1." + System.lineSeparator()
                + "This is the content for file 1." + System.lineSeparator(), outputStream.toString());
    }

    /**
     * Tests evaluate method sort and glob interaction
     * For example: sort TestFile*
     * Expected: sort content of textFile as only 1 file
     */
    @Test
    void testEvaluateSortCommandWithGlobInteraction1FileShouldOutputCorrectly() throws Exception {

        List<String> argList = StringsArgListHelper.concantenateStringsToList(SORT_APP ,"TestFile*");
        callCommand = new CallCommand(argList , new ApplicationRunner(), new ArgumentResolver());
        callCommand.evaluate(inputStream,outputStream);
        assertEquals(   F1_CONTENT_SORT + System.lineSeparator() , outputStream.toString());
    }

    /**
     * Tests evaluate method sort and glob interaction
     * For example: sort TextFile*
     * Expected: Error would be thrown as no such file exist in folder
     */
    @Test
    void testEvaluateSortCommandWithGlobInteractionNoFileShouldThrowNoFile() throws Exception {

        List<String> argList = StringsArgListHelper.concantenateStringsToList(SORT_APP ,"TextFile*");
        callCommand = new CallCommand(argList , new ApplicationRunner(), new ArgumentResolver());
        assertThrows(SortException.class, () -> callCommand.evaluate(System.in, outputStream));
    }

    /**
     * Tests evaluate method cut and glob interaction
     * For example: cut -c 3 TestFile*
     * Expected: Cut third char TestFile as only 1 file inside current folder
     */
    @Test
    void testEvaluateCutCommandWithGlobInteraction1FileShouldOutputCorrectly() throws Exception {

        List<String> argList = StringsArgListHelper.concantenateStringsToList(CUT_APP ,"-c","3", "TestFile*");
        callCommand = new CallCommand(argList , new ApplicationRunner(), new ArgumentResolver());
        callCommand.evaluate(inputStream,outputStream);
        assertEquals(   F1_CONTENT_CUT  , outputStream.toString());
    }

    /**
     * Tests evaluate method cut and glob interaction
     * For example: cut -c 3 TestFile*
     * Expected: Cut third char for both TestFiles  inside current folder
     */
    @Test
    void testEvaluateCutCommandWithGlobInteractionMultipleFileShouldOutputCorrectly() throws Exception {
        BufferedWriter writer1 = new BufferedWriter(new PrintWriter(FILENAME2));
        writer1.write(FILE_1_CONTENT);
        writer1.flush();
        writer1.close();

        List<String> argList = StringsArgListHelper.concantenateStringsToList(CUT_APP ,"-c","3", "TestFile*");
        callCommand = new CallCommand(argList , new ApplicationRunner(), new ArgumentResolver());
        callCommand.evaluate(inputStream,outputStream);
        assertEquals(   F1_CONTENT_CUT +  F1_CONTENT_CUT , outputStream.toString());
    }


    /**
     * Tests evaluate method cut and glob interaction
     * For example: cut -c 3 TextFile*
     * Expected: Throw Error cause no such file
     */
    @Test
    void testEvaluateCutCommandWithGlobInteractionNoFileShouldThrowNoFileFound() throws Exception {

        List<String> argList = StringsArgListHelper.concantenateStringsToList(CUT_APP ,"-c","3", "TextFile*");
        callCommand = new CallCommand(argList , new ApplicationRunner(), new ArgumentResolver());
        assertThrows( CutException.class , () -> callCommand.evaluate(System.in, outputStream));
    }

    /**
     * Tests evaluate method rm and glob interaction
     * For example: rm TestFile*
     * Expected: Rm TestFile1.txt
     */
    @Test
    void testEvaluateRmCommandWithGlobInteraction1FileShouldOutputCorrectly() throws Exception {

        File file = new File(FILENAME1);
        assertTrue(file.exists());
        List<String> argList = StringsArgListHelper.concantenateStringsToList("rm" ,"TestFile*");
        callCommand = new CallCommand(argList , new ApplicationRunner(), new ArgumentResolver());
        callCommand.evaluate(inputStream,outputStream);
        assertFalse(file.exists());

        //put back TestFile1.txt
        BufferedWriter writer1 = new BufferedWriter(new PrintWriter(FILENAME1));
        writer1.write(FILE_1_CONTENT);
        writer1.flush();
        writer1.close();

    }

    /**
     * Tests evaluate method rm and glob interaction
     * For example: rm TestFile*
     * Expected: Rm TestFile1.txt TestFile2.txt TestFile3.txt
     */
    @Test
    void testEvaluateRmCommandWithGlobInteraction3FilesShouldOutputCorrectly() throws Exception {

        File file2 = new File(FILENAME2);
        file2.createNewFile();
        File file3 = new File(FILENAME3);
        file3.createNewFile();

        File file = new File(FILENAME1);
        assertTrue(file.exists());
        assertTrue(file2.exists());
        assertTrue(file3.exists());
        List<String> argList = StringsArgListHelper.concantenateStringsToList("rm" ,"TestFile*");
        callCommand = new CallCommand(argList , new ApplicationRunner(), new ArgumentResolver());
        callCommand.evaluate(inputStream,outputStream);
        assertFalse(file.exists());
        assertFalse(file2.exists());
        assertFalse(file3.exists());

        //put back TestFile1.txt
        BufferedWriter writer1 = new BufferedWriter(new PrintWriter(FILENAME1));
        writer1.write(FILE_1_CONTENT);
        writer1.flush();
        writer1.close();

    }

    /**
     * Tests evaluate method rm and glob interaction
     * For example: rm TestFile3*
     * Expected: Rm TestFile1.txt TestFile2.txt TestFile3.txt
     */
    @Test
    void testEvaluateRmCommandWithGlobInteractionNoFileShouldThrowErrorFileNotFound() throws Exception {

        File file = new File("TestFile3");
        assertFalse(file.exists());
        List<String> argList = StringsArgListHelper.concantenateStringsToList("rm" ,"TestFile3*");
        callCommand = new CallCommand(argList , new ApplicationRunner(), new ArgumentResolver());
        assertThrows(RmException.class, ()-> callCommand.evaluate(inputStream,outputStream));
    }

    /**
     * Tests evaluate method mv and glob interaction
     * For example: mv TestFile* AnotherFolder
     * Expected: Should mv 1 TestFile1.txt to AnotherFolder
     */
    @Test
    void testEvaluatePMvCommandWithGlobInteraction1FileShouldThrowFileNotFound() throws Exception {
        BufferedWriter writer1 = new BufferedWriter(new PrintWriter(FILENAME1));
        writer1.write(FILE_1_CONTENT);
        writer1.flush();
        writer1.close();

        FileIOHelper.createFileFolder("AnotherFolder" , true);
        File file = new File(FILENAME1);
        assertTrue(file.exists());
        List<String> argList = StringsArgListHelper.concantenateStringsToList("mv" ,"TestFile*", "AnotherFolder");
        callCommand = new CallCommand(argList , new ApplicationRunner(), new ArgumentResolver());
        callCommand.evaluate(inputStream,outputStream);
        File file2 = new File(currDir + File.separator + "AnotherFolder" + File.separator + FILENAME1);
        assertTrue(file2.exists());
        assertFalse(file.exists());

        file2.delete();
        File folder = new File("AnotherFolder");
        folder.delete();

        BufferedWriter writer2 = new BufferedWriter(new PrintWriter(FILENAME1));
        writer2.write(FILE_1_CONTENT);
        writer2.flush();
        writer2.close();
    }

    /**
     * Tests evaluate method mv and glob interaction
     * For example: mv TestFile* AnotherFolder
     * Expected: Should mv 1 TestFile1.txt TestFile2.txt TestFile3.txt to AnotherFolder folder
     */
    @Test
    void testEvaluatePMvCommandWithGlobInteraction3FileShouldThrowFileNotFound() throws Exception {
        BufferedWriter writer1 = new BufferedWriter(new PrintWriter(FILENAME1));
        writer1.write(FILE_1_CONTENT);
        writer1.flush();
        writer1.close();

        FileIOHelper.createFileFolder("AnotherFolder" , true);
        FileIOHelper.createFileFolder(FILENAME2 , false);
        FileIOHelper.createFileFolder(FILENAME3 , false);
        File file = new File(FILENAME1);
        assertTrue(file.exists());
        List<String> argList = StringsArgListHelper.concantenateStringsToList("mv" ,"TestFile*", "AnotherFolder");
        callCommand = new CallCommand(argList , new ApplicationRunner(), new ArgumentResolver());
        callCommand.evaluate(inputStream,outputStream);
        File file2 = new File(currDir + File.separator + "AnotherFolder" + File.separator + FILENAME1);
        File file3 = new File(currDir + File.separator + "AnotherFolder" + File.separator + FILENAME2);
        File file4 = new File(currDir + File.separator + "AnotherFolder" + File.separator + FILENAME3);

        assertTrue(file2.exists());
        assertTrue(file3.exists());
        assertTrue(file4.exists());
        assertFalse(file.exists());

        file2.delete();
        file3.delete();
        file4.delete();
        File folder = new File("AnotherFolder");
        folder.delete();

        BufferedWriter writer2 = new BufferedWriter(new PrintWriter(FILENAME1));
        writer2.write(FILE_1_CONTENT);
        writer2.flush();
        writer2.close();
    }

    /**
     * Tests evaluate method mv and glob interaction
     * For example: mv TextFile* AnotherFolder
     * Expected: Should throw error as no Files globbed
     */
    @Test
    void testEvaluatePMvCommandWithGlobInteractionNoFileShouldThrowFileNotFound() throws Exception {

        BufferedWriter writer1 = new BufferedWriter(new PrintWriter(FILENAME1));
        writer1.write(FILE_1_CONTENT);
        writer1.flush();
        writer1.close();

        List<String> argList = StringsArgListHelper.concantenateStringsToList("mv" ,"TextFile*", "AnotherFolder");
        callCommand = new CallCommand(argList , new ApplicationRunner(), new ArgumentResolver());
        assertThrows(MvException.class, ()-> callCommand.evaluate(inputStream,outputStream));

    }

    /**
     * Tests evaluate method echo and glob interaction
     * For example: echo TestFile*
     * Expected: Only output file name of TestFile1.txt
     */
    @Test
    void testEvaluateEchoCommandWithGlobInteraction1FileShouldOutputCorrectly() throws Exception {

        List<String> argList = StringsArgListHelper.concantenateStringsToList(ECHO_APP ,"TestFile*");
        callCommand = new CallCommand(argList , new ApplicationRunner(), new ArgumentResolver());
        callCommand.evaluate(inputStream,outputStream);
        assertEquals(   FILENAME1 + System.lineSeparator() , outputStream.toString());
    }

    /**
     * Tests evaluate method echo and glob interaction
     * For example: echo TestFile*
     * Expected: Outputs filenames of TestFile1.txt TestFile2.txt and TestFile3.txt
     */
    @Test
    void testEvaluateEchoCommandWithGlobInteractionMultipleFileShouldOutputCorrectly() throws Exception {

        BufferedWriter writer1 = new BufferedWriter(new PrintWriter(FILENAME2));
        writer1.write(FILE_1_CONTENT);
        writer1.flush();
        writer1.close();

        BufferedWriter writer2 = new BufferedWriter(new PrintWriter(FILENAME3));
        writer2.write(FILE_1_CONTENT);
        writer2.flush();
        writer2.close();

        List<String> argList = StringsArgListHelper.concantenateStringsToList(ECHO_APP ,"TestFile*");
        callCommand = new CallCommand(argList , new ApplicationRunner(), new ArgumentResolver());
        callCommand.evaluate(inputStream,outputStream);
        assertEquals(   FILENAME1 + " "
                + FILENAME2 + " "
                + FILENAME3 + System.lineSeparator(), outputStream.toString());
    }

    /**
     * Tests evaluate method echo and glob interaction
     * For example: echo TextFile*
     * Expected: Only Echo TextFile* as no file found
     */
    @Test
    void testEvaluateEchoCommandWithGlobInteractionNoFileShouldOutputCorrectly() throws Exception {

        List<String> argList = StringsArgListHelper.concantenateStringsToList(ECHO_APP ,"TextFile*");
        callCommand = new CallCommand(argList , new ApplicationRunner(), new ArgumentResolver());
        callCommand.evaluate(inputStream,outputStream);
        assertEquals(   "TextFile*" + System.lineSeparator() , outputStream.toString());
    }

    /**
     * Tests evaluate method paste and glob interaction
     * For example: paste TestFile* > outputFile1.txt
     * Expected: Check content of outputFile1.txt should be correct for 1 testFile1.txt
     */
    @Test
    void testEvaluatePasteCommandWithGlobInteraction1FileShouldOutputCorrectly() throws Exception {

        List<String> argList = StringsArgListHelper.concantenateStringsToList(PASTE_APP ,"TestFile*", OUTPUT_REDIR_CHAR , OUTPUT_FILE_1);
        callCommand = new CallCommand(argList , new ApplicationRunner(), new ArgumentResolver());
        callCommand.evaluate(inputStream,outputStream);


        String outputFromFile = FileIOHelper.extractAndConcatenate(OUTPUT_FILE_1);
        String expectedOutput = FILE_1_CONTENT;
        assertEquals(expectedOutput, outputFromFile);
}

    /**
     * Tests evaluate method paste and glob interaction
     * For example: paste TestFile* > outputFile1.txt
     * Expected: Check content of outputFile1.txt should be correct for 2 files TestFile1.txt TestFile2.txt
     */
    @Test
    void testEvaluatePasteCommandWithGlobInteraction2FileShouldOutputCorrectly() throws Exception {

        BufferedWriter writer1 = new BufferedWriter(new PrintWriter(FILENAME2));
        writer1.write(System.lineSeparator()
                + System.lineSeparator()
                + System.lineSeparator()
                + System.lineSeparator() +"A");
        writer1.flush();
        writer1.close();

        BufferedWriter writer2 = new BufferedWriter(new PrintWriter(FILENAME3));
        writer2.write(System.lineSeparator()
                + System.lineSeparator()
                + System.lineSeparator()
                + System.lineSeparator()
                + System.lineSeparator() +"B");
        writer2.flush();
        writer2.close();

        List<String> argList = StringsArgListHelper.concantenateStringsToList(PASTE_APP ,"TestFile*", OUTPUT_REDIR_CHAR , OUTPUT_FILE_1);
        callCommand = new CallCommand(argList , new ApplicationRunner(), new ArgumentResolver());
        callCommand.evaluate(inputStream,outputStream);

        String outputFromFile = FileIOHelper.extractAndConcatenate(OUTPUT_FILE_1);
        String expectedOutput = "This is the content for file 1." + "\t"+ "\t"
                + System.lineSeparator() + "There are some content here."+ "\t"+ "\t"
                + System.lineSeparator() + "Some numbers: 50 1 2."+ "\t"+ "\t"
                + System.lineSeparator() + "Some whitespace    ><*&^%.?"+ "\t"+ "\t"
                + System.lineSeparator() + "A" + "\t"
                + System.lineSeparator() + "B";
        assertEquals(expectedOutput, outputFromFile);
    }

    /**
     * Tests evaluate method paste and glob interaction
     * For example: paste TextFile* > outputFile1.txt
     * Expected: Should throw error cause no found globbed
     */
    @Test
    void testEvaluatePasteCommandWithGlobInteraction1FileShouldThrowFileNotFound() throws Exception {

        List<String> argList = StringsArgListHelper.concantenateStringsToList(PASTE_APP ,"TextFile*", OUTPUT_REDIR_CHAR , OUTPUT_FILE_1);
        callCommand = new CallCommand(argList , new ApplicationRunner(), new ArgumentResolver());
        assertThrows(PasteException.class, ()-> callCommand.evaluate(inputStream,outputStream));

    }


    /**
     * Tests evaluate method cp and glob interaction
     * For example: cp TestFile* AnotherFolder
     * Expected: Should cp 1 TestFile1.txt to AnotherFolder folder
     */
    @Test
    void testEvaluatePCpCommandWithGlobInteraction1FileShouldThrowFileNotFound() throws Exception {
        FileIOHelper.createFileFolder("AnotherFolder" , true);
        File file = new File(FILENAME1);
        assertTrue(file.exists());
        List<String> argList = StringsArgListHelper.concantenateStringsToList("cp" ,"TestFile*", "AnotherFolder");
        callCommand = new CallCommand(argList , new ApplicationRunner(), new ArgumentResolver());
        callCommand.evaluate(inputStream,outputStream);
        File file2 = new File(currDir + File.separator + "AnotherFolder" + File.separator + FILENAME1);
        assertTrue(file2.exists());
        assertTrue(file.exists());

        file2.delete();
        File folder = new File("AnotherFolder");
        folder.delete();

    }

    /**
     * Tests evaluate method cp and glob interaction
     * For example: cp TestFile* AnotherFolder
     * Expected: Should cp 1 TestFile1.txt TestFile2.txt TestFile3.txt to AnotherFolder folder
     */
    @Test
    void testEvaluatePCpCommandWithGlobInteraction3FileShouldThrowFileNotFound() throws Exception {
        FileIOHelper.createFileFolder("AnotherFolder" , true);
        FileIOHelper.createFileFolder(FILENAME2 , false);
        FileIOHelper.createFileFolder(FILENAME3 , false);
        File file = new File(FILENAME1);
        assertTrue(file.exists());
        List<String> argList = StringsArgListHelper.concantenateStringsToList("cp" ,"TestFile*", "AnotherFolder");
        callCommand = new CallCommand(argList , new ApplicationRunner(), new ArgumentResolver());
        callCommand.evaluate(inputStream,outputStream);
        File file2 = new File(currDir + File.separator + "AnotherFolder" + File.separator + FILENAME1);
        File file3 = new File(currDir + File.separator + "AnotherFolder" + File.separator + FILENAME2);
        File file4 = new File(currDir + File.separator + "AnotherFolder" + File.separator + FILENAME3);

        assertTrue(file2.exists());
        assertTrue(file3.exists());
        assertTrue(file4.exists());
        assertTrue(file.exists());

        file2.delete();
        file3.delete();
        file4.delete();
        File folder = new File("AnotherFolder");
        folder.delete();

    }

    /**
     * Tests evaluate method cp and glob interaction
     * For example: cp TextFile* AnotherFolder
     * Expected: Should throw error as no Files globbed
     */
    @Test
    void testEvaluateCpCommandWithGlobInteractionNoFileShouldThrowFileNotFound() throws Exception {

        List<String> argList = StringsArgListHelper.concantenateStringsToList("cp" ,"TextFile*", "AnotherFolder");
        callCommand = new CallCommand(argList , new ApplicationRunner(), new ArgumentResolver());
        assertThrows(CpException.class, ()-> callCommand.evaluate(inputStream,outputStream));

    }

    /**
     * Tests evaluate method wc and glob interaction
     * For example: wc TestFile*
     * Expected: Output correctly for TestFile1.txt
     */
    @Test
    void testEvaluateWcCommandWithGlobInteraction1FileShouldOutputCorrectly() throws Exception {

        List<String> argList = StringsArgListHelper.concantenateStringsToList(WC_APP ,"TestFile*");
        callCommand = new CallCommand(argList , new ApplicationRunner(), new ArgumentResolver());
        callCommand.evaluate(inputStream,outputStream);
        assertEquals( String.format(" %7d %7d %7d", 3, 20, 110)+ " " + FILENAME1 + System.lineSeparator(), outputStream.toString());
    }

    /**
     * Tests evaluate method wc and glob interaction
     * For example: wc TestFile*
     * Expected: Output correctly for TestFile1.txt TestFile2.txt
     */
    @Test
    void testEvaluateWcCommandWithGlobInteraction3FileShouldOutputCorrectly() throws Exception {

        BufferedWriter writer1 = new BufferedWriter(new PrintWriter(FILENAME2));
        writer1.write(FILE_1_CONTENT);
        writer1.flush();
        writer1.close();

        BufferedWriter writer2 = new BufferedWriter(new PrintWriter(FILENAME3));
        writer2.write(FILE_1_CONTENT);
        writer2.flush();
        writer2.close();
        List<String> argList = StringsArgListHelper.concantenateStringsToList(WC_APP, "TestFile*");
        callCommand = new CallCommand(argList, new ApplicationRunner(), new ArgumentResolver());
        callCommand.evaluate(inputStream, outputStream);
        assertEquals(String.format(" %7d %7d %7d", 3, 20, 110) + " " + FILENAME1 + System.lineSeparator()
                + String.format(" %7d %7d %7d", 3, 20, 110) + " " + FILENAME2 + System.lineSeparator()
                + String.format(" %7d %7d %7d", 3, 20, 110) + " " + FILENAME3 + System.lineSeparator()
                + String.format(" %7d %7d %7d", 9, 60, 330) + " " + "total" + System.lineSeparator(), outputStream.toString());

    }
    /**
     * Tests evaluate method wc and glob interaction
     * For example: wc TextFile*
     * Expected: Should throw error as no Files globbed
     */
    @Test
    void testEvaluateWcCommandWithGlobInteractionNoFileShouldThrowFileNotFound() throws Exception {

        List<String> argList = StringsArgListHelper.concantenateStringsToList("wc" ,"TextFile*");
        callCommand = new CallCommand(argList , new ApplicationRunner(), new ArgumentResolver());
        callCommand.evaluate(inputStream,outputStream);

        assertEquals("wc: No such file or directory" + System.lineSeparator(), outputStream.toString());
    }


//    /**
//     * Tests evaluate method grep and glob interaction
//     * For example: grep "some content" TestFile*
//     * Expected: Output 1 line in TestFile1.txt
//     */
//    @Test
//    void testEvaluateGrepCommandWithGlobInteraction1FileShouldOutputCorrectly() throws Exception {
//
//        List<String> argList = StringsArgListHelper.concantenateStringsToList(GREP_APP,"\"some content\"" ,"TestFile*");
//        callCommand = new CallCommand(argList , new ApplicationRunner(), new ArgumentResolver());
//        callCommand.evaluate(inputStream,outputStream);
//        assertEquals(   "There are some content here." + System.lineSeparator() , outputStream.toString());
//    }



}
