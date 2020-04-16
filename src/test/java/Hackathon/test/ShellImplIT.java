package Hackathon.test;

import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import sg.edu.nus.comp.cs4218.EnvironmentHelper;
import sg.edu.nus.comp.cs4218.Shell;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.DiffException;
import sg.edu.nus.comp.cs4218.exception.SedException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.FileIOHelper;
import sg.edu.nus.comp.cs4218.impl.ShellImpl;
import sg.edu.nus.comp.cs4218.impl.util.ArgumentResolver;
import sg.edu.nus.comp.cs4218.impl.util.StringUtils;
import tdd.util.TestUtil;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_SPACE;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;

public class ShellImplIT {
    private static Shell shell;
    private static InputStream inputStream;
    private static ArgumentResolver argumentResolver;
    private static ByteArrayOutputStream outputStream;
    private static final Path DIRECTORY = Paths.get("src", "test", "java", "Hackathon", "resource", "main1", "sub1");
    private static final String ABSOLUTE_PATH = DIRECTORY.toFile().getAbsolutePath();
    private static final String MAIN_1_DIR = "main1";
    private static final String MAIN_2_DIR = "main2";
    private static final String MAIN_3_DIR = "main3";
    private static final String BMP_1 = "a.bmp";
    private static final String BMP_2 = "b.bmp";
    private static final String FILE_1 = "file1.txt";
    private static final String Z = "ZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZ";
    private static final String FILE_1_CONTENTS =  "abc" + STRING_NEWLINE + "def" + STRING_NEWLINE + "geh"
            + STRING_NEWLINE + "ijk" + STRING_NEWLINE  + "lmn" + STRING_NEWLINE  + Z + STRING_NEWLINE;
    private static final String FILE_1_PARTIAL = "abc" + STRING_NEWLINE + "def" + STRING_NEWLINE  + "geh"
            + STRING_NEWLINE  + "ijk" + STRING_NEWLINE + "lmn" + STRING_NEWLINE ;
    private static final String PARENT_FOLDER = "sub1\\";
    private static final String BMP_1_FILE = ABSOLUTE_PATH + "/a.bmp";
    private static final String BMP_2_FILE = ABSOLUTE_PATH + "/b.bmp";
    private static final String FILE_1_PATH = ABSOLUTE_PATH + "/file1.txt";
    private static final String FILE_3_PATH = ABSOLUTE_PATH + "/file3.txt";
    private static final String FILE_4_PATH = ABSOLUTE_PATH + "/file4.txt";
    private static final String SUB_3_SUB_DIR = MAIN_3_DIR + File.separator + "sub3";
    private static final String RESOURCE_DIR = "src" + File.separator + "test" + File.separator + "java" + File.separator + "Hackathon" + File.separator + "resource";
    private static final String KEEP_FILE = ".keep";

    @BeforeAll
    static void setUp() throws IOException {
        EnvironmentHelper.currentDirectory = System.getProperty("user.dir");
        inputStream = Mockito.mock(InputStream.class);

        File main3Dir = new File(MAIN_3_DIR);
        main3Dir.mkdir();

        File sub3Dir = new File(SUB_3_SUB_DIR);
        sub3Dir.mkdir();

        // The limitation in git of not allowing empty directories to be pushed to git repository
        // and the work around is to include a .keep file within the empty directory which makes it not technically empty but will be able to push to GitHub
        // The resolution is to check if the .keep file exists and if so remove them before running the test suite to ensure the testing resource structure is maintained
        Path resource = Paths.get(EnvironmentHelper.currentDirectory + File.separator + RESOURCE_DIR);
        Path[] keepPaths = {resource.resolve(MAIN_1_DIR + File.separator + "sub2" + File.separator + KEEP_FILE),
                resource.resolve(MAIN_2_DIR + File.separator + KEEP_FILE),
                resource.resolve(MAIN_3_DIR + File.separator + KEEP_FILE),
                resource.resolve(MAIN_3_DIR + File.separator + "sub3" + File.separator + KEEP_FILE)};
        for (Path path : keepPaths) {
            if (Files.exists(path)) {
                Files.delete(path);
            }
        }
    }

