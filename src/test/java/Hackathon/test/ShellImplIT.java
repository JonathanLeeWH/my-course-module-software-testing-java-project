package Hackathon.test;

import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import sg.edu.nus.comp.cs4218.EnvironmentHelper;
import sg.edu.nus.comp.cs4218.Shell;
import sg.edu.nus.comp.cs4218.impl.FileIOHelper;
import sg.edu.nus.comp.cs4218.impl.ShellImpl;

import java.io.*;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_INVALID_ARGS;

public class ShellImplIT {
    private static Shell shell;
    private static InputStream inputStream;
    private static ByteArrayOutputStream outputStream;
    private static final String MAIN_3_DIR = "main3";
    private static final String SUB_3_SUB_DIR = MAIN_3_DIR + File.separator + "sub3";

    @BeforeAll
    static void setUp() throws IOException {
        inputStream = Mockito.mock(InputStream.class);

        File main3Dir = new File(MAIN_3_DIR);
        main3Dir.mkdir();

        File sub3Dir = new File(SUB_3_SUB_DIR);
        sub3Dir.mkdir();
    }

    @AfterAll
    static void tearDown() throws IOException {
        inputStream.close();
        outputStream.close();
        FileIOHelper.deleteTestFiles(MAIN_3_DIR, SUB_3_SUB_DIR);
    }

    @AfterEach
    void tearDownAfterEach() throws IOException {
        inputStream.close();
        outputStream.close();
    }

    @BeforeEach
    void setUpBeforeEach() {
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
}