package Hackathon.test;

import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import sg.edu.nus.comp.cs4218.EnvironmentHelper;
import sg.edu.nus.comp.cs4218.Shell;
import sg.edu.nus.comp.cs4218.impl.FileIOHelper;
import sg.edu.nus.comp.cs4218.impl.ShellImpl;
import sg.edu.nus.comp.cs4218.impl.util.StringUtils;
import tdd.util.TestUtil;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;

public class ShellImplIT {
    private static Shell shell;
    private static InputStream inputStream;
    private static ByteArrayOutputStream outputStream;
    private static final String MAIN_3_DIR = "main3";
    private static final String SUB_3_SUB_DIR = MAIN_3_DIR + File.separator + "sub3";

    @BeforeAll
    static void setUp() throws IOException {
        EnvironmentHelper.currentDirectory = System.getProperty("user.dir");
        inputStream = Mockito.mock(InputStream.class);

        File main3Dir = new File(MAIN_3_DIR);
        main3Dir.mkdir();

        File sub3Dir = new File(SUB_3_SUB_DIR);
        sub3Dir.mkdir();
    }

    @AfterAll
    static void tearDown() throws IOException {
        EnvironmentHelper.currentDirectory = System.getProperty("user.dir");
        inputStream.close();
        outputStream.close();
        FileIOHelper.deleteTestFiles(MAIN_3_DIR, SUB_3_SUB_DIR);
    }

    @AfterEach
    void tearDownAfterEach() throws IOException {
        EnvironmentHelper.currentDirectory = System.getProperty("user.dir");
        inputStream.close();
        outputStream.close();
    }

    @BeforeEach
    void setUpBeforeEach() {
        EnvironmentHelper.currentDirectory = System.getProperty("user.dir");
        shell = new ShellImpl();
        inputStream = new ByteArrayInputStream("123".getBytes());
        outputStream = new ByteArrayOutputStream();
    }

    @Test
    void testParseAndEvaluateForBugReportNum3() throws Exception {
        String input = "cd main3/`ls main3`";
        String expectedPath = Paths.get(EnvironmentHelper.currentDirectory + "/main3/sub3").toString();
        shell.parseAndEvaluate(input, outputStream);
        assertEquals(expectedPath, EnvironmentHelper.currentDirectory);
    }

    @Test
    void testParseAndEvaluateForBugReportNum11() {
        String input = "cut -b 1,3-5 file1.txt";
        Throwable thrown = assertThrows(Exception.class, () -> shell.parseAndEvaluate(input, outputStream));
        String expectedResult = "cut: " + ERR_INVALID_ARGS;
        assertEquals(thrown.getMessage(), expectedResult);
    }

    /**
     * Tests Bug Report 2 where LS prints new line in empty file
     * Command: ls main2
     */
    @Test
    void testParseAndEvaluateForBugReportNum2() throws Exception {

        String path = System.getProperty("user.dir")
                + StringUtils.fileSeparator() + "src"
                + StringUtils.fileSeparator() + "test"
                + StringUtils.fileSeparator() + "java"
                + StringUtils.fileSeparator() + "Hackathon"
                + StringUtils.fileSeparator() + "resource";
        if (Files.isDirectory(TestUtil.resolveFilePath(path))) {
            EnvironmentHelper.currentDirectory = TestUtil.resolveFilePath(path).toString();
        }

        String input = "ls main2";
        shell.parseAndEvaluate(input, outputStream);
        String expectedResult = "";
        assertEquals(expectedResult ,outputStream.toString() );
    }



    /**
     * Tests Bug Report 9 where file path has bug regarding move
     * Command:    cd main1; mv file1.txt sub1/file3.txt
     */
    @Test
    void testParseAndEvaluateForBugReportNum9() throws Exception {

        String path = System.getProperty("user.dir")
                + StringUtils.fileSeparator() + "src"
                + StringUtils.fileSeparator() + "test"
                + StringUtils.fileSeparator() + "java"
                + StringUtils.fileSeparator() + "Hackathon"
                + StringUtils.fileSeparator() + "resource";
        if (Files.isDirectory(TestUtil.resolveFilePath(path))) {
            EnvironmentHelper.currentDirectory = TestUtil.resolveFilePath(path).toString();
        }

        String input = "cd main1; mv file1.txt sub1/file3.txt";
        shell.parseAndEvaluate(input, outputStream);

        File file = new File(path + File.separator + "main1" + File.separator + "sub1" + File.separator + "file3.txt");
        assertTrue(file.exists());


        String inputPutBack = "cd sub1; mv file3.txt " + "\"" + path + File.separator + "main1" +  File.separator + "file1.txt\"";
        shell.parseAndEvaluate(inputPutBack, outputStream);

    }

    /**
     * Tests Bug Report 7 where moving folder to same location
     * Command:    mv main3 .
     */
    @Test
    void testParseAndEvaluateForBugReportNum7() throws Exception {

        String path = System.getProperty("user.dir")
                + StringUtils.fileSeparator() + "src"
                + StringUtils.fileSeparator() + "test"
                + StringUtils.fileSeparator() + "java"
                + StringUtils.fileSeparator() + "Hackathon"
                + StringUtils.fileSeparator() + "resource";
        if (Files.isDirectory(TestUtil.resolveFilePath(path))) {
            EnvironmentHelper.currentDirectory = TestUtil.resolveFilePath(path).toString();
        }

        String input = "mv main3 .";
        Throwable thrown = assertThrows(Exception.class, () -> shell.parseAndEvaluate(input, outputStream));
        String expectedResult = "mv: " + IDENTICAL_LOCATION;
        assertEquals(thrown.getMessage(), expectedResult);
    }

    /**
     * Tests Bug Report 8 where moving folder to same location
     * Command:  mv main3 main3/sub3
     */
    @Test
    void testParseAndEvaluateForBugReportNum8() throws Exception {

        String path = System.getProperty("user.dir")
                + StringUtils.fileSeparator() + "src"
                + StringUtils.fileSeparator() + "test"
                + StringUtils.fileSeparator() + "java"
                + StringUtils.fileSeparator() + "Hackathon"
                + StringUtils.fileSeparator() + "resource";
        if (Files.isDirectory(TestUtil.resolveFilePath(path))) {
            EnvironmentHelper.currentDirectory = TestUtil.resolveFilePath(path).toString();
        }

        String input = "mv main3 main3/sub3";
        Throwable thrown = assertThrows(Exception.class, () -> shell.parseAndEvaluate(input, outputStream));
        String expectedResult = "mv: " + MOVING_TO_CHILD;
        assertEquals(thrown.getMessage(), expectedResult);
    }
}