    @AfterAll
    static void tearDown() throws IOException {
        EnvironmentHelper.currentDirectory = System.getProperty("user.dir");
        inputStream.close();
        outputStream.close();
        FileIOHelper.deleteTestFiles(MAIN_3_DIR, SUB_3_SUB_DIR);

        // The limitation in git of not allowing empty directories to be pushed to git repository
        // and the work around is to include a .keep file within the empty directory which makes it not technically empty but will be able to push to GitHub
        // To avoid issues with deleting the .keep file and pushing the deletion to Git repository after this test suite is completed, this will revert the deletion of .keep file after the test suite completed running.
        Path resource = Paths.get(EnvironmentHelper.currentDirectory + File.separator + RESOURCE_DIR);
        Path[] keepPaths = {resource.resolve(MAIN_1_DIR + File.separator + "sub2" + File.separator + KEEP_FILE),
                resource.resolve(MAIN_2_DIR + File.separator + KEEP_FILE),
                resource.resolve(MAIN_3_DIR + File.separator + KEEP_FILE),
                resource.resolve(MAIN_3_DIR + File.separator + "sub3" + File.separator + KEEP_FILE)};
        for (Path path : keepPaths) {
            if (!Files.exists(path)) {
                Files.createFile(path);
            }
        }
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
        argumentResolver = new ArgumentResolver();
    }

    /**
     * Tests Bug Report 1 for the multi-dash feature.
     * Command: paste - - < file1.txt (Note: the full path of the file should be provided instead)
     * Bug is not fixed, and thus, is expected to fail.
     */
    @Test
    void testParseAndEvaluateForBugReportNum1() throws AbstractApplicationException, ShellException {
        String input = "paste - - < " + FILE_1_PATH;
        String expected = FILE_1_CONTENTS;
        shell.parseAndEvaluate(input, outputStream);
        assertEquals(expected, outputStream.toString());
    }

    /**
     * Tests Bug Report 2 where LS prints new line in empty file
     * Command:     ls main2
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
        assertEquals(expectedResult, outputStream.toString());
    }

    @Test
    void testParseAndEvaluateForBugReportNum3() throws Exception {
        String input = "cd main3/`ls main3`";
        String expectedPath = Paths.get(EnvironmentHelper.currentDirectory + "/main3/sub3").toString();
        shell.parseAndEvaluate(input, outputStream);
        assertEquals(expectedPath, EnvironmentHelper.currentDirectory);

        // This part was meant to test the example below which is mentioned in bug report 3
        // Example:
        // Before Command Sub: arg: abc`echo 1 2 3`xyz`echo 4 5 6`
        // After Command Sub: arg: abc`1 2 3`xyz`4 5 6` (contents in `` is after command sub)
        // Expected output: [abc1, 2, 3xyz4, 5, 6]
        String argsList = "abc`echo 1 2 3`xyz`echo 4 5 6`";
        List<String> expectedArgList = Arrays.asList("abc1", "2", "3xyz4", "5", "6");
        assertArrayEquals(expectedArgList.toArray(), argumentResolver.resolveOneArgument(argsList).toArray());
    }

    /**
     * ArgumentResolver does not workcorrectly with backticks within
     * double quotes, expecting an error with missing closing backtick.
     * Command:     echo “a`a”
     */
    @Test
    void testParseAndEvaluateForBugReportNum6() throws Exception {
        String input = "echo \"a`a\"";
        Throwable thrown = assertThrows(Exception.class, () -> shell.parseAndEvaluate(input, outputStream));
        String expectedResult = "echo: missing closing back quote" ;
        assertEquals(thrown.getMessage(), expectedResult);
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


        String inputPutBack = "cd sub1; mv file3.txt " + "\"" + path + File.separator + "main1" + File.separator + "file1.txt\"";
        shell.parseAndEvaluate(inputPutBack, outputStream);

    }

    @Test
    void testParseAndEvaluateForBugReportNum11() {
        String input = "cut -b 1,3-5 file1.txt";
        Throwable thrown = assertThrows(Exception.class, () -> shell.parseAndEvaluate(input, outputStream));
        String expectedResult = "cut: " + ERR_INVALID_ARGS;
        assertEquals(thrown.getMessage(), expectedResult);
    }

    /**
     * Test for bug report 13. This test case tests replacement index 2, 3, and 4.
     * Command: sed s/Z/A/2 file1.txt (Note that the full path of the file should be used instead.)
     */
    @Test
    void testParseAndEvaluateForBugReportNum13() throws IOException, AbstractApplicationException, ShellException {
        String input = "sed s/Z/A/2 " + FILE_1_PATH;
        String expected = FILE_1_PARTIAL + "ZAZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZ";
        shell.parseAndEvaluate(input, outputStream);
        assertEquals(expected + STRING_NEWLINE, outputStream.toString());
        outputStream.close();

        outputStream = new ByteArrayOutputStream();
        input = "sed s/Z/A/3 " + FILE_1_PATH;
        expected = FILE_1_PARTIAL + "ZZAZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZ";
        shell.parseAndEvaluate(input, outputStream);
        assertEquals(expected + STRING_NEWLINE, outputStream.toString());
        outputStream.close();

        outputStream = new ByteArrayOutputStream();
        input = "sed s/Z/A/4 " + FILE_1_PATH;
        expected = FILE_1_PARTIAL + "ZZZAZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZ";
        shell.parseAndEvaluate(input, outputStream);
        assertEquals(expected + STRING_NEWLINE, outputStream.toString());
    }

    /**
     * Tests Bug Report 14 where special character is used as the regular expression in sed.
     * Command: sed s/?/./ file3.txt (Note that the full path of the file should be used instead.)
     * Expected to fail as this bug is not fixed.
     * @throws Exception
     */
    @Test
    void testParseAndEvaluateForBugReportNum14() throws Exception {
        String input = "sed s/?/./ " + FILE_3_PATH;
        String expected = ".?" + STRING_NEWLINE + "..";
        shell.parseAndEvaluate(input, outputStream);
        assertEquals(expected + STRING_NEWLINE, outputStream.toString());
    }

    /**
     * Tests Bug Report Number 18 and 19 where binary files are different (DiffApplication)
     * Command: diff a.bmp b.bmp (note that the full path has to be provided for each filename)
     * There are two cases, one with both binary files identical, while the other, both are different.
     * Observations (Bug fixed): The word "Binary" now appears in the output.
     */
    @Test
    void testRunWithBinaryFilesForBugReportNum18And19() {
        try {
            String input = "diff " + BMP_1_FILE + CHAR_SPACE + BMP_2_FILE;
            String expected = "Binary" + " files " + PARENT_FOLDER +  BMP_1 + CHAR_SPACE + PARENT_FOLDER + BMP_2 + " differ";
            shell.parseAndEvaluate(input, outputStream);
            assertEquals(expected + STRING_NEWLINE, outputStream.toString());
            outputStream.close();
        } catch(Exception e) {
            fail("should not fail: " + e.getMessage());
        }

        try {
            outputStream = new ByteArrayOutputStream();
            String input = "diff " + BMP_1_FILE + CHAR_SPACE + BMP_1_FILE;
            shell.parseAndEvaluate(input, outputStream);
            assertEquals("", outputStream.toString());
        } catch(Exception e) {
            fail("should not fail: " + e.getMessage());
        }
    }

    /**
     * Tests Bug Report 27 where windows throws error “Illegal char <*> at index” Unix systems will work
     * Command:   ls -d * /
     */
    @Test
    void testParseAndEvaluateForBugReportNum27() throws Exception {

        String path = System.getProperty("user.dir")
                + StringUtils.fileSeparator() + "src"
                + StringUtils.fileSeparator() + "test"
                + StringUtils.fileSeparator() + "java"
                + StringUtils.fileSeparator() + "Hackathon"
                + StringUtils.fileSeparator() + "resource";
        if (Files.isDirectory(TestUtil.resolveFilePath(path))) {
            EnvironmentHelper.currentDirectory = TestUtil.resolveFilePath(path).toString();
        }

        String input = "ls -d */";
        shell.parseAndEvaluate(input, outputStream);
        String expectedResult = MAIN_1_DIR + File.separator + System.lineSeparator()
                + MAIN_2_DIR + File.separator + System.lineSeparator()
                + MAIN_3_DIR + File.separator + System.lineSeparator();
        assertEquals(expectedResult ,outputStream.toString());
    }

    /**
     * Tests Bug Report 22 by providing diff with 2 directories
     * Command: diff main1 main2
     * Expected to fail, since the bug is not fixed.
     */
    @Test
    void testParseAndEvaluateForBugReportNum22() throws Exception {
        String path = System.getProperty("user.dir")
                + StringUtils.fileSeparator() + "src"
                + StringUtils.fileSeparator() + "test"
                + StringUtils.fileSeparator() + "java"
                + StringUtils.fileSeparator() + "Hackathon"
                + StringUtils.fileSeparator() + "resource";
        if (Files.isDirectory(TestUtil.resolveFilePath(path))) {
            EnvironmentHelper.currentDirectory = TestUtil.resolveFilePath(path).toString();
        }
        String mainOnePath = path + File.separator + "main1";
        String mainTwoPath = path + File.separator + "main2";
        String input = "diff " + mainOnePath + CHAR_SPACE + mainTwoPath;
        Throwable thrown = assertThrows(DiffException.class, () -> shell.parseAndEvaluate(input, outputStream));
        String expected = "diff: " + ERR_IS_DIR;
        assertEquals(expected, thrown.getMessage());
    }

    /**
     * Tests Bug Report 32 where echo should expand to all files in subdirectories in the current directory
     * Command:
     */
    @Test
    void testParseAndEvaluateForBugReportNum32() throws Exception {

        String path = System.getProperty("user.dir")
                + StringUtils.fileSeparator() + "src"
                + StringUtils.fileSeparator() + "test"
                + StringUtils.fileSeparator() + "java"
                + StringUtils.fileSeparator() + "Hackathon"
                + StringUtils.fileSeparator() + "resource";
        if (Files.isDirectory(TestUtil.resolveFilePath(path))) {
            EnvironmentHelper.currentDirectory = TestUtil.resolveFilePath(path).toString();
        }

        String input = "echo */*";
        shell.parseAndEvaluate(input, outputStream);
        String expectedResult = "main1/a.bmp main1/file1.txt main1/file2.txt main1/sub1 main1/sub2 main3/sub3" ;
        assertEquals(expectedResult ,outputStream.toString() );
    }

    /**
     * Tests Bug Report 33 where echo should expand to all files in subdirectories in the current directory
     * Command: paste file3.txt - < file4.txt
     * Expected to fail as bug not fixed.
     */
    @Test
    void testParseAndEvaluateForBugReportNum33() throws FileNotFoundException, AbstractApplicationException, ShellException {
        String input = "paste " + FILE_3_PATH + " - < " + FILE_4_PATH;
        String tab = "\t";
        shell.parseAndEvaluate(input, outputStream);
        String expected = "??" + tab + "ab" + STRING_NEWLINE + ".?" + tab + "cd";
        assertEquals(expected, outputStream.toString());
    }

    /**
     * Tests Bug Report 34 where concatenation of flags are accepted regardless if there are duplicates or not.
     * This test case tests the flag with double i, double c, and ic.
     * Command: grep -ii -c a file1.txt
     */
    @Test
    void testParseAndEvaluateForBugReportNum34() throws IOException, AbstractApplicationException, ShellException {
        String input = "grep -ii -c a " + FILE_1_PATH;

        shell.parseAndEvaluate(input, outputStream);
        assertEquals(1 + System.lineSeparator(), outputStream.toString());
        outputStream.close();

        outputStream = new ByteArrayOutputStream();
        input = "grep -i -cc a " + FILE_1_PATH;
        shell.parseAndEvaluate(input, outputStream);
        assertEquals(1 + System.lineSeparator(), outputStream.toString());
        outputStream.close();

        outputStream = new ByteArrayOutputStream();
        input = "grep -ic a " + FILE_1_PATH;
        shell.parseAndEvaluate(input, outputStream);
        assertEquals(1 + System.lineSeparator(), outputStream.toString());
    }

